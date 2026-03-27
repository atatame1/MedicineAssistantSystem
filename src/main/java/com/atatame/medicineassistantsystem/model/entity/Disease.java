package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 疾病实体
 * 疾病分类库
 */
@Data
@TableName("disease")
public class Disease {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 疾病名称
     */
    private String name;

    /**
     * 疾病别名
     */
    private String aliases;

    /**
     * 中医病名
     */
    private String tcmName;

    /**
     * 中医证候分类
     */
    private String tcmSyndrome;

    /**
     * 疾病分类 (内科 外科 妇科 儿科等)
     */
    private String category;

    /**
     * 疾病亚类
     */
    private String subCategory;

    /**
     * ICD 编码
     */
    private String icdCode;

    /**
     * 现代医学名称
     */
    private String modernName;

    /**
     * 疾病概述
     */
    private String overview;

    /**
     * 病因病机
     */
    private String etiologyPathogenesis;

    /**
     * 临床表现
     */
    private String clinicalManifestations;

    /**
     * 诊断标准
     */
    private String diagnosticCriteria;

    /**
     * 鉴别诊断
     */
    private String differentialDiagnosis;

    /**
     * 常见证型 (JSON 数组)
     */
    private String commonSyndromes;

    /**
     * 常用方剂 (JSON 数组)
     */
    private String commonFormulas;

    /**
     * 常用药材 (JSON 数组)
     */
    private String commonHerbs;

    /**
     * 关联靶点 (JSON 数组)
     */
    private String relatedTargets;

    /**
     * 关联通路 (JSON 数组)
     */
    private String relatedPathways;

    /**
     * 流行病学数据
     */
    private String epidemiology;

    /**
     * 预后评估
     */
    private String prognosis;

    /**
     * 预防建议
     */
    private String prevention;

    /**
     * 疾病严重程度 (MILD-轻度 MODERATE-中度 SEVERE-重度)
     */
    private String severity;

    /**
     * 适用人群
     */
    private String applicablePopulation;

    /**
     * 相关疾病 (JSON 数组)
     */
    private String relatedDiseases;

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

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
}
