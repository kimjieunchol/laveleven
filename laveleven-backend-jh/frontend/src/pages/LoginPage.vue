<script setup>
import { ref } from 'vue';

const props = defineProps({
  onLogin: {
    type: Function,
    required: true,
  },
  users: {
    type: Array,
    default: () => [],
  },
});

const username = ref('');
const password = ref('');
const idRegex = /^[a-zA-Z0-9]*$/;

const handleSubmit = () => {
  if (!username.value || !password.value) {
    alert('아이디와 비밀번호를 입력해주세요.');
    return;
  }

  if (username.value === 'admin' && password.value === 'admin') {
    props.onLogin({ userId: 'admin', team: '관리자' });
    return;
  }

  const matchedUser = props.users.find(
    (u) => u.userId === username.value && u.password === password.value,
  );

  if (!matchedUser) {
    alert('아이디 또는 비밀번호가 일치하지 않습니다.');
    return;
  }

  props.onLogin(matchedUser);
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

      <div class="space-y-4">
        <div>
          <input
            type="text"
            :value="username"
            @input="handleUsernameInput"
            @keypress.enter="handleSubmit"
            class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            placeholder="아이디"
          />
        </div>

        <div>
          <input
            type="password"
            v-model="password"
            @keypress.enter="handleSubmit"
            class="w-full px-3 py-2 border border-[#d0d0d0] text-sm focus:outline-none focus:border-[#1565c0]"
            placeholder="비밀번호"
          />
        </div>

        <button
          @click="handleSubmit"
          class="w-full bg-[#1565c0] hover:bg-[#0d47a1] text-white py-2 text-sm mt-6"
        >
          로그인
        </button>
      </div>
    </div>
  </div>
</template>
