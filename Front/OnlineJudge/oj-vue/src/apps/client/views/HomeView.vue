<template>
  <PageContainer title="欢迎" subtitle="进入你的学习空间，开启新的解题旅程">
    <div class="home-page">
      <section class="home-hero">
        <div class="home-hero__content">
          <div class="home-hero__kicker">Re Online Judge</div>
          <div class="home-hero__title">欢迎回来，{{ displayName }}</div>
          <p class="home-hero__desc">
            在题库中选择目标、提交代码并追踪进度，随时在题解与讨论区分享你的思考。
          </p>
          <div class="home-hero__tags">
            <a-tag color="geekblue">角色：{{ roleLabel }}</a-tag>
            <a-tag color="blue">账号：{{ authStore.user?.username || '-' }}</a-tag>
            <a-tag color="green">状态：在线</a-tag>
          </div>
          <div class="home-hero__actions">
            <a-button type="primary" @click="goTo('/problems')">开始刷题</a-button>
            <a-button @click="goTo('/submissions')">查看提交</a-button>
          </div>
        </div>
        <div class="home-hero__panel">
          <div class="home-hero__panel-title">快捷入口</div>
          <div class="home-hero__grid">
            <button
              v-for="item in quickLinks"
              :key="item.key"
              type="button"
              class="home-entry"
              @click="goTo(item.path)"
            >
              <div class="home-entry__icon">
                <component :is="item.icon" />
              </div>
              <div class="home-entry__text">
                <div class="home-entry__title">{{ item.title }}</div>
                <div class="home-entry__desc">{{ item.desc }}</div>
              </div>
            </button>
          </div>
        </div>
      </section>

      <section class="home-section">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :lg="8">
            <a-card class="home-card" title="账户概览">
              <div class="home-profile">
                <a-avatar :size="64" :src="authStore.user?.avatar">
                  <template #icon>
                    <UserOutlined />
                  </template>
                </a-avatar>
                <div class="home-profile__info">
                  <div class="home-profile__name">{{ displayName }}</div>
                  <div class="home-profile__meta">账号：{{ authStore.user?.username || '-' }}</div>
                  <div class="home-profile__meta">身份：{{ roleLabel }}</div>
                </div>
              </div>
              <div class="home-profile__actions">
                <a-button type="primary" @click="goTo('/profile')">个人中心</a-button>
                <a-button @click="goTo('/ranking')">查看排行</a-button>
              </div>
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="16">
            <a-card class="home-card" title="学习指引">
              <a-steps direction="vertical" :items="guideSteps" />
              <div class="home-guide__actions">
                <a-button type="primary" @click="goTo('/solutions')">阅读题解</a-button>
                <a-button @click="goTo('/discussions')">进入讨论</a-button>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </section>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { UserOutlined, CodeOutlined, FileSearchOutlined, TrophyOutlined, BookOutlined, MessageOutlined, ScheduleOutlined } from '@ant-design/icons-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const displayName = computed(() => authStore.user?.username || '同学');
const roleLabel = computed(() => {
  const role = authStore.role;
  if (role === 'student') return '学生';
  if (role === 'teacher') return '教师';
  if (role === 'admin') return '管理员';
  return '访客';
});

const quickLinks = [
  { key: 'problems', title: '题库', desc: '筛选题目并提交', path: '/problems', icon: CodeOutlined },
  { key: 'submissions', title: '提交记录', desc: '追踪评测状态', path: '/submissions', icon: FileSearchOutlined },
  { key: 'ranking', title: '排行榜', desc: '查看成绩排名', path: '/ranking', icon: TrophyOutlined },
  { key: 'solutions', title: '题解', desc: '阅读解题思路', path: '/solutions', icon: BookOutlined },
  { key: 'discussions', title: '讨论', desc: '交流解题想法', path: '/discussions', icon: MessageOutlined },
  { key: 'homeworks', title: '作业', desc: '班级作业进度', path: '/homeworks', icon: ScheduleOutlined },
];

const guideSteps = [
  { title: '选择目标', description: '在题库中挑选题目，关注时间与内存限制。' },
  { title: '提交代码', description: '编写解答并提交，系统自动完成评测。' },
  { title: '复盘提升', description: '查看题解与讨论，总结方法并持续优化。' },
];

const goTo = (path: string) => {
  router.push(path);
};
</script>

<style scoped lang="less">
.home-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.home-hero {
  padding: 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.18), rgba(34, 197, 94, 0.16));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.1);
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  gap: 24px;
}

.home-hero__content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.home-hero__kicker {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted, #64748b);
}

.home-hero__title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.home-hero__desc {
  margin: 0;
  color: var(--text-color);
  opacity: 0.8;
  line-height: 1.7;
}

.home-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.home-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.home-hero__panel {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.home-hero__panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
}

.home-hero__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.home-entry {
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: var(--body-bg);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  text-align: left;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.home-entry:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.12);
}

.home-entry__icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(14, 165, 233, 0.12);
  color: #0ea5e9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex: 0 0 auto;
}

.home-entry__title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
}

.home-entry__desc {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.home-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.home-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
}

.home-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.home-profile__info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.home-profile__name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.home-profile__meta {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.home-profile__actions,
.home-guide__actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .home-hero {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .home-hero__grid {
    grid-template-columns: 1fr;
  }
}

:global(:root[data-theme='dark']) .home-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(22, 163, 74, 0.32));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .home-hero__panel,
:global(:root[data-theme='dark']) .home-card,
:global(:root[data-theme='dark']) .home-entry {
  border-color: rgba(148, 163, 184, 0.2);
  background: rgba(15, 23, 42, 0.7);
  box-shadow: 0 16px 30px rgba(0, 0, 0, 0.32);
}

:global(:root[data-theme='dark']) .home-entry__icon {
  background: rgba(56, 189, 248, 0.18);
  color: #38bdf8;
}
</style>
