package ru.coursework.sklad_opt.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.coursework.sklad_opt.model.Role;
import ru.coursework.sklad_opt.model.User;
import ru.coursework.sklad_opt.repository.RoleRepository;
import ru.coursework.sklad_opt.repository.UserRepository;

@Component
@Order(0)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFullName("Администратор");
            admin.setEmail("admin@example.com");
            admin.setEnabled(true);
            admin.addRole(adminRole);
            admin.addRole(userRole);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setFullName("Оптовый клиент");
            user.setEmail("user@example.com");
            user.setEnabled(true);
            user.addRole(userRole);
            userRepository.save(user);
        }
    }
}
