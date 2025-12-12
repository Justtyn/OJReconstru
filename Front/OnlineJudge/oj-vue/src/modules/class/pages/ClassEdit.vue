<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form ref="formRef" layout="vertical" :model="formState" :rules="rules" class="class-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="班级名称" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入班级名称" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="邀请码">
              <a-input v-model:value="formState.code" placeholder="可选：用于学生加入" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="创建者（教师）" name="creatorId">
              <a-select
                v-model:value="formState.creatorId"
                show-search
                allow-clear
                :filter-option="false"
                :options="teacherOptions"
                :loading="teacherLoading"
                placeholder="检索教师姓名/账号并选择"
                @search="handleTeacherSearch"
                @dropdownVisibleChange="handleTeacherDropdown"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="起止日期">
              <a-range-picker
                v-model:value="dateRange"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="描述">
              <a-input v-model:value="formState.description" placeholder="简要描述班级" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="班级学生">
          <a-select
            v-model:value="selectedStudentIds"
            mode="multiple"
            show-search
            allow-clear
            :filter-option="false"
            :options="studentOptions"
            :loading="studentLoading"
            placeholder="输入用户名/邮箱检索并选择学生"
            @search="handleStudentSearch"
            @dropdownVisibleChange="handleStudentDropdown"
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
import { computed, reactive, ref, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import dayjs, { type Dayjs } from 'dayjs';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesMemberService, classesService } from '@/services/modules/classes';
import { studentService } from '@/services/modules/student';
import { teacherService } from '@/services/modules/teacher';
import type { ClassesMemberRequest, ClassesRequest, Student, Teacher } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);

const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const formState = reactive<ClassesRequest>({
  name: '',
  code: '',
  startDate: '',
  endDate: '',
  description: '',
  creatorId: '',
});

const dateRange = ref<[string | Dayjs, string | Dayjs] | []>([]);
const selectedStudentIds = ref<string[]>([]);
const originalMemberMap = reactive<Record<string, string>>({}); // studentId -> memberId
const studentOptions = ref<{ label: string; value: string }[]>([]);
const studentLoading = ref(false);
const teacherOptions = ref<{ label: string; value: string }[]>([]);
const teacherLoading = ref(false);

