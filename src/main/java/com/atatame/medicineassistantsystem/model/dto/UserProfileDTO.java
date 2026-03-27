package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

/**
 * 用户资料 DTO
 */
@Data
public class UserProfileDTO {
    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 性别
     */
    private String gender;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 账户状态
     */
    private String status;

    /**
     * 统计信息
     */
    private UserStatisticsDTO statistics;
}
