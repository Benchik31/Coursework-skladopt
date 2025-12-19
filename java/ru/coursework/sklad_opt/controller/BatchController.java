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
import ru.coursework.sklad_opt.model.Batch;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.Supplier;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.service.BatchService;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.service.StockMovementService;
import ru.coursework.sklad_opt.service.SupplierService;
import ru.coursework.sklad_opt.service.WarehouseCrudService;
import ru.coursework.sklad_opt.web.form.BatchForm;
import ru.coursework.sklad_opt.web.form.MovementForm;

@Controller
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final WarehouseCrudService warehouseCrudService;
    private final StockMovementService stockMovementService;

    public BatchController(BatchService batchService,
                           ProductService productService,
                           SupplierService supplierService,
                           WarehouseCrudService warehouseCrudService,
                           StockMovementService stockMovementService) {
        this.batchService = batchService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.warehouseCrudService = warehouseCrudService;
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("batches", batchService.findAll());
        model.addAttribute("pageTitle", "Поставки (партии)");
        return "batches/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BatchForm());
        fillRefs(model);
        model.addAttribute("pageTitle", "Принять партию");
        return "batches/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("form") @Valid BatchForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            fillRefs(model);
            model.addAttribute("pageTitle", "Принять партию");
            return "batches/form";
        }
        Product product = productService.findById(form.getProductId()).orElse(null);
        Supplier supplier = supplierService.findById(form.getSupplierId()).orElse(null);
        Warehouse warehouse = warehouseCrudService.findById(form.getWarehouseId()).orElse(null);
        if (product == null || warehouse == null) {
            bindingResult.reject("notFound", "Товар или склад не найдены");
            fillRefs(model);
            model.addAttribute("pageTitle", "Принять партию");
            return "batches/form";
        }

        Batch batch = new Batch();
        batch.setProduct(product);
        batch.setSupplier(supplier);
        batch.setWarehouse(warehouse);
        batch.setReceivedDate(form.getReceivedDate());
        batch.setExpiryDate(form.getExpiryDate());
        batch.setPurchasePrice(form.getPurchasePrice());
        batch.setQuantity(form.getQuantity());
        batch.setNote(form.getNote());
        batchService.save(batch);

        MovementForm movementForm = new MovementForm();
        movementForm.setType("IN");
        movementForm.setProductId(product.getId());
        movementForm.setWarehouseId(warehouse.getId());
        movementForm.setQuantity(form.getQuantity());
        movementForm.setComment("Поставка, партия #" + batch.getId());
        stockMovementService.registerMovement(movementForm, product, warehouse, null, batch);

        return "redirect:/batches";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        batchService.delete(id);
        return "redirect:/batches";
    }

    private void fillRefs(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("warehouses", warehouseCrudService.findAll());
    }
}
