package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.service.FilmService;
import com.gestion.filmotheque.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final FilmService filmService;

    public AuthController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Featured films (random selection)
        List<Film> featuredFilms = filmService.findRandomFilms(4);
        model.addAttribute("featuredFilms", featuredFilms);

        // Latest additions
        List<Film> latestFilms = filmService.findLatestFilms(8);
        model.addAttribute("latestFilms", latestFilms);

        model.addAttribute("isAdmin", userService.isAdmin());
        return "home";
    }

    @GetMapping("/login")
    public String loginForm() {
        if (userService.getCurrentUser() != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        if (userService.getCurrentUser() != null) {
            return "redirect:/";
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(required = false) String phoneNumber,
            RedirectAttributes redirectAttributes) {
        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Le nom d'utilisateur existe déjà");
            return "redirect:/register";
        }

        if (userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "L'adresse email existe déjà");
            return "redirect:/register";
        }

        userService.registerNewUser(username, email, password, fullName, phoneNumber);
        redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Veuillez vous connecter.");
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("message", "Vous n'avez pas les permissions nécessaires pour accéder à cette page.");
        model.addAttribute("redirectUrl", "/film/user/catalog");
        model.addAttribute("redirectText", "Voir le catalogue de films");
        return "auth/access-denied";
    }
}