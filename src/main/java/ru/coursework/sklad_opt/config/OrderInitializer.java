package ru.coursework.sklad_opt.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Customer;
import ru.coursework.sklad_opt.model.OrderLine;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.model.enums.OrderStatus;
import ru.coursework.sklad_opt.repository.CustomerRepository;
import ru.coursework.sklad_opt.repository.OrderLineRepository;
import ru.coursework.sklad_opt.repository.OrderRepository;
import ru.coursework.sklad_opt.repository.ProductRepository;
import ru.coursework.sklad_opt.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(3)
public class OrderInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final UserRepository userRepository;

    public OrderInitializer(CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            OrderLineRepository orderLineRepository,
                            UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (orderRepository.count() > 0) {
            return;
        }

        Customer customer = customerRepository.save(
                new Customer(null, "ООО «Первый клиент»", "7701234567", "Иванов Иван",
                        "+7 999 111-22-33", "client@example.com", "Москва, ул. Примерная 2"));

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }
        Product p1 = products.get(0);
        Product p2 = products.size() > 1 ? products.get(1) : products.get(0);

        User creator = userRepository.findByUsername("admin").orElse(null);

        ru.coursework.sklad_opt.model.Order order1 = new ru.coursework.sklad_opt.model.Order();
        order1.setNumber("ORDER-001");
        order1.setCustomer(customer);
        order1.setCreatedBy(creator);
        order1.setStatus(OrderStatus.CONFIRMED);
        order1.setCreatedAt(LocalDateTime.now().minusDays(2));
        order1.setTotal(BigDecimal.ZERO);
        order1 = orderRepository.save(order1);

        OrderLine line1 = new OrderLine();
        line1.setOrder(order1);
        line1.setProduct(p1);
        line1.setPrice(p1.getPrice() != null ? p1.getPrice() : BigDecimal.ZERO);
        line1.setQuantity(new BigDecimal("10"));
        orderLineRepository.save(line1);

        OrderLine line2 = new OrderLine();
        line2.setOrder(order1);
        line2.setProduct(p2);
        line2.setPrice(p2.getPrice() != null ? p2.getPrice() : BigDecimal.ZERO);
        line2.setQuantity(new BigDecimal("5"));
        orderLineRepository.save(line2);

        BigDecimal total1 = line1.getPrice().multiply(line1.getQuantity())
                .add(line2.getPrice().multiply(line2.getQuantity()));
        order1.setTotal(total1);
        orderRepository.save(order1);

        ru.coursework.sklad_opt.model.Order order2 = new ru.coursework.sklad_opt.model.Order();
        order2.setNumber("ORDER-002");
        order2.setCustomer(customer);
        order2.setCreatedBy(creator);
        order2.setStatus(OrderStatus.DRAFT);
        order2.setCreatedAt(LocalDateTime.now().minusDays(1));
        order2.setTotal(line1.getPrice().multiply(new BigDecimal("3")));
        order2 = orderRepository.save(order2);

        OrderLine line3 = new OrderLine();
        line3.setOrder(order2);
        line3.setProduct(p1);
        line3.setPrice(line1.getPrice());
        line3.setQuantity(new BigDecimal("3"));
        orderLineRepository.save(line3);
    }
}
