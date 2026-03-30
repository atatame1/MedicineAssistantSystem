package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project_document")
public class ProjectDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private Long projectId;

    @TableField("doc_type")
    private String docType;

    @TableField("doc_name")
    private String docName;

    @TableField("storage_key")
    private String storageKey;

    @TableField("original_filename")
    private String originalFilename;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField(value = "upload_time")
    private LocalDateTime uploadTime;

    @TableField("tags")
    private String tags;

    @TableField("summary")
    private String summary;

}
