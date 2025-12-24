<template>
  <PageContainer>
    <template #header>
      <div class="detail-header">
        <div class="detail-header__title">
          <h1>{{ problem?.name || '题目详情' }}</h1>
          <a-space size="small">
            <a-tag :color="difficultyColor(problem?.difficulty)">{{ difficultyLabel(problem?.difficulty) }}</a-tag>
            <a-tag v-if="isDailyChallenge(problem?.dailyChallenge)" color="gold">每日挑战</a-tag>
            <a-tag v-if="homeworkId" color="cyan">作业题目</a-tag>
          </a-space>
        </div>
        <div class="detail-header__meta">
          <span>ID：{{ problem?.id || '-' }}</span>
          <span>通过率：{{ passRateText }}</span>
        </div>
      </div>
    </template>
    <template #extra>
      <a-button type="primary" :disabled="!problem" @click="goSubmit">提交代码</a-button>
    </template>

    <div class="problem-detail">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-spin :spinning="loading">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :lg="16">
            <a-card class="detail-card" title="题目说明">
              <div class="detail-section">
                <h3>题目描述</h3>
                <div class="detail-text">{{ formatText(problem?.description) }}</div>
              </div>
              <a-divider />
              <div class="detail-section">
                <h3>输入描述</h3>
                <div class="detail-text">{{ formatText(problem?.descriptionInput) }}</div>
              </div>
              <a-divider />
              <div class="detail-section">
                <h3>输出描述</h3>
                <div class="detail-text">{{ formatText(problem?.descriptionOutput) }}</div>
              </div>
              <a-divider />
              <div class="detail-section">
                <h3>样例</h3>
                <div class="detail-samples">
                  <div class="detail-sample">
                    <div class="detail-sample__title">样例输入</div>
                    <pre class="detail-code">{{ formatCode(problem?.sampleInput) }}</pre>
                  </div>
                  <div class="detail-sample">
                    <div class="detail-sample__title">样例输出</div>
                    <pre class="detail-code">{{ formatCode(problem?.sampleOutput) }}</pre>
                  </div>
                </div>
              </div>
              <a-divider />
              <div class="detail-section">
                <h3>提示</h3>
                <div class="detail-text">{{ formatText(problem?.hint) }}</div>
              </div>
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="8">
            <a-card class="detail-card" title="题目信息">
              <a-descriptions size="small" :column="1" bordered class="detail-info">
                <a-descriptions-item label="时间限制">
                  <a-tag class="detail-pill detail-pill--time" color="geekblue">
                    {{ formatLimit(problem?.timeLimitMs, 'ms') }}
                  </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="内存限制">
                  <a-tag class="detail-pill detail-pill--memory" color="green">
                    {{ formatLimit(problem?.memoryLimitKb, 'KB') }}
                  </a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="通过/提交">
                  <a-tag class="detail-pill" color="cyan">{{ passRateDetail }}</a-tag>
                </a-descriptions-item>
                <a-descriptions-item label="来源">
                  <a-tag class="detail-pill" color="purple">{{ problem?.source || '-' }}</a-tag>
                </a-descriptions-item>
              </a-descriptions>
              <div class="detail-progress">
                <div class="detail-progress__label">通过率</div>
                <a-progress :percent="passRatePercent" :stroke-color="passRateColor" />
              </div>
              <a-button block type="primary" :disabled="!problem" @click="goSubmit">立即提交</a-button>
            </a-card>
            <a-card class="detail-card detail-card--tips" title="温馨提示">
              <ul class="detail-tips">
                <li>提交代码后系统会自动判题，请耐心等待。</li>
                <li>注意时间与内存限制，超时或超限将判为失败。</li>
                <li>可在个人中心查看你的提交通过率变化。</li>
              </ul>
            </a-card>
          </a-col>
        </a-row>
      </a-spin>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { problemService } from '@/services/modules/problem';
import type { Problem, ProblemDifficulty } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const problem = ref<Problem | null>(null);
const loading = ref(false);
const loadError = ref('');

const problemId = computed(() => route.params.id as string);
const homeworkId = computed(() => (route.query.homeworkId as string) || '');

const loadProblem = async (id: string) => {
  loading.value = true;
  loadError.value = '';
  try {
    problem.value = await problemService.fetchDetail(id);
  } catch (error) {
    const msg = extractErrorMessage(error, '获取题目详情失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const goSubmit = () => {
  if (!problem.value) return;
  const query = homeworkId.value ? { homeworkId: homeworkId.value } : undefined;
  router.push({ path: `/problems/${problem.value.id}/submit`, query });
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

const passRatePercent = computed(() => {
  if (!problem.value) return 0;
  const submit = problem.value.submitCount ?? 0;
  if (!submit) return 0;
  return Math.round(((problem.value.acCount ?? 0) / submit) * 100);
});

const passRateText = computed(() => (problem.value ? `${passRatePercent.value}%` : '-'));
const passRateDetail = computed(() => {
  if (!problem.value) return '-';
  return `${problem.value.acCount ?? 0} / ${problem.value.submitCount ?? 0}`;
});
const passRateColor = computed(() => {
  if (passRatePercent.value >= 75) return '#22c55e';
  if (passRatePercent.value >= 45) return '#f59e0b';
  return '#ef4444';
});

const isDailyChallenge = (value?: string | number | boolean | null) =>
  value === 1 || value === '1' || value === true;

const formatText = (val?: string | null) => (val && val.trim() ? val : '暂无');
const formatCode = (val?: string | null) => (val && val.trim() ? val : '-');
const formatLimit = (value?: number, unit?: string) => (value ? `${value}${unit ?? ''}` : '-');

watch(
  () => problemId.value,
  (id) => {
    if (id) loadProblem(id);
  },
  { immediate: true },
);
</script>

<style scoped lang="less">
.problem-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-header__title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.detail-header__title h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.detail-header__meta {
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.detail-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.detail-section h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: var(--text-color);
}

.detail-text {
  white-space: pre-line;
  line-height: 1.8;
  color: var(--text-color);
}

.detail-samples {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.detail-sample__title {
  font-size: 13px;
  color: var(--text-muted, #64748b);
  margin-bottom: 6px;
}

.detail-code {
  background: var(--body-bg);
  border-radius: 10px;
  border: 1px dashed rgba(15, 23, 42, 0.15);
  padding: 12px;
  color: var(--text-color);
  font-size: 13px;
  line-height: 1.6;
  height: 160px;
  overflow: auto;
}

.detail-info :deep(.ant-descriptions-item-label) {
  width: 96px;
  min-width: 96px;
  padding: 6px 8px;
}

.detail-info :deep(.ant-descriptions-item-content) {
  padding: 6px 10px;
}

.detail-pill {
  font-size: 12px;
  margin-right: 0;
  border-radius: 999px;
  padding: 1px 10px;
  line-height: 20px;
}

.detail-progress {
  margin: 16px 0;
}

.detail-progress__label {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
  margin-bottom: 6px;
}

.detail-card--tips {
  margin-top: 16px;
}

.detail-tips {
  margin: 0;
  padding-left: 18px;
  color: var(--text-muted, #94a3b8);
  line-height: 1.8;
}

@media (max-width: 960px) {
  .detail-samples {
    grid-template-columns: 1fr;
  }
}

:global(:root[data-theme='dark']) .detail-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
}

:global(:root[data-theme='dark']) .detail-code {
  background: rgba(15, 23, 42, 0.7);
  border-color: rgba(148, 163, 184, 0.2);
}

</style>
