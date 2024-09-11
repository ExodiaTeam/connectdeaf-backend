package com.connectdeaf.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "TB_ROLES")
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Getter
    public enum Values {
        ROLE_USER(1L),
        ROLE_PROFESSIONAL(2L),
        ROLE_ADMIN(3L);

        final long id;

        Values(long id) {
            this.id = id;
        }
    }

    public String getName() {
        return name;
    }
}
