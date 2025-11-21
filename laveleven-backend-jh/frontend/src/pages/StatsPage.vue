<script setup>
import { computed, ref } from 'vue';
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
});

const teamStats = ref([]);

const totalStats = computed(() => ({
  total: teamStats.value.reduce((sum, team) => sum + (team.totalTasks || 0), 0),
  completed: teamStats.value.reduce((sum, team) => sum + (team.completed || 0), 0),
  inProgress: teamStats.value.reduce((sum, team) => sum + (team.inProgress || 0), 0),
  pending: teamStats.value.reduce((sum, team) => sum + (team.pending || 0), 0),
}));
</script>

<template>
  <div class="h-screen flex flex-col bg-white">
    <TopBar :on-navigate="props.onNavigate" :current-user="props.currentUser" />

    <div class="flex-1 flex overflow-hidden">
      <Sidebar :on-navigate="props.onNavigate" current-page="stats" />

      <div class="flex-1 overflow-auto bg-[#f5f5f5]">
        <div class="p-6">
          <div class="max-w-6xl mx-auto space-y-6">
            <div class="bg-white border border-[#e0e0e0]">
              <div class="border-b border-[#e0e0e0] px-4 py-3">
                <h2 class="text-sm font-bold text-black">전체 업무 요약</h2>
              </div>

              <div class="p-4">
                <table class="w-full text-xs">
                  <thead class="bg-[#f5f5f5] border-b border-[#e0e0e0]">
                    <tr>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">전체 업무</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">완료</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">진행중</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">대기</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr class="border-b border-[#f0f0f0]">
                      <td class="px-4 py-3 text-center text-black font-bold text-base">{{ totalStats.total }}</td>
                      <td class="px-4 py-3 text-center text-[#666] font-medium text-base">
                        {{ totalStats.completed }}
                      </td>
                      <td class="px-4 py-3 text-center text-[#1565c0] font-medium text-base">
                        {{ totalStats.inProgress }}
                      </td>
                      <td class="px-4 py-3 text-center text-[#999] font-medium text-base">{{ totalStats.pending }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="bg-white border border-[#e0e0e0]">
              <div class="border-b border-[#e0e0e0] px-4 py-3">
                <h2 class="text-sm font-bold text-black">팀별 업무 현황</h2>
              </div>

              <div class="p-4">
                <div v-if="teamStats.length === 0" class="text-center py-8 text-[#999] text-xs">
                  표시할 데이터가 없습니다
                </div>
                <table v-else class="w-full text-xs">
                  <thead class="bg-[#f5f5f5] border-b border-[#e0e0e0]">
                    <tr>
                      <th class="px-4 py-2 text-left font-normal text-[#666]">팀명</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">팀원 수</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">전체 업무</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">완료</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">진행중</th>
                      <th class="px-4 py-2 text-center font-normal text-[#666]">대기</th>
                      <th class="px-4 py-2 text-left font-normal text-[#666]">완료율</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(team, idx) in teamStats" :key="idx" class="border-b border-[#f0f0f0]">
                      <td class="px-4 py-3 text-black font-medium">{{ team.team }}</td>
                      <td class="px-4 py-3 text-center text-[#666]">{{ team.memberCount || 0 }}명</td>
                      <td class="px-4 py-3 text-center text-[#666]">{{ team.totalTasks }}</td>
                      <td class="px-4 py-3 text-center text-[#666]">{{ team.completed }}</td>
                      <td class="px-4 py-3 text-center text-[#1565c0]">{{ team.inProgress }}</td>
                      <td class="px-4 py-3 text-center text-[#999]">{{ team.pending }}</td>
                      <td class="px-4 py-3">
                        <div class="flex items-center gap-2">
                          <div class="flex-1 bg-[#f0f0f0] h-2 rounded-full overflow-hidden">
                            <div
                              class="bg-[#1565c0] h-full rounded-full"
                              :style="{ width: `${team.completionRate}%` }"
                            ></div>
                          </div>
                          <span class="text-[#666] w-10 text-right">{{ team.completionRate }}%</span>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
