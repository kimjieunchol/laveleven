<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  onClose: {
    type: Function,
    required: true,
  },
  onSubmit: {
    type: Function,
    required: true,
  },
  currentUser: {
    type: Object,
    default: null,
  },
});

const today = computed(() => new Date().toISOString().split('T')[0]);

const createDefaultForm = () => ({
  id: '',
  title: '',
  productName: '',
  createdAt: today.value,
  createdBy: props.currentUser?.userId || '',
  version: '',
  team: props.currentUser?.team || '',
  updatedAt: today.value,
});

const formData = ref(createDefaultForm());

watch(
  () => props.isOpen,
  (open) => {
    if (open) {
      formData.value = createDefaultForm();
    }
  },
);

watch(
  () => props.currentUser,
  () => {
    formData.value.createdBy = props.currentUser?.userId || '';
    formData.value.team = props.currentUser?.team || '';
  },
);

const handleSubmit = () => {
  if (!formData.value.id || !formData.value.title || !formData.value.productName || !formData.value.version) {
    alert('모든 필수 항목을 입력해주세요.');
    return;
  }
  props.onSubmit({ ...formData.value });
  props.onClose();
};
</script>

<template>
  <div
    v-if="props.isOpen"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
      <div class="px-6 py-4 border-b border-[#e0e0e0] flex items-center justify-between">
        <h2 class="text-lg font-semibold text-black">새 작업 생성</h2>
        <button
          @click="props.onClose"
          class="text-[#666] hover:text-black text-xl leading-none"
        >
          ×
        </button>
      </div>

      <div class="px-6 py-4">
        <div class="space-y-4">
          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">Id</label>
            <input
              v-model="formData.id"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">Title</label>
            <input
              v-model="formData.title"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">ProductName</label>
            <input
              v-model="formData.productName"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">CreatedAt</label>
            <input
              v-model="formData.createdAt"
              type="date"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">CreatedBy</label>
            <input
              v-model="formData.createdBy"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm bg-[#f5f5f5] focus:outline-none focus:border-[#1565c0]"
              readonly
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">Version</label>
            <input
              v-model="formData.version"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">Team</label>
            <input
              v-model="formData.team"
              type="text"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>

          <div>
            <label class="block text-xs text-[#666] mb-1 text-right">UpdatedAt</label>
            <input
              v-model="formData.updatedAt"
              type="date"
              class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            />
          </div>
        </div>

        <div class="flex gap-3 justify-end mt-6 pt-4 border-t border-[#e0e0e0]">
          <button
            @click="props.onClose"
            class="px-5 py-2 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] text-sm"
          >
            취소
          </button>
          <button
            @click="handleSubmit"
            class="px-5 py-2 bg-[#1565c0] hover:bg-[#0d47a1] text-white text-sm"
          >
            생성하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
