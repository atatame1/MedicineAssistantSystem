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
    private Long projectId;

    /**
     * 决策类型 (FORMULATION-配方决策 EXPERIMENT-实验决策 RESOURCE-资源决策 RISK-风险控制)
     */
    private String decisionType;

    /**
     * 决策标题
     */
    private String title;

    /**
     * 决策内容
     */
    private String content;

    /**
     * AI 分析建议
     */
    private String aiRecommendation;

    /**
     * 专家结论
     */
    private String expertConclusion;

    /**
     * 证据链 (JSON 格式)
     */
    private String evidenceChain;

    /**
     * 置信度 (0-1)
     */
    private Double confidence;

    /**
     * 决策人
     */
    private Long projectorId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 关联文档 ID
     */
    private Long documentId;

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
