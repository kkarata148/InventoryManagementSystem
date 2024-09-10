package com.inventory.service;

import com.inventory.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void save(Category category);
    void initCategories();
}
