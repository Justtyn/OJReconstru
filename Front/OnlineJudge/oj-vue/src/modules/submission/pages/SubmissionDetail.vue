<template>
  <PageContainer :title="`提交详情 #${submission?.id || ''}`" @back="goBack" :show-back="true">
    <a-card v-if="submission">
      <a-descriptions bordered :column="1" size="small">
        <a-descriptions-item label="提交ID">{{ submission.id }}</a-descriptions-item>
        <a-descriptions-item label="题目">{{ problemName }}</a-descriptions-item>
        <a-descriptions-item label="语言">{{ languageLabel(submission.languageId) }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-badge :status="statusBadge(submission.overallStatusCode)" :text="submission.overallStatusName || submission.overallStatusCode" />
        </a-descriptions-item>
        <a-descriptions-item label="通过/总数">
          {{ submission.passedCaseCount ?? 0 }} / {{ submission.totalCaseCount ?? 0 }}
        </a-descriptions-item>
        <a-descriptions-item label="得分">{{ submission.score ?? 0 }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ submission.createdAt ? format(new Date(submission.createdAt), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="更新时间">
          {{ submission.updatedAt ? format(new Date(submission.updatedAt), 'yyyy-MM-dd HH:mm:ss') : '-' }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>

    <a-card class="mt-16" title="源代码" v-if="submission?.sourceCode">
      <template #extra>
        <a-button type="link" size="small" @click="copyCode">复制代码</a-button>
      </template>
      <CodeEditor :model-value="submission?.sourceCode || ''" :language="languageLabel(submission?.languageId)" :readonly="true" />
    </a-card>

    <a-card class="mt-16" title="测试点结果" v-if="submission?.testcaseResults?.length">
      <a-table
        row-key="testcaseId"
        :columns="caseColumns"
        :data-source="submission.testcaseResults"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'statusDescription'">
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
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
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
const problemName = ref('');

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
  { title: '用例ID', dataIndex: 'testcaseId', key: 'testcaseId', width: 200 },
  { title: '状态', dataIndex: 'statusDescription', key: 'statusDescription', width: 120 },
  { title: 'stdout', dataIndex: 'stdout', key: 'stdout' },
  { title: 'stderr', dataIndex: 'stderr', key: 'stderr' },
  { title: '编译输出', dataIndex: 'compileOutput', key: 'compileOutput' },
  { title: '消息', dataIndex: 'message', key: 'message' },
  { title: '时间(ms)', dataIndex: 'timeUsed', key: 'timeUsed', width: 100 },
  { title: '内存(kb)', dataIndex: 'memoryUsed', key: 'memoryUsed', width: 100 },
];

const loadDetail = async () => {
  try {
    const id = route.params.id as string;
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
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取提交详情失败'));
  }
};

const goBack = () => {
  router.back();
};

const languageLabel = (id?: number) => languageOptions.find((l) => l.id === id)?.name || id || '-';
const statusBadge = (code?: string) => {
  if (code === 'ACCEPTED') return 'success';
  if (code === 'WRONG') return 'error';
  return 'processing';
};
const statusBadgeFromId = (id?: number) => {
  if (id === 3) return 'success';
  if (id === 5) return 'error';
  if (id === 4) return 'warning';
  return 'processing';
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
</style>
