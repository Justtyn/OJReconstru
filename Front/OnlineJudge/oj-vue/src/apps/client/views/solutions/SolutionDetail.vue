<template>
  <PageContainer :title="solution?.title || '题解详情'" :show-back="true">
    <div class="solution-detail">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="16">
          <a-card class="detail-card" :loading="loading">
            <div class="detail-meta">
              <a-space>
                <a-tag v-if="solution?.language" color="blue">{{ solution?.language }}</a-tag>
                <a-tag v-if="solution?.isActive === false" color="red">未启用</a-tag>
              </a-space>
              <div class="detail-meta__info">
                <span>题目：{{ problemName }}</span>
                <span>作者：{{ authorName }}</span>
                <span>发布时间：{{ formatTime(solution?.createTime) }}</span>
              </div>
            </div>
            <div class="markdown-body" v-html="renderMarkdown(solution?.content || '')"></div>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="8">
          <a-card class="detail-card" title="题解信息" :loading="loading">
            <a-descriptions size="small" :column="1" bordered>
              <a-descriptions-item label="题目">{{ problemName }}</a-descriptions-item>
              <a-descriptions-item label="作者">{{ authorName }}</a-descriptions-item>
              <a-descriptions-item label="语言">{{ solution?.language || '-' }}</a-descriptions-item>
              <a-descriptions-item label="更新时间">{{ formatTime(solution?.updatedAt) }}</a-descriptions-item>
            </a-descriptions>
            <a-button block type="primary" style="margin-top: 16px" @click="goProblem">查看题目</a-button>
          </a-card>
          <a-card class="detail-card mt-16" title="阅读提示">
            <ul class="detail-tips">
              <li>题解内容支持 Markdown 展示。</li>
              <li>如需提交代码，请返回题目详情页。</li>
              <li>欢迎分享你的理解，帮助更多同学。</li>
            </ul>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { solutionService } from '@/services/modules/solution';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Solution, Problem, Student } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const solution = ref<Solution | null>(null);
const loading = ref(false);
const loadError = ref('');
const problem = ref<Problem | null>(null);
const author = ref<Student | null>(null);

const solutionId = computed(() => route.params.id as string);
const problemName = computed(() => problem.value?.name || solution.value?.problemId || '-');
const authorName = computed(() => {
  if (author.value) return author.value.name ? `${author.value.username}（${author.value.name}）` : author.value.username;
  return solution.value?.userId || '-';
});

const loadDetail = async () => {
  if (!solutionId.value) return;
  loading.value = true;
  loadError.value = '';
  try {
    const data = await solutionService.fetchDetail(solutionId.value);
    solution.value = data;
    if (data.problemId) {
      try {
        problem.value = await problemService.fetchDetail(data.problemId);
      } catch {
        problem.value = null;
      }
    }
    if (data.userId) {
      try {
        author.value = await studentService.fetchDetail(data.userId);
      } catch {
        author.value = null;
      }
    }
  } catch (error) {
    const msg = extractErrorMessage(error, '获取题解详情失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const goProblem = () => {
  if (!solution.value?.problemId) return;
  router.push(`/problems/${solution.value.problemId}`);
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

onMounted(() => {
  loadDetail();
});
</script>

<style scoped lang="less">
.solution-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-meta__info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.detail-tips {
  margin: 0;
  padding-left: 18px;
  color: var(--text-muted, #94a3b8);
  line-height: 1.8;
}

.mt-16 {
  margin-top: 16px;
}

.markdown-body :deep(p) {
  line-height: 1.8;
}

:global(:root[data-theme='dark']) .detail-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
}
</style>
