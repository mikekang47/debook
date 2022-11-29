package com.sihoo.me.debook.applications;

import com.sihoo.me.debook.domains.Role;
import com.sihoo.me.debook.domains.RoleType;
import com.sihoo.me.debook.domains.User;
import com.sihoo.me.debook.infra.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(User user) {
        Role role = Role.builder()
                .userId(user.getId())
                .type(RoleType.USER)
                .build();

        return roleRepository.save(role);
    }
}

