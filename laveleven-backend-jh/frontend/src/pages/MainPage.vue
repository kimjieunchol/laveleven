<script setup>
import { ref } from 'vue';
import NewWorkModal from '../components/NewWorkModal.vue';

const props = defineProps({
  onNavigate: {
    type: Function,
    required: true,
  },
  currentUser: {
    type: Object,
    default: null,
  },
});

const isModalOpen = ref(false);

const handleNewWork = (formData) => {
  localStorage.setItem('currentWorkData', JSON.stringify(formData));
  props.onNavigate('new', formData);
};
</script>

<template>
  <div class="h-screen flex flex-col bg-white">
    <div class="bg-white border-b border-[#e0e0e0] px-4 py-2 flex items-center justify-between">
      <h1 class="text-xl font-bold text-black">Labeleven</h1>
      <div class="flex gap-2 items-center">
        <button
          @click="props.onNavigate('mypage')"
          class="text-xs text-[#666] mr-2 hover:underline"
        >
          {{ props.currentUser?.userId }}
        </button>
        <button
          @click="props.onNavigate('login')"
          class="px-4 py-1 border bg-white hover:bg-[#f5f5f5] text-xs"
        >
          로그아웃
        </button>
      </div>
    </div>

    <div class="flex-1 flex items-center justify-center bg-[#fafafa]">
      <div class="text-center space-y-8">
        <h2 class="text-3xl font-bold text-black">환영합니다</h2>
        <p class="text-[#666] text-sm">새 작업을 준비하세요</p>
        <div class="flex gap-4 justify-center">
          <button
            @click="isModalOpen = true"
            class="px-8 py-4 bg-[#1565c0] hover:bg-[#0d47a1] text-white text-sm"
          >
            새 작업 만들기
          </button>
          <button
            @click="props.onNavigate('load')"
            class="px-8 py-4 bg-white border hover:bg-[#f5f5f5] text-sm"
          >
            기존 작업 불러오기
          </button>
        </div>
      </div>
    </div>

    <NewWorkModal
      :is-open="isModalOpen"
      :on-close="() => (isModalOpen = false)"
      :on-submit="handleNewWork"
      :current-user="props.currentUser"
    />
  </div>
</template>
