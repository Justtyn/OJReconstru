<template>
  <a-layout class="client-layout full-height">
    <a-layout-header class="client-layout__header">
      <div class="client-layout__brand" @click="goHome">Re Online Judge</div>
      <div class="client-layout__links">
        <RouterLink to="/home">首页</RouterLink>
        <RouterLink to="/problems">题库</RouterLink>
        <RouterLink to="/submissions">提交记录</RouterLink>
        <RouterLink to="/ranking">排行榜</RouterLink>
        <RouterLink to="/solutions">题解</RouterLink>
        <RouterLink to="/discussions">讨论</RouterLink>
        <RouterLink v-if="isStudent" to="/homeworks">作业</RouterLink>
        <RouterLink to="/announcements">公告</RouterLink>
        <RouterLink to="/about">关于</RouterLink>
      </div>
      <div class="client-layout__actions">
        <ThemeSwitcher />
        <span class="client-layout__role" :class="roleClass">{{ roleLabel }}</span>
        <a-dropdown>
          <a class="ant-dropdown-link" @click.prevent>
            {{ authStore.user?.username || '用户' }} <DownOutlined />
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item v-if="authStore.role === 'student'" key="profile" @click="goProfile">
                个人中心
              </a-menu-item>
              <a-menu-item
                v-if="authStore.role === 'admin' || authStore.role === 'teacher'"
                key="admin"
                @click="goAdmin"
              >
                管理系统
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" danger @click="handleLogout">退出登录</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    <a-layout-content class="client-layout__content">
      <div class="client-layout__main">
        <RouterView />
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter, RouterLink, RouterView } from 'vue-router';
import { DownOutlined } from '@ant-design/icons-vue';
import { useAuthStore } from '@/stores/auth';
import ThemeSwitcher from '@/components/common/ThemeSwitcher.vue';

const router = useRouter();
const authStore = useAuthStore();
const isStudent = computed(() => authStore.role === 'student');
const roleLabel = computed(() => {
  if (authStore.role === 'student') return '学生';
  if (authStore.role === 'teacher') return '教师';
  if (authStore.role === 'admin') return '管理员';
  return '访客';
});
const roleClass = computed(() => ({
  'client-layout__role--student': authStore.role === 'student',
  'client-layout__role--teacher': authStore.role === 'teacher',
  'client-layout__role--admin': authStore.role === 'admin',
  'client-layout__role--guest': !authStore.role,
}));

const goHome = () => {
  router.push('/home');
};

const goProfile = () => {
  router.push('/profile');
};

const goAdmin = () => {
  router.push('/admin');
};

const handleLogout = async () => {
  await authStore.logout();
  router.replace('/login');
};
</script>

<style scoped lang="less">
.client-layout__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card-bg);
  color: var(--text-color);
  padding: 0 40px;
  height: 64px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.08);
}

.client-layout__brand {
  font-weight: 600;
  cursor: pointer;
}

.client-layout__links {
  display: flex;
  gap: 16px;

  a {
    color: inherit;
    font-weight: 500;
  }

  .disabled-link {
    opacity: 0.5;
    pointer-events: none;
  }
}

.client-layout__actions {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-right: 12px;
}

.client-layout__role {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
  border: 1px solid transparent;
}

.client-layout__role--student {
  background: rgba(34, 197, 94, 0.16);
  border-color: rgba(34, 197, 94, 0.4);
  color: #166534;
}

.client-layout__role--teacher {
  background: rgba(14, 165, 233, 0.16);
  border-color: rgba(14, 165, 233, 0.45);
  color: #0f172a;
}

.client-layout__role--admin {
  background: rgba(249, 115, 22, 0.16);
  border-color: rgba(249, 115, 22, 0.45);
  color: #9a3412;
}

.client-layout__role--guest {
  background: rgba(148, 163, 184, 0.2);
  border-color: rgba(148, 163, 184, 0.4);
  color: var(--text-color);
}

.client-layout__content {
  padding: 24px 48px;
  background: var(--body-bg);
  min-height: calc(100vh - 64px);
}

.client-layout__main {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
}

:global(:root[data-theme='dark']) .client-layout__role--student {
  color: #bbf7d0;
}

:global(:root[data-theme='dark']) .client-layout__role--teacher {
  color: #e0f2fe;
}

:global(:root[data-theme='dark']) .client-layout__role--admin {
  color: #fed7aa;
}
</style>
