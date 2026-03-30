package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

@Data
public class ProjectDocumentUploadRequest {
    private Long uploadUserId;
    private String docType;
    private String summary;
}
