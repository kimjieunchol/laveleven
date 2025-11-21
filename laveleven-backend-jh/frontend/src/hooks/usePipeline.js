import { ref } from "vue";
import { pipelineAPI } from "../services/api";

export default function usePipeline() {
  const uploadedFile = ref(null);
  const uploadedImage = ref(null);
  const ocrResult = ref(null);
  const structureResult = ref(null);
  const translateResult = ref(null);
  const htmlResult = ref(null);
  const isLoading = ref(false);
  const error = ref(null);
  const activeTab = ref("1. Scan");

  const formData = ref({
    id: "",
    productName: "",
    createdBy: "",
    createdAt: "",
    version: "",
    etc: "",
  });

  const setUploadedImage = (file, base64) => {
    uploadedFile.value = file;
    uploadedImage.value = base64;
  };

  const setFormData = (value) => {
    formData.value = value;
  };

  // 1. Scan - OCR 호출
  const callOcr = async () => {
    if (!uploadedFile.value) {
      error.value = "이미지를 먼저 업로드해주세요.";
      return null;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await pipelineAPI.processOcr(uploadedFile.value);
      ocrResult.value = response.data;
      console.log("[OCR_SUCCESS]", response.data);
      return response.data;
    } catch (err) {
      console.error("[OCR_ERROR]", err);
      error.value =
        err.response?.data?.error || "OCR 처리 중 오류가 발생했습니다.";
      return null;
    } finally {
      isLoading.value = false;
    }
  };

  // 2. Schema - Structure 호출
  const callStructure = async () => {
    if (!ocrResult.value) {
      error.value = "OCR 결과가 없습니다. Scan을 먼저 실행해주세요.";
      return null;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await pipelineAPI.processStructure({
        texts: ocrResult.value.texts,
        language: ocrResult.value.language || "korean",
        raw_data: {
          // ✅ 추가
          rec_texts: ocrResult.value.texts,
        },
      });
      structureResult.value = response.data;
      console.log("[STRUCTURE_SUCCESS]", response.data);
      return response.data;
    } catch (err) {
      console.error("[STRUCTURE_ERROR]", err);
      error.value =
        err.response?.data?.error || "Structure 처리 중 오류가 발생했습니다.";
      return null;
    } finally {
      isLoading.value = false;
    }
  };

  // 3. Translate 호출
  const callTranslate = async () => {
    if (!structureResult.value) {
      error.value = "Structure 결과가 없습니다. Schema를 먼저 실행해주세요.";
      return null;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await pipelineAPI.processTranslate({
        language: ocrResult.value?.language || "korean",
        data: structureResult.value, // ✅ structure 결과 전달
        target_country: "US",
      });
      translateResult.value = response.data;
      console.log("[TRANSLATE_SUCCESS]", response.data);
      return response.data;
    } catch (err) {
      console.error("[TRANSLATE_ERROR]", err);
      error.value =
        err.response?.data?.error || "Translate 처리 중 오류가 발생했습니다.";
      return null;
    } finally {
      isLoading.value = false;
    }
  };

  // 4. Sketch - HTML 생성
  const callHtml = async () => {
    if (!translateResult.value) {
      error.value = "Translate 결과가 없습니다. Translate를 먼저 실행해주세요.";
      return null;
    }

    isLoading.value = true;
    error.value = null;

    try {
      const response = await pipelineAPI.processHtml({
        label_data: structureResult.value, // ✅ 원본 구조화 데이터
        data: translateResult.value, // ✅ 번역된 데이터
        source_language: ocrResult.value?.language || "korean",
        target_country: "US",
        country: "US",
      });
      htmlResult.value = response.data.html || response.data;
      console.log("[HTML_SUCCESS]", htmlResult.value);
      return htmlResult.value;
    } catch (err) {
      console.error("[HTML_ERROR]", err);
      error.value =
        err.response?.data?.error || "HTML 생성 중 오류가 발생했습니다.";
      return null;
    } finally {
      isLoading.value = false;
    }
  };

  const setActiveTab = async (tab) => {
    activeTab.value = tab;

    switch (tab) {
      case "1. Scan":
        if (!ocrResult.value && uploadedFile.value) {
          await callOcr();
        }
        break;
      case "2. Schema":
        if (!structureResult.value && ocrResult.value) {
          await callStructure();
        }
        break;
      case "3. Translate":
        if (!translateResult.value && structureResult.value) {
          await callTranslate();
        }
        break;
      case "4. Sketch":
        if (!htmlResult.value && translateResult.value) {
          await callHtml();
        }
        break;
      case "5. Validate":
        break;
    }
  };

  const handleRerun = async () => {
    switch (activeTab.value) {
      case "1. Scan":
        await callOcr();
        break;
      case "2. Schema":
        await callStructure();
        break;
      case "3. Translate":
        await callTranslate();
        break;
      case "4. Sketch":
        await callHtml();
        break;
    }
  };

  const handleValidate = () => alert("검수 준비 중입니다.");
  const handleApply = () => alert("적용되었습니다.");

  const handleCancel = () => {
    if (confirm("작업을 취소하시겠습니까?")) {
      uploadedFile.value = null;
      uploadedImage.value = null;
      ocrResult.value = null;
      structureResult.value = null;
      translateResult.value = null;
      htmlResult.value = null;
      error.value = null;
    }
  };

  const handleExport = () => {
    const exportData = {
      formData: formData.value,
      ocrResult: ocrResult.value,
      structureResult: structureResult.value,
      translateResult: translateResult.value,
      htmlResult: htmlResult.value,
    };
    const blob = new Blob([JSON.stringify(exportData, null, 2)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `labeleven_export_${Date.now()}.json`;
    a.click();
  };

  return {
    uploadedFile,
    uploadedImage,
    ocrResult,
    structureResult,
    translateResult,
    htmlResult,
    isLoading,
    error,
    activeTab,
    formData,
    setFormData,
    setActiveTab,
    handlers: {
      callOcr,
      callStructure,
      callTranslate,
      callHtml,
      handleRerun,
      handleValidate,
      handleExport,
      handleCancel,
      handleApply,
      setUploadedImage,
    },
  };
}
