package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目用户关联实体
 * 项目与用户的多对多关系
 */
@Data
@TableName("project_member")
public class ProjectMember {

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
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户角色 (LEAD-负责人 MEMBER-普通成员 REVIEWER-评审员 OBSERVER-观察员)
     */
    private String role;

}
