<script setup>
import { ref, watch } from "vue";
import TopBar from "../components/TopBar.vue";
import TabsBar from "../components/TabsBar.vue";
import FormPanel from "../components/FormPanel.vue";
import DocumentViewer from "../components/DocumentViewer.vue";
import DataPanel from "../components/DataPanel.vue";
import LoadView from "../components/LoadView.vue";
import usePipeline from "../hooks/usePipeline";

const props = defineProps({
  onNavigate: { type: Function, required: true },
  currentUser: { type: Object, default: null },
  initialView: { type: String, default: "new" },
  workData: { type: Object, default: null },
});

const currentView = ref(props.initialView);

// ✅ usePipeline에서 activeTab과 setActiveTab 가져오기
const {
  uploadedImage,
  ocrResult,
  isLoading,
  activeTab, // ✅ 추가
  setActiveTab, // ✅ 추가
  handlers,
  formData,
  setFormData,
} = usePipeline();

// ... watch 코드들 동일 ...

watch(
  () => props.workData,
  (value) => {
    if (value && currentView.value === "new") {
      setFormData({
        id: value.id || "",
        title: value.title || "",
        productName: value.productName || "",
        createdAt: value.createdAt || "",
        createdBy: value.createdBy || props.currentUser?.userId || "",
        version: value.version || "",
        team: value.team || props.currentUser?.team || "",
        updatedAt: value.updatedAt || "",
      });
    }
  },
  { immediate: true }
);

watch(
  () => props.initialView,
  (value) => {
    currentView.value = value;
  },
  { immediate: true }
);

watch(
  () => currentView.value,
  (newView, oldView) => {
    if (newView !== oldView) {
      handlers.setUploadedImage(null, null);
      setFormData({
        id: "",
        title: "",
        productName: "",
        createdAt: "",
        createdBy: props.currentUser?.userId || "",
        version: "",
        team: props.currentUser?.team || "",
        updatedAt: "",
      });

      if (newView === "new" && props.workData) {
        setFormData({
          id: props.workData.id || "",
          title: props.workData.title || "",
          productName: props.workData.productName || "",
          createdAt: props.workData.createdAt || "",
          createdBy:
            props.workData.createdBy || props.currentUser?.userId || "",
          version: props.workData.version || "",
          team: props.workData.team || props.currentUser?.team || "",
          updatedAt: props.workData.updatedAt || "",
        });
      }
    }
  }
);

const extractedData = [
  { label: "id", value: "" },
  { label: "selection", value: "" },
  { label: "missing", value: "" },
  { label: "answer", value: "" },
  { label: "reference", value: "" },
  { label: "source", value: "" },
];

const setWorkspaceView = (view) => {
  currentView.value = view;
};

// ❌ 삭제: const setActiveTabValue = (tab) => { activeTab.value = tab; };
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
      :set-active-tab="setActiveTab"
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
