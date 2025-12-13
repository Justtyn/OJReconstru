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
      <a-form-item label="验证码" name="captcha">
        <div class="captcha-row">
          <a-input
            ref="captchaInputRef"
            v-model:value="formState.captcha"
            placeholder="请输入验证码"
            allow-clear
            @keydown="handleKeydown($event, 'captcha')"
          />
          <div
            class="captcha-box"
            role="button"
            tabindex="0"
            @click="refreshCaptcha"
            @keydown.enter.prevent="refreshCaptcha"
          >
            <span class="captcha-text">{{ captchaCode }}</span>
          </div>
        </div>
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
import { onMounted, reactive, ref } from 'vue';
import type { FormInstance, FormProps } from 'ant-design-vue';
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
const usernameInputRef = ref<HTMLInputElement | null>(null);
const passwordInputRef = ref<HTMLInputElement | null>(null);
const roleSegmentedRef = ref<any>(null);
const loginButtonRef = ref<any>(null);
const captchaInputRef = ref<HTMLInputElement | null>(null);

const roleOptions = [
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '管理员', value: 'admin' },
];

type LoginFormState = LoginRequest & { captcha: string };

const formState = reactive<LoginFormState>({
  username: '',
  password: '',
  role: 'student',
  captcha: '',
});

const rules: FormProps['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
  role: [{ required: true }],
  captcha: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!value) return Promise.reject('请输入验证码');
        if (value.trim().toLowerCase() !== captchaCode.value.toLowerCase()) {
          return Promise.reject('验证码不正确');
        }
        return Promise.resolve();
      },
      trigger: 'blur',
    },
  ],
};

const captchaCode = ref('');

const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789';
  let result = '';
  for (let i = 0; i < 6; i += 1) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

const refreshCaptcha = () => {
  captchaCode.value = generateCaptcha();
};

type FocusTarget = 'username' | 'password' | 'captcha' | 'role' | 'submit';

const focusField = (target: FocusTarget) => {
  const map: Record<FocusTarget, any> = {
    username: usernameInputRef.value,
    password: passwordInputRef.value,
    role: roleSegmentedRef.value,
    captcha: captchaInputRef.value,
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
  const order: FocusTarget[] = ['username', 'password', 'captcha', 'role', 'submit'];
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
    if (formState.captcha.trim().toLowerCase() !== captchaCode.value.toLowerCase()) {
      message.error('验证码不正确');
      refreshCaptcha();
      formState.captcha = '';
      focusField('captcha');
      return;
    }
    submitting.value = true;
    await authStore.login({
      username: formState.username,
      password: formState.password,
      role: formState.role,
    });
    message.success('登录成功');
    const redirect =
      (route.query.redirect as string) || resolveDefaultRoute(authStore.role as UserRole);
    router.replace(redirect);
  } catch (error: any) {
    const errMsg = extractErrorMessage(error, '登录失败');
    const isEmailUnverified = errMsg.includes('未验证') || /not\s+verified/i.test(errMsg);
    if (isEmailUnverified) {
      authStore.setPendingVerifyUser({ username: formState.username, password: formState.password });
      message.warning('邮箱未验证，请先完成验证');
      router.replace({ path: '/verify-email', query: { username: formState.username } });
      return;
    }
    message.error(errMsg);
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  refreshCaptcha();
});
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

.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-box {
  min-width: 140px;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid var(--border-color, rgba(0, 0, 0, 0.12));
  background: var(--card-bg, rgba(255, 255, 255, 0.92));
  color: var(--text-color, #0f172a);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.captcha-text {
  letter-spacing: 2px;
  font-weight: 700;
  font-size: 18px;
  user-select: none;
}

@media (max-width: 576px) {
  .captcha-row {
    flex-direction: column;
    align-items: stretch;
  }

  .captcha-box {
    width: 100%;
    align-items: center;
  }
}
</style>
