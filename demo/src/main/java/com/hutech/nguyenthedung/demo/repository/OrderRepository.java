package com.hutech.nguyenthedung.demo.repository;

import com.hutech.nguyenthedung.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}