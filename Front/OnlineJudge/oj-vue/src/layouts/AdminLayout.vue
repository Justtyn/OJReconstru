<template>
  <a-layout class="admin-layout full-height">
    <a-layout-sider collapsible v-model:collapsed="collapsed" class="admin-layout__sider">
      <div class="admin-layout__logo">OJ Admin</div>
      <a-menu
        theme="dark"
        mode="inline"
        :selectedKeys="selectedKeys"
        :items="menuItems"
        @click="handleMenuClick"
      />
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="admin-layout__header">
        <div class="admin-layout__user">
          <strong>{{ authStore.user?.username }}</strong>
          <span class="role-badge">{{ authStore.role }}</span>
        </div>
        <a-space>
          <ThemeSwitcher />
          <a-button type="link" @click="backToClient">返回客户端</a-button>
          <a-button danger type="primary" @click="handleLogout">退出</a-button>
        </a-space>
      </a-layout-header>
      <a-layout-content class="admin-layout__content">
        <RouterView />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, ref, h } from 'vue';
import { useRouter, useRoute, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import type { MenuProps } from 'ant-design-vue';
import {
  DashboardOutlined,
  TeamOutlined,
  UserOutlined,
  SolutionOutlined,
  NotificationOutlined,
  AuditOutlined,
  CodeOutlined,
  BookOutlined,
} from '@ant-design/icons-vue';
import ThemeSwitcher from '@/components/common/ThemeSwitcher.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const collapsed = ref(false);

type MenuItem = {
  key: string;
  label: string;
  icon: () => ReturnType<typeof h>;
  roles?: Array<'admin' | 'teacher'>;
};

const rawMenuItems: MenuItem[] = [
  {
    key: '/admin/overview',
    icon: () => h(DashboardOutlined),
    label: '仪表盘',
    roles: ['admin', 'teacher'],
  },
  {
    key: '/admin/users',
    icon: () => h(UserOutlined),
    label: '管理员管理',
    roles: ['admin'],
  },
  {
    key: '/admin/teachers',
    icon: () => h(TeamOutlined),
    label: '教师管理',
    roles: ['admin', 'teacher'],
  },
  {
    key: '/admin/students',
    icon: () => h(SolutionOutlined),
    label: '学生管理',
    roles: ['admin'],
  },
  {
    key: '/admin/classes',
    icon: () => h(BookOutlined),
    label: '班级与作业',
    roles: ['admin', 'teacher'],
  },
  {
    key: '/admin/announcements',
    icon: () => h(NotificationOutlined),
    label: '公告管理',
    roles: ['admin'],
  },
  {
    key: '/admin/login-logs',
    icon: () => h(AuditOutlined),
    label: '登录日志',
    roles: ['admin'],
  },
  {
    key: '/admin/problems',
    icon: () => h(CodeOutlined),
    label: '题目管理',
    roles: ['admin', 'teacher'],
  },
];

const menuItems = computed<MenuProps['items']>(() => {
  const role = authStore.role ?? 'student';
  return rawMenuItems
    .filter((item) => !item.roles || item.roles.includes(role as 'admin' | 'teacher'))
    .map(({ key, icon, label }) => ({
      key,
      icon,
      label,
    }));
});

const resolveSelectedKey = () => {
  if (route.path.startsWith('/admin/users')) return '/admin/users';
  if (route.path.startsWith('/admin/teachers')) return '/admin/teachers';
  if (route.path.startsWith('/admin/students')) return '/admin/students';
  if (route.path.startsWith('/admin/classes')) return '/admin/classes';
  if (route.path.startsWith('/admin/announcements')) return '/admin/announcements';
  if (route.path.startsWith('/admin/login-logs')) return '/admin/login-logs';
  if (route.path.startsWith('/admin/problems')) return '/admin/problems';
  return '/admin/overview';
};

const selectedKeys = computed(() => {
  if (route.path.startsWith('/admin')) {
    return [resolveSelectedKey()];
  }
  return ['/admin/overview'];
});

const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
  if (typeof key === 'string') {
    router.push(key);
  }
};

const handleLogout = async () => {
  await authStore.logout();
  router.replace('/login');
};

const backToClient = () => {
  router.push('/home');
};
</script>

<style scoped lang="less">
.admin-layout__sider {
  min-height: 100vh;
}

.admin-layout__logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
}

.admin-layout__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card-bg);
  padding: 0 24px;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.08);
}

.admin-layout__content {
  padding: 24px;
  min-height: calc(100vh - 64px);
  background: var(--body-bg);
}

.role-badge {
  margin-left: 8px;
  padding: 2px 8px;
  border-radius: 12px;
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
  font-size: 12px;
  text-transform: uppercase;
}
</style>
