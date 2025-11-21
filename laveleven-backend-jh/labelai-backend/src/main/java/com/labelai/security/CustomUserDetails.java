package com.labelai.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final Long userId;
    private final String departmentId;
    private final String role;

    public CustomUserDetails(String username, String password, 
                            Collection<? extends GrantedAuthority> authorities,
                            Long userId, String departmentId, String role) {
        super(username, password, authorities);
        this.userId = userId;
        this.departmentId = departmentId;
        this.role = role;
    }
}
