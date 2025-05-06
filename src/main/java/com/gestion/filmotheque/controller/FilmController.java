package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.service.FileStorageService;
import com.gestion.filmotheque.service.IServiceCategorie;
import com.gestion.filmotheque.service.IServiceFilm;
import com.gestion.filmotheque.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/film/")
@AllArgsConstructor
public class FilmController {

    IServiceFilm iServiceFilm;
    IServiceCategorie iServiceCategorie;
    FileStorageService fileStorageService;
    UserService userService;

    @GetMapping("all")
    public String allFilms(Model model) {
        // Check if user is admin
        if (userService.isAdmin()) {
            // Admin view
            model.addAttribute("films", iServiceFilm.findAllFilms());
            model.addAttribute("categories", iServiceCategorie.findAllCategories());
            model.addAttribute("isAdmin", true);
            return "admin/films";
        } else {
            // Redirect regular users to the user catalog
            return "redirect:/film/user/catalog";
        }
    }

    @GetMapping("user/catalog")
    public String userCatalog(Model model) {
        model.addAttribute("films", iServiceFilm.findAllFilms());
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "user/catalog";
    }

    @GetMapping("user/filterByCategory")
    public String filterByCategory(@RequestParam("idcat") int categoryId, Model model) {
        List<Film> films;

        if (categoryId == 0) {
            // Show all films if category ID is 0
            films = iServiceFilm.findAllFilms();
        } else {
            // Get the category and filter films
            Categorie category = iServiceCategorie.findCategorieById(categoryId);
            films = iServiceFilm.findFilmsByCategory(category);
        }

        model.addAttribute("films", films);
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("isAdmin", userService.isAdmin());

        return "user/catalog";
    }

    @GetMapping("detail/{id}")
    public String showFilmDetails(@PathVariable int id, Model model) {
        if (iServiceFilm.filmExist(id)) {
            model.addAttribute("film", iServiceFilm.findFilmById(id));
            model.addAttribute("isAdmin", userService.isAdmin());
            return "detail";
        }
        return "redirect:/film/user/catalog";
    }

    @GetMapping("new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        return "ajout";
    }

    @PostMapping("add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addFilm(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.store(imageFile);
            film.setImageUrl(fileName);
        }
        iServiceFilm.createFilm(film);
        return "redirect:/film/all";
    }

    @GetMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUpdateForm(@PathVariable int id, Model model) {
        model.addAttribute("film", iServiceFilm.findFilmById(id));
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        return "update";
    }

    @PostMapping("update")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@ModelAttribute Film film,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Film existingFilm = iServiceFilm.findFilmById(film.getId());

        // Keep the existing image if no new one is uploaded
        if (imageFile == null || imageFile.isEmpty()) {
            film.setImageUrl(existingFilm.getImageUrl());
        } else {
            String fileName = fileStorageService.store(imageFile);
            film.setImageUrl(fileName);
        }

        iServiceFilm.updateFilm(film);
        return "redirect:/film/all";
    }

    @PostMapping("user/search")
    public String userSearchFilmsByTitle(@RequestParam String titre, Model model) {
        model.addAttribute("films", iServiceFilm.findFilmsByTitle(titre));
        model.addAttribute("searchTerm", titre);
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "user/catalog";
    }

    @PostMapping("search")
    public String searchFilmsByTitle(@RequestParam String titre, Model model) {
        model.addAttribute("films", iServiceFilm.findFilmsByTitle(titre));
        model.addAttribute("searchTerm", titre);
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "admin/films";
    }

    @GetMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFilm(@PathVariable int id) {
        if (iServiceFilm.filmExist(id)) {
            iServiceFilm.deleteFilm(id);
        }
        return "redirect:/film/all";
    }
}
