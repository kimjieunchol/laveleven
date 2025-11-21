package com.labelai.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private String role;
    private String team;
    private String password;
}
