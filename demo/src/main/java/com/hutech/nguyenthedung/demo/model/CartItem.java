package com.hutech.nguyenthedung.demo.model;

public class CartItem {
    private Product product;
    private int quantity;
    private double price; // Trường này dùng để lưu giá sau khi tính toán ràng buộc

    // Constructor cập nhật (Quan trọng để CartService gọi được)
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        // Mặc định ban đầu lấy giá bán của sản phẩm
        this.price = product.getPrice();
    }

    // --- GETTERS AND SETTERS ---
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}