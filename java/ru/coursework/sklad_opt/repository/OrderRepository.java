package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.Customer;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByNumber(String number);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomer(Customer customer);
    List<Order> findByCreatedBy(User user);
    long countByStatus(OrderStatus status);
}
