package ru.coursework.sklad_opt.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.sklad_opt.model.Role;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.repository.RoleRepository;
import ru.coursework.sklad_opt.repository.UserRepository;
import ru.coursework.sklad_opt.web.form.RegisterForm;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute("form") RegisterForm form,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (form.getPassword() == null || !form.getPassword().equals(form.getPasswordConfirm())) {
            model.addAttribute("error", "Пароли не совпадают");
            return "auth/register";
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            model.addAttribute("error", "Такой логин уже занят");
            return "auth/register";
        }
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Роль ROLE_USER не найдена"));

        User user = new User();
        user.setUsername(form.getUsername());
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.addRole(roleUser);
        user.setEnabled(true);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно. Войдите под новым аккаунтом.");
        return "redirect:/login";
    }
}
