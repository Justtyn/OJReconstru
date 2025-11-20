<template>
  <PageContainer title="系统公告">
    <a-space direction="vertical" class="announcement-actions">
      <a-input-search
        v-model:value="query.keyword"
        placeholder="搜索标题"
        enter-button="搜索"
        @search="handleSearch"
        allow-clear
      />
      <a-space>
        <a-switch
          v-model:checked="query.pinnedOnly"
          checked-children="仅置顶"
          un-checked-children="全部公告"
          @change="handleSearch"
        />
      </a-space>
    </a-space>

    <a-list
      :data-source="announcements"
      :loading="loading"
      item-layout="vertical"
      class="announcement-list"
      :pagination="paginationConfig"
    >
      <template #renderItem="{ item }">
        <a-list-item @click="openDetail(item.id)">
          <a-list-item-meta :description="formatDate(item.time)">
            <template #title>
              <span>{{ item.title }}</span>
              <a-tag v-if="item.isPinned" color="gold" style="margin-left: 8px">置顶</a-tag>
            </template>
          </a-list-item-meta>
          <div class="announcement-excerpt">
            {{ extractExcerpt(item.content) }}
          </div>
        </a-list-item>
      </template>
    </a-list>

    <a-drawer
      width="640"
      :open="detailOpen"
      :title="currentDetail?.title"
      @close="detailOpen = false"
    >
      <a-space direction="vertical" style="width: 100%">
        <a-space>
          <a-tag v-if="currentDetail?.isPinned" color="gold">置顶</a-tag>
          <a-tag v-if="!currentDetail?.isActive" color="red">未启用</a-tag>
          <span>{{ formatDate(currentDetail?.time) }}</span>
        </a-space>
        <div class="markdown-body" v-html="renderMarkdown(currentDetail?.content ?? '')"></div>
      </a-space>
    </a-drawer>
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import { fetchPublicAnnouncements, fetchPublicAnnouncementDetail } from '@/services/modules/announcement';
import type { AnnouncementVO, AnnouncementQuery } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';

const query = reactive<AnnouncementQuery>({
  page: 1,
  size: 10,
  keyword: '',
  pinnedOnly: false,
});

const announcements = ref<AnnouncementVO[]>([]);
const total = ref(0);
const loading = ref(false);
const detailOpen = ref(false);
const currentDetail = ref<AnnouncementVO | null>(null);

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

const loadData = async () => {
  loading.value = true;
  try {
    const data = await fetchPublicAnnouncements(query);
    announcements.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取公告失败'));
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const openDetail = async (id: string) => {
  detailOpen.value = true;
  currentDetail.value = null;
  try {
    currentDetail.value = await fetchPublicAnnouncementDetail(id);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取公告详情失败'));
    detailOpen.value = false;
  }
};

const extractExcerpt = (content: string) => {
  const text = content?.replace(/[#>*`]/g, '').slice(0, 120);
  return `${text}${content?.length > 120 ? '…' : ''}`;
};

const formatDate = (value?: string) => {
  if (!value) return '';
  return format(new Date(value), 'yyyy-MM-dd HH:mm');
};

onMounted(loadData);
</script>

<style scoped lang="less">
.announcement-actions {
  width: 100%;
  margin-bottom: 16px;
}

.announcement-list {
  background: var(--card-bg);
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.announcement-excerpt {
  color: var(--text-color);
  opacity: 0.8;
}

.markdown-body :deep(p) {
  line-height: 1.8;
}
</style>
