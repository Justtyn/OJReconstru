<template>
  <PageContainer title="提交记录">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="题目">
          <a-select
            v-model:value="query.problemId"
            show-search
            allow-clear
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            style="width: 240px"
            placeholder="搜索题目"
            @search="searchProblems"
            @dropdownVisibleChange="handleProblemDropdown"
          />
        </a-form-item>
        <a-form-item label="学生">
          <a-select
            v-model:value="query.userId"
            show-search
            allow-clear
            :filter-option="false"
            :options="studentOptions"
            :loading="studentLoading"
            style="width: 240px"
            placeholder="搜索学生"
            @search="searchStudents"
            @dropdownVisibleChange="handleStudentDropdown"
          />
        </a-form-item>
        <a-form-item label="语言">
          <a-select v-model:value="query.languageId" allow-clear placeholder="全部" style="width: 220px">
            <a-select-option v-for="lang in languageOptions" :key="lang.id" :value="lang.id">
              {{ lang.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="query.overallStatusId" allow-clear placeholder="全部" style="width: 160px">
            <a-select-option :value="3">通过</a-select-option>
            <a-select-option :value="5">错误</a-select-option>
            <a-select-option :value="4">编译错误</a-select-option>
            <a-select-option :value="2">运行中</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="mt-16">
      <a-table
        row-key="id"
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="paginationConfig"
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'problemId'">
            {{ problemCache[record.problemId]?.name || record.problemId }}
          </template>
          <template v-else-if="column.key === 'studentId'">
            {{ studentLabel(record) }}
          </template>
          <template v-else-if="column.key === 'overallStatusName'">
            <a-badge :status="statusBadge(record.overallStatusCode)" :text="record.overallStatusName || record.overallStatusCode" />
          </template>
          <template v-else-if="column.key === 'caseCount'">
            {{ record.passedCaseCount ?? 0 }} / {{ record.totalCaseCount ?? 0 }}
          </template>
          <template v-else-if="column.key === 'languageId'">
            {{ languageLabel(record.languageId) }}
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import type { TableColumnType } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { submissionService } from '@/services/modules/submission';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Submission, SubmissionQuery, Problem, Student } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

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
  { id: 5, name: 'Java Test (OpenJDK 14.0.1, JUnit 1.6.2)' },
  { id: 6, name: 'MPI C (GCC 8.4.0)' },
  { id: 7, name: 'MPI C++ (GCC 8.4.0)' },
  { id: 8, name: 'MPI Python (3.7.7)' },
  { id: 89, name: 'Multi-file program' },
  { id: 9, name: 'Nim (stable)' },
  { id: 10, name: 'Python for ML (3.7.7)' },
  { id: 20, name: 'Visual Basic.Net (vbnc 0.0.0.5943)' },
];

const query = reactive<SubmissionQuery>({ page: 1, size: 10 });
const list = ref<Submission[]>([]);
const total = ref(0);
const loading = ref(false);

const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const problemCache = reactive<Record<string, Problem>>({});

const studentOptions = ref<{ label: string; value: string }[]>([]);
const studentLoading = ref(false);
const studentCache = reactive<Record<string, Student>>({});

const columns: TableColumnType<Submission>[] = [
  { title: '提交ID', dataIndex: 'id', key: 'id', width: 200 },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 220 },
  { title: '学生', dataIndex: 'studentId', key: 'studentId', width: 200 },
  { title: '语言', dataIndex: 'languageId', key: 'languageId', width: 160 },
  { title: '状态', dataIndex: 'overallStatusName', key: 'overallStatusName', width: 140 },
  { title: '通过/总数', dataIndex: 'caseCount', key: 'caseCount', width: 120 },
  { title: '得分', dataIndex: 'score', key: 'score', width: 80 },
  { title: '时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'actions', width: 120 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await submissionService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
    preloadCaches(data.records);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取提交记录失败'));
  } finally {
    loading.value = false;
  }
};

const preloadCaches = (records: Submission[]) => {
  const pIds = Array.from(new Set(records.map((r) => r.problemId).filter((id) => id && !problemCache[id])));
  const uIds = Array.from(
    new Set(records.map((r) => r.studentId || (r as any).userId).filter((id) => id && !studentCache[id])),
  );
  if (pIds.length) {
    Promise.all(
      pIds.map(async (id) => {
        try {
          const detail = await problemService.fetchDetail(id);
          problemCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
  if (uIds.length) {
    Promise.all(
      uIds.map(async (id) => {
        try {
          const detail = await studentService.fetchDetail(id);
          studentCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.problemId = undefined;
  query.userId = undefined;
  query.languageId = undefined;
  query.overallStatusId = undefined;
  handleSearch();
};

const viewDetail = (record: Submission) => {
  router.push(`/admin/submissions/${record.id}`);
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

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const handleStudentDropdown = (open: boolean) => {
  if (open) searchStudents('');
};

const languageLabel = (id?: number) => languageOptions.find((l) => l.id === id)?.name || id || '-';

const studentLabel = (record: Submission) => {
  const id = record.studentId || (record as any).userId;
  if (!id) return '-';
  const cached = studentCache[id];
  if (cached) return cached.name ? `${cached.username}（${cached.name}）` : cached.username;
  return record.studentUsername || (record as any).username || id;
};

const statusBadge = (code?: string) => {
  if (code === 'ACCEPTED') return 'success';
  if (code === 'WRONG') return 'error';
  return 'processing';
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, size?: number) => {
    query.page = page;
    query.size = size ?? query.size;
    loadData();
  },
}));

loadData();
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}
</style>
