package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class FavoriteStatisticsResponse {
    private Long totalCount;
    private Map<String, Long> typeCountMap;
}

