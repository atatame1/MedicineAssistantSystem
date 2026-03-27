package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteResponse {
    private Long id;
    private Long favoriteId;
    private String favoriteType;
    private String entityName;
    private LocalDateTime collectTime;
}

