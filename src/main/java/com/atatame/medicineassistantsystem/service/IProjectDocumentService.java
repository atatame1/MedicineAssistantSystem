package com.atatame.medicineassistantsystem.service;

import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProjectDocumentService extends IService<ProjectDocument> {

    ProjectDocument upload(Long projectId, MultipartFile file, Long uploadUserId, String docType) throws IOException;

    List<ProjectDocument> searchKeyword(Long projectId, String keyword);
}
