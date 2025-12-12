<template>
  <PageContainer title="讨论管理">
    <a-card>
      <a-form layout="inline" :model="query">
        <a-form-item label="关键词">
          <a-input v-model:value="query.keyword" allow-clear placeholder="标题/内容" @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="题目">
          <a-select
            v-model:value="query.problemId"
            show-search
            allow-clear
            :filter-option="false"
            :options="problemOptions"
            :loading="problemLoading"
            style="width: 240px"
            placeholder="搜索题目"
            @search="searchProblems"
            @dropdownVisibleChange="handleProblemDropdown"
          />
        </a-form-item>
        <a-form-item label="作者">
          <a-select
            v-model:value="query.userId"
            show-search
            allow-clear
            :filter-option="false"
            :options="studentOptions"
            :loading="studentLoading"
            style="width: 240px"
            placeholder="搜索学生"
            @search="searchStudents"
            @dropdownVisibleChange="handleStudentDropdown"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="query.isActive" allow-clear placeholder="全部" style="width: 140px">
            <a-select-option :value="true">启用</a-select-option>
            <a-select-option :value="false">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
          <a-button type="primary" ghost style="margin-left: 8px" @click="goCreate">新增讨论</a-button>
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
            :columns="commentColumns"
            :data-source="commentMap[record.id]?.items || []"
            :loading="commentMap[record.id]?.loading"
            row-key="id"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record: comment, text }">
              <template v-if="column.key === 'userId'">
                {{ studentCache[comment.userId]?.username || comment.userId }}
              </template>
              <template v-else-if="column.key === 'createTime'">
                {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
              </template>
              <template v-else-if="column.key === 'actions'">
                <a-button danger type="link" size="small" @click="confirmRemoveComment(record.id, comment)">
                  删除评论
                </a-button>
              </template>
            </template>
          </a-table>
        </template>
        <template #bodyCell="{ column, record, text }">
          <template v-if="column.key === 'problemId'">
            {{ problemCache[record.problemId]?.name || record.problemId }}
          </template>
          <template v-else-if="column.key === 'userId'">
            {{ studentCache[record.userId]?.username || record.userId }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ text ? format(new Date(text), 'yyyy-MM-dd HH:mm') : '-' }}
          </template>
          <template v-else-if="column.key === 'isActive'">
            <a-badge :status="record.isActive ? 'success' : 'error'" :text="record.isActive ? '启用' : '禁用'" />
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="edit(record)">编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="link" size="small" @click="openCommentModal(record.id)">评论</a-button>
              <a-divider type="vertical" />
              <a-button danger type="link" size="small" @click="confirmRemove(record)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </PageContainer>

  <a-modal
    v-model:open="commentModalVisible"
    title="发布评论"
    :confirm-loading="commentSubmitting"
    @ok="submitComment"
    @cancel="commentModalVisible = false"
    destroy-on-close
  >
    <a-form layout="vertical">
      <a-form-item label="评论内容" required>
        <a-textarea
          v-model:value="commentForm.content"
          :maxlength="200"
          show-count
          :rows="4"
          placeholder="请输入评论内容（不超过200字）"
        />
      </a-form-item>
      <a-form-item label="发布学生" required>
        <a-select
          v-model:value="commentForm.authorId"
          show-search
          allow-clear
          :filter-option="false"
          :options="studentOptions"
          :loading="studentLoading"
          placeholder="搜索并选择学生"
          @search="searchStudents"
          @dropdownVisibleChange="handleStudentDropdown"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message, Modal } from 'ant-design-vue';
import type { TableColumnType } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { discussionService } from '@/services/modules/discussion';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Discussion, DiscussionComment, DiscussionQuery, Problem, Student } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();

const query = reactive<DiscussionQuery>({ page: 1, size: 10, keyword: '', isActive: undefined });
const list = ref<Discussion[]>([]);
const total = ref(0);
const loading = ref(false);
const expandedRowKeys = ref<string[]>([]);
const commentModalVisible = ref(false);
const commentSubmitting = ref(false);
const commentTargetId = ref<string>('');
const commentForm = reactive<{ content: string; authorId?: string }>({ content: '', authorId: undefined });

const problemOptions = ref<{ label: string; value: string }[]>([]);
const problemLoading = ref(false);
const problemCache = reactive<Record<string, Problem>>({});

const studentOptions = ref<{ label: string; value: string }[]>([]);
const studentLoading = ref(false);
const studentCache = reactive<Record<string, Student>>({});

const commentMap = reactive<Record<string, { loading: boolean; items: DiscussionComment[] }>>({});

const columns: TableColumnType<Discussion>[] = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '题目', dataIndex: 'problemId', key: 'problemId', width: 220 },
  { title: '作者', dataIndex: 'userId', key: 'userId', width: 200 },
  { title: '状态', dataIndex: 'isActive', key: 'isActive', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 180 },
];

