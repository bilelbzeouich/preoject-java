package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService extends IServiceFilm {
    List<Film> getAllFilms();

    Optional<Film> getFilmById(int id);
}