package ru.coursework.sklad_opt.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Category;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.model.OrderLine;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.repository.BatchRepository;
import ru.coursework.sklad_opt.repository.OrderLineRepository;
import ru.coursework.sklad_opt.repository.OrderRepository;
import ru.coursework.sklad_opt.repository.ProductRepository;
import ru.coursework.sklad_opt.repository.StockItemRepository;
import ru.coursework.sklad_opt.repository.StockMovementRepository;
import ru.coursework.sklad_opt.web.form.ProductForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BatchRepository batchRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository,
                          StockItemRepository stockItemRepository,
                          StockMovementRepository stockMovementRepository,
                          BatchRepository batchRepository,
                          OrderLineRepository orderLineRepository,
                          OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.batchRepository = batchRepository;
        this.orderLineRepository = orderLineRepository;
        this.orderRepository = orderRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> search(String q, Boolean active) {
        List<Product> base = productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        List<Product> result = new ArrayList<>();
        for (Product p : base) {
            boolean match = true;
            if (q != null && !q.isBlank()) {
                String qq = q.toLowerCase();
                match &= p.getName().toLowerCase().contains(qq) || p.getSku().toLowerCase().contains(qq);
            }
            if (active != null) {
                match &= p.isActive() == active;
            }
            if (match) {
                result.add(p);
            }
        }
        return result;
    }

    public Map<Long, BigDecimal> getAvailablePerProduct(List<Product> products) {
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Product p : products) {
            BigDecimal total = stockItemRepository.sumAvailableByProduct(p).orElse(BigDecimal.ZERO);
            map.put(p.getId(), total);
        }
        return map;
    }

    public Product createFromForm(ProductForm form, Category category) {
        Product product = new Product();
        fillProduct(product, form, category);
        return productRepository.save(product);
    }

    public Product updateFromForm(Product product, ProductForm form, Category category) {
        fillProduct(product, form, category);
        return productRepository.save(product);
    }

    private void fillProduct(Product product, ProductForm form, Category category) {
        product.setSku(form.getSku());
        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setCategory(category);
        product.setPrice(form.getPrice());
        product.setMinStock(form.getMinStock());
        product.setActive(form.isActive());
        product.setImageUrl(form.getImageUrl());
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        // удалить движения и остатки
        stockMovementRepository.deleteByProduct(product);
        stockItemRepository.deleteByProduct(product);
        batchRepository.deleteByProduct(product);

        // удалить строки заказов и пересчитать суммы заказов
        List<OrderLine> lines = orderLineRepository.findByProduct(product);
        Set<Long> affectedOrders = lines.stream()
                .map(l -> l.getOrder() != null ? l.getOrder().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        orderLineRepository.deleteAll(lines);
        for (Long orderId : affectedOrders) {
            orderRepository.findById(orderId).ifPresent(order -> {
                BigDecimal total = order.getLines().stream()
                        .map(l -> l.getPrice().multiply(l.getQuantity()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                order.setTotal(total);
                orderRepository.save(order);
            });
        }

        productRepository.delete(product);
    }
}
