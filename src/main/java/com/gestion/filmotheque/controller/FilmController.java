package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.service.FileStorageService;
import com.gestion.filmotheque.service.IServiceCategorie;
import com.gestion.filmotheque.service.IServiceFilm;
import com.gestion.filmotheque.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("detail/{id}")
    public String showFilmDetail(@PathVariable int id, Model model) {
        if (!iServiceFilm.filmExist(id)) {
            return "redirect:/";
        }
        model.addAttribute("film", iServiceFilm.findFilmById(id));
        model.addAttribute("isAdmin", userService.isAdmin());
        return "detail";
    }

    @GetMapping("new")
    @PreAuthorize("hasRole('ADMIN')")
    public String afficheNewForm(Model model) {
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        return "ajout";
    }

    @PostMapping("add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.store(imageFile);
            film.setImageUrl(fileName);
        }
        iServiceFilm.createFilm(film);
        return "redirect:/film/all";
    }

    @GetMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable int id, Model model) {
        try {
            iServiceFilm.deleteFilm(id);
            return "redirect:/film/all";
        } catch (DataIntegrityViolationException e) {
            // Film has relationships that prevent deletion
            model.addAttribute("filmId", id);
            return "error/delete-error";
        }
    }

    @GetMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String afficheUpdateForm(@PathVariable int id, Model model) {
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

    @GetMapping("user/search")
    public String userSearchFilmsByTitleGet(@RequestParam String titre, Model model) {
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

    @PostMapping("user/filterByCategory")
    public String userFilterFilmsByCategory(@RequestParam int idcat, Model model) {
        if (idcat == 0) {
            // If "Toutes les catégories" is selected, show all films
            model.addAttribute("films", iServiceFilm.findAllFilms());
        } else {
            // Get the category by ID
            Categorie categorie = iServiceCategorie.findCategorieById(idcat);
            model.addAttribute("films", iServiceFilm.findFilmsByCategory(categorie));
        }
        model.addAttribute("selectedCategoryId", idcat);
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "user/catalog";
    }

    @GetMapping("user/filterByCategory")
    public String userFilterFilmsByCategoryGet(@RequestParam int idcat, Model model) {
        // Reuse the same implementation as the POST method
        return userFilterFilmsByCategory(idcat, model);
    }

    @PostMapping("filterByCategory")
    public String filterFilmsByCategory(@RequestParam int idcat, Model model) {
        if (idcat == 0) {
            // If "Toutes les catégories" is selected, show all films
            model.addAttribute("films", iServiceFilm.findAllFilms());
        } else {
            // Get the category by ID
            Categorie categorie = iServiceCategorie.findCategorieById(idcat);
            model.addAttribute("films", iServiceFilm.findFilmsByCategory(categorie));
        }
        model.addAttribute("selectedCategoryId", idcat);
        model.addAttribute("categories", iServiceCategorie.findAllCategories());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "admin/films";
    }

    @GetMapping("filterByCategory")
    public String filterFilmsByCategoryGet(@RequestParam int idcat, Model model) {
        // Reuse the same implementation as the POST method
        return filterFilmsByCategory(idcat, model);
    }

    /**
     * Handles the scenario when a user directly types /film/all in the URL bar
     * Will redirect authorized users to admin view and unauthorized to user catalog
     */
    @GetMapping("all/redirect")
    public String handleDirectAccess() {
        if (userService.isAdmin()) {
            return "redirect:/film/all";
        } else {
            return "redirect:/film/user/catalog";
        }
    }
}
