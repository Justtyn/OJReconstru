<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form
        layout="vertical"
        :model="formState"
        :rules="rules"
        ref="formRef"
        @submit.prevent="handleSubmit"
        class="admin-edit-form"
      >
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formState.username" :disabled="isEdit" placeholder="请输入用户名" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item :label="passwordLabel" name="password">
              <a-input-password v-model:value="formState.password" :placeholder="passwordPlaceholder" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formState.name" placeholder="请输入姓名" />
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
            <a-form-item label="状态" name="isActive">
              <a-switch v-model:checked="formState.isActive" checked-children="启用" un-checked-children="禁用" />
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
import type { FormInstance, FormRules } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { adminUserService } from '@/services/modules/adminUser';
import type { AdminUpsertRequest, AdminUser } from '@/types';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const formState = reactive<AdminUpsertRequest>({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  isActive: true,
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [
    {
      validator: (_rule, value) => {
        if (!isEdit.value && !value) return Promise.reject('请输入密码');
        if (value && (value.length < 6 || value.length > 100)) {
          return Promise.reject('密码长度需在 6-100 之间');
        }
        return Promise.resolve();
      },
    },
  ],
  email: [
    {
      type: 'email',
      message: '邮箱格式不正确',
      trigger: 'blur',
    },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '手机号格式不正确',
      trigger: 'blur',
    },
  ],
};

const passwordLabel = computed(() => (isEdit.value ? '密码（留空表示不修改）' : '密码'));
const passwordPlaceholder = computed(() => (isEdit.value ? '不修改可留空' : '请输入密码'));
const pageTitle = computed(() => (isEdit.value ? '编辑管理员' : '新建管理员'));

const loadDetail = async () => {
  if (!isEdit.value) return;
  try {
    if (!recordId.value) return;
    const data = await adminUserService.fetchDetail(recordId.value);
    formState.username = data.username;
    formState.name = data.name ?? '';
    formState.email = data.email ?? '';
    formState.phone = data.phone ?? '';
    formState.isActive = data.isActive ?? true;
    formState.password = '';
  } catch (error: any) {
    message.error(error?.message || '获取详情失败');
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: AdminUpsertRequest = {
      username: formState.username,
      name: formState.name,
      email: formState.email,
      phone: formState.phone,
      isActive: formState.isActive,
    };
    if (formState.password) {
      payload.password = formState.password;
    }
    if (isEdit.value) {
      await adminUserService.update(recordId.value!, payload);
      message.success('更新成功');
    } else {
      if (!formState.password) {
        message.error('请输入密码');
        submitting.value = false;
        return;
      }
      payload.password = formState.password;
      const created = await adminUserService.create(payload);
      message.success(`创建成功：${created.username}`);
    }
    router.push('/admin/users');
  } catch (error: any) {
    const backendMsg = error?.message || error?.response?.data?.message;
    if (backendMsg) {
      message.error(backendMsg);
    }
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

<style scoped lang="less">
.admin-edit-form {
  max-width: 960px;
  margin: 0 auto;
}
</style>
