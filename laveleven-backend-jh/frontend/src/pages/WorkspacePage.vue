<script setup>
import { ref, watch } from 'vue';
import TopBar from '../components/TopBar.vue';
import TabsBar from '../components/TabsBar.vue';
import FormPanel from '../components/FormPanel.vue';
import DocumentViewer from '../components/DocumentViewer.vue';
import DataPanel from '../components/DataPanel.vue';
import LoadView from '../components/LoadView.vue';
import usePipeline from '../hooks/usePipeline';

const props = defineProps({
  onNavigate: {
    type: Function,
    required: true,
  },
  currentUser: {
    type: Object,
    default: null,
  },
  initialView: {
    type: String,
    default: 'new',
  },
  workData: {
    type: Object,
    default: null,
  },
});

const currentView = ref(props.initialView);
const activeTab = ref('1. Scan');
const { uploadedImage, ocrResult, isLoading, handlers, formData, setFormData } = usePipeline();

// workData가 있고 new 뷰일 때만 formData 설정
watch(
  () => props.workData,
  (value) => {
    if (value && currentView.value === 'new') {
      setFormData({
        id: value.id || '',
        title: value.title || '',
        productName: value.productName || '',
        createdAt: value.createdAt || '',
        createdBy: value.createdBy || props.currentUser?.userId || '',
        version: value.version || '',
        team: value.team || props.currentUser?.team || '',
        updatedAt: value.updatedAt || '',
      });
    }
  },
  { immediate: true },
);

watch(
  () => props.initialView,
  (value) => {
    currentView.value = value;
  },
  { immediate: true },
);

// currentView가 변경될 때 formData와 이미지 초기화
watch(
  () => currentView.value,
  (newView, oldView) => {
    // 뷰가 실제로 변경되었을 때만
    if (newView !== oldView) {
      // 이미지 초기화
      handlers.setUploadedImage(null);
      
      // formData 초기화 (빈 값으로)
      setFormData({
        id: '',
        title: '',
        productName: '',
        createdAt: '',
        createdBy: props.currentUser?.userId || '',
        version: '',
        team: props.currentUser?.team || '',
        updatedAt: '',
      });

      // new 뷰로 변경되고 workData가 있으면 다시 설정
      if (newView === 'new' && props.workData) {
        setFormData({
          id: props.workData.id || '',
          title: props.workData.title || '',
          productName: props.workData.productName || '',
          createdAt: props.workData.createdAt || '',
          createdBy: props.workData.createdBy || props.currentUser?.userId || '',
          version: props.workData.version || '',
          team: props.workData.team || props.currentUser?.team || '',
          updatedAt: props.workData.updatedAt || '',
        });
      }
    }
  },
);

const extractedData = [
  { label: 'id', value: '' },
  { label: 'selection', value: '' },
  { label: 'missing', value: '' },
  { label: 'answer', value: '' },
  { label: 'reference', value: '' },
  { label: 'source', value: '' },
];

const setWorkspaceView = (view) => {
  currentView.value = view;
};

const setActiveTabValue = (tab) => {
  activeTab.value = tab;
};
</script>

<template>
  <div class="h-screen flex flex-col bg-white">
    <TopBar
      :on-navigate="props.onNavigate"
      :current-user="props.currentUser"
      :current-view="currentView"
      :set-current-view="setWorkspaceView"
    />

    <TabsBar
      :active-tab="activeTab"
      :set-active-tab="setActiveTabValue"
      :is-loading="isLoading"
      :on-rerun="handlers.handleRerun"
      :on-validate="handlers.handleValidate"
    />

    <div class="flex-1 flex">
      <LoadView v-if="currentView === 'load'" :on-select-item="() => {}" />
      <FormPanel :form-data="formData" :set-form-data="setFormData" />
      <DocumentViewer
        :uploaded-image="uploadedImage"
        :set-uploaded-image="handlers.setUploadedImage"
        :ocr-result="ocrResult"
        :is-loading="isLoading"
        :handle-export="handlers.handleExport"
        :handle-cancel="handlers.handleCancel"
        :handle-apply="handlers.handleApply"
        :call-ocr="handlers.callOcr"
      />
      <DataPanel :extracted-data="extractedData" />
    </div>
  </div>
</template>