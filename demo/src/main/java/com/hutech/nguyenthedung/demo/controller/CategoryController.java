package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.model.Category;
import com.hutech.nguyenthedung.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // Thêm import này
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // Khai báo duy nhất một lần ở đây để dùng chung cho cả class
    @Value("${upload.path}")
    private String uploadPath;

    /* ================= SHARE CATEGORY FOR LAYOUT ================= */
    @ModelAttribute("categories")
    public java.util.List<Category> categories() {
        return categoryService.getAllCategories();
    }

    /* ================= LIST ================= */
    @GetMapping
    public String listCategories() {
        return "categories/categories-list";
    }

    /* ================= FORM ADD ================= */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/add-category";
    }

    /* ================= HANDLE ADD ================= */
    @PostMapping("/add")
    public String addCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            @RequestParam("iconFile") MultipartFile iconFile,
            Model model) {

        if (result.hasErrors()) {
            return "categories/add-category";
        }

        try {
            if (!iconFile.isEmpty()) {
                String fileName = System.currentTimeMillis()
                        + "_" + iconFile.getOriginalFilename().replaceAll("\\s+", "_");

                // Sử dụng uploadPath đã cấu hình thay vì src/...
                Path staticPath = Paths.get(uploadPath, "/category");
                if (!Files.exists(staticPath)) {
                    Files.createDirectories(staticPath);
                }

                Path filePath = staticPath.resolve(fileName);
                Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                category.setIconPath(fileName);
            }
            categoryService.addCategory(category);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload icon: " + e.getMessage());
            return "categories/add-category";
        }

        return "redirect:/categories";
    }

    /* ================= FORM UPDATE ================= */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/categories";
        }
        model.addAttribute("category", category);
        return "categories/update-category";
    }

    /* ================= HANDLE UPDATE ================= */
    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("category") Category formCategory,
            BindingResult result,
            @RequestParam("iconFile") MultipartFile iconFile,
            Model model) {

        if (result.hasErrors()) {
            return "categories/update-category";
        }

        try {
            Category existingCategory = categoryService.getCategoryById(id);
            if (existingCategory == null) {
                return "redirect:/categories";
            }

            existingCategory.setName(formCategory.getName());

            if (!iconFile.isEmpty()) {
                String fileName = System.currentTimeMillis()
                        + "_" + iconFile.getOriginalFilename().replaceAll("\\s+", "_");

                // Thống nhất dùng chung một thư mục lưu trữ ngoài project
                Path staticPath = Paths.get(uploadPath, "/category");
                if (!Files.exists(staticPath)) {
                    Files.createDirectories(staticPath);
                }

                Path filePath = staticPath.resolve(fileName);
                Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                existingCategory.setIconPath(fileName);
            }
            categoryService.updateCategory(existingCategory);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi upload icon");
            return "categories/update-category";
        }

        return "redirect:/categories";
    }

    /* ================= DELETE ================= */
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            if (category.getIconPath() != null) {
                try {
                    // Xóa file tại thư mục cấu hình ngoài project
                    Path filePath = Paths.get(uploadPath, "category").resolve(category.getIconPath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            categoryService.deleteCategoryById(id);
        }
        return "redirect:/categories";
    }
}