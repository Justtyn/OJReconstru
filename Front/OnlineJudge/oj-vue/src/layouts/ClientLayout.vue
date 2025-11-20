<template>
  <a-layout class="client-layout full-height">
    <a-layout-header class="client-layout__header">
      <div class="client-layout__brand" @click="goHome">Re Online Judge</div>
      <div class="client-layout__links">
        <RouterLink to="/home">首页</RouterLink>
        <RouterLink to="/announcements">公告</RouterLink>
        <span class="disabled-link">题库（开发中）</span>
      </div>
      <div class="client-layout__actions">
        <ThemeSwitcher />
        <span class="client-layout__role">{{ authStore.role || '访客' }}</span>
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
              <a-menu-divider />
              <a-menu-item key="logout" danger @click="handleLogout">退出登录</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    <a-layout-content class="client-layout__content">
      <RouterView />
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { useRouter, RouterLink, RouterView } from 'vue-router';
import { DownOutlined } from '@ant-design/icons-vue';
import { useAuthStore } from '@/stores/auth';
import ThemeSwitcher from '@/components/common/ThemeSwitcher.vue';

const router = useRouter();
const authStore = useAuthStore();

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
  padding: 0 32px;
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
  gap: 12px;
}

.client-layout__content {
  padding: 24px 48px;
  background: var(--body-bg);
  min-height: calc(100vh - 64px);
}
</style>
