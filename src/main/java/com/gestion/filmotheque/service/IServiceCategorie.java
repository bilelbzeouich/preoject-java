package com.gestion.filmotheque.service;

import com.gestion.filmotheque.entities.Categorie;

import java.util.List;

public interface IServiceCategorie {
    public Categorie createCategorie(Categorie c);

    public Categorie findCategorieById(int id);

    public List<Categorie> findAllCategories();

    public Categorie updateCategorie(Categorie c);

    public void deleteCategorie(int id);

    public boolean categorieExist(int id);
}
