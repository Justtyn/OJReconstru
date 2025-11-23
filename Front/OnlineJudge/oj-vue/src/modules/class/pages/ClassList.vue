<template>
  <PageContainer title="班级管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="名称/描述/邀请码"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建班级</a-button>
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
          <template v-if="column.key === 'startDate' || column.key === 'endDate'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd') : '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="goHomeworks(record)">作业管理</a-button>
              <a-divider type="vertical" />
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
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService } from '@/services/modules/classes';
import type { Classes, ClassesQuery } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const query = reactive<ClassesQuery>({ page: 1, size: 10, keyword: '' });
const list = ref<Classes[]>([]);
const total = ref(0);
const loading = ref(false);

const columns: TableColumnType<Classes>[] = [
  { title: '名称', dataIndex: 'name', key: 'name', width: 200 },
  { title: '邀请码', dataIndex: 'code', key: 'code', width: 140 },
  { title: '开始日期', dataIndex: 'startDate', key: 'startDate', width: 140 },
  { title: '结束日期', dataIndex: 'endDate', key: 'endDate', width: 140 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'actions', width: 240 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await classesService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级列表失败'));
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
  router.push('/admin/classes/create');
};

const edit = (record: Classes) => {
  router.push(`/admin/classes/${record.id}/edit`);
};

const goHomeworks = (record: Classes) => {
  router.push(`/admin/classes/${record.id}/homeworks`);
};

const remove = async (record: Classes) => {
  try {
    await classesService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Classes) => {
  Modal.confirm({
    title: '删除班级',
    content: `确认删除班级「${record.name}」？`,
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