const rules: FormProps['rules'] = {
  name: [{ required: true, message: '请输入班级名称' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑班级' : '新建班级'));

const loadDetail = async () => {
  if (!isEdit.value || !recordId.value) return;
  try {
    const data = await classesService.fetchDetail(recordId.value);
    formState.name = data.name;
    formState.code = data.code ?? '';
    formState.description = data.description ?? '';
    formState.startDate = data.startDate ?? '';
    formState.endDate = data.endDate ?? '';
    formState.creatorId = data.creatorId ?? '';
    if (data.creatorId) {
      mergeTeacherOptions([
        {
          label: `${data.creatorName ?? '教师'}（ID: ${data.creatorId}）`,
          value: data.creatorId,
        },
      ]);
    }
    if (data.startDate || data.endDate) {
      dateRange.value = [data.startDate ? dayjs(data.startDate) : '', data.endDate ? dayjs(data.endDate) : ''] as [
        Dayjs | string,
        Dayjs | string,
      ];
    }
    await loadClassStudents(recordId.value);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级详情失败'));
  }
};

watchEffect(() => {
  if (dateRange.value && dateRange.value.length === 2) {
    formState.startDate = dateRange.value[0] ? dayjs(dateRange.value[0]).format('YYYY-MM-DD') : '';
    formState.endDate = dateRange.value[1] ? dayjs(dateRange.value[1]).format('YYYY-MM-DD') : '';
  }
});

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: ClassesRequest = { ...formState, creatorId: formState.creatorId || undefined };
    if (isEdit.value) {
      await classesService.update(recordId.value!, payload);
      await syncClassStudents(recordId.value!);
      message.success('更新成功');
    } else {
      const created = await classesService.create(payload);
      if (created?.id) {
        await syncClassStudents(created.id);
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
  router.push('/admin/classes');
};

loadDetail();

const loadClassStudents = async (classId: string) => {
  try {
    const res = await classesMemberService.fetchList({ classId, page: 1, size: 200 });
    selectedStudentIds.value = [];
    Object.keys(originalMemberMap).forEach((key) => delete originalMemberMap[key]);
    for (const m of res.records || []) {
      if (!m.studentId) continue;
      selectedStudentIds.value.push(m.studentId);
      if (m.id) {
        originalMemberMap[m.studentId] = m.id;
      }
    }
    if (selectedStudentIds.value.length) {
      await preloadStudents(selectedStudentIds.value);
    }
  } catch (error: any) {
    message.error(extractErrorMessage(error, '加载班级学生失败'));
  }
};

const handleStudentSearch = async (keyword: string) => {
  studentLoading.value = true;
  try {
    const data = await studentService.fetchList({ page: 1, size: 50, username: keyword, email: keyword });
    const options = data.records.map((s: Student) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
    mergeStudentOptions(options);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '检索学生失败'));
  } finally {
    studentLoading.value = false;
  }
};

const handleStudentDropdown = (open: boolean) => {
  if (open) {
    handleStudentSearch('');
  }
};

const preloadStudents = async (ids: string[]) => {
  try {
    const results = await Promise.all(ids.map((id) => studentService.fetchDetail(id)));
    const options = results.map((s) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
    mergeStudentOptions(options);
  } catch (error: any) {
    // 失败则忽略，已有 ID 仍然可提交
  }
};

const mergeStudentOptions = (options: { label: string; value: string }[]) => {
  const map = new Map<string, { label: string; value: string }>();
  studentOptions.value.forEach((o) => map.set(o.value, o));
  options.forEach((o) => map.set(o.value, o));
  selectedStudentIds.value.forEach((id) => {
    if (!map.has(id)) map.set(id, { label: `学生（ID: ${id}）`, value: id });
  });
  studentOptions.value = Array.from(map.values());
};

const syncClassStudents = async (classId: string) => {
  const current = new Set(selectedStudentIds.value);
  const original = new Set(Object.keys(originalMemberMap));
  const toRemove = Array.from(original).filter((id) => !current.has(id));
  const toAdd = Array.from(current).filter((id) => !original.has(id));

  if (toRemove.length) {
    await Promise.all(
      toRemove
        .map((id) => originalMemberMap[id])
        .filter(Boolean)
        .map((memberId) => classesMemberService.remove(memberId)),
    );
  }

  if (toAdd.length) {
    const payloads: ClassesMemberRequest[] = toAdd.map((id) => ({
      classId,
      memberType: 'student',
      studentId: id,
    }));
    await Promise.all(payloads.map((p) => classesMemberService.create(p)));
  }

  // 更新原始映射
  if (toAdd.length || toRemove.length) {
    await loadClassStudents(classId);
  }
};

const handleTeacherSearch = async (keyword: string) => {
  teacherLoading.value = true;
  try {
    const data = await teacherService.fetchList({ page: 1, size: 50, keyword });
    const options = data.records.map((t: Teacher) => ({
      label: `${t.name ?? t.username ?? '教师'}（ID: ${t.id}）`,
      value: t.id,
    }));
    mergeTeacherOptions(options);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '检索教师失败'));
  } finally {
    teacherLoading.value = false;
  }
};

const handleTeacherDropdown = (open: boolean) => {
  if (open) {
    handleTeacherSearch('');
  }
};

const mergeTeacherOptions = (options: { label: string; value: string }[]) => {
  const map = new Map<string, { label: string; value: string }>();
  teacherOptions.value.forEach((o) => map.set(o.value, o));
  options.forEach((o) => map.set(o.value, o));
  if (formState.creatorId && !map.has(String(formState.creatorId))) {
    map.set(String(formState.creatorId), {
      label: `教师（ID: ${formState.creatorId}）`,
      value: String(formState.creatorId),
    });
  }
  teacherOptions.value = Array.from(map.values());
};
</script>

<style scoped lang="less">
.class-edit-form {
  max-width: 960px;
}
</style>
