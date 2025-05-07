package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    User registerNewUser(String username, String email, String password, String fullName, String phoneNumber);

    User registerNewAdmin(String username, String email, String password, String fullName, String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserDetails getCurrentUser();

    boolean isAdmin();
}