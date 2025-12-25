<template>
  <PageContainer title="提交记录" subtitle="查看你的判题状态与提交详情">
    <div class="submission-page">
      <section class="submission-hero">
        <div class="submission-hero__top">
          <div class="submission-hero__title">提交记录筛选</div>
          <div class="submission-hero__stats">
            <span class="stat-pill">
              记录总数
              <span class="stat-pill__value">{{ totalLabel }}</span>
            </span>
            <span class="stat-pill">
              本页通过率
              <span class="stat-pill__value">{{ pagePassRate }}</span>
            </span>
          </div>
        </div>
        <div class="submission-hero__filters">
          <a-select
            v-model:value="query.problemId"
            show-search
            allow-clear
            size="small"
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            placeholder="搜索题目"
            class="filter-select"
            @search="searchProblems"
            @change="handleSearch"
            @dropdownVisibleChange="handleProblemDropdown"
          />
          <a-select
            v-model:value="query.languageId"
            allow-clear
            size="small"
            placeholder="语言"
            class="filter-select"
            @change="handleSearch"
          >
            <a-select-option v-for="lang in languageOptions" :key="lang.id" :value="lang.id">
              {{ lang.name }}
            </a-select-option>
          </a-select>
          <a-button type="primary" size="small" @click="handleSearch">查询</a-button>
          <a-button size="small" @click="resetQuery">重置</a-button>
        </div>
      </section>

      <a-card class="submission-card">
        <a-table
          row-key="id"
          :loading="loading"
          :columns="columns"
          :data-source="list"
          :pagination="paginationConfig"
        >
          <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'problemId'">
            {{ problemCache[record.problemId]?.name || record.problemId }}
          </template>
          <template v-else-if="column.key === 'student'">
            <div class="student-cell">
              <a-avatar :src="studentAvatar(record)" :size="28">{{ studentInitial(record) }}</a-avatar>
              <span class="student-cell__name">{{ studentDisplayName(record) }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'languageId'">
            {{ languageLabel(record.languageId) }}
          </template>
            <template v-else-if="column.key === 'overallStatusName'">
              <a-badge
                :status="statusBadge(record.overallStatusCode)"
                :text="record.overallStatusName || record.overallStatusCode || '-'"
              />
            </template>
            <template v-else-if="column.key === 'caseCount'">
              {{ record.passedCaseCount ?? 0 }} / {{ record.totalCaseCount ?? 0 }}
            </template>
            <template v-else-if="column.key === 'createdAt'">
              {{ formatTime(record.createdAt) }}
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { TableColumnType } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { format } from 'date-fns';
import PageContainer from '@/components/common/PageContainer.vue';
import { useAuthStore } from '@/stores/auth';
import { submissionService } from '@/services/modules/submission';
import { problemService } from '@/services/modules/problem';
import type { Submission, SubmissionQuery, Problem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const authStore = useAuthStore();
const query = reactive<SubmissionQuery>({
  page: 1,
  size: 10,
  problemId: undefined,
  languageId: undefined,
  studentId: undefined,
});
const list = ref<Submission[]>([]);
const total = ref(0);
const loading = ref(false);
const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const problemCache = reactive<Record<string, Problem>>({});
const polling = ref(false);
let loadSeq = 0;
const POLL_INTERVAL_MS = 2500;
const POLL_MAX_ATTEMPTS = 80;
let pollTimer: ReturnType<typeof window.setInterval> | undefined;
let pollAttempts = 0;

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

const columns: TableColumnType<Submission>[] = [
  { title: '提交ID', dataIndex: 'id', key: 'id', width: 200 },
  { title: '学生', key: 'student', width: 180 },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 220 },
  { title: '语言', dataIndex: 'languageId', key: 'languageId', width: 180 },
  { title: '状态', dataIndex: 'overallStatusName', key: 'overallStatusName', width: 140 },
  { title: '通过/总数', dataIndex: 'caseCount', key: 'caseCount', width: 120 },
  { title: '得分', dataIndex: 'score', key: 'score', width: 80 },
  { title: '提交时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'actions', width: 120 },
];

const totalLabel = computed(() => (total.value ? total.value.toString() : '-'));
const pagePassRate = computed(() => {
  if (!list.value.length) return '-';
  const accepted = list.value.filter((item) => isAccepted(item)).length;
  return `${Math.round((accepted / list.value.length) * 100)}%`;
});

const loadData = async (options?: { silent?: boolean; muteError?: boolean }) => {
  const silent = options?.silent ?? false;
  const muteError = options?.muteError ?? false;
  if (!query.studentId) return;
  const seq = (loadSeq += 1);
  if (!silent) loading.value = true;
  try {
    const data = await submissionService.fetchList(query);
    if (seq !== loadSeq) return;
    list.value = data.records || [];
    total.value = data.total || 0;
    preloadProblems(list.value);
    startPollingIfNeeded();
  } catch (error) {
    if (!muteError && seq === loadSeq) {
      message.error(extractErrorMessage(error, '获取提交记录失败'));
    }
  } finally {
    if (!silent && seq === loadSeq) loading.value = false;
  }
};

const isPendingSubmission = (record: Submission) => {
  if (record.overallStatusId === 1 || record.overallStatusId === 2) return true;
  const code = (record.overallStatusCode || '').toUpperCase();
  return code === 'IN_QUEUE' || code === 'PROCESSING';
};

const stopPolling = () => {
  if (pollTimer) {
    window.clearInterval(pollTimer);
    pollTimer = undefined;
  }
  polling.value = false;
  pollAttempts = 0;
};

const startPollingIfNeeded = () => {
  if (polling.value) return;
  if (!list.value.some(isPendingSubmission)) return;
  polling.value = true;
  pollAttempts = 0;
  pollTimer = window.setInterval(async () => {
    if (!query.studentId) {
      stopPolling();
      return;
    }
    pollAttempts += 1;
    await loadData({ silent: true, muteError: true });
    if (!list.value.some(isPendingSubmission)) {
      stopPolling();
      return;
    }
    if (pollAttempts >= POLL_MAX_ATTEMPTS) {
      stopPolling();
      message.warning('判题耗时较长，已停止自动刷新，请手动刷新');
    }
  }, POLL_INTERVAL_MS);
};

const preloadProblems = (records: Submission[]) => {
  const ids = Array.from(new Set(records.map((item) => item.problemId).filter((id) => id && !problemCache[id])));
  if (!ids.length) return;
  Promise.all(
    ids.map(async (id) => {
      try {
        const detail = await problemService.fetchDetail(id);
        problemCache[id] = detail;
      } catch {
        /* ignore */
      }
    }),
  );
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword, isActive: true });
    problemOptions.value = data.records.map((p: Problem) => ({ label: `${p.name}（ID: ${p.id}）`, value: p.id }));
  } catch (error) {
    message.error(extractErrorMessage(error, '获取题目列表失败'));
  } finally {
    problemLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const handleSearch = () => {
  stopPolling();
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.problemId = undefined;
  query.languageId = undefined;
  handleSearch();
};

const viewDetail = (record: Submission) => {
  router.push(`/submissions/${record.id}`);
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

const languageLabel = (id?: number | string) =>
  languageOptions.find((item) => item.id === Number(id))?.name || (id ? String(id) : '-');

const studentDisplayName = (record: Submission) => {
  return authStore.user?.username || record.studentUsername || record.studentId || '-';
};

const studentAvatar = (record: Submission) => {
  return authStore.user?.avatar || (record as any).avatar || '';
};

const studentInitial = (record: Submission) => {
  const name = studentDisplayName(record);
  if (!name || name === '-') return 'S';
  return name.slice(0, 1).toUpperCase();
};

const statusBadge = (code?: string) => {
  const normalized = (code || '').toUpperCase();
  if (!normalized) return 'default';
  if (normalized === 'ACCEPTED') return 'success';
  if (normalized === 'IN_QUEUE' || normalized === 'PROCESSING') return 'processing';
  return 'error';
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

const isAccepted = (record: Submission) => {
  if (record.overallStatusCode?.toUpperCase() === 'ACCEPTED') return true;
  return record.overallStatusId === 3;
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, size?: number) => {
    query.page = page;
    query.size = size ?? query.size;
    stopPolling();
    loadData();
  },
}));

watch(
  () => authStore.user?.id,
  (id) => {
    if (id) {
      query.studentId = id;
      loadData();
    }
  },
  { immediate: true },
);

onMounted(() => {
  ensureProfile();
});

onBeforeUnmount(() => {
  stopPolling();
});
</script>

<style scoped lang="less">
.submission-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.submission-hero {
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.16), rgba(14, 116, 144, 0.16));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.submission-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.submission-hero__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.submission-hero__stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-pill {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(15, 23, 42, 0.06);
  font-size: 12px;
  color: var(--text-color);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.stat-pill__value {
  font-weight: 600;
  color: #0ea5e9;
}

.submission-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-select {
  width: 200px;
}

.submission-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.student-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.student-cell__name {
  font-weight: 600;
  color: var(--text-color);
}

@media (max-width: 768px) {
  .submission-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }
}

:global(:root[data-theme='dark']) .submission-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(8, 145, 178, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .stat-pill,
:global(:root[data-theme='dark']) .submission-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: rgba(15, 23, 42, 0.7);
}

:global(:root[data-theme='dark']) .stat-pill__value {
  color: #38bdf8;
}
</style>
