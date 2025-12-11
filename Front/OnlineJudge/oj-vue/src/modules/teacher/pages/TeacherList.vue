<template>
  <PageContainer title="教师管理">
    <a-card>
      <a-form layout="inline" :model="query">
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
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'sex'">
            <a-tag :color="getSexTagColor(text)">{{ formatSexLabel(text) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
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
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { teacherService } from '@/services/modules/teacher';
import type { Teacher, TeacherQuery } from '@/types';
import { useAuthStore } from '@/stores/auth';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const authStore = useAuthStore();

const query = reactive<TeacherQuery>({ page: 1, size: 10, keyword: '' });
const list = ref<Teacher[]>([]);
const total = ref(0);
const loading = ref(false);

const canCreate = computed(() => authStore.role === 'admin');

const columns: TableColumnType<Teacher>[] = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'sex', key: 'sex', width: 80 },
  { title: '职称', dataIndex: 'title', key: 'title' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  {
    title: '最近登录',
    dataIndex: 'lastLoginTime',
    key: 'lastLoginTime',
    customRender: ({ text }) => (text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-'),
    width: 180,
  },
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
    const data = await teacherService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取教师列表失败'));
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
  router.push('/admin/teachers/create');
};

const edit = (record: Teacher) => {
  router.push(`/admin/teachers/${record.id}/edit`);
};

const remove = async (record: Teacher) => {
  try {
    await teacherService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
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
