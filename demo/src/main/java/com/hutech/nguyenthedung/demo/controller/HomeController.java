package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.service.CategoryService;
import com.hutech.nguyenthedung.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {

        // 🔥 Sản phẩm đang sale
        model.addAttribute("saleProducts",
                productService.getSaleProducts());

        // 🔥 Sản phẩm không sale
        model.addAttribute("normalProducts",
                productService.getNormalProducts());

        // Nếu bạn cần category cho menu
        model.addAttribute("categories",
                categoryService.getAllCategories());

        return "home/home"; // đúng theo folder của bạn
    }
}