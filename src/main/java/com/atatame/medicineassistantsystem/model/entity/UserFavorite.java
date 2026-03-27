package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Long userId;

    /**
     * 被收藏实体Id
     */
    private Long favoriteId;

    /**
     * 被收藏实体的类型
     */
    private String favoriteType;
}
