package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.sklad_opt.model.enums.OrderStatus;
import ru.coursework.sklad_opt.service.CustomerService;
import ru.coursework.sklad_opt.service.OrderService;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.web.form.OrderForm;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderController(OrderService orderService,
                           CustomerService customerService,
                           ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    public String orders(@RequestParam(value = "status", required = false) OrderStatus status,
                         Model model) {
        model.addAttribute("pageTitle", "Заказы");
        model.addAttribute("currentStatus", status);
        if (status != null) {
            model.addAttribute("orders", orderService.findByStatus(status));
        } else {
            model.addAttribute("orders", orderService.findAll());
        }
        model.addAttribute("statuses", OrderStatus.values());
        return "orders/list";
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        OrderForm form = new OrderForm();
        ensureLines(form, 3);
        model.addAttribute("form", form);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("pageTitle", "Новый заказ");
        return "orders/form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") OrderForm form,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        ensureLines(form, 3);
        try {
            String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            orderService.create(form, username);
            redirectAttributes.addFlashAttribute("success", "Заказ создан (DRAFT)");
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("pageTitle", "Новый заказ");
            model.addAttribute("error", e.getMessage());
            return "orders/form";
        }
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @ResponseBody
    public String exportCsv(@RequestParam(value = "status", required = false) OrderStatus status) {
        return orderService.exportCsv(status);
    }

    private void ensureLines(OrderForm form, int count) {
        while (form.getLines().size() < count) {
            form.getLines().add(new ru.coursework.sklad_opt.web.form.OrderLineForm());
        }
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.confirm(id);
            redirectAttributes.addFlashAttribute("success", "Заказ подтвержден");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/ship")
    public String ship(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.ship(id);
            redirectAttributes.addFlashAttribute("success", "Заказ отгружен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancel(id);
            redirectAttributes.addFlashAttribute("success", "Заказ отменен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }
}
