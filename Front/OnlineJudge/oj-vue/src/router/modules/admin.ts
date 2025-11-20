import type { RouteRecordRaw } from 'vue-router';

export const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['teacher', 'admin'], app: 'admin' },
    children: [
      { path: '', redirect: '/admin/overview' },
      {
        path: 'overview',
        name: 'AdminOverview',
        component: () => import('@/apps/admin/views/AdminDashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true, roles: ['teacher', 'admin'] },
      },
      {
        path: 'users',
        name: 'AdminUserList',
        component: () => import('@/modules/admin/pages/AdminList.vue'),
        meta: { title: '管理员管理', requiresAuth: true, roles: ['admin'] },
      },
      {
        path: 'users/create',
        name: 'AdminUserCreate',
        component: () => import('@/modules/admin/pages/AdminEdit.vue'),
        meta: { title: '新建管理员', requiresAuth: true, roles: ['admin'] },
      },
      {
        path: 'users/:id/edit',
        name: 'AdminUserEdit',
        component: () => import('@/modules/admin/pages/AdminEdit.vue'),
        meta: { title: '编辑管理员', requiresAuth: true, roles: ['admin'] },
      },
      {
        path: 'teachers',
        name: 'TeacherList',
        component: () => import('@/modules/teacher/pages/TeacherList.vue'),
        meta: { title: '教师管理', requiresAuth: true, roles: ['admin', 'teacher'] },
      },
      {
        path: 'teachers/create',
        name: 'TeacherCreate',
        component: () => import('@/modules/teacher/pages/TeacherEdit.vue'),
        meta: { title: '新建教师', requiresAuth: true, roles: ['admin'] },
      },
      {
        path: 'teachers/:id/edit',
        name: 'TeacherEdit',
        component: () => import('@/modules/teacher/pages/TeacherEdit.vue'),
        meta: { title: '编辑教师', requiresAuth: true, roles: ['admin', 'teacher'] },
      },
    ],
  },
];
