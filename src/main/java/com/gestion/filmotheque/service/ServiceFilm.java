package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Film;
import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.repository.FilmRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ServiceFilm implements IServiceFilm {

    FilmRepository filmRepository;

    @Override
    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }

    @Override
    public Film findFilmById(int id) {
        return filmRepository.findById(id).get();
    }

    @Override
    public List<Film> findAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film updateFilm(Film film) {
        return filmRepository.save(film);
    }

    @Override
    public void deleteFilm(int id) {
        filmRepository.deleteById(id);
    }

    @Override
    public boolean filmExist(int id) {
        return filmRepository.existsById(id);
    }

    @Override
    public List<Film> findFilmsByTitre(String titre) {
        return filmRepository.findByTitreContaining(titre);
    }

    @Override
    public List<Film> findFilmsByCategorie(int categorieId) {
        return filmRepository.findByCategorieId(categorieId);
    }

    @Override
    public List<Film> findFilmsByTitle(String title) {
        return filmRepository.findByTitreContainingIgnoreCase(title);
    }

    @Override
    public List<Film> findAllFilmsOrderedByTitle() {
        return filmRepository.findAllByOrderByTitreAsc();
    }

    @Override
    public List<Film> findFilmsByCategory(Categorie categorie) {
        return filmRepository.findByCategorie(categorie);
    }

    @Override
    public List<Film> findRandomFilms(int count) {
        List<Film> allFilms = filmRepository.findAll();

        // If we don't have enough films, return all
        if (allFilms.size() <= count) {
            return allFilms;
        }

        // Shuffle films and return the specified count
        List<Film> shuffledFilms = new ArrayList<>(allFilms);
        Collections.shuffle(shuffledFilms);
        return shuffledFilms.subList(0, count);
    }

    @Override
    public List<Film> findLatestFilms(int count) {
        // Assuming films might be sorted by id or created date
        return filmRepository.findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }
}
