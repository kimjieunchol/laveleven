<script setup>
import { ref } from 'vue';
import NewWorkModal from './NewWorkModal.vue';

const props = defineProps({
  onNavigate: {
    type: Function,
    required: true,
  },
  currentUser: {
    type: Object,
    default: null,
  },
  currentView: {
    type: String,
    default: '',
  },
  setCurrentView: {
    type: Function,
    default: null,
  },
});

const isModalOpen = ref(false);

const handleNewWork = (formData) => {
  localStorage.setItem('currentWorkData', JSON.stringify(formData));
  props.onNavigate('new', formData);
};

const handleNewClick = () => {
  if (props.setCurrentView) {
    props.setCurrentView('new');
  }
  isModalOpen.value = true;
};

const handleLoadClick = () => {
  if (props.setCurrentView) {
    props.setCurrentView('load');
  }
};
</script>

<template>
  <div class="bg-white border-b border-[#e0e0e0] px-4 py-2 flex items-center justify-between">
    <div class="flex items-center gap-4">
      <button
        type="button"
        @click="props.onNavigate('main')"
        class="text-xl font-bold text-black hover:text-[#1565c0] cursor-pointer"
      >
        Labeleven
      </button>

      <div v-if="props.currentView && props.setCurrentView" class="flex gap-2 ml-4">
        <button
          type="button"
          @click="handleNewClick"
          :class="[
            'px-3 py-1 text-xs',
            props.currentView === 'new' ? 'text-black font-semibold' : 'text-black hover:bg-[#f5f5f5]',
          ]"
        >
          New
        </button>
        <button
          type="button"
          @click="handleLoadClick"
          :class="[
            'px-3 py-1 text-xs',
            props.currentView === 'load' ? 'text-black font-semibold' : 'text-black hover:bg-[#f5f5f5]',
          ]"
        >
          Load
        </button>
      </div>
    </div>

    <div class="flex gap-2 items-center">
      <button
        type="button"
        @click="props.onNavigate('mypage')"
        class="text-xs text-[#666] mr-2 hover:underline"
      >
        {{ props.currentUser?.userId }}
        <span v-if="props.currentUser?.team" class="ml-1 text-[#999]">
          ({{ props.currentUser.team }})
        </span>
      </button>

      <button
        type="button"
        @click="props.onNavigate('login')"
        class="px-4 py-1 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] text-xs"
      >
        로그아웃
      </button>
    </div>
  </div>

  <NewWorkModal
    :is-open="isModalOpen"
    :on-close="() => (isModalOpen = false)"
    :on-submit="handleNewWork"
    :current-user="props.currentUser"
  />
</template>
