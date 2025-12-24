<template>
  <PageContainer :title="homework?.title || '作业详情'" :show-back="true" @back="goBack">
    <div class="homework-detail">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-card class="detail-card" :loading="loading">
        <div class="detail-header">
          <div>
            <div class="detail-title">{{ homework?.title || '-' }}</div>
            <div class="detail-meta">
              <span>开始：{{ formatTime(homework?.startTime) }}</span>
              <span>结束：{{ formatTime(homework?.endTime) }}</span>
              <span>状态：{{ statusLabel }}</span>
            </div>
          </div>
          <a-tag :color="statusColor">{{ statusLabel }}</a-tag>
        </div>
        <div class="detail-desc">{{ homework?.description || '暂无作业说明' }}</div>
      </a-card>

      <a-card class="detail-card mt-16" title="作业题目">
        <div class="problem-list">
          <a-card v-for="item in problems" :key="problemKey(item)" class="problem-card" :class="{ disabled: !canEnter }" hoverable>
            <div class="problem-card__header">
              <div class="problem-card__title">{{ item.name || `题目 ${problemKey(item)}` }}</div>
              <a-tag :color="difficultyColor(item.difficulty)">{{ difficultyLabel(item.difficulty) }}</a-tag>
            </div>
            <div class="problem-card__meta">
              <span>AC/提交：{{ item.acCount ?? 0 }} / {{ item.submitCount ?? 0 }}</span>
              <span>题目ID：{{ item.problemId || item.id }}</span>
            </div>
            <div class="problem-card__footer">
              <span class="problem-card__hint">点击进入题目详情</span>
              <a-button type="link" :disabled="!canEnter" @click="goProblem(item)">查看题目</a-button>
            </div>
          </a-card>
        </div>
        <a-empty v-if="!loading && !problems.length" description="暂无题目" class="homework-empty" />
      </a-card>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { homeworkService } from '@/services/modules/homework';
import type { Homework, HomeworkProblem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const homework = ref<Homework | null>(null);
const problems = ref<HomeworkProblem[]>([]);
const loading = ref(false);
const loadError = ref('');

const homeworkId = computed(() => route.params.id as string);

const loadDetail = async () => {
  if (!homeworkId.value) return;
  loading.value = true;
  loadError.value = '';
  try {
    homework.value = await homeworkService.fetchDetail(homeworkId.value);
    problems.value = await homeworkService.fetchProblems(homeworkId.value);
  } catch (error) {
    const msg = extractErrorMessage(error, '获取作业详情失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.back();
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

const statusLabel = computed(() => {
  if (!homework.value) return '-';
  if (homework.value.isActive === false) return '未启用';
  const now = Date.now();
  const start = homework.value.startTime ? new Date(homework.value.startTime).getTime() : undefined;
  const end = homework.value.endTime ? new Date(homework.value.endTime).getTime() : undefined;
  if (start && now < start) return '未开始';
  if (end && now > end) return '已结束';
  return '进行中';
});

const statusColor = computed(() => {
  if (statusLabel.value === '未开始') return 'default';
  if (statusLabel.value === '已结束') return 'red';
  if (statusLabel.value === '未启用') return 'orange';
  return 'green';
});

const canEnter = computed(() => statusLabel.value === '进行中');

const problemKey = (item: HomeworkProblem) => item.problemId || item.id;

const goProblem = (item: HomeworkProblem) => {
  if (!canEnter.value) return;
  const id = item.problemId || item.id;
  if (!id) return;
  router.push({ path: `/problems/${id}`, query: { homeworkId: homeworkId.value } });
};

const difficultyLabel = (val?: string) => {
  if (val === 'easy') return '简单';
  if (val === 'medium') return '中等';
  if (val === 'hard') return '困难';
  return val || '-';
};

const difficultyColor = (val?: string) => {
  if (val === 'easy') return 'green';
  if (val === 'medium') return 'orange';
  if (val === 'hard') return 'red';
  return 'blue';
};

onMounted(() => {
  loadDetail();
});
</script>

<style scoped lang="less">
.homework-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-top: 6px;
}

.detail-desc {
  margin-top: 12px;
  color: var(--text-color);
  line-height: 1.7;
}

.problem-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.problem-card {
  border-radius: 14px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.08);
}

.problem-card.disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.problem-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.problem-card__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.problem-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-bottom: 8px;
}

.problem-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.problem-card__hint {
  font-size: 12px;
}

.mt-16 {
  margin-top: 16px;
}

.homework-empty {
  margin-top: 24px;
}

:global(:root[data-theme='dark']) .detail-card,
:global(:root[data-theme='dark']) .problem-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
  background: rgba(15, 23, 42, 0.7);
}
</style>
