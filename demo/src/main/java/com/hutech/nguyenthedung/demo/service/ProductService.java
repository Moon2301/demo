package com.hutech.nguyenthedung.demo.service;

import com.hutech.nguyenthedung.demo.model.Product;
import com.hutech.nguyenthedung.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /* ================= LẤY TẤT CẢ ================= */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /* ================= LẤY SẢN PHẨM SALE ================= */
    public List<Product> getSaleProducts() {
        return productRepository.findBySaleTrue();
    }

    /* ================= LẤY SẢN PHẨM THƯỜNG ================= */
    public List<Product> getNormalProducts() {
        return productRepository.findBySaleFalse();
    }

    /* ================= THÊM ================= */
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    /* ================= TÌM THEO ID ================= */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /* ================= CẬP NHẬT ================= */
    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    /* ================= XÓA ================= */
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    /* ================= BẬT SALE ================= */
    public void enableSale(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setSale(true);
        productRepository.save(product);
    }

    /* ================= TẮT SALE ================= */
    public void disableSale(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setSale(false);
        productRepository.save(product);
    }

    @Transactional
    public void checkAndDisableSale(Long productId, int quantitySold) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (Boolean.TRUE.equals(product.getSale()) && product.getSaleQuantity() != null) {
            int newQuantity = product.getSaleQuantity() - quantitySold;

            if (newQuantity <= 0) {
                product.setSale(false); // Tắt chế độ sale
                product.setSaleQuantity(0);
            } else {
                product.setSaleQuantity(newQuantity);
            }
            productRepository.save(product);
        }
    }
}