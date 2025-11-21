<script setup>
import { ref } from "vue";
import { authAPI } from "../services/api";

const props = defineProps({
  onLogin: {
    type: Function,
    required: true,
  },
});

const username = ref("");
const password = ref("");
const isLoading = ref(false);
const errorMessage = ref("");
const idRegex = /^[a-zA-Z0-9]*$/;

const handleSubmit = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = "아이디와 비밀번호를 입력해주세요.";
    return;
  }

  isLoading.value = true;
  errorMessage.value = "";

  try {
    const response = await authAPI.login(username.value, password.value);
    const { accessToken, refreshToken } = response.data;

    // 토큰 저장
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    // JWT 디코딩하여 사용자 정보 추출 (간단한 방법)
    const payload = JSON.parse(atob(accessToken.split(".")[1]));

    props.onLogin({
      userId: payload.sub, // username
      role: payload.role,
      team: payload.department || "",
    });
  } catch (error) {
    console.error("Login error:", error);
    errorMessage.value =
      error.response?.data?.error || "로그인에 실패했습니다.";
  } finally {
    isLoading.value = false;
  }
};

const handleUsernameInput = (event) => {
  if (idRegex.test(event.target.value)) {
    username.value = event.target.value;
  }
};
</script>

<template>
  <div class="h-screen flex items-center justify-center bg-[#f5f5f5]">
    <div class="bg-white border border-[#e0e0e0] p-8 w-96 shadow-lg">
      <h1 class="text-2xl font-bold text-black mb-6 text-center">Labeleven</h1>

      <div
        v-if="errorMessage"
        class="mb-4 p-2 bg-red-50 border border-red-200 text-red-600 text-xs rounded"
      >
        {{ errorMessage }}
      </div>

      <div class="space-y-4">
        <div>
          <input
            type="text"
            :value="username"
            @input="handleUsernameInput"
            @keypress.enter="handleSubmit"
            :disabled="isLoading"
            class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0] disabled:bg-gray-100"
            placeholder="아이디"
          />
        </div>

        <div>
          <input
            type="password"
            v-model="password"
            @keypress.enter="handleSubmit"
            :disabled="isLoading"
            class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0] disabled:bg-gray-100"
            placeholder="비밀번호"
          />
        </div>

        <button
          @click="handleSubmit"
          :disabled="isLoading"
          class="w-full bg-[#1565c0] hover:bg-[#0d47a1] text-white py-2 text-sm mt-6 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isLoading ? "로그인 중..." : "로그인" }}
        </button>
      </div>
    </div>
  </div>
</template>
