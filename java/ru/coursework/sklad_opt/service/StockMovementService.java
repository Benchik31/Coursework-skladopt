package ru.coursework.sklad_opt.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Batch;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockItem;
import ru.coursework.sklad_opt.model.StockMovement;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.model.enums.MovementType;
import ru.coursework.sklad_opt.repository.StockMovementRepository;
import ru.coursework.sklad_opt.web.form.MovementForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final StockItemService stockItemService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                StockItemService stockItemService) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockItemService = stockItemService;
    }

    public List<StockMovement> findAll() {
        return stockMovementRepository.findAll(Sort.by(Sort.Direction.DESC, "movementTime"));
    }

    public StockMovement save(StockMovement movement) {
        return stockMovementRepository.save(movement);
    }

    public List<StockMovement> findByProduct(Product product) {
        return stockMovementRepository.findByProduct(product);
    }

    @Transactional
    public StockMovement registerMovement(MovementForm form, Product product, Warehouse warehouse,
                                          Warehouse targetWarehouse, Batch batch) {
        MovementType type = MovementType.valueOf(form.getType());
        BigDecimal qty = form.getQuantity();

        StockMovement movement = new StockMovement();
        movement.setType(type);
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setTargetWarehouse(targetWarehouse);
        movement.setBatch(batch);
        movement.setQuantity(qty);
        movement.setComment(form.getComment());

        switch (type) {
            case IN -> applyIn(warehouse, product, qty);
            case OUT -> applyOut(warehouse, product, qty);
            case TRANSFER -> applyTransfer(warehouse, targetWarehouse, product, qty);
            default -> throw new IllegalArgumentException("Unknown movement type");
        }

        return stockMovementRepository.save(movement);
    }

    private void applyIn(Warehouse warehouse, Product product, BigDecimal qty) {
        StockItem item = stockItemService.getOrCreate(product, warehouse);
        item.setQtyOnHand(item.getQtyOnHand().add(qty));
        stockItemService.save(item);
    }

    private void applyOut(Warehouse warehouse, Product product, BigDecimal qty) {
        StockItem item = stockItemService.getOrCreate(product, warehouse);
        item.setQtyOnHand(item.getQtyOnHand().subtract(qty));
        stockItemService.save(item);
    }

    private void applyTransfer(Warehouse from, Warehouse to, Product product, BigDecimal qty) {
        if (to == null) {
            throw new IllegalArgumentException("Target warehouse is required for transfer");
        }
        StockItem fromItem = stockItemService.getOrCreate(product, from);
        fromItem.setQtyOnHand(fromItem.getQtyOnHand().subtract(qty));
        stockItemService.save(fromItem);

        StockItem toItem = stockItemService.getOrCreate(product, to);
        toItem.setQtyOnHand(toItem.getQtyOnHand().add(qty));
        stockItemService.save(toItem);
    }

    public String exportCsv() {
        List<StockMovement> list = findAll();
        String header = "datetime,type,product,warehouse,target_warehouse,quantity,comment\n";
        return header + list.stream()
                .map(mv -> String.join(",",
                        quote(mv.getMovementTime().toString()),
                        quote(mv.getType().name()),
                        quote(mv.getProduct() != null ? mv.getProduct().getName() : ""),
                        quote(mv.getWarehouse() != null ? mv.getWarehouse().getName() : ""),
                        quote(mv.getTargetWarehouse() != null ? mv.getTargetWarehouse().getName() : ""),
                        mv.getQuantity().toPlainString(),
                        quote(mv.getComment() != null ? mv.getComment() : "")
                ))
                .collect(Collectors.joining("\n"));
    }

    private String quote(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
