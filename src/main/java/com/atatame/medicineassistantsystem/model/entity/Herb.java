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
    private String name;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 药材别名 (逗号分隔)
     */
    private String aliases;

    /**
     * 药材来源 (植物/动物/矿物来源)
     */
    private String source;

    /**
     * 药用部位
     */
    private String medicinalPart;

    /**
     * 性味 (四性)
     */
    private String nature;

    /**
     * 性味 (五味)
     */
    private String taste;

    /**
     * 归经 (归哪些经脉)
     */
    private String meridian;

    /**
     * 功效 (功效描述)
     */
    private String effects;

    /**
     * 主治病症
     */
    private String indications;

    /**
     * 用法用量
     */
    private String usage;

    /**
     * 炮制方法
     */
    private String processing;

    /**
     * 产地
     */
    private String origin;

    /**
     * 采集时间
     */
    private String collectionTime;

    /**
     * 贮藏方法
     */
    private String storage;

    /**
     * 毒性 (NONE-无毒 LOW-低毒 MEDIUM-中毒 HIGH-剧毒)
     */
    private String toxicity;

    /**
     * 禁忌
     */
    private String contraindications;

    /**
     * 相关方剂 (JSON 数组)
     */
    private String relatedFormulas;

    /**
     * 活性成分 (JSON 数组)
     */
    private String activeComponents;

    /**
     * 图片 URL
     */
    private String imageUrl;

    /**
     * 分类标签
     */
    private String category;

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
