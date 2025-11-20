<template>
  <AuthLayout>
    <header class="auth-card__header">
      <h2>找回密码</h2>
      <p>通过邮箱验证码重置密码（仅学生）</p>
    </header>
    <a-steps :current="currentStep" class="mb-24">
      <a-step title="验证用户名" />
      <a-step title="输入验证码与新密码" />
    </a-steps>

    <div v-if="currentStep === 0">
      <a-form
        ref="stepOneForm"
        class="auth-form"
        layout="vertical"
        :model="stepOne"
        :rules="stepOneRules"
        @submit.prevent="handleSendCode"
        @keyup.enter="handleSendCode"
      >
        <a-form-item label="学生用户名" name="username">
          <a-input v-model:value="stepOne.username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item>
            <a-button type="primary" block size="large" :loading="sending" @click="handleSendCode">发送验证码</a-button>
        </a-form-item>
      </a-form>
    </div>

    <div v-else>
      <a-alert type="info" show-icon class="mb-16" :message="`验证码已发送至 ${stepOne.username} 绑定邮箱`" />
      <a-form
        class="auth-form"
        ref="stepTwoForm"
        layout="vertical"
        :model="stepTwo"
        :rules="stepTwoRules"
        @submit.prevent="handleReset"
        @keyup.enter="handleReset"
      >
        <a-form-item label="验证码" name="code">
          <a-input v-model:value="stepTwo.code" placeholder="6 位验证码" maxlength="6" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="stepTwo.newPassword" placeholder="至少 6 位" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="stepTwo.confirmPassword" placeholder="再次输入新密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" block size="large" :loading="resetting" @click="handleReset">重置密码</a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="auth-card__footer">
      <RouterLink to="/login">返回登录</RouterLink>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { useRouter, RouterLink } from 'vue-router';
import AuthLayout from './components/AuthLayout.vue';
import { useAuthStore } from '@/stores/auth';
import { extractErrorMessage } from '@/utils/error';

const authStore = useAuthStore();
const router = useRouter();

const currentStep = ref(0);

const stepOne = reactive({ username: '' });
const stepOneForm = ref<FormInstance>();
const sending = ref(false);

const stepTwo = reactive({
  username: '',
  code: '',
  newPassword: '',
  confirmPassword: '',
});
const stepTwoForm = ref<FormInstance>();
const resetting = ref(false);

const stepOneRules: FormProps['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
};

const stepTwoRules: FormProps['rules'] = {
  code: [{ required: true, message: '请输入验证码' }],
  newPassword: [{ required: true, message: '请输入新密码' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码' },
    {
      validator: (_rule: unknown, value: string) => {
        if (value !== stepTwo.newPassword) {
          return Promise.reject('两次输入的密码不一致');
        }
        return Promise.resolve();
      },
    },
  ],
};

const handleSendCode = async () => {
  try {
    await stepOneForm.value?.validate();
    sending.value = true;
    await authStore.sendForgotPasswordCode({ username: stepOne.username });
    stepTwo.username = stepOne.username;
    currentStep.value = 1;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '发送验证码失败'));
  } finally {
    sending.value = false;
  }
};

const handleReset = async () => {
  try {
    await stepTwoForm.value?.validate();
    resetting.value = true;
    await authStore.verifyForgotPassword({
      username: stepTwo.username,
      code: stepTwo.code,
      newPassword: stepTwo.newPassword,
    });
    router.replace('/login');
  } catch (error: any) {
    message.error(extractErrorMessage(error, '重置密码失败'));
  } finally {
    resetting.value = false;
  }
};
</script>

<style scoped lang="less">
.auth-card__header {
  margin-bottom: 16px;
}
.mb-16 {
  margin-bottom: 16px;
}
.mb-24 {
  margin-bottom: 24px;
}
.auth-card__footer {
  margin-top: 16px;
  text-align: center;
}
</style>
