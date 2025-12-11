<template>
  <div class="code-editor">
    <div class="code-editor__wrapper">
      <div class="code-editor__gutter" ref="gutterRef">
        <span v-for="line in lineNumbers" :key="line">{{ line }}</span>
      </div>
      <pre class="code-editor__highlight" ref="highlightRef" aria-hidden="true"><code v-html="highlightedCode"></code></pre>
      <textarea
        ref="textareaRef"
        v-model="localValue"
        class="code-editor__input"
        :placeholder="placeholder"
        :disabled="disabled || readonly"
        :readonly="readonly"
        @input="emitChange"
        @scroll="syncScroll"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';

const props = defineProps<{
  modelValue: string;
  language?: string;
  placeholder?: string;
  disabled?: boolean;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const textareaRef = ref<HTMLTextAreaElement>();
const highlightRef = ref<HTMLElement>();
const gutterRef = ref<HTMLElement>();
const localValue = ref(props.modelValue || '');

watch(
  () => props.modelValue,
  (val) => {
    if (val !== localValue.value) {
      localValue.value = val || '';
    }
  },
);

const lineNumbers = computed(() => {
  const lines = localValue.value.split('\n').length || 1;
  return Array.from({ length: lines }, (_, i) => i + 1);
});

const escapeHtml = (input: string) =>
  input
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');

const applyHighlight = (code: string) => {
  let escaped = escapeHtml(code);

  escaped = escaped.replace(/(\/\*[\s\S]*?\*\/)/g, '<span class="token comment">$1</span>');
  escaped = escaped.replace(/(\/\/[^\n]*)/g, '<span class="token comment">$1</span>');
  escaped = escaped.replace(/("[^"\\]*(?:\\.[^"\\]*)*")/g, '<span class="token string">$1</span>');
  escaped = escaped.replace(/('[^'\\]*(?:\\.[^'\\]*)*')/g, '<span class="token string">$1</span>');

  const keywords =
    '\\b(int|float|double|char|class|struct|public|private|protected|return|if|else|for|while|do|switch|case|break|continue|void|static|namespace|using|include|import|def|print|True|False|null|nullptr|var|let|const|async|await|new|this|extends|try|catch|finally|throw)\\b';
  const keywordRegex = new RegExp(keywords, 'g');
  escaped = escaped.replace(keywordRegex, '<span class="token keyword">$1</span>');

  return escaped;
};

const highlightedCode = computed(() => applyHighlight(localValue.value || ' '));

const emitChange = () => {
  emit('update:modelValue', localValue.value);
};

const syncScroll = () => {
  const top = textareaRef.value?.scrollTop || 0;
  if (highlightRef.value) highlightRef.value.scrollTop = top;
  if (gutterRef.value) gutterRef.value.scrollTop = top;
};
</script>

<style scoped lang="less">
.code-editor {
  border: 1px solid var(--border-color, rgba(0, 0, 0, 0.08));
  border-radius: 8px;
  background: var(--card-bg, #0f172a0a);
  overflow: hidden;
  --gutter-width: 52px;
}

.code-editor__wrapper {
  position: relative;
  display: flex;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
  font-size: 13px;
  line-height: 1.5;
  min-height: 260px;
  max-height: 560px;
}

.code-editor__gutter {
  padding: 12px 8px 12px 12px;
  width: var(--gutter-width);
  background: var(--body-bg, #0b12201a);
  color: var(--text-muted, #8c8c8c);
  text-align: right;
  user-select: none;
  border-right: 1px solid var(--border-color, rgba(0, 0, 0, 0.08));
  overflow: hidden;
}

.code-editor__gutter span {
  display: block;
  height: 20px;
}

.code-editor__highlight {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: var(--gutter-width);
  padding: 12px;
  margin: 0;
  pointer-events: none;
  white-space: pre-wrap;
  word-break: break-word;
  overflow: auto;
  color: var(--text-color, #0f172a);
}

.code-editor__highlight code {
  display: block;
}

.code-editor__input {
  position: relative;
  z-index: 1;
  flex: 1;
  border: none;
  outline: none;
  resize: vertical;
  padding: 12px;
  background: transparent;
  color: transparent;
  caret-color: var(--text-color, #0f172a);
  text-shadow: none;
  line-height: 1.5;
  min-height: 260px;
  max-height: 560px;
  overflow: auto;
}

.code-editor__input::placeholder {
  color: var(--text-muted, #8c8c8c);
}

.token.keyword {
  color: #0070f3;
}

.token.string {
  color: #e67e22;
}

.token.comment {
  color: #8c8c8c;
}

.code-editor__highlight::-webkit-scrollbar,
.code-editor__input::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.code-editor__highlight::-webkit-scrollbar-track,
.code-editor__input::-webkit-scrollbar-track {
  background: var(--body-bg, rgba(15, 23, 42, 0.04));
}

.code-editor__highlight::-webkit-scrollbar-thumb,
.code-editor__input::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.35);
  border-radius: 4px;
}

.code-editor__highlight::-webkit-scrollbar-thumb:hover,
.code-editor__input::-webkit-scrollbar-thumb:hover {
  background: rgba(99, 102, 241, 0.5);
}
</style>
