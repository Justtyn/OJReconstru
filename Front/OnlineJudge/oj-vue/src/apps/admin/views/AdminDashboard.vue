<template>
  <PageContainer title="管理端仪表盘" subtitle="概览关键数据，快速进入常用管理模块">
    <a-row :gutter="[16, 16]" class="mb-16">
      <a-col :span="24">
        <a-alert
          type="info"
          show-icon
          message="欢迎回来"
          :description="`当前用户：${authStore.user?.username || '-'}（${roleLabel}）`"
        />
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]" class="mb-16">
      <a-col v-for="card in statCards" :key="card.key" :xs="12" :md="6">
        <a-card :bordered="false" class="stat-card" :loading="statsLoading">
          <div class="stat-card__title">
            <component :is="card.icon" />
            <span>{{ card.title }}</span>
          </div>
          <div class="stat-card__value">
            <a-statistic :value="card.value" />
          </div>
          <div class="stat-card__desc">{{ card.desc }}</div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="14">
        <a-card title="近期提交" :loading="recentLoading" body-style="{ padding: '0 16px 16px' }">
          <a-table
            size="small"
            :columns="recentColumns"
            :data-source="recentSubmissions"
            :pagination="false"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-badge :status="statusBadge(record.overallStatusCode)" :text="record.overallStatusName || record.overallStatusCode" />
              </template>
              <template v-else-if="column.key === 'time'">
                {{ record.createdAt ? format(new Date(record.createdAt), 'MM-dd HH:mm') : '-' }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="10">
        <a-card title="快捷入口" body-style="{ padding: '12px 16px' }">
          <a-list :data-source="quickLinks" :split="false">
            <template #renderItem="{ item }">
              <a-list-item class="quick-link" @click="go(item.path)">
                <div class="quick-link__title">{{ item.title }}</div>
                <div class="quick-link__desc">{{ item.desc }}</div>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import {
  TeamOutlined,
  UserOutlined,
  BookOutlined,
  ScheduleOutlined,
  CodeOutlined,
  CommentOutlined,
  SolutionOutlined,
  LaptopOutlined,
} from '@ant-design/icons-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { useAuthStore } from '@/stores/auth';
import { studentService } from '@/services/modules/student';
import { teacherService } from '@/services/modules/teacher';
import { classesService } from '@/services/modules/classes';
import { homeworkService } from '@/services/modules/homework';
import { problemService } from '@/services/modules/problem';
import { solutionService } from '@/services/modules/solution';
import { discussionService } from '@/services/modules/discussion';
import { submissionService } from '@/services/modules/submission';
import type { Submission, SubmissionQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const authStore = useAuthStore();
const router = useRouter();

const statsLoading = ref(false);
const stats = reactive({
  students: 0,
  teachers: 0,
  classes: 0,
  homeworks: 0,
  problems: 0,
  submissions: 0,
  solutions: 0,
  discussions: 0,
});

const recentLoading = ref(false);
const recentSubmissions = ref<Submission[]>([]);

const statCards = computed(() => [
  { key: 'students', title: '学生数', value: stats.students, desc: '活跃/总学生', icon: TeamOutlined },
  { key: 'teachers', title: '教师数', value: stats.teachers, desc: '可管理教师', icon: UserOutlined },
  { key: 'classes', title: '班级数', value: stats.classes, desc: '正在使用的班级', icon: BookOutlined },
  { key: 'homeworks', title: '作业数', value: stats.homeworks, desc: '布置中的作业', icon: ScheduleOutlined },
  { key: 'problems', title: '题目数', value: stats.problems, desc: '题库总量', icon: CodeOutlined },
  { key: 'submissions', title: '提交数', value: stats.submissions, desc: '历史提交总计', icon: LaptopOutlined },
  { key: 'solutions', title: '题解数', value: stats.solutions, desc: '已发布题解', icon: SolutionOutlined },
  { key: 'discussions', title: '讨论数', value: stats.discussions, desc: '讨论与回复', icon: CommentOutlined },
]);

const recentColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 160 },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 160 },
  { title: '学生', dataIndex: 'studentId', key: 'studentId', width: 120 },
  { title: '状态', dataIndex: 'overallStatusCode', key: 'status', width: 120 },
  { title: '时间', dataIndex: 'createdAt', key: 'time', width: 120 },
];

const quickLinks = [
  { title: '题目管理', desc: '维护题库、标签与限制', path: '/admin/problems' },
  { title: '作业管理', desc: '布置/编辑班级作业', path: '/admin/classes' },
  { title: '提交记录', desc: '查看判题与重新提交', path: '/admin/submissions' },
  { title: '讨论/题解', desc: '社区内容审核与维护', path: '/admin/discussions' },
  { title: '学生管理', desc: '用户资料与授权', path: '/admin/students' },
  { title: '教师管理', desc: '教师账户与权限', path: '/admin/teachers' },
];

const roleLabel = computed(() => {
  if (authStore.role === 'admin') return '管理员';
  if (authStore.role === 'teacher') return '教师';
  return authStore.role || '-';
});

const loadStats = async () => {
  statsLoading.value = true;
  try {
    const [stu, tea, cls, hw, prob, sol, dis, sub] = await Promise.all([
      studentService.fetchList({ page: 1, size: 1 }),
      teacherService.fetchList({ page: 1, size: 1 }),
      classesService.fetchList({ page: 1, size: 1 }),
      homeworkService.fetchList({ page: 1, size: 1 }),
      problemService.fetchList({ page: 1, size: 1 }),
      solutionService.fetchList({ page: 1, size: 1 }),
      discussionService.fetchList({ page: 1, size: 1 }),
      submissionService.fetchList({ page: 1, size: 1 } as SubmissionQuery),
    ]);
    stats.students = stu.total || 0;
    stats.teachers = tea.total || 0;
    stats.classes = cls.total || 0;
    stats.homeworks = hw.total || 0;
    stats.problems = prob.total || 0;
    stats.solutions = sol.total || 0;
    stats.discussions = dis.total || 0;
    stats.submissions = sub.total || 0;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取统计数据失败'));
  } finally {
    statsLoading.value = false;
  }
};

const loadRecent = async () => {
  recentLoading.value = true;
  try {
    const data = await submissionService.fetchList({ page: 1, size: 5 } as SubmissionQuery);
    recentSubmissions.value = data.records || [];
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取近期提交失败'));
  } finally {
    recentLoading.value = false;
  }
};

const statusBadge = (code?: string) => {
  if (code === 'ACCEPTED') return 'success';
  if (code === 'WRONG') return 'error';
  return 'processing';
};

const go = (path: string) => router.push(path);

onMounted(() => {
  loadStats();
  loadRecent();
});
</script>

<style scoped lang="less">
.mb-16 {
  margin-bottom: 16px;
}

.stat-card {
  background: var(--card-bg);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  border-radius: 12px;
}

.stat-card__title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-muted, #8c8c8c);
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-color, #0f172a);
}

.stat-card__desc {
  margin-top: 4px;
  color: var(--text-muted, #8c8c8c);
  font-size: 12px;
}

.quick-link {
  padding: 10px 0;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-link:hover {
  color: #6366f1;
}

.quick-link__title {
  font-weight: 600;
}

.quick-link__desc {
  color: var(--text-muted, #8c8c8c);
  font-size: 12px;
}

.mt-16 {
  margin-top: 16px;
}
</style>
