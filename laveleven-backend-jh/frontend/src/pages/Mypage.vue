<script setup>
import { computed, ref } from 'vue';
import { Search, Plus, Edit, Trash2, ChevronLeft, ChevronRight, Filter, X } from 'lucide-vue-next';
import TopBar from '../components/TopBar.vue';
import Sidebar from '../components/Sidebar.vue';
import NewWorkModal from '../components/NewWorkModal.vue';

const props = defineProps({
  currentUser: {
    type: Object,
    default: null,
  },
  onNavigate: {
    type: Function,
    required: true,
  },
});

const searchTerm = ref('');
const searchInput = ref('');
const selectedItems = ref([]);
const currentPage = ref(1);
const items = ref([]);
const statusFilter = ref('all');
const showAdvancedSearch = ref(false);
const advancedFilters = ref({
  createdBy: '',
  dateFrom: '',
  dateTo: '',
  version: '',
});
const activeSidebarTab = ref('my-tasks');
const itemsPerPage = ref(10);
const isNewWorkModalOpen = ref(false);

const tabStatusMap = {
  'my-tasks': 'all',
  completed: 'completed',
  'in-progress': 'in_progress',
  pending: 'pending',
};

const handleSidebarTabChange = (tab) => {
  activeSidebarTab.value = tab;
  statusFilter.value = tabStatusMap[tab] ?? 'all';
  currentPage.value = 1;
};

const handleViewChange = (view) => {
  props.onNavigate(view);
};

const handleNewWork = () => {
  alert('새 작업이 생성되었습니다!');
};

const handleSelectAll = (event) => {
  if (event.target.checked) {
    selectedItems.value = items.value.map((item) => item.id);
  } else {
    selectedItems.value = [];
  }
};

const handleSelectItem = (id) => {
  if (selectedItems.value.includes(id)) {
    selectedItems.value = selectedItems.value.filter((itemId) => itemId !== id);
  } else {
    selectedItems.value = [...selectedItems.value, id];
  }
};

const getStatusText = (status) => {
  const statusMap = {
    completed: '완료',
    in_progress: '진행중',
    pending: '대기',
  };
  return statusMap[status] || status;
};

const getStatusColor = (status) => {
  const colorMap = {
    completed: 'text-[#666]',
    in_progress: 'text-[#1565c0]',
    pending: 'text-[#999]',
  };
  return colorMap[status] || 'text-gray-600';
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const filteredItems = computed(() =>
  items.value.filter((item) => {
    const matchesSearch =
      item.name?.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      item.productName?.toLowerCase().includes(searchTerm.value.toLowerCase());

    const matchesStatus = statusFilter.value === 'all' || item.status === statusFilter.value;

    const matchesCreatedBy =
      !advancedFilters.value.createdBy ||
      item.createdBy?.toLowerCase().includes(advancedFilters.value.createdBy.toLowerCase());

    const matchesVersion =
      !advancedFilters.value.version || item.version?.includes(advancedFilters.value.version);

    let matchesDate = true;
    if (advancedFilters.value.dateFrom || advancedFilters.value.dateTo) {
      const itemDate = new Date(item.createdAt);
      if (advancedFilters.value.dateFrom) {
        matchesDate = matchesDate && itemDate >= new Date(advancedFilters.value.dateFrom);
      }
      if (advancedFilters.value.dateTo) {
        matchesDate = matchesDate && itemDate <= new Date(advancedFilters.value.dateTo);
      }
    }

    return matchesSearch && matchesStatus && matchesCreatedBy && matchesVersion && matchesDate;
  }),
);

const handleClearFilters = () => {
  searchTerm.value = '';
  searchInput.value = '';
  statusFilter.value = 'all';
  advancedFilters.value = {
    createdBy: '',
    dateFrom: '',
    dateTo: '',
    version: '',
  };
};

const handleSearch = () => {
  searchTerm.value = searchInput.value;
};

const totalPages = computed(() =>
  filteredItems.value.length === 0 ? 0 : Math.ceil(filteredItems.value.length / itemsPerPage.value),
);
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value);
const paginatedItems = computed(() =>
  filteredItems.value.slice(startIndex.value, startIndex.value + itemsPerPage.value),
);
</script>

