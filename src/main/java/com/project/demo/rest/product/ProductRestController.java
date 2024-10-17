package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductDTO;
import com.project.demo.logic.entity.product.ProductRepository;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.Converter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.WebResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request){

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream().map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory().getId() : null
        )). collect(Collectors.toList());
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                productDTOs, HttpStatus.OK, meta);
    }

    @GetMapping("/category/{id}/categories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllByCategory (@PathVariable Long id,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if(foundCategory.isPresent()) {

            Pageable pageable = PageRequest.of(page-1, size);
            Page<Product> produtsPage = productRepository.getProductByCategoryId(id, pageable);
            // Convertir productos a ProductDTO
            List<ProductDTO> productDTOs = produtsPage.getContent().stream().map(product -> new ProductDTO(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategory() != null ? product.getCategory().getId() : null
            )). collect(Collectors.toList());

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(produtsPage.getTotalPages());
            meta.setTotalElements(produtsPage.getTotalElements());
            meta.setPageNumber(produtsPage.getNumber() + 1);
            meta.setPageSize(produtsPage.getSize());


            return new GlobalResponseHandler().handleResponse("Order retrieved successfully",
                    productDTOs, HttpStatus.OK, meta);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping("/category/{id}")
    public ResponseEntity<?> addProductToCategory(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if(foundCategory.isPresent()) {
            product.setCategory(foundCategory.get());
            Product savedProduct = productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Order created successfully",
                    savedProduct, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{productId}/{categoryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @PathVariable Long categoryId,@RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        Optional<Category> foundCategory = categoryRepository.findById(categoryId);
        if (foundProduct.isPresent() && foundCategory.isPresent()) {
            // Actualizar el ID del producto
            product.setId(productId);
            // Actualizar o mantener la categoria el ID del producto
            product.setCategory(foundCategory.get());
            // Guardar el producto actualizado
            productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }


    @GetMapping("/category/{id}/products")
    public ResponseEntity<?> getAllProductsByid(@PathVariable Long id,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if (foundCategory.isPresent()) {
            Pageable pageable = PageRequest.of(page-1, size);

            Page<Product> productsPage =productRepository.getProductByCategoryId(id, pageable);

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(productsPage.getTotalPages());
            meta.setTotalElements(productsPage.getTotalElements());
            meta.setPageNumber(productsPage.getNumber() + 1);
            meta.setPageSize(productsPage.getSize());
            return new GlobalResponseHandler().handleResponse("Order retrieved successfully",
                    productsPage.getContent(), HttpStatus.OK, meta);
        }else{
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }

    }



    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            Optional<Category> category = categoryRepository.findById(foundProduct.get().getCategory().getId());
            category.get().getProducts().remove(foundProduct.get());
            productRepository.deleteById(foundProduct.get().getId());
            return new GlobalResponseHandler().handleResponse("Order deleted successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Order id " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    
}
