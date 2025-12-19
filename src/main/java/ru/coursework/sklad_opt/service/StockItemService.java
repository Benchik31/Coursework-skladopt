package ru.coursework.sklad_opt.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockItem;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.repository.StockItemRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StockItemService {

    private final StockItemRepository stockItemRepository;

    public StockItemService(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }

    public List<StockItem> findAll() {
        return stockItemRepository.findAll(Sort.by("product.name", "warehouse.name"));
    }

    public List<StockItem> findByProduct(Product product) {
        return stockItemRepository.findByProduct(product);
    }

    public List<StockItem> findByWarehouse(Warehouse warehouse) {
        return stockItemRepository.findByWarehouse(warehouse);
    }

    public StockItem save(StockItem item) {
        return stockItemRepository.save(item);
    }

    public StockItem getOrCreate(Product product, Warehouse warehouse) {
        Optional<StockItem> existing = stockItemRepository.findByProductAndWarehouse(product, warehouse);
        if (existing.isPresent()) {
            return existing.get();
        }
        StockItem item = new StockItem();
        item.setProduct(product);
        item.setWarehouse(warehouse);
        item.setQtyOnHand(item.getQtyOnHand());
        item.setReservedQty(item.getReservedQty());
        return stockItemRepository.save(item);
    }

    public void reserve(Product product, BigDecimal quantity) {
        allocate(product, quantity, true);
    }

    public void release(Product product, BigDecimal quantity) {
        allocate(product, quantity, false);
    }

    public void ship(Product product, BigDecimal quantity) {
        List<StockItem> items = stockItemRepository.findByProduct(product)
                .stream()
                .sorted(Comparator.comparing(StockItem::getReservedQty).reversed())
                .toList();
        BigDecimal remaining = quantity;
        for (StockItem item : items) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal take = item.getReservedQty().min(remaining);
            item.setReservedQty(item.getReservedQty().subtract(take));
            item.setQtyOnHand(item.getQtyOnHand().subtract(take));
            remaining = remaining.subtract(take);
            stockItemRepository.save(item);
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Недостаточно зарезервированного товара для отгрузки");
        }
    }

    private void allocate(Product product, BigDecimal quantity, boolean reserve) {
        List<StockItem> items = stockItemRepository.findByProduct(product)
                .stream()
                .sorted(Comparator.comparing(StockItem::getAvailable).reversed())
                .toList();
        BigDecimal remaining = quantity;
        for (StockItem item : items) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal available = item.getAvailable();
            if (!reserve) {
                available = item.getReservedQty();
            }
            if (available.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal take = available.min(remaining);
            if (reserve) {
                item.setReservedQty(item.getReservedQty().add(take));
            } else {
                item.setReservedQty(item.getReservedQty().subtract(take));
                if (item.getReservedQty().compareTo(BigDecimal.ZERO) < 0) {
                    item.setReservedQty(BigDecimal.ZERO);
                }
            }
            remaining = remaining.subtract(take);
            stockItemRepository.save(item);
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Недостаточно доступного остатка");
        }
    }
}
