package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderDetailController {

    private final OrderService orderService;

    public OrderDetailController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("lines", order.getLines());
        model.addAttribute("pageTitle", "Заказ " + order.getNumber());
        return "orders/detail";
    }
}
