<template>
  <section class="page-container">
    <header v-if="$slots.header || title" class="page-container__header">
      <div class="page-container__title">
        <slot name="header">
          <h1>{{ title }}</h1>
        </slot>
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
defineProps<{
  title?: string;
}>();
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
}

:global(.page-container__body) {
  color: var(--text-color);
  background: var(--card-bg);
}
</style>
