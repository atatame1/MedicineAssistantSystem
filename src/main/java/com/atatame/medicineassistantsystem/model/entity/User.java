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
    private String username;

    /**
     * 密码
     */
    private String password;

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
     * 性别 (MALE-男 FEMALE-女 OTHER-其他)
     */
    private String gender;

    /**
     * 头像 URL
     */
    private String avatarUrl;


    /**
     * 账户状态 (ACTIVE-正常 DISABLED-禁用 DELETED-已删除)
     */
    private String status;



}
