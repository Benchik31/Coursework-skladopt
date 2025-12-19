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
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.service.WarehouseCrudService;

@Controller
@RequestMapping("/warehouses")
public class WarehouseCrudController {

    private final WarehouseCrudService warehouseCrudService;

    public WarehouseCrudController(WarehouseCrudService warehouseCrudService) {
        this.warehouseCrudService = warehouseCrudService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("warehouses", warehouseCrudService.findAll());
        model.addAttribute("pageTitle", "Склады");
        return "warehouses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        model.addAttribute("pageTitle", "Новый склад");
        return "warehouses/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("warehouse") @Valid Warehouse warehouse,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "warehouses/form";
        }
        warehouseCrudService.save(warehouse);
        return "redirect:/warehouses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Warehouse warehouse = warehouseCrudService.findById(id).orElseThrow();
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("pageTitle", "Редактировать склад");
        return "warehouses/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("warehouse") @Valid Warehouse form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "warehouses/form";
        }
        Warehouse warehouse = warehouseCrudService.findById(id).orElseThrow();
        warehouse.setName(form.getName());
        warehouse.setCode(form.getCode());
        warehouse.setAddress(form.getAddress());
        warehouseCrudService.save(warehouse);
        return "redirect:/warehouses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        warehouseCrudService.delete(id);
        return "redirect:/warehouses";
    }
}
