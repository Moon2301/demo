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
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String customerName, Model model) {
        // Lấy thông tin từ CartService trước khi thanh toán
        double totalPrice = cartService.getTotalPrice();
        double shippingFee = cartService.calculateShippingFee();
        double finalTotal = cartService.getFinalTotal();

        // Giả sử logic tích điểm: 100.000đ = 1 điểm
        int pointsEarned = (int) (finalTotal / 100000);

        // Lưu đơn hàng vào DB qua OrderService (đã bao gồm logic trừ kho sale)
        orderService.createOrder(customerName, cartService.getCartItems());

        // Truyền dữ liệu sang trang xác nhận
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("points", pointsEarned);
        model.addAttribute("customerName", customerName);

        // Xóa giỏ hàng
        cartService.clearCart();

        return "cart/order-confirmation";
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