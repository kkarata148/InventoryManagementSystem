package com.inventory.service.impl;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import com.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void initCategories() {
        List<String> defaultCategories = Arrays.asList("Electronics", "Furniture", "Clothing", "Food", "Books");

        // Check if categories are already initialized
        if (categoryRepository.count() == 0) {
            defaultCategories.forEach(categoryName -> {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
            });
        }
    }
}
