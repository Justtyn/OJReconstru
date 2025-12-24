<template>
  <PageContainer title="排行榜" subtitle="根据学生得分排序">
    <div class="ranking-page">
      <section class="ranking-hero">
        <div class="ranking-hero__top">
          <div class="ranking-hero__intro">
            <div class="ranking-hero__title">根据学生得分排序</div>
          </div>
          <div class="ranking-hero__stats">
            <span class="stat-pill">
              总人数
              <span class="stat-pill__value">{{ totalLabel }}</span>
            </span>
            <span class="stat-pill">
              当前页
              <span class="stat-pill__value">{{ query.page }}</span>
            </span>
          </div>
        </div>
        <div class="ranking-hero__filters">
          <a-input
            v-model:value="query.username"
            allow-clear
            size="small"
            placeholder="搜索用户名"
            class="ranking-search"
            @pressEnter="handleSearch"
          />
          <a-button type="primary" size="small" @click="handleSearch">查询</a-button>
          <a-button size="small" @click="resetQuery">重置</a-button>
        </div>
      </section>

      <a-card class="ranking-card">
        <a-table
          row-key="id"
          :loading="loading"
          :columns="columns"
          :data-source="rankedList"
          :pagination="paginationConfig"
          :scroll="{ x: 1150 }"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'rank'">
              <span class="rank-badge" :class="rankClass(index)">{{ rankLabel(index) }}</span>
            </template>
            <template v-else-if="column.key === 'student'">
              <div class="rank-user" @click="goProfile(record)">
                <a-avatar :src="record.avatar" :size="32">{{ record.username?.[0] || 'S' }}</a-avatar>
                <div class="rank-user__info">
                  <div class="rank-user__name">{{ record.username || record.name || record.id }}</div>
                  <div class="rank-user__meta">{{ record.school || '-' }}</div>
                </div>
              </div>
            </template>
          <template v-else-if="column.key === 'score'">
            <span class="rank-score">{{ record.score ?? 0 }}</span>
          </template>
          <template v-else-if="column.key === 'ac'">
            {{ record.ac ?? 0 }}
          </template>
          <template v-else-if="column.key === 'submit'">
            {{ record.submit ?? 0 }}
          </template>
          <template v-else-if="column.key === 'passRate'">
            {{ passRateLabel(record) }}
          </template>
          <template v-else-if="column.key === 'sex'">
            {{ sexLabel(record.sex) }}
          </template>
          <template v-else-if="column.key === 'bio'">
            <a-typography-paragraph style="margin: 0" :ellipsis="{ rows: 2 }">
              {{ bioExcerpt(record.bio) }}
            </a-typography-paragraph>
          </template>
          <template v-else-if="column.key === 'actions'">
            <a-button type="link" size="small" @click="goProfile(record)">查看主页</a-button>
          </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import type { TableColumnType } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import { studentService } from '@/services/modules/student';
import type { Student, StudentQuery } from '@/types';
import { extractErrorMessage } from '@/utils/error';

const router = useRouter();
const query = reactive<StudentQuery>({
  page: 1,
  size: 20,
  username: '',
  email: '',
});
const list = ref<Student[]>([]);
const total = ref(0);
const loading = ref(false);
let loadSeq = 0;

const columns: TableColumnType<Student>[] = [
  { title: '排名', key: 'rank', width: 80 },
  { title: '学生', key: 'student', width: 220 },
  { title: '得分', key: 'score', width: 100 },
  { title: '通过', key: 'ac', width: 80 },
  { title: '提交', key: 'submit', width: 80 },
  { title: '通过率', key: 'passRate', width: 90 },
  { title: '性别', key: 'sex', width: 70 },
  { title: '个人简介', key: 'bio', width: 240 },
  { title: '操作', key: 'actions', width: 120 },
];

const rankedList = computed(() => {
  const sorted = [...list.value].sort((a, b) => (b.score ?? 0) - (a.score ?? 0));
  return sorted;
});

const totalLabel = computed(() => (total.value ? total.value.toString() : '-'));

