package com.atatame.medicineassistantsystem.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${app.storage.root:${user.dir}/medicine-uploads}")
    private String root;

    public String store(Long projectId, MultipartFile file) throws IOException {
        Path base = Paths.get(root).normalize().toAbsolutePath();
        Path dir = base.resolve("projects").resolve(String.valueOf(projectId));
        Files.createDirectories(dir);
        String orig = file.getOriginalFilename();
        String ext = FileUtil.extName(orig);
        String name = UUID.randomUUID().toString().replace("-", "") + (ext == null || ext.isEmpty() ? "" : "." + ext);
        Path target = dir.resolve(name);
        file.transferTo(target.toFile());
        return "projects/" + projectId + "/" + name;
    }

    public Path resolvePath(String storageKey) {
        if (storageKey == null || storageKey.contains("..")) {
            throw new IllegalArgumentException("invalid storage key");
        }
        return Paths.get(root).normalize().toAbsolutePath().resolve(storageKey).normalize();
    }
}
