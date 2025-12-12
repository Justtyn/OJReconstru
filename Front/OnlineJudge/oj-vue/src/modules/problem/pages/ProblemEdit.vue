<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form layout="vertical" :model="formState" :rules="rules" ref="formRef" class="problem-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="名称" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入题目名称" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="难度" name="difficulty">
              <a-select v-model:value="formState.difficulty" placeholder="请选择难度">
                <a-select-option value="easy">简单</a-select-option>
                <a-select-option value="medium">中等</a-select-option>
                <a-select-option value="hard">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="时间限制 (ms)">
              <a-input-number v-model:value="formState.timeLimitMs" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="内存限制 (KB)">
              <a-input-number v-model:value="formState.memoryLimitKb" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="题目描述" name="description">
              <a-textarea v-model:value="formState.description" :rows="2" placeholder="描述题意与约束" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="输入描述">
              <a-textarea v-model:value="formState.descriptionInput" :rows="2" placeholder="说明输入格式" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="输出描述">
              <a-textarea v-model:value="formState.descriptionOutput" :rows="2" placeholder="说明输出格式" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="提示">
              <a-textarea v-model:value="formState.hint" :rows="2" placeholder="可选的补充提示" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="样例输入">
              <a-textarea v-model:value="formState.sampleInput" :rows="2" placeholder="样例输入" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="样例输出">
              <a-textarea v-model:value="formState.sampleOutput" :rows="2" placeholder="样例输出" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="来源">
              <a-select
                v-model:value="formState.source"
                show-search
                allow-clear
                placeholder="请选择来源"
                option-filter-prop="label"
                :options="sourceOptions"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="日常挑战标记">
              <a-input v-model:value="formState.dailyChallenge" placeholder="例如 0/1，依后端约定" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="状态">
          <a-switch v-model:checked="formState.isActive" checked-children="启用" un-checked-children="禁用" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">保存</a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="mt-16">
      <div class="case-header">
        <h3>测试用例</h3>
        <a-button type="primary" @click="openCreateCase">新增用例</a-button>
      </div>
      <a-table
        row-key="id"
        :columns="caseColumns"
        :data-source="caseList"
        :loading="caseLoading"
        :pagination="casePagination"
      >
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'isSample'">
            <a-tag :color="record.isSample ? 'blue' : 'default'">{{ record.isSample ? '样例' : '评测' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'inputData' || column.key === 'outputData'">
            <a-tooltip :title="text">
              <span class="ellipsis">{{ text }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="openEditCase(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button danger type="link" size="small" @click="confirmRemoveCase(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer
      :width="520"
      :open="caseDrawerVisible"
      :title="caseDrawerTitle"
      destroy-on-close
      @close="caseDrawerVisible = false"
    >
      <ProblemCaseForm ref="caseFormRef" v-model="caseFormState" />
      <div class="drawer-footer">
        <a-space>
          <a-button @click="caseDrawerVisible = false">取消</a-button>
          <a-button type="primary" :loading="caseSubmitting" @click="handleSubmitCase">保存</a-button>
        </a-space>
      </div>
    </a-drawer>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import type { FormInstance, FormProps, TableColumnType } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import ProblemCaseForm from '@/modules/problem/components/ProblemCaseForm.vue';
import { problemCaseService, problemService } from '@/services/modules/problem';
import type { ProblemCase, ProblemCaseUpsertRequest, ProblemUpsertRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);
const formRef = ref<FormInstance>();
const submitting = ref(false);
const BUILTIN_SOURCES = [
  'LeetCode',
  '牛客',
  'Codeforces',
  'AtCoder',
  'CodeChef',
  'TopCoder',
  'HackerRank',
  'HackerEarth',
  '洛谷',
  'HDU',
  'POJ',
  'ZOJ',
  'UVa',
  'Timus',
  'SPOJ',
  'Kattis',
  'CSES',
  'DMOJ',
  'UOJ',
  'HydroOJ',
  'VJudge',
  'Project Euler',
  'Kaggle',
  '杭州电子科技大学',
  '清华大学',
  '北京大学',
  '浙江大学',
  '武汉大学',
  '中山大学',
  '厦门大学',
  '电子科技大学',
  '华中科技大学',
] as const;
const sourceOptions = ref(BUILTIN_SOURCES.map((value) => ({ label: value, value })));

const formState = reactive<ProblemUpsertRequest>({
  name: '',
  difficulty: 'medium',
  description: '',
  descriptionInput: '',
  descriptionOutput: '',
  sampleInput: '',
  sampleOutput: '',
  hint: '',
  dailyChallenge: '',
  timeLimitMs: 1000,
  memoryLimitKb: 131072,
  source: '',
  isActive: true,
});

const rules: FormProps['rules'] = {
  name: [{ required: true, message: '请输入名称' }],
  difficulty: [{ required: true, message: '请选择难度' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑题目' : '新建题目'));

const loadDetail = async () => {
  if (!isEdit.value || !recordId.value) return;
  try {
    const data = await problemService.fetchDetail(recordId.value);
    formState.name = data.name;
    formState.difficulty = data.difficulty ?? 'medium';
    formState.description = data.description ?? '';
    formState.descriptionInput = data.descriptionInput ?? '';
    formState.descriptionOutput = data.descriptionOutput ?? '';
    formState.sampleInput = data.sampleInput ?? '';
    formState.sampleOutput = data.sampleOutput ?? '';
    formState.hint = data.hint ?? '';
    formState.dailyChallenge = data.dailyChallenge ?? '';
    formState.source = data.source ?? '';
    ensureSourceOption(formState.source);
    formState.timeLimitMs = data.timeLimitMs ?? 1000;
    formState.memoryLimitKb = data.memoryLimitKb ?? 131072;
    formState.isActive = data.isActive ?? true;
    loadCases();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取题目详情失败'));
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: ProblemUpsertRequest = { ...formState, source: formState.source || undefined };
    if (isEdit.value) {
      await problemService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      const created = await problemService.create(payload);
      message.success('创建成功');
      router.replace(`/admin/problems/${created.id}/edit`);
      return;
    }
    router.push('/admin/problems');
  } catch (error: any) {
    message.error(extractErrorMessage(error, '提交失败'));
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.back();
};

const ensureSourceOption = (value?: string | null) => {
  if (!value) return;
  if (!sourceOptions.value.find((o) => o.value === value)) {
    sourceOptions.value = [...sourceOptions.value, { label: value, value }];
  }
};

// 用例区
const caseList = ref<ProblemCase[]>([]);
const caseTotal = ref(0);
const caseLoading = ref(false);
const caseQuery = reactive<{ page: number; size: number }>({ page: 1, size: 10 });
const caseDrawerVisible = ref(false);
const caseDrawerTitle = computed(() => (editingCaseId.value ? '编辑用例' : '新增用例'));
const caseFormRef = ref<InstanceType<typeof ProblemCaseForm>>();
const caseFormState = ref<ProblemCaseUpsertRequest>({
  inputData: '',
  outputData: '',
  isSample: false,
});
const caseSubmitting = ref(false);
const editingCaseId = ref<string | null>(null);

const caseColumns: TableColumnType<ProblemCase>[] = [
  { title: '类型', dataIndex: 'isSample', key: 'isSample', width: 100 },
  { title: '输入', dataIndex: 'inputData', key: 'inputData' },
  { title: '输出', dataIndex: 'outputData', key: 'outputData' },
  { title: '操作', key: 'actions', width: 150 },
];

const casePagination = computed(() => (caseList.value.length > 10 ? {
  current: caseQuery.page,
  pageSize: caseQuery.size,
  total: caseTotal.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, pageSize?: number) => {
    caseQuery.page = page;
    caseQuery.size = pageSize ?? caseQuery.size;
    loadCases();
  },
} : false));

const loadCases = async () => {
  if (!recordId.value) return;
  caseLoading.value = true;
  try {
    const data = await problemCaseService.fetchList(recordId.value, caseQuery);
    caseList.value = Array.isArray(data) ? data : [];
    caseTotal.value = caseList.value.length;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取用例失败'));
  } finally {
    caseLoading.value = false;
  }
};

const openCreateCase = () => {
  if (!recordId.value) {
    message.warning('请先保存题目后再添加用例');
    return;
  }
  editingCaseId.value = null;
  caseFormState.value = {
    inputData: '',
    outputData: '',
    isSample: false,
  };
  caseDrawerVisible.value = true;
};

const openEditCase = async (record: ProblemCase) => {
  editingCaseId.value = record.id;
  caseDrawerVisible.value = true;
  try {
    const data = await problemCaseService.fetchDetail(record.id);
    caseFormState.value = {
      inputData: (data as any).inputData ?? (data as any).input ?? '',
      outputData: (data as any).outputData ?? (data as any).output ?? '',
      isSample: data.isSample ?? false,
    };
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取用例详情失败'));
  }
};

const handleSubmitCase = async () => {
  if (!recordId.value) {
    message.warning('请先保存题目后再添加用例');
    return;
  }
  try {
    await caseFormRef.value?.validate();
    caseSubmitting.value = true;
    if (editingCaseId.value) {
      await problemCaseService.update(editingCaseId.value, caseFormState.value);
      message.success('更新用例成功');
    } else {
      await problemCaseService.create(recordId.value, caseFormState.value);
      message.success('新增用例成功');
    }
    caseDrawerVisible.value = false;
    loadCases();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '保存用例失败'));
  } finally {
    caseSubmitting.value = false;
  }
};

const removeCase = async (record: ProblemCase) => {
  try {
    await problemCaseService.remove(record.id);
    message.success('删除用例成功');
    loadCases();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除用例失败'));
  }
};

const confirmRemoveCase = (record: ProblemCase) => {
  Modal.confirm({
    title: '删除用例',
    content: '确认删除该测试用例吗？',
    okText: '确定',
    cancelText: '取消',
    onOk: () => removeCase(record),
  });
};

onMounted(() => {
  if (isEdit.value) {
    loadDetail();
  }
});
</script>

<style scoped lang="less">
.problem-edit-form {
  max-width: 1080px;
  margin: 0 auto;
}

.mt-16 {
  margin-top: 16px;
}

.case-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.ellipsis {
  max-width: 360px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
