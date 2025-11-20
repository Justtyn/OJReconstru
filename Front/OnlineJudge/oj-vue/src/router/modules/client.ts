import type { RouteRecordRaw } from 'vue-router';

export const clientRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/ClientLayout.vue'),
    meta: { requiresAuth: true, app: 'client' },
    children: [
      { path: '', redirect: '/home' },
      {
        path: 'home',
        name: 'ClientHome',
        component: () => import('@/apps/client/views/HomeView.vue'),
        meta: { title: '首页', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'ClientProfile',
        component: () => import('@/apps/client/views/ProfilePlaceholder.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'announcements',
        name: 'ClientAnnouncements',
        component: () => import('@/apps/client/views/announcements/AnnouncementList.vue'),
        meta: { title: '系统公告', requiresAuth: true },
      },
    ],
  },
];
