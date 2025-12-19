package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockMovement;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.model.enums.MovementType;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProduct(Product product);
    List<StockMovement> findByWarehouse(Warehouse warehouse);
    List<StockMovement> findByType(MovementType type);
    List<StockMovement> findByMovementTimeBetween(LocalDateTime from, LocalDateTime to);

    void deleteByProduct(Product product);
}
