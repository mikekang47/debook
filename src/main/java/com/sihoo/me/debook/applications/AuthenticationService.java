package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.errors.CustomException;
import com.sihoo.me.debook.infra.RoleRepository;
import com.sihoo.me.debook.infra.UserRepository;
import com.sihoo.me.debook.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found(Email: " + email + ")",
                        HttpStatus.NOT_FOUND));

        boolean authentication = user.authenticate(password);

        if (!authentication) {
            throw new CustomException("Login failed. Password doesn't match (Email: " + email + ")", HttpStatus.BAD_REQUEST);
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
