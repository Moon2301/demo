package com.hutech.nguyenthedung.demo.service;

import com.hutech.nguyenthedung.demo.model.CartItem;
import com.hutech.nguyenthedung.demo.model.Order;
import com.hutech.nguyenthedung.demo.model.OrderDetail;
import com.hutech.nguyenthedung.demo.repository.OrderDetailRepository;
import com.hutech.nguyenthedung.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrder(String customerName, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);

            // Gọi logic tắt sale nếu hết số lượng khuyến mãi
            productService.checkAndDisableSale(item.getProduct().getId(), item.getQuantity());
        }

        cartService.clearCart();
        return order;
    }
}