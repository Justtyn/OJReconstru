<template>
  <PageContainer title="学生主页">
    <div class="student-profile">
      <section class="profile-hero" :style="heroStyle" :class="{ 'profile-hero--has-bg': !!backgroundUrl }">
        <div class="profile-hero__content">
          <div class="profile-hero__main">
            <a-avatar :size="88" :src="detail?.avatar">
              <template #icon>
                <UserOutlined />
              </template>
            </a-avatar>
            <div class="profile-hero__text">
              <div class="profile-hero__name">{{ displayName }}</div>
              <div class="profile-hero__meta">
                <span>账号：{{ accountLabel }}</span>
                <span>邮箱：{{ emailLabel }}</span>
              </div>
              <div class="profile-hero__hint">上次登录：{{ lastLoginLabel }}</div>
            </div>
          </div>
          <div class="profile-hero__stats">
            <div class="profile-stat">
              <div class="profile-stat__label">提交通过率</div>
              <div class="profile-stat__value">
                <span class="profile-stat__main">{{ passRateText }}</span>
                <span class="profile-stat__pill">{{ passRateDetail }}</span>
              </div>
            </div>
            <div class="profile-stat">
              <div class="profile-stat__label">当前得分</div>
              <div class="profile-stat__value">
                <span class="profile-stat__main">{{ scoreLabel }}</span>
                <span class="profile-stat__unit">分</span>
              </div>
              <div class="profile-stat__desc">累计积分</div>
            </div>
            <div class="profile-stat">
              <div class="profile-stat__label">账号创建</div>
              <div class="profile-stat__value">
                <span class="profile-stat__main">{{ accountDaysLabel }}</span>
                <span class="profile-stat__unit">{{ accountDaysDesc }}</span>
              </div>
              <div class="profile-stat__desc">加入至今</div>
            </div>
          </div>
        </div>
      </section>

      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />

      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="16">
          <a-card class="profile-card" title="个人信息" :loading="loading">
            <a-descriptions :column="1" size="small" bordered>
              <a-descriptions-item label="用户名">{{ displayName }}</a-descriptions-item>
              <a-descriptions-item label="账号">{{ accountLabel }}</a-descriptions-item>
              <a-descriptions-item label="性别">{{ sexLabel }}</a-descriptions-item>
              <a-descriptions-item label="邮箱">{{ emailLabel }}</a-descriptions-item>
              <a-descriptions-item label="学校名称">{{ schoolLabel }}</a-descriptions-item>
              <a-descriptions-item label="得分">{{ scoreLabel }}</a-descriptions-item>
              <a-descriptions-item label="提交通过率">{{ passRateDetail }}</a-descriptions-item>
              <a-descriptions-item label="上次登录时间">{{ lastLoginLabel }}</a-descriptions-item>
              <a-descriptions-item label="上次提交语言">{{ lastLanguageLabel }}</a-descriptions-item>
              <a-descriptions-item label="账号创建至今">{{ accountDaysLabel }} {{ accountDaysDesc }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="8">
          <a-card class="profile-card" title="个人简介" :loading="loading">
            <div class="profile-bio">{{ bioLabel }}</div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import { UserOutlined } from '@ant-design/icons-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { studentService } from '@/services/modules/student';
import type { Student } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const route = useRoute();
const detail = ref<Student | null>(null);
const loading = ref(false);
const loadError = ref('');

const studentId = computed(() => route.params.id as string);

const displayName = computed(() => detail.value?.name || detail.value?.username || '-');
const accountLabel = computed(() => detail.value?.username || '-');
const emailLabel = computed(() => detail.value?.email || '-');
const schoolLabel = computed(() => detail.value?.school || '-');
const bioLabel = computed(() => detail.value?.bio || '暂无个人简介');
const lastLanguageLabel = computed(() => detail.value?.lastLanguage || '-');
const backgroundPrefix = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const resolveBackground = (value?: string | null) => {
  if (!value) return '';
  if (/^https?:\/\//.test(value)) return value;
  if (value.startsWith('/')) return `${backgroundPrefix}${value}`;
  if (value.includes('/')) return `${backgroundPrefix}/${value}`;
  return `${backgroundPrefix}/files/avatars/background/${value}`;
};
const backgroundUrl = computed(() => resolveBackground(detail.value?.background));
const heroStyle = computed(() => {
  if (!backgroundUrl.value) return {};
  return {
    backgroundImage: `url(${backgroundUrl.value})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  } as Record<string, string>;
});

const sexLabel = computed(() => {
  const sex = detail.value?.sex;
  if (!sex) return '未知';
  const normalized = sex.toLowerCase();
  if (['male', 'm', '男'].includes(normalized)) return '男';
  if (['female', 'f', '女'].includes(normalized)) return '女';
  return sex;
});

const passRatePercent = computed(() => {
  if (!detail.value) return 0;
  const submit = detail.value.submit ?? 0;
  if (!submit) return 0;
  return Math.round(((detail.value.ac ?? 0) / submit) * 100);
});

const passRateText = computed(() => (detail.value ? `${passRatePercent.value}%` : '-'));
const passRateDetail = computed(() => {
  if (!detail.value) return '-';
  return `${detail.value.ac ?? 0} / ${detail.value.submit ?? 0}`;
});
const scoreLabel = computed(() => (detail.value ? String(detail.value.score ?? 0) : '-'));

const lastLoginLabel = computed(() => formatTime(detail.value?.lastLoginTime || detail.value?.lastVisitTime));

const accountDaysLabel = computed(() => {
  const createdAt = detail.value?.createTime;
  if (!createdAt) return '-';
  const ts = new Date(createdAt).getTime();
  if (Number.isNaN(ts)) return '-';
  const days = Math.max(0, Math.floor((Date.now() - ts) / (24 * 60 * 60 * 1000)));
  return String(days);
});
const accountDaysDesc = computed(() => (accountDaysLabel.value === '-' ? '未记录' : '天'));

const formatTime = (value?: string | null) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

const loadDetail = async () => {
  if (!studentId.value) return;
  loading.value = true;
  loadError.value = '';
  try {
    detail.value = await studentService.fetchDetail(studentId.value);
  } catch (error) {
    const msg = extractErrorMessage(error, '获取学生信息失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadDetail();
});
</script>

<style scoped lang="less">
.student-profile {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.profile-hero {
  position: relative;
  padding: 28px 28px;
  min-height: 240px;
  border-radius: 18px;
  overflow: hidden;
  background-color: var(--card-bg);
  background: linear-gradient(135deg, rgba(56, 189, 248, 0.16), rgba(14, 116, 144, 0.16));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
}

.profile-hero__content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(240px, 1.4fr) minmax(220px, 1fr);
  gap: 20px;
  align-items: center;
}

.profile-hero__main {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.profile-hero__text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.profile-hero__name {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
}

.profile-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #64748b);
  font-size: 12px;
}

.profile-hero__hint {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.profile-hero__stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 160px));
  gap: 12px;
  justify-content: start;
}

.profile-stat {
  padding: 12px 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.12), rgba(59, 130, 246, 0.08));
  border: 1px solid rgba(14, 165, 233, 0.2);
  max-width: 180px;
}

.profile-stat__label {
  font-size: 12px;
  color: var(--text-muted, #64748b);
}

.profile-stat__value {
  margin-top: 6px;
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.profile-stat__main {
  font-size: 20px;
  font-weight: 600;
  color: #0ea5e9;
}

.profile-stat__unit {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.profile-stat__pill {
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid rgba(14, 165, 233, 0.3);
  background: rgba(14, 165, 233, 0.12);
  font-size: 12px;
  color: #0f172a;
}

.profile-stat__desc {
  font-size: 12px;
  margin-top: 4px;
  color: var(--text-muted, #94a3b8);
}

.profile-hero--has-bg .profile-hero__name,
.profile-hero--has-bg .profile-hero__meta,
.profile-hero--has-bg .profile-hero__hint,
.profile-hero--has-bg .profile-stat__label,
.profile-hero--has-bg .profile-stat__desc {
  color: rgba(248, 250, 252, 0.88);
  text-shadow: 0 2px 8px rgba(15, 23, 42, 0.45);
}

.profile-hero--has-bg .profile-hero__text {
  background: rgba(15, 23, 42, 0.32);
  padding: 8px 12px;
  border-radius: 12px;
}

.profile-hero--has-bg .profile-stat {
  background: rgba(15, 23, 42, 0.48);
  border-color: rgba(226, 232, 240, 0.28);
}

.profile-hero--has-bg .profile-stat__main {
  color: #f8fafc;
}

.profile-hero--has-bg .profile-stat__pill {
  border-color: rgba(226, 232, 240, 0.45);
  background: rgba(15, 23, 42, 0.35);
  color: #f8fafc;
}

.profile-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.profile-bio {
  line-height: 1.8;
  color: var(--text-color);
}

@media (max-width: 960px) {
  .profile-hero__content {
    grid-template-columns: 1fr;
  }
}

:global(:root[data-theme='dark']) .profile-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(8, 145, 178, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .profile-stat,
:global(:root[data-theme='dark']) .profile-card {
  border-color: rgba(56, 189, 248, 0.28);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, rgba(14, 116, 144, 0.3), rgba(15, 23, 42, 0.7));
}

:global(:root[data-theme='dark']) .profile-stat__pill {
  border-color: rgba(56, 189, 248, 0.4);
  background: rgba(56, 189, 248, 0.16);
  color: #e2e8f0;
}

:global(:root[data-theme='dark']) .profile-stat__main {
  color: #38bdf8;
}
</style>
