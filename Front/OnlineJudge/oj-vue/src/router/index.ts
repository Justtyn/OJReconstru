import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { clientRoutes } from './modules/client';
import { adminRoutes } from './modules/admin';
import { WHITE_LIST } from './whitelist';
import { useAuthStore } from '@/stores/auth';
import { pinia } from '@/stores';
import { resolveDefaultRoute } from '@/utils/navigation';

const routes: RouteRecordRaw[] = [
  ...clientRoutes,
  ...adminRoutes,
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/apps/auth/Login.vue'),
    meta: { guestOnly: true, title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/apps/auth/Register.vue'),
    meta: { guestOnly: true, title: '注册' },
  },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('@/apps/auth/VerifyEmail.vue'),
    meta: { allowGuest: true, title: '邮箱验证' },
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/apps/auth/ForgotPassword.vue'),
    meta: { allowGuest: true, title: '找回密码' },
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/Forbidden.vue'),
    meta: { allowGuest: true, title: '无权限' },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { allowGuest: true, title: '页面不存在' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
});

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore(pinia);
  if (!authStore.initialized) {
    await authStore.restore();
  }

  const isWhitelisted = WHITE_LIST.includes(to.path);

  if (isWhitelisted || to.meta.allowGuest) {
    if (to.meta.guestOnly && authStore.isAuthenticated) {
      return next(resolveDefaultRoute(authStore.role));
    }
    return next();
  }

  if (to.meta.requiresAuth !== false && !authStore.isAuthenticated) {
    return next({ path: '/login', query: { redirect: to.fullPath } });
  }

  if (to.meta.roles && authStore.role && !to.meta.roles.includes(authStore.role)) {
    return next('/403');
  }

  const targetApp = to.meta.app;
  if (targetApp === 'client' && authStore.role && authStore.role !== 'student') {
    return next(resolveDefaultRoute(authStore.role));
  }

  if (targetApp === 'admin' && authStore.role === 'student') {
    return next('/403');
  }

  return next();
});

export default router;
