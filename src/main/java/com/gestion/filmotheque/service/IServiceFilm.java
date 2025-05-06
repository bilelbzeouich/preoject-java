package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Categorie;

import java.util.List;

public interface IServiceFilm {
    public Film createFilm(Film film);

    public Film findFilmById(int id);

    public List<Film> findAllFilms();

    public Film updateFilm(Film film);

    public void deleteFilm(int id);

    // New methods
    public List<Film> findFilmsByTitle(String title);

    public List<Film> findAllFilmsOrderedByTitle();

    public List<Film> findFilmsByCategory(Categorie categorie);

    public boolean filmExist(int id);

    public List<Film> findFilmsByTitre(String titre);

    public List<Film> findFilmsByCategorie(int categorieId);

    // Home page methods
    public List<Film> findRandomFilms(int count);

    public List<Film> findLatestFilms(int count);
}
