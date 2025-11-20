<template>
  <PageContainer :title="pageTitle">
    <a-card>
      <a-form layout="vertical" :model="formState" :rules="rules" ref="formRef" class="announcement-edit-form">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="标题" name="title">
              <a-input v-model:value="formState.title" placeholder="请输入标题" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="发布时间" name="time">
              <a-date-picker
                v-model:value="formState.time"
                show-time
                value-format="YYYY-MM-DDTHH:mm:ss"
                placeholder="选择发布时间"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="置顶" name="isPinned">
              <a-switch v-model:checked="formState.isPinned" checked-children="置顶" un-checked-children="普通" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="状态" name="isActive">
              <a-switch v-model:checked="formState.isActive" checked-children="启用" un-checked-children="禁用" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="内容" name="content">
          <a-textarea v-model:value="formState.content" :rows="8" placeholder="请输入公告内容（支持 Markdown）" />
        </a-form-item>
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
import type { FormInstance, FormProps } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { adminAnnouncementService } from '@/services/modules/adminAnnouncement';
import type { AnnouncementRequest } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const isEdit = computed(() => Boolean(route.params.id));
const recordId = computed(() => route.params.id as string | undefined);

const formState = reactive<AnnouncementRequest>({
  title: '',
  content: '',
  time: '',
  isPinned: false,
  isActive: true,
});

const rules: FormProps['rules'] = {
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入内容' }],
};

const pageTitle = computed(() => (isEdit.value ? '编辑公告' : '新建公告'));

const loadDetail = async () => {
  if (!isEdit.value) return;
  try {
    if (!recordId.value) return;
    const data = await adminAnnouncementService.fetchDetail(recordId.value);
    formState.title = data.title;
    formState.content = data.content;
    formState.time = data.time;
    formState.isPinned = data.isPinned;
    formState.isActive = data.isActive;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取详情失败'));
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
    submitting.value = true;
    const payload: AnnouncementRequest = {
      title: formState.title,
      content: formState.content,
      time: formState.time,
      isPinned: formState.isPinned,
      isActive: formState.isActive,
    };
    if (isEdit.value) {
      if (!recordId.value) {
        message.error('缺少公告ID');
        submitting.value = false;
        return;
      }
      await adminAnnouncementService.update(recordId.value, payload);
      message.success('更新成功');
    } else {
      await adminAnnouncementService.create(payload);
      message.success('创建成功');
    }
    router.push('/admin/announcements');
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

<style scoped lang="less">
.announcement-edit-form {
  max-width: 960px;
  margin: 0 auto;
}
</style>
