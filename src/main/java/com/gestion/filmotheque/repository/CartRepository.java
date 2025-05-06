package com.gestion.filmotheque.repository;

import com.gestion.filmotheque.entities.Cart;
import com.gestion.filmotheque.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}