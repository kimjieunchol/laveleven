<script setup>
import { computed, ref, watch } from 'vue';
import { Plus, X, Search, Trash2, ChevronLeft } from 'lucide-vue-next';
import TopBar from '../components/TopBar.vue';
import Sidebar from '../components/Sidebar.vue';

const props = defineProps({
  currentUser: {
    type: Object,
    default: null,
  },
  onNavigate: {
    type: Function,
    required: true,
  },
  users: {
    type: Array,
    default: () => [],
  },
  setUsers: {
    type: Function,
    default: null,
  },
});

const showUserModal = ref(false);
const selectedUser = ref(null);
const selectedUserIds = ref([]);
const teamSelect = ref('');
const teamInput = ref('');
const newUser = ref({
  userId: '',
  password: '',
  confirmPw: '',
  name: '',
  email: '',
  role: '',
});

const searchFilters = ref({
  userId: '',
  name: '',
  team: '',
  email: '',
});


const regex = {
  userId: /^[a-zA-Z0-9]*$/,
  name: /^[가-힣a-zA-Z]*$/,
  team: /^[가-힣a-zA-Z0-9 ]*$/,
  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  pw: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,20}$/,
};

const getUserWorkHistory = () => [];

const usersList = computed(() => (Array.isArray(props.users) ? props.users : []));

const handleDeleteUser = (userId) => {
  if (!props.setUsers) return;
  if (window.confirm('정말로 사용자를 삭제하시겠습니까?')) {
    const updated = usersList.value.filter((u) => u.id !== userId);
    props.setUsers(updated);
  }
};

const applySearchRegex = (field, value) => {
  if (value === '') return value;
  if (regex[field].test(value)) return value;
  return searchFilters.value[field];
};

const filteredUsers = computed(() =>
  usersList.value.filter((user) => {
    const matchUserId = user.userId.includes(searchFilters.value.userId);
    const matchName = user.name.includes(searchFilters.value.name);
    const matchTeam = (user.team || '').includes(searchFilters.value.team);
    const matchEmail = user.email.includes(searchFilters.value.email);
    return matchUserId && matchName && matchTeam && matchEmail;
  }),
);

const handleClearFilters = () => {
  searchFilters.value = { userId: '', name: '', team: '', email: '' };
};

const resetNewUser = () => {
  newUser.value = {
    userId: '',
    password: '',
    confirmPw: '',
    name: '',
    email: '',
    role: '',
  };
  teamSelect.value = '';
  teamInput.value = '';
};

const closeModal = () => {
  showUserModal.value = false;
  resetNewUser();
};

const handleAddUser = () => {
  if (!props.setUsers) return;
  if (!/^[a-zA-Z0-9]{4,20}$/.test(newUser.value.userId)) {
    alert('아이디 규칙을 확인해주세요.');
    return;
  }
  if (!regex.pw.test(newUser.value.password)) {
    alert('비밀번호 규칙을 확인해주세요.');
    return;
  }
  if (newUser.value.password !== newUser.value.confirmPw) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  if (!regex.email.test(newUser.value.email)) {
    alert('이메일 형식이 올바르지 않습니다.');
    return;
  }

  const dup = usersList.value.some((u) => u.userId === newUser.value.userId);
  if (dup) {
    alert('이미 존재하는 아이디입니다.');
    return;
  }

  const newId = usersList.value.length ? Math.max(...usersList.value.map((u) => u.id || 0)) + 1 : 1;
  const updatedUsers = [
    ...usersList.value,
    {
      id: newId,
      userId: newUser.value.userId,
      password: newUser.value.password,
      name: newUser.value.name,
      email: newUser.value.email,
      role: newUser.value.role,
      team: teamInput.value,
    },
  ];
  props.setUsers(updatedUsers);

  closeModal();
};

const handleCheckDuplicate = () => {
  if (!/^[a-zA-Z0-9]{4,20}$/.test(newUser.value.userId)) {
    alert('아이디 규칙을 확인해주세요.');
    return;
  }

  const dup = usersList.value.some((u) => u.userId === newUser.value.userId);
  alert(dup ? '이미 존재하는 아이디입니다' : '사용 가능한 아이디입니다');
};

const isAllSelected = computed(() => {
  if (!filteredUsers.value.length) return false;
  return filteredUsers.value.every((user) => selectedUserIds.value.includes(user.id));
});

