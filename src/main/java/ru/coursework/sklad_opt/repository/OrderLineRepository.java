package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.model.OrderLine;
import ru.coursework.sklad_opt.model.Product;

import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    List<OrderLine> findByOrder(Order order);
    List<OrderLine> findByProduct(Product product);
}
