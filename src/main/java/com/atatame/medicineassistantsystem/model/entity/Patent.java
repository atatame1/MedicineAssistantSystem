package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专利实体
 * 专利库
 */
@Data
@TableName("patent")
public class Patent {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 专利名称
     */
    @TableField("name")
    private String name;

    /**
     * 专利名称英文
     */
    @TableField("name_english")
    private String nameEnglish;

    /**
     * 专利号
     */
    @TableField("patent_number")
    private String patentNumber;

    /**
     * 国际专利分类号 (IPC)
     */
    @TableField("ipc_classification")
    private String ipcClassification;

    /**
     * PCT 号
     */
    @TableField("pct_number")
    private String pctNumber;

    /**
     * 申请号
     */
    @TableField("application_number")
    private String applicationNumber;

    /**
     * 公开号
     */
    @TableField("publication_number")
    private String publicationNumber;

    /**
     * 专利申请日期
     */
    @TableField("application_date")
    private LocalDateTime applicationDate;

    /**
     * 专利授权日期
     */
    @TableField("grant_date")
    private LocalDateTime grantDate;

    /**
     * 专利类型 (INVENTION-发明实用新型实用新型)
     */
    @TableField("patent_type")
    private String patentType;

    /**
     * 专利状态 (PENDING-待审 GRANTED-已授权 REJECTED-被驳回 INVALID-无效)
     */
    @TableField("status")
    private String status;

    /**
     * 所属国家/地区
     */
    @TableField("country_region")
    private String countryRegion;

    /**
     * 申请人/专利权人
     */
    @TableField("applicant")
    private String applicant;

    /**
     * 申请人类型 (INDIVIDUAL-个人 ORGANIZATION-机构 UNIVERSITY-高校)
     */
    @TableField("applicant_type")
    private String applicantType;

    /**
     * 发明人/设计人 (JSON 数组)
     */
    @TableField("inventors")
    private String inventors;

    /**
     * 代理机构
     */
    @TableField("agency")
    private String agency;

    /**
     * 代理人
     */
    @TableField("attorney")
    private String attorney;

    /**
     * 专利摘要
     */
    @TableField("abstract_content")
    private String abstractContent;

    /**
     * 摘要英文
     */
    @TableField("abstract_english")
    private String abstractEnglish;

    /**
     * 权利要求书 (JSON 数组)
     */
    @TableField("claims")
    private String claims;

    /**
     * 技术领域
     */
    @TableField("technical_field")
    private String technicalField;

    /**
     * 背景技术
     */
    @TableField("background_technology")
    private String backgroundTechnology;

    /**
     * 发明内容
     */
    @TableField("invention_content")
    private String inventionContent;

    /**
     * 附图说明
     */
    @TableField("figure_description")
    private String figureDescription;

    /**
     * 具体实施方式
     */
    @TableField("implementation_details")
    private String implementationDetails;

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
     * 涉及适应症 (JSON 数组)
     */
    @TableField("related_indications")
    private String relatedIndications;

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
     * 创新点
     */
    @TableField("innovations")
    private String innovations;

    /**
     * 技术优势
     */
    @TableField("technical_advantages")
    private String technicalAdvantages;

    /**
     * 潜在侵权风险 (HIGH-高风险 MEDIUM-中风险 LOW-低风险 NONE-无风险)
     */
    @TableField("infringement_risk")
    private String infringementRisk;

    /**
     * 侵权风险描述
     */
    @TableField("infringement_description")
    private String infringementDescription;

    /**
     * 专利价值评分 (0-10)
     */
    @TableField("patent_value_score")
    private Double patentValueScore;

    /**
     * 被引次数
     */
    @TableField("citation_count")
    private Integer citationCount;

    /**
     * 同族专利数
     */
    @TableField("family_size")
    private Integer familySize;

    /**
     * 同族专利 (JSON 数组)
     */
    @TableField("family_patents")
    private String familyPatents;

    /**
     * 相似专利 (JSON 数组)
     */
    @TableField("similar_patents")
    private String similarPatents;

    /**
     * 专利全文 PDF 路径
     */
    @TableField("full_text_path")
    private String fullTextPath;

    /**
     * 专利说明书图片 URL
     */
    @TableField("figures_url")
    private String figuresUrl;

    /**
     * 是否已分析
     */
    @TableField("is_analyzed")
    private Boolean isAnalyzed;

    /**
     * 分析人
     */
    @TableField("analyzed_by")
    private String analyzedBy;

    /**
     * 分析时间
     */
    @TableField("analyzed_time")
    private LocalDateTime analyzedTime;

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
