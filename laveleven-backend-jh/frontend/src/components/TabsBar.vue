<script setup>
const props = defineProps({
  activeTab: {
    type: String,
    required: true,
  },
  setActiveTab: {
    type: Function,
    required: true,
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  onRerun: {
    type: Function,
    required: true,
  },
  onValidate: {
    type: Function,
    required: true,
  },
});

const tabs = ['1. Scan', '2. Schema', '3. Translate', '4. Sketch', '5. Validate'];
</script>

<template>
  <div class="bg-[#f5f5f5] border-b border-[#e0e0e0] flex items-center justify-between px-2">
    <div class="flex">
      <button
        type="button"
        v-for="tab in tabs"
        :key="tab"
        @click="props.setActiveTab(tab)"
        :class="[
          'px-6 py-2 text-black text-xs',
          props.activeTab === tab
            ? 'bg-white border-t-2 border-x border-[#e0e0e0] border-t-[#1565c0]'
            : 'bg-[#f5f5f5] hover:bg-[#e8e8e8]',
        ]"
      >
        {{ tab }}
      </button>
    </div>

    <div class="flex gap-2 py-1">
      <button
        type="button"
        @click="props.onRerun"
        :disabled="props.isLoading"
        class="px-4 py-1 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] text-xs disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {{ props.isLoading ? '처리중...' : 'rerun' }}
      </button>
      <button
        type="button"
        @click="props.onValidate"
        class="px-4 py-1 bg-[#dc143c] hover:bg-[#c01030] text-white border border-[#d0d0d0] text-xs"
      >
        검수
      </button>
    </div>
  </div>
</template>
