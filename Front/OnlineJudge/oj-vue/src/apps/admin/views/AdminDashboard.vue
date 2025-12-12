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

    <a-tabs v-model:activeKey="activeTab" type="card">
      <a-tab-pane key="overview" tab="概览">
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

        <template v-if="!isTeacher">
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :lg="14">
              <a-card title="近期提交" :loading="recentLoading" body-style="{ padding: '0 16px 16px' }">
                <div class="tab-tools">
                  <span>时间范围：</span>
                  <a-radio-group size="small" v-model:value="timeRange">
                    <a-radio-button value="24h">近24小时</a-radio-button>
                    <a-radio-button value="7d">近7天</a-radio-button>
                    <a-radio-button value="all">全部</a-radio-button>
                  </a-radio-group>
                </div>
                <a-table
                  size="small"
                  :columns="recentColumns"
                  :data-source="filteredSubmissions"
                  :pagination="false"
                  row-key="id"
                  :scroll="{ y: 'calc(100% - 40px)' }"
                >
                  <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'problemId'">
                      {{ problemLabel(record.problemId) }}
                    </template>
                    <template v-else-if="column.key === 'studentId'">
                      {{ studentLabel(record.studentId) }}
                    </template>
                    <template v-else-if="column.key === 'status'">
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
              <a-card title="提交质量摘要" :loading="recentLoading">
                <div class="quality-grid">
                  <div class="quality-item">
                    <div class="label">通过率</div>
                    <div class="value">{{ submissionSummary.passRate }}%</div>
                    <a-progress :percent="submissionSummary.passRate" size="small" status="active" />
                  </div>
                  <div class="quality-item">
                    <div class="label">AC</div>
                    <div class="value">{{ submissionSummary.accepted }}</div>
                  </div>
                  <div class="quality-item">
                    <div class="label">错误/编译</div>
                    <div class="value">{{ submissionSummary.failed }}</div>
                  </div>
                  <div class="quality-item">
                    <div class="label">运行中</div>
                    <div class="value">{{ submissionSummary.pending }}</div>
                  </div>
                </div>
              </a-card>

              <a-card title="快捷入口" class="mt-16" body-style="{ padding: '12px 16px' }">
                <a-list :data-source="visibleQuickLinks" :split="false">
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
        </template>
        <template v-else>
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :lg="14">
              <a-card title="班级作业进度" :loading="recentLoading">
                <a-list :data-source="teacherHomeworks" :locale="{ emptyText: '暂无作业' }">
                  <template #renderItem="{ item }">
                    <a-list-item>
                      <div class="homework-item">
                        <div>
                          <div class="homework-title">{{ item.title }}</div>
                          <div class="homework-desc">
                            <a-tag :color="progressColor(item.startTime, item.endTime)">
                              {{ progressLabel(item.startTime, item.endTime) }}
                            </a-tag>
                            <span class="muted" v-if="item.endTime">截止：{{ format(new Date(item.endTime), 'MM-dd HH:mm') }}</span>
                          </div>
                        </div>
                      </div>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>
            </a-col>
            <a-col :xs="24" :lg="10">
              <a-card title="提交榜（所授班级）" :loading="recentLoading">
                <a-list :data-source="topStudents" :locale="{ emptyText: '暂无提交' }" :split="false">
                  <template #renderItem="{ item }">
                    <a-list-item>
                      <div class="rank-item">
                        <div class="rank-title">{{ studentLabel(item.studentId) }}</div>
                        <div class="rank-desc">总提交 {{ item.total }} · AC {{ item.accepted }}</div>
                        <a-progress :percent="item.passRate" size="small" />
                      </div>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>

              <a-card title="快捷入口" class="mt-16" body-style="{ padding: '12px 16px' }">
                <a-list :data-source="visibleQuickLinks" :split="false">
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
        </template>
      </a-tab-pane>

      <a-tab-pane key="charts" tab="数据可视化">
        <a-card>
          <p class="muted">数据可视化模块预留，待后端统计接口完成后接入图表。</p>
        </a-card>
      </a-tab-pane>

      <a-tab-pane key="quality" tab="质量与排行">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :lg="14">
            <a-card title="高错误率题目（本周期）" :loading="recentLoading" body-style="{ padding: '0 16px 16px' }">
              <a-table
                :columns="problemColumns"
                :data-source="problemAlerts"
                :pagination="false"
                size="small"
                row-key="problemId"
                :scroll="{ y: 'calc(100% - 40px)' }"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'problemId'">
                    {{ problemLabel(record.problemId) }}
                  </template>
                </template>
              </a-table>
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="10">
            <a-card title="提交活跃学生 Top5" :loading="recentLoading">
                      <a-list :data-source="topStudents" :split="false">
                        <template #renderItem="{ item }">
                          <a-list-item>
                            <div class="rank-item">
                              <div class="rank-title">{{ studentLabel(item.studentId) }}</div>
                              <div class="rank-desc">总提交 {{ item.total }} · AC {{ item.accepted }}</div>
                              <a-progress :percent="item.passRate" size="small" />
                            </div>
                          </a-list-item>
                        </template>
              </a-list>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="content" tab="内容动态">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :lg="8">
            <a-card title="最新公告" :loading="contentLoading">
              <a-list :data-source="latestAnnouncements" :locale="{ emptyText: '暂无公告' }">
                <template #renderItem="{ item }">
                  <a-list-item>{{ item.title }}</a-list-item>
                </template>
              </a-list>
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="8">
            <a-card title="最新讨论" :loading="contentLoading">
              <a-list :data-source="latestDiscussions" :locale="{ emptyText: '暂无讨论' }">
                <template #renderItem="{ item }">
                  <a-list-item>{{ item.title }}</a-list-item>
                </template>
              </a-list>
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="8">
            <a-card title="最新题解" :loading="contentLoading">
              <a-list :data-source="latestSolutions" :locale="{ emptyText: '暂无题解' }">
                <template #renderItem="{ item }">
                  <a-list-item>{{ item.title }}</a-list-item>
                </template>
              </a-list>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="ops" tab="运营与个性化">
        <a-card title="运营提示">
          <ul class="bullet">
            <li>可在后端提供按时间范围的提交统计接口，以支持趋势图。</li>
            <li>建议增加“按班级/教师”过滤参数，前端联动切换统计卡。</li>
            <li>埋点接口响应时间与失败率，显示前端简易健康度。</li>
          </ul>
        </a-card>
        <a-card title="角色定制" class="mt-16">
          <ul class="bullet">
            <li>管理员优先展示全局数据；教师优先展示所授班级作业进度与提交榜。</li>
            <li>允许自定义快捷入口顺序（本地存储）。</li>
          </ul>
        </a-card>
      </a-tab-pane>
    </a-tabs>
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
import { fetchPublicAnnouncements } from '@/services/modules/announcement';
import type { Submission, SubmissionQuery, AnnouncementVO, Discussion, Solution, Homework } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const authStore = useAuthStore();
const router = useRouter();
const isTeacher = computed(() => authStore.role === 'teacher');

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
const submissionPool = ref<Submission[]>([]);
const contentLoading = ref(false);
const latestAnnouncements = ref<AnnouncementVO[]>([]);
const latestDiscussions = ref<Discussion[]>([]);
const latestSolutions = ref<Solution[]>([]);
const activeTab = ref('overview');
const timeRange = ref<'24h' | '7d' | 'all'>('24h');
const problemCache = reactive<Record<string, any>>({});
const studentCache = reactive<Record<string, any>>({});
const teacherHomeworks = ref<Homework[]>([]);

