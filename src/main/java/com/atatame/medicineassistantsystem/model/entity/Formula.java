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
    @TableField("name")
    private String name;

    /**
     * 方剂别名
     */
    @TableField("aliases")
    private String aliases;

    /**
     * 方剂出处 (典籍名称)
     */
    @TableField("source")
    private String source;

    /**
     * 方剂年代
     */
    @TableField("era")
    private String era;

    /**
     * 方剂分类 (解表剂 泻下剂 和解剂 清热剂等)
     */
    @TableField("category")
    private String category;

    /**
     * 组方结构
     */
    @TableField("composition")
    private String composition;

    /**
     * 君药 (JSON 数组，包含药材名称和用量)
     */
    @TableField("jun_yao")
    private String junYao;

    /**
     * 臣药 (JSON 数组，包含药材名称和用量)
     */
    @TableField("chen_yao")
    private String chenYao;

    /**
     * 佐药 (JSON 数组，包含药材名称和用量)
     */
    @TableField("zuo_yao")
    private String zuoYao;

    /**
     * 使药 (JSON 数组，包含药材名称和用量)
     */
    @TableField("shi_yao")
    private String shiYao;

    /**
     * 总剂量
     */
    @TableField("total_dose")
    private String totalDose;

    /**
     * 功效
     */
    @TableField("effects")
    private String effects;

    /**
     * 主治病症
     */
    @TableField("indications")
    private String indications;

    /**
     * 主治证候
     */
    @TableField("syndromes")
    private String syndromes;

    /**
     * 临床表现
     */
    @TableField("clinical_manifestations")
    private String clinicalManifestations;

    /**
     * 现代应用
     */
    @TableField("modern_applications")
    private String modernApplications;

    /**
     * 用法用量
     */
    @TableField("`usage`")
    private String usage;

    /**
     * 配伍禁忌
     */
    @TableField("contraindications")
    private String contraindications;

    /**
     * 加减变化
     */
    @TableField("modifications")
    private String modifications;

    /**
     * 现代研究 (JSON 数组)
     */
    @TableField("modern_research")
    private String modernResearch;

    /**
     * 临床验证案例数
     */
    @TableField("clinical_case_count")
    private Integer clinicalCaseCount;

    /**
     * 有效性评分 (0-10)
     */
    @TableField("efficacy_score")
    private Double efficacyScore;

    /**
     * 方剂图片 URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 相关药材 (JSON 数组)
     */
    @TableField("related_herbs")
    private String relatedHerbs;

    /**
     * 相关方剂 (JSON 数组)
     */
    @TableField("related_formulas")
    private String relatedFormulas;

    /**
     * 相似方剂推荐 (JSON 数组)
     */
    @TableField("similar_formulas")
    private String similarFormulas;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
