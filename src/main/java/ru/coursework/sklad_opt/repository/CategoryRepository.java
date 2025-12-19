package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    List<Category> findByNameContainingIgnoreCase(String name);
}
