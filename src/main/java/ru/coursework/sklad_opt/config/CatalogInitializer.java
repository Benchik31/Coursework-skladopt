package ru.coursework.sklad_opt.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Category;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.repository.CategoryRepository;
import ru.coursework.sklad_opt.repository.ProductRepository;

import java.math.BigDecimal;

@Component
@Order(1)
public class CatalogInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogInitializer(CategoryRepository categoryRepository,
                              ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Category catFood = categoryRepository.save(new Category(null, "Продукты питания", null));
        Category catBeverage = categoryRepository.save(new Category(null, "Напитки", null));

        productRepository.save(new Product(null, "SKU-1001", "Макароны Fusilli 500г",
                "Сухие макароны из твердых сортов пшеницы", catFood,
                new BigDecimal("75.50"), new BigDecimal("20"), true, null));

        productRepository.save(new Product(null, "SKU-2001", "Сок апельсиновый 1л",
                "Нектар без сахара", catBeverage,
                new BigDecimal("95.00"), new BigDecimal("15"), true, null));

        productRepository.save(new Product(null, "SKU-2002", "Вода минеральная 0.5л",
                "Негазированная, стекло", catBeverage,
                new BigDecimal("35.00"), new BigDecimal("30"), true, null));
    }
}
