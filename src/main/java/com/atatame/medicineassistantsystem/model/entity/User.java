package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 系统用户账户
 */
@Data
@TableName("user")
public class User {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录账号）
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 性别 (MALE-男 FEMALE-女 OTHER-其他)
     */
    @TableField("gender")
    private String gender;

    /**
     * 头像 URL
     */
    @TableField("avatar_url")
    private String avatarUrl;


    /**
     * 账户状态 (0-正常 1-禁用 2-已删除)
     */
    @TableField("status")
    private Integer status;

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
