package ru.coursework.sklad_opt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.repository.UserRepository;
import ru.coursework.sklad_opt.web.form.ProfileForm;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String profile(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setEmail(user.getEmail());
        model.addAttribute("form", form);
        model.addAttribute("pageTitle", "Профиль");
        return "auth/profile";
    }

    @PostMapping
    public String update(@ModelAttribute("form") ProfileForm form,
                         Authentication auth,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                model.addAttribute("form", form);
                model.addAttribute("error", "Пароли не совпадают");
                model.addAttribute("pageTitle", "Профиль");
                return "auth/profile";
            }
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Профиль обновлён");
        return "redirect:/profile";
    }
}
