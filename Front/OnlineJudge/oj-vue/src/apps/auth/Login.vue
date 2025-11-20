<template>
  <AuthLayout>
    <header class="auth-card__header">
      <h2>欢迎回来</h2>
      <p>登录以继续使用在线判题系统</p>
    </header>
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
        <a-input v-model:value="formState.username" placeholder="请输入用户名" auto-complete="username" />
      </a-form-item>
      <a-form-item label="密码" name="password">
        <a-input-password v-model:value="formState.password" placeholder="请输入密码" auto-complete="current-password" />
      </a-form-item>
      <a-form-item label="角色" name="role">
        <a-segmented
          v-model:value="formState.role"
          :options="roleOptions"
          block
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" block size="large" :loading="submitting" @click="handleSubmit">登录</a-button>
      </a-form-item>
    </a-form>
    <div class="auth-card__footer">
      <RouterLink to="/register">创建新账号</RouterLink>
      <RouterLink to="/forgot-password">忘记密码？</RouterLink>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { FormInstance, FormRules } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import type { LoginRequest, UserRole } from '@/types';
import AuthLayout from './components/AuthLayout.vue';
import { resolveDefaultRoute } from '@/utils/navigation';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const formRef = ref<FormInstance>();
const submitting = ref(false);

const roleOptions = [
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '管理员', value: 'admin' },
];

const formState = reactive<LoginRequest>({
  username: '',
  password: '',
  role: 'student',
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  role: [{ required: true }],
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    await authStore.login(formState);
    message.success('登录成功');
    const redirect =
      (route.query.redirect as string) || resolveDefaultRoute(authStore.role as UserRole);
    router.replace(redirect);
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
  margin-bottom: 24px;

  h2 {
    margin: 0 0 8px;
    font-size: 28px;
    font-weight: 600;
  }

  p {
    margin: 0;
    color: rgba(15, 23, 42, 0.6);
  }
}

.auth-card__footer {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
}
</style>
