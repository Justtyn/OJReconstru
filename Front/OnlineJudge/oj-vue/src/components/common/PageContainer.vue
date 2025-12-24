<template>
  <section class="page-container">
    <header v-if="$slots.header || title" class="page-container__header">
      <div class="page-container__title">
        <div class="page-container__title-row">
          <a-button v-if="shouldShowBack" type="text" size="small" class="page-container__back" @click="handleBack">
            <template #icon>
              <LeftOutlined />
            </template>
            <span v-if="backText">{{ backText }}</span>
          </a-button>
          <div class="page-container__heading">
            <slot name="header">
              <h1>{{ title }}</h1>
            </slot>
            <div v-if="subtitle" class="page-container__subtitle">{{ subtitle }}</div>
          </div>
        </div>
      </div>
      <div class="page-container__extra">
        <slot name="extra" />
      </div>
    </header>
    <div class="page-container__body">
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { LeftOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
  title?: string;
  subtitle?: string;
  showBack?: boolean;
  backText?: string;
  backTo?: string;
}>();

const emit = defineEmits<{
  (e: 'back'): void;
}>();

const route = useRoute();
const router = useRouter();

const routeSegments = computed(() => route.path.split('/').filter(Boolean));
const isAdminRoute = computed(() => route.path.startsWith('/admin/'));
const resolveParentPath = () => {
  const segments = routeSegments.value;
  if (segments.length <= 1) return '';
  for (let i = segments.length - 1; i >= 1; i -= 1) {
    const candidate = `/${segments.slice(0, i).join('/')}`;
    const resolved = router.resolve(candidate);
    const lastMatched = resolved.matched[resolved.matched.length - 1];
    if (lastMatched && lastMatched.name !== 'NotFound') {
      return candidate;
    }
  }
  return '';
};

const shouldShowBack = computed(() => {
  if (typeof props.showBack === 'boolean') return props.showBack;
  if (!isAdminRoute.value) return false;
  return routeSegments.value.length > 2;
});

const handleBack = () => {
  emit('back');
  if (props.backTo) {
    router.push(props.backTo);
    return;
  }
  const parentPath = resolveParentPath();
  if (parentPath) {
    if (parentPath === '/admin') {
      router.push('/admin/overview');
      return;
    }
    router.push(parentPath);
    return;
  }
  router.back();
};
</script>

<style scoped lang="less">
.page-container {
  padding: 24px;
  background-color: var(--card-bg);
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  min-height: calc(100vh - 160px);

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  &__title h1 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: var(--text-color);
  }

  &__title-row {
    display: flex;
    align-items: flex-start;
    gap: 8px;
  }

  &__back {
    margin-left: -8px;
    color: var(--text-muted, #94a3b8);
  }

  &__heading {
    min-width: 0;
  }

  &__subtitle {
    margin-top: 4px;
    color: var(--text-muted, #94a3b8);
    font-size: 12px;
    line-height: 1.4;
  }
}

:global(.page-container__body) {
  color: var(--text-color);
  background: var(--card-bg);
}
</style>