const loadData = async () => {
  const seq = (loadSeq += 1);
  loading.value = true;
  try {
    const data = await studentService.fetchList(query);
    if (seq !== loadSeq) return;
    list.value = data.records || [];
    total.value = data.total || 0;
  } catch (error) {
    if (seq === loadSeq) {
      message.error(extractErrorMessage(error, '获取排行榜失败'));
    }
  } finally {
    if (seq === loadSeq) loading.value = false;
  }
};

const handleSearch = () => {
  query.page = 1;
  loadData();
};

const resetQuery = () => {
  query.username = '';
  handleSearch();
};

const goProfile = (record: Student) => {
  router.push(`/students/${record.id}`);
};

const rankLabel = (index: number) => {
  const rank = (query.page - 1) * query.size + index + 1;
  return `#${rank}`;
};

const rankClass = (index: number) => {
  const rank = (query.page - 1) * query.size + index + 1;
  if (rank === 1) return 'gold';
  if (rank === 2) return 'silver';
  if (rank === 3) return 'bronze';
  return '';
};

const passRateLabel = (record: Student) => {
  const submit = record.submit ?? 0;
  if (!submit) return '0%';
  const rate = Math.round(((record.ac ?? 0) / submit) * 100);
  return `${rate}%`;
};

const sexLabel = (sex?: string | null) => {
  if (!sex) return '-';
  const normalized = sex.toLowerCase();
  if (['male', 'm', '男'].includes(normalized)) return '男';
  if (['female', 'f', '女'].includes(normalized)) return '女';
  return sex;
};

const bioExcerpt = (bio?: string | null) => {
  if (!bio) return '暂无简介';
  const plain = bio.replace(/\s+/g, ' ').trim();
  if (!plain) return '暂无简介';
  return plain.length > 80 ? `${plain.slice(0, 80)}…` : plain;
};

const paginationConfig = computed(() => ({
  current: query.page,
  pageSize: query.size,
  total: total.value,
  showTotal: (t: number) => `共 ${t} 人`,
  onChange: (page: number, size?: number) => {
        query.page = page;
        query.size = size ?? query.size;
        loadData();
  },
}));

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.ranking-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.ranking-hero {
  padding: 14px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.16), rgba(16, 185, 129, 0.14));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ranking-hero__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.ranking-hero__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.ranking-hero__stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-pill {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(15, 23, 42, 0.06);
  font-size: 12px;
  color: var(--text-color);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.stat-pill__value {
  font-weight: 600;
  color: #0ea5e9;
}

.ranking-search {
  width: 200px;
}

.ranking-hero__filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.ranking-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}

.rank-badge {
  display: inline-flex;
  padding: 4px 8px;
  border-radius: 999px;
  font-weight: 600;
  background: var(--body-bg);
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.rank-badge.gold {
  background: rgba(250, 204, 21, 0.2);
  border-color: rgba(250, 204, 21, 0.5);
  color: #a16207;
}

.rank-badge.silver {
  background: rgba(148, 163, 184, 0.2);
  border-color: rgba(148, 163, 184, 0.5);
  color: #475569;
}

.rank-badge.bronze {
  background: rgba(217, 119, 6, 0.2);
  border-color: rgba(217, 119, 6, 0.5);
  color: #92400e;
}

.rank-user {
  display: flex;
  gap: 10px;
  align-items: center;
  cursor: pointer;
}

.rank-user__info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rank-user__name {
  color: var(--text-color);
  font-weight: 600;
}

.rank-user__meta {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
}

.rank-score {
  font-weight: 600;
  color: var(--text-color);
}

@media (max-width: 768px) {
  .ranking-hero__filters {
    flex-direction: column;
    align-items: stretch;
  }

  .ranking-hero__stats {
    width: 100%;
    justify-content: flex-start;
  }
}

:global(:root[data-theme='dark']) .ranking-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(16, 185, 129, 0.4));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .stat-pill,
:global(:root[data-theme='dark']) .ranking-card,
:global(:root[data-theme='dark']) .rank-badge {
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.3);
  background: rgba(15, 23, 42, 0.7);
}

:global(:root[data-theme='dark']) .stat-pill__value {
  color: #38bdf8;
}
</style>
