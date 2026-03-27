package com.atatame.medicineassistantsystem.model.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class SettingsResponse {
    private Long userId;
    private NotificationSettings notificationSettings;
    private ThemeSettings themeSettings;
    private PrivacySettings privacySettings;
    private Map<String, Object> otherSettings;

    @Data
    public static class NotificationSettings {
        private Boolean emailEnabled;
        private Boolean inAppEnabled;
        private Boolean taskReminderEnabled;
        private Boolean projectNotificationEnabled;
    }

    @Data
    public static class ThemeSettings {
        private String mode;
        private String accentColor;
    }

    @Data
    public static class PrivacySettings {
        private String profileVisibility;
        private Boolean searchEnabled;
    }
}

