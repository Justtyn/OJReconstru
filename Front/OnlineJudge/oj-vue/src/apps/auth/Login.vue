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
      @keydown.enter.prevent="handleSubmit"
    >
      <a-form-item label="用户名" name="username">
        <a-input
          ref="usernameInputRef"
          v-model:value="formState.username"
          placeholder="请输入用户名"
          auto-complete="username"
          @keydown="handleKeydown($event, 'username')"
        />
      </a-form-item>
      <a-form-item label="密码" name="password">
        <a-input-password
          ref="passwordInputRef"
          v-model:value="formState.password"
          placeholder="请输入密码"
          auto-complete="current-password"
          @keydown="handleKeydown($event, 'password')"
        />
      </a-form-item>
      <a-form-item label="角色" name="role">
        <a-segmented
          ref="roleSegmentedRef"
          v-model:value="formState.role"
          :options="roleOptions"
          block
          @keydown="handleKeydown($event, 'role')"
        />
      </a-form-item>
      <a-form-item>
        <a-button
          ref="loginButtonRef"
          type="primary"
          block
          size="large"
          :loading="submitting"
          @click="handleSubmit"
          @keydown="handleKeydown($event, 'submit')"
        >
          登录
        </a-button>
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
import type { FormInstance, FormProps, InputRef } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import type { LoginRequest, UserRole } from '@/types';
import AuthLayout from './components/AuthLayout.vue';
import { resolveDefaultRoute } from '@/utils/navigation';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const usernameInputRef = ref<InputRef>();
const passwordInputRef = ref<InputRef>();
const roleSegmentedRef = ref<any>();
const loginButtonRef = ref<any>();

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

const rules: FormProps['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  role: [{ required: true }],
};

type FocusTarget = 'username' | 'password' | 'role' | 'submit';

const focusField = (target: FocusTarget) => {
  const map: Record<FocusTarget, any> = {
    username: usernameInputRef.value,
    password: passwordInputRef.value,
    role: roleSegmentedRef.value,
    submit: loginButtonRef.value,
  };
  const instance = map[target];
  if (!instance) return;

  if (typeof instance.focus === 'function') {
    instance.focus();
    return;
  }

  const el = instance.$el as HTMLElement | undefined;
  if (el?.focus) {
    el.focus();
    return;
  }

  const interactive = el?.querySelector<HTMLElement>('input,button,[tabindex]');
  interactive?.focus();
};

const getNextTarget = (current: FocusTarget, backward = false): FocusTarget | undefined => {
  const order: FocusTarget[] = ['username', 'password', 'role', 'submit'];
  const index = order.indexOf(current);
  if (index === -1) return undefined;
  const nextIndex = index + (backward ? -1 : 1);
  if (nextIndex < 0 || nextIndex >= order.length) return undefined;
  return order[nextIndex];
};

const handleKeydown = (event: KeyboardEvent, current: FocusTarget) => {
  if (event.key === 'Enter') {
    event.preventDefault();
    handleSubmit();
    return;
  }

  if (event.key === 'Tab') {
    const next = getNextTarget(current, event.shiftKey);
    if (!next) return;
    event.preventDefault();
    focusField(next);
  }
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
    message.error(extractErrorMessage(error, '登录失败'));
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
