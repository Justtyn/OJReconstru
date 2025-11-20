<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form
        layout="vertical"
        :model="formState"
        :rules="rules"
        ref="formRef"
        @submit.prevent="handleSubmit"
        class="admin-edit-form"
      >
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" :disabled="isEdit" placeholder="请输入用户名" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item :label="passwordLabel" name="password">
              <a-input-password v-model:value="formState.password" :placeholder="passwordPlaceholder" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password v-model:value="formState.confirmPassword" placeholder="请再次输入密码" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="性别" name="sex">
              <a-select v-model:value="formState.sex" placeholder="请选择性别" allow-clear>
                <a-select-option value="male">男</a-select-option>
                <a-select-option value="female">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formState.phone" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="出生日期" name="birth">
              <a-date-picker
                v-model:value="formState.birth"
                placeholder="选择出生日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
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
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { adminUserService } from '@/services/modules/adminUser';
import type { AdminUpsertRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

type AdminFormState = AdminUpsertRequest & { confirmPassword?: string };

const formState = reactive<AdminFormState>({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  sex: '',
  birth: '',
  confirmPassword: '',
});

const rules: FormProps['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!isEdit.value && !value) return Promise.reject('请输入密码');
        if (value && (value.length < 6 || value.length > 100)) {
          return Promise.reject('密码长度需在 6-100 之间');
        }
        return Promise.resolve();
      },
    },
  ],
  email: [
    {
      type: 'email',
      message: '邮箱格式不正确',
      trigger: 'blur',
    },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '手机号格式不正确',
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!formState.password && !value) return Promise.resolve();
        if (!value) return Promise.reject('请再次输入密码');
        if (value !== formState.password) return Promise.reject('两次输入的密码不一致');
        return Promise.resolve();
      },
      trigger: 'blur',
    },
  ],
};

const passwordLabel = computed(() => (isEdit.value ? '密码（留空表示不修改）' : '密码'));
const passwordPlaceholder = computed(() => (isEdit.value ? '不修改可留空' : '请输入密码'));
const pageTitle = computed(() => (isEdit.value ? '编辑管理员' : '新建管理员'));

const loadDetail = async () => {
  if (!isEdit.value) return;
  try {
    if (!recordId.value) return;
    const data = await adminUserService.fetchDetail(recordId.value);
    formState.username = data.username;
    formState.name = data.name ?? '';
    formState.sex = data.sex ?? '';
    formState.email = data.email ?? '';
    formState.phone = data.phone ?? '';
    formState.birth = data.birth ?? '';
    formState.password = '';
    formState.confirmPassword = '';
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取详情失败'));
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: AdminUpsertRequest = {
      username: formState.username,
      name: formState.name,
      sex: formState.sex,
      email: formState.email,
      phone: formState.phone,
      birth: formState.birth || undefined,
    };
    if (formState.password || formState.confirmPassword) {
      if (formState.password !== formState.confirmPassword) {
        message.error('两次输入的密码不一致');
        submitting.value = false;
        return;
      }
      if (formState.password) {
        payload.password = formState.password;
      }
    }
    if (isEdit.value) {
      await adminUserService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      if (!formState.password) {
        message.error('请输入密码');
        submitting.value = false;
        return;
      }
      payload.password = formState.password;
      const created = await adminUserService.create(payload);
      message.success(`创建成功：${created.username}`);
    }
    router.push('/admin/users');
  } catch (error: any) {
    message.error(extractErrorMessage(error, '提交失败'));
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  loadDetail();
});
</script>

<style scoped lang="less">
.admin-edit-form {
  max-width: 960px;
  margin: 0 auto;
}
</style>
