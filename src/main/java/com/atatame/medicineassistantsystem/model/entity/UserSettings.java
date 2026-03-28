package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_settings")
public class UserSettings {

    /**
     * 主键 userId,与userid相同
     */
    @TableId
    private Long userId;

    /**
     * 用户设置，json存储，直接返回给前端处理
     */
    @TableField("preferences")
    private String preferences;

}
