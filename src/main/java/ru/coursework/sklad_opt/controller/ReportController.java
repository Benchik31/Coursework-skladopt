package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.coursework.sklad_opt.service.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Отчёты");
        model.addAttribute("lowStock", reportService.lowStock());
        model.addAttribute("topProducts", reportService.topProducts());
        model.addAttribute("turnover", reportService.monthlyTurnoverWithChange());
        return "reports/index";
    }
}
