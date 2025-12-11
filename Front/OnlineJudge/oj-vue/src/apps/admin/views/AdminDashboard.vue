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
            <li>优化邮件HTML样式</li>
            <li>没有重发验证码功能</li>
            <li>缺少本地验证码（数字、字母混合）或邮件验证码</li>
            <li>作业需要一个下拉框将所有存在的题目检索出来，然后可以多选选择作业要包含的题目，修改作业包含的题目时需要把当前包含的题目检索出来防止重复选择</li>
            <li>学生管理可以查看的具体信息需要增加</li>
            <li>教师职称在前端应该枚举多种职称</li>
            <li>班级应该显示创建者，还需要二级显示班级当前的学生，应该存在多选下拉框可以选择要添加的学生</li>
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
