package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文献实体
 * 文献库
 */
@Data
@TableName("literature")
public class  Literature {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文献标题
     */
    private String title;

    /**
     * 文献标题英文
     */
    private String titleEnglish;

    /**
     * 作者列表 (JSON 数组)
     */
    private String authors;

    /**
     * 发表年份
     */
    private Integer publishYear;

    /**
     * 发表月份
     */
    private Integer publishMonth;

    /**
     * 期刊名称
     */
    private String journalName;

    /**
     * ISSN 号
     */
    private String issn;

    /**
     * 卷号
     */
    private String volume;

    /**
     * 期号
     */
    private String issue;

    /**
     * 页码范围
     */
    private String pages;

    /**
     * DOI 号
     */
    private String doi;

    /**
     * PubMed ID
     */
    private String pubmedId;

    /**
     * WOS 号
     */
    private String wosNumber;

    /**
     * 文献摘要
     */
    private String abstractContent;

    /**
     * 摘要英文
     */
    private String abstractEnglish;

    /**
     * 关键词 (JSON 数组)
     */
    private String keywords;

    /**
     * 关键词英文 (JSON 数组)
     */
    private String keywordsEnglish;

    /**
     * 文献类型 (ORIGINAL_RESEARCH-原创研究 REVIEW-综述 CASE_REPORT-病例报道 META_ANALYSIS-荟萃分析)
     */
    private String literatureType;

    /**
     * 研究类型 (IN_VITRO-体外 IN_VIVO-体内 CLINICAL-临床)
     */
    private String researchType;

    /**
     * 研究对象 (JSON 数组)
     */
    private String subjects;

    /**
     * 涉及药材 (JSON 数组)
     */
    private String relatedHerbs;

    /**
     * 涉及方剂 (JSON 数组)
     */
    private String relatedFormulas;

    /**
     * 涉及成分 (JSON 数组)
     */
    private String relatedComponents;

    /**
     * 涉及靶点 (JSON 数组)
     */
    private String relatedTargets;

    /**
     * 涉及通路 (JSON 数组)
     */
    private String relatedPathways;

    /**
     * 涉及疾病 (JSON 数组)
     */
    private String relatedDiseases;

    /**
     * 研究方法
     */
    private String methodology;

    /**
     * 主要发现
     */
    private String mainFindings;

    /**
     * 结论
     */
    private String conclusion;

    /**
     * 创新点
     */
    private String innovations;

    /**
     * 技术路线
     */
    private String technicalRoute;

    /**
     * 被引次数
     */
    private Integer citationCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 全文链接 URL
     */
    private String fullTextUrl;

    /**
     * 摘要链接 URL
     */
    private String abstractUrl;

    /**
     * PDF 文件路径
     */
    private String pdfPath;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 文献分类标签 (JSON 数组)
     */
    private String tags;

    /**
     * 重要程度 (CRITICAL-关键 IMPORTANT-重要 NORMAL-普通)
     */
    private String importance;

}
