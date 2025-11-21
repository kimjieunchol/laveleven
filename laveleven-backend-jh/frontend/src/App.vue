<script setup>
import { ref } from 'vue';
import LoginPage from './pages/LoginPage.vue';
import MainPage from './pages/MainPage.vue';
import WorkspacePage from './pages/WorkspacePage.vue';
import Mypage from './pages/Mypage.vue';
import SettingsPage from './pages/SettingsPage.vue';
import StatsPage from './pages/StatsPage.vue';

const currentPage = ref('login');
const currentUser = ref(null);
const users = ref([]);
const workspaceView = ref('new');
const workData = ref(null);

const handleLogin = (user) => {
  currentUser.value = user;
  currentPage.value = 'main';
};

const handleNavigate = (page, data) => {
  if (page === 'login') {
    currentUser.value = null;
    currentPage.value = 'login';
    return;
  }
  if (page === 'main') {
    currentPage.value = 'main';
    return;
  }
  if (page === 'mypage') {
    currentPage.value = 'mypage';
    return;
  }
  if (page === 'settings') {
    currentPage.value = 'settings';
    return;
  }
  if (page === 'stats') {
    currentPage.value = 'stats';
    return;
  }
  if (page === 'new') {
    workspaceView.value = 'new';
    workData.value = data || null;
    currentPage.value = 'workspace';
    return;
  }
  if (page === 'load') {
    workspaceView.value = 'load';
    workData.value = null;
    currentPage.value = 'workspace';
    return;
  }

  currentPage.value = 'workspace';
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
