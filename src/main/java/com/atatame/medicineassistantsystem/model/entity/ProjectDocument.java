package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档管理实体
 * 存储项目相关文档
 */
@Data
@TableName("project_document")
public class ProjectDocument {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目 ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 文档类型 (PROJECT_REPORT-项目报告 RESEARCH-研究文档 EXPERIMENT-实验记录 TECHNICAL-技术资料)
     */
    @TableField("doc_type")
    private String docType;

    /**
     * 文档名称
     */
    @TableField("doc_name")
    private String docName;

    @TableField("storage_key")
    private String storageKey;

    @TableField("original_filename")
    private String originalFilename;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小 (字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private LocalDateTime uploadTime;

    /**
     * 分类标签 (逗号分隔)
     */
    @TableField("tags")
    private String tags;

    /**
     * 摘要/描述
     */
    @TableField("summary")
    private String summary;

}
