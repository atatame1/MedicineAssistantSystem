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
    private String name;

    /**
     * 专利名称英文
     */
    private String nameEnglish;

    /**
     * 专利号
     */
    private String patentNumber;

    /**
     * 国际专利分类号 (IPC)
     */
    private String ipcClassification;

    /**
     * PCT 号
     */
    private String pctNumber;

    /**
     * 申请号
     */
    private String applicationNumber;

    /**
     * 公开号
     */
    private String publicationNumber;

    /**
     * 专利申请日期
     */
    private LocalDateTime applicationDate;

    /**
     * 专利授权日期
     */
    private LocalDateTime grantDate;

    /**
     * 专利类型 (INVENTION-发明实用新型实用新型)
     */
    private String patentType;

    /**
     * 专利状态 (PENDING-待审 GRANTED-已授权 REJECTED-被驳回 INVALID-无效)
     */
    private String status;

    /**
     * 所属国家/地区
     */
    private String countryRegion;

    /**
     * 申请人/专利权人
     */
    private String applicant;

    /**
     * 申请人类型 (INDIVIDUAL-个人 ORGANIZATION-机构 UNIVERSITY-高校)
     */
    private String applicantType;

    /**
     * 发明人/设计人 (JSON 数组)
     */
    private String inventors;

    /**
     * 代理机构
     */
    private String agency;

    /**
     * 代理人
     */
    private String attorney;

    /**
     * 专利摘要
     */
    private String abstractContent;

    /**
     * 摘要英文
     */
    private String abstractEnglish;

    /**
     * 权利要求书 (JSON 数组)
     */
    private String claims;

    /**
     * 技术领域
     */
    private String technicalField;

    /**
     * 背景技术
     */
    private String backgroundTechnology;

    /**
     * 发明内容
     */
    private String inventionContent;

    /**
     * 附图说明
     */
    private String figureDescription;

    /**
     * 具体实施方式
     */
    private String implementationDetails;

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
     * 涉及适应症 (JSON 数组)
     */
    private String relatedIndications;

    /**
     * 涉及靶点 (JSON 数组)
     */
    private String relatedTargets;

    /**
     * 涉及通路 (JSON 数组)
     */
    private String relatedPathways;

    /**
     * 创新点
     */
    private String innovations;

    /**
     * 技术优势
     */
    private String technicalAdvantages;

    /**
     * 潜在侵权风险 (HIGH-高风险 MEDIUM-中风险 LOW-低风险 NONE-无风险)
     */
    private String infringementRisk;

    /**
     * 侵权风险描述
     */
    private String infringementDescription;

    /**
     * 专利价值评分 (0-10)
     */
    private Double patentValueScore;

    /**
     * 被引次数
     */
    private Integer citationCount;

    /**
     * 同族专利数
     */
    private Integer familySize;

    /**
     * 同族专利 (JSON 数组)
     */
    private String familyPatents;

    /**
     * 相似专利 (JSON 数组)
     */
    private String similarPatents;

    /**
     * 专利全文 PDF 路径
     */
    private String fullTextPath;

    /**
     * 专利说明书图片 URL
     */
    private String figuresUrl;

    /**
     * 是否已分析
     */
    private Boolean isAnalyzed;

    /**
     * 分析人
     */
    private String analyzedBy;

    /**
     * 分析时间
     */
    private LocalDateTime analyzedTime;

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