const commentColumns: TableColumnType<DiscussionComment>[] = [
  { title: '评论ID', dataIndex: 'id', key: 'id', width: 180 },
  { title: '评论人', dataIndex: 'userId', key: 'userId', width: 180 },
  { title: '内容', dataIndex: 'content', key: 'content' },
  { title: '时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 160 },
];

const loadData = async () => {
  loading.value = true;
  try {
    const params: DiscussionQuery = { ...query };
    if (params.isActive === undefined) delete params.isActive;
    const data = await discussionService.fetchList(params);
    list.value = data.records;
    total.value = data.total;
    preloadCaches(data.records);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取讨论列表失败'));
  } finally {
    loading.value = false;
  }
};

const preloadCaches = (records: Discussion[]) => {
  const problemIds = Array.from(new Set(records.map((r) => r.problemId).filter((id) => id && !problemCache[id])));
  const userIds = Array.from(new Set(records.map((r) => r.userId).filter((id) => id && !studentCache[id])));

  if (problemIds.length) {
    Promise.all(
      problemIds.map(async (id) => {
        try {
          const detail = await problemService.fetchDetail(id);
          problemCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }

  if (userIds.length) {
    Promise.all(
      userIds.map(async (id) => {
        try {
          const detail = await studentService.fetchDetail(id);
          studentCache[id] = detail;
        } catch {
          /* ignore */
        }
      }),
    );
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.keyword = '';
  query.problemId = undefined;
  query.userId = undefined;
  query.isActive = undefined;
  handleSearch();
};

const edit = (record: Discussion) => {
  router.push(`/admin/discussions/${record.id}/edit`);
};

const openCommentModal = (discussionId: string) => {
  commentTargetId.value = discussionId;
  commentForm.content = '';
  commentForm.authorId = undefined;
  commentModalVisible.value = true;
};

const goCreate = () => {
  router.push('/admin/discussions/create');
};

const remove = async (record: Discussion) => {
  try {
    await discussionService.remove(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除失败'));
  }
};

const confirmRemove = (record: Discussion) => {
  Modal.confirm({
    title: '删除讨论',
    content: `确认删除讨论「${record.title}」？不可恢复`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => remove(record),
  });
};

const loadComments = async (discussionId: string) => {
  if (!commentMap[discussionId]) {
    commentMap[discussionId] = { loading: false, items: [] };
  }
  commentMap[discussionId].loading = true;
  try {
    const data = await discussionService.fetchComments(discussionId);
    commentMap[discussionId].items = data;
    const userIds = Array.from(
      new Set(data.map((c) => c.userId).filter((id) => id && !studentCache[id])),
    );
    if (userIds.length) {
      Promise.all(
        userIds.map(async (id) => {
          try {
            const detail = await studentService.fetchDetail(id);
            studentCache[id] = detail;
          } catch {
            /* ignore */
          }
        }),
      );
    }
  } catch (error: any) {
    message.error(extractErrorMessage(error, '获取评论失败'));
  } finally {
    commentMap[discussionId].loading = false;
  }
};

const handleExpand = (expanded: boolean, record: Discussion) => {
  if (expanded) {
    expandedRowKeys.value = [...expandedRowKeys.value, record.id];
    loadComments(record.id);
  } else {
    expandedRowKeys.value = expandedRowKeys.value.filter((k) => k !== record.id);
  }
};

const removeComment = async (discussionId: string, comment: DiscussionComment) => {
  try {
    await discussionService.removeComment(comment.id);
    message.success('评论已删除');
    loadComments(discussionId);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '删除评论失败'));
  }
};

const confirmRemoveComment = (discussionId: string, comment: DiscussionComment) => {
  Modal.confirm({
    title: '删除评论',
    content: `确认删除评论「${comment.content.slice(0, 20)}」？`,
    okText: '确定',
    cancelText: '取消',
    onOk: () => removeComment(discussionId, comment),
  });
};

const searchProblems = async (keyword: string) => {
  problemLoading.value = true;
  try {
    const data = await problemService.fetchList({ page: 1, size: 50, keyword });
    problemOptions.value = data.records.map((p: Problem) => ({ label: `${p.name}（ID: ${p.id}）`, value: p.id }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索题目失败'));
  } finally {
    problemLoading.value = false;
  }
};

const searchStudents = async (keyword: string) => {
  studentLoading.value = true;
  try {
    const data = await studentService.fetchList({ page: 1, size: 50, username: keyword, email: keyword });
    studentOptions.value = data.records.map((s: Student) => ({
      label: `${s.username}${s.name ? `（${s.name}）` : ''}（ID: ${s.id}）`,
      value: s.id,
    }));
  } catch (error: any) {
    message.error(extractErrorMessage(error, '搜索学生失败'));
  } finally {
    studentLoading.value = false;
  }
};

const handleProblemDropdown = (open: boolean) => {
  if (open) searchProblems('');
};

const handleStudentDropdown = (open: boolean) => {
  if (open) searchStudents('');
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

const submitComment = async () => {
  if (!commentTargetId.value) return;
  if (!commentForm.content.trim()) {
    message.warning('请输入评论内容');
    return;
  }
  if (!commentForm.authorId) {
    message.warning('请选择发布学生');
    return;
  }
  commentSubmitting.value = true;
  try {
    await discussionService.createComment(commentTargetId.value, {
      content: commentForm.content.slice(0, 200),
      authorId: commentForm.authorId,
    });
    message.success('评论已发布');
    commentModalVisible.value = false;
    loadComments(commentTargetId.value);
  } catch (error: any) {
    message.error(extractErrorMessage(error, '发布评论失败'));
  } finally {
    commentSubmitting.value = false;
  }
};

loadData();
</script>

<style scoped lang="less">
.mt-16 {
  margin-top: 16px;
}
</style>
