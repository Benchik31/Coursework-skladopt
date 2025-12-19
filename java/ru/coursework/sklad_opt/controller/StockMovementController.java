package ru.coursework.sklad_opt.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.model.enums.MovementType;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.service.StockMovementService;
import ru.coursework.sklad_opt.service.WarehouseService;
import ru.coursework.sklad_opt.web.form.MovementForm;

@Controller
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public StockMovementController(StockMovementService stockMovementService,
                                   ProductService productService,
                                   WarehouseService warehouseService) {
        this.stockMovementService = stockMovementService;
        this.productService = productService;
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Журнал движений");
        model.addAttribute("movements", stockMovementService.findAll());
        return "stock/movements";
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @ResponseBody
    public String exportCsv() {
        return stockMovementService.exportCsv();
    }

    @GetMapping("/new")
    public String newMovement(@RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "productId", required = false) Long productId,
                              @RequestParam(value = "warehouseId", required = false) Long warehouseId,
                              Model model) {
        model.addAttribute("pageTitle", "Новое движение");
        MovementForm form = new MovementForm();
        if (type != null) {
            form.setType(type);
        }
        if (productId != null) {
            form.setProductId(productId);
        }
        if (warehouseId != null) {
            form.setWarehouseId(warehouseId);
        }
        model.addAttribute("form", form);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("warehouses", warehouseService.findAll());
        model.addAttribute("types", MovementType.values());
        return "stock/movement-form";
    }

    @PostMapping("/new")
    public String createMovement(@ModelAttribute("form") @Valid MovementForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("products", productService.findAll());
            model.addAttribute("warehouses", warehouseService.findAll());
            model.addAttribute("types", MovementType.values());
            model.addAttribute("pageTitle", "Новое движение");
            return "stock/movement-form";
        }

        Product product = productService.findById(form.getProductId()).orElse(null);
        Warehouse warehouse = warehouseService.findById(form.getWarehouseId()).orElse(null);
        Warehouse target = form.getTargetWarehouseId() != null
                ? warehouseService.findById(form.getTargetWarehouseId()).orElse(null)
                : null;

        if (product == null || warehouse == null) {
            bindingResult.reject("notFound", "Товар или склад не найдены");
            model.addAttribute("products", productService.findAll());
            model.addAttribute("warehouses", warehouseService.findAll());
            model.addAttribute("types", MovementType.values());
            model.addAttribute("pageTitle", "Новое движение");
            return "stock/movement-form";
        }

        stockMovementService.registerMovement(form, product, warehouse, target, null);
        return "redirect:/stock-movements";
    }
}
