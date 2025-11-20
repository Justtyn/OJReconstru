<template>
  <PageContainer title="管理端仪表盘">
    <a-alert
      type="info"
      message="后台入口"
      description="此页作为管理员/教师的默认落地页，后续会扩展用户、题目、作业等管理功能。"
      show-icon
    />
    <a-divider />
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :md="12">
        <a-card title="系统状态">
          <p>当前用户：{{ authStore.user?.username }}</p>
          <p>角色：{{ authStore.role }}</p>
        </a-card>
      </a-col>
      <a-col :xs="24" :md="12">
        <a-card title="下一步">
          <ul>
            <li>完善用户/班级/题库管理页</li>
            <li>接入业务统计图表</li>
          </ul>
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
