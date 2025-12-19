package ru.coursework.sklad_opt.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Path.of("uploads");

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Files.createDirectories(root);
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
            String filename = UUID.randomUUID() + ext;
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }
}
