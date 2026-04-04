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
    @TableField("name")
    private String name;

    /**
     * 名称别名
     */
    @TableField("aliases")
    private String aliases;

    /**
     * 类型 (TARGET-靶点 PATHWAY-通路)
     */
    @TableField("type")
    private String type;

    /**
     * 靶点类型 (GENE-基因 PROTEIN-蛋白 RECEPTOR-受体 ENZYME-酶)
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 靶点基因符号
     */
    @TableField("gene_symbol")
    private String geneSymbol;

    /**
     * 靶点基因 ID
     */
    @TableField("gene_id")
    private String geneId;

    /**
     * 蛋白质名称
     */
    @TableField("protein_name")
    private String proteinName;

    /**
     * UniProt ID
     */
    @TableField("uniprot_id")
    private String uniprotId;

    /**
     * 信号通路名称
     */
    @TableField("pathway_name")
    private String pathwayName;

    /**
     * KEGG 通路 ID
     */
    @TableField("kegg_pathway_id")
    private String keggPathwayId;

    /**
     * 信号通路类别
     */
    @TableField("pathway_category")
    private String pathwayCategory;

    /**
     * 靶点功能描述
     */
    @TableField("`function`")
    private String function;

    /**
     * 生物学过程
     */
    @TableField("biological_process")
    private String biologicalProcess;

    /**
     * 细胞组分
     */
    @TableField("cellular_component")
    private String cellularComponent;

    /**
     * 分子功能
     */
    @TableField("molecular_function")
    private String molecularFunction;

    /**
     * 关联疾病 (JSON 数组)
     */
    @TableField("related_diseases")
    private String relatedDiseases;

    /**
     * 关联药物/药材 (JSON 数组)
     */
    @TableField("related_drugs")
    private String relatedDrugs;

    /**
     * 关联成分 (JSON 数组)
     */
    @TableField("related_components")
    private String relatedComponents;

    /**
     * 上游调控因子
     */
    @TableField("upstream_regulators")
    private String upstreamRegulators;

    /**
     * 下游效应分子
     */
    @TableField("downstream_effectors")
    private String downstreamEffectors;

    /**
     * 激活/抑制效应 (ACTIVATE-激活 INHIBIT-抑制 REGULATE-调控)
     */
    @TableField("effect")
    private String effect;

    /**
     * 作用机制描述
     */
    @TableField("mechanism_description")
    private String mechanismDescription;

    /**
     * 证据等级 (HIGH-高 MEDIUM-中 LOW-低)
     */
    @TableField("evidence_level")
    private String evidenceLevel;

    /**
     * 文献数量
     */
    @TableField("literature_count")
    private Integer literatureCount;

    /**
     * 置信度评分 (0-1)
     */
    @TableField("confidence_score")
    private Double confidenceScore;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
