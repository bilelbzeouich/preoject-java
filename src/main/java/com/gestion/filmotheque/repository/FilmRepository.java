package com.gestion.filmotheque.repository;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
    // Search films by title (partial or complete)
    List<Film> findByTitreContaining(String titre);

    // Search films by title (partial or complete) ignoring case
    List<Film> findByTitreContainingIgnoreCase(String titre);

    // Find all films ordered by title in ascending order
    List<Film> findAllByOrderByTitreAsc();

    // Filter films by category
    List<Film> findByCategorie(Categorie categorie);

    List<Film> findByCategorieId(int categorieId);
}
