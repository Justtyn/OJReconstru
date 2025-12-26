<template>
  <PageContainer title="关于系统" subtitle="面向课程与竞赛的在线评测与学习平台">
    <div class="about-page">
      <section class="about-hero">
        <div class="about-hero__content">
          <div class="about-hero__badge">Re Online Judge</div>
          <div class="about-hero__title">Re Online Judge（XUJCOJ）</div>
          <p class="about-hero__desc">题库、作业、题解、讨论与排行榜，构建完整的刷题与学习协作闭环。</p>
          <div class="about-hero__tags">
            <a-tag v-for="item in heroTags" :key="item" color="geekblue">{{ item }}</a-tag>
          </div>
        </div>
        <div class="about-hero__highlights">
          <div v-for="item in heroHighlights" :key="item.title" class="about-highlight">
            <component :is="item.icon" class="about-highlight__icon" />
            <div class="about-highlight__title">{{ item.title }}</div>
            <div class="about-highlight__desc">{{ item.description }}</div>
          </div>
        </div>
      </section>

      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="15">
          <a-card class="about-page__card" :bordered="false">
            <a-collapse accordion class="about-page__collapse">
            <a-collapse-panel key="result" header="Q：系统的返回信息都是什么意思？">
              <a-table
                :columns="resultColumns"
                :data-source="resultRows"
                :pagination="false"
                size="small"
                row-key="key"
              >
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'status'">
                    <a-tag v-if="record.color" :color="record.color" style="margin-right: 0">
                      {{ record.status }}
                    </a-tag>
                    <template v-else>{{ record.status }}</template>
                  </template>
                </template>
              </a-table>
            </a-collapse-panel>

            <a-collapse-panel key="usage" header="Q：如何使用本系统？">
              <a-steps direction="vertical" :items="usageSteps" />
            </a-collapse-panel>

            <a-collapse-panel key="features" header="Q：系统有哪些功能？">
              <a-list :data-source="featureList" item-layout="vertical">
                <template #renderItem="{ item }">
                  <a-list-item>
                    <a-list-item-meta>
                      <template #title>
                        <a-space>
                          <component :is="item.icon" />
                          <span>{{ item.title }}</span>
                        </a-space>
                      </template>
                      <template #description>
                        <span class="about-page__muted">{{ item.description }}</span>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </a-collapse-panel>

            <a-collapse-panel key="contact" header="Q：如何联系我们？">
              <a-descriptions :column="1" size="small" bordered>
                <a-descriptions-item label="电子邮件">
                  <a-space direction="vertical" size="small">
                    <a href="mailto:1046220903@qq.com">1046220903@qq.com</a>
                    <a href="mailto:sweu24025@stu.xujc.com">sweu24025@stu.xujc.com</a>
                  </a-space>
                </a-descriptions-item>
                <a-descriptions-item label="联系地址">
                  福建省漳州市厦门大学漳州校区
                </a-descriptions-item>
                <a-descriptions-item label="社交媒体">
                  <a-space>
                    <a href="https://github.com/Justtyn" target="_blank" rel="noreferrer">GitHub：Justtyn</a>
                    <span class="about-page__muted">Blog：Justyn</span>
                  </a-space>
                </a-descriptions-item>
              </a-descriptions>
            </a-collapse-panel>

            <a-collapse-panel key="resources" header="Q：数据结构与算法学习资源推荐">
              <a-tabs type="card" class="about-page__tabs">
                <a-tab-pane key="basic" tab="入门基础">
                  <a-typography-title :level="5">在线刷题平台</a-typography-title>
                  <a-list size="small" :data-source="resourceBasicPlatforms">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                  <a-typography-title :level="5" style="margin-top: 12px">在线教程与博客</a-typography-title>
                  <a-list size="small" :data-source="resourceBasicBlogs">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                </a-tab-pane>

                <a-tab-pane key="visual" tab="可视化工具">
                  <a-list size="small" :data-source="resourceVisualization">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                </a-tab-pane>

                <a-tab-pane key="courses" tab="系统化课程">
                  <a-typography-title :level="5">大学公开课</a-typography-title>
                  <a-list size="small" :data-source="resourceCoursesPublic">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                  <a-typography-title :level="5" style="margin-top: 12px">MOOCs 平台</a-typography-title>
                  <a-list size="small" :data-source="resourceCoursesMooc">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                </a-tab-pane>

                <a-tab-pane key="contest" tab="竞赛与实践">
                  <a-list size="small" :data-source="resourceContest">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                </a-tab-pane>

                <a-tab-pane key="community" tab="社区与论坛">
                  <a-list size="small" :data-source="resourceCommunity">
                    <template #renderItem="{ item }">
                      <a-list-item>{{ item }}</a-list-item>
                    </template>
                  </a-list>
                </a-tab-pane>
              </a-tabs>
            </a-collapse-panel>
            </a-collapse>
          </a-card>
        </a-col>

        <a-col :xs="24" :lg="9">
          <a-card class="about-page__card" :bordered="false" title="项目信息">
            <a-space direction="vertical" style="width: 100%">
              <a-descriptions :column="1" size="small">
                <a-descriptions-item label="系统">
                  Re Online Judge
                </a-descriptions-item>
                <a-descriptions-item label="适用场景">
                  课程作业 / 训练题库 / 讨论交流
                </a-descriptions-item>
                <a-descriptions-item label="说明">
                  评测结果以服务端为准；若出现 Internal Error 请联系管理员。
                </a-descriptions-item>
              </a-descriptions>
              <a-divider style="margin: 8px 0" />
              <div class="about-side__section">
                <div class="about-side__title">核心模块</div>
                <div class="about-side__tags">
                  <a-tag color="blue">题库</a-tag>
                  <a-tag color="cyan">作业</a-tag>
                  <a-tag color="geekblue">题解</a-tag>
                  <a-tag color="gold">讨论</a-tag>
                  <a-tag color="purple">提交记录</a-tag>
                </div>
              </div>
              <div class="about-page__footer">
                Copyright © 2024 - 2025 XUJC SWEU24025-焦梓豪
              </div>
            </a-space>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import type { TableColumnsType } from 'ant-design-vue';
