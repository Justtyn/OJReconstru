<template>
  <AuthLayout>
    <header class="auth-card__header">
      <h2>验证邮箱</h2>
      <p>输入验证码以激活账号</p>
    </header>
    <a-alert
      type="info"
      show-icon
      class="mb-16"
      :message="`验证码已发送至 ${pendingEmail || '注册邮箱'}`"
      description="验证码有效期 10 分钟，如未收到可在 1 分钟后重新发送。"
    />
    <a-form
      ref="formRef"
      class="auth-form"
      layout="vertical"
      :model="formState"
      :rules="rules"
      @submit.prevent="handleSubmit"
      @keyup.enter="handleSubmit"
    >
      <a-form-item label="用户名" name="username">
        <a-input v-model:value="formState.username" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="验证码" name="code">
        <a-input v-model:value="formState.code" placeholder="6 位验证码" maxlength="6" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" block size="large" :loading="submitting" @click="handleSubmit">验证并登录</a-button>
      </a-form-item>
    </a-form>
    <div class="auth-card__footer">
      <RouterLink to="/login">返回登录</RouterLink>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';
import type { FormInstance, FormRules } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import AuthLayout from './components/AuthLayout.vue';
import { useAuthStore } from '@/stores/auth';
import type { VerifyEmailRequest, UserRole } from '@/types';
import { resolveDefaultRoute } from '@/utils/navigation';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const formRef = ref<FormInstance>();
const submitting = ref(false);

const formState = reactive<VerifyEmailRequest>({
  username: (route.query.username as string) || authStore.pendingVerifyUser?.username || '',
  code: '',
});

const pendingEmail = computed(() => authStore.pendingVerifyUser?.email);

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名' }],
  code: [{ required: true, message: '请输入验证码' }],
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const data = await authStore.verifyEmail(formState);
    message.success('邮箱验证成功');
    router.replace(resolveDefaultRoute(data.role as UserRole));
  } catch (error: any) {
    const backendMsg = error?.message || error?.response?.data?.message;
    if (backendMsg) {
      message.error(backendMsg);
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped lang="less">
.auth-card__header {
  margin-bottom: 16px;
  h2 {
    margin: 0 0 8px;
    font-size: 28px;
  }
}

.mb-16 {
  margin-bottom: 16px;
}

.auth-card__footer {
  margin-top: 12px;
  text-align: center;
}
</style>
