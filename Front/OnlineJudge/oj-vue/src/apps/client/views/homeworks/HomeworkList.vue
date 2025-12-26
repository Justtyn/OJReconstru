<template>
  <PageContainer title="作业" subtitle="班级作业与进度一览">
    <div class="homework-page">
      <a-alert v-if="loadError" type="error" show-icon :message="loadError" />

      <section class="homework-hero" v-if="currentClass">
        <div class="homework-hero__intro">
          <div class="homework-hero__title">{{ currentClass.name }}</div>
          <div class="homework-hero__desc">{{ currentClass.description || '当前班级作业列表' }}</div>
        </div>
        <div class="homework-hero__meta">
          <span>班级ID：{{ currentClass.id }}</span>
          <span>教师：{{ currentClass.creatorName || currentClass.creatorId || '-' }}</span>
          <span>开课：{{ currentClass.startDate || '-' }}</span>
          <span>结课：{{ currentClass.endDate || '-' }}</span>
        </div>
        <div class="homework-hero__filters">
          <a-input
            v-model:value="homeworkQuery.keyword"
            allow-clear
            size="small"
            placeholder="搜索作业标题/描述"
            class="filter-input"
            @pressEnter="handleHomeworkSearch"
          />
          <a-button type="primary" size="small" @click="handleHomeworkSearch">查询</a-button>
          <a-button size="small" @click="resetHomeworkQuery">重置</a-button>
        </div>
      </section>

      <section class="homework-hero" v-else>
        <div class="homework-hero__intro">
          <div class="homework-hero__title">加入班级</div>
          <div class="homework-hero__desc">选择班级后点击加入，输入邀请码验证通过即可进入。</div>
        </div>
        <div class="homework-hero__filters">
          <a-input
            v-model:value="classQuery.keyword"
            allow-clear
            size="small"
            placeholder="搜索班级名称"
            class="filter-input"
            @pressEnter="handleClassSearch"
          />
          <a-button type="primary" size="small" @click="handleClassSearch">查询</a-button>
          <a-button size="small" @click="resetClassQuery">重置</a-button>
        </div>
      </section>

      <a-spin :spinning="loading">
        <div v-if="currentClass" class="homework-list">
          <a-card v-for="item in homeworkList" :key="item.id" class="homework-card" :class="{ disabled: !canEnter(item) }" hoverable>
            <div class="homework-card__header">
              <div class="homework-card__title">{{ item.title }}</div>
              <a-tag :color="statusColor(item)">{{ statusLabel(item) }}</a-tag>
            </div>
            <div class="homework-card__meta">
              <span>开始：{{ formatTime(item.startTime) }}</span>
              <span>结束：{{ formatTime(item.endTime) }}</span>
            </div>
            <div class="homework-card__desc">{{ item.description || '暂无描述' }}</div>
            <div class="homework-card__footer">
              <span class="homework-card__hint">题目数：{{ homeworkProblemCount[item.id] ?? '-' }}</span>
              <a-button type="link" :disabled="!canEnter(item)" @click="goHomework(item)">进入作业</a-button>
            </div>
          </a-card>
          <a-empty v-if="!loading && !homeworkList.length" description="暂无作业" class="homework-empty" />
          <div class="homework-pagination" v-if="homeworkTotal > 0">
            <a-pagination
              :current="homeworkQuery.page"
              :page-size="homeworkQuery.size"
              :total="homeworkTotal"
              show-size-changer
              @change="handleHomeworkPage"
              @showSizeChange="handleHomeworkPage"
            />
          </div>
        </div>

        <div v-else class="class-list">
          <a-card v-for="item in classList" :key="item.id" class="class-card" hoverable>
            <div class="class-card__title">{{ item.name }}</div>
            <div class="class-card__meta">
              <span>班级ID：{{ item.id }}</span>
              <span>教师：{{ item.creatorName || item.creatorId || '-' }}</span>
            </div>
            <div class="class-card__desc">{{ item.description || '暂无班级简介' }}</div>
            <div class="class-card__footer">
              <span>起止：{{ item.startDate || '-' }} / {{ item.endDate || '-' }}</span>
              <a-button type="primary" size="small" :disabled="joining" @click="openJoinModal(item)">加入班级</a-button>
            </div>
          </a-card>
          <a-empty v-if="!loading && !classList.length" description="暂无班级" class="homework-empty" />
          <div class="homework-pagination" v-if="classTotal > 0">
            <a-pagination
              :current="classQuery.page"
              :page-size="classQuery.size"
              :total="classTotal"
              show-size-changer
              @change="handleClassPage"
              @showSizeChange="handleClassPage"
            />
          </div>
        </div>
      </a-spin>
    </div>

    <a-modal
      v-model:open="joinModalVisible"
      title="加入班级"
      :confirm-loading="joining"
      destroy-on-close
      @ok="handleJoin"
      @cancel="closeJoinModal"
    >
      <div class="join-modal">
        <div class="join-modal__title">{{ selectedClass?.name || '请选择班级' }}</div>
        <a-input v-model:value="joinCode" placeholder="输入班级邀请码" />
      </div>
    </a-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { format } from 'date-fns';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { classesService } from '@/services/modules/classes';
