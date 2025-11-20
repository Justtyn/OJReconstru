<template>
  <PageContainer title="欢迎">
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :md="12">
        <a-card title="账户信息">
          <p>用户名：{{ authStore.user?.username }}</p>
          <p>角色：{{ authStore.role }}</p>
        </a-card>
      </a-col>
      <a-col :xs="24" :md="12">
        <a-card title="快速入口">
          <a-space direction="vertical">
            <a-button type="primary" disabled>前往题库（开发中）</a-button>
            <a-button disabled>班级作业（开发中）</a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </PageContainer>
</template>

<script setup lang="ts">
import PageContainer from '@/components/common/PageContainer.vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
</script>
