package com.hutech.nguyenthedung.demo.controller;

import com.hutech.nguyenthedung.demo.model.CartItem;
import com.hutech.nguyenthedung.demo.service.CartService;
import com.hutech.nguyenthedung.demo.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // ================= CHECKOUT =================
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {

        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        Integer points = (Integer) session.getAttribute("loyaltyPoints");
        if (points == null) {
            points = 0;
            session.setAttribute("loyaltyPoints", 0);
        }

        model.addAttribute("points", points);

        return "cart/checkout";
    }

    // ================= SUBMIT ORDER =================
    @PostMapping("/submit")
    public String submitOrder(@RequestParam String customerName,
                              @RequestParam String phone,
                              @RequestParam String email,
                              @RequestParam String address,
                              @RequestParam String area,
                              HttpSession session) {

        List<CartItem> cartItems = cartService.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // 1️⃣ Tính tổng tiền sản phẩm
        double subtotal = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // 2️⃣ Tính phí vận chuyển
        double shipping = 0;
        if ("outer".equals(area)) {
            shipping = 100000; // Ngoại thành +100k
        }

        // 3️⃣ Tổng cuối cùng
        double total = subtotal + shipping;

        // 4️⃣ Tính điểm tích lũy (10.000đ = 1 điểm)
        int earnedPoints = (int) (total / 10000);

        Integer currentPoints =
                (Integer) session.getAttribute("loyaltyPoints");

        if (currentPoints == null) currentPoints = 0;

        session.setAttribute("loyaltyPoints",
                currentPoints + earnedPoints);

        // 5️⃣ Tạo đơn hàng
        orderService.createOrder(customerName, cartItems);

        // 6️⃣ Xóa giỏ hàng
        cartService.clearCart();

        return "redirect:/order/confirmation";
    }

    // ================= CONFIRM =================
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model,
                                    HttpSession session) {

        Integer points =
                (Integer) session.getAttribute("loyaltyPoints");

        if (points == null) points = 0;

        model.addAttribute("points", points);
        model.addAttribute("message",
                "Cảm ơn bạn đã mua hàng!");

        return "cart/order-confirmation";
    }
}