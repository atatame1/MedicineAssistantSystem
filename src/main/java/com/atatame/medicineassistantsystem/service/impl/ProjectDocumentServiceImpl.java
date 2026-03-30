package com.atatame.medicineassistantsystem.service.impl;

import cn.hutool.core.io.FileUtil;
import com.atatame.medicineassistantsystem.mapper.ProjectDocumentMapper;
import com.atatame.medicineassistantsystem.model.dto.request.ProjectDocumentUploadRequest;
import com.atatame.medicineassistantsystem.model.entity.ProjectDocument;
import com.atatame.medicineassistantsystem.service.IProjectDocumentService;
import com.atatame.medicineassistantsystem.utils.FileStorageUtil;
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

    private final FileStorageUtil fileStorageUtil;

    @Override
    public ProjectDocument upload(Long projectId, MultipartFile file, ProjectDocumentUploadRequest metadata) throws IOException {
        if (metadata == null) {
            metadata = new ProjectDocumentUploadRequest();
        }
        String key = fileStorageUtil.store(projectId, file);
        String orig = file.getOriginalFilename();
        String ext = FileUtil.extName(orig);
        ProjectDocument d = new ProjectDocument();
        d.setProjectId(projectId);
        d.setStorageKey(key);
        d.setOriginalFilename(orig);
        d.setDocName(StringUtils.hasText(orig) ? orig : "upload");
        d.setFileSize(file.getSize());
        String ct = file.getContentType();
        if (ct != null && ct.length() > 50) {
            ct = ct.substring(0, 50);
        }
        d.setFileType(ct);
        d.setUploadUserId(metadata.getUploadUserId());
        d.setDocType(StringUtils.hasText(metadata.getDocType()) ? metadata.getDocType() : guessDocType(ext));
        d.setSummary(metadata.getSummary());
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
