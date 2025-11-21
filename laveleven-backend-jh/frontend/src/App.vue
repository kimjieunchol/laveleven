<script setup>
import { ref, onMounted } from "vue";
import LoginPage from "./pages/LoginPage.vue";
import MainPage from "./pages/MainPage.vue";
import WorkspacePage from "./pages/WorkspacePage.vue";
import Mypage from "./pages/Mypage.vue";
import SettingsPage from "./pages/SettingsPage.vue";
import StatsPage from "./pages/StatsPage.vue";

const currentPage = ref("login");
const currentUser = ref(null);
const users = ref([]);
const workspaceView = ref("new");
const workData = ref(null);

// JWT 디코딩 함수
const parseJwt = (token) => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("JWT 파싱 실패:", e);
    return null;
  }
};

// 토큰 유효성 검증
const isTokenValid = (token) => {
  if (!token) return false;

  const decoded = parseJwt(token);
  if (!decoded) return false;

  const now = Date.now() / 1000;
  return decoded.exp > now;
};

// 자동 로그인 (새로고침 시 토큰 확인)
const tryAutoLogin = () => {
  const token = localStorage.getItem("accessToken");

  console.log("🔍 자동 로그인 시도...");

  if (isTokenValid(token)) {
    const payload = parseJwt(token);
    currentUser.value = {
      userId: payload.sub,
      role: payload.role,
      team: payload.departmentId || "",
    };

    // 이전에 보던 페이지로 복원
    const lastPage = sessionStorage.getItem("lastPage");
    if (lastPage && lastPage !== "login") {
      currentPage.value = lastPage;
      console.log("✅ 자동 로그인 성공 → 페이지:", lastPage);
    } else {
      currentPage.value = "main";
      console.log("✅ 자동 로그인 성공 → 메인 페이지");
    }

    console.log("👤 현재 사용자:", currentUser.value);
    return true;
  }

  // 토큰이 없거나 만료됨
  console.log("❌ 유효한 토큰 없음 → 로그인 페이지");
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  sessionStorage.removeItem("lastPage");
  currentPage.value = "login";
  return false;
};

// 컴포넌트 마운트 시 자동 로그인 시도
onMounted(() => {
  tryAutoLogin();
});

const handleLogin = (user) => {
  currentUser.value = user;
  currentPage.value = "main";
  sessionStorage.setItem("lastPage", "main");
  console.log("✅ 로그인 완료:", user);
};

const handleLogout = () => {
  console.log("🚪 로그아웃 처리...");
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  sessionStorage.removeItem("lastPage");
  currentUser.value = null;
  currentPage.value = "login";
  console.log("✅ 로그아웃 완료");
};

const handleNavigate = (page, data) => {
  console.log("🔄 페이지 이동:", page, data ? "(데이터 있음)" : "");

  // 로그아웃 처리
  if (page === "login") {
    handleLogout();
    return;
  }

  // 일반 페이지 이동
  if (page === "main") {
    currentPage.value = "main";
    sessionStorage.setItem("lastPage", "main");
    return;
  }

  if (page === "mypage") {
    currentPage.value = "mypage";
    sessionStorage.setItem("lastPage", "mypage");
    return;
  }

  if (page === "settings") {
    currentPage.value = "settings";
    sessionStorage.setItem("lastPage", "settings");
    return;
  }

  if (page === "stats") {
    currentPage.value = "stats";
    sessionStorage.setItem("lastPage", "stats");
    return;
  }

  // Workspace 페이지
  if (page === "new") {
    workspaceView.value = "new";
    workData.value = data || null;
    currentPage.value = "workspace";
    sessionStorage.setItem("lastPage", "workspace");
    return;
  }

  if (page === "load") {
    workspaceView.value = "load";
    workData.value = null;
    currentPage.value = "workspace";
    sessionStorage.setItem("lastPage", "workspace");
    return;
  }

  // 기본값
  currentPage.value = "workspace";
  sessionStorage.setItem("lastPage", "workspace");
};
</script>

<template>
  <LoginPage
    v-if="currentPage === 'login'"
    :on-login="handleLogin"
    :users="users"
  />

  <MainPage
    v-else-if="currentPage === 'main'"
    :on-navigate="handleNavigate"
    :current-user="currentUser"
  />

  <WorkspacePage
    v-else-if="currentPage === 'workspace'"
    :on-navigate="handleNavigate"
    :current-user="currentUser"
    :initial-view="workspaceView"
    :work-data="workData"
  />

  <Mypage
    v-else-if="currentPage === 'mypage'"
    :on-navigate="handleNavigate"
    :current-user="currentUser"
  />

  <SettingsPage
    v-else-if="currentPage === 'settings'"
    :on-navigate="handleNavigate"
    :current-user="currentUser"
    :users="users"
    :set-users="(value) => (users.value = value)"
  />

  <StatsPage
    v-else-if="currentPage === 'stats'"
    :on-navigate="handleNavigate"
    :current-user="currentUser"
  />
</template>
