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
import ru.coursework.sklad_opt.model.Category;
import ru.coursework.sklad_opt.service.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Категории");
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Новая категория");
        model.addAttribute("categories", categoryService.findAll());
        return "categories/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("category") @Valid Category category,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "categories/form";
        }
        categoryService.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id).orElseThrow();
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Редактировать категорию");
        model.addAttribute("categories", categoryService.findAll());
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("category") @Valid Category form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "categories/form";
        }
        Category category = categoryService.findById(id).orElseThrow();
        category.setName(form.getName());
        category.setParent(form.getParent());
        categoryService.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
