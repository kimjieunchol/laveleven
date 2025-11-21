<script setup>
import { Upload } from 'lucide-vue-next';

const props = defineProps({
  uploadedImage: {
    type: String,
    default: null,
  },
  setUploadedImage: {
    type: Function,
    required: true,
  },
  ocrResult: {
    type: Object,
    default: null,
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  handleExport: {
    type: Function,
    required: true,
  },
  handleCancel: {
    type: Function,
    required: true,
  },
  handleApply: {
    type: Function,
    required: true,
  },
  callOcr: {
    type: Function,
    required: true,
  },
});

const handleImageUpload = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = async (loadEvent) => {
    const base64 = loadEvent.target?.result;
    props.setUploadedImage(base64);
    if (base64) {
      await props.callOcr(base64);
    }
  };
  reader.readAsDataURL(file);
};
</script>

<template>
  <div class="flex-1 bg-white flex overflow-hidden">
    <div class="w-[600px] flex flex-col border-r border-[#e0e0e0] flex-shrink-0 bg-white">
      <div class="bg-[#f5f5f5] border-b border-[#e0e0e0] px-4 py-2">
        <span class="text-black text-xs">원본 이미지</span>
      </div>
      <div class="flex-1 p-4 flex items-center justify-center bg-white">
        <label
          v-if="!props.uploadedImage"
          class="cursor-pointer flex flex-col items-center justify-center w-full h-full hover:bg-[#fafafa]"
        >
          <Upload class="w-12 h-12 text-[#999]" />
          <input type="file" accept="image/*" class="hidden" @change="handleImageUpload" />
        </label>
        <img
          v-else
          :src="props.uploadedImage"
          alt="Uploaded document"
          class="border-2 border-[#d0d0d0] max-w-full max-h-full object-contain"
        />
      </div>
    </div>

    <div class="flex-1 bg-white flex flex-col">
      <div class="bg-[#f5f5f5] border-b border-[#e0e0e0] px-4 py-2">
        <span class="text-black text-xs">OCR 결과</span>
      </div>

      <div class="flex-1 flex flex-col p-6">
        <div class="flex-1 flex items-center justify-center">
          <span v-if="props.isLoading" class="text-[#666]">처리 중...</span>

          <div v-else-if="props.uploadedImage" class="space-y-6">
            <div class="flex justify-center">
              <img
                :src="props.uploadedImage"
                alt="OCR Result"
                class="border-2 border-[#d0d0d0] shadow-lg max-w-full max-h-[60vh] object-contain"
              />
            </div>
            <div v-if="props.ocrResult" class="space-y-3 text-xs max-w-4xl mx-auto">
              <div
                v-for="(value, key) in props.ocrResult"
                :key="key"
                class="p-4 bg-[#f5f5f5] border border-[#d0d0d0]"
              >
                <div class="text-[#666] mb-2">{{ key }}</div>
                <div class="text-black">{{ String(value) }}</div>
              </div>
            </div>
          </div>

          <span v-else class="text-[#999] mb-4 text-center text-xs">
            이미지를 업로드하면 결과가 표시됩니다
          </span>
        </div>

        <div class="flex items-center justify-between pt-6 pb-2">
          <button
            @click="props.handleExport"
            class="bg-[#1565c0] hover:bg-[#0d47a1] text-white px-3 py-1 text-xs border border-[#d0d0d0]"
          >
            내보내기
          </button>

          <div class="flex gap-6">
            <button
              @click="props.handleCancel"
              class="bg-[#808080] hover:bg-[#707070] text-white px-6 py-1 text-xs border border-[#d0d0d0]"
            >
              취소
            </button>
            <button
              @click="props.handleApply"
              class="bg-[#8bc34a] hover:bg-[#7cb342] text-white px-6 py-1 text-xs border border-[#d0d0d0]"
            >
              적용
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
