package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务 DTO
 */
@Data
public class TaskDTO {
    /**
     * 任务 ID
     */
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务优先级
     */
    private String priority;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 关联项目 ID
     */
    private Long projectId;

    /**
     * 关联项目名
     */
    private String projectName;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否逾期
     */
    private Boolean overdue;

    /**
     * 标签列表
     */
    private List<String> tags;
}
