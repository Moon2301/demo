package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.model.Product;
import com.hutech.nguyenthedung.demo.service.CategoryService;
import com.hutech.nguyenthedung.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/products-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }

        if (Boolean.FALSE.equals(product.getSale())) {
            product.setSaleQuantity(0);
        }

        handleImageUpload(product, imageFile);
        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }

        // Logic xử lý thuộc tính Sale khi cập nhật
        if (Boolean.FALSE.equals(product.getSale())) {
            product.setSaleQuantity(0);
        }

        handleImageUpload(product, imageFile);
        productService.updateProduct(product);
        return "redirect:/products";
    }

    /**
     * Hàm dùng chung để xử lý upload ảnh
     */
    private void handleImageUpload(Product product, MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadPath + "/products/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(imageFile.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                product.setImage("products/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}