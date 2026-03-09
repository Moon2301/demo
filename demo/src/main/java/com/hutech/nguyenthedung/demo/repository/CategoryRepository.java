package com.hutech.nguyenthedung.demo.repository;

import com.hutech.nguyenthedung.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
