<template>
  <PageContainer title="班级管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input
            v-model:value="query.keyword"
            allow-clear
            placeholder="名称/描述/邀请码"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
        </a-form-item>
        <a-form-item style="margin-left:auto;">
          <a-button type="primary" @click="goCreate">新建班级</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card class="mt-16">
      <a-table
        row-key="id"
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="paginationConfig"
        :expanded-row-keys="expandedRowKeys"
        @expand="handleExpand"
      >
        <template #expandedRowRender="{ record }">
          <a-table
            :columns="studentColumns"
            :data-source="studentListMap[record.id]?.items || []"
            :loading="studentListMap[record.id]?.loading"
            row-key="id"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record: student }">
              <template v-if="column.key === 'isVerified'">
                <a-tag :color="student.isVerified ? 'green' : 'orange'">
                  {{ student.isVerified ? '已验证' : '未验证' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'actions'">
                <a-button danger type="link" size="small" @click="confirmRemoveStudent(record.id, student)">
                  移出班级
                </a-button>
              </template>
            </template>
          </a-table>
        </template>
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'startDate' || column.key === 'endDate'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd') : '-' }}
          </template>
          <template v-else-if="column.key === 'creatorName'">
            {{ record.creatorName || record.creatorId || '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="goHomeworks(record)">作业管理</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button danger type="link" size="small" @click="confirmRemove(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </PageContainer>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService, classesMemberService } from '@/services/modules/classes';
import type { Classes, ClassesMember, ClassesQuery, Student } from '@/types';
import type { TableColumnType } from 'ant-design-vue';
import { extractErrorMessage } from '@/utils/error';
import { studentService } from '@/services/modules/student';

const router = useRouter();

const query = reactive<ClassesQuery>({ page: 1, size: 10, keyword: '' });
const list = ref<Classes[]>([]);
const total = ref(0);
const loading = ref(false);
const expandedRowKeys = ref<string[]>([]);
const studentListMap = reactive<Record<string, { loading: boolean; items: (Student & { memberId: string })[] }>>({});
const studentCache = reactive<Record<string, Student>>({});

const columns: TableColumnType<Classes>[] = [
  { title: '名称', dataIndex: 'name', key: 'name', width: 260 },
  { title: '创建者', dataIndex: 'creatorName', key: 'creatorName', width: 160 },
  { title: '邀请码', dataIndex: 'code', key: 'code', width: 140 },
  { title: '开始日期', dataIndex: 'startDate', key: 'startDate', width: 140 },
  { title: '结束日期', dataIndex: 'endDate', key: 'endDate', width: 140 },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'actions', width: 240 },
];

const studentColumns: TableColumnType<Student & { memberId: string }>[] = [
  { title: '学生ID', dataIndex: 'id', key: 'id', width: 160 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 140 },
  { title: '姓名', dataIndex: 'name', key: 'name', width: 120 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 180 },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 140 },
  { title: '积分', dataIndex: 'score', key: 'score', width: 80 },
  { title: '邮箱状态', dataIndex: 'isVerified', key: 'isVerified', width: 100 },
  { title: '操作', key: 'actions', width: 120 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const data = await classesService.fetchList(query);
    list.value = data.records;
    total.value = data.total;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级列表失败'));
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  handleSearch();
};

const loadStudents = async (classId: string) => {
  if (!studentListMap[classId]) {
    studentListMap[classId] = { loading: false, items: [] };
  }
  studentListMap[classId].loading = true;
  try {
    const members = await classesMemberService.fetchList({ classId, page: 1, size: 200 });
    const items: (Student & { memberId: string })[] = [];
    for (const m of members.records || []) {
      if (!m.studentId) continue;
      let student = studentCache[m.studentId];
      if (!student) {
        try {
          student = await studentService.fetchDetail(m.studentId);
          studentCache[m.studentId] = student;
        } catch (err: any) {
          // 失败时也记录占位，避免重复请求
          studentCache[m.studentId] = { id: m.studentId, username: m.studentId } as Student;
        }
      }
      items.push({ ...student, memberId: m.id });
    }
    studentListMap[classId].items = items;
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取班级学生失败'));
  } finally {
    studentListMap[classId].loading = false;
  }
};

const handleExpand = (expanded: boolean, record: Classes) => {
  if (expanded) {
    expandedRowKeys.value = [...expandedRowKeys.value, record.id];
    loadStudents(record.id);
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((k) => k !== record.id);
  }
};

const goCreate = () => {
  router.push('/admin/classes/create');
};

const edit = (record: Classes) => {
  router.push(`/admin/classes/${record.id}/edit`);
};

const goHomeworks = (record: Classes) => {
  router.push(`/admin/classes/${record.id}/homeworks`);
};

const remove = async (record: Classes) => {
  try {
    await classesService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Classes) => {
  Modal.confirm({
    title: '删除班级',
    content: `确认删除班级「${record.name}」？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

const removeStudent = async (classId: string, student: Student & { memberId: string }) => {
  try {
    await classesMemberService.remove(student.memberId);
    message.success('已移出班级');
    loadStudents(classId);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '移除失败'));
  }
};

const confirmRemoveStudent = (classId: string, student: Student & { memberId: string }) => {
  Modal.confirm({
    title: '移除学生',
    content: `确认将学生「${student.name || student.username || student.id}」移出班级？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => removeStudent(classId, student),
  });
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 条`,
  onChange: (page: number, size?: number) => {
    query.page = page;
    query.size = size ?? query.size;
    loadData();
  },
}));

loadData();
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}
</style>
