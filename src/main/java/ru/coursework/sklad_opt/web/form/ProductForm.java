package ru.coursework.sklad_opt.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductForm {

    @NotBlank
    @Size(max = 64)
    private String sku;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    private Long categoryId;

    @DecimalMin(value = "0", message = "Цена не может быть отрицательной")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "Мин. остаток не может быть отрицательным")
    private BigDecimal minStock;

    private boolean active = true;

    // либо URL, либо загруженный файл
    private String imageUrl;
    private org.springframework.web.multipart.MultipartFile imageFile;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public org.springframework.web.multipart.MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(org.springframework.web.multipart.MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
