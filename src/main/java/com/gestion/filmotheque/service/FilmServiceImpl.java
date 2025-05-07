package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.entities.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

    private final IServiceFilm serviceFilm;

    @Autowired
    public FilmServiceImpl(IServiceFilm serviceFilm) {
        this.serviceFilm = serviceFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return findAllFilms();
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        if (filmExist(id)) {
            return Optional.of(findFilmById(id));
        }
        return Optional.empty();
    }

    @Override
    public Film createFilm(Film film) {
        return serviceFilm.createFilm(film);
    }

    @Override
    public Film findFilmById(int id) {
        return serviceFilm.findFilmById(id);
    }

    @Override
    public List<Film> findAllFilms() {
        return serviceFilm.findAllFilms();
    }

    @Override
    public Film updateFilm(Film film) {
        return serviceFilm.updateFilm(film);
    }

    @Override
    public void deleteFilm(int id) {
        serviceFilm.deleteFilm(id);
    }

    @Override
    public List<Film> findFilmsByTitle(String title) {
        return serviceFilm.findFilmsByTitle(title);
    }

    @Override
    public List<Film> findAllFilmsOrderedByTitle() {
        return serviceFilm.findAllFilmsOrderedByTitle();
    }

    @Override
    public List<Film> findFilmsByCategory(Categorie categorie) {
        return serviceFilm.findFilmsByCategory(categorie);
    }

    @Override
    public boolean filmExist(int id) {
        return serviceFilm.filmExist(id);
    }

    @Override
    public List<Film> findFilmsByTitre(String titre) {
        return serviceFilm.findFilmsByTitre(titre);
    }

    @Override
    public List<Film> findFilmsByCategorie(int categorieId) {
        return serviceFilm.findFilmsByCategorie(categorieId);
    }

    @Override
    public List<Film> findRandomFilms(int count) {
        return serviceFilm.findRandomFilms(count);
    }

    @Override
    public List<Film> findLatestFilms(int count) {
        return serviceFilm.findLatestFilms(count);
    }
}