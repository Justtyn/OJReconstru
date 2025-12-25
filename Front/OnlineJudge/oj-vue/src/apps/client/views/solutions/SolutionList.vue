<template>
  <PageContainer title="题解" subtitle="阅读他人的解题思路与经验分享">
    <div class="solution-page">
      <section class="solution-hero">
        <div class="solution-hero__top">
          <div class="solution-hero__title">题解筛选</div>
          <div class="solution-hero__right">
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
            <a-button v-if="isStudent" type="primary" size="small" @click="openCreate">发布题解</a-button>
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
              <a-space size="small" align="center">
                <a-tag v-if="item.language" color="blue">{{ item.language }}</a-tag>
                <a-tag v-if="item.isActive === false" color="red">未启用</a-tag>
                <a-button type="link" size="small" @click="goDetail(item)">查看详情</a-button>
              </a-space>
            </div>
            <div class="solution-card__meta">
              <span>题目：{{ problemLabel(item.problemId) }}</span>
              <span class="solution-card__author">
                <a-avatar :size="22" :src="authorAvatar(item.userId)">{{ authorInitial(item.userId) }}</a-avatar>
                <span>作者：{{ authorLabel(item.userId) }}</span>
              </span>
              <span>{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="solution-card__excerpt">{{ excerptText(item.content) }}</div>
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

    <a-modal
      v-if="isStudent"
      v-model:open="createVisible"
      title="发布题解"
      :confirm-loading="createSubmitting"
      ok-text="发布"
      cancel-text="取消"
      @ok="handleCreate"
      @cancel="handleCreateCancel"
    >
      <a-form ref="createFormRef" layout="vertical" :model="createForm" :rules="createRules">
        <a-form-item label="题目" name="problemId">
          <a-select
            v-model:value="createForm.problemId"
            show-search
            allow-clear
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            placeholder="搜索并选择题目"
            @search="searchProblems"
            @dropdownVisibleChange="handleProblemDropdown"
          />
        </a-form-item>
        <a-form-item label="标题" name="title">
          <a-input v-model:value="createForm.title" placeholder="请输入题解标题" />
        </a-form-item>
        <a-form-item label="语言" name="language">
          <a-select v-model:value="createForm.language" allow-clear placeholder="选择语言（可选）">
            <a-select-option v-for="item in languageOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="内容" name="content">
          <a-textarea v-model:value="createForm.content" :rows="6" placeholder="请输入题解内容，支持 Markdown" />
        </a-form-item>
      </a-form>
    </a-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { solutionService } from '@/services/modules/solution';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Solution, SolutionQuery, SolutionRequest, Problem, Student } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();
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
const createVisible = ref(false);
const createSubmitting = ref(false);
const createFormRef = ref<FormInstance>();

const languageOptions = [
  { value: 'c', label: 'C' },
  { value: 'cpp', label: 'C++' },
  { value: 'java', label: 'Java' },
  { value: 'python', label: 'Python' },
  { value: 'python3', label: 'Python 3' },
  { value: 'go', label: 'Go' },
  { value: 'javascript', label: 'JavaScript' },
  { value: 'typescript', label: 'TypeScript' },
  { value: 'rust', label: 'Rust' },
  { value: 'kotlin', label: 'Kotlin' },
];

const createForm = reactive<Pick<SolutionRequest, 'problemId' | 'title' | 'content' | 'language'>>({
  problemId: undefined,
  title: '',
  content: '',
  language: '',
});

const createRules: FormProps['rules'] = {
  problemId: [{ required: true, message: '请选择题目' }],
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }],
};

const totalLabel = computed(() => (total.value ? total.value.toString() : '-'));
const isStudent = computed(() => authStore.role === 'student');

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

const openCreate = () => {
  createVisible.value = true;
};

const resetCreateForm = () => {
  createForm.problemId = undefined;
  createForm.title = '';
  createForm.content = '';
  createForm.language = '';
  createFormRef.value?.clearValidate();
};

const handleCreateCancel = () => {
  createVisible.value = false;
  resetCreateForm();
};

const handleCreate = async () => {
  if (!authStore.user?.id) {
    message.error('登录信息缺失，请重新登录');
    return;
  }
  try {
    await createFormRef.value?.validate();
    if (!createForm.problemId) return;
    createSubmitting.value = true;
    const payload: SolutionRequest = {
      problemId: createForm.problemId,
      authorId: authStore.user.id,
      title: createForm.title,
      content: createForm.content,
      language: createForm.language,
      isActive: true,
    };
    await solutionService.create(createForm.problemId, payload);
    message.success('题解已发布');
    createVisible.value = false;
    resetCreateForm();
    loadData();
  } catch (error) {
    message.error(extractErrorMessage(error, '发布题解失败'));
  } finally {
    createSubmitting.value = false;
  }
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
const authorAvatar = (id: string) => studentCache[id]?.avatar || '';
const authorInitial = (id: string) => {
  const student = studentCache[id];
  const label = student?.username || id || 'U';
  return label.charAt(0).toUpperCase();
};

const stripCode = (content?: string) => {
  if (!content) return '';
  return content.replace(/```[\s\S]*?```/g, '').replace(/`[^`]*`/g, '');
};

const excerptText = (content?: string) => {
  if (!content) return '暂无内容';
  const html = renderMarkdown(stripCode(content));
  const plain = html.replace(/<pre[\s\S]*?<\/pre>/g, '').replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim();
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

.solution-hero__right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
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

.solution-card__author {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.solution-card__excerpt {
  color: var(--text-color);
  opacity: 0.85;
  line-height: 1.6;
  min-height: 56px;
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
