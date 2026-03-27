package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目 DTO
 */
@Data
public class ProjectDTO {
    /**
     * 项目 ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 药材名称
     */
    private String herbName;

    /**
     * 方剂名称
     */
    private String formulaName;

    /**
     * 适应症
     */
    private String indication;

    /**
     * 项目阶段
     */
    private String phase;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 立项时间
     */
    private LocalDateTime startTime;

    /**
     * 计划完成时间
     */
    private LocalDateTime plannedEndTime;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 预算金额
     */
    private Double budget;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
