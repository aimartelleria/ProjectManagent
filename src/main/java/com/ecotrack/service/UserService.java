package com.ecotrack.service;

import com.ecotrack.model.User;
import com.ecotrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class that encapsulates business logic related to application users.
 * Also implements Spring Security's UserDetailsService for authentication.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user. Passwords are encoded and a default role is assigned.
     *
     * @param user incoming user with raw password
     * @return saved user
     */
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.getRoles().add("ROLE_USER");
        }
        return userRepository.save(user);
    }

    /**
     * Update an existing user's profile information (name, avatar). Email and password are not changed here.
     *
     * @param user current user persisted in database
     * @param name new display name
     * @param avatar new avatar path or URL
     * @return updated user
     */
    public User updateProfile(User user, String name, String avatar) {
        user.setName(name);
        user.setAvatar(avatar);
        return userRepository.save(user);
    }

    /**
     * Spring Security callback used to fetch user details during authentication.
     *
     * @param email the username (we use email as username)
     * @return UserDetails for Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Find a user by email if present.
     *
     * @param email email to search for
     * @return Optional of user
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
