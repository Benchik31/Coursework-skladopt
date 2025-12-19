package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.service.StockItemService;
import ru.coursework.sklad_opt.service.StockMovementService;

@Controller
@RequestMapping("/products")
public class ProductDetailController {

    private final ProductService productService;
    private final StockItemService stockItemService;
    private final StockMovementService stockMovementService;

    public ProductDetailController(ProductService productService,
                                   StockItemService stockItemService,
                                   StockMovementService stockMovementService) {
        this.productService = productService;
        this.stockItemService = stockItemService;
        this.stockMovementService = stockMovementService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("stocks", stockItemService.findByProduct(product));
        model.addAttribute("movements", stockMovementService.findByProduct(product));
        model.addAttribute("pageTitle", product.getName());
        return "products/detail";
    }
}
