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
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'startTime' || column.key === 'endTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
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
import type { Classes, Homework, HomeworkQuery } from '@/types';
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

const pageTitle = computed(() => (classInfo.value ? `${classInfo.value.name}的作业` : '作业管理'));

const columns: TableColumnType<Homework>[] = [
  { title: '标题', dataIndex: 'title', key: 'title', width: 220 },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime', width: 180 },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime', width: 180 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'actions', width: 160 },
];

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
