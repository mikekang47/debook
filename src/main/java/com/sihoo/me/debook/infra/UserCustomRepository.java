package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.User;

import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> findUserById(Long id);
}
