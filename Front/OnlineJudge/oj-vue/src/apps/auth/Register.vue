<template>
  <AuthLayout>
    <header class="auth-card__header">
      <h2>创建账号</h2>
      <p>注册并加入 Re Online Judge</p>
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
        <a-input v-model:value="formState.username" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="密码" name="password">
        <a-input-password v-model:value="formState.password" placeholder="设置密码" />
      </a-form-item>
      <a-form-item label="确认密码" name="confirmPassword">
        <a-input-password v-model:value="formState.confirmPassword" placeholder="再次输入密码" />
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="formState.email" placeholder="用于接收验证码" />
      </a-form-item>
      <a-form-item label="姓名 (可选)" name="name">
        <a-input v-model:value="formState.name" placeholder="请输入真实姓名" />
      </a-form-item>
      <a-alert type="info" show-icon class="mb-16" message="当前仅支持注册学生账户，教师与管理员由后台创建。" />
      <a-form-item>
        <a-button type="primary" block size="large" :loading="submitting" @click="handleSubmit">注册</a-button>
      </a-form-item>
    </a-form>
    <div class="auth-card__footer">
      已有账号？<RouterLink to="/login">立即登录</RouterLink>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter, RouterLink } from 'vue-router';
import { message } from 'ant-design-vue';
import type { FormInstance, FormRules } from 'ant-design-vue';
import { useAuthStore } from '@/stores/auth';
import type { RegisterRequest, UserRole } from '@/types';
import AuthLayout from './components/AuthLayout.vue';
import { resolveDefaultRoute } from '@/utils/navigation';

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref<FormInstance>();
const submitting = ref(false);

const formState = reactive<Omit<RegisterRequest, 'role'> & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  name: '',
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  confirmPassword: [
    { required: true, message: '请再次输入密码' },
    {
      validator: (_rule, value) => {
        if (value !== formState.password) {
          return Promise.reject('两次输入的密码不一致');
        }
        return Promise.resolve();
      },
    },
  ],
  email: [{ required: true, message: '请输入邮箱' }],
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: RegisterRequest = {
      username: formState.username,
      password: formState.password,
      email: formState.email,
      name: formState.name,
      role: 'student',
    };
    const result = await authStore.register(payload);
    if (result) {
      message.success('注册成功');
      router.replace(resolveDefaultRoute(result.role as UserRole));
    } else {
      message.success('验证码已发送，请查收邮箱完成激活');
      router.replace({ path: '/verify-email', query: { username: formState.username } });
    }
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
  text-align: center;
}

.mb-16 {
  margin-bottom: 16px;
}
</style>
