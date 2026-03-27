package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏项 DTO
 */
@Data
public class FavoriteDTO {
    /**
     * 收藏 ID
     */
    private Long id;

    /**
     * 被收藏实体 ID
     */
    private Long favoriteId;

    /**
     * 被收藏实体的类型
     */
    private String favoriteType;

    /**
     * 收藏实体名称
     */
    private String entityName;

    /**
     * 收藏时间
     */
    private LocalDateTime collectTime;
}
