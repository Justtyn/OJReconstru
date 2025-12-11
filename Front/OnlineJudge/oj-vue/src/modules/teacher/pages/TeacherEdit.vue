<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form
          layout="vertical"
          :model="formState"
          :rules="rules"
          ref="formRef"
          class="admin-edit-form"
      >
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" :disabled="isEdit" placeholder="请输入用户名"/>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入姓名"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item :label="passwordLabel" name="password">
              <a-input-password v-model:value="formState.password" :placeholder="passwordPlaceholder"/>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password v-model:value="formState.confirmPassword" placeholder="请再次输入密码"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="职称" name="title">
              <a-auto-complete
                  v-model:value="formState.title"
                  :options="titleOptions"
                  allow-clear
                  placeholder="请选择或输入职称"
                  :filter-option="true"
              />
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
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formState.email" placeholder="请输入邮箱"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formState.phone" placeholder="请输入手机号"/>
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
import {computed, onMounted, reactive, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {message} from 'ant-design-vue';
import type {FormInstance, FormProps} from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import {teacherService} from '@/services/modules/teacher';
import type {TeacherUpsertRequest} from '@/types';
import {extractErrorMessage} from '@/utils/error';

const router = useRouter();
const route = useRoute();
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);
const formRef = ref<FormInstance>();
const submitting = ref(false);

type TeacherFormState = TeacherUpsertRequest & { confirmPassword?: string };

const formState = reactive<TeacherFormState>({
  username: '',
  password: '',
  name: '',
  sex: '',
  title: '',
  email: '',
  phone: '',
  confirmPassword: '',
});

const rules: FormProps['rules'] = {
  username: [{required: true, message: '请输入用户名'}],
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
  email: [
    {type: 'email', message: '邮箱格式不正确', trigger: 'blur'},
  ],
  phone: [
    {pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur'},
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
};

const titleOptions = [
  { value: '助教' },
  { value: '讲师' },
  { value: '副教授' },
  { value: '教授' },
  { value: '特聘教授' },
  { value: '外聘教师' },
  { value: '兼职教师' },
  { value: '高级工程师' },
  { value: '研究员' },
  { value: '首席专家' },
];

const passwordLabel = computed(() => (isEdit.value ? '密码（留空表示不修改）' : '密码'));
const passwordPlaceholder = computed(() => (isEdit.value ? '不修改可留空' : '请输入密码'));
const pageTitle = computed(() => (isEdit.value ? '编辑教师' : '新建教师'));

const loadDetail = async () => {
  if (!isEdit.value) return;
  try {
    if (!recordId.value) return;
    const data = await teacherService.fetchDetail(recordId.value);
    formState.username = data.username;
    formState.name = data.name ?? '';
    formState.sex = data.sex ?? '';
    formState.title = data.title ?? '';
    formState.email = data.email ?? '';
    formState.phone = data.phone ?? '';
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
    const payload: TeacherUpsertRequest = {
      username: formState.username,
      name: formState.name,
      sex: formState.sex,
      title: formState.title,
      email: formState.email,
      phone: formState.phone,
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
      await teacherService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      if (!formState.password) {
        message.error('请输入密码');
        submitting.value = false;
        return;
      }
      payload.password = formState.password;
      await teacherService.create(payload);
      message.success('创建成功');
    }
    router.push('/admin/teachers');
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
</script>