const statCards = computed(() =>
  isTeacher.value
    ? [
        { key: 'classes', title: '班级数', value: stats.classes, desc: '所授班级', icon: BookOutlined },
        { key: 'homeworks', title: '作业数', value: stats.homeworks, desc: '布置中的作业', icon: ScheduleOutlined },
        { key: 'submissions', title: '提交数', value: stats.submissions, desc: '近期提交', icon: LaptopOutlined },
        { key: 'problems', title: '题目数', value: stats.problems, desc: '题库量', icon: CodeOutlined },
      ]
    : [
        { key: 'students', title: '学生数', value: stats.students, desc: '活跃/总学生', icon: TeamOutlined },
        { key: 'teachers', title: '教师数', value: stats.teachers, desc: '可管理教师', icon: UserOutlined },
        { key: 'classes', title: '班级数', value: stats.classes, desc: '正在使用的班级', icon: BookOutlined },
        { key: 'homeworks', title: '作业数', value: stats.homeworks, desc: '布置中的作业', icon: ScheduleOutlined },
        { key: 'problems', title: '题目数', value: stats.problems, desc: '题库总量', icon: CodeOutlined },
        { key: 'submissions', title: '提交数', value: stats.submissions, desc: '历史提交总计', icon: LaptopOutlined },
        { key: 'solutions', title: '题解数', value: stats.solutions, desc: '已发布题解', icon: SolutionOutlined },
        { key: 'discussions', title: '讨论数', value: stats.discussions, desc: '讨论与回复', icon: CommentOutlined },
      ],
);

const recentColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 160 },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 160 },
  { title: '学生', dataIndex: 'studentId', key: 'studentId', width: 140 },
  { title: '状态', dataIndex: 'overallStatusCode', key: 'status', width: 120 },
  { title: '时间', dataIndex: 'createdAt', key: 'time', width: 120 },
];

