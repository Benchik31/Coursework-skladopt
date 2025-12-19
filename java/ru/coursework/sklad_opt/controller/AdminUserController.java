package ru.coursework.sklad_opt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.sklad_opt.model.Role;
import ru.coursework.sklad_opt.service.AdminUserService;
import ru.coursework.sklad_opt.repository.RoleRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final RoleRepository roleRepository;

    public AdminUserController(AdminUserService adminUserService, RoleRepository roleRepository) {
        this.adminUserService = adminUserService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", adminUserService.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Пользователи");
        return "admin/users";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminUserService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Статус пользователя изменён");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @ModelAttribute("role") String role,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.changeRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Роль изменена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось изменить роль");
        }
        return "redirect:/admin/users";
    }
}
