package ru.coursework.sklad_opt.web.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderForm {

    @NotNull
    private Long customerId;

    private String comment;

    @Valid
    private List<OrderLineForm> lines = new ArrayList<>();

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<OrderLineForm> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineForm> lines) {
        this.lines = lines;
    }
}