import PageContainer from '@/components/common/PageContainer.vue';
import {
  BookOutlined,
  CodeOutlined,
  LineChartOutlined,
  NotificationOutlined,
  TeamOutlined,
  TrophyOutlined,
} from '@ant-design/icons-vue';

type ResultRow = {
  key: string;
  status: string;
  description: string;
  color?: string;
};

const resultRows: ResultRow[] = [
  { key: 'in-queue', status: 'In Queue', description: '您的代码已提交并正在等待评测系统处理，请耐心等待。', color: 'blue' },
  { key: 'processing', status: 'Processing', description: '评测系统正在处理您的代码，结果将很快出现。', color: 'geekblue' },
  { key: 'accepted', status: 'Accepted', description: '恭喜！您的代码通过了所有测试用例，解答正确。', color: 'green' },
  { key: 'wa', status: 'Wrong Answer', description: '您的代码输出结果与预期答案不符，请检查算法逻辑。', color: 'red' },
  { key: 'tle', status: 'Time Limit Exceeded', description: '您的代码执行时间超出了题目限制，需要优化算法提高效率。', color: 'volcano' },
  { key: 'ce', status: 'Compilation Error', description: '您的代码无法编译通过，请检查语法错误。', color: 'orange' },
  {
    key: 'sigsegv',
    status: 'Runtime Error (SIGSEGV)',
    description: '程序执行时发生段错误，通常是因为非法内存访问，如数组越界。',
    color: 'magenta',
  },
  { key: 'sigxfsz', status: 'Runtime Error (SIGXFSZ)', description: '程序生成的文件超过系统允许的大小限制。', color: 'purple' },
  { key: 'sigfpe', status: 'Runtime Error (SIGFPE)', description: '程序执行时发生浮点错误，如除以零或浮点溢出。', color: 'purple' },
  { key: 'sigabrt', status: 'Runtime Error (SIGABRT)', description: '程序执行被中止，通常是由于断言失败或调用 abort() 函数。', color: 'purple' },
  { key: 'nzec', status: 'Runtime Error (NZEC)', description: '程序以非零状态退出，表示执行过程中出现了错误。', color: 'purple' },
  { key: 'other', status: 'Runtime Error (Other)', description: '程序执行时发生了其他类型的运行时错误。', color: 'purple' },
  { key: 'ie', status: 'Internal Error', description: '评测系统内部错误，请联系管理员处理。', color: 'default' },
  { key: 'efe', status: 'Exec Format Error', description: '执行文件格式错误，无法运行生成的程序。', color: 'default' },
];

const resultColumns: TableColumnsType<ResultRow> = [
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 210,
  },
  { title: '说明', dataIndex: 'description', key: 'description' },
];

const heroTags = ['题库', '作业', '题解', '讨论', '排行榜', '公告'];

const heroHighlights = [
  {
    title: '高效评测',
    description: '多语言在线评测与实时反馈，帮助你快速定位问题。',
    icon: TrophyOutlined,
  },
  {
    title: '课程协作',
    description: '作业管理与班级协作，让学习过程更有节奏。',
    icon: TeamOutlined,
  },
  {
    title: '社区沉淀',
    description: '题解分享与讨论交流，构建可持续的学习社区。',
    icon: BookOutlined,
  },
];

const usageSteps = [
  {
    title: '注册与登录',
    description: '首次使用需注册账号；已有账号可直接登录系统。',
  },
  {
    title: '浏览题目',
    description: '在题库中浏览题目，按难度/类型/关键词筛选（按当前版本实际开放为准）。',
  },
  {
    title: '提交代码',
    description: '进入题目详情编写解答，选择语言并提交（C++/Java/Python 等）。',
  },
  {
    title: '查看结果',
    description: '在提交记录中查看评测状态、运行时间、内存使用与详细报告。',
  },
  {
    title: '参与讨论',
    description: '在讨论区交流解题思路、分享方案或提出问题。',
  },
  {
    title: '查看排名',
    description: '在排行榜中查看解题数量与比赛排名，了解自身表现。',
  },
  {
    title: '班级功能',
    description: '加入班级后可访问班级题目与作业，并查看班级统计与排名。',
  },
  {
    title: '个人中心',
    description: '查看解题统计、提交历史、收藏题目并管理个人资料与设置。',
  },
];

