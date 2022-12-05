package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

}
