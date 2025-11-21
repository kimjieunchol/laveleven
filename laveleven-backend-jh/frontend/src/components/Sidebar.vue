<script setup>
const props = defineProps({
  activeSidebarTab: {
    type: String,
    default: '',
  },
  setActiveSidebarTab: {
    type: Function,
    default: null,
  },
  onNavigate: {
    type: Function,
    required: true,
  },
  currentPage: {
    type: String,
    default: '',
  },
});

const updateTab = (tab) => {
  if (props.setActiveSidebarTab) {
    props.setActiveSidebarTab(tab);
  }
  if (props.currentPage !== 'mypage') {
    props.onNavigate('mypage');
  }
};
</script>

<template>
  <div class="w-48 bg-[#fafafa] border-r border-[#e0e0e0] flex-shrink-0">
    <div class="p-3">
      <div class="space-y-1">
        <button
          type="button"
          @click="updateTab('my-tasks')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.activeSidebarTab === 'my-tasks' && props.currentPage === 'mypage'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          내 업무 목록
        </button>
        <button
          type="button"
          @click="updateTab('completed')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.activeSidebarTab === 'completed' && props.currentPage === 'mypage'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          완료된 업무
        </button>
        <button
          type="button"
          @click="updateTab('in-progress')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.activeSidebarTab === 'in-progress' && props.currentPage === 'mypage'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          진행중인 업무
        </button>
        <button
          type="button"
          @click="updateTab('pending')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.activeSidebarTab === 'pending' && props.currentPage === 'mypage'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          검수대기 업무
        </button>
      </div>
    </div>
    <div class="border-t border-[#e0e0e0] mt-4 pt-3 px-3">
      <div class="space-y-1">
        <button
          type="button"
          @click="props.onNavigate('settings')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.currentPage === 'settings'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          설정
        </button>
        <button
          type="button"
          @click="props.onNavigate('stats')"
          :class="[
            'w-full text-left px-3 py-2 text-xs rounded',
            props.currentPage === 'stats'
              ? 'bg-[#1565c0] text-white'
              : 'text-black hover:bg-[#f0f0f0]',
          ]"
        >
          통계
        </button>
      </div>
    </div>
  </div>
</template>
