package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.model.enums.OrderStatus;
import ru.coursework.sklad_opt.repository.BatchRepository;
import ru.coursework.sklad_opt.repository.OrderRepository;
import ru.coursework.sklad_opt.repository.ProductRepository;
import ru.coursework.sklad_opt.repository.StockItemRepository;
import ru.coursework.sklad_opt.repository.StockMovementRepository;
import ru.coursework.sklad_opt.repository.SupplierRepository;
import ru.coursework.sklad_opt.repository.CustomerRepository;
import ru.coursework.sklad_opt.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final OrderRepository orderRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(ProductRepository productRepository,
                                    CustomerRepository customerRepository,
                                    SupplierRepository supplierRepository,
                                    OrderRepository orderRepository,
                                    StockItemRepository stockItemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    BatchRepository batchRepository,
                                    UserRepository userRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Админ-панель");
        model.addAttribute("productsCount", productRepository.count());
        model.addAttribute("customersCount", customerRepository.count());
        model.addAttribute("suppliersCount", supplierRepository.count());
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("ordersDraft", orderRepository.countByStatus(OrderStatus.DRAFT));
        model.addAttribute("ordersConfirmed", orderRepository.countByStatus(OrderStatus.CONFIRMED));
        model.addAttribute("ordersShipped", orderRepository.countByStatus(OrderStatus.SHIPPED));
        model.addAttribute("lowStock", stockItemRepository.countLowStock());
        model.addAttribute("movementsCount", stockMovementRepository.count());
        model.addAttribute("batchesCount", batchRepository.count());
        return "admin/index";
    }
}
