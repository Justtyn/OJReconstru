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
        path: 'ranking',
        name: 'ClientRanking',
        component: () => import('@/apps/client/views/ranking/RankingList.vue'),
        meta: { title: '排行榜', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'ClientProfile',
        component: () => import('@/apps/client/views/ProfilePlaceholder.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'students/:id',
        name: 'ClientStudentProfile',
        component: () => import('@/apps/client/views/students/StudentProfile.vue'),
        meta: { title: '学生主页', requiresAuth: true },
      },
      {
        path: 'problems',
        name: 'ClientProblemList',
        component: () => import('@/apps/client/views/problems/ProblemList.vue'),
        meta: { title: '题库', requiresAuth: true },
      },
      {
        path: 'problems/:id/submit',
        name: 'ClientProblemSubmit',
        component: () => import('@/apps/client/views/problems/ProblemSubmit.vue'),
        meta: { title: '提交代码', requiresAuth: true },
      },
      {
        path: 'problems/:id',
        name: 'ClientProblemDetail',
        component: () => import('@/apps/client/views/problems/ProblemDetail.vue'),
        meta: { title: '题目详情', requiresAuth: true },
      },
      {
        path: 'homeworks',
        name: 'ClientHomeworkList',
        component: () => import('@/apps/client/views/homeworks/HomeworkList.vue'),
        meta: { title: '作业', requiresAuth: true },
      },
      {
        path: 'homeworks/:id',
        name: 'ClientHomeworkDetail',
        component: () => import('@/apps/client/views/homeworks/HomeworkDetail.vue'),
        meta: { title: '作业详情', requiresAuth: true },
      },
      {
        path: 'submissions',
        name: 'ClientSubmissionList',
        component: () => import('@/apps/client/views/submissions/SubmissionList.vue'),
        meta: { title: '提交记录', requiresAuth: true },
      },
      {
        path: 'submissions/:id',
        name: 'ClientSubmissionDetail',
        component: () => import('@/apps/client/views/submissions/SubmissionDetail.vue'),
        meta: { title: '提交详情', requiresAuth: true },
      },
      {
        path: 'solutions',
        name: 'ClientSolutionList',
        component: () => import('@/apps/client/views/solutions/SolutionList.vue'),
        meta: { title: '题解', requiresAuth: true },
      },
      {
        path: 'solutions/:id',
        name: 'ClientSolutionDetail',
        component: () => import('@/apps/client/views/solutions/SolutionDetail.vue'),
        meta: { title: '题解详情', requiresAuth: true },
      },
      {
        path: 'discussions',
        name: 'ClientDiscussionList',
        component: () => import('@/apps/client/views/discussions/DiscussionList.vue'),
        meta: { title: '讨论', requiresAuth: true },
      },
      {
        path: 'discussions/:id',
        name: 'ClientDiscussionDetail',
        component: () => import('@/apps/client/views/discussions/DiscussionDetail.vue'),
        meta: { title: '讨论详情', requiresAuth: true },
      },
      {
        path: 'announcements',
        name: 'ClientAnnouncements',
        component: () => import('@/apps/client/views/announcements/AnnouncementList.vue'),
        meta: { title: '系统公告', requiresAuth: true },
      },
      {
        path: 'about',
        name: 'ClientAbout',
        component: () => import('@/apps/client/views/AboutView.vue'),
        meta: { title: '关于系统', requiresAuth: true },
      },
    ],
  },
];
