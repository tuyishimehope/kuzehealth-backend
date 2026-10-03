package rw.ac.auca.kuzahealth.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final String email;
    private final EUserType role;
    private final boolean enabled;
    private final Long tokensInvalidBefore;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername() != null ? user.getUsername() : user.getEmail();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.tokensInvalidBefore = user.getTokensInvalidBefore();
        this.authorities = role == null
                ? Collections.emptyList()
                : Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public boolean hasRole(EUserType expected) {
        return role == expected;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
