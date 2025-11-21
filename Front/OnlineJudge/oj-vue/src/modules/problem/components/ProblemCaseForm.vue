<template>
  <a-form layout="vertical" :model="formState" :rules="rules" ref="formRef">
    <a-form-item label="是否样例" name="isSample">
      <a-switch v-model:checked="formState.isSample" />
    </a-form-item>
    <a-form-item label="是否启用" name="isActive">
      <a-switch v-model:checked="formState.isActive" />
    </a-form-item>
    <a-form-item label="分值" name="score">
      <a-input-number v-model:value="formState.score" :min="0" style="width: 100%" placeholder="可选，默认为 0" />
    </a-form-item>
    <a-form-item label="输入" name="inputData">
      <a-textarea v-model:value="formState.inputData" :rows="8" show-count placeholder="请输入测试用例输入" />
    </a-form-item>
    <a-form-item label="输出" name="outputData">
      <a-textarea v-model:value="formState.outputData" :rows="8" show-count placeholder="请输入对应输出" />
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import type { FormInstance, FormProps } from 'ant-design-vue';
import type { ProblemCaseUpsertRequest, ProblemCase } from '@/types';

const props = defineProps<{
  modelValue?: ProblemCaseUpsertRequest | ProblemCase;
}>();

const emits = defineEmits<{
  (e: 'change', value: ProblemCaseUpsertRequest): void;
  (e: 'update:modelValue', value: ProblemCaseUpsertRequest): void;
}>();

const formRef = ref<FormInstance>();
const formState = reactive<ProblemCaseUpsertRequest>({
  inputData: '',
  outputData: '',
  score: 0,
  isSample: false,
  isActive: true,
});

const rules: FormProps['rules'] = {
  inputData: [{ required: true, message: '请输入输入内容' }],
  outputData: [{ required: true, message: '请输入输出内容' }],
  score: [
    {
      validator: (_r, v: number | undefined) => {
        if (v !== undefined && v < 0) return Promise.reject('分值不能小于 0');
        return Promise.resolve();
      },
    },
  ],
};

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      formState.inputData = (val as any).inputData ?? (val as any).input ?? '';
      formState.outputData = (val as any).outputData ?? (val as any).output ?? '';
      formState.score = val.score ?? 0;
      formState.isSample = Boolean(val.isSample);
      formState.isActive = val.isActive ?? true;
    }
  },
  { immediate: true },
);

watch(
  formState,
  (val) => {
    emits('update:modelValue', { ...val });
    emits('change', { ...val });
  },
  { deep: true },
);

const validate = () => formRef.value?.validate();

defineExpose({
  validate,
  formState,
});
</script>
