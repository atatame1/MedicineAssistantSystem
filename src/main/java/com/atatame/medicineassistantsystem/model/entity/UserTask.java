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
    private Long userId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型 (RESEARCH-研究任务 APPROVAL-审批任务 EXPERIMENT-实验任务 REPORT-报告任务 LEARNING-学习任务)
     */
    private String taskType;

    /**
     * 任务优先级 (HIGH-高 MEDIUM-中 LOW-低)
     */
    private String priority;

    /**
     * 任务状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消 OVERDUE-已逾期)
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
     * 关联决策 ID
     */
    private Long decisionId;

    /**
     * 关联文档 ID
     */
    private Long documentId;

    /**
     * 关联 AI 分析 ID
     */
    private Long aiAnalysisId;

    /**
     * 负责人 ID
     */
    private Long assigneeId;

    /**
     * 负责人姓名
     */
    private String assigneeName;

    /**
     * 创建人 ID
     */
    private Long createBy;

    /**
     * 创建人姓名
     */
    private String createByName;

    /**
     * 任务来源 (MANUAL-手动创建 AUTO-AI 自动生成 SYSTEM-系统任务)
     */
    private String source;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 实际完成时间
     */
    private LocalDateTime actualCompletionTime;

    /**
     * 任务分类标签 (JSON 数组)
     */
    private String tags;

    /**
     * 任务提醒设置 (JSON 对象)
     */
    private String reminderSettings;

    /**
     * 任务评分 (0-5)
     */
    private Integer completionScore;

    /**
     * 任务完成反馈
     */
    private String completionFeedback;

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
