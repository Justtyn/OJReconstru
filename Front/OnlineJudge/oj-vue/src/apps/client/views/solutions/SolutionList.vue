<template>
  <PageContainer title="题解" subtitle="阅读他人的解题思路与经验分享">
    <div class="solution-page">
      <section class="solution-hero">
        <div class="solution-hero__top">
          <div class="solution-hero__title">题解筛选</div>
          <div class="solution-hero__stats">
            <span class="stat-pill">
              题解数量
              <span class="stat-pill__value">{{ totalLabel }}</span>
            </span>
            <span class="stat-pill">
              当前页
              <span class="stat-pill__value">{{ query.page }}</span>
            </span>
          </div>
        </div>
        <div class="solution-hero__filters">
          <a-input
            v-model:value="query.title"
            allow-clear
            size="small"
            placeholder="搜索题解标题"
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
        <div class="solution-list">
          <a-card v-for="item in list" :key="item.id" class="solution-card" hoverable>
            <div class="solution-card__header">
              <span class="solution-card__title">{{ item.title }}</span>
              <a-tag v-if="item.language" color="blue">{{ item.language }}</a-tag>
            </div>
            <div class="solution-card__meta">
              <span>题目：{{ problemLabel(item.problemId) }}</span>
              <span>作者：{{ authorLabel(item.userId) }}</span>
              <span>{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="solution-card__excerpt">{{ excerptText(item.content) }}</div>
            <div class="solution-card__footer">
              <a-space size="small">
                <a-tag v-if="item.isActive === false" color="red">未启用</a-tag>
              </a-space>
              <a-button type="link" @click="goDetail(item)">查看详情</a-button>
            </div>
          </a-card>
        </div>
        <a-empty v-if="!loading && !list.length" description="暂无题解" class="solution-empty" />
      </a-spin>

      <div class="solution-pagination" v-if="total > 0">
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
import { solutionService } from '@/services/modules/solution';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Solution, SolutionQuery, Problem, Student } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const query = reactive<SolutionQuery>({
  page: 1,
  size: 8,
  title: '',
  problemId: undefined,
  isActive: true,
});
const list = ref<Solution[]>([]);
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
    const data = await solutionService.fetchList(query);
    if (seq !== loadSeq) return;
    list.value = data.records || [];
    total.value = data.total || 0;
    preloadCaches(list.value);
  } catch (error) {
    if (seq === loadSeq) {
      message.error(extractErrorMessage(error, '获取题解失败'));
    }
  } finally {
    if (seq === loadSeq) loading.value = false;
  }
};

const preloadCaches = (records: Solution[]) => {
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
  query.title = '';
  query.problemId = undefined;
  handleSearch();
};

const handlePageChange = (page: number, size?: number) => {
  query.page = page;
  if (size) query.size = size;
  loadData();
};

const goDetail = (item: Solution) => {
  router.push(`/solutions/${item.id}`);
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
.solution-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.solution-hero {
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(125, 211, 252, 0.16), rgba(99, 102, 241, 0.18));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.solution-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.solution-hero__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.solution-hero__stats {
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

.solution-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.solution-list {
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

.solution-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
}

.solution-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.solution-card__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.solution-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-bottom: 8px;
}

.solution-card__excerpt {
  color: var(--text-color);
  opacity: 0.85;
  line-height: 1.6;
  min-height: 56px;
}

.solution-card__footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.solution-pagination {
  display: flex;
  justify-content: center;
  padding: 12px 0 4px;
}

.solution-empty {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .solution-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }
}

:global(:root[data-theme='dark']) .solution-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(67, 56, 202, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .stat-pill,
:global(:root[data-theme='dark']) .solution-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: rgba(15, 23, 42, 0.7);
}

:global(:root[data-theme='dark']) .stat-pill__value {
  color: #38bdf8;
}
</style>
