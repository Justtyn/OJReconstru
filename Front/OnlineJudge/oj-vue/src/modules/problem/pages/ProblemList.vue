<template>
  <PageContainer title="题目管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input v-model:value="query.keyword" allow-clear placeholder="名称/ID" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="难度">
          <a-select v-model:value="query.difficulty" allow-clear placeholder="全部" style="width: 140px">
            <a-select-option value="easy">简单</a-select-option>
            <a-select-option value="medium">中等</a-select-option>
            <a-select-option value="hard">困难</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="query.isActive" allow-clear placeholder="全部" style="width: 120px">
            <a-select-option :value="true">启用</a-select-option>
            <a-select-option :value="false">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建题目</a-button>
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
          <template v-if="column.key === 'difficulty'">
            <a-tag :color="difficultyColor(record.difficulty)">{{ difficultyLabel(record.difficulty) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'source'">
            <span>{{ text || '未设置' }}</span>
          </template>
          <template v-else-if="column.key === 'acCount'">
            {{ record.acCount ?? 0 }} / {{ record.submitCount ?? 0 }}
          </template>
          <template v-else-if="column.key === 'caseCount'">
            <a-space size="small">
              <a-spin v-if="getCaseCount(record) === undefined" size="small" />
              <span v-else>{{ getCaseCount(record) ?? 0 }}</span>
            </a-space>
          </template>
          <template v-else-if="column.key === 'isActive'">
            <a-badge :status="record.isActive ? 'success' : 'error'" :text="record.isActive ? '启用' : '禁用'" />
          </template>
          <template v-else-if="column.key === 'updatedAt'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
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
import type { TableColumnType } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { problemCaseService, problemService } from '@/services/modules/problem';
import type { Problem, ProblemDifficulty, ProblemQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const query = reactive<ProblemQuery>({ page: 1, size: 10, keyword: '', difficulty: undefined, isActive: undefined });
const list = ref<Problem[]>([]);
const total = ref(0);
const loading = ref(false);
const caseCountMap = reactive<Record<string, number | undefined>>({});

const columns: TableColumnType<Problem>[] = [
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '难度', dataIndex: 'difficulty', key: 'difficulty', width: 100 },
  { title: '来源', dataIndex: 'source', key: 'source', width: 140 },
  { title: 'AC/提交', dataIndex: 'acCount', key: 'acCount', width: 120 },
  { title: '用例/样例数', dataIndex: 'caseCount', key: 'caseCount', width: 120 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'actions', width: 160 },
];

const difficultyLabel = (val?: ProblemDifficulty) => {
  if (val === 'easy') return '简单';
  if (val === 'medium') return '中等';
  if (val === 'hard') return '困难';
  return val || '-';
};

const difficultyColor = (val?: ProblemDifficulty) => {
  if (val === 'easy') return 'green';
  if (val === 'medium') return 'orange';
  if (val === 'hard') return 'red';
  return 'blue';
};

const loadData = async () => {
  loading.value = true;
  try {
    const params = { ...query };
    if (params.isActive === undefined) delete params.isActive;
    if (params.difficulty === undefined) delete params.difficulty;
    const data = await problemService.fetchList(params);
    list.value = data.records;
    total.value = data.total;
    await loadCaseCounts(data.records);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取题目列表失败'));
  } finally {
    loading.value = false;
  }
};

const loadCaseCounts = async (problems: Problem[]) => {
  Object.keys(caseCountMap).forEach((key) => delete caseCountMap[key]);
  const pending = problems.filter((p) => p.caseCount === undefined);
  problems.forEach((p) => {
    if (p.caseCount !== undefined) {
      caseCountMap[p.id] = p.caseCount;
    }
  });
  if (!pending.length) return;
  await Promise.allSettled(
    pending.map(async (p) => {
      try {
        const cases = await problemCaseService.fetchList(p.id, { page: 1, size: 200 });
        caseCountMap[p.id] = Array.isArray(cases) ? cases.length : 0;
      } catch {
        caseCountMap[p.id] = 0;
      }
    }),
  );
};

const getCaseCount = (record: Problem) => record.caseCount ?? caseCountMap[record.id];

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  query.difficulty = undefined;
  query.isActive = undefined;
  handleSearch();
};

const goCreate = () => {
  router.push('/admin/problems/create');
};

const edit = (record: Problem) => {
  router.push(`/admin/problems/${record.id}/edit`);
};

const remove = async (record: Problem) => {
  try {
    await problemService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Problem) => {
  Modal.confirm({
    title: '删除题目',
    content: `确认删除题目「${record.name}」？不可恢复`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, pageSize?: number) => {
    query.page = page;
    query.size = pageSize ?? query.size;
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
