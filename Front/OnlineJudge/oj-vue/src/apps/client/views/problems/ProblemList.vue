<template>
  <PageContainer title="题库" subtitle="挑选题目、查看详情、立即提交">
    <div class="problem-page">
      <section class="problem-hero">
        <div class="problem-hero__top">
          <div class="problem-hero__title">题库筛选</div>
          <div class="problem-hero__stats">
            <span class="stat-pill">
              题目数量
              <span class="stat-pill__value">{{ totalLabel }}</span>
            </span>
            <span class="stat-pill">
              当前页
              <span class="stat-pill__value">{{ query.page }}</span>
            </span>
          </div>
        </div>
        <div class="problem-hero__filters">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            size="small"
            placeholder="搜索题目名称或关键字"
            class="filter-input"
            @pressEnter="handleSearch"
          />
          <a-select
            v-model:value="query.difficulty"
            allow-clear
            size="small"
            placeholder="难度"
            class="filter-select"
            @change="handleSearch"
          >
            <a-select-option value="easy">简单</a-select-option>
            <a-select-option value="medium">中等</a-select-option>
            <a-select-option value="hard">困难</a-select-option>
          </a-select>
          <a-button type="primary" size="small" @click="handleSearch">查询</a-button>
          <a-button size="small" @click="resetQuery">重置</a-button>
        </div>
      </section>

      <a-spin :spinning="loading">
        <div class="problem-list">
          <a-card v-for="item in list" :key="item.id" class="problem-card" hoverable>
            <div class="problem-card__body">
              <div class="problem-card__info">
                <div class="problem-card__header">
                  <span class="problem-card__id">#{{ item.id }}</span>
                  <a-tag v-if="isDailyChallenge(item.dailyChallenge)" color="gold">每日挑战</a-tag>
                </div>
                <div class="problem-card__title">
                  <span class="problem-card__title-text">{{ item.name }}</span>
                  <a-tag class="problem-card__difficulty" :color="difficultyColor(item.difficulty)">
                    {{ difficultyLabel(item.difficulty) }}
                  </a-tag>
                </div>
                <div class="problem-card__desc">{{ summaryText(item) }}</div>
                <div class="problem-card__meta">
                  <a-tag class="limit-tag limit-tag--time" color="geekblue">
                    {{ formatLimit(item.timeLimitMs, 'ms') }}
                  </a-tag>
                  <a-tag class="limit-tag limit-tag--memory" color="green">
                    {{ formatLimit(item.memoryLimitKb, 'KB') }}
                  </a-tag>
                </div>
              </div>
              <div class="problem-card__aside">
                <div class="problem-card__stats">
                  <div class="stat-item">
                    <div class="stat-item__value">{{ passRate(item) }}%</div>
                    <div class="stat-item__label">通过率</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-item__value">{{ item.acCount ?? 0 }}</div>
                    <div class="stat-item__label">通过</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-item__value">{{ item.submitCount ?? 0 }}</div>
                    <div class="stat-item__label">提交</div>
                  </div>
                </div>
                <a-button type="link" size="small" @click="goDetail(item)">查看详情</a-button>
              </div>
            </div>
          </a-card>
        </div>

        <a-empty v-if="!loading && !list.length" description="暂无题目" class="problem-empty" />
      </a-spin>

      <div class="problem-pagination" v-if="total > 0">
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
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { problemService } from '@/services/modules/problem';
import type { Problem, ProblemDifficulty, ProblemQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const query = reactive<ProblemQuery>({
  page: 1,
  size: 12,
  keyword: '',
  difficulty: undefined,
  isActive: true,
});
const list = ref<Problem[]>([]);
const total = ref(0);
const loading = ref(false);
let loadSeq = 0;

const totalLabel = computed(() => (total.value ? total.value.toString() : '-'));

const loadData = async (options?: { silent?: boolean }) => {
  const silent = options?.silent ?? false;
  const seq = (loadSeq += 1);
  if (!silent) loading.value = true;
  try {
    const data = await problemService.fetchList(query);
    if (seq !== loadSeq) return;
    list.value = data.records || [];
    total.value = data.total || 0;
  } catch (error) {
    if (seq === loadSeq) {
      message.error(extractErrorMessage(error, '获取题目失败'));
    }
  } finally {
    if (!silent && seq === loadSeq) loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  query.difficulty = undefined;
  handleSearch();
};

const handlePageChange = (page: number, size?: number) => {
  query.page = page;
  if (size) query.size = size;
  loadData();
};

const goDetail = (item: Problem) => {
  router.push(`/problems/${item.id}`);
};

const difficultyLabel = (val?: ProblemDifficulty) => {
  if (!val) return '未知';
  const key = val.toLowerCase();
  if (key === 'easy') return '简单';
  if (key === 'medium') return '中等';
  if (key === 'hard') return '困难';
  return val;
};

const difficultyColor = (val?: ProblemDifficulty) => {
  const key = val?.toLowerCase();
  if (key === 'easy') return 'green';
  if (key === 'medium') return 'orange';
  if (key === 'hard') return 'red';
  return 'default';
};

const passRate = (record: Problem) => {
  const submit = record.submitCount ?? 0;
  if (!submit) return 0;
  return Math.round(((record.acCount ?? 0) / submit) * 100);
};

const isDailyChallenge = (value?: string | number | boolean | null) =>
  value === 1 || value === '1' || value === true;

const stripHtml = (input?: string | null) => {
  if (!input) return '';
  return input.replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
};

const summaryText = (record: Problem) => {
  const base = stripHtml(record.description) || stripHtml(record.descriptionInput) || stripHtml(record.descriptionOutput);
  if (!base) return '暂无描述';
  return base.length > 80 ? `${base.slice(0, 80)}…` : base;
};

const formatLimit = (value?: number, unit?: string) => (value ? `${value}${unit ?? ''}` : '-');

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.problem-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.problem-hero {
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.14), rgba(59, 130, 246, 0.16));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.problem-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.problem-hero__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.problem-hero__stats {
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

.problem-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.problem-list {
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

.problem-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: var(--card-bg, #ffffff);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.1);
}

.problem-card__body {
  display: flex;
  gap: 18px;
  align-items: stretch;
}

.problem-card__info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.problem-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  color: var(--text-muted, #64748b);
  font-size: 12px;
}

.problem-card__id {
  font-weight: 600;
  color: var(--text-color);
}

.problem-card__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 8px;
}

.problem-card__title-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.problem-card__difficulty {
  margin-right: 0;
}

.problem-card__desc {
  color: var(--text-muted, #64748b);
  font-size: 13px;
  line-height: 1.6;
  min-height: 42px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.problem-card__meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.limit-tag {
  font-size: 12px;
  margin-right: 0;
}

.problem-card__aside {
  width: 280px;
  flex: 0 0 280px;
  padding-left: 16px;
  border-left: 1px solid rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
  justify-content: center;
}

.problem-card__stats {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  flex-wrap: nowrap;
}

.stat-item {
  width: 78px;
  padding: 6px 8px;
  border-radius: 10px;
  background: var(--body-bg);
  border: 1px solid rgba(15, 23, 42, 0.06);
  text-align: center;
  flex: 0 0 auto;
}

.stat-item__value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.stat-item__label {
  margin-top: 2px;
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.problem-pagination {
  display: flex;
  justify-content: center;
  padding: 12px 0 4px;
}

.problem-empty {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .problem-hero__content {
    flex-direction: column;
    align-items: flex-start;
  }

  .problem-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }

  .problem-card__body {
    flex-direction: column;
  }

  .problem-card__aside {
    width: 100%;
    flex: none;
    padding-left: 0;
    padding-top: 12px;
    border-left: none;
    border-top: 1px solid rgba(15, 23, 42, 0.1);
    align-items: flex-start;
  }

  .problem-card__stats {
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}

:global(:root[data-theme='dark']) .problem-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(30, 64, 175, 0.35));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .stat-pill {
  background: rgba(15, 23, 42, 0.7);
  border-color: rgba(148, 163, 184, 0.2);
}

:global(:root[data-theme='dark']) .stat-pill__value {
  color: #38bdf8;
}

:global(:root[data-theme='dark']) .problem-card {
  border-color: rgba(148, 163, 184, 0.3);
  background: rgba(15, 23, 42, 0.7);
  box-shadow: 0 18px 34px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .problem-card__aside {
  border-left-color: rgba(148, 163, 184, 0.25);
}

:global(:root[data-theme='dark']) .stat-item {
  background: rgba(15, 23, 42, 0.7);
  border-color: rgba(148, 163, 184, 0.18);
}
</style>
