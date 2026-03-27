package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

/**
 * 用户统计数据 DTO
 */
@Data
public class UserStatisticsDTO {
    /**
     * 任务总数
     */
    private Long totalTasks;

    /**
     * 待处理任务数
     */
    private Long pendingTasks;

    /**
     * 进行中任务数
     */
    private Long inProgressTasks;

    /**
     * 已完成任务数
     */
    private Long completedTasks;

    /**
     * 参与项目总数
     */
    private Long totalProjects;

    /**
     * 进行中的项目数
     */
    private Long activeProjects;

    /**
     * 收藏总数
     */
    private Long totalFavorites;

    /**
     * 文档总数
     */
    private Long totalDocuments;
}
