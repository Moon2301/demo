package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String showCart(Model model, HttpSession session) {

        model.addAttribute("cartItems", cartService.getCartItems());

        // Lấy điểm từ session
        Integer points = (Integer) session.getAttribute("loyaltyPoints");
        if (points == null) {
            points = 0;
            session.setAttribute("loyaltyPoints", 0);
        }

        model.addAttribute("points", points);

        return "cart/cart";
    }

    @PostMapping("/add")
    @ResponseBody // Bắt buộc phải có để trả về JSON
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                       @RequestParam int quantity) {
        try {
            cartService.addToCart(productId, quantity);
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "success",
                    "totalItems", cartService.getTotalQuantity()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {

        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {

        cartService.clearCart();
        return "redirect:/cart";
    }
}