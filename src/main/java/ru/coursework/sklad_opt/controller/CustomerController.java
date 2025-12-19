package ru.coursework.sklad_opt.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.model.Customer;
import ru.coursework.sklad_opt.service.CustomerService;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("pageTitle", "Клиенты");
        return "customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Новый клиент");
        return "customers/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("customer") @Valid Customer customer,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "customers/form";
        }
        customerService.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id).orElseThrow();
        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Редактировать клиента");
        return "customers/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("customer") @Valid Customer form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "customers/form";
        }
        Customer customer = customerService.findById(id).orElseThrow();
        customer.setName(form.getName());
        customer.setTaxId(form.getTaxId());
        customer.setContactName(form.getContactName());
        customer.setPhone(form.getPhone());
        customer.setEmail(form.getEmail());
        customer.setAddress(form.getAddress());
        customerService.save(customer);
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        customerService.delete(id);
        return "redirect:/customers";
    }
}
