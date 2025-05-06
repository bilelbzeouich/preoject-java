package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Cart;
import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.User;
import com.gestion.filmotheque.repository.CartRepository;
import com.gestion.filmotheque.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Cart getOrCreateCart(User user) {
        Optional<Cart> existingCart = cartRepository.findByUser(user);

        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setFilms(new ArrayList<>());
            return cartRepository.save(newCart);
        }
    }

    @Override
    @Transactional
    public void addFilmToCart(User user, Film film) {
        Cart cart = getOrCreateCart(user);

        // Check if film already exists in cart to avoid duplicates
        if (!cart.getFilms().contains(film)) {
            cart.getFilms().add(film);
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public void removeFilmFromCart(User user, Film film) {
        Cart cart = getOrCreateCart(user);
        cart.getFilms().remove(film);
        cartRepository.save(cart);
    }

    @Override
    public List<Film> getCartContents(User user) {
        Optional<Cart> cart = cartRepository.findByUser(user);
        return cart.map(Cart::getFilms).orElse(new ArrayList<>());
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.setFilms(new ArrayList<>());
        cartRepository.save(cart);
    }

    @Override
    public int getCartCount(User user) {
        Optional<Cart> cart = cartRepository.findByUser(user);
        return cart.map(c -> c.getFilms().size()).orElse(0);
    }
}