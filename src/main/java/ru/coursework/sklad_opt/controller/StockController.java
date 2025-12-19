package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.service.StockItemService;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockItemService stockItemService;

    public StockController(StockItemService stockItemService) {
        this.stockItemService = stockItemService;
    }

    @GetMapping
    public String stock(Model model) {
        model.addAttribute("pageTitle", "Остатки");
        model.addAttribute("items", stockItemService.findAll());
        return "stock/index";
    }
}