const toggleSelectAll = (checked) => {
  if (checked) {
    selectedUserIds.value = filteredUsers.value.map((user) => user.id);
  } else {
    selectedUserIds.value = [];
  }
};

const toggleSelectUser = (userId) => {
  if (selectedUserIds.value.includes(userId)) {
    selectedUserIds.value = selectedUserIds.value.filter((id) => id !== userId);
  } else {
    selectedUserIds.value = [...selectedUserIds.value, userId];
  }
};

const handleDeleteSelected = () => {
  if (!props.setUsers || selectedUserIds.value.length === 0) return;
  if (!window.confirm(`선택한 ${selectedUserIds.value.length}명의 사용자를 삭제하시겠습니까?`)) return;
  const updated = usersList.value.filter((user) => !selectedUserIds.value.includes(user.id));
  selectedUserIds.value = [];
  props.setUsers(updated);
};

// ✔ watch를 여기로 이동!
watch(
  () => usersList.value,
  (list) => {
    const validIds = new Set(list.map((user) => user.id));
    selectedUserIds.value = selectedUserIds.value.filter((id) => validIds.has(id));
  },
  { deep: true }
);
</script>

<template>
  <div class="h-screen flex flex-col bg-white">
    <TopBar :on-navigate="props.onNavigate" :current-user="props.currentUser" />

    <div class="flex-1 flex overflow-hidden">
      <Sidebar :on-navigate="props.onNavigate" current-page="settings" />

      <div class="flex-1 overflow-auto bg-[#f5f5f5] p-6">
        <div v-if="selectedUser" class="max-w-6xl mx-auto bg-white border">
          <div class="border-b px-4 py-3 flex items-center gap-2">
            <button @click="selectedUser = null" class="text-[#666]">
              <ChevronLeft class="w-4 h-4" />
            </button>
            <h2 class="text-sm font-bold">
              {{ selectedUser.name }} ({{ selectedUser.userId }}) - 작업 이력
            </h2>
          </div>

          <div class="p-4 text-xs">
            <div class="grid grid-cols-2 gap-4 mb-4">
              <div><span class="text-[#666]">아이디:</span> <span class="ml-2">{{ selectedUser.userId }}</span></div>
              <div><span class="text-[#666]">이름:</span> <span class="ml-2">{{ selectedUser.name }}</span></div>
              <div><span class="text-[#666]">팀:</span> <span class="ml-2">{{ selectedUser.team }}</span></div>
              <div><span class="text-[#666]">이메일:</span> <span class="ml-2">{{ selectedUser.email }}</span></div>
            </div>

            <div class="border-t pt-4">
              <h3 class="font-bold mb-3">작업 이력</h3>

              <div v-if="getUserWorkHistory(selectedUser.userId).length === 0" class="text-center py-8 text-[#999]">
                작업 이력이 없습니다
              </div>
              <table v-else class="w-full">
                <thead class="bg-[#f5f5f5]">
                  <tr>
                    <th class="px-4 py-2 text-left">작업명</th>
                    <th class="px-4 py-2 text-left">상태</th>
                    <th class="px-4 py-2 text-left">작업일</th>
                    <th class="px-4 py-2 text-left">버전</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(history, idx) in getUserWorkHistory(selectedUser.userId)"
                    :key="idx"
                    class="border-b"
                  >
                    <td class="px-4 py-2">{{ history.name }}</td>
                    <td class="px-4 py-2">{{ history.status }}</td>
                    <td class="px-4 py-2">{{ history.date }}</td>
                    <td class="px-4 py-2">{{ history.version }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div v-else class="max-w-6xl mx-auto space-y-4">
          <div class="bg-white border">
            <div class="border-b px-4 py-3 flex items-center justify-between">
              <h2 class="text-sm font-bold">사용자 설정</h2>
              <button
                class="px-3 py-1.5 bg-[#1565c0] text-white text-xs flex items-center gap-1"
                @click="showUserModal = true"
              >
                <Plus class="w-3 h-3" />
                새 사용자
              </button>
            </div>

            <div class="p-4 space-y-4 text-xs">
              <div class="grid grid-cols-5 gap-3 items-end">
                <div>
                  <label class="text-[10px] text-[#666]">아이디</label>
                  <input
                    type="text"
                    :value="searchFilters.userId"
                    @input="
                      (event) =>
                        (searchFilters.userId = applySearchRegex('userId', event.target.value))
                    "
                    class="w-full px-2 py-1.5 border"
                    placeholder="아이디 검색"
                  />
                </div>

                <div>
                  <label class="text-[10px] text-[#666]">이름</label>
                  <input
                    type="text"
                    :value="searchFilters.name"
                    @input="
                      (event) =>
                        (searchFilters.name = applySearchRegex('name', event.target.value))
                    "
                    class="w-full px-2 py-1.5 border"
                    placeholder="이름 검색"
                  />
                </div>

                <div>
                  <label class="text-[10px] text-[#666]">팀</label>
                  <input
                    type="text"
                    :value="searchFilters.team"
                    @input="
                      (event) =>
                        (searchFilters.team = applySearchRegex('team', event.target.value))
                    "
                    class="w-full px-2 py-1.5 border"
                    placeholder="부서"
                  />
                </div>

                <div>
                  <label class="text-[10px] text-[#666]">이메일</label>
                  <input
                    type="text"
                    :value="searchFilters.email"
                    @input="(event) => (searchFilters.email = event.target.value)"
                    class="w-full px-2 py-1.5 border"
                    placeholder="이메일@gmail.com"
                  />
                </div>

                <div class="flex items-center gap-2">
                  <button
                    type="button"
                    class="p-1.5 bg-[#1565c0] text-white flex items-center justify-center"
                  >
                    <Search class="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    @click="handleClearFilters"
                    class="px-3 py-1.5 border flex items-center gap-1"
                  >
                    <X class="w-3 h-3" /> 초기화
                  </button>
                </div>
              </div>
            </div>

            <div class="p-4 text-xs">
              <div class="flex items-center justify-between mb-3">
                <div class="text-[#666] text-xs">
                  총 {{ filteredUsers.length }}명 / 선택 {{ selectedUserIds.length }}명
                </div>
                <button
                  type="button"
                  @click="handleDeleteSelected"
                  :disabled="selectedUserIds.length === 0"
                  class="px-3 py-1 border text-xs disabled:opacity-50 disabled:cursor-not-allowed bg-white hover:bg-[#f5f5f5]"
                >
                  선택 삭제
                </button>
              </div>

              <div v-if="filteredUsers.length === 0" class="text-center py-8 text-[#999]">
                {{ usersList.length === 0 ? '등록된 사용자가 없습니다~!!^^' : '검색 결과가 없습니다' }}
              </div>
              <table v-else class="w-full">
                <thead class="bg-[#f5f5f5]">
                  <tr>
                    <th class="px-4 py-2 text-center w-10">
                    <input
                      type="checkbox"
                      class="w-3 h-3"
                      :checked="isAllSelected"
                      @change.stop="toggleSelectAll($event.target.checked)"
                    />
                    </th>
                    <th class="px-4 py-2 text-center w-16">번호</th>
                    <th class="px-4 py-2 text-left">아이디</th>
                    <th class="px-4 py-2 text-left">이름</th>
                    <th class="px-4 py-2 text-left">팀</th>
                    <th class="px-4 py-2 text-left">이메일</th>
                    <th class="px-4 py-2 text-center">작업</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(user, index) in filteredUsers"
                    :key="user.id"
                    class="border-b hover:bg-[#fafafa] cursor-pointer"
                    @click="selectedUser = user"
                  >
                    <td class="px-4 py-3 text-center" @click.stop>
                      <input
                        type="checkbox"
                        class="w-3 h-3"
                        :checked="selectedUserIds.includes(user.id)"
                        @change.stop="toggleSelectUser(user.id)"
                      />
                    </td>
                    <td class="px-4 py-3 text-center">{{ index + 1 }}</td>
                    <td class="px-4 py-3 text-[#1565c0]">{{ user.userId }}</td>
                    <td class="px-4 py-3">{{ user.name }}</td>
                    <td class="px-4 py-3">{{ user.team }}</td>
                    <td class="px-4 py-3">{{ user.email }}</td>
                    <td class="px-4 py-3 text-center">
                      <button
                        @click.stop="handleDeleteUser(user.id)"
                        class="text-[#666] hover:text-red-600"
                      >
                        <Trash2 class="w-3 h-3" />
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="showUserModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white border w-full max-w-md">
        <div class="border-b px-4 py-3 flex justify-between items-center">
          <h3 class="text-sm font-bold">새 사용자 추가</h3>
          <button @click="closeModal">
            <X class="w-4 h-4 text-[#666]" />
          </button>
        </div>

        <div class="p-4 space-y-3 text-xs">
          <div>
            <label class="text-[10px] text-[#666]">아이디 *</label>
            <div class="flex gap-2 mt-1">
              <input
                type="text"
                :value="newUser.userId"
                @input="
                  (event) => {
                    if (/^[a-zA-Z0-9]*$/.test(event.target.value)) {
                      newUser.userId = event.target.value;
                    }
                  }
                "
                class="flex-1 px-3 py-1.5 border"
                placeholder="영문/숫자 4~20자"
              />
              <button
                type="button"
                class="px-3 py-1.5 bg-[#1565c0] text-white"
                @click="handleCheckDuplicate"
              >
                확인
              </button>
            </div>
          </div>

          <div>
            <label class="text-[10px] text-[#666]">비밀번호 *</label>
            <input
              type="password"
              v-model="newUser.password"
              class="w-full px-3 py-1.5 border mt-1"
              placeholder="영문+숫자+특수문자 8~20자"
            />
          </div>

          <div>
            <label class="text-[10px] text-[#666]">비밀번호 확인 *</label>
            <input
              type="password"
              v-model="newUser.confirmPw"
              class="w-full px-3 py-1.5 border mt-1"
            />
            <p v-if="newUser.confirmPw.length > 0 && newUser.confirmPw === newUser.password" class="text-green-600 text-[11px] mt-1">
              비밀번호가 일치합니다
            </p>
            <p v-else-if="newUser.confirmPw.length > 0" class="text-red-600 text-[11px] mt-1">
              비밀번호가 일치하지 않습니다
            </p>
          </div>

          <div>
            <label class="text-[10px] text-[#666]">이름 *</label>
            <input
              type="text"
              :value="newUser.name"
              @input="
                (event) => {
                  if (/^[가-힣a-zA-Z]*$/.test(event.target.value)) {
                    newUser.name = event.target.value;
                  }
                }
              "
              class="w-full px-3 py-1.5 border mt-1"
            />
          </div>

          <div>
            <label class="text-[10px] text-[#666]">팀</label>
            <select
              :value="teamSelect"
              @change="
                (event) => {
                  teamSelect = event.target.value;
                  if (event.target.value !== '팀직접입력') {
                    teamInput = event.target.value;
                  } else {
                    teamInput = '';
                  }
                }
              "
              class="w-full px-3 py-1.5 border mt-1"
            >
              <option value="">선택해주세요</option>
              <option value="기획팀">기획팀</option>
              <option value="개발팀">개발팀</option>
              <option value="검수팀">검수팀</option>
              <option value="팀직접입력">팀직접입력</option>
            </select>

            <input
              v-if="teamSelect === '팀직접입력'"
              type="text"
              :value="teamInput"
              @input="
                (event) => {
                  const value = event.target.value;
                  if (value === '' || /^[가-힣a-zA-Z0-9 ]+$/.test(value)) {
                    teamInput = value;
                  }
                }
              "
              placeholder="팀 이름 입력"
              class="w-full px-3 py-1.5 border mt-2"
            />
          </div>

          <div>
            <label class="text-[10px] text-[#666]">이메일 *</label>
            <input
              type="text"
              v-model="newUser.email"
              class="w-full px-3 py-1.5 border mt-1"
              placeholder="이메일@gmail.com"
            />
          </div>

          <div>
            <label class="text-[10px] text-[#666]">권한 *</label>
            <select
              v-model="newUser.role"
              class="w-full px-3 py-1.5 border mt-1"
            >
              <option value="">선택해주세요</option>
              <option value="team_leader">팀장</option>
              <option value="member">팀원</option>
            </select>
          </div>
        </div>

        <div class="border-t px-4 py-3 flex justify-end gap-2">
          <button class="px-4 py-1.5 bg-white border text-xs" @click="closeModal">취소</button>
          <button class="px-4 py-1.5 bg-[#1565c0] text-white text-xs" @click="handleAddUser">추가</button>
        </div>
      </div>
    </div>
  </div>
</template>