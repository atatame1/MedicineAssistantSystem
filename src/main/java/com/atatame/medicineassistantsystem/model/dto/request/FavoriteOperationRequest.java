package com.atatame.medicineassistantsystem.model.dto.request;

import lombok.Data;

@Data
public class FavoriteOperationRequest {
    private Long favoriteId;
    private String favoriteType;
}

