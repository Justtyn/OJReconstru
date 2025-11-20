<template>
  <PageContainer title="教师管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="姓名">
          <a-input v-model:value="query.name" allow-clear placeholder="请输入姓名" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="query.username" allow-clear placeholder="请输入用户名" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="职称">
          <a-input v-model:value="query.title" allow-clear placeholder="请输入职称" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item v-if="canCreate" style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建教师</a-button>
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
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.isActive ? 'green' : 'red'">
              {{ record.isActive ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="toggleStatus(record)">
                {{ record.isActive ? '禁用' : '启用' }}
              </a-button>
              <template v-if="authStore.role === 'admin'">
                <a-divider type="vertical" />
                <a-button danger type="link" size="small" @click="confirmRemove(record)">删除</a-button>
              </template>
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
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { teacherService } from '@/services/modules/teacher';
import type { Teacher, TeacherQuery } from '@/types';
import { useAuthStore } from '@/stores/auth';
import type { TableColumnType } from 'ant-design-vue';

const router = useRouter();
const authStore = useAuthStore();

const query = reactive<TeacherQuery>({ page: 1, size: 10 });
const list = ref<Teacher[]>([]);
const total = ref(0);
const loading = ref(false);

const canCreate = computed(() => authStore.role === 'admin');

const columns: TableColumnType<Teacher>[] = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '职称', dataIndex: 'title', key: 'title' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '状态', dataIndex: 'isActive', key: 'status' },
  { title: '操作', key: 'actions', width: 220 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await teacherService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(error?.message || '获取教师列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.name = '';
  query.username = '';
  query.title = '';
  handleSearch();
};

const goCreate = () => {
  router.push('/admin/teachers/create');
};

const edit = (record: Teacher) => {
  router.push(`/admin/teachers/${record.id}/edit`);
};

const toggleStatus = async (record: Teacher) => {
  const nextStatus = !record.isActive;
  try {
    await teacherService.update(record.id, { username: record.username, isActive: nextStatus });
    message.success(nextStatus ? '已启用' : '已禁用');
    loadData();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  }
};

const remove = async (record: Teacher) => {
  try {
    await teacherService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(error?.message || '删除失败');
  }
};

const confirmRemove = (record: Teacher) => {
  Modal.confirm({
    title: '删除教师',
    content: `确认删除教师「${record.name || record.username}」？`,
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
