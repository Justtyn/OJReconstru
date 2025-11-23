<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form ref="formRef" layout="vertical" :model="formState" :rules="rules" class="class-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="班级名称" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入班级名称" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="邀请码">
              <a-input v-model:value="formState.code" placeholder="可选：用于学生加入" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="起止日期">
              <a-range-picker
                v-model:value="dateRange"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="描述">
              <a-input v-model:value="formState.description" placeholder="简要描述班级" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">保存</a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import dayjs, { type Dayjs } from 'dayjs';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService } from '@/services/modules/classes';
import type { ClassesRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);

const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const formState = reactive<ClassesRequest>({
  name: '',
  code: '',
  startDate: '',
  endDate: '',
  description: '',
});

const dateRange = ref<[string | Dayjs, string | Dayjs] | []>([]);

const rules: FormProps['rules'] = {
  name: [{ required: true, message: '请输入班级名称' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑班级' : '新建班级'));

const loadDetail = async () => {
  if (!isEdit.value || !recordId.value) return;
  try {
    const data = await classesService.fetchDetail(recordId.value);
    formState.name = data.name;
    formState.code = data.code ?? '';
    formState.description = data.description ?? '';
    formState.startDate = data.startDate ?? '';
    formState.endDate = data.endDate ?? '';
    if (data.startDate || data.endDate) {
      dateRange.value = [data.startDate ? dayjs(data.startDate) : '', data.endDate ? dayjs(data.endDate) : ''] as [
        Dayjs | string,
        Dayjs | string,
      ];
    }
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级详情失败'));
  }
};

watchEffect(() => {
  if (dateRange.value && dateRange.value.length === 2) {
    formState.startDate = dateRange.value[0] ? dayjs(dateRange.value[0]).format('YYYY-MM-DD') : '';
    formState.endDate = dateRange.value[1] ? dayjs(dateRange.value[1]).format('YYYY-MM-DD') : '';
  }
});

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: ClassesRequest = { ...formState };
    if (isEdit.value) {
      await classesService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      await classesService.create(payload);
      message.success('创建成功');
    }
    goBack();
  } catch (error: any) {
    message.error(extractErrorMessage(error, isEdit.value ? '更新失败' : '创建失败'));
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.push('/admin/classes');
};

loadDetail();
</script>

<style scoped lang="less">
.class-edit-form {
  max-width: 960px;
}
</style>
