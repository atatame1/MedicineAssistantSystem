package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户任务实体
 * 用户的待处理任务
 */
@Data
@TableName("user_task")
public class UserTask {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 任务标题
     */
    @TableField("title")
    private String title;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 任务类型 (RESEARCH-研究任务 APPROVAL-审批任务 EXPERIMENT-实验任务 REPORT-报告任务 LEARNING-学习任务)
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务优先级 (HIGH-高 MEDIUM-中 LOW-低)
     */
    @TableField("priority")
    private String priority;

    /**
     * 任务状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消 OVERDUE-已逾期)
     */
    @TableField("status")
    private String status;

    /**
     * 关联项目 ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 关联项目名
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 关联决策 ID
     */
    @TableField("decision_id")
    private Long decisionId;

    /**
     * 关联文档 ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 关联 AI 分析 ID
     */
    @TableField("ai_analysis_id")
    private Long aiAnalysisId;

    /**
     * 负责人 ID
     */
    @TableField("assignee_id")
    private Long assigneeId;

    /**
     * 负责人姓名
     */
    @TableField("assignee_name")
    private String assigneeName;

    /**
     * 创建人 ID
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建人姓名
     */
    @TableField("create_by_name")
    private String createByName;

    /**
     * 任务来源 (MANUAL-手动创建 AUTO-AI 自动生成 SYSTEM-系统任务)
     */
    @TableField("source")
    private String source;

    /**
     * 截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 实际完成时间
     */
    @TableField("actual_completion_time")
    private LocalDateTime actualCompletionTime;

    /**
     * 任务分类标签 (JSON 数组)
     */
    @TableField("tags")
    private String tags;

    /**
     * 任务提醒设置 (JSON 对象)
     */
    @TableField("reminder_settings")
    private String reminderSettings;

    /**
     * 任务评分 (0-5)
     */
    @TableField("completion_score")
    private Integer completionScore;

    /**
     * 任务完成反馈
     */
    @TableField("completion_feedback")
    private String completionFeedback;

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