<template>
  <div class="h-screen flex flex-col bg-white">
    <TopBar :on-navigate="props.onNavigate" :current-user="props.currentUser" />

    <div class="flex-1 flex overflow-hidden">
      <Sidebar
        :active-sidebar-tab="activeSidebarTab"
        :set-active-sidebar-tab="handleSidebarTabChange"
        :on-navigate="props.onNavigate"
        current-page="mypage"
      />

      <div class="flex-1 flex flex-col overflow-hidden">
        <div class="bg-white border-b border-[#e0e0e0] px-4 py-3">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-3 flex-1">
              <div class="relative flex-1 max-w-md flex gap-2">
                <div class="relative flex-1">
                  <Search class="absolute left-2 top-1/2 transform -translate-y-1/2 w-4 h-4 text-[#999]" />
                  <input
                    type="text"
                    placeholder="작업명, 제품명으로 검색..."
                    v-model="searchInput"
                    @keypress.enter="handleSearch"
                    class="w-full pl-8 pr-3 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
                  />
                </div>

                <button
                  @click="handleSearch"
                  class="p-1.5 bg-[#1565c0] hover:bg-[#0d47a1] text-white border border-[#d0d0d0] flex items-center justify-center"
                  title="검색"
                >
                  <Search class="w-4 h-4" />
                </button>
              </div>

              <select
                v-model="statusFilter"
                class="px-3 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
              >
                <option value="all">전체 상태</option>
                <option value="completed">완료</option>
                <option value="in_progress">진행중</option>
                <option value="pending">대기</option>
              </select>

              <button
                @click="showAdvancedSearch = !showAdvancedSearch"
                :class="[
                  'px-3 py-1.5 border border-[#d0d0d0] text-xs flex items-center gap-1',
                  showAdvancedSearch ? 'bg-[#1565c0] text-white' : 'bg-white text-black hover:bg-[#f5f5f5]',
                ]"
              >
                <Filter class="w-3 h-3" />
                고급 검색
              </button>

              <button
                @click="handleClearFilters"
                class="px-3 py-1.5 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] text-xs flex items-center gap-1"
              >
                <X class="w-3 h-3" />
                초기화
              </button>
            </div>

            <div class="flex items-center gap-2">
              <button
                @click="isNewWorkModalOpen = true"
                class="px-3 py-1.5 bg-[#1565c0] hover:bg-[#0d47a1] text-white text-xs border border-[#d0d0d0] flex items-center gap-1"
              >
                <Plus class="w-3 h-3" />
                새 작업
              </button>
              <button
                :disabled="selectedItems.length === 0"
                class="px-3 py-1.5 bg-white hover:bg-[#f5f5f5] text-black text-xs border border-[#d0d0d0] flex items-center gap-1 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Trash2 class="w-3 h-3" />
                삭제 ({{ selectedItems.length }})
              </button>
            </div>
          </div>

          <div v-if="showAdvancedSearch" class="pt-3 border-t border-[#e0e0e0]">
            <div class="grid grid-cols-4 gap-3">
              <div>
                <label class="text-[10px] text-[#666] mb-1 block">작성자</label>
                <input
                  type="text"
                  placeholder="작성자를"
                  v-model="advancedFilters.createdBy"
                  class="w-full px-2 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
                />
              </div>
              <div>
                <label class="text-[10px] text-[#666] mb-1 block">작성일 (시작)</label>
                <input
                  type="date"
                  v-model="advancedFilters.dateFrom"
                  class="w-full px-2 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
                />
              </div>
              <div>
                <label class="text-[10px] text-[#666] mb-1 block">작성일 (종료)</label>
                <input
                  type="date"
                  v-model="advancedFilters.dateTo"
                  class="w-full px-2 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
                />
              </div>
              <div>
                <label class="text-[10px] text-[#666] mb-1 block">버전</label>
                <input
                  type="text"
                  placeholder="예: 1.0.0"
                  v-model="advancedFilters.version"
                  class="w-full px-2 py-1.5 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0]"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="flex-1 overflow-auto">
          <table class="w-full text-xs">
            <thead class="bg-[#f5f5f5] border-b border-[#e0e0e0] sticky top-0">
              <tr>
                <th class="px-4 py-3 text-left font-normal text-[#666] w-10">
                  <input
                    type="checkbox"
                    class="w-3 h-3"
                    :checked="selectedItems.length === items.length && items.length > 0"
                    @change="handleSelectAll"
                  />
                </th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">작업명</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">제품명</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">상태</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">작성일시</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">작성자</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">수정일시</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">버전</th>
                <th class="px-4 py-3 text-left font-normal text-[#666]">작업</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="paginatedItems.length === 0">
                <td colspan="9" class="px-4 py-8 text-center text-[#999]">
                  등록된 작업이 없습니다!!
                </td>
              </tr>
              <tr
                v-else
                v-for="item in paginatedItems"
                :key="item.id"
                class="border-b border-[#f0f0f0] hover:bg-[#fafafa] cursor-pointer"
              >
                <td class="px-4 py-3">
                  <input
                    type="checkbox"
                    class="w-3 h-3"
                    :checked="selectedItems.includes(item.id)"
                    @change="() => handleSelectItem(item.id)"
                  />
                </td>
                <td class="px-4 py-3 text-[#1565c0] font-medium">{{ item.name }}</td>
                <td class="px-4 py-3 text-black">{{ item.productName }}</td>
                <td class="px-4 py-3">
                  <span :class="[getStatusColor(item.status), 'font-medium']">
                    {{ getStatusText(item.status) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-[#666]">{{ formatDate(item.createdAt) }}</td>
                <td class="px-4 py-3 text-[#666]">{{ item.createdBy }}</td>
                <td class="px-4 py-3 text-[#666]">{{ formatDate(item.modifiedAt) }}</td>
                <td class="px-4 py-3 text-[#666]">{{ item.version }}</td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <button class="text-[#1565c0] hover:text-[#0d47a1]">
                      <Edit class="w-3 h-3" />
                    </button>
                    <button class="text-[#666] hover:text-black">
                      <Trash2 class="w-3 h-3" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="bg-white border-t border-[#e0e0e0] px-4 py-2 flex items-center justify-between">
          <div class="text-xs text-[#666]">
            {{
              filteredItems.length > 0
                ? `${startIndex + 1}-${Math.min(startIndex + itemsPerPage, filteredItems.length)} of ${filteredItems.length}`
                : '0'
            }}
            items
          </div>
          <div class="flex items-center gap-2">
            <button
              @click="currentPage = Math.max(1, currentPage - 1)"
              :disabled="currentPage === 1 || filteredItems.length === 0"
              class="p-1 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft class="w-4 h-4" />
            </button>
            <span class="text-xs text-[#666]">
              {{ filteredItems.length > 0 ? `${currentPage} / ${totalPages}` : '0 / 0' }}
            </span>
            <button
              @click="currentPage = Math.min(totalPages, currentPage + 1)"
              :disabled="currentPage === totalPages || filteredItems.length === 0"
              class="p-1 border border-[#d0d0d0] bg-white text-black hover:bg-[#f5f5f5] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronRight class="w-4 h-4" />
            </button>
            <select
              :value="itemsPerPage"
              @change="(event) => { itemsPerPage.value = Number(event.target.value); currentPage = 1; }"
              class="ml-2 px-2 py-1 border border-[#d0d0d0] bg-white text-black text-xs focus:outline-none focus:border-[#1565c0] cursor-pointer"
            >
              <option value="10">10/page</option>
              <option value="25">25/page</option>
              <option value="50">50/page</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <NewWorkModal
      :is-open="isNewWorkModalOpen"
      :on-close="() => (isNewWorkModalOpen = false)"
      :on-submit="handleNewWork"
      :current-user="props.currentUser"
    />
  </div>
</template>