import { homeworkService } from '@/services/modules/homework';
import { studentClassService } from '@/services/modules/studentClass';
import type { Classes, ClassesQuery, Homework, HomeworkQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const currentClass = ref<Classes | null>(null);
const classList = ref<Classes[]>([]);
const classTotal = ref(0);
const homeworkList = ref<Homework[]>([]);
const homeworkTotal = ref(0);
const homeworkProblemCount = reactive<Record<string, number>>({});
const loading = ref(false);
const joining = ref(false);
const loadError = ref('');
const joinCode = ref('');
const joinModalVisible = ref(false);
const selectedClass = ref<Classes | null>(null);

const classQuery = reactive<ClassesQuery>({
  page: 1,
  size: 9,
  keyword: '',
});

const homeworkQuery = reactive<HomeworkQuery>({
  page: 1,
  size: 8,
  classId: undefined,
  keyword: '',
  activeOnly: false,
});

const fetchCurrentClass = async () => {
  loading.value = true;
  loadError.value = '';
  try {
    currentClass.value = await studentClassService.fetchCurrent();
    homeworkQuery.classId = currentClass.value.id;
  } catch (error) {
    currentClass.value = null;
  } finally {
    loading.value = false;
  }
};

const loadClasses = async () => {
  loading.value = true;
  try {
    const data = await classesService.fetchList(classQuery);
    classList.value = data.records || [];
    classTotal.value = data.total || 0;
  } catch (error) {
    message.error(extractErrorMessage(error, '获取班级列表失败'));
  } finally {
    loading.value = false;
  }
};

const loadHomeworks = async () => {
  if (!homeworkQuery.classId) return;
  loading.value = true;
  try {
    const data = await homeworkService.fetchList(homeworkQuery);
    homeworkList.value = data.records || [];
    homeworkTotal.value = data.total || 0;
    preloadHomeworkCounts(homeworkList.value);
  } catch (error) {
    message.error(extractErrorMessage(error, '获取作业列表失败'));
  } finally {
    loading.value = false;
  }
};

const preloadHomeworkCounts = (records: Homework[]) => {
  const ids = records.map((item) => item.id).filter((id) => id && homeworkProblemCount[id] === undefined);
  if (!ids.length) return;
  ids.forEach(async (id) => {
    try {
      const problems = await homeworkService.fetchProblems(id);
      homeworkProblemCount[id] = problems.length;
    } catch {
      homeworkProblemCount[id] = 0;
    }
  });
};

const handleJoin = async () => {
  if (!selectedClass.value) {
    message.error('请选择要加入的班级');
    return;
  }
  if (!joinCode.value.trim()) {
    message.error('请输入班级邀请码');
    return;
  }
  if (selectedClass.value.code && joinCode.value.trim() !== selectedClass.value.code) {
    message.error('邀请码不正确');
    return;
  }
  joining.value = true;
  try {
    const data = await studentClassService.join({ code: joinCode.value.trim() });
    message.success('加入班级成功');
    currentClass.value = data;
    homeworkQuery.classId = data.id;
    joinCode.value = '';
    joinModalVisible.value = false;
    selectedClass.value = null;
    await loadHomeworks();
  } catch (error) {
    message.error(extractErrorMessage(error, '加入班级失败'));
  } finally {
    joining.value = false;
  }
};

const openJoinModal = (item: Classes) => {
  selectedClass.value = item;
  joinCode.value = '';
  joinModalVisible.value = true;
};

const closeJoinModal = () => {
  joinModalVisible.value = false;
  selectedClass.value = null;
  joinCode.value = '';
};

const handleClassSearch = () => {
  classQuery.page = 1;
  loadClasses();
};

const resetClassQuery = () => {
  classQuery.keyword = '';
  handleClassSearch();
};

const handleClassPage = (page: number, size?: number) => {
  classQuery.page = page;
  classQuery.size = size ?? classQuery.size;
  loadClasses();
};

const handleHomeworkSearch = () => {
  homeworkQuery.page = 1;
  loadHomeworks();
};

const resetHomeworkQuery = () => {
  homeworkQuery.keyword = '';
  handleHomeworkSearch();
};

const handleHomeworkPage = (page: number, size?: number) => {
  homeworkQuery.page = page;
  homeworkQuery.size = size ?? homeworkQuery.size;
  loadHomeworks();
};

const goHomework = (item: Homework) => {
  if (!canEnter(item)) return;
  router.push(`/homeworks/${item.id}`);
};

const statusLabel = (item: Homework) => {
  if (item.isActive === false) return '未启用';
  const now = Date.now();
  const start = item.startTime ? new Date(item.startTime).getTime() : undefined;
  const end = item.endTime ? new Date(item.endTime).getTime() : undefined;
  if (start && now < start) return '未开始';
  if (end && now > end) return '已结束';
  return '进行中';
};

const statusColor = (item: Homework) => {
  const status = statusLabel(item);
  if (status === '未开始') return 'default';
  if (status === '已结束') return 'red';
  if (status === '未启用') return 'orange';
  return 'green';
};

const canEnter = (item: Homework) => statusLabel(item) === '进行中';

const formatTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '-';
  return format(date, 'yyyy-MM-dd HH:mm');
};

