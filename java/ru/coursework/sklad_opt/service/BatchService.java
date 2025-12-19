package ru.coursework.sklad_opt.service;

import org.springframework.stereotype.Service;
import ru.coursework.sklad_opt.model.Batch;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.Supplier;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.repository.BatchRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<Batch> findAll() {
        return batchRepository.findAll();
    }

    public Optional<Batch> findById(Long id) {
        return batchRepository.findById(id);
    }

    public Batch save(Batch batch) {
        return batchRepository.save(batch);
    }

    public void delete(Long id) {
        batchRepository.deleteById(id);
    }

    public List<Batch> findByProduct(Product product) {
        return batchRepository.findByProduct(product);
    }

    public List<Batch> findBySupplier(Supplier supplier) {
        return batchRepository.findBySupplier(supplier);
    }

    public List<Batch> findByWarehouse(Warehouse warehouse) {
        return batchRepository.findByWarehouse(warehouse);
    }
}
