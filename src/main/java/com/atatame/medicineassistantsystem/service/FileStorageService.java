package com.atatame.medicineassistantsystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    String store(Long projectId, MultipartFile file) throws IOException;

    Path resolvePath(String storageKey);
}
