<template>
  <PageContainer title="个人中心" subtitle="查看并管理你的账号信息与个性设置">
    <div class="profile-page">
      <section class="profile-hero" :style="heroStyle" :class="{ 'profile-hero--has-bg': !!backgroundUrl }">
        <div class="profile-hero__content">
          <div class="profile-hero__main">
            <div class="profile-hero__avatar">
              <a-avatar :size="96" :src="avatarUrl">
                <template #icon>
                  <UserOutlined />
                </template>
              </a-avatar>
            </div>
            <div class="profile-hero__text">
              <div class="profile-hero__name">
                <span>{{ displayName }}</span>
                <a-tag color="blue">学生</a-tag>
              </div>
              <div class="profile-hero__meta">
                <span>账号：{{ accountLabel }}</span>
                <span>邮箱：{{ emailLabel }}</span>
              </div>
              <div class="profile-hero__hint">
                上次登录：{{ lastLoginLabel }}
              </div>
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

      <a-alert
        v-if="loadError"
        type="error"
        show-icon
        :message="loadError"
        banner
      />

      <a-tabs v-model:activeKey="activeTab" type="card" class="profile-tabs">
        <a-tab-pane key="info" tab="信息">
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :lg="16">
              <a-card class="profile-card" title="个人信息" :loading="loading">
                <template v-if="!loading && studentDetail">
                  <a-descriptions :column="{ xs: 1, sm: 1, md: 2 }" size="small" bordered>
                    <a-descriptions-item label="用户名">{{ displayName }}</a-descriptions-item>
                    <a-descriptions-item label="账号">{{ accountLabel }}</a-descriptions-item>
                    <a-descriptions-item label="性别">{{ sexLabel }}</a-descriptions-item>
                    <a-descriptions-item label="邮箱">{{ emailLabel }}</a-descriptions-item>
                    <a-descriptions-item label="学校名称">{{ schoolLabel }}</a-descriptions-item>
                    <a-descriptions-item label="得分">{{ scoreLabel }}</a-descriptions-item>
                    <a-descriptions-item label="提交通过率">{{ passRateDetail }}</a-descriptions-item>
                    <a-descriptions-item label="上次登录时间">{{ lastLoginLabel }}</a-descriptions-item>
                    <a-descriptions-item label="上次提交语言">{{ lastLanguageLabel }}</a-descriptions-item>
                    <a-descriptions-item label="账号创建至今">{{ accountDaysDisplay }}</a-descriptions-item>
                  </a-descriptions>
                </template>
                <template v-else-if="!loading">
                  <a-empty description="暂无个人信息" />
                </template>
              </a-card>
            </a-col>
            <a-col :xs="24" :lg="8">
              <a-card class="profile-card" title="学习概览" :loading="loading">
                <template v-if="!loading && studentDetail">
                  <div class="profile-progress">
                    <div class="profile-progress__label">提交通过率</div>
                    <a-progress
                      :percent="passRatePercent"
                      :stroke-color="passRateColor"
                      :show-info="false"
                    />
                    <div class="profile-progress__meta">
                      {{ passRateText }}（{{ passRateDetail }}）
                    </div>
                  </div>
                  <div class="profile-metrics">
                    <div class="profile-metric">
                      <div class="profile-metric__value">{{ acLabel }}</div>
                      <div class="profile-metric__label">通过题数</div>
                    </div>
                    <div class="profile-metric">
                      <div class="profile-metric__value">{{ submitLabel }}</div>
                      <div class="profile-metric__label">总提交数</div>
                    </div>
                    <div class="profile-metric">
                      <div class="profile-metric__value">{{ lastLanguageLabel }}</div>
                      <div class="profile-metric__label">最近语言</div>
                    </div>
                    <div class="profile-metric">
                      <div class="profile-metric__value">{{ schoolLabel }}</div>
                      <div class="profile-metric__label">学校</div>
                    </div>
                  </div>
                </template>
                <template v-else-if="!loading">
                  <a-empty description="暂无学习概览" />
                </template>
              </a-card>
            </a-col>
          </a-row>

          <a-row :gutter="[16, 16]">
            <a-col :span="24">
              <a-card class="profile-card" title="个人简介" :loading="loading">
                <template v-if="!loading && studentDetail">
                  <div class="profile-bio">{{ bioLabel }}</div>
                </template>
                <template v-else-if="!loading">
                  <a-empty description="暂无个人简介" />
                </template>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>

        <a-tab-pane key="background" tab="背景">
          <a-card class="profile-card" title="背景图">
            <div class="profile-background__hint">
              已提供 12 张背景图，点击即可设置个人主页背景。
            </div>
            <div class="profile-background">
              <div
                v-for="item in backgroundOptions"
                :key="item.id"
                class="background-card"
                :class="{
                  'background-card--active': isBackgroundActive(item.url),
                  'background-card--loading': backgroundSubmitting && pendingBackgroundId === item.id,
                }"
                role="button"
                :aria-label="item.label"
                tabindex="0"
                @click="applyBackground(item)"
                @keydown.enter="applyBackground(item)"
                @keydown.space.prevent="applyBackground(item)"
              >
                <div class="background-card__thumb">
                  <img :src="item.url" :alt="item.label" loading="lazy" decoding="async" />
                </div>
              </div>
            </div>
          </a-card>
        </a-tab-pane>

        <a-tab-pane key="settings" tab="设置">
          <a-row :gutter="[16, 16]">
            <a-col :xs="24" :md="12">
              <a-card class="profile-card" title="修改个人信息">
                <p class="profile-muted">更新用户名、性别、学校、邮箱、头像与个人简介。</p>
                <a-button type="primary" :disabled="!studentDetail" @click="openEditProfile">修改信息</a-button>
              </a-card>
            </a-col>
            <a-col :xs="24" :md="12">
              <a-card class="profile-card" title="修改密码">
                <p class="profile-muted">修改密码后请使用新密码重新登录。</p>
                <a-button type="primary" danger @click="openChangePassword">修改密码</a-button>
              </a-card>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </div>

    <a-modal
      v-model:open="editProfileVisible"
      title="修改个人信息"
      :confirm-loading="profileSubmitting"
      destroy-on-close
      @ok="handleProfileSubmit"
      @cancel="editProfileVisible = false"
    >
      <a-form ref="profileFormRef" layout="vertical" :model="profileForm" :rules="profileRules">
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="账号" name="username">
              <a-input v-model:value="profileForm.username" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="用户名" name="name">
              <a-input v-model:value="profileForm.name" placeholder="请输入用户名" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="性别" name="sex">
              <a-select v-model:value="profileForm.sex" placeholder="请选择性别" allow-clear>
                <a-select-option value="male">男</a-select-option>
                <a-select-option value="female">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="profileForm.email" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="学校名称" name="school">
              <a-select
                v-model:value="profileForm.school"
                show-search
                allow-clear
                :options="schoolOptions"
                :loading="schoolLoading"
                placeholder="搜索或选择学校"
                :filter-option="false"
                @search="handleSchoolSearch"
                @dropdownVisibleChange="handleSchoolDropdown"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="头像" name="avatar">
              <a-upload
                :action="`${avatarPrefix}/api/files/avatar`"
                name="file"
                accept="image/*"
                :show-upload-list="false"
                :before-upload="beforeUpload"
                :headers="uploadHeaders"
                @change="handleUploadChange"
              >
                <div class="avatar-upload">
                  <a-avatar :src="avatarPreview" shape="square" :size="72" style="border-radius: 10px">
                    <template #icon>+</template>
                  </a-avatar>
                  <div class="avatar-upload__text">点击上传</div>
                </div>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="个人简介" name="bio">
          <a-textarea v-model:value="profileForm.bio" :rows="3" placeholder="请输入个人简介" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="changePasswordVisible"
      title="修改密码"
      :confirm-loading="passwordSubmitting"
      destroy-on-close
      @ok="handlePasswordSubmit"
      @cancel="changePasswordVisible = false"
    >
      <a-form ref="passwordFormRef" layout="vertical" :model="passwordForm" :rules="passwordRules">
        <a-form-item label="旧密码" name="oldPassword">
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入旧密码" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认新密码" name="confirmPassword">
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { message, Upload } from 'ant-design-vue';
import type { FormInstance, FormProps, UploadProps } from 'ant-design-vue';
import { format } from 'date-fns';
import { UserOutlined } from '@ant-design/icons-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { useAuthStore } from '@/stores/auth';
import { studentService } from '@/services/modules/student';
import { authService } from '@/services/modules/auth';
import type { ChangePasswordRequest, Student, StudentUpsertRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const authStore = useAuthStore();
const activeTab = ref('info');
const studentDetail = ref<Student | null>(null);
const loading = ref(false);
const loadError = ref('');
const editProfileVisible = ref(false);
const changePasswordVisible = ref(false);
const profileSubmitting = ref(false);
const passwordSubmitting = ref(false);

const profileFormRef = ref<FormInstance>();
const passwordFormRef = ref<FormInstance>();

const profileForm = reactive({
  username: '',
  name: '',
  sex: undefined as string | undefined,
  email: '',
  school: '',
  avatar: '',
  bio: '',
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const profileRules: FormProps['rules'] = {
  username: [{ required: true, message: '账号不可为空' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
};

const passwordRules: FormProps['rules'] = {
  oldPassword: [{ required: true, message: '请输入旧密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, max: 100, message: '密码长度需在 6-100 之间', trigger: 'blur' },
  ],
  confirmPassword: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!value) return Promise.reject('请确认新密码');
        if (value !== passwordForm.newPassword) return Promise.reject('两次输入的密码不一致');
        return Promise.resolve();
      },
      trigger: 'blur',
    },
  ],
};

const avatarPrefix = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const MAX_AVATAR_SIZE = 1024 * 1024;
const avatarPreview = ref('');
const uploadHeaders = computed(() => {
  const raw = authStore.token || '';
  const token = raw.replace(/^Bearer\s+/i, '');
  return {
    Authorization: token ? `Bearer ${token}` : '',
  };
});

const backgroundPrefix = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const resolveBackground = (value?: string | null) => {
  if (!value) return '';
  if (/^https?:\/\//.test(value)) return value;
  if (value.startsWith('/')) return `${backgroundPrefix}${value}`;
  if (value.includes('/')) return `${backgroundPrefix}/${value}`;
  return `${backgroundPrefix}/files/avatars/background/${value}`;
};
const backgroundOptions = computed(() =>
  Array.from({ length: 12 }, (_, index) => {
    const order = String(index + 1).padStart(2, '0');
    return {
      id: index + 1,
      label: `背景 ${order}`,
      url: `${backgroundPrefix}/files/avatars/background/BackImg${order}.jpg`,
    };
  })
);
const backgroundCache = new Map<string, HTMLImageElement>();
const backgroundPrefetched = ref(false);
const cacheBackground = (url: string) => {
  if (backgroundCache.has(url)) return;
  const img = new Image();
  img.src = url;
  backgroundCache.set(url, img);
};
const prefetchBackgrounds = () => {
  if (backgroundPrefetched.value) return;
  backgroundPrefetched.value = true;
  const task = () => {
    backgroundOptions.value.forEach((item) => cacheBackground(item.url));
  };
  if (typeof window !== 'undefined' && 'requestIdleCallback' in window) {
    (window as Window & { requestIdleCallback?: (cb: () => void) => void }).requestIdleCallback?.(task);
  } else {
    setTimeout(task, 0);
  }
};

const avatarUrl = computed(() => studentDetail.value?.avatar || authStore.user?.avatar || '');
const backgroundUrl = computed(() => resolveBackground(studentDetail.value?.background));
const displayName = computed(
  () => studentDetail.value?.name || studentDetail.value?.username || authStore.user?.username || '-'
);
const accountLabel = computed(() => studentDetail.value?.username || authStore.user?.username || '-');
const emailLabel = computed(() => studentDetail.value?.email || authStore.user?.email || '-');
const schoolLabel = computed(() => studentDetail.value?.school || '-');
const bioLabel = computed(() => studentDetail.value?.bio || '暂无个人简介');
const acLabel = computed(() => (studentDetail.value ? String(studentDetail.value.ac ?? 0) : '-'));
const submitLabel = computed(() => (studentDetail.value ? String(studentDetail.value.submit ?? 0) : '-'));
const lastLanguageLabel = computed(() => studentDetail.value?.lastLanguage || '-');

const sexLabel = computed(() => {
  const sex = studentDetail.value?.sex;
  if (!sex) return '未知';
  const normalized = sex.toLowerCase();
  if (['male', 'm', '男'].includes(normalized)) return '男';
  if (['female', 'f', '女'].includes(normalized)) return '女';
  return sex;
});

const passRatePercent = computed(() => {
  if (!studentDetail.value) return 0;
  const submit = studentDetail.value.submit ?? 0;
  if (!submit) return 0;
  return Math.round(((studentDetail.value.ac ?? 0) / submit) * 100);
});

const passRateText = computed(() => (studentDetail.value ? `${passRatePercent.value}%` : '-'));
const passRateDetail = computed(() => {
  if (!studentDetail.value) return '-';
  const ac = studentDetail.value.ac ?? 0;
  const submit = studentDetail.value.submit ?? 0;
  return `${ac} / ${submit}`;
});
const passRateColor = computed(() => {
  if (passRatePercent.value >= 75) return '#22c55e';
  if (passRatePercent.value >= 45) return '#f59e0b';
  return '#ef4444';
});
const scoreLabel = computed(() => (studentDetail.value ? String(studentDetail.value.score ?? 0) : '-'));

const lastLoginLabel = computed(() => formatTime(studentDetail.value?.lastLoginTime || studentDetail.value?.lastVisitTime));

const accountDaysLabel = computed(() => {
  const createdAt = studentDetail.value?.createTime;
  if (!createdAt) return '-';
  const ts = new Date(createdAt).getTime();
  if (Number.isNaN(ts)) return '-';
  const days = Math.max(0, Math.floor((Date.now() - ts) / (24 * 60 * 60 * 1000)));
  return String(days);
});

const accountDaysDisplay = computed(() => (accountDaysLabel.value === '-' ? '-' : `${accountDaysLabel.value} 天`));
const accountDaysDesc = computed(() => (accountDaysLabel.value === '-' ? '未记录' : '天'));

const heroStyle = computed(() => {
  if (!backgroundUrl.value) return {};
  return {
    backgroundImage: `url(${backgroundUrl.value})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  } as Record<string, string>;
});

const resolveAvatar = (value?: string | null) => {
  if (!value) return `${avatarPrefix}/files/avatars/DefaultAvatar.jpg`;
  if (value.startsWith('http')) return value;
  if (value.startsWith('/')) return `${avatarPrefix}${value}`;
  return `${avatarPrefix}/${value}`;
};

const beforeUpload: UploadProps['beforeUpload'] = async (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    message.error('请上传图片文件');
    return Upload.LIST_IGNORE;
  }
  if (file.size > MAX_AVATAR_SIZE) {
    message.error('头像大小不能超过 1MB');
    return Upload.LIST_IGNORE;
  }
  const isSquare = await checkSquare(file);
  if (!isSquare) {
    message.error('请上传正方形图片');
    return Upload.LIST_IGNORE;
  }
  return true;
};

const checkSquare = (file: File): Promise<boolean> =>
  new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => resolve(img.width === img.height);
      img.onerror = () => resolve(false);
      img.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  });

const handleUploadChange: UploadProps['onChange'] = ({ file }) => {
  if (file.status === 'uploading') return;
  if (file.status === 'done' && file.response) {
    const resp: any = file.response;
    const urlSuffix = resp?.data?.url || resp?.data || resp?.url || resp;
    profileForm.avatar = urlSuffix || '';
    avatarPreview.value = resolveAvatar(profileForm.avatar);
    message.success('上传成功');
  } else if (file.status === 'error') {
    message.error('上传失败');
  }
};

const allSchools = ref<{ label: string; value: string; keyword: string }[]>([]);
const schoolOptions = ref<{ label: string; value: string }[]>([]);
const schoolLoading = ref(false);
const schoolsLoaded = ref(false);

const stripLevelSuffix = (text: string) =>
  text.replace(/\s*[\(（][^()（）]*?(本科|专科)[^()（）]*?[\)）]\s*$/i, '').trim();

const parseSchools = (rawText: string) => {
  try {
    const parsed = JSON.parse(rawText || '{}');
    const rawList: string =
      typeof parsed.schools === 'string' ? parsed.schools : Array.isArray(parsed.schools) ? parsed.schools.join(',') : '';
    const seen = new Set<string>();
    return rawList
      .split(',')
      .map((item: string) => item.trim())
      .filter(Boolean)
      .map((item: string) => {
        const base = stripLevelSuffix(item);
        return { label: base, value: base, keyword: `${base}${item}`.toLowerCase() };
      })
      .filter((item) => {
        if (seen.has(item.value)) return false;
        seen.add(item.value);
        return true;
      });
  } catch (error) {
    console.error('parse schools error', error);
    return [];
  }
};

const loadSchools = async () => {
  if (schoolsLoaded.value) return;
  schoolLoading.value = true;
  try {
    const response = await fetch(`${import.meta.env.BASE_URL}schools.json.txt`);
    const text = await response.text();
    allSchools.value = parseSchools(text);
    schoolOptions.value = filterSchools('');
    ensureSchoolOption(profileForm.school);
    schoolsLoaded.value = true;
  } catch (error) {
    message.error('加载学校列表失败');
  } finally {
    schoolLoading.value = false;
  }
};

const filterSchools = (keyword: string) => {
  const lower = keyword.trim().toLowerCase();
  const filtered = lower
    ? allSchools.value.filter((item) => item.keyword.includes(lower) || item.label.toLowerCase().includes(lower))
    : allSchools.value;
  return filtered.slice(0, 80).map(({ label, value }) => ({ label, value }));
};

const handleSchoolSearch = (value: string) => {
  schoolOptions.value = filterSchools(value);
};

const handleSchoolDropdown = (open: boolean) => {
  if (open && !schoolsLoaded.value) {
    loadSchools();
  }
};

const ensureSchoolOption = (value?: string) => {
  if (!value) return;
  if (!allSchools.value.length) {
    allSchools.value = [{ label: value, value, keyword: value.toLowerCase() }];
  } else if (!allSchools.value.some((item) => item.value === value)) {
    allSchools.value.unshift({ label: value, value, keyword: value.toLowerCase() });
  }
  if (!schoolOptions.value.some((item) => item.value === value)) {
    schoolOptions.value = [{ label: value, value }, ...schoolOptions.value].slice(0, 80);
  }
};

const backgroundSubmitting = ref(false);
const pendingBackgroundId = ref<number | null>(null);
const isBackgroundActive = (url: string) => backgroundUrl.value === url;

const applyBackground = async (item: { id: number; url: string }) => {
  if (backgroundSubmitting.value) return;
  if (!studentDetail.value) {
    message.warning('个人信息尚未加载完成');
    return;
  }
  cacheBackground(item.url);
  if (isBackgroundActive(item.url)) {
    message.info('当前已使用该背景');
    return;
  }
  const username = studentDetail.value.username || authStore.user?.username;
  if (!authStore.user?.id || !username) {
    message.error('未获取到账号信息');
    return;
  }
  backgroundSubmitting.value = true;
  pendingBackgroundId.value = item.id;
  try {
    const detail = studentDetail.value;
    const payload: StudentUpsertRequest = {
      username,
      name: detail.name ?? undefined,
      sex: detail.sex ?? undefined,
      email: detail.email ?? undefined,
      school: detail.school ?? undefined,
      avatar: detail.avatar ?? undefined,
      bio: detail.bio ?? undefined,
      birth: detail.birth ?? undefined,
      phone: detail.phone ?? undefined,
      background: item.url,
      score: detail.score ?? undefined,
    };
    const updated = await studentService.update(authStore.user.id, payload);
    studentDetail.value = updated;
    message.success('背景已更新');
  } catch (error) {
    message.error(extractErrorMessage(error, '更新背景失败'));
  } finally {
    backgroundSubmitting.value = false;
    pendingBackgroundId.value = null;
  }
};

const formatTime = (value?: string | null) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

const loadStudent = async (id: string) => {
  loading.value = true;
  loadError.value = '';
  try {
    const data = await studentService.fetchDetail(id);
    studentDetail.value = data;
  } catch (error) {
    loadError.value = extractErrorMessage(error, '获取个人信息失败');
    message.error(loadError.value);
  } finally {
    loading.value = false;
  }
};

const ensureProfile = async () => {
  if (!authStore.user && authStore.token) {
    try {
      await authStore.fetchProfile();
    } catch (error) {
      console.warn('Failed to load auth profile', error);
    }
  }
};

const openEditProfile = async () => {
  if (!studentDetail.value) {
    message.warning('个人信息尚未加载完成');
    return;
  }
  profileForm.username = studentDetail.value.username || authStore.user?.username || '';
  profileForm.name = studentDetail.value.name ?? '';
  profileForm.sex = studentDetail.value.sex ?? undefined;
  profileForm.email = studentDetail.value.email ?? '';
  profileForm.school = studentDetail.value.school ?? '';
  profileForm.avatar = studentDetail.value.avatar ?? '';
  profileForm.bio = studentDetail.value.bio ?? '';
  avatarPreview.value = resolveAvatar(profileForm.avatar);
  ensureSchoolOption(profileForm.school);
  loadSchools();
  editProfileVisible.value = true;
  await nextTick();
  profileFormRef.value?.clearValidate();
};

const handleProfileSubmit = async () => {
  if (!profileFormRef.value) return;
  try {
    await profileFormRef.value.validate();
    if (!authStore.user?.id) {
      message.error('未获取到账号信息');
      return;
    }
    profileSubmitting.value = true;
    const detail = studentDetail.value;
    const payload: StudentUpsertRequest = {
      username: profileForm.username.trim(),
      name: profileForm.name.trim(),
      sex: profileForm.sex,
      email: detail?.email ?? profileForm.email.trim(),
      school: profileForm.school.trim(),
      avatar: resolveAvatar(profileForm.avatar.trim()),
      bio: profileForm.bio.trim(),
      birth: detail?.birth ?? undefined,
      phone: detail?.phone ?? undefined,
      background: detail?.background ?? undefined,
      score: detail?.score ?? undefined,
    };
    const updated = await studentService.update(authStore.user.id, payload);
    studentDetail.value = updated;
    await authStore.fetchProfile();
    message.success('个人信息已更新');
    editProfileVisible.value = false;
  } catch (error) {
    message.error(extractErrorMessage(error, '更新个人信息失败'));
  } finally {
    profileSubmitting.value = false;
  }
};

const openChangePassword = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  changePasswordVisible.value = true;
};

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return;
  try {
    await passwordFormRef.value.validate();
    passwordSubmitting.value = true;
    const payload: ChangePasswordRequest = {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    };
    await authService.changePassword(payload);
    message.success('密码修改成功，请使用新密码登录');
    changePasswordVisible.value = false;
  } catch (error) {
    message.error(extractErrorMessage(error, '修改密码失败'));
  } finally {
    passwordSubmitting.value = false;
  }
};

onMounted(() => {
  ensureProfile();
});

watch(
  () => authStore.user?.id,
  (id) => {
    if (id) loadStudent(id);
  },
  { immediate: true }
);

watch(
  () => activeTab.value,
  (tab) => {
    if (tab === 'background') {
      prefetchBackgrounds();
    }
  }
);
</script>

<style scoped lang="less">
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-hero {
  position: relative;
  padding: 32px 32px;
  min-height: 250px;
  border-radius: 18px;
  overflow: hidden;
  background-color: var(--card-bg);
  background-image: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(14, 165, 233, 0.2));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.1);
}

.profile-hero__content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(240px, 1.3fr) minmax(220px, 1fr);
  gap: 24px;
  align-items: center;
}

.profile-hero__main {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-wrap: wrap;
}

.profile-hero__avatar :deep(.ant-avatar) {
  border: 3px solid var(--card-bg);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.18);
}

.profile-hero__text {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.profile-hero__name {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: 600;
  color: var(--text-color);
}

.profile-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--text-muted, #64748b);
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

.profile-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 12px;
}

.profile-card {
  border-radius: 14px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
}

.profile-progress {
  padding-bottom: 16px;
  border-bottom: 1px dashed rgba(15, 23, 42, 0.12);
}

.profile-progress__label {
  font-size: 13px;
  color: var(--text-muted, #64748b);
  margin-bottom: 6px;
}

.profile-progress__meta {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.profile-metrics {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  gap: 12px;
}

.profile-metric {
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--body-bg);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.profile-metric__value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  word-break: break-word;
}

.profile-metric__label {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.profile-bio {
  line-height: 1.8;
  color: var(--text-color);
  padding: 4px 0;
}

.profile-background__hint {
  color: var(--text-muted, #94a3b8);
  margin-bottom: 12px;
}

.profile-background {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 18px;
}

.background-card {
  border-radius: 12px;
  overflow: hidden;
  background: var(--body-bg);
  border: 1px solid rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.background-card--active {
  border-color: rgba(59, 130, 246, 0.7);
  box-shadow: 0 14px 28px rgba(59, 130, 246, 0.2);
}

.background-card--loading {
  cursor: progress;
  opacity: 0.7;
}

.background-card:focus-visible {
  outline: 2px solid rgba(59, 130, 246, 0.6);
  outline-offset: 2px;
}

.background-card__thumb {
  position: relative;
  display: block;
  aspect-ratio: 3 / 2;
  background-color: rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.background-card__thumb img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center;
}

.profile-muted {
  color: var(--text-muted, #94a3b8);
  margin-bottom: 12px;
}

.avatar-upload {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.avatar-upload__text {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

@media (max-width: 960px) {
  .profile-hero__content {
    grid-template-columns: 1fr;
  }
}

:global(:root[data-theme='dark']) .profile-hero {
  background-image: linear-gradient(135deg, rgba(30, 41, 59, 0.92), rgba(15, 23, 42, 0.95));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.36);
}

:global(:root[data-theme='dark']) .profile-stat {
  background: linear-gradient(135deg, rgba(14, 116, 144, 0.3), rgba(15, 23, 42, 0.7));
  border-color: rgba(56, 189, 248, 0.28);
}

:global(:root[data-theme='dark']) .profile-stat__pill {
  border-color: rgba(56, 189, 248, 0.4);
  background: rgba(56, 189, 248, 0.16);
  color: #e2e8f0;
}

:global(:root[data-theme='dark']) .profile-stat__main {
  color: #38bdf8;
}

:global(:root[data-theme='dark']) .profile-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.32);
}

:global(:root[data-theme='dark']) .profile-progress {
  border-bottom-color: rgba(148, 163, 184, 0.2);
}

:global(:root[data-theme='dark']) .profile-metric {
  background: rgba(15, 23, 42, 0.7);
  border-color: rgba(148, 163, 184, 0.2);
}

:global(:root[data-theme='dark']) .background-card {
  border-color: rgba(148, 163, 184, 0.2);
}

:global(:root[data-theme='dark']) .background-card__thumb {
  background-color: rgba(2, 6, 23, 0.35);
}

:global(:root[data-theme='dark']) .background-card--active {
  border-color: rgba(56, 189, 248, 0.6);
  box-shadow: 0 16px 30px rgba(8, 145, 178, 0.35);
}
</style>
