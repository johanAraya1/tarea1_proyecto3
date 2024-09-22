package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.product.Product;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
