<template>
  <PageContainer title="题解管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="标题">
          <a-input v-model:value="query.title" allow-clear placeholder="标题模糊查询" @pressEnter="handleSearch" />
        </a-form-item>
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
        <a-form-item label="作者">
          <a-select
            v-model:value="query.authorId"
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
        <a-form-item label="状态">
          <a-select v-model:value="query.isActive" allow-clear placeholder="全部" style="width: 140px">
            <a-select-option :value="true">启用</a-select-option>
            <a-select-option :value="false">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建题解</a-button>
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
          <template v-if="column.key === 'language'">
            <a-tag color="geekblue">{{ languageLabel(record.language) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'isActive'">
            <a-badge :status="record.isActive ? 'success' : 'error'" :text="record.isActive ? '启用' : '禁用'" />
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'userId'">
            {{ studentCache[record.userId]?.username || record.userId }}
          </template>
          <template v-else-if="column.key === 'problemId'">
            {{ problemCache[record.problemId]?.name || record.problemId }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button danger type="link" size="small" @click="confirmRemove(record)">删除</a-button>
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
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { solutionService } from '@/services/modules/solution';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Solution, SolutionQuery, Student, Problem } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const languageLabel = (lang?: string) => {
  const map: Record<string, string> = {
    c: 'C',
    cpp: 'C++',
    java: 'Java',
    python: 'Python',
    python3: 'Python 3',
    go: 'Go',
    javascript: 'JavaScript',
    typescript: 'TypeScript',
    rust: 'Rust',
    kotlin: 'Kotlin',
    php: 'PHP',
    csharp: 'C#',
  };
  return map[lang || ''] || lang || '-';
};

const query = reactive<SolutionQuery>({
  page: 1,
  size: 10,
  title: '',
  problemId: undefined,
  authorId: undefined,
  isActive: undefined,
});
const list = ref<Solution[]>([]);
const total = ref(0);
const loading = ref(false);

const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemCache = reactive<Record<string, Problem>>({});
const problemLoading = ref(false);

const studentOptions = ref<{ label: string; value: string }[]>([]);
const studentCache = reactive<Record<string, Student>>({});
const studentLoading = ref(false);

const columns: TableColumnType<Solution>[] = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 200 },
  { title: '作者', dataIndex: 'userId', key: 'userId', width: 200 },
  { title: '语言', dataIndex: 'language', key: 'language', width: 120 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 180 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const params: SolutionQuery = { ...query };
    if (params.isActive === undefined) delete params.isActive;
    const data = await solutionService.fetchList(params);
    list.value = data.records;
    total.value = data.total;
    preloadCaches(data.records);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取题解列表失败'));
  } finally {
    loading.value = false;
  }
};

const preloadCaches = (records: Solution[]) => {
  const problemIds = Array.from(
    new Set(records.map((r) => r.problemId).filter((id) => id && !problemCache[id])),
  );
  const studentIds = Array.from(new Set(records.map((r) => r.userId).filter((id) => id && !studentCache[id])));

  if (problemIds.length) {
    Promise.all(
      problemIds.map(async (id) => {
        try {
          const detail = await problemService.fetchDetail(id);
          problemCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }

  if (studentIds.length) {
    Promise.all(
      studentIds.map(async (id) => {
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
  query.title = '';
  query.problemId = undefined;
  query.authorId = undefined;
  query.isActive = undefined;
  query.problemId = undefined;
  query.userId = undefined;
  query.isActive = undefined;
  handleSearch();
};

const goCreate = () => {
  router.push('/admin/solutions/create');
};

const edit = (record: Solution) => {
  router.push(`/admin/solutions/${record.id}/edit`);
};

const remove = async (record: Solution) => {
  try {
    await solutionService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Solution) => {
  Modal.confirm({
    title: '删除题解',
    content: `确认删除题解「${record.title}」？不可恢复`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword });
    const options = data.records.map((p) => ({ label: `${p.name}（ID: ${p.id}）`, value: p.id }));
    problemOptions.value = options;
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
    const options = data.records.map((s) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
    studentOptions.value = options;
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
