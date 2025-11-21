<script setup>
const props = defineProps({
  formData: {
    type: Object,
    required: true,
  },
  setFormData: {
    type: Function,
    required: true,
  },
});

const fields = ['id', 'title', 'productName', 'createdAt', 'createdBy', 'version', 'team', 'updatedAt'];

const handleChange = (field, value) => {
  props.setFormData({ ...props.formData, [field]: value });
};
</script>

<template>
  <div class="w-[180px] bg-[#f5f5f5] border-r border-[#e0e0e0] flex-shrink-0">
    <div class="p-4 space-y-3">
      <div v-for="field in fields" :key="field" class="space-y-1">
        <label class="text-black text-right block pr-2 text-[10px]">
          {{ field.charAt(0).toUpperCase() + field.slice(1) }}
        </label>
        <input
          type="text"
          :value="props.formData[field]"
          @input="(event) => handleChange(field, event.target.value)"
          class="w-full px-2 py-0.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#999]"
        />
      </div>
    </div>
  </div>
</template>
