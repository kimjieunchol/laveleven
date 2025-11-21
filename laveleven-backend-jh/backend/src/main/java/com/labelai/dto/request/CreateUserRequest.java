package com.labelai.dto.request;

import lombok.Data;

/**
 * 사용자 생성 요청 DTO (관리자용, ERD 기반)
 */
@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String departmentId;
}