package com.labelai.controller;

import com.labelai.dto.TeamStats;
import com.labelai.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/teams")
    public ResponseEntity<List<TeamStats>> getTeamStats() {
        return ResponseEntity.ok(statsService.getTeamStats());
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getTotalStats() {
        return ResponseEntity.ok(statsService.getTotalStats());
    }
}
