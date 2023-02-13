package com.sihoo.me.debook.applications;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.errors.PasswordNotMatchException;
import com.sihoo.me.debook.errors.UserNotFoundException;
import com.sihoo.me.debook.infra.RoleRepository;
import com.sihoo.me.debook.infra.UserRepository;
import com.sihoo.me.debook.utils.JwtUtil;

import io.jsonwebtoken.Claims;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        boolean authentication = user.authenticate(password, passwordEncoder);

        if (!authentication) {
            throw new PasswordNotMatchException(email);
        }

        return jwtUtil.encode(user.getId());
    }

    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }

    public List<Role> getRoles(Long userId) {
        return roleRepository.findAllByUserId(userId);
    }
}
