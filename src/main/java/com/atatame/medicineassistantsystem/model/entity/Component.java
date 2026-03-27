package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成分实体
 * 化学成分库
 */
@Data
@TableName("component")
public class Component {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成分名称
     */
    @TableField("name")
    private String name;

    /**
     * 成分别名
     */
    @TableField("aliases")
    private String aliases;

    /**
     * 化学式
     */
    @TableField("chemical_formula")
    private String chemicalFormula;

    /**
     * 分子量
     */
    @TableField("molecular_weight")
    private Double molecularWeight;

    /**
     * CAS 号
     */
    @TableField("cas_number")
    private String casNumber;

    /**
     * 化学结构式 (SMILES 格式)
     */
    @TableField("chemical_structure")
    private String chemicalStructure;

    /**
     * 结构图片 URL
     */
    @TableField("structure_url")
    private String structureUrl;

    /**
     * 成分来源药材 (JSON 数组)
     */
    @TableField("source_herbs")
    private String sourceHerbs;

    /**
     * 生物活性 (生物活性描述)
     */
    @TableField("bioactivity")
    private String bioactivity;

    /**
     * 药理作用 (JSON 数组)
     */
    @TableField("pharmacological_effects")
    private String pharmacologicalEffects;

    /**
     * 潜在靶点 (JSON 数组)
     */
    @TableField("potential_targets")
    private String potentialTargets;

    /**
     * 溶解性
     */
    @TableField("solubility")
    private String solubility;

    /**
     * LogP 值
     */
    @TableField("logp")
    private Double logP;

    /**
     * 分子量 (Da)
     */
    @TableField("molecular_weight_da")
    private Double molecularWeightDa;

    /**
     * 氢键供体数
     */
    @TableField("h_bond_donor_count")
    private Integer hBondDonorCount;

    /**
     * 氢键受体数
     */
    @TableField("h_bond_acceptor_count")
    private Integer hBondAcceptorCount;

    /**
     * 旋转键数
     */
    @TableField("rotatable_bond_count")
    private Integer rotatableBondCount;

    /**
     * 极性表面积
     */
    @TableField("polar_surface_area")
    private Double polarSurfaceArea;

    /**
     * 类药性 (符合 Lipinski 规则的数量)
     */
    @TableField("drug_like_score")
    private Integer drugLikeScore;

    /**
     * 毒性评价
     */
    @TableField("toxicity")
    private String toxicity;

    /**
     * ADME 属性
     */
    @TableField("adme_properties")
    private String admeProperties;

    /**
     * 来源数据库
     */
    @TableField("source_database")
    private String sourceDatabase;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
