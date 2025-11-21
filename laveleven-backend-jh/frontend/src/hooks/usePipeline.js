import { ref } from 'vue';

export default function usePipeline() {
  const uploadedImage = ref(null);
  const ocrResult = ref(null);
  const isLoading = ref(false);
  const formData = ref({
    id: '',
    productName: '',
    createdBy: '',
    createdAt: '',
    version: '',
    etc: '',
  });

  const setUploadedImage = (value) => {
    uploadedImage.value = value;
  };

  const setFormData = (value) => {
    formData.value = value;
  };

  const callOcr = async () => {
    isLoading.value = true;
    setTimeout(() => {
      isLoading.value = false;
    }, 800);
  };

  const handleRerun = () => alert('재실행 테스트');
  const handleValidate = () => alert('검수 준비 중입니다.');
  const handleApply = () => alert('적용되었습니다.');
  const handleCancel = () => {
    if (confirm('작업을 취소하시겠습니까?')) {
      setUploadedImage(null);
      ocrResult.value = null;
    }
  };

  const handleExport = () => {
    const exportData = { formData: formData.value, ocrResult: ocrResult.value };
    const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `labeleven_export_${Date.now()}.json`;
    a.click();
  };

  return {
    uploadedImage,
    ocrResult,
    isLoading,
    formData,
    setFormData,
    handlers: {
      callOcr,
      handleRerun,
      handleValidate,
      handleExport,
      handleCancel,
      handleApply,
      setUploadedImage,
    },
  };
}
