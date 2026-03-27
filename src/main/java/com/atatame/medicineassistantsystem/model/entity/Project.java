package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目实体
 * 中药新药研发项目
 */
@Data
@TableName("project")
public class Project {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
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
     * 项目阶段 (EXPLORATION-探索期 VERIFICATION-验证期 OPTIMIZATION-优化期 DECLARATION-申报期)
     */
    private String phase;

    /**
     * 项目负责人
     */
    private Long projectorId;

    /**
     * 项目状态 (PLANNING-立项中 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消)
     */
    private String status;

    /**
     * 立项时间
     */
    private LocalDateTime startTime;

    /**
     * 计划完成时间
     */
    private LocalDateTime plannedEndTime;

    /**
     * 实际完成时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 预算金额
     */
    private Double budget;

    /**
     * 优先级 (HIGH-高 MEDIUM-中 LOW-低)
     */
    private String priority;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
