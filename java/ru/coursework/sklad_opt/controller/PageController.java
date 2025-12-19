package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.service.OrderService;
import ru.coursework.sklad_opt.service.ProductService;
import ru.coursework.sklad_opt.repository.BatchRepository;
import ru.coursework.sklad_opt.repository.CustomerRepository;
import ru.coursework.sklad_opt.repository.StockItemRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    private final ProductService productService;
    private final OrderService orderService;
    private final StockItemRepository stockItemRepository;
    private final BatchRepository batchRepository;
    private final CustomerRepository customerRepository;

    public PageController(ProductService productService,
                          OrderService orderService,
                          StockItemRepository stockItemRepository,
                          BatchRepository batchRepository,
                          CustomerRepository customerRepository) {
        this.productService = productService;
        this.orderService = orderService;
        this.stockItemRepository = stockItemRepository;
        this.batchRepository = batchRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.findAll();
        Product featured = products.stream().findFirst().orElse(null);
        BigDecimal featuredAvailable = BigDecimal.ZERO;
        if (featured != null) {
            Map<Long, BigDecimal> map = productService.getAvailablePerProduct(List.of(featured));
            featuredAvailable = map.getOrDefault(featured.getId(), BigDecimal.ZERO);
        }

        List<Product> topProducts = products.stream()
                .sorted(Comparator.comparing((Product p) -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO).reversed())
                .limit(3)
                .toList();

        model.addAttribute("featured", featured);
        model.addAttribute("featuredAvailable", featuredAvailable);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalOrders", orderService.findAll().size());
        model.addAttribute("lowStock", stockItemRepository.countLowStock());
        model.addAttribute("batchesCount", batchRepository.count());
        model.addAttribute("customersCount", customerRepository.count());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
