package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.model.Product;
import com.hutech.nguyenthedung.demo.service.CategoryService;
import com.hutech.nguyenthedung.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Hiển thị danh sách sản phẩm
     */
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/products-list";
    }

    /**
     * Hiển thị form thêm sản phẩm
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add-product";
    }

    /**
     * Xử lý thêm sản phẩm (CÓ ẢNH)
     */
    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }

        // ===== XỬ LÝ UPLOAD ẢNH =====
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();

                Path uploadDir = Paths.get("src/main/upload/static/images/products");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Files.copy(
                        imageFile.getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                product.setImage(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    /**
     * Hiển thị form sửa sản phẩm
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid product ID: " + id)
                );

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/update-product";
    }

    /**
     * Xử lý cập nhật sản phẩm (CÓ ẢNH)
     */
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }

        // Nếu có ảnh mới thì cập nhật
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();

                Path uploadDir = Paths.get("target/classes/static/images/products/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Files.copy(
                        imageFile.getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                product.setImage(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    /**
     * Xóa sản phẩm
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
