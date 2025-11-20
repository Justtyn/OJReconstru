<template>
  <div :class="['auth-layout', `auth-layout--${theme}`]">
    <div class="auth-layout__panel">
      <div class="auth-layout__brand">
        <img src="/logo.svg" alt="Re Online Judge" />
        <div>
          <p class="brand-title">Re Online Judge</p>
          <p class="brand-desc">现代化编程训练与评测平台</p>
        </div>
      </div>
      <div class="auth-layout__switch">
        <ThemeSwitcher />
      </div>
    </div>
    <div class="auth-layout__content">
      <div class="auth-layout__card">
        <slot />
      </div>
      <footer class="auth-layout__footer">© {{ year }} Re Online Judge</footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useSettingsStore } from '@/stores/settings';
import ThemeSwitcher from '@/components/common/ThemeSwitcher.vue';

const settingsStore = useSettingsStore();
const theme = computed(() => settingsStore.effectiveTheme);
const year = new Date().getFullYear();
</script>

<style scoped lang="less">
.auth-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 360px 1fr;
  transition: background 0.3s;

  &__panel {
    padding: 48px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  &__content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 32px;
  }

  &__card {
    width: 420px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 24px;
    padding: 32px;
    box-shadow: 0 20px 60px rgba(15, 23, 42, 0.2);
    backdrop-filter: blur(8px);
    color: var(--text-color);
  }

  &__brand {
    display: flex;
    gap: 16px;
    align-items: center;

    img {
      width: 48px;
      height: 48px;
    }

    .brand-title {
      font-size: 20px;
      font-weight: 600;
      margin: 0;
    }

    .brand-desc {
      margin: 4px 0 0;
      color: rgba(15, 23, 42, 0.7);
    }
  }

  &__footer {
    margin-top: 24px;
    color: rgba(15, 23, 42, 0.6);
  }
}

.auth-layout--dark {
  background: radial-gradient(circle at top, #1e293b, #0f172a);

  .auth-layout__card {
    background: rgba(15, 23, 42, 0.92);
    color: #e2e8f0;
  }

  .brand-desc,
  .auth-layout__footer {
    color: rgba(226, 232, 240, 0.7);
  }
}

.auth-layout--light {
  background: linear-gradient(135deg, #dfe9f3 0%, #ffffff 100%);
}
</style>
