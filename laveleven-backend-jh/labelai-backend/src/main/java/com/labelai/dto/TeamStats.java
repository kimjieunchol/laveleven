package com.labelai.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamStats {
    private String team;
    private Integer memberCount;
    private Integer totalTasks;
    private Integer completed;
    private Integer inProgress;
    private Integer pending;
    private Integer completionRate;
}
