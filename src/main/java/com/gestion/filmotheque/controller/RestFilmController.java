package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.service.FileStorageService;
import com.gestion.filmotheque.service.IServiceFilm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/films")
public class RestFilmController {

    @Autowired
    IServiceFilm serviceFilm;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "Get all films", description = "Returns a list of all films")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all films")
    public List<Film> getAllFilms() {
        return serviceFilm.findAllFilms();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get film by ID", description = "Returns a film by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the film"),
            @ApiResponse(responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<?> getFilmById(@PathVariable int id) {
        if (!serviceFilm.filmExist(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not Found");
        }
        return ResponseEntity.ok(serviceFilm.findFilmById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new film", description = "Creates a new film")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Film successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        serviceFilm.createFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image for a film", description = "Uploads an image for a specific film")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image successfully uploaded"),
            @ApiResponse(responseCode = "404", description = "Film not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> uploadImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        if (!serviceFilm.filmExist(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not Found");
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            String fileName = fileStorageService.store(file);
            Film film = serviceFilm.findFilmById(id);
            film.setImageUrl(fileName);
            serviceFilm.updateFilm(film);

            return ResponseEntity.ok().body("Image uploaded successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a film", description = "Deletes a film by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<String> deleteFilm(@PathVariable int id) {
        if (!serviceFilm.filmExist(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not Found");
        }
        serviceFilm.deleteFilm(id);
        return ResponseEntity.ok("Film deleted successfully");
    }

    @PutMapping("/update")
    @Operation(summary = "Update a film", description = "Updates an existing film")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film successfully updated"),
            @ApiResponse(responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<?> updateFilm(@RequestBody Film film) {
        if (!serviceFilm.filmExist(film.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not Found");
        }

        // Preserve the existing imageUrl if not provided in the request
        if (film.getImageUrl() == null || film.getImageUrl().isEmpty()) {
            Film existingFilm = serviceFilm.findFilmById(film.getId());
            film.setImageUrl(existingFilm.getImageUrl());
        }

        serviceFilm.updateFilm(film);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/search")
    @Operation(summary = "Search films by title", description = "Returns films matching the title")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching films")
    public List<Film> findFilmsByTitre(@RequestParam String titre) {
        return serviceFilm.findFilmsByTitre(titre);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter films by category", description = "Returns films by category ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered films")
    public List<Film> filterFilmsByCategorie(@RequestParam(required = false, defaultValue = "0") int categorieId) {
        if (categorieId == 0) {
            return serviceFilm.findAllFilms();
        }
        return serviceFilm.findFilmsByCategorie(categorieId);
    }
}
