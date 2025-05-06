package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Cart;
import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.User;

import java.util.List;

public interface CartService {
    Cart getOrCreateCart(User user);

    void addFilmToCart(User user, Film film);

    void removeFilmFromCart(User user, Film film);

    List<Film> getCartContents(User user);

    void clearCart(User user);

    int getCartCount(User user);
}