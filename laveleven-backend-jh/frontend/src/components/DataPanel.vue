<script setup>
import { ref } from 'vue';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';

defineProps({
  extractedData: {
    type: Array,
    default: () => [],
  },
});

const isExpanded = ref(true);
</script>

<template>
  <button
    @click="isExpanded = true"
    :class="[
      'bg-[#e8e8e8] border-l border-[#e0e0e0] hover:bg-[#d8d8d8] flex items-center justify-center flex-shrink-0 transition-all duration-300',
      isExpanded ? 'w-0 opacity-0' : 'w-8 opacity-100',
    ]"
  >
    <ChevronLeft class="w-4 h-4 text-black" />
  </button>

  <div
    :class="[
      'bg-white border-l border-[#e0e0e0] flex flex-col flex-shrink-0 transition-all duration-300 ease-in-out overflow-hidden',
      isExpanded ? 'w-[300px] opacity-100' : 'w-0 opacity-0',
    ]"
  >
    <button
      @click="isExpanded = false"
      class="border-b border-[#e0e0e0] px-4 py-3 hover:bg-[#f5f5f5] flex items-center justify-between text-left"
    >
      <div>
        <h3 class="text-black text-xs whitespace-nowrap">추출 데이터</h3>
      </div>
      <ChevronRight class="w-4 h-4 text-black" />
    </button>
  </div>
</template>