const problemColumns = [
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 180 },
  { title: '错误率', dataIndex: 'errorRate', key: 'errorRate', width: 120, sorter: true },
  { title: '总提交', dataIndex: 'total', key: 'total', width: 100 },
  { title: 'AC', dataIndex: 'accepted', key: 'accepted', width: 80 },
];

const quickLinks = [
  { key: 'problems', title: '题目管理', desc: '维护题库、标签与限制', path: '/admin/problems' },
  { key: 'classes', title: '作业管理', desc: '布置/编辑班级作业', path: '/admin/classes' },
  { key: 'submissions', title: '提交记录', desc: '查看判题与重新提交', path: '/admin/submissions' },
  { key: 'content', title: '讨论/题解', desc: '社区内容审核与维护', path: '/admin/discussions' },
  { key: 'students', title: '学生管理', desc: '用户资料与授权', path: '/admin/students' },
  { key: 'teachers', title: '教师管理', desc: '教师账户与权限', path: '/admin/teachers' },
];
const visibleQuickLinks = computed(() =>
  isTeacher.value ? quickLinks.filter((l) => !['students', 'teachers'].includes(l.key)) : quickLinks,
);

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
    const data = await submissionService.fetchList({ page: 1, size: 50 } as SubmissionQuery);
    submissionPool.value = data.records || [];
    preloadCaches(submissionPool.value);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取近期提交失败'));
  } finally {
    recentLoading.value = false;
  }
};

const loadContent = async () => {
  contentLoading.value = true;
  try {
    const [anns, dis, sols] = await Promise.all([
      fetchPublicAnnouncements({ page: 1, size: 5, isPinned: false }),
      discussionService.fetchList({ page: 1, size: 5 }),
      solutionService.fetchList({ page: 1, size: 5 }),
    ]);
    latestAnnouncements.value = anns.records || [];
    latestDiscussions.value = dis.records || [];
    latestSolutions.value = sols.records || [];
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取内容动态失败'));
  } finally {
    contentLoading.value = false;
  }
};

const loadTeacherHomeworks = async () => {
  if (!isTeacher.value) return;
  try {
    const data = await homeworkService.fetchList({ page: 1, size: 8 });
    teacherHomeworks.value = data.records || [];
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级作业失败'));
  }
};

const statusBadge = (code?: string) => {
  if (code === 'ACCEPTED') return 'success';
  if (code === 'WRONG') return 'error';
  return 'processing';
};

const go = (path: string) => router.push(path);

const progressLabel = (start?: string, end?: string) => {
  if (!start && !end) return '未设置';
  const now = Date.now();
  const startAt = start ? new Date(start).getTime() : undefined;
  const endAt = end ? new Date(end).getTime() : undefined;
  if (startAt && now < startAt) return '未开始';
  if (endAt && now > endAt) return '已结束';
  return '进行中';
};

const progressColor = (start?: string, end?: string) => {
  const label = progressLabel(start, end);
  if (label === '未开始') return 'default';
  if (label === '进行中') return 'blue';
  if (label === '已结束') return 'red';
  return 'default';
};

const filteredSubmissions = computed(() => {
  if (!submissionPool.value.length) return [];
  const now = Date.now();
  const rangeMs = timeRange.value === '24h' ? 24 * 3600 * 1000 : timeRange.value === '7d' ? 7 * 24 * 3600 * 1000 : Infinity;
  return submissionPool.value.filter((s) => {
    if (!s.createdAt || timeRange.value === 'all') return true;
    const ts = new Date(s.createdAt).getTime();
    return now - ts <= rangeMs;
  });
});

const submissionSummary = computed(() => {
  const list = filteredSubmissions.value;
  const total = list.length || 0;
  const accepted = list.filter((s) => s.overallStatusCode === 'ACCEPTED').length;
  const failed = list.filter((s) => s.overallStatusCode === 'WRONG' || s.overallStatusId === 4).length;
  const pending = list.filter((s) => s.overallStatusCode !== 'ACCEPTED' && s.overallStatusCode !== 'WRONG' && s.overallStatusId !== 4).length;
  const passRate = total ? Math.round((accepted / total) * 100) : 0;
  return { total, accepted, failed, pending, passRate };
});

const problemAlerts = computed(() => {
  const agg: Record<string, { total: number; accepted: number }> = {};
  filteredSubmissions.value.forEach((s) => {
    if (!s.problemId) return;
    if (!agg[s.problemId]) agg[s.problemId] = { total: 0, accepted: 0 };
    agg[s.problemId].total += 1;
    if (s.overallStatusCode === 'ACCEPTED') agg[s.problemId].accepted += 1;
  });
  return Object.entries(agg)
    .map(([problemId, val]) => {
      const errorRate = val.total ? Math.round(((val.total - val.accepted) / val.total) * 100) : 0;
      return { problemId, total: val.total, accepted: val.accepted, errorRate };
    })
    .filter((item) => item.total >= 2)
    .sort((a, b) => b.errorRate - a.errorRate)
    .slice(0, 6);
});

