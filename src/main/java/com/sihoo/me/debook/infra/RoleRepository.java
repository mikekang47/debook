package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select u from User u where u.id = :userId and u.isDeleted = false")
    List<Role> findAllByUserId(@Param("userId") Long userId);
}
