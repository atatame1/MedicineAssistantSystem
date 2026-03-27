package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 收藏统计 DTO
 */
@Data
public class FavoriteStatisticsDTO {
    /**
     * 收藏总数
     */
    private Long totalCount;

    /**
     * 各类型收藏数量
     */
    private Map<String, Long> typeCountMap;
}
