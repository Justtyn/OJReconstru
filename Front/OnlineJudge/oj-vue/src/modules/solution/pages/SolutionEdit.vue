<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form ref="formRef" layout="vertical" :model="formState" :rules="rules" class="solution-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="题目" name="problemId">
              <a-select
                v-model:value="formState.problemId"
                show-search
                allow-clear
                :filter-option="false"
                :options="problemOptions"
                :loading="problemLoading"
                placeholder="搜索并选择题目"
                @search="searchProblems"
                @dropdownVisibleChange="handleProblemDropdown"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="发布学生" name="authorId">
              <a-select
                v-model:value="formState.authorId"
                show-search
                allow-clear
                :filter-option="false"
                :options="studentOptions"
                :loading="studentLoading"
                placeholder="搜索并选择学生"
                @search="searchStudents"
                @dropdownVisibleChange="handleStudentDropdown"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="题解标题" name="title">
              <a-input v-model:value="formState.title" placeholder="请输入标题" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="语言" name="language">
              <a-select v-model:value="formState.language" allow-clear placeholder="请选择语言">
                <a-select-option v-for="item in languageOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="内容" name="content">
          <a-textarea v-model:value="formState.content" :rows="6" placeholder="请输入题解内容" />
        </a-form-item>
        <a-form-item label="状态">
          <a-switch v-model:checked="formState.isActive" checked-children="启用" un-checked-children="禁用" />
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
import { computed, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { solutionService } from '@/services/modules/solution';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Problem, SolutionRequest, Student } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);

const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const languageOptions = [
  { value: 'c', label: 'C' },
  { value: 'cpp', label: 'C++' },
  { value: 'java', label: 'Java' },
  { value: 'python', label: 'Python' },
  { value: 'python3', label: 'Python 3' },
  { value: 'go', label: 'Go' },
  { value: 'javascript', label: 'JavaScript' },
  { value: 'typescript', label: 'TypeScript' },
  { value: 'rust', label: 'Rust' },
  { value: 'kotlin', label: 'Kotlin' },
  { value: 'php', label: 'PHP' },
  { value: 'csharp', label: 'C#' },
];

const formState = reactive<SolutionRequest>({
  problemId: undefined,
  userId: undefined,
  authorId: undefined,
  title: '',
  content: '',
  language: '',
  isActive: true,
});

const problemOptions = ref<{ label: string; value: string }[]>([]);
const studentOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const studentLoading = ref(false);

const rules: FormProps['rules'] = {
  problemId: [{ required: true, message: '请选择题目' }],
  authorId: [{ required: true, message: '请选择发布学生' }],
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑题解' : '新建题解'));

const loadDetail = async () => {
  if (!isEdit.value || !recordId.value) return;
  try {
    const data = await solutionService.fetchDetail(recordId.value);
    formState.problemId = data.problemId;
    formState.userId = data.userId;
    formState.authorId = data.userId;
    formState.title = data.title;
    formState.content = data.content ?? '';
    formState.language = data.language ?? '';
    formState.isActive = data.isActive ?? true;

    if (formState.problemId) {
      try {
        const p = await problemService.fetchDetail(formState.problemId.toString());
        problemOptions.value = [
          ...problemOptions.value,
          { label: `${p.name}（ID: ${p.id}）`, value: p.id },
        ];
      } catch {
        problemOptions.value = [{ label: `题目（ID: ${formState.problemId}）`, value: formState.problemId.toString() }];
      }
    }

    if (formState.authorId) {
      try {
        const s = await studentService.fetchDetail(formState.authorId.toString());
        studentOptions.value = [
          ...studentOptions.value,
          { label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`, value: s.id },
        ];
      } catch {
        studentOptions.value = [{ label: `学生（ID: ${formState.authorId}）`, value: formState.authorId.toString() }];
      }
    }
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取题解详情失败'));
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: SolutionRequest = {
      problemId: formState.problemId,
      authorId: formState.authorId,
      title: formState.title,
      content: formState.content,
      language: formState.language,
      isActive: formState.isActive,
    };

    if (!payload.problemId) {
      message.error('请选择题目');
      submitting.value = false;
      return;
    }

    if (isEdit.value) {
      await solutionService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      await solutionService.create(payload.problemId, payload);
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
  router.push('/admin/solutions');
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword });
    problemOptions.value = data.records.map((p: Problem) => ({ label: `${p.name}（ID: ${p.id}）`, value: p.id }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索题目失败'));
  } finally {
    problemLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const searchStudents = async (keyword: string) => {
  studentLoading.value = true;
  try {
    const data = await studentService.fetchList({ page: 1, size: 50, username: keyword, email: keyword });
    studentOptions.value = data.records.map((s: Student) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索学生失败'));
  } finally {
    studentLoading.value = false;
  }
};

const handleStudentDropdown = (open: boolean) => {
  if (open) searchStudents('');
};

loadDetail();
</script>

<style scoped lang="less">
.solution-edit-form {
  max-width: 960px;
}
</style>
