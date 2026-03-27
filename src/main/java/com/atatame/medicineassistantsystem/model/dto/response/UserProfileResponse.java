package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String gender;
    private String avatarUrl;
    private String status;
    private UserStatisticsResponse statistics;
}

