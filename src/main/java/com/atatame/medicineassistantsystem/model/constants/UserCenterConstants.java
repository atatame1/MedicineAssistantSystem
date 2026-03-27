package com.atatame.medicineassistantsystem.model.constants;

/**
 * 个人中心相关常量
 */
public class UserCenterConstants {

    /**
     * 个人中心相关状态常量
     */
    public static class UserCenterStatus {
        /**
         * 正常状态
         */
        public static final String ACTIVE = "ACTIVE";

        /**
         * 禁用状态
         */
        public static final String DISABLED = "DISABLED";

        /**
         * 已删除状态
         */
        public static final String DELETED = "DELETED";
    }

    /**
     * 个人中心实体类型常量
     */
    public static class EntityType {
        /**
         * 任务类型
         */
        public static final String TASK = "TASK";

        /**
         * 项目类型
         */
        public static final String PROJECT = "PROJECT";

        /**
         * 文档类型
         */
        public static final String DOCUMENT = "DOCUMENT";

        /**
         * 文献类型
         */
        public static final String LITERATURE = "LITERATURE";

        /**
         * 专利类型
         */
        public static final String PATENT = "PATENT";

        /**
         * 方剂类型
         */
        public static final String FORMULA = "FORMULA";

        /**
         * 中药材类型
         */
        public static final String HERB = "HERB";

        /**
         * 成分类型
         */
        public static final String COMPONENT = "COMPONENT";
    }

    /**
     * 个人中心排序字段常量
     */
    public static class SortField {
        /**
         * 创建时间
         */
        public static final String CREATE_TIME = "createTime";

        /**
         * 更新时间
         */
        public static final String UPDATE_TIME = "updateTime";

        /**
         * 截止时间
         */
        public static final String DEADLINE = "deadline";

        /**
         * 优先级
         */
        public static final String PRIORITY = "priority";
    }

    /**
     * 个人中心排序方向常量
     */
    public static class SortDirection {
        /**
         * 升序
         */
        public static final String ASC = "ASC";

        /**
         * 降序
         */
        public static final String DESC = "DESC";
    }
}
