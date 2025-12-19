package ru.coursework.sklad_opt.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public class MovementForm {

    @NotNull
    private Long productId;

    @NotNull
    private Long warehouseId;

    private Long targetWarehouseId;

    @NotNull
    @DecimalMin(value = "1", message = "Количество должно быть больше нуля")
    @Digits(integer = 12, fraction = 0, message = "Количество должно быть целым")
    private BigDecimal quantity;

    @NotNull
    private String type;

    private String comment;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getTargetWarehouseId() {
        return targetWarehouseId;
    }

    public void setTargetWarehouseId(Long targetWarehouseId) {
        this.targetWarehouseId = targetWarehouseId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
