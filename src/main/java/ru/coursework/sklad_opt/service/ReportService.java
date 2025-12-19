package ru.coursework.sklad_opt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.sklad_opt.model.Order;
import ru.coursework.sklad_opt.model.OrderLine;
import ru.coursework.sklad_opt.model.Product;
import ru.coursework.sklad_opt.model.StockItem;
import ru.coursework.sklad_opt.model.enums.OrderStatus;
import ru.coursework.sklad_opt.repository.OrderRepository;
import ru.coursework.sklad_opt.repository.ProductRepository;
import ru.coursework.sklad_opt.repository.StockItemRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportService {

    private final StockItemRepository stockItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReportService(StockItemRepository stockItemRepository,
                         ProductRepository productRepository,
                         OrderRepository orderRepository) {
        this.stockItemRepository = stockItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<StockItem> lowStock() {
        List<StockItem> all = stockItemRepository.findAll();
        List<StockItem> low = new ArrayList<>();
        for (StockItem item : all) {
            if (item.getProduct().getMinStock() != null &&
                    item.getAvailable().compareTo(item.getProduct().getMinStock()) < 0) {
                low.add(item);
            }
        }
        low.sort(Comparator.comparing(si -> si.getProduct().getName()));
        return low;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> topProducts() {
        Map<Long, BigDecimal> turnoverByProduct = new HashMap<>();
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
                continue;
            }
            for (OrderLine line : order.getLines()) {
                if (line.getProduct() == null || line.getPrice() == null || line.getQuantity() == null) {
                    continue;
                }
                BigDecimal lineTotal = line.getPrice().multiply(line.getQuantity());
                turnoverByProduct.merge(line.getProduct().getId(), lineTotal, BigDecimal::add);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Product p : productRepository.findAll()) {
            BigDecimal turnover = turnoverByProduct.getOrDefault(p.getId(), BigDecimal.ZERO);
            Map<String, Object> row = new HashMap<>();
            row.put("name", p.getName());
            row.put("turnover", turnover);
            result.add(row);
        }
        result.sort((a, b) -> ((BigDecimal) b.get("turnover")).compareTo((BigDecimal) a.get("turnover")));
        if (result.size() > 5) {
            return result.subList(0, 5);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> monthlyTurnover() {
        Map<YearMonth, BigDecimal> byMonth = new TreeMap<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() != OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
                continue;
            }
            if (order.getCreatedAt() == null || order.getLines() == null) {
                continue;
            }
            BigDecimal sum = order.getLines().stream()
                    .filter(l -> l.getPrice() != null && l.getQuantity() != null)
                    .map(l -> l.getPrice().multiply(l.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            YearMonth ym = YearMonth.from(order.getCreatedAt());
            byMonth.merge(ym, sum, BigDecimal::add);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<YearMonth, BigDecimal> entry : byMonth.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("month", entry.getKey().toString());
            row.put("amount", entry.getValue());
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> monthlyTurnoverWithChange() {
        List<Map<String, Object>> base = monthlyTurnover();
        BigDecimal prev = null;
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : base) {
            BigDecimal amount = (BigDecimal) row.get("amount");
            Map<String, Object> copy = new HashMap<>(row);
            if (prev != null && prev.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diff = amount.subtract(prev);
                BigDecimal percent = diff.multiply(BigDecimal.valueOf(100))
                        .divide(prev, 2, RoundingMode.HALF_UP);
                copy.put("mom", percent);
            } else {
                copy.put("mom", null);
            }
            prev = amount;
            result.add(copy);
        }
        return result;
    }
}
