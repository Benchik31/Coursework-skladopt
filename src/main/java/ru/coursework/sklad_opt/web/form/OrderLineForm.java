package ru.coursework.sklad_opt.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderLineForm {

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "1", message = "Количество должно быть больше нуля")
    private BigDecimal quantity;

    @DecimalMin(value = "0", message = "Цена не может быть отрицательной")
    private BigDecimal price;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
