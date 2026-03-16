package com.hutech.nguyenthedung.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    // ✅ Giá khuyến mãi
    @Column(name = "sale_price")
    private Double salePrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_sale")
    private Boolean sale = false;

    private Double originalPrice;

    private Integer saleQuantity = 0;

}