const featureList = [
  { title: '题库系统', description: '提供丰富编程题目，支持多语言提交与自动评测。', icon: CodeOutlined },
  { title: '在线评测', description: '支持在线编译运行与多维评测信息展示。', icon: TrophyOutlined },
  { title: '排行榜', description: '实时更新解题数量与排名，促进学习与竞赛。', icon: LineChartOutlined },
  { title: '数据分析', description: '提供个人统计、能力分析与进步趋势等可视化能力。', icon: LineChartOutlined },
  { title: '社区互动', description: '讨论区支持分享解法与交流疑问，沉淀学习成果。', icon: TeamOutlined },
  { title: '班级管理', description: '支持班级创建与管理，提供班级作业与统计能力。', icon: TeamOutlined },
  { title: '公告系统', description: '发布系统公告、比赛通知与重要更新。', icon: NotificationOutlined },
  { title: '资源推荐', description: '汇总学习资源与路径，帮助系统化提升。', icon: BookOutlined },
];

const resourceBasicPlatforms = [
  'LeetCode（力扣）- 国内热门刷题平台',
  'HackerRank - 多领域算法练习平台',
  'Codeforces - 国际顶级竞赛平台',
  'AtCoder - 日本高质量竞赛平台',
  'NeetCode - 面试题目录与刷题路径',
];

const resourceBasicBlogs = [
  'GeeksforGeeks - 详尽的算法与数据结构教程',
  'Programiz - 初学者友好的可视化教程',
  'Rosetta Code - 多语言算法实现示例',
];

const resourceVisualization = [
  'VisuAlgo - 数据结构与算法动画演示',
  'USFCA 数据结构可视化 - 美国旧金山大学可视化平台',
  'CS 1332 Visualization Tool - 佐治亚理工大学可视化工具',
];

const resourceCoursesPublic = ['MIT OCW 6.006 Introduction to Algorithms'];
const resourceCoursesMooc = ['Coursera Algorithms Specialization', 'Udemy 算法课程'];
const resourceContest = ['Codeforces - 全球算法竞赛平台', 'AtCoder - 日本竞赛平台', 'Topcoder - 多种比赛形式'];
const resourceCommunity = [
  'Reddit r/learnprogramming - 编程学习社区',
  'Stack Overflow - 技术问答平台',
  'GeeksforGeeks 讨论区 - 算法讨论社区',
];
</script>

<style scoped lang="less">
.about-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.about-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  gap: 20px;
  padding: 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(14, 165, 233, 0.14));
  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
}

.about-hero__content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.about-hero__badge {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-muted, #64748b);
}

.about-hero__title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.about-hero__desc {
  margin: 0;
  color: var(--text-color);
  opacity: 0.82;
  line-height: 1.7;
}

.about-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.about-hero__highlights {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.about-highlight {
  border-radius: 14px;
  padding: 12px 14px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: var(--card-bg);
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 0 0 180px;
}

.about-highlight__icon {
  font-size: 18px;
  color: #0ea5e9;
}

.about-highlight__title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
}

.about-highlight__desc {
  font-size: 12px;
  color: var(--text-muted, #94a3b8);
  line-height: 1.6;
}

.about-page__card {
  background: var(--card-bg);
  border: 1px solid var(--chart-track);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.about-page__intro {
  color: var(--text-color);
  opacity: 0.82;
  margin: 8px 0 0;
}

.about-page__collapse :deep(.ant-collapse-item) {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--chart-track);
  margin-bottom: 12px;
}

.about-page__collapse :deep(.ant-collapse-header) {
  background: var(--body-bg);
  color: var(--text-color);
  font-weight: 600;
}

.about-page__collapse :deep(.ant-collapse-content) {
  background: var(--card-bg);
  color: var(--text-color);
}

.about-page__tabs :deep(.ant-tabs-nav) {
  margin-bottom: 12px;
}

.about-page__muted {
  color: var(--text-color);
  opacity: 0.78;
}

.about-page__footer {
  color: var(--text-color);
  opacity: 0.72;
  font-size: 12px;
  line-height: 1.6;
}

.about-side__section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.about-side__title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-color);
}

.about-side__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

@media (max-width: 1024px) {
  .about-hero {
    grid-template-columns: 1fr;
  }
}

:global(:root[data-theme='dark']) .about-page__card {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.28);
}

:global(:root[data-theme='dark']) .about-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(30, 64, 175, 0.35));
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.35);
}

:global(:root[data-theme='dark']) .about-highlight {
  border-color: rgba(148, 163, 184, 0.2);
  background: rgba(15, 23, 42, 0.7);
}

:global(:root[data-theme='dark']) .about-highlight__icon {
  color: #38bdf8;
}
</style>
