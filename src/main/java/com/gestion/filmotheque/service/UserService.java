package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    User registerNewUser(String username, String email, String password, String fullName);

    User registerNewAdmin(String username, String email, String password, String fullName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDetails getCurrentUser();

    boolean isAdmin();
}