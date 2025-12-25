<template>
  <PageContainer :title="`提交详情 #${submission?.id || ''}`" :show-back="true">
    <div class="detail-page">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="16">
          <a-card class="detail-card" :loading="loading">
            <a-descriptions bordered :column="1" size="small">
              <a-descriptions-item label="提交ID">{{ submission?.id || '-' }}</a-descriptions-item>
              <a-descriptions-item label="题目">{{ problemName }}</a-descriptions-item>
              <a-descriptions-item label="语言">{{ languageLabel(submission?.languageId) }}</a-descriptions-item>
              <a-descriptions-item label="状态">
                <a-badge
                  :status="statusBadge(submission?.overallStatusCode)"
                  :text="submission?.overallStatusName || submission?.overallStatusCode || '-'"
                />
              </a-descriptions-item>
              <a-descriptions-item label="通过/总数">
                {{ submission?.passedCaseCount ?? 0 }} / {{ submission?.totalCaseCount ?? 0 }}
              </a-descriptions-item>
              <a-descriptions-item label="得分">{{ submission?.score ?? 0 }}</a-descriptions-item>
              <a-descriptions-item label="创建时间">{{ formatTime(submission?.createdAt) }}</a-descriptions-item>
              <a-descriptions-item label="更新时间">{{ formatTime(submission?.updatedAt) }}</a-descriptions-item>
            </a-descriptions>
          </a-card>

          <a-card class="detail-card mt-16" title="源代码" v-if="submission?.sourceCode">
            <template #extra>
              <a-button type="link" size="small" @click="copyCode">复制代码</a-button>
            </template>
            <CodeEditor
              :model-value="submission?.sourceCode || ''"
              :language="languageLabel(submission?.languageId)"
              :readonly="true"
              :resizable="false"
              :auto-height="true"
              :max-height="560"
            />
          </a-card>

        </a-col>

        <a-col :xs="24" :lg="8">
          <a-card class="detail-card" title="判题概览" :loading="loading">
            <div class="summary-stat">
              <div class="summary-stat__label">提交状态</div>
              <div class="summary-stat__value">{{ submission?.overallStatusName || submission?.overallStatusCode || '-' }}</div>
            </div>
            <div class="summary-stat">
              <div class="summary-stat__label">通过率</div>
              <a-progress :percent="passRatePercent" :stroke-color="passRateColor" />
            </div>
            <div class="summary-grid">
              <div class="summary-item">
                <div class="summary-item__value">{{ submission?.passedCaseCount ?? 0 }}</div>
                <div class="summary-item__label">通过数</div>
              </div>
              <div class="summary-item">
                <div class="summary-item__value">{{ submission?.totalCaseCount ?? 0 }}</div>
                <div class="summary-item__label">总用例</div>
              </div>
              <div class="summary-item">
                <div class="summary-item__value">{{ submission?.score ?? 0 }}</div>
                <div class="summary-item__label">得分</div>
              </div>
            </div>
            <a-space direction="vertical" style="width: 100%">
              <a-button block type="primary" @click="goProblem">查看题目</a-button>
              <a-button block @click="goResubmit">重新提交</a-button>
            </a-space>
          </a-card>
          <a-card class="detail-card mt-16" title="提示">
            <ul class="summary-tips">
              <li>可在题目详情页重新提交代码。</li>
              <li>若判题耗时较长，请稍后刷新。</li>
              <li>单个用例详情支持展开查看输出。</li>
            </ul>
          </a-card>
        </a-col>
      </a-row>

      <a-card class="detail-card" title="测试点结果" v-if="submission?.testcaseResults?.length">
        <a-table
          row-key="testcaseId"
          :columns="caseColumns"
          :data-source="submission.testcaseResults"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record, text, index }">
            <template v-if="column.key === 'caseIndex'">
              {{ index + 1 }}
            </template>
            <template v-else-if="column.key === 'statusDescription'">
              <a-badge :status="statusBadgeFromId(record.statusId)" :text="record.statusDescription || record.statusId" />
            </template>
            <template v-else-if="column.key === 'stdout' || column.key === 'stderr' || column.key === 'message' || column.key === 'compileOutput'">
              <a-typography-paragraph style="margin: 0" :ellipsis="{ rows: 2, expandable: true }">
                <pre class="code-block inline-code"><code>{{ text || '-' }}</code></pre>
              </a-typography-paragraph>
            </template>
            <template v-else-if="column.key === 'timeUsed'">
              {{ record.timeUsed ?? '-' }}
            </template>
            <template v-else-if="column.key === 'memoryUsed'">
              {{ record.memoryUsed ?? '-' }}
            </template>
          </template>
        </a-table>
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
import CodeEditor from '@/components/common/CodeEditor.vue';
import { submissionService } from '@/services/modules/submission';
import { problemService } from '@/services/modules/problem';
import type { SubmissionDetail, Problem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const router = useRouter();
const submission = ref<SubmissionDetail>();
const problemName = ref('-');
const loading = ref(false);
const loadError = ref('');

const languageOptions = [
  { id: 11, name: 'Bosque (latest)' },
  { id: 3, name: 'C3 (latest)' },
  { id: 1, name: 'C (Clang 10.0.1)' },
  { id: 2, name: 'C++ (Clang 10.0.1)' },
  { id: 13, name: 'C (Clang 9.0.1)' },
  { id: 14, name: 'C++ (Clang 9.0.1)' },
  { id: 22, name: 'C# (Mono 6.12.0.122)' },
  { id: 21, name: 'C# (.NET Core SDK 3.1.406)' },
  { id: 15, name: 'C++ Test (Clang 10.0.1, Google Test 1.8.1)' },
  { id: 12, name: 'C++ Test (GCC 8.4.0, Google Test 1.8.1)' },
  { id: 23, name: 'C# Test (.NET Core SDK 3.1.406, NUnit 3.12.0)' },
  { id: 24, name: 'F# (.NET Core SDK 3.1.406)' },
  { id: 4, name: 'Java (OpenJDK 14.0.1)' },
  { id: 5, name: 'Java Test (OpenJDK 14.0.1, JUnit Platform Console Standalone 1.6.2)' },
  { id: 6, name: 'MPI (OpenRTE 3.1.3) with C (GCC 8.4.0)' },
  { id: 7, name: 'MPI (OpenRTE 3.1.3) with C++ (GCC 8.4.0)' },
  { id: 8, name: 'MPI (OpenRTE 3.1.3) with Python (3.7.7)' },
  { id: 89, name: 'Multi-file program' },
  { id: 9, name: 'Nim (stable)' },
  { id: 10, name: 'Python for ML (3.7.7)' },
  { id: 20, name: 'Visual Basic.Net (vbnc 0.0.0.5943)' },
];

const caseColumns = [
  { title: '用例序号', key: 'caseIndex', width: 100 },
  { title: '状态', dataIndex: 'statusDescription', key: 'statusDescription', width: 120 },
  { title: 'stdout', dataIndex: 'stdout', key: 'stdout' },
  { title: 'stderr', dataIndex: 'stderr', key: 'stderr' },
  { title: '编译输出', dataIndex: 'compileOutput', key: 'compileOutput' },
  { title: '消息', dataIndex: 'message', key: 'message' },
  { title: '时间(ms)', dataIndex: 'timeUsed', key: 'timeUsed', width: 100 },
  { title: '内存(kb)', dataIndex: 'memoryUsed', key: 'memoryUsed', width: 100 },
];

const passRatePercent = computed(() => {
  if (!submission.value) return 0;
  const total = submission.value.totalCaseCount ?? 0;
  if (!total) return 0;
  return Math.round(((submission.value.passedCaseCount ?? 0) / total) * 100);
});

const passRateColor = computed(() => {
  if (passRatePercent.value >= 75) return '#22c55e';
  if (passRatePercent.value >= 45) return '#f59e0b';
  return '#ef4444';
});

const loadDetail = async () => {
  const id = route.params.id as string;
  if (!id) return;
  loading.value = true;
  loadError.value = '';
  try {
    const data = await submissionService.fetchDetail(id);
    submission.value = data;
    if (data.problemId) {
      try {
        const p: Problem = await problemService.fetchDetail(data.problemId);
        problemName.value = `${p.name}（ID: ${p.id}）`;
      } catch {
        problemName.value = data.problemId;
      }
    }
  } catch (error) {
    const msg = extractErrorMessage(error, '获取提交详情失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const goProblem = () => {
  if (!submission.value?.problemId) return;
  router.push(`/problems/${submission.value.problemId}`);
};

const goResubmit = () => {
  if (!submission.value?.problemId) return;
  const query = submission.value.homeworkId ? { homeworkId: submission.value.homeworkId } : undefined;
  router.push({ path: `/problems/${submission.value.problemId}/submit`, query });
};

const languageLabel = (id?: number | string) =>
  languageOptions.find((item) => item.id === Number(id))?.name || (id ? String(id) : '-');

const statusBadge = (code?: string) => {
  const normalized = (code || '').toUpperCase();
  if (!normalized) return 'default';
  if (normalized === 'ACCEPTED') return 'success';
  if (normalized === 'IN_QUEUE' || normalized === 'PROCESSING') return 'processing';
  return 'error';
};

const statusBadgeFromId = (id?: number) => {
  if (id === 3) return 'success';
  if (id === 4) return 'warning';
  if (id === 5) return 'error';
  return 'processing';
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm:ss');
};

const copyCode = async () => {
  const code = submission.value?.sourceCode;
  if (!code) {
    message.warning('暂无可复制的代码');
    return;
  }
  try {
    await navigator.clipboard.writeText(code);
    message.success('代码已复制');
  } catch {
    message.error('复制失败，请手动复制');
  }
};

onMounted(() => {
  loadDetail();
});
</script>

<style scoped lang="less">
.detail-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.mt-16 {
  margin-top: 16px;
}

.code-block {
  background: var(--card-bg, #0f172a0a);
  border: 1px solid var(--border-color, rgba(0, 0, 0, 0.08));
  padding: 12px;
  border-radius: 8px;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
}

.inline-code {
  margin: 0;
}

.summary-stat {
  margin-bottom: 16px;
}

.summary-stat__label {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
  margin-bottom: 6px;
}

.summary-stat__value {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 16px;
}

.summary-item {
  padding: 10px;
  border-radius: 10px;
  background: var(--body-bg);
  border: 1px solid rgba(15, 23, 42, 0.06);
  text-align: center;
}

.summary-item__value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.summary-item__label {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
  margin-top: 4px;
}

.summary-tips {
  margin: 0;
  padding-left: 18px;
  color: var(--text-muted, #94a3b8);
  line-height: 1.8;
}

:global(:root[data-theme='dark']) .detail-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
}

:global(:root[data-theme='dark']) .summary-item {
  background: rgba(15, 23, 42, 0.7);
  border-color: rgba(148, 163, 184, 0.2);
}
</style>
