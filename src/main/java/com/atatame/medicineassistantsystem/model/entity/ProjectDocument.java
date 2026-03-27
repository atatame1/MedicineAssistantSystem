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
    private Long projectId;

    /**
     * 文档类型 (PROJECT_REPORT-项目报告 RESEARCH-研究文档 EXPERIMENT-实验记录 TECHNICAL-技术资料)
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
     * 上传人
     */
    private String uploadBy;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    /**
     * 分类标签 (逗号分隔)
     */
    private String tags;

    /**
     * 摘要/描述
     */
    private String summary;

}
