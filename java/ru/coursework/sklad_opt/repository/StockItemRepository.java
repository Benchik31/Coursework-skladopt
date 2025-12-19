package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockItem;
import ru.coursework.sklad_opt.model.Warehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByProductAndWarehouse(Product product, Warehouse warehouse);
    List<StockItem> findByWarehouse(Warehouse warehouse);
    List<StockItem> findByProduct(Product product);

    @Query("select coalesce(sum(si.qtyOnHand - si.reservedQty), 0) from StockItem si where si.product = :product")
    Optional<BigDecimal> sumAvailableByProduct(@Param("product") Product product);

    @Query("select count(si) from StockItem si where si.qtyOnHand - si.reservedQty < coalesce(si.product.minStock, 0)")
    long countLowStock();

    void deleteByProduct(Product product);
}
