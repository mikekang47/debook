package com.sihoo.me.debook.domains;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Builder
@Getter
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleType type;

    public Role(Long userId, RoleType type) {
        this.userId = userId;
        this.type = type;
    }

    public Role(Long id, Long userId,  RoleType type) {
        this.id = id;
        this.userId = userId;
        this.type = type;
    }

    public Role( RoleType type) {
        this(null, type);
    }
}
