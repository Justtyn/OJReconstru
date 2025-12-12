<template>
  <PageContainer :title="pageTitle">
    <template #extra>
      <a-space>
        <a-button @click="goBack">返回班级</a-button>
        <a-button type="primary" @click="goCreate">新建作业</a-button>
      </a-space>
    </template>

    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="标题/描述"
            @pressEnter="handleSearch"
          />
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
        :expanded-row-keys="expandedRowKeys"
        @expand="handleExpand"
      >
        <template #expandedRowRender="{ record }">
          <a-table
            :columns="problemColumns"
            :data-source="problemListMap[record.id]?.items || []"
            :loading="problemListMap[record.id]?.loading"
            row-key="id"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record: problem, text }">
              <template v-if="column.key === 'difficulty'">
                <a-tag :color="difficultyColor(problem.difficulty)">{{ difficultyLabel(problem.difficulty) }}</a-tag>
              </template>
              <template v-else-if="column.key === 'acCount'">
                {{ problem.acCount ?? 0 }} / {{ problem.submitCount ?? 0 }}
              </template>
              <template v-else-if="column.key === 'isActive'">
                <a-badge :status="problem.isActive ? 'success' : 'error'" :text="problem.isActive ? '启用' : '禁用'" />
              </template>
              <template v-else-if="column.key === 'updatedAt' || column.key === 'createTime'">
                {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
              </template>
              <template v-else-if="column.key === 'actions'">
                <a-button danger type="link" size="small" @click="confirmRemoveProblem(record.id, problem)">
                  从作业移除
                </a-button>
              </template>
            </template>
          </a-table>
        </template>
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'startTime' || column.key === 'endTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'progress'">
            <a-tag :color="progressColor(record.startTime, record.endTime)">
              {{ progressLabel(record.startTime, record.endTime) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isActive'">
            <a-tag :color="record.isActive ? 'green' : 'default'">
              {{ record.isActive ? '启用' : '停用' }}
            </a-tag>
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
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import { format } from 'date-fns';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService } from '@/services/modules/classes';
import { homeworkService } from '@/services/modules/homework';
import type { Classes, Homework, HomeworkProblem, HomeworkQuery } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const classId = computed(() => (route.params.classId as string) || '');

const classInfo = ref<Classes>();
const query = reactive<HomeworkQuery>({ page: 1, size: 10, classId: classId.value, keyword: '' });
const list = ref<Homework[]>([]);
const total = ref(0);
const loading = ref(false);
const expandedRowKeys = ref<string[]>([]);
const problemListMap = reactive<Record<string, { loading: boolean; items: HomeworkProblem[] }>>({});

const pageTitle = computed(() => (classInfo.value ? `${classInfo.value.name}的作业` : '作业管理'));

const columns: TableColumnType<Homework>[] = [
  { title: '标题', dataIndex: 'title', key: 'title', width: 220 },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime', width: 180 },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime', width: 180 },
  { title: '进度', dataIndex: 'progress', key: 'progress', width: 140 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'actions', width: 160 },
];

const problemColumns: TableColumnType<HomeworkProblem>[] = [
  { title: '题目ID', dataIndex: 'id', key: 'id', width: 120 },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '难度', dataIndex: 'difficulty', key: 'difficulty', width: 100 },
  { title: 'AC/提交', dataIndex: 'acCount', key: 'acCount', width: 120 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 180 },
  { title: '操作', key: 'actions', width: 140 },
];

const difficultyLabel = (val?: string) => {
  if (val === 'easy') return '简单';
  if (val === 'medium') return '中等';
  if (val === 'hard') return '困难';
  return val || '-';
};

const difficultyColor = (val?: string) => {
  if (val === 'easy') return 'green';
  if (val === 'medium') return 'orange';
  if (val === 'hard') return 'red';
  return 'blue';
};

const loadClass = async () => {
  try {
    const data = await classesService.fetchDetail(classId.value);
    classInfo.value = data;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级信息失败'));
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await homeworkService.fetchList({ ...query, activeOnly: false });
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取作业列表失败'));
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  handleSearch();
};

const loadProblems = async (homeworkId: string) => {
  if (!problemListMap[homeworkId]) {
    problemListMap[homeworkId] = { loading: false, items: [] };
  }
  problemListMap[homeworkId].loading = true;
  try {
    const data = await homeworkService.fetchProblems(homeworkId);
    problemListMap[homeworkId].items = data;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取作业题目失败'));
  } finally {
    problemListMap[homeworkId].loading = false;
  }
};

const handleExpand = (expanded: boolean, record: Homework) => {
  if (expanded) {
    expandedRowKeys.value = [...expandedRowKeys.value, record.id];
    loadProblems(record.id);
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((k) => k !== record.id);
  }
};

const progressLabel = (start?: string, end?: string) => {
  if (!start && !end) return '未设置';
  const now = new Date().getTime();
  const startAt = start ? new Date(start).getTime() : undefined;
  const endAt = end ? new Date(end).getTime() : undefined;
  if (startAt && now < startAt) return '未开始';
  if (endAt && now > endAt) return '已结束';
  return '进行中';
};

const progressColor = (start?: string, end?: string) => {
  const label = progressLabel(start, end);
  if (label === '未开始') return 'default';
  if (label === '进行中') return 'blue';
  if (label === '已结束') return 'red';
  return 'default';
};

const goCreate = () => {
  router.push(`/admin/classes/${classId.value}/homeworks/create`);
};

const edit = (record: Homework) => {
  router.push(`/admin/classes/${classId.value}/homeworks/${record.id}/edit`);
};

const remove = async (record: Homework) => {
  try {
    await homeworkService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Homework) => {
  Modal.confirm({
    title: '删除作业',
    content: `确认删除作业「${record.title}」？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

const removeProblem = async (homeworkId: string, problem: HomeworkProblem) => {
  try {
    await homeworkService.removeProblem(homeworkId, problem.id);
    message.success('已从作业移除该题目');
    loadProblems(homeworkId);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '移除题目失败'));
  }
};

const confirmRemoveProblem = (homeworkId: string, problem: HomeworkProblem) => {
  Modal.confirm({
    title: '移除题目',
    content: `确认从作业中移除题目「${problem.name || problem.problemId}」？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => removeProblem(homeworkId, problem),
  });
};

const goBack = () => {
  router.push('/admin/classes');
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

onMounted(() => {
  loadClass();
  loadData();
});
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}
</style>
