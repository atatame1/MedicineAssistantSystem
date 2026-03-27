package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 靶点通路实体
 * 靶点与信号通路库
 */
@Data
@TableName("target_pathway")
public class TargetPathway {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 靶点/通路名称
     */
    private String name;

    /**
     * 名称别名
     */
    private String aliases;

    /**
     * 类型 (TARGET-靶点 PATHWAY-通路)
     */
    private String type;

    /**
     * 靶点类型 (GENE-基因 PROTEIN-蛋白 RECEPTOR-受体 ENZYME-酶)
     */
    private String targetType;

    /**
     * 靶点基因符号
     */
    private String geneSymbol;

    /**
     * 靶点基因 ID
     */
    private String geneId;

    /**
     * 蛋白质名称
     */
    private String proteinName;

    /**
     * UniProt ID
     */
    private String uniprotId;

    /**
     * 信号通路名称
     */
    private String pathwayName;

    /**
     * KEGG 通路 ID
     */
    private String keggPathwayId;

    /**
     * 信号通路类别
     */
    private String pathwayCategory;

    /**
     * 靶点功能描述
     */
    private String function;

    /**
     * 生物学过程
     */
    private String biologicalProcess;

    /**
     * 细胞组分
     */
    private String cellularComponent;

    /**
     * 分子功能
     */
    private String molecularFunction;

    /**
     * 关联疾病 (JSON 数组)
     */
    private String relatedDiseases;

    /**
     * 关联药物/药材 (JSON 数组)
     */
    private String relatedDrugs;

    /**
     * 关联成分 (JSON 数组)
     */
    private String relatedComponents;

    /**
     * 上游调控因子
     */
    private String upstreamRegulators;

    /**
     * 下游效应分子
     */
    private String downstreamEffectors;

    /**
     * 激活/抑制效应 (ACTIVATE-激活 INHIBIT-抑制 REGULATE-调控)
     */
    private String effect;

    /**
     * 作用机制描述
     */
    private String mechanismDescription;

    /**
     * 证据等级 (HIGH-高 MEDIUM-中 LOW-低)
     */
    private String evidenceLevel;

    /**
     * 文献数量
     */
    private Integer literatureCount;

    /**
     * 置信度评分 (0-1)
     */
    private Double confidenceScore;

}
