<template>
  <PageContainer title="学生管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="用户名">
          <a-input v-model:value="query.username" allow-clear placeholder="按用户名搜索" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="query.email" allow-clear placeholder="按邮箱搜索" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item v-if="canCreate" style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建学生</a-button>
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
          <template v-if="column.key === 'avatar'">
            <a-avatar
              :src="record.avatar"
              shape="square"
              :size="48"
              style="border-radius: 6px"
            >
              <template #icon>{{ (record.username && record.username[0]) || 'S' }}</template>
            </a-avatar>
          </template>
          <template v-else-if="column.key === 'sex'">
            <a-tag :color="getSexTagColor(text)">{{ formatSexLabel(text) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'ac'">
            {{ record.ac ?? 0 }}
          </template>
          <template v-else-if="column.key === 'submit'">
            {{ record.submit ?? 0 }}
          </template>
          <template v-else-if="column.key === 'passRate'">
            <a-tag color="geekblue">{{ getPassRate(record) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'lastLoginTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'isVerified'">
            <a-tag :color="record.isVerified ? 'green' : 'orange'">
              {{ record.isVerified ? '已验证' : '未验证' }}
            </a-tag>
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
import { studentService } from '@/services/modules/student';
import type { Student, StudentQuery } from '@/types';
import { useAuthStore } from '@/stores/auth';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const authStore = useAuthStore();

const query = reactive<StudentQuery>({ page: 1, size: 10, username: '', email: '' });
const list = ref<Student[]>([]);
const total = ref(0);
const loading = ref(false);

const canCreate = computed(() => authStore.role === 'admin');

const columns: TableColumnType<Student>[] = [
  { title: '头像', dataIndex: 'avatar', key: 'avatar', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'sex', key: 'sex', width: 80 },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '学校', dataIndex: 'school', key: 'school' },
  { title: 'AC', dataIndex: 'ac', key: 'ac', width: 80 },
  { title: '提交', dataIndex: 'submit', key: 'submit', width: 80 },
  { title: '通过率', dataIndex: 'passRate', key: 'passRate', width: 100 },
  { title: '积分', dataIndex: 'score', key: 'score', width: 80 },
  { title: '邮箱状态', dataIndex: 'isVerified', key: 'isVerified', width: 100 },
  { title: '最近登录', dataIndex: 'lastLoginTime', key: 'lastLoginTime', width: 180 },
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

const getPassRate = (record: Student) => {
  const submit = record.submit ?? 0;
  const ac = record.ac ?? 0;
  if (!submit) return '0%';
  const rate = (ac / submit) * 100;
  return `${rate.toFixed(1)}%`;
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await studentService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取学生列表失败'));
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.username = '';
  query.email = '';
  handleSearch();
};

const goCreate = () => {
  router.push('/admin/students/create');
};

const edit = (record: Student) => {
  router.push(`/admin/students/${record.id}/edit`);
};

const remove = async (record: Student) => {
  try {
    await studentService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Student) => {
  Modal.confirm({
    title: '删除学生',
    content: `确认删除学生「${record.name || record.username}」？`,
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
