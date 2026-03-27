package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档 DTO
 */
@Data
public class DocumentDTO {
    /**
     * 文档 ID
     */
    private Long id;

    /**
     * 项目 ID
     */
    private Long projectId;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 文档类型
     */
    private String docType;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 分类标签
     */
    private String tags;

    /**
     * 摘要/描述
     */
    private String summary;
}
