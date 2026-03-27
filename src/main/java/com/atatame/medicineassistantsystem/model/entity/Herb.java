package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 中药材实体
 * 中药材信息库
 */
@Data
@TableName("herb")
public class Herb {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 药材名称
     */
    @TableField("name")
    private String name;

    /**
     * 拼音
     */
    @TableField("pinyin")
    private String pinyin;

    /**
     * 药材别名 (逗号分隔)
     */
    @TableField("aliases")
    private String aliases;

    /**
     * 药材来源 (植物/动物/矿物来源)
     */
    @TableField("source")
    private String source;

    /**
     * 药用部位
     */
    @TableField("medicinal_part")
    private String medicinalPart;

    /**
     * 性味 (四性)
     */
    @TableField("nature")
    private String nature;

    /**
     * 性味 (五味)
     */
    @TableField("taste")
    private String taste;

    /**
     * 归经 (归哪些经脉)
     */
    @TableField("meridian")
    private String meridian;

    /**
     * 功效 (功效描述)
     */
    @TableField("effects")
    private String effects;

    /**
     * 主治病症
     */
    @TableField("indications")
    private String indications;

    /**
     * 用法用量
     */
    @TableField("`usage`")
    private String usage;

    /**
     * 炮制方法
     */
    @TableField("processing")
    private String processing;

    /**
     * 产地
     */
    @TableField("origin")
    private String origin;

    /**
     * 采集时间
     */
    @TableField("collection_time")
    private String collectionTime;

    /**
     * 贮藏方法
     */
    @TableField("storage")
    private String storage;

    /**
     * 毒性 (NONE-无毒 LOW-低毒 MEDIUM-中毒 HIGH-剧毒)
     */
    @TableField("toxicity")
    private String toxicity;

    /**
     * 禁忌
     */
    @TableField("contraindications")
    private String contraindications;

    /**
     * 相关方剂 (JSON 数组)
     */
    @TableField("related_formulas")
    private String relatedFormulas;

    /**
     * 活性成分 (JSON 数组)
     */
    @TableField("active_components")
    private String activeComponents;

    /**
     * 图片 URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 分类标签
     */
    @TableField("category")
    private String category;

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