const topStudents = computed(() => {
  const agg: Record<string, { total: number; accepted: number }> = {};
  filteredSubmissions.value.forEach((s) => {
    const id = (s as any).studentId || (s as any).userId;
    if (!id) return;
    if (!agg[id]) agg[id] = { total: 0, accepted: 0 };
    agg[id].total += 1;
    if (s.overallStatusCode === 'ACCEPTED') agg[id].accepted += 1;
  });
  return Object.entries(agg)
    .map(([studentId, val]) => ({
      studentId,
      total: val.total,
      accepted: val.accepted,
      passRate: val.total ? Math.round((val.accepted / val.total) * 100) : 0,
    }))
    .sort((a, b) => b.total - a.total)
    .slice(0, 5);
});

const problemLabel = (id?: string | number) => {
  if (!id) return '-';
  const val = problemCache[id as string];
  return val?.name ? `${val.name}（ID: ${id}）` : id;
};

const studentLabel = (id?: string | number) => {
  if (!id) return '-';
  const val = studentCache[id as string];
  if (val) return val.name ? `${val.username}（${val.name}）` : val.username;
  return id;
};

const preloadCaches = (records: Submission[]) => {
  const pIds = Array.from(new Set(records.map((r) => r.problemId).filter(Boolean) as string[]));
  const sIds = Array.from(
    new Set(records.map((r) => (r as any).studentId || (r as any).userId).filter(Boolean) as string[]),
  );
  pIds.forEach(async (id) => {
    if (problemCache[id]) return;
    try {
      const detail = await problemService.fetchDetail(id);
      problemCache[id] = detail;
    } catch {
      /* ignore */
    }
  });
  sIds.forEach(async (id) => {
    if (studentCache[id]) return;
    try {
      const detail = await studentService.fetchDetail(id);
      studentCache[id] = detail;
    } catch {
      /* ignore */
    }
  });
};

onMounted(() => {
  loadStats();
  loadRecent();
  loadContent();
  loadTeacherHomeworks();
});
</script>

<style scoped lang="less">
.dashboard {
  height: 100vh;
  background: var(--body-bg, #f8fafc);
}

.dashboard__wrap {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.header-row {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 2px 0;
}

.welcome {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: var(--text-color, #0f172a);
  font-size: 14px;
}

.welcome__role {
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(99, 102, 241, 0.12);
  color: #6366f1;
  font-size: 11px;
}

.stats-row {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 6px;
}

.stat {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 8px 10px;
  background: var(--card-bg, #fff);
  border-radius: 10px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06);
}

.stat__icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: rgba(99, 102, 241, 0.12);
  color: #6366f1;
  display: grid;
  place-items: center;
  font-size: 14px;
}

.stat__title {
  color: var(--text-muted, #6b7280);
  font-size: 12px;
}

.stat__value {
  font-weight: 700;
  color: var(--text-color, #0f172a);
  line-height: 1.1;
}

.stat__desc {
  color: var(--text-muted, #94a3b8);
  font-size: 11px;
}

.tabs-area {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.ant-tabs-content-holder) {
  flex: 1;
  min-height: 0;
}

:deep(.ant-tabs-content) {
  height: 100%;
}

:deep(.ant-tabs-tabpane) {
  height: 100%;
}

.tab-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.split {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 10px;
}

.stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.triple {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px;
}

.panel {
  display: flex;
  flex-direction: column;
  background: var(--card-bg, #fff);
  border-radius: 10px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.06);
  min-height: 0;
}

.panel__header {
  flex-shrink: 0;
  padding: 10px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel__title {
  font-weight: 600;
  color: var(--text-color, #0f172a);
}

.panel__body {
  padding: 0 14px 14px;
}

.scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 0 14px 14px;
}

.tab-tools {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 12px;
  color: var(--text-muted, #6b7280);
}

.quality-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.quality-item {
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 10px;
  padding: 10px;
}

.quality-item .label {
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.quality-item .value {
  font-weight: 700;
  color: var(--text-color, #0f172a);
  margin: 4px 0;
}

.quick-link {
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
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.homework-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.homework-title {
  font-weight: 600;
}

.homework-desc {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-muted, #94a3b8);
}

.muted {
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.rank-item {
  width: 100%;
}

.rank-title {
  font-weight: 600;
}

.rank-desc {
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-bottom: 4px;
}

.bullet {
  padding-left: 18px;
  color: var(--text-color, #0f172a);
}

.bullet li {
  margin-bottom: 6px;
}
</style>
