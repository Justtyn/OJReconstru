<template>
  <a-segmented
    v-model:value="current"
    :options="options"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import { computed, h } from 'vue';
import { BulbOutlined, BgColorsOutlined, DesktopOutlined } from '@ant-design/icons-vue';
import type { ThemeMode } from '@/types/settings';
import { useSettingsStore } from '@/stores/settings';

const settingsStore = useSettingsStore();

const options = [
  { label: '浅色', value: 'light', icon: () => h(BulbOutlined) },
  { label: '系统', value: 'system', icon: () => h(DesktopOutlined) },
  { label: '深色', value: 'dark', icon: () => h(BgColorsOutlined) },
];

const current = computed({
  get: () => settingsStore.themeMode,
  set: (value: ThemeMode) => settingsStore.setTheme(value),
});

const handleChange = (value: ThemeMode) => {
  settingsStore.setTheme(value);
};
</script>