onMounted(async () => {
  await fetchCurrentClass();
  if (currentClass.value) {
    await loadHomeworks();
  } else {
    await loadClasses();
  }
});
</script>

<style scoped lang="less">
.homework-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.homework-hero {
  padding: 20px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.16), rgba(14, 116, 144, 0.16));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.homework-hero__title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-color);
}

.homework-hero__desc {
  margin-top: 6px;
  color: var(--text-muted, #64748b);
}

.homework-hero__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.homework-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.homework-list,
.class-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-input {
  width: 220px;
}

.homework-card,
.class-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
}

.homework-card.disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.homework-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.homework-card__title,
.class-card__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
}

.homework-card__meta,
.class-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
  margin-bottom: 8px;
}

.homework-card__desc,
.class-card__desc {
  color: var(--text-color);
  opacity: 0.85;
  line-height: 1.6;
  min-height: 48px;
}

.homework-card__footer,
.class-card__footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-muted, #94a3b8);
  font-size: 12px;
}

.homework-pagination {
  display: flex;
  justify-content: center;
  padding: 12px 0 4px;
}

.join-modal {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.join-modal__title {
  font-weight: 600;
  color: var(--text-color);
}

.homework-empty {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .homework-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }
}

:global(:root[data-theme='dark']) .homework-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(37, 99, 235, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .homework-card,
:global(:root[data-theme='dark']) .class-card {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: rgba(15, 23, 42, 0.7);
}
</style>
