package ru.coursework.sklad_opt.service;

import org.springframework.stereotype.Service;
import ru.coursework.sklad_opt.model.Warehouse;
import ru.coursework.sklad_opt.repository.WarehouseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseCrudService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseCrudService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> findById(Long id) {
        return warehouseRepository.findById(id);
    }

    public Warehouse save(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }
}
