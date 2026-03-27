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
    private String name;

    /**
     * 成分别名
     */
    private String aliases;

    /**
     * 化学式
     */
    private String chemicalFormula;

    /**
     * 分子量
     */
    private Double molecularWeight;

    /**
     * CAS 号
     */
    private String casNumber;

    /**
     * 化学结构式 (SMILES 格式)
     */
    private String chemicalStructure;

    /**
     * 结构图片 URL
     */
    private String structureUrl;

    /**
     * 成分来源药材 (JSON 数组)
     */
    private String sourceHerbs;

    /**
     * 生物活性 (生物活性描述)
     */
    private String bioactivity;

    /**
     * 药理作用 (JSON 数组)
     */
    private String pharmacologicalEffects;

    /**
     * 潜在靶点 (JSON 数组)
     */
    private String potentialTargets;

    /**
     * 溶解性
     */
    private String solubility;

    /**
     * LogP 值
     */
    private Double logP;

    /**
     * 分子量 (Da)
     */
    private Double molecularWeightDa;

    /**
     * 氢键供体数
     */
    private Integer hBondDonorCount;

    /**
     * 氢键受体数
     */
    private Integer hBondAcceptorCount;

    /**
     * 旋转键数
     */
    private Integer rotatableBondCount;

    /**
     * 极性表面积
     */
    private Double polarSurfaceArea;

    /**
     * 类药性 (符合 Lipinski 规则的数量)
     */
    private Integer drugLikeScore;

    /**
     * 毒性评价
     */
    private String toxicity;

    /**
     * ADME 属性
     */
    private String admeProperties;

    /**
     * 来源数据库
     */
    private String sourceDatabase;


}
