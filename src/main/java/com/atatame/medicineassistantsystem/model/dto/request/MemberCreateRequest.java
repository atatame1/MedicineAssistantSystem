package com.atatame.medicineassistantsystem.model.dto.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberCreateRequest {

    /**
     * 用户 ID
     */

    private Long userId;

    /**
     * 用户角色 (LEAD-负责人 MEMBER-普通成员 REVIEWER-评审员 OBSERVER-观察员)
     */
    private String role;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;
}
