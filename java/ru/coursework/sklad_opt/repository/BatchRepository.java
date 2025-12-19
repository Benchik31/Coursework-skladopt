package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.Batch;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.Supplier;
import ru.coursework.sklad_opt.model.Warehouse;

import java.time.LocalDate;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByProduct(Product product);
    List<Batch> findBySupplier(Supplier supplier);
    List<Batch> findByWarehouse(Warehouse warehouse);
    List<Batch> findByExpiryDateBefore(LocalDate date);

    void deleteByProduct(Product product);
}
