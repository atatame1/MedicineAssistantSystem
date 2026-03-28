package com.atatame.medicineassistantsystem.service.impl;

import cn.hutool.core.io.FileUtil;
import com.atatame.medicineassistantsystem.mapper.ProjectDocumentMapper;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.service.FileStorageService;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectDocumentServiceImpl extends ServiceImpl<ProjectDocumentMapper, ProjectDocument> implements IProjectDocumentService {

    private final FileStorageService fileStorageService;

    @Override
    public ProjectDocument upload(Long projectId, MultipartFile file, Long uploadUserId, String docType) throws IOException {
        String key = fileStorageService.store(projectId, file);
        String orig = file.getOriginalFilename();
        String ext = FileUtil.extName(orig);
        ProjectDocument d = new ProjectDocument();
        d.setProjectId(projectId);
        d.setStorageKey(key);
        d.setFilePath(key);
        d.setOriginalFilename(orig);
        d.setDocName(StringUtils.hasText(orig) ? orig : "upload");
        d.setFileSize(file.getSize());
        d.setFileType(file.getContentType());
        d.setUploadUserId(uploadUserId);
        d.setDocType(StringUtils.hasText(docType) ? docType : guessDocType(ext));
        d.setTags(d.getDocType() + "," + (ext == null ? "" : ext));
        save(d);
        return d;
    }

    @Override
    public List<ProjectDocument> searchKeyword(Long projectId, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return list(new LambdaQueryWrapper<ProjectDocument>()
                    .eq(ProjectDocument::getProjectId, projectId)
                    .orderByDesc(ProjectDocument::getUploadTime));
        }
        return baseMapper.searchKeyword(projectId, keyword.trim());
    }

    private static String guessDocType(String ext) {
        if (ext == null || ext.isEmpty()) {
            return "TECHNICAL";
        }
        return switch (ext.toLowerCase()) {
            case "pdf" -> "TECHNICAL";
            case "doc", "docx" -> "PROJECT_REPORT";
            case "xls", "xlsx", "csv" -> "RESEARCH";
            case "txt", "md" -> "RESEARCH";
            default -> "TECHNICAL";
        };
    }
}
