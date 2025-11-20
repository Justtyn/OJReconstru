<template>
  <PageContainer title="公告管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="标题"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-switch
            v-model:checked="query.pinnedOnly"
            checked-children="仅置顶"
            un-checked-children="全部"
            @change="handleSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建公告</a-button>
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
          <template v-if="column.key === 'time'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'isPinned'">
            <a-tag :color="record.isPinned ? 'gold' : 'default'">
              {{ record.isPinned ? '置顶' : '普通' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isActive'">
            <a-tag :color="record.isActive ? 'green' : 'red'">
              {{ record.isActive ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" danger size="small" @click="confirmRemove(record)">删除</a-button>
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
import { adminAnnouncementService } from '@/services/modules/adminAnnouncement';
import type { AnnouncementVO, AnnouncementQuery } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const query = reactive<AnnouncementQuery>({ page: 1, size: 10, keyword: '', pinnedOnly: false });
const list = ref<AnnouncementVO[]>([]);
const total = ref(0);
const loading = ref(false);

const columns: TableColumnType<AnnouncementVO>[] = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '发布时间', dataIndex: 'time', key: 'time', width: 180 },
  { title: '置顶', dataIndex: 'isPinned', key: 'isPinned', width: 100 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '操作', key: 'actions', width: 160 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await adminAnnouncementService.fetchList({
      ...query,
      pinnedOnly: query.pinnedOnly || undefined,
      isPinned: query.pinnedOnly || undefined,
    });
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取公告列表失败'));
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
  query.pinnedOnly = false;
  handleSearch();
};

const goCreate = () => {
  router.push('/admin/announcements/create');
};

const edit = (record: AnnouncementVO) => {
  router.push(`/admin/announcements/${record.id}/edit`);
};

const remove = async (record: AnnouncementVO) => {
  try {
    await adminAnnouncementService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: AnnouncementVO) => {
  Modal.confirm({
    title: '删除公告',
    content: `确认删除公告「${record.title}」？`,
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
