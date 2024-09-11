    package com.connectdeaf.domain.user;

    import com.connectdeaf.domain.address.Address;
    import com.connectdeaf.domain.appointment.Appointment;
    import com.fasterxml.jackson.annotation.JsonManagedReference;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.Collections;
    import java.util.List;
    import java.util.Set;
    import java.util.UUID;

    @Entity
    @Table(name = "TB_USER")
    @Getter
    @Setter
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        private String name;
        @Email
        private String email;
        private String password;
        private String phoneNumber;

        @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private List<Address> addresses;

        @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JsonManagedReference
        private List<Appointment> appointments;

        @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinTable(
                name = "TB_USER_ROLES",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )

        private Set<Role> roles;

        public User(String name, String email, String password, String phoneNumber) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.addresses = Collections.emptyList();
            this.appointments = Collections.emptyList();
            this.roles = Collections.emptySet();
        }

        public User() {
        }

        public Set<Role> getRoles() {
            return roles;
        }
    }