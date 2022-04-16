package ru.otus.hw12.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "fk_user_role"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            inverseForeignKey = @ForeignKey(name = "fk_role_user")
    )
    private List<Role> roles;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean enabled;
    @Column(nullable = false)
    private boolean locked;
    @Column(nullable = false)
    private boolean expired;
}
