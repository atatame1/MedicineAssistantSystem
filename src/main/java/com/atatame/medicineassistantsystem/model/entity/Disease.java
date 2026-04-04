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
    @TableField("name")
    private String name;

    /**
     * 疾病别名
     */
    @TableField("aliases")
    private String aliases;

    /**
     * 中医病名
     */
    @TableField("tcm_name")
    private String tcmName;

    /**
     * 中医证候分类
     */
    @TableField("tcm_syndrome")
    private String tcmSyndrome;

    /**
     * 疾病分类 (内科 外科 妇科 儿科等)
     */
    @TableField("category")
    private String category;

    /**
     * 疾病亚类
     */
    @TableField("sub_category")
    private String subCategory;

    /**
     * ICD 编码
     */
    @TableField("icd_code")
    private String icdCode;

    /**
     * 现代医学名称
     */
    @TableField("modern_name")
    private String modernName;

    /**
     * 疾病概述
     */
    @TableField("overview")
    private String overview;

    /**
     * 病因病机
     */
    @TableField("etiology_pathogenesis")
    private String etiologyPathogenesis;

    /**
     * 临床表现
     */
    @TableField("clinical_manifestations")
    private String clinicalManifestations;

    /**
     * 诊断标准
     */
    @TableField("diagnostic_criteria")
    private String diagnosticCriteria;

    /**
     * 鉴别诊断
     */
    @TableField("differential_diagnosis")
    private String differentialDiagnosis;

    /**
     * 常见证型 (JSON 数组)
     */
    @TableField("common_syndromes")
    private String commonSyndromes;

    /**
     * 常用方剂 (JSON 数组)
     */
    @TableField("common_formulas")
    private String commonFormulas;

    /**
     * 常用药材 (JSON 数组)
     */
    @TableField("common_herbs")
    private String commonHerbs;

    /**
     * 关联靶点 (JSON 数组)
     */
    @TableField("related_targets")
    private String relatedTargets;

    /**
     * 关联通路 (JSON 数组)
     */
    @TableField("related_pathways")
    private String relatedPathways;

    /**
     * 流行病学数据
     */
    @TableField("epidemiology")
    private String epidemiology;

    /**
     * 预后评估
     */
    @TableField("prognosis")
    private String prognosis;

    /**
     * 预防建议
     */
    @TableField("prevention")
    private String prevention;

    /**
     * 疾病严重程度
     */
    @TableField("severity")
    private String severity;

    /**
     * 适用人群
     */
    @TableField("applicable_population")
    private String applicablePopulation;

    /**
     * 相关疾病 (JSON 数组)
     */
    @TableField("related_diseases")
    private String relatedDiseases;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
