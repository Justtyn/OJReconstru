<template>
  <PageContainer title="管理员管理">
    <a-card>
      <a-form layout="inline" :model="query" @submit.prevent="loadData">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="用户名/姓名/邮箱"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建管理员</a-button>
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
          <template v-if="column.key === 'sex'">
            <a-tag :color="getSexTagColor(text)">{{ formatSexLabel(text) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'lastLoginTime'">
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
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { adminUserService } from '@/services/modules/adminUser';
import type { AdminQuery, AdminUser } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const query = reactive<AdminQuery>({ page: 1, size: 10, keyword: '' });
const list = ref<AdminUser[]>([]);
const total = ref(0);
const loading = ref(false);

const columns: TableColumnType<AdminUser>[] = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'sex', key: 'sex', width: 80 },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '最后登录', dataIndex: 'lastLoginTime', key: 'lastLoginTime' },
  { title: '操作', key: 'actions', width: 160 },
];

const formatSexLabel = (sex?: string | null) => {
  if (sex === 'male') return '男';
  if (sex === 'female') return '女';
  return '未填写';
};

const getSexTagColor = (sex?: string | null) => {
  if (sex === 'male') return 'blue';
  if (sex === 'female') return 'magenta';
  return 'default';
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await adminUserService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取管理员列表失败'));
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
  router.push('/admin/users/create');
};

const edit = (record: AdminUser) => {
  router.push(`/admin/users/${record.id}/edit`);
};

const remove = async (record: AdminUser) => {
  try {
    await adminUserService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: AdminUser) => {
  Modal.confirm({
    title: '删除管理员',
    content: `确认删除管理员「${record.username}」？不可恢复`,
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
