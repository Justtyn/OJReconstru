<template>
  <PageContainer title="提交记录">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="题目">
          <a-select
            v-model:value="query.problemId"
            show-search
            allow-clear
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            style="width: 240px"
            placeholder="搜索题目"
            @search="searchProblems"
            @dropdownVisibleChange="handleProblemDropdown"
          />
        </a-form-item>
        <a-form-item label="学生">
          <a-select
            v-model:value="query.studentId"
            show-search
            allow-clear
            :filter-option="false"
            :options="studentOptions"
            :loading="studentLoading"
            style="width: 240px"
            placeholder="搜索学生"
            @search="searchStudents"
            @dropdownVisibleChange="handleStudentDropdown"
          />
        </a-form-item>
        <a-form-item label="语言">
          <a-select v-model:value="query.languageId" allow-clear placeholder="全部" style="width: 220px">
            <a-select-option v-for="lang in languageOptions" :key="lang.id" :value="lang.id">
              {{ lang.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="query.statusId" allow-clear placeholder="全部" style="width: 160px">
            <a-select-option :value="3">通过</a-select-option>
            <a-select-option :value="5">错误</a-select-option>
            <a-select-option :value="4">编译错误</a-select-option>
            <a-select-option :value="2">运行中</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
          <a-button type="primary" ghost style="margin-left: 8px" @click="openCreateModal">新增提交</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="mt-16">
      <a-table
        row-key="id"
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="paginationConfig"
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'problemId'">
            {{ problemCache[record.problemId]?.name || record.problemId }}
          </template>
          <template v-else-if="column.key === 'studentId'">
            {{ studentLabel(record) }}
          </template>
          <template v-else-if="column.key === 'overallStatusName'">
            <a-badge :status="statusBadge(record.overallStatusCode)" :text="record.overallStatusName || record.overallStatusCode" />
          </template>
          <template v-else-if="column.key === 'caseCount'">
            {{ record.passedCaseCount ?? 0 }} / {{ record.totalCaseCount ?? 0 }}
          </template>
          <template v-else-if="column.key === 'languageId'">
            {{ languageLabel(record.languageId) }}
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="createVisible"
      title="新增提交"
      width="900px"
      :confirm-loading="createLoading"
      @ok="handleCreate"
    >
      <a-spin :spinning="createLoading">
        <a-form layout="vertical">
          <a-form-item label="提交方式" required>
            <a-radio-group v-model:value="submissionMode" @change="handleModeChange">
              <a-radio-button value="problem">按题目提交</a-radio-button>
              <a-radio-button value="homework">按作业提交</a-radio-button>
            </a-radio-group>
          </a-form-item>

          <a-row :gutter="16" v-if="submissionMode === 'problem'">
            <a-col :span="12">
              <a-form-item label="题目" required>
                <a-select
                  v-model:value="createForm.problemId"
                  show-search
                  allow-clear
                  :filter-option="false"
                  :options="problemOptions"
                  :loading="problemLoading"
                  placeholder="搜索题目"
                  @search="searchProblems"
                  @dropdownVisibleChange="handleProblemDropdown"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <template v-else>
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="班级" required>
                  <a-select
                    v-model:value="createForm.classId"
                    show-search
                    allow-clear
                    :filter-option="false"
                    :options="classOptions"
                    :loading="classLoading"
                    placeholder="搜索班级"
                    @change="handleClassChange"
                    @search="searchClasses"
                    @dropdownVisibleChange="handleClassDropdown"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="作业" required>
                  <a-select
                    v-model:value="createForm.homeworkId"
                    show-search
                    allow-clear
                    :filter-option="false"
                    :options="homeworkOptions"
                    :loading="homeworkLoading"
                    :disabled="!createForm.classId"
                    placeholder="请选择作业"
                    @search="searchHomeworks"
                    @change="handleHomeworkChange"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="题目" required>
                  <a-select
                    v-model:value="createForm.problemId"
                    show-search
                    allow-clear
                    :filter-option="false"
                    :options="homeworkProblemOptions"
                    :loading="homeworkProblemLoading"
                    :disabled="!createForm.homeworkId"
                    placeholder="请选择作业下的题目"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </template>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="学生" required>
                <a-select
                  v-model:value="createForm.studentId"
                  show-search
                  allow-clear
                  :filter-option="false"
                  :options="studentOptions"
                  :loading="studentLoading"
                  placeholder="搜索学生"
                  @search="searchStudents"
                  @dropdownVisibleChange="handleStudentDropdown"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="语言" required>
                <a-select v-model:value="createForm.languageId" allow-clear placeholder="选择语言">
                  <a-select-option v-for="lang in languageOptions" :key="lang.id" :value="lang.id">
                    {{ lang.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="代码" required>
            <CodeEditor
              v-model="createForm.sourceCode"
              :language="languageLabel(createForm.languageId)"
              placeholder="在此编写或粘贴代码，支持行号与基础高亮"
              :max-height="400"
              :resizable="false"
            />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import type { TableColumnType } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import CodeEditor from '@/components/common/CodeEditor.vue';
import { submissionService } from '@/services/modules/submission';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import { classesService } from '@/services/modules/classes';
import { homeworkService } from '@/services/modules/homework';
import type { Submission, SubmissionQuery, Problem, Student, Homework, HomeworkProblem } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

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

const query = reactive<SubmissionQuery>({ page: 1, size: 10 });
const list = ref<Submission[]>([]);
const total = ref(0);
const loading = ref(false);
const createVisible = ref(false);
const createLoading = ref(false);
const submissionMode = ref<'problem' | 'homework'>('problem');

const createForm = reactive<{
  problemId?: string;
  classId?: string;
  studentId?: string;
  homeworkId?: string;
  languageId?: number;
  sourceCode: string;
}>({
  problemId: undefined,
  classId: undefined,
  studentId: undefined,
  homeworkId: undefined,
  languageId: undefined,
  sourceCode: '',
});

const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const problemCache = reactive<Record<string, Problem>>({});

const studentOptions = ref<{ label: string; value: string }[]>([]);
const studentLoading = ref(false);
const studentCache = reactive<Record<string, Student>>({});

const classOptions = ref<{ label: string; value: string }[]>([]);
const classLoading = ref(false);
const homeworkOptions = ref<{ label: string; value: string }[]>([]);
const homeworkLoading = ref(false);
const homeworkProblemOptions = ref<{ label: string; value: string }[]>([]);
const homeworkProblemLoading = ref(false);

const columns: TableColumnType<Submission>[] = [
  { title: '提交ID', dataIndex: 'id', key: 'id', width: 200 },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 220 },
  { title: '学生', dataIndex: 'studentId', key: 'studentId', width: 200 },
  { title: '语言', dataIndex: 'languageId', key: 'languageId', width: 160 },
  { title: '状态', dataIndex: 'overallStatusName', key: 'overallStatusName', width: 140 },
  { title: '通过/总数', dataIndex: 'caseCount', key: 'caseCount', width: 120 },
  { title: '得分', dataIndex: 'score', key: 'score', width: 80 },
  { title: '时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  { title: '操作', key: 'actions', width: 120 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await submissionService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
    preloadCaches(data.records);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取提交记录失败'));
  } finally {
    loading.value = false;
  }
};

const preloadCaches = (records: Submission[]) => {
  const pIds = Array.from(new Set(records.map((r) => r.problemId).filter((id) => id && !problemCache[id])));
  const uIds = Array.from(
    new Set(records.map((r) => r.studentId || (r as any).userId).filter((id) => id && !studentCache[id])),
  );
  if (pIds.length) {
    Promise.all(
      pIds.map(async (id) => {
        try {
          const detail = await problemService.fetchDetail(id);
          problemCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
  if (uIds.length) {
    Promise.all(
      uIds.map(async (id) => {
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

const openCreateModal = () => {
  submissionMode.value = 'problem';
  resetCreateForm();
  createVisible.value = true;
};

const resetCreateForm = () => {
  createForm.problemId = undefined;
  createForm.classId = undefined;
  createForm.studentId = undefined;
  createForm.homeworkId = undefined;
  createForm.languageId = languageOptions[0]?.id;
  createForm.sourceCode = '';
  homeworkOptions.value = [];
  homeworkProblemOptions.value = [];
};

const handleModeChange = () => {
  createForm.problemId = undefined;
  createForm.classId = undefined;
  createForm.homeworkId = undefined;
  homeworkOptions.value = [];
  homeworkProblemOptions.value = [];
};

const handleCreate = async () => {
  if (submissionMode.value === 'problem') {
    if (!createForm.problemId) {
      message.error('请选择题目');
      return;
    }
  } else {
    if (!createForm.classId) {
      message.error('请选择班级');
      return;
    }
    if (!createForm.homeworkId) {
      message.error('请选择作业');
      return;
    }
    if (!createForm.problemId) {
      message.error('请选择作业下的题目');
      return;
    }
  }
  if (!createForm.studentId) {
    message.error('请选择学生');
    return;
  }
  if (!createForm.languageId) {
    message.error('请选择语言');
    return;
  }
  if (!createForm.sourceCode?.trim()) {
    message.error('请输入代码');
    return;
  }
  createLoading.value = true;
  try {
    const payloadHomeworkId = submissionMode.value === 'homework' ? createForm.homeworkId : undefined;
    await submissionService.create({
      problemId: createForm.problemId,
      homeworkId: payloadHomeworkId,
      studentId: createForm.studentId,
      languageId: createForm.languageId,
      sourceCode: createForm.sourceCode,
    });
    message.success('提交成功，判题进行中');
    createVisible.value = false;
    resetCreateForm();
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '提交失败，请稍后重试'));
  } finally {
    createLoading.value = false;
  }
};

const resetQuery = () => {
  query.problemId = undefined;
  query.studentId = undefined;
  query.languageId = undefined;
  query.statusId = undefined;
  handleSearch();
};

const viewDetail = (record: Submission) => {
  router.push(`/admin/submissions/${record.id}`);
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword });
    problemOptions.value = data.records.map((p: Problem) => ({
      label: `${p.name}（ID: ${p.id}）${p.isActive ? '' : '【已禁用】'}`,
      value: p.id,
      disabled: p.isActive === false,
    }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索题目失败'));
  } finally {
    problemLoading.value = false;
  }
};

const searchStudents = async (keyword: string) => {
  studentLoading.value = true;
  try {
    const data = await studentService.fetchList({ page: 1, size: 50, username: keyword, email: keyword });
    studentOptions.value = data.records.map((s: Student) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索学生失败'));
  } finally {
    studentLoading.value = false;
  }
};

const searchHomeworks = async (keyword: string) => {
  if (!createForm.classId) return;
  homeworkLoading.value = true;
  try {
    const data = await homeworkService.fetchList({ page: 1, size: 50, classId: createForm.classId as any, keyword });
    homeworkOptions.value = data.records.map((h: Homework) => ({ label: `${h.title}（ID: ${h.id}）`, value: h.id }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索作业失败'));
  } finally {
    homeworkLoading.value = false;
  }
};

const loadHomeworkProblems = async (homeworkId?: string) => {
  if (!homeworkId) {
    homeworkProblemOptions.value = [];
    return;
  }
  homeworkProblemLoading.value = true;
  try {
    const data = await homeworkService.fetchProblems(homeworkId);
    homeworkProblemOptions.value = data.map((p: HomeworkProblem) => ({
      label: `${p.name || p.id}${p.isActive ? '' : '【已禁用】'}`,
      value: p.id as string,
      disabled: p.isActive === false,
    }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取作业题目失败'));
  } finally {
    homeworkProblemLoading.value = false;
  }
};

const searchClasses = async (keyword: string) => {
  classLoading.value = true;
  try {
    const data = await classesService.fetchList({ page: 1, size: 50, keyword });
    classOptions.value = data.records.map((c) => ({ label: `${c.name}（ID: ${c.id}）`, value: c.id }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索班级失败'));
  } finally {
    classLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const handleStudentDropdown = (open: boolean) => {
  if (open) searchStudents('');
};

const handleClassDropdown = (open: boolean) => {
  if (open) searchClasses('');
};

const handleClassChange = () => {
  createForm.homeworkId = undefined;
  createForm.problemId = undefined;
  homeworkOptions.value = [];
  homeworkProblemOptions.value = [];
  if (createForm.classId) {
    searchHomeworks('');
  }
};

const handleHomeworkChange = (value: string) => {
  createForm.problemId = undefined;
  loadHomeworkProblems(value);
};

const languageLabel = (id?: number) => languageOptions.find((l) => l.id === id)?.name || id || '-';

const studentLabel = (record: Submission) => {
  const id = record.studentId || (record as any).userId;
  if (!id) return '-';
  const cached = studentCache[id];
  if (cached) return cached.name ? `${cached.username}（${cached.name}）` : cached.username;
  return record.studentUsername || (record as any).username || id;
};

const statusBadge = (code?: string) => {
  if (code === 'ACCEPTED') return 'success';
  if (code === 'WRONG') return 'error';
  return 'processing';
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, size?: number) => {
    query.page = page;
    query.size = size ?? query.size;
    loadData();
  },
}));

resetCreateForm();
loadData();
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}

.actions-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
