package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被收藏实体Id
     */
    @TableField("favorite_id")
    private Long favoriteId;

    /**
     * 被收藏实体的类型
     */
    @TableField("favorite_type")
    private String favoriteType;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
