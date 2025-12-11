<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form
        layout="vertical"
        :model="formState"
        :rules="rules"
        ref="formRef"
        class="student-edit-form"
      >
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" :disabled="isEdit" placeholder="请输入用户名" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
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
                  <a-avatar :src="avatarPreview" shape="square" :size="80" style="border-radius: 10px">
                    <template #icon>+</template>
                  </a-avatar>
                  <div class="avatar-upload__text">点击上传</div>
                </div>
              </a-upload>
              <div class="tip-text">当前前缀：{{ avatarPrefix }}</div>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item :label="passwordLabel" name="password">
              <a-input-password v-model:value="formState.password" :placeholder="passwordPlaceholder" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password v-model:value="formState.confirmPassword" placeholder="请再次输入密码" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="性别" name="sex">
              <a-select v-model:value="formState.sex" placeholder="请选择性别" allow-clear>
                <a-select-option value="male">男</a-select-option>
                <a-select-option value="female">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="出生日期" name="birth">
              <a-date-picker
                v-model:value="formState.birth"
                placeholder="选择出生日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="学校" name="school">
              <a-input v-model:value="formState.school" placeholder="请输入学校" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formState.phone" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="积分" name="score">
              <a-input-number v-model:value="formState.score" style="width: 100%" :min="0" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="24">
            <a-form-item label="个人简介" name="bio">
              <a-textarea v-model:value="formState.bio" :rows="3" placeholder="请输入个人简介" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">保存</a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Upload, type UploadProps } from 'ant-design-vue';
import type { FormInstance, FormProps } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { studentService } from '@/services/modules/student';
import type { StudentUpsertRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);
const formRef = ref<FormInstance>();
const submitting = ref(false);
const authStore = useAuthStore();
const avatarPrefixKey = 'studentAvatarPrefix';
const avatarPrefix = ref<string>(localStorage.getItem(avatarPrefixKey) || 'http://localhost:8080');
const uploadHeaders = computed(() => {
  const raw = authStore.token || '';
  const token = raw.replace(/^Bearer\s+/i, '');
  return {
    Authorization: token ? `Bearer ${token}` : '',
  };
});

type StudentFormState = StudentUpsertRequest & { confirmPassword?: string };

const formState = reactive<StudentFormState>({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  sex: '',
  birth: '',
  phone: '',
  email: '',
  school: '',
  score: 0,
  bio: '',
  avatar: '',
});
const avatarPreview = ref('');

const rules: FormProps['rules'] = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!isEdit.value && !value) return Promise.reject('请输入密码');
        if (value && (value.length < 6 || value.length > 100)) {
          return Promise.reject('密码长度需在 6-100 之间');
        }
        return Promise.resolve();
      },
    },
  ],
  confirmPassword: [
    {
      validator: (_rule: unknown, value: string) => {
        if (!formState.password && !value) return Promise.resolve();
        if (!value) return Promise.reject('请再次输入密码');
        if (value !== formState.password) return Promise.reject('两次输入的密码不一致');
        return Promise.resolve();
      },
      trigger: 'blur',
    },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
};

const passwordLabel = computed(() => (isEdit.value ? '密码（留空表示不修改）' : '密码'));
const passwordPlaceholder = computed(() => (isEdit.value ? '不修改可留空' : '请输入密码'));
const pageTitle = computed(() => (isEdit.value ? '编辑学生' : '新建学生'));

const loadDetail = async () => {
  if (!isEdit.value) return;
  try {
    if (!recordId.value) return;
    const data = await studentService.fetchDetail(recordId.value);
    formState.username = data.username;
    formState.name = data.name ?? '';
    formState.sex = data.sex ?? '';
    formState.birth = data.birth ?? '';
    formState.phone = data.phone ?? '';
    formState.email = data.email ?? '';
    formState.school = data.school ?? '';
    formState.score = data.score ?? 0;
    formState.bio = data.bio ?? '';
    formState.avatar = data.avatar ?? '';
    avatarPreview.value = resolveAvatar(data.avatar);
    formState.password = '';
    formState.confirmPassword = '';
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取详情失败'));
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: StudentUpsertRequest = {
      username: formState.username,
      name: formState.name,
      sex: formState.sex,
      birth: formState.birth || undefined,
      phone: formState.phone,
      email: formState.email,
      school: formState.school,
      score: formState.score,
      bio: formState.bio,
      avatar: resolveAvatar(formState.avatar),
    };
    if (formState.password || formState.confirmPassword) {
      if (formState.password !== formState.confirmPassword) {
        message.error('两次输入的密码不一致');
        submitting.value = false;
        return;
      }
      if (formState.password) {
        payload.password = formState.password;
      }
    }
    if (isEdit.value) {
      await studentService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      if (!formState.password) {
        message.error('请输入密码');
        submitting.value = false;
        return;
      }
      payload.password = formState.password;
      await studentService.create(payload);
      message.success('创建成功');
    }
    router.push('/admin/students');
  } catch (error: any) {
    message.error(extractErrorMessage(error, '提交失败'));
  } finally {
    submitting.value = false;
  }
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  loadDetail();
});

const beforeUpload: UploadProps['beforeUpload'] = async (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    message.error('请上传图片文件');
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
      img.onload = () => {
        resolve(img.width === img.height);
      };
      img.onerror = () => resolve(false);
      img.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  });

const handleUploadChange: UploadProps['onChange'] = async ({ file }) => {
  if (file.status === 'uploading') {
    return;
  }
  if (file.status === 'done' && file.response) {
    const urlSuffix = file.response?.data?.url || file.response?.data || file.response?.url || file.response;
    formState.avatar = urlSuffix;
    avatarPreview.value = resolveAvatar(urlSuffix);
    message.success('上传成功');
  } else if (file.status === 'error') {
    message.error('上传失败');
  }
};

const withPrefix = (url?: string) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return `${avatarPrefix.value}${url}`;
};

const resolveAvatar = (url?: unknown) => {
  const str = typeof url === 'string' ? url : '';
  if (!str) return `${avatarPrefix.value}/files/avatars/DefaultAvatar.jpg`;
  return withPrefix(str);
};
</script>

<style scoped lang="less">
.student-edit-form {
  max-width: 960px;
  margin: 0 auto;
}

.avatar-upload {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.avatar-upload__text {
  color: var(--text-color, rgba(0, 0, 0, 0.65));
  font-size: 12px;
}

.tip-text {
  margin-top: 4px;
  color: var(--text-color, rgba(0, 0, 0, 0.65));
  font-size: 12px;
}
</style>
