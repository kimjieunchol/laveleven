<script setup>
import { ref } from 'vue';

const props = defineProps({
  onSelectItem: {
    type: Function,
    required: true,
  },
});

const items = ref([]);
const selectedId = ref(null);

const handleSelectItem = (item) => {
  selectedId.value = item.id;
  props.onSelectItem(item);
};
</script>

<template>
  <div class="w-85 bg-white border-r border-[#e0e0e0] flex flex-col flex-shrink-0">
    <div class="bg-[#f5f5f5] border-b border-[#e0e0e0] px-4 py-2">
      <span class="text-black text-xs font-semibold">Works</span>
    </div>
    <div class="flex-1 overflow-auto">
      <table class="w-full text-xs">
        <thead class="bg-[#f5f5f5] border-b border-[#e0e0e0] sticky top-0">
          <tr>
            <th class="px-1 py-2"></th>
            <th class="px-2 py-2 text-left text-[#666]">작업명</th>
            <th class="px-2 py-2 text-left text-[#666]">제품명</th>
            <th class="px-2 py-2 text-left text-[#666]">작성자명</th>
            <th class="px-2 py-2 text-left text-[#666]">작성일</th>
            <th class="px-2 py-2 text-left text-[#666]">버전</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="items.length === 0">
            <td colspan="4" class="px-3 py-8 text-center text-[#999]">
              저장된 목록이 없습니다
            </td>
          </tr>
          <tr
            v-for="item in items"
            v-else
            :key="item.id"
            @click="handleSelectItem(item)"
            :class="[
              'border-b border-[#f0f0f0] hover:bg-[#f5f5f5] cursor-pointer',
              selectedId === item.id ? 'bg-[#e3f2fd]' : '',
            ]"
          >
            <td class="px-3 py-2">
              <input
                type="checkbox"
                class="w-3 h-3"
                :checked="selectedId === item.id"
                @change.stop
              />
            </td>
            <td class="px-3 py-2">{{ item.name }}</td>
            <td class="px-3 py-2">{{ item.createdOn }}</td>
            <td class="px-3 py-2">{{ item.createdBy }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="border-t border-[#e0e0e0] px-4 py-2 text-xs text-[#666]">
      {{ items.length }} items
    </div>
  </div>
</template>
