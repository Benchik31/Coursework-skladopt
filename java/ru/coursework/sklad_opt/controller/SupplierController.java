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
import ru.coursework.sklad_opt.model.Supplier;
import ru.coursework.sklad_opt.service.SupplierService;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Поставщики");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("pageTitle", "Новый поставщик");
        return "suppliers/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("supplier") @Valid Supplier supplier,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "suppliers/form";
        }
        supplierService.save(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id).orElseThrow();
        model.addAttribute("supplier", supplier);
        model.addAttribute("pageTitle", "Редактировать поставщика");
        return "suppliers/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("supplier") @Valid Supplier form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "suppliers/form";
        }
        Supplier supplier = supplierService.findById(id).orElseThrow();
        supplier.setName(form.getName());
        supplier.setTaxId(form.getTaxId());
        supplier.setContactName(form.getContactName());
        supplier.setPhone(form.getPhone());
        supplier.setEmail(form.getEmail());
        supplier.setAddress(form.getAddress());
        supplierService.save(supplier);
        return "redirect:/suppliers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        supplierService.delete(id);
        return "redirect:/suppliers";
    }
}
