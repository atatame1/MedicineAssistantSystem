package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 方剂实体
 * 经典方剂库
 */
@Data
@TableName("formula")
public class Formula {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 方剂名称
     */
    private String name;

    /**
     * 方剂别名
     */
    private String aliases;

    /**
     * 方剂出处 (典籍名称)
     */
    private String source;

    /**
     * 方剂年代
     */
    private String era;

    /**
     * 方剂分类 (解表剂 泻下剂 和解剂 清热剂等)
     */
    private String category;

    /**
     * 组方结构
     */
    private String composition;

    /**
     * 君药 (JSON 数组，包含药材名称和用量)
     */
    private String junYao;

    /**
     * 臣药 (JSON 数组，包含药材名称和用量)
     */
    private String chenYao;

    /**
     * 佐药 (JSON 数组，包含药材名称和用量)
     */
    private String zuoYao;

    /**
     * 使药 (JSON 数组，包含药材名称和用量)
     */
    private String shiYao;

    /**
     * 总剂量
     */
    private String totalDose;

    /**
     * 功效
     */
    private String effects;

    /**
     * 主治病症
     */
    private String indications;

    /**
     * 主治证候
     */
    private String syndromes;

    /**
     * 临床表现
     */
    private String clinicalManifestations;

    /**
     * 现代应用
     */
    private String modernApplications;

    /**
     * 用法用量
     */
    private String usage;

    /**
     * 配伍禁忌
     */
    private String contraindications;

    /**
     * 加减变化
     */
    private String modifications;

    /**
     * 现代研究 (JSON 数组)
     */
    private String modernResearch;

    /**
     * 临床验证案例数
     */
    private Integer clinicalCaseCount;

    /**
     * 有效性评分 (0-10)
     */
    private Double efficacyScore;

    /**
     * 方剂图片 URL
     */
    private String imageUrl;

    /**
     * 相关药材 (JSON 数组)
     */
    private String relatedHerbs;

    /**
     * 相关方剂 (JSON 数组)
     */
    private String relatedFormulas;

    /**
     * 相似方剂推荐 (JSON 数组)
     */
    private String similarFormulas;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
