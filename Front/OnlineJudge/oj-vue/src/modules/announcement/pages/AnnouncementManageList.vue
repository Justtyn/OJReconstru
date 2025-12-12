<template>
  <PageContainer title="公告管理">
    <a-card>
      <a-form layout="inline" :model="query" class="filter-bar">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="标题"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="显示范围">
          <a-segmented
            v-model:value="pinnedFilter"
            :options="pinnedOptions"
            size="middle"
            @change="handlePinnedChange"
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
              <a-button type="link" size="small" @click="openPreview(record)">预览</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" danger size="small" @click="confirmRemove(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer
      width="760"
      :open="previewVisible"
      title="预览公告"
      destroy-on-close
      @close="closePreview"
    >
      <a-spin :spinning="previewLoading">
        <template v-if="previewRecord">
          <a-space direction="vertical" style="width: 100%">
            <a-space>
              <a-tag v-if="previewRecord.isPinned" color="gold">置顶</a-tag>
              <a-tag v-if="!previewRecord.isActive" color="red">未启用</a-tag>
              <span>{{ previewRecord.time ? format(new Date(previewRecord.time), 'yyyy-MM-dd HH:mm') : '' }}</span>
            </a-space>
            <h3 class="preview-title">{{ previewRecord.title }}</h3>
            <div class="markdown-body" v-html="renderMarkdown(previewRecord.content || '')"></div>
          </a-space>
        </template>
      </a-spin>
    </a-drawer>
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
import { renderMarkdown } from '@/utils/markdown';

const router = useRouter();

const query = reactive<AnnouncementQuery>({ page: 1, size: 10, keyword: '', pinnedOnly: false });
const list = ref<AnnouncementVO[]>([]);
const total = ref(0);
const loading = ref(false);
const previewVisible = ref(false);
const previewLoading = ref(false);
const previewRecord = ref<AnnouncementVO | null>(null);
const pinnedOptions = [
  { label: '全部', value: 'all' },
  { label: '仅置顶', value: 'pinned' },
];
const pinnedFilter = computed({
  get: () => (query.pinnedOnly ? 'pinned' : 'all'),
  set: (val: string) => {
    query.pinnedOnly = val === 'pinned';
  },
});

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
    const params: AnnouncementQuery = {
      ...query,
      pinnedOnly: query.pinnedOnly || undefined,
      isPinned: query.pinnedOnly || undefined,
    };
    const data = await adminAnnouncementService.fetchList(params);
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

const handlePinnedChange = () => {
  handleSearch();
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

const openPreview = async (record: AnnouncementVO) => {
  previewVisible.value = true;
  previewLoading.value = true;
  try {
    previewRecord.value = await adminAnnouncementService.fetchDetail(record.id);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取公告详情失败'));
  } finally {
    previewLoading.value = false;
  }
};

const closePreview = () => {
  previewVisible.value = false;
  previewRecord.value = null;
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

.filter-bar {
  gap: 8px;
}

.preview-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.markdown-body {
  background: var(--card-bg);
  padding: 12px;
  border-radius: 10px;
  border: 1px solid rgba(15, 23, 42, 0.06);
}
</style>
