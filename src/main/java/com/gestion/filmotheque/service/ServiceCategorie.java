package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Categorie;
import com.gestion.filmotheque.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategorie implements IServiceCategorie {
    @Autowired
    CategorieRepository categorieRepository;

    @Override
    public Categorie createCategorie(Categorie c) {
        return categorieRepository.save(c);
    }

    @Override
    public Categorie findCategorieById(int id) {
        return categorieRepository.findById(id).get();
    }

    @Override
    public List<Categorie> findAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public Categorie updateCategorie(Categorie c) {
        return categorieRepository.save(c);
    }

    @Override
    public void deleteCategorie(int id) {
        categorieRepository.deleteById(id);
    }

    @Override
    public boolean categorieExist(int id) {
        return categorieRepository.existsById(id);
    }
}
