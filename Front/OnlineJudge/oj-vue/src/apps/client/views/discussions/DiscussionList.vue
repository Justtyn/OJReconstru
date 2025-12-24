<template>
  <PageContainer title="讨论" subtitle="分享想法、交流解题思路">
    <div class="discussion-page">
      <section class="discussion-hero">
        <div class="discussion-hero__top">
          <div class="discussion-hero__title">讨论筛选</div>
          <div class="discussion-hero__stats">
            <span class="stat-pill">
              讨论数量
              <span class="stat-pill__value">{{ totalLabel }}</span>
            </span>
            <span class="stat-pill">
              当前页
              <span class="stat-pill__value">{{ query.page }}</span>
            </span>
          </div>
        </div>
        <div class="discussion-hero__filters">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            size="small"
            placeholder="搜索讨论标题或关键字"
            class="filter-input"
            @pressEnter="handleSearch"
          />
          <a-select
            v-model:value="query.problemId"
            show-search
            allow-clear
            size="small"
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            placeholder="筛选题目"
            class="filter-select"
            @search="searchProblems"
            @change="handleSearch"
            @dropdownVisibleChange="handleProblemDropdown"
          />
          <a-button type="primary" size="small" @click="handleSearch">查询</a-button>
          <a-button size="small" @click="resetQuery">重置</a-button>
        </div>
      </section>

      <a-spin :spinning="loading">
        <div class="discussion-list">
          <a-card v-for="item in list" :key="item.id" class="discussion-card" hoverable>
            <div class="discussion-card__header">
              <span class="discussion-card__title">{{ item.title }}</span>
              <a-tag v-if="item.isActive === false" color="red">未启用</a-tag>
            </div>
            <div class="discussion-card__meta">
              <span>题目：{{ problemLabel(item.problemId) }}</span>
              <span>作者：{{ authorLabel(item.userId) }}</span>
              <span>{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="discussion-card__excerpt">{{ excerptText(item.content) }}</div>
            <div class="discussion-card__footer">
              <span class="discussion-card__hint">进入详情查看讨论内容与评论</span>
              <a-button type="link" @click="goDetail(item)">查看详情</a-button>
            </div>
          </a-card>
        </div>
        <a-empty v-if="!loading && !list.length" description="暂无讨论" class="discussion-empty" />
      </a-spin>

      <div class="discussion-pagination" v-if="total > 0">
        <a-pagination
          :current="query.page"
          :page-size="query.size"
          :total="total"
          show-size-changer
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
        />
      </div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { discussionService } from '@/services/modules/discussion';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Discussion, DiscussionQuery, Problem, Student } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const query = reactive<DiscussionQuery>({
  page: 1,
  size: 8,
  keyword: '',
  problemId: undefined,
  isActive: true,
});
const list = ref<Discussion[]>([]);
const total = ref(0);
const loading = ref(false);
const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const problemCache = reactive<Record<string, Problem>>({});
const studentCache = reactive<Record<string, Student>>({});
let loadSeq = 0;

const totalLabel = computed(() => (total.value ? total.value.toString() : '-'));

const loadData = async () => {
  const seq = (loadSeq += 1);
  loading.value = true;
  try {
    const data = await discussionService.fetchList(query);
    if (seq !== loadSeq) return;
    list.value = data.records || [];
    total.value = data.total || 0;
    preloadCaches(list.value);
  } catch (error) {
    if (seq === loadSeq) {
      message.error(extractErrorMessage(error, '获取讨论失败'));
    }
  } finally {
    if (seq === loadSeq) loading.value = false;
  }
};

const preloadCaches = (records: Discussion[]) => {
  const problemIds = Array.from(new Set(records.map((r) => r.problemId).filter((id) => id && !problemCache[id])));
  const userIds = Array.from(new Set(records.map((r) => r.userId).filter((id) => id && !studentCache[id])));
  if (problemIds.length) {
    Promise.all(
      problemIds.map(async (id) => {
        try {
          const detail = await problemService.fetchDetail(id);
          problemCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
  if (userIds.length) {
    Promise.all(
      userIds.map(async (id) => {
        try {
          const detail = await studentService.fetchDetail(id);
          studentCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  query.problemId = undefined;
  handleSearch();
};

const handlePageChange = (page: number, size?: number) => {
  query.page = page;
  if (size) query.size = size;
  loadData();
};

const goDetail = (item: Discussion) => {
  router.push(`/discussions/${item.id}`);
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword, isActive: true });
    problemOptions.value = data.records.map((p: Problem) => ({ label: `${p.name}（ID: ${p.id}）`, value: p.id }));
  } catch (error) {
    message.error(extractErrorMessage(error, '获取题目列表失败'));
  } finally {
    problemLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

const problemLabel = (id: string) => problemCache[id]?.name || id;
const authorLabel = (id: string) => {
  const student = studentCache[id];
  if (!student) return id;
  return student.name ? `${student.username}（${student.name}）` : student.username;
};

const excerptText = (content?: string) => {
  if (!content) return '暂无内容';
  const html = renderMarkdown(content);
  const plain = html.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
  if (!plain) return '暂无内容';
  return plain.length > 120 ? `${plain.slice(0, 120)}…` : plain;
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.discussion-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.discussion-hero {
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(253, 186, 116, 0.2), rgba(251, 191, 36, 0.18));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.discussion-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.discussion-hero__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.discussion-hero__stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-pill {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(15, 23, 42, 0.06);
  font-size: 12px;
  color: var(--text-color);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.stat-pill__value {
  font-weight: 600;
  color: #0ea5e9;
}

.discussion-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.discussion-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-input {
  width: 200px;
}

.filter-select {
  width: 200px;
}

.discussion-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
}

.discussion-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.discussion-card__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.discussion-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-bottom: 8px;
}

.discussion-card__excerpt {
  color: var(--text-color);
  opacity: 0.85;
  line-height: 1.6;
  min-height: 56px;
}

.discussion-card__footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discussion-card__hint {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.discussion-pagination {
  display: flex;
  justify-content: center;
  padding: 12px 0 4px;
}

.discussion-empty {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .discussion-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }
}

:global(:root[data-theme='dark']) .discussion-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(217, 119, 6, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .stat-pill,
:global(:root[data-theme='dark']) .discussion-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: rgba(15, 23, 42, 0.7);
}

:global(:root[data-theme='dark']) .stat-pill__value {
  color: #38bdf8;
}
</style>
