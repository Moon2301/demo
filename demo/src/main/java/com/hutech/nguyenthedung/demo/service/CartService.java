package com.hutech.nguyenthedung.demo.service;

import com.hutech.nguyenthedung.demo.model.CartItem;
import com.hutech.nguyenthedung.demo.model.Product;
import com.hutech.nguyenthedung.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private final List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    /** Thêm sản phẩm vào giỏ */
    public void addToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + productId)
                );

        // Nếu sản phẩm đã tồn tại trong giỏ → cộng thêm số lượng
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Nếu chưa có → thêm mới
        cartItems.add(new CartItem(product, quantity));
    }

    /** Lấy danh sách sản phẩm trong giỏ */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /** Xóa 1 sản phẩm khỏi giỏ */
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item ->
                item.getProduct().getId().equals(productId)
        );
    }

    /** Xóa toàn bộ giỏ hàng */
    public void clearCart() {
        cartItems.clear();
    }

    public int getTotalQuantity() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getTotalPrice() {
        return cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();
    }

    public double calculateShippingFee() {
        double currentTotal = getTotalPrice();
        int totalQuantity = getTotalQuantity();
        if (totalQuantity >= 2 && currentTotal > 1000000) {
            return 0;
        }
        return 30000;
    }

    public double getFinalTotal() {
        return getTotalPrice() + calculateShippingFee();
    }
}