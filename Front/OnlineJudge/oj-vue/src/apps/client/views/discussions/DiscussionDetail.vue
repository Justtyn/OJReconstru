<template>
  <PageContainer :title="discussion?.title || '讨论详情'" :show-back="true">
    <div class="discussion-detail">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="16">
          <a-card class="detail-card" :loading="loading">
            <div class="detail-meta">
              <a-space>
                <a-tag v-if="discussion?.isActive === false" color="red">未启用</a-tag>
              </a-space>
              <div class="detail-meta__info">
                <span>题目：{{ problemName }}</span>
                <span>作者：{{ authorName }}</span>
                <span>发布时间：{{ formatTime(discussion?.createTime) }}</span>
              </div>
            </div>
            <div class="markdown-body" v-html="renderMarkdown(discussion?.content || '')"></div>
          </a-card>

          <a-card class="detail-card mt-16" title="评论">
            <a-list :data-source="comments" :loading="commentLoading" :locale="{ emptyText: '暂无评论' }">
              <template #renderItem="{ item }">
                <a-comment :author="commentAuthor(item.userId)" :content="item.content" :datetime="formatTime(item.createTime)">
                  <template #avatar>
                    <a-avatar :size="28" :src="commentAvatar(item.userId)">
                      {{ commentInitial(item.userId) }}
                    </a-avatar>
                  </template>
                </a-comment>
              </template>
            </a-list>
            <a-divider v-if="isStudent" />
            <div v-if="isStudent" class="comment-editor">
              <a-textarea
                v-model:value="commentContent"
                :rows="4"
                placeholder="发表评论，支持 Markdown"
                :maxlength="500"
                show-count
              />
              <div class="comment-editor__actions">
                <a-button
                  type="primary"
                  :loading="commentSubmitting"
                  :disabled="!commentContent.trim()"
                  @click="handleCommentSubmit"
                >
                  发表评论
                </a-button>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="8">
          <a-card class="detail-card" title="讨论信息" :loading="loading">
            <a-descriptions size="small" :column="1" bordered class="discussion-info">
              <a-descriptions-item label="题目">{{ problemName }}</a-descriptions-item>
              <a-descriptions-item label="作者">{{ authorName }}</a-descriptions-item>
              <a-descriptions-item label="评论数">{{ comments.length }}</a-descriptions-item>
              <a-descriptions-item label="更新时间">{{ formatTime(discussion?.updateTime) }}</a-descriptions-item>
            </a-descriptions>
            <a-button block type="primary" style="margin-top: 16px" @click="goProblem">查看题目</a-button>
          </a-card>
          <a-card class="detail-card mt-16" title="参与提示">
            <ul class="detail-tips">
              <li>在管理端可维护讨论内容与评论。</li>
              <li>评论区会展示所有公开回复。</li>
              <li>如需提交代码，请回到题目详情。</li>
            </ul>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { discussionService } from '@/services/modules/discussion';
import { problemService } from '@/services/modules/problem';
import { studentService } from '@/services/modules/student';
import type { Discussion, DiscussionComment, Problem, Student } from '@/types';
import { renderMarkdown } from '@/utils/markdown';
import { extractErrorMessage } from '@/utils/error';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const discussion = ref<Discussion | null>(null);
const comments = ref<DiscussionComment[]>([]);
const loading = ref(false);
const commentLoading = ref(false);
const commentSubmitting = ref(false);
const commentContent = ref('');
const loadError = ref('');
const problem = ref<Problem | null>(null);
const author = ref<Student | null>(null);
const studentCache = ref<Record<string, Student>>({});

const discussionId = computed(() => route.params.id as string);
const problemName = computed(() => problem.value?.name || discussion.value?.problemId || '-');
const authorName = computed(() => {
  if (author.value) return author.value.name ? `${author.value.username}（${author.value.name}）` : author.value.username;
  return discussion.value?.userId || '-';
});
const isStudent = computed(() => authStore.role === 'student');

const loadDetail = async () => {
  if (!discussionId.value) return;
  loading.value = true;
  loadError.value = '';
  try {
    const data = await discussionService.fetchDetail(discussionId.value);
    discussion.value = data;
    if (data.problemId) {
      try {
        problem.value = await problemService.fetchDetail(data.problemId);
      } catch {
        problem.value = null;
      }
    }
    if (data.userId) {
      try {
        author.value = await studentService.fetchDetail(data.userId);
      } catch {
        author.value = null;
      }
    }
  } catch (error) {
    const msg = extractErrorMessage(error, '获取讨论详情失败');
    loadError.value = msg;
    message.error(msg);
  } finally {
    loading.value = false;
  }
};

const loadComments = async () => {
  if (!discussionId.value) return;
  commentLoading.value = true;
  try {
    const data = await discussionService.fetchComments(discussionId.value);
    comments.value = data || [];
    await preloadCommentAuthors(comments.value);
  } catch (error) {
    message.error(extractErrorMessage(error, '获取讨论评论失败'));
  } finally {
    commentLoading.value = false;
  }
};

const preloadCommentAuthors = async (items: DiscussionComment[]) => {
  const cache = studentCache.value;
  const ids = Array.from(new Set(items.map((c) => c.userId).filter((id) => id && !cache[id])));
  if (!ids.length) return;
  await Promise.all(
    ids.map(async (id) => {
      try {
        const detail = await studentService.fetchDetail(id);
        cache[id] = detail;
      } catch {
        /* ignore */
      }
    }),
  );
  studentCache.value = { ...cache };
};

const commentAuthor = (userId: string) => {
  const student = studentCache.value[userId];
  if (!student) return userId;
  return student.name ? `${student.username}（${student.name}）` : student.username;
};
const commentAvatar = (userId: string) => studentCache.value[userId]?.avatar || '';
const commentInitial = (userId: string) => {
  const student = studentCache.value[userId];
  const label = student?.username || userId || 'U';
  return label.charAt(0).toUpperCase();
};

const handleCommentSubmit = async () => {
  if (!discussionId.value) return;
  const content = commentContent.value.trim();
  if (!content) return;
  if (!authStore.user?.id) {
    message.error('登录信息缺失，请重新登录');
    return;
  }
  commentSubmitting.value = true;
  try {
    await discussionService.createComment(discussionId.value, { content, authorId: authStore.user.id });
    commentContent.value = '';
    await loadComments();
    message.success('评论已发布');
  } catch (error) {
    message.error(extractErrorMessage(error, '发表评论失败'));
  } finally {
    commentSubmitting.value = false;
  }
};

const goProblem = () => {
  if (!discussion.value?.problemId) return;
  router.push(`/problems/${discussion.value.problemId}`);
};

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

onMounted(async () => {
  await loadDetail();
  await loadComments();
});
</script>

<style scoped lang="less">
.discussion-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.detail-meta__info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.detail-tips {
  margin: 0;
  padding-left: 18px;
  color: var(--text-muted, #94a3b8);
  line-height: 1.8;
}

.comment-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-editor__actions {
  display: flex;
  justify-content: flex-end;
}

.discussion-info :deep(.ant-descriptions-item-label) {
  width: 72px;
  min-width: 72px;
  padding: 6px 8px;
}

.discussion-info :deep(.ant-descriptions-item-content) {
  padding: 6px 10px;
}

.mt-16 {
  margin-top: 16px;
}

.markdown-body :deep(p) {
  line-height: 1.8;
}

:global(:root[data-theme='dark']) .detail-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 32px rgba(0, 0, 0, 0.32);
}
</style>
