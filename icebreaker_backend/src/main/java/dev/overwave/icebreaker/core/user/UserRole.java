package dev.overwave.icebreaker.core.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    CAPTAIN,
    ADMIN,
    ;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
