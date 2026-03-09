package com.hutech.nguyenthedung.demo.service;

import com.hutech.nguyenthedung.demo.model.Category;
import com.hutech.nguyenthedung.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /* ================= CREATE ================= */
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    /* ================= READ ALL ================= */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /* ================= READ BY ID ================= */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /* ================= UPDATE ================= */
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    /* ================= DELETE ================= */
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
}
