<template>
  <PageContainer title="提交代码" :subtitle="problem?.name || '请选择题目'">
    <div class="submit-page">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-card class="submit-card" :loading="loading">
        <div class="submit-summary">
          <div class="submit-summary__info">
            <div class="submit-summary__title">{{ problem?.name || '-' }}</div>
        <div class="submit-summary__meta">
          <span>ID：{{ problem?.id || '-' }}</span>
          <span v-if="homeworkId">作业：{{ homeworkId }}</span>
          <span>时间限制：{{ formatLimit(problem?.timeLimitMs, 'ms') }}</span>
          <span>内存限制：{{ formatLimit(problem?.memoryLimitKb, 'KB') }}</span>
        </div>
          </div>
          <div class="submit-summary__actions">
            <a-button @click="goDetail">返回题目</a-button>
          </div>
        </div>
        <a-alert type="info" show-icon message="提交后系统会自动判题，通过题目可获得积分奖励。" />
      </a-card>

      <a-card class="submit-card" title="提交信息">
        <a-form layout="vertical">
          <a-row :gutter="16">
            <a-col :xs="24" :md="12">
              <a-form-item label="提交用户">
                <a-input :value="userLabel" disabled />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-form-item label="编程语言" required>
                <a-select v-model:value="formState.languageId" placeholder="选择语言">
                  <a-select-option v-for="lang in languageOptions" :key="lang.id" :value="lang.id">
                    {{ lang.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="代码" required>
            <CodeEditor
              v-model="formState.sourceCode"
              :language="languageLabel(formState.languageId)"
              placeholder="在此输入或粘贴代码"
              :max-height="480"
              :resizable="false"
            />
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" :loading="submitting" @click="handleSubmit">提交代码</a-button>
              <a-button @click="goDetail">取消</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import CodeEditor from '@/components/common/CodeEditor.vue';
import { problemService } from '@/services/modules/problem';
import { submissionService } from '@/services/modules/submission';
import { useAuthStore } from '@/stores/auth';
import type { Problem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const problem = ref<Problem | null>(null);
const loading = ref(false);
const loadError = ref('');
const submitting = ref(false);

const problemId = computed(() => route.params.id as string);
const homeworkId = computed(() => (route.query.homeworkId as string) || '');

const formState = reactive({
  languageId: undefined as number | undefined,
  sourceCode: '',
});

const languageOptions = [
  { id: 11, name: 'Bosque (latest)' },
  { id: 3, name: 'C3 (latest)' },
  { id: 1, name: 'C (Clang 10.0.1)' },
  { id: 2, name: 'C++ (Clang 10.0.1)' },
  { id: 13, name: 'C (Clang 9.0.1)' },
  { id: 14, name: 'C++ (Clang 9.0.1)' },
  { id: 22, name: 'C# (Mono 6.12.0.122)' },
  { id: 21, name: 'C# (.NET Core SDK 3.1.406)' },
  { id: 15, name: 'C++ Test (Clang 10.0.1, Google Test 1.8.1)' },
  { id: 12, name: 'C++ Test (GCC 8.4.0, Google Test 1.8.1)' },
  { id: 23, name: 'C# Test (.NET Core SDK 3.1.406, NUnit 3.12.0)' },
  { id: 24, name: 'F# (.NET Core SDK 3.1.406)' },
  { id: 4, name: 'Java (OpenJDK 14.0.1)' },
  { id: 5, name: 'Java Test (OpenJDK 14.0.1, JUnit Platform Console Standalone 1.6.2)' },
  { id: 6, name: 'MPI (OpenRTE 3.1.3) with C (GCC 8.4.0)' },
  { id: 7, name: 'MPI (OpenRTE 3.1.3) with C++ (GCC 8.4.0)' },
  { id: 8, name: 'MPI (OpenRTE 3.1.3) with Python (3.7.7)' },
  { id: 89, name: 'Multi-file program' },
  { id: 9, name: 'Nim (stable)' },
  { id: 10, name: 'Python for ML (3.7.7)' },
  { id: 20, name: 'Visual Basic.Net (vbnc 0.0.0.5943)' },
];

const userLabel = computed(() => authStore.user?.username || authStore.user?.id || '-');

const loadProblem = async (id: string) => {
  loading.value = true;
  loadError.value = '';
  try {
    problem.value = await problemService.fetchDetail(id);
  } catch (error) {
    const msg = extractErrorMessage(error, '获取题目信息失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const ensureProfile = async () => {
  if (!authStore.user && authStore.token) {
    try {
      await authStore.fetchProfile();
    } catch (error) {
      console.warn('Failed to load auth profile', error);
    }
  }
};

const handleSubmit = async () => {
  if (!problem.value) {
    message.error('题目信息未加载完成');
    return;
  }
  if (!authStore.user?.id) {
    message.error('未获取到学生账号信息');
    return;
  }
  if (!formState.languageId) {
    message.error('请选择编程语言');
    return;
  }
  if (!formState.sourceCode.trim()) {
    message.error('请输入代码');
    return;
  }
  submitting.value = true;
  try {
    await submissionService.create({
      problemId: problem.value.id,
      homeworkId: homeworkId.value || undefined,
      studentId: authStore.user.id,
      languageId: formState.languageId,
      sourceCode: formState.sourceCode,
    });
    message.success('提交成功，系统正在判题');
    const query = homeworkId.value ? { homeworkId: homeworkId.value } : undefined;
    router.push({ path: `/problems/${problem.value.id}`, query });
  } catch (error) {
    message.error(extractErrorMessage(error, '提交失败，请稍后重试'));
  } finally {
    submitting.value = false;
  }
};

const goDetail = () => {
  if (!problemId.value) return;
  router.push(`/problems/${problemId.value}`);
};

const formatLimit = (value?: number, unit?: string) => (value ? `${value}${unit ?? ''}` : '-');

const languageLabel = (id?: number) => languageOptions.find((item) => item.id === id)?.name || 'code';

watch(
  () => problemId.value,
  (id) => {
    if (id) loadProblem(id);
  },
  { immediate: true },
);

onMounted(() => {
  ensureProfile();
  if (!formState.languageId) formState.languageId = languageOptions[0]?.id;
});
</script>

<style scoped lang="less">
.submit-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.submit-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.submit-summary {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.submit-summary__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.submit-summary__meta {
  margin-top: 6px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

:global(:root[data-theme='dark']) .submit-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
}
</style>
