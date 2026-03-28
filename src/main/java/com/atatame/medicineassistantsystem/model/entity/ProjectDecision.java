package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策记录实体
 * 记录项目重要决策及 AI 分析建议
 */
@Data
@TableName("project_decision")
public class ProjectDecision {

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
     * 前端决定
     * 决策类型 (FORMULATION-配方决策 EXPERIMENT-实验决策 RESOURCE-资源决策 RISK-风险控制)
     */
    @TableField("decision_type")
    private String decisionType;

    /**
     * 决策标题
     */
    @TableField("title")
    private String title;

    /**
     * 决策内容
     */
    @TableField("content")
    private String content;

    /**
     * AI 分析建议
     */
    @TableField("ai_recommendation")
    private String aiRecommendation;

    /**
     * 专家结论
     */
    @TableField("expert_conclusion")
    private String expertConclusion;

    /**
     * 决策人
     */
    @TableField("projector_id")
    private Long projectorId;

    /**
     * 版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
