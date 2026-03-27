package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

@Data
public class FavoriteCreateRequest {
    private Long favoriteId;
    private String favoriteType;
}

