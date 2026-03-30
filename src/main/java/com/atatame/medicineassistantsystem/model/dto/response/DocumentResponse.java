package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String docType;
    private String docName;
    private String storageKey;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadTime;
    private String tags;
    private String summary;
}
