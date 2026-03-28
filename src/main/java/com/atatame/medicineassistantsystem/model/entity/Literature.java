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
    @TableField("title")
    private String title;

    /**
     * 文献标题英文
     */
    @TableField("title_english")
    private String titleEnglish;

    /**
     * 作者列表 (JSON 数组)
     */
    @TableField("authors")
    private String authors;

    /**
     * 发表年份
     */
    @TableField("publish_year")
    private Integer publishYear;

    /**
     * 发表月份
     */
    @TableField("publish_month")
    private Integer publishMonth;

    /**
     * 期刊名称
     */
    @TableField("journal_name")
    private String journalName;

    /**
     * ISSN 号
     */
    @TableField("issn")
    private String issn;

    /**
     * 卷号
     */
    @TableField("volume")
    private String volume;

    /**
     * 期号
     */
    @TableField("issue")
    private String issue;

    /**
     * 页码范围
     */
    @TableField("pages")
    private String pages;

    /**
     * DOI 号
     */
    @TableField("doi")
    private String doi;

    /**
     * PubMed ID
     */
    @TableField("pubmed_id")
    private String pubmedId;

    /**
     * WOS 号
     */
    @TableField("wos_number")
    private String wosNumber;

    /**
     * 文献摘要
     */
    @TableField("abstract_content")
    private String abstractContent;

    /**
     * 摘要英文
     */
    @TableField("abstract_english")
    private String abstractEnglish;

    /**
     * 关键词 (JSON 数组)
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 关键词英文 (JSON 数组)
     */
    @TableField("keywords_english")
    private String keywordsEnglish;

    /**
     * 文献类型 (ORIGINAL_RESEARCH-原创研究 REVIEW-综述 CASE_REPORT-病例报道 META_ANALYSIS-荟萃分析)
     */
    @TableField("literature_type")
    private String literatureType;

    /**
     * 研究类型 (IN_VITRO-体外 IN_VIVO-体内 CLINICAL-临床)
     */
    @TableField("research_type")
    private String researchType;

    /**
     * 研究对象 (JSON 数组)
     */
    @TableField("subjects")
    private String subjects;

    /**
     * 涉及药材 (JSON 数组)
     */
    @TableField("related_herbs")
    private String relatedHerbs;

    /**
     * 涉及方剂 (JSON 数组)
     */
    @TableField("related_formulas")
    private String relatedFormulas;

    /**
     * 涉及成分 (JSON 数组)
     */
    @TableField("related_components")
    private String relatedComponents;

    /**
     * 涉及靶点 (JSON 数组)
     */
    @TableField("related_targets")
    private String relatedTargets;

    /**
     * 涉及通路 (JSON 数组)
     */
    @TableField("related_pathways")
    private String relatedPathways;

    /**
     * 涉及疾病 (JSON 数组)
     */
    @TableField("related_diseases")
    private String relatedDiseases;

    /**
     * 研究方法
     */
    @TableField("methodology")
    private String methodology;

    /**
     * 主要发现
     */
    @TableField("main_findings")
    private String mainFindings;

    /**
     * 结论
     */
    @TableField("conclusion")
    private String conclusion;

    /**
     * 创新点
     */
    @TableField("innovations")
    private String innovations;

    /**
     * 技术路线
     */
    @TableField("technical_route")
    private String technicalRoute;

    /**
     * 被引次数
     */
    @TableField("citation_count")
    private Integer citationCount;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 全文链接 URL
     */
    @TableField("full_text_url")
    private String fullTextUrl;

    /**
     * 摘要链接 URL
     */
    @TableField("abstract_url")
    private String abstractUrl;

    /**
     * PDF 文件路径
     */
    @TableField("pdf_path")
    private String pdfPath;

    /**
     * 文件大小 (字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文献分类标签 (JSON 数组)
     */
    @TableField("tags")
    private String tags;

    /**
     * 重要程度
     */
    @TableField("importance")
    private Integer importance;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
