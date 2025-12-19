package ru.coursework.sklad_opt.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Customer;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.model.OrderLine;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.model.enums.OrderStatus;
import ru.coursework.sklad_opt.repository.OrderRepository;
import ru.coursework.sklad_opt.repository.UserRepository;
import ru.coursework.sklad_opt.web.form.OrderForm;
import ru.coursework.sklad_opt.web.form.OrderLineForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final StockItemService stockItemService;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository,
                        StockItemService stockItemService,
                        UserRepository userRepository,
                        ProductService productService,
                        CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.stockItemService = stockItemService;
        this.userRepository = userRepository;
        this.productService = productService;
        this.customerService = customerService;
    }

    public List<Order> findAll() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order create(OrderForm form, String username) {
        Customer customer = customerService.findById(form.getCustomerId()).orElseThrow();
        User creator = null;
        if (username != null) {
            creator = userRepository.findByUsername(username).orElse(null);
        }

        Order order = new Order();
        order.setNumber(generateNumber());
        order.setCustomer(customer);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.DRAFT);
        order.setComment(form.getComment());
        order.setCreatedAt(java.time.LocalDateTime.now());

        List<OrderLineForm> validLines = form.getLines().stream()
                .filter(l -> l.getProductId() != null && l.getQuantity() != null && l.getQuantity().signum() > 0)
                .toList();
        if (validLines.isEmpty()) {
            throw new IllegalArgumentException("Добавьте хотя бы одну позицию");
        }

        for (OrderLineForm lf : validLines) {
            Product product = productService.findById(lf.getProductId()).orElseThrow();
            OrderLine line = new OrderLine();
            line.setOrder(order);
            line.setProduct(product);
            line.setQuantity(lf.getQuantity());
            if (lf.getPrice() != null) {
                line.setPrice(lf.getPrice());
            } else {
                line.setPrice(product.getPrice());
            }
            order.addLine(line);
        }

        BigDecimal total = order.getLines().stream()
                .filter(l -> l.getPrice() != null && l.getQuantity() != null)
                .map(l -> l.getPrice().multiply(l.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        return orderRepository.save(order);
    }

    private String generateNumber() {
        long count = orderRepository.count() + 1;
        return "ORD-" + String.format("%05d", count);
    }

    public String exportCsv(OrderStatus status) {
        List<Order> list = status != null ? findByStatus(status) : findAll();
        String header = "number,status,customer,total,created_at\n";
        return header + list.stream()
                .map(o -> String.join(",",
                        quote(o.getNumber()),
                        quote(o.getStatus().name()),
                        quote(o.getCustomer() != null ? o.getCustomer().getName() : ""),
                        o.getTotal() != null ? o.getTotal().toPlainString() : "0",
                        quote(o.getCreatedAt() != null ? o.getCreatedAt().toString() : "")
                ))
                .collect(Collectors.joining("\n"));
    }

    private String quote(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    @Transactional
    public void confirm(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Подтвердить можно только заказ в статусе DRAFT");
        }
        for (OrderLine line : order.getLines()) {
            stockItemService.reserve(line.getProduct(), line.getQuantity());
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional
    public void ship(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Отгрузить можно только заказ в статусе CONFIRMED");
        }
        for (OrderLine line : order.getLines()) {
            stockItemService.ship(line.getProduct(), line.getQuantity());
        }
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderLine line : order.getLines()) {
                stockItemService.release(line.getProduct(), line.getQuantity());
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);
    }
}
