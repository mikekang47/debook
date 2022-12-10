package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.User;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> findUserById(Long id);

    List<User> findUserByNickName(String nickName);

    List<User> findUsers();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
