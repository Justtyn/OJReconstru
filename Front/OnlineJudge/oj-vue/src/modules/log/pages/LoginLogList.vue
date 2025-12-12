<template>
  <PageContainer title="登录日志">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="角色">
          <a-select v-model:value="query.role" allow-clear placeholder="全部角色" style="width: 140px">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="teacher">教师</a-select-option>
            <a-select-option value="student">学生</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户ID">
          <a-input v-model:value="query.userId" allow-clear placeholder="按用户ID筛选" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input
            v-model:value="query.username"
            allow-clear
            placeholder="按用户名模糊匹配"
            @pressEnter="handleSearch"
            style="width: 200px"
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
          <template v-if="column.key === 'role'">
            <a-tag :color="roleColorMap[record.role] || 'blue'">
              {{ roleLabelMap[record.role] || record.role || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'success'">
            <a-badge :status="record.success ? 'success' : 'error'" :text="record.success ? '成功' : '失败'" />
          </template>
          <template v-else-if="column.key === 'loginTime' || column.key === 'logoutTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="openDetail(record.id)">查看</a-button>
              <a-divider type="vertical" />
              <a-button danger type="link" size="small" @click="confirmRemove(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer
      :width="520"
      :open="detailVisible"
      title="日志详情"
      destroy-on-close
      @close="detailVisible = false"
    >
      <a-descriptions :column="1" size="small" bordered>
        <a-descriptions-item label="角色">{{ roleLabelMap[detail?.role || ''] || detail?.role || '-' }}</a-descriptions-item>
        <a-descriptions-item label="用户">
          {{ detail?.username }} <span class="muted">(#{{ detail?.userId }})</span>
        </a-descriptions-item>
        <a-descriptions-item label="登录结果">
          <a-badge :status="detail?.success ? 'success' : 'error'" :text="detail?.success ? '成功' : '失败'" />
        </a-descriptions-item>
        <a-descriptions-item label="登录时间">
          {{ detail?.loginTime ? format(new Date(detail.loginTime), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="登出时间">
          {{ detail?.logoutTime ? format(new Date(detail.logoutTime), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="IP 地址">{{ detail?.ipAddress || '-' }}</a-descriptions-item>
        <a-descriptions-item label="归属地">{{ detail?.location || '-' }}</a-descriptions-item>
        <a-descriptions-item label="设备">{{ detail?.device || '-' }}</a-descriptions-item>
        <a-descriptions-item label="User Agent">
          <div class="ua-text">{{ detail?.userAgent || '-' }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="失败原因">{{ detail?.failReason || '-' }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ detail?.createdAt ? format(new Date(detail.createdAt), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="更新时间">
          {{ detail?.updatedAt ? format(new Date(detail.updatedAt), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import type { TableColumnType } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { loginLogService } from '@/services/modules/loginLog';
import type { LoginLog, LoginLogQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const query = reactive<LoginLogQuery>({ page: 1, size: 10, role: undefined, userId: '', username: '' });
const list = ref<LoginLog[]>([]);
const total = ref(0);
const loading = ref(false);
const detail = ref<LoginLog>();
const detailVisible = ref(false);

const columns: TableColumnType<LoginLog>[] = [
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 140 },
  { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 120 },
  { title: 'IP 地址', dataIndex: 'ipAddress', key: 'ipAddress', width: 160 },
  { title: '归属地', dataIndex: 'location', key: 'location', width: 160 },
  { title: '设备', dataIndex: 'device', key: 'device', ellipsis: true },
  { title: '登录时间', dataIndex: 'loginTime', key: 'loginTime', width: 180 },
  { title: '登出时间', dataIndex: 'logoutTime', key: 'logoutTime', width: 180 },
  { title: '结果', dataIndex: 'success', key: 'success', width: 90 },
  { title: '操作', key: 'actions', width: 150 },
];

const roleLabelMap: Record<string, string> = {
  admin: '管理员',
  teacher: '教师',
  student: '学生',
};

const roleColorMap: Record<string, string> = {
  admin: 'red',
  teacher: 'blue',
  student: 'green',
};

const loadData = async () => {
  loading.value = true;
  try {
    const data = await loginLogService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取日志失败'));
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.role = undefined;
  query.userId = '';
  query.username = '';
  handleSearch();
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

const openDetail = async (id: string) => {
  try {
    const data = await loginLogService.fetchDetail(id);
    detail.value = data;
    detailVisible.value = true;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取详情失败'));
  }
};

const remove = async (record: LoginLog) => {
  try {
    await loginLogService.remove(record.id);
    message.success('删除成功');
    if (detailVisible.value && detail.value?.id === record.id) {
      detailVisible.value = false;
    }
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: LoginLog) => {
  Modal.confirm({
    title: '删除日志',
    content: `确认删除该登录日志记录（用户：${record.username}）？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

loadData();
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}

.muted {
  color: var(--text-color-secondary, #999);
}

.ua-text {
  word-break: break-all;
  white-space: pre-wrap;
}
</style>
