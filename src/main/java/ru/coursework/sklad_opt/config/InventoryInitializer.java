package ru.coursework.sklad_opt.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockItem;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.repository.ProductRepository;
import ru.coursework.sklad_opt.repository.StockItemRepository;
import ru.coursework.sklad_opt.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(2)
public class InventoryInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockItemRepository stockItemRepository;

    public InventoryInitializer(ProductRepository productRepository,
                                WarehouseRepository warehouseRepository,
                                StockItemRepository stockItemRepository) {
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockItemRepository = stockItemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (stockItemRepository.count() > 0) {
            return;
        }

        Warehouse msk = warehouseRepository.save(new Warehouse(null, "MSK", "Склад Москва", "Москва, ул. Примерная 1"));
        Warehouse spb = warehouseRepository.save(new Warehouse(null, "SPB", "Склад Санкт-Петербург", "Санкт-Петербург, Невский 10"));

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }

        for (Product product : products) {
            stockItemRepository.save(new StockItem(null, product, msk,
                    new BigDecimal("120"), new BigDecimal("10")));
            stockItemRepository.save(new StockItem(null, product, spb,
                    new BigDecimal("80"), new BigDecimal("5")));
        }
    }
}
