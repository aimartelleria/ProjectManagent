package com.ecotrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing an application user.
 * A user can register, log in and log eco‑friendly actions.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique email address used for authentication.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * BCrypt hashed password. Never store raw passwords!
     */
    @Column(nullable = false)
    private String password;

    /**
     * Display name of the user.
     */
    private String name;

    /**
     * Optional URL or filename of the user avatar.
     */
    private String avatar;

    /**
     * Roles assigned to the user. Spring Security expects a role prefix like ROLE_USER.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
}
