package com.labelai.service;

import com.labelai.dto.TeamStats;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<TeamStats> getTeamStats() {
        var users = userRepository.findAll();
        var items = itemRepository.findAll();
        
        Map<String, Long> teamMembers = users.stream()
            .filter(u -> u.getDepartmentId() != null)
            .collect(Collectors.groupingBy(u -> u.getDepartmentId(), Collectors.counting()));
        
        Map<String, List<com.labelai.entity.Item>> teamItems = items.stream()
            .filter(i -> i.getDepartmentId() != null)
            .collect(Collectors.groupingBy(i -> i.getDepartmentId()));
        
        Set<String> allTeams = new HashSet<>();
        allTeams.addAll(teamMembers.keySet());
        allTeams.addAll(teamItems.keySet());
        
        return allTeams.stream().map(team -> {
            var teamItemList = teamItems.getOrDefault(team, Collections.emptyList());
            int total = teamItemList.size();
            int completed = 0, inProgress = 0, pending = total;
            int rate = total > 0 ? (completed * 100 / total) : 0;
            return TeamStats.builder()
                .team(team)
                .memberCount(teamMembers.getOrDefault(team, 0L).intValue())
                .totalTasks(total)
                .completed(completed)
                .inProgress(inProgress)
                .pending(pending)
                .completionRate(rate)
                .build();
        }).collect(Collectors.toList());
    }

    public Map<String, Integer> getTotalStats() {
        var stats = getTeamStats();
        return Map.of(
            "total", stats.stream().mapToInt(TeamStats::getTotalTasks).sum(),
            "completed", stats.stream().mapToInt(TeamStats::getCompleted).sum(),
            "inProgress", stats.stream().mapToInt(TeamStats::getInProgress).sum(),
            "pending", stats.stream().mapToInt(TeamStats::getPending).sum()
        );
    }
}
