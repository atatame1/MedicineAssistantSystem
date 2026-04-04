package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_task")
public class UserTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("project_id")
    private Long projectId;

    @TableField("title")
    private String title;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 任务优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 0-发送任务 1-用户进行任务中 2-用户完成任务
     */
    @TableField("status")
    private Integer status;


    /**
     * 指派人 ID
     */
    @TableField("assignee_id")
    private Long assigneeId;



    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 任务完成时用户需填写的完成报告
     */
    @TableField("completion_feedback")
    private String completionFeedback;

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
