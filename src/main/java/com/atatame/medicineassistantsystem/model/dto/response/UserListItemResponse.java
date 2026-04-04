package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class UserListItemResponse {
    private Long id;
    private String username;
    private String nickname;
}
