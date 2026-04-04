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
    @TableField("project_name")
    private String projectName;

    /**
     * 药材名称
     */
    @TableField("herb_name")
    private String herbName;

    /**
     * 方剂名称
     */
    @TableField("formula_name")
    private String formulaName;

    /**
     * 适应症
     */
    @TableField("indication")
    private String indication;

    /**
     * 项目阶段 (EXPLORATION-探索期 VERIFICATION-验证期 OPTIMIZATION-优化期 DECLARATION-申报期)
     */
    @TableField("phase")
    private String phase;

    /**
     * 项目负责人
     */
    @TableField("projector_id")
    private Long projectorId;

    /**
     * 项目状态 (1-立项中 2-进行中 3-已完成 4-已取消)
     */
    @TableField("status")
    private Integer status;

    /**
     * 立项时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 计划完成时间
     */
    @TableField("planned_end_time")
    private LocalDateTime plannedEndTime;

    /**
     * 实际完成时间
     */
    @TableField("actual_end_time")
    private LocalDateTime actualEndTime;

    /**
     * 项目描述
     */
    @TableField("description")
    private String description;

    /**
     * 预算金额
     */
    @TableField("budget")
    private Double budget;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * AI 评估
     */
    @TableField("ai_assess")
    private String aiAssess;

    /**
     * AI生成报告
     */
    @TableField("ai_report")
    private String aiReport;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
