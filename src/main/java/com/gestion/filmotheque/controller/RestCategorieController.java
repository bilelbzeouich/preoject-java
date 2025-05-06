package com.gestion.filmotheque.controller;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.service.IServiceCategorie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class RestCategorieController {

    @Autowired
    IServiceCategorie serviceCategorie;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns a list of all categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all categories")
    public List<Categorie> getAllCategories() {
        return serviceCategorie.findAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<?> getCategorieById(@PathVariable int id) {
        if (!serviceCategorie.categorieExist(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not Found");
        }
        return ResponseEntity.ok(serviceCategorie.findCategorieById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new category", description = "Creates a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Categorie> addCategorie(@RequestBody Categorie categorie) {
        serviceCategorie.createCategorie(categorie);
        return ResponseEntity.status(HttpStatus.CREATED).body(categorie);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<String> deleteCategorie(@PathVariable int id) {
        if (!serviceCategorie.categorieExist(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not Found");
        }
        serviceCategorie.deleteCategorie(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @PutMapping("/update")
    @Operation(summary = "Update a category", description = "Updates an existing category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully updated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<?> updateCategorie(@RequestBody Categorie categorie) {
        if (!serviceCategorie.categorieExist(categorie.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not Found");
        }
        serviceCategorie.updateCategorie(categorie);
        return ResponseEntity.ok(categorie);
    }
}