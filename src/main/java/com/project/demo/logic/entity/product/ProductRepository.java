package com.project.demo.logic.entity.product;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Page<Product> getProductByCategoryId(Long id, Pageable pageable);

    <T> Page<T> findAll(Pageable pageable);
}
