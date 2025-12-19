package ru.coursework.sklad_opt.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.service.CategoryService;
import ru.coursework.sklad_opt.service.FileStorageService;
import ru.coursework.sklad_opt.web.form.ProductForm;

import java.util.List;
import ru.coursework.sklad_opt.model.Product;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    public ProductController(ProductService productService, CategoryService categoryService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "active", required = false) Boolean active,
                       Model model) {
        List<Product> products = productService.search(q, active);
        model.addAttribute("pageTitle", "Каталог товаров");
        model.addAttribute("products", products);
        model.addAttribute("availableMap", productService.getAvailablePerProduct(products));
        model.addAttribute("q", q);
        model.addAttribute("active", active);
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new ProductForm());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Новый товар");
        return "products/form";
    }

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String create(@ModelAttribute("form") ProductForm form,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model) {
        if (bindingResult.hasErrors() || form.getCategoryId() == null) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("pageTitle", "Новый товар");
            return "products/form";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            form.setImageUrl(fileStorageService.store(imageFile));
        }
        var category = categoryService.findById(form.getCategoryId()).orElse(null);
        productService.createFromForm(form, category);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id).orElseThrow();
        ProductForm form = new ProductForm();
        form.setSku(product.getSku());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        form.setPrice(product.getPrice());
        form.setMinStock(product.getMinStock());
        form.setActive(product.isActive());
        form.setImageUrl(product.getImageUrl());

        model.addAttribute("form", form);
        model.addAttribute("productId", id);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Редактировать товар");
        return "products/form";
    }

    @PostMapping(value = "/{id}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") ProductForm form,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model) {
        if (bindingResult.hasErrors() || form.getCategoryId() == null) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("pageTitle", "Редактировать товар");
            return "products/form";
        }
        Product product = productService.findById(id).orElseThrow();
        if (imageFile != null && !imageFile.isEmpty()) {
            form.setImageUrl(fileStorageService.store(imageFile));
        } else {
            // если не загрузили новое изображение — оставляем старое
            form.setImageUrl(product.getImageUrl());
        }
        var category = categoryService.findById(form.getCategoryId()).orElse(null);
        productService.updateFromForm(product, form, category);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Товар удалён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить товар: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
