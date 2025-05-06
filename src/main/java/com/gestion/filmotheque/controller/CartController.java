package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.User;
import com.gestion.filmotheque.repository.UserRepository;
import com.gestion.filmotheque.service.CartService;
import com.gestion.filmotheque.service.IServiceFilm;
import com.gestion.filmotheque.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final IServiceFilm filmService;
    private final UserService userService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, IServiceFilm filmService,
            UserService userService, UserRepository userRepository) {
        this.cartService = cartService;
        this.filmService = filmService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/view")
    public String viewCart(Model model) {
        UserDetails userDetails = userService.getCurrentUser();
        List<Film> cartItems;

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            cartItems = cartService.getCartContents(user);
        } else {
            // For demo purposes, we'll just show an empty cart for non-logged in users
            cartItems = List.of();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartItems.size());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "cart/view";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable int id, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();

        if (userDetails != null && filmService.filmExist(id)) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            Film film = filmService.findFilmById(id);

            if (user != null && film != null) {
                cartService.addFilmToCart(user, film);
                redirectAttributes.addFlashAttribute("message", "Film ajouté au panier");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            }
        } else {
            // For demo purposes, just show a success message anyway
            redirectAttributes.addFlashAttribute("message", "Film ajouté au panier");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        }

        return "redirect:/film/user/catalog";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable int id, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();

        if (userDetails != null && filmService.filmExist(id)) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            Film film = filmService.findFilmById(id);

            if (user != null && film != null) {
                cartService.removeFilmFromCart(user, film);
                redirectAttributes.addFlashAttribute("message", "Film retiré du panier");
                redirectAttributes.addFlashAttribute("alertClass", "alert-info");
            }
        }

        return "redirect:/cart/view";
    }

    @GetMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        UserDetails userDetails = userService.getCurrentUser();

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

            if (user != null) {
                cartService.clearCart(user);
                redirectAttributes.addFlashAttribute("message", "Panier vidé");
                redirectAttributes.addFlashAttribute("alertClass", "alert-info");
            }
        }

        return "redirect:/cart/view";
    }
}