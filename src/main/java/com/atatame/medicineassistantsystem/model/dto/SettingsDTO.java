package com.atatame.medicineassistantsystem.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 用户设置 DTO
 */
@Data
public class SettingsDTO {
    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 通知设置
     */
    private NotificationSettings notificationSettings;

    /**
     * 主题设置
     */
    private ThemeSettings themeSettings;

    /**
     * 隐私设置
     */
    private PrivacySettings privacySettings;

    /**
     * 其他设置
     */
    private Map<String, Object> otherSettings;

    /**
     * 通知设置
     */
    @Data
    public static class NotificationSettings {
        /**
         * 邮件通知
         */
        private Boolean emailEnabled;

        /**
         * 站内通知
         */
        private Boolean inAppEnabled;

        /**
         * 任务提醒
         */
        private Boolean taskReminderEnabled;

        /**
         * 项目通知
         */
        private Boolean projectNotificationEnabled;
    }

    /**
     * 主题设置
     */
    @Data
    public static class ThemeSettings {
        /**
         * 主题模式 (LIGHT/DARK/AUTO)
         */
        private String mode;

        /**
         * 主题色
         */
        private String accentColor;
    }

    /**
     * 隐私设置
     */
    @Data
    public static class PrivacySettings {
        /**
         * 资料可见性 (PUBLIC/PRIVATE)
         */
        private String profileVisibility;

        /**
         * 允许搜索
         */
        private Boolean searchEnabled;
    }
}
