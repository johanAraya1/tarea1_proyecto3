package com.project.demo.logic.entity.product;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long category_id; // Aquí se almacena el ID de la categoría

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getcategory_id() {
        return category_id;
    }

    public void setcategory_id(Long category_id) {
        this.category_id = category_id;
    }



    // Constructor, getters y setters
    public ProductDTO(Long id, String name, String description, Double price, Integer stock, Long category_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category_id = category_id;
    }
}
