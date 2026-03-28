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
     * 前端原样提交的字符串，后端不解析
     */
    @TableField("preferences")
    private String preferences;

}
