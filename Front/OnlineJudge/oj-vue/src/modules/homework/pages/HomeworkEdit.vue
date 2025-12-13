<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form ref="formRef" layout="vertical" :model="formState" :rules="rules" class="homework-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="所属班级">
              <a-input :value="classInfo?.name || `班级 #${classId}`" disabled />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="作业标题" name="title">
              <a-input v-model:value="formState.title" placeholder="请输入作业标题" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="时间范围">
              <a-range-picker
                v-model:value="dateRange"
                style="width: 100%"
                show-time
                format="YYYY-MM-DD HH:mm"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="状态">
              <a-switch v-model:checked="formState.isActive" checked-children="启用" un-checked-children="停用" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="描述">
          <a-textarea v-model:value="formState.description" :rows="3" placeholder="作业说明、评分标准等" />
        </a-form-item>
        <a-form-item label="选择题目">
          <a-select
            v-model:value="selectedProblemIds"
            mode="multiple"
            show-search
            allow-clear
            :filter-option="false"
            :options="problemOptions"
            :loading="problemOptionsLoading"
            placeholder="输入题目名称或ID检索，支持多选"
            @search="handleProblemSearch"
            @dropdownVisibleChange="handleProblemDropdown"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">保存</a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import dayjs, { type Dayjs } from 'dayjs';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService } from '@/services/modules/classes';
import { homeworkService } from '@/services/modules/homework';
import { problemService } from '@/services/modules/problem';
import type { Classes, HomeworkProblem, HomeworkRequest, Problem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const classId = computed(() => (route.params.classId as string) || '');
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const formRef = ref<FormInstance>();
const submitting = ref(false);
const classInfo = ref<Classes>();
const dateRange = ref<[Dayjs, Dayjs] | []>([]);
const selectedProblemIds = ref<string[]>([]);
const originalProblemIds = ref<string[]>([]);
const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemOptionsLoading = ref(false);

const formState = reactive<HomeworkRequest>({
  title: '',
  classId: classId.value,
  description: '',
  startTime: '',
  endTime: '',
  isActive: true,
});

const rules: FormProps['rules'] = {
  title: [{ required: true, message: '请输入作业标题' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑作业' : '新建作业'));

const loadClass = async () => {
  try {
    const data = await classesService.fetchDetail(classId.value);
    classInfo.value = data;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级信息失败'));
  }
};

const loadDetail = async () => {
  if (!isEdit.value || !recordId.value) return;
  try {
    const data = await homeworkService.fetchDetail(recordId.value);
    formState.title = data.title;
    formState.classId = data.classId;
    formState.description = data.description ?? '';
    formState.isActive = data.isActive ?? true;
    formState.startTime = data.startTime ?? '';
    formState.endTime = data.endTime ?? '';
    await loadHomeworkProblems(recordId.value);
    if (data.startTime || data.endTime) {
      const range: Dayjs[] = [];
      if (data.startTime) range.push(dayjs(data.startTime));
      if (data.endTime) range.push(dayjs(data.endTime));
      if (range.length === 2) {
        const [start, end] = range;
        if (start && end) {
          dateRange.value = [start, end];
        }
      }
    }
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取作业详情失败'));
  }
};

watch(dateRange, (range) => {
  if (range && range.length === 2) {
    formState.startTime = dayjs(range[0]).toISOString();
    formState.endTime = dayjs(range[1]).toISOString();
  } else {
    formState.startTime = '';
    formState.endTime = '';
  }
});

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;

    const payload: HomeworkRequest = {
      ...formState,
      classId: classId.value,
    };

    if (isEdit.value) {
      await homeworkService.update(recordId.value!, payload);
      await syncHomeworkProblems(recordId.value!);
      message.success('更新成功');
    } else {
      const created = await homeworkService.create(payload);
      if (created?.id) {
        await syncHomeworkProblems(created.id);
      }
      message.success('创建成功');
    }
    goBack();
  } catch (error: any) {
    message.error(extractErrorMessage(error, isEdit.value ? '更新失败' : '创建失败'));
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.push(`/admin/classes/${classId.value}/homeworks`);
};

onMounted(() => {
  loadClass();
  loadDetail();
});

const handleProblemSearch = async (keyword: string) => {
  problemOptionsLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword });
    const options = data.records.map((item: Problem) => ({
      label: `${item.name}（ID: ${item.id}）`,
      value: item.id,
    }));
    mergeOptions(options);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '检索题目失败'));
  } finally {
    problemOptionsLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) {
    handleProblemSearch('');
  }
};

const loadHomeworkProblems = async (id: string) => {
  try {
    const problems: HomeworkProblem[] = await homeworkService.fetchProblems(id);
    selectedProblemIds.value = problems.map((p) => p.id.toString());
    originalProblemIds.value = [...selectedProblemIds.value];
    const options = problems.map((p) => ({ label: `${p.name ?? '题目'}（ID: ${p.id}）`, value: p.id }));
    mergeOptions(options);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '加载作业题目失败'));
  }
};

const mergeOptions = (options: { label: string; value: string }[]) => {
  const map = new Map<string, { label: string; value: string }>();
  problemOptions.value.forEach((o) => map.set(o.value, o));
  options.forEach((o) => map.set(o.value, o));
  selectedProblemIds.value.forEach((id) => {
    if (!map.has(id)) {
      map.set(id, { label: `题目（ID: ${id}）`, value: id });
    }
  });
  problemOptions.value = Array.from(map.values());
};

const syncHomeworkProblems = async (homeworkId: string) => {
  const currentIds = selectedProblemIds.value.map((id) => id.toString());
  const originalIdsSet = new Set(originalProblemIds.value);
  const currentIdsSet = new Set(currentIds);

  const toRemove = originalProblemIds.value.filter((id) => !currentIdsSet.has(id));
  const toAdd = currentIds.filter((id) => !originalIdsSet.has(id));

  if (toRemove.length) {
    await Promise.all(
      toRemove.map((pid) => homeworkService.removeProblem(homeworkId, pid)),
    );
  }

  if (toAdd.length) {
    await homeworkService.addProblems(homeworkId, { problemIds: toAdd });
  }

  originalProblemIds.value = [...currentIds];
};
</script>

<style scoped lang="less">
.homework-edit-form {
  max-width: 960px;
}
</style>
