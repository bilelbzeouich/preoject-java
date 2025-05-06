package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Role;
import com.gestion.filmotheque.entities.User;
import com.gestion.filmotheque.repository.RoleRepository;
import com.gestion.filmotheque.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerNewUser(String username, String email, String password, String fullName) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEnabled(true);

        // Assign USER role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerNewAdmin(String username, String email, String password, String fullName) {
        User user = registerNewUser(username, email, password, fullName);

        // Add ADMIN role
        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        user.getRoles().add(adminRole);

        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }

    @Override
    public boolean isAdmin() {
        UserDetails userDetails = getCurrentUser();
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(ROLE_ADMIN));
        }
        return false;
    }
}