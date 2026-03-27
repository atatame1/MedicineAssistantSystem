package com.atatame.medicineassistantsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 法规实体
 * 新药研发相关法规和指引库
 */
@Data
@TableName("regulation")
public class Regulation {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 法规名称
     */
    @TableField("name")
    private String name;

    /**
     * 法规名称英文
     */
    @TableField("name_english")
    private String nameEnglish;

    /**
     * 法规编号/文号
     */
    @TableField("regulation_number")
    private String regulationNumber;

    /**
     * 发布机构
     */
    @TableField("issuing_authority")
    private String issuingAuthority;

    /**
     * 发布机构类型 (NMPA-国家药监局 WHO-世卫组织 FDA-美国 FDA EMA-欧盟 EMA ICH-国际人用药品注册技术协调会)
     */
    @TableField("issuing_authority_type")
    private String issuingAuthorityType;

    /**
     * 法规类型 (REGULATION-法规 GUIDELINE-指导原则 NOTICE-通知 ANNOUNCEMENT-公告)
     */
    @TableField("regulation_type")
    private String regulationType;

    /**
     * 法规分类 (注册管理 药学研究 非临床研究 临床研究 上市后研究)
     */
    @TableField("category")
    private String category;

    /**
     * 法规亚类
     */
    @TableField("sub_category")
    private String subCategory;

    /**
     * 发布/生效日期
     */
    @TableField("effective_date")
    private LocalDateTime effectiveDate;

    /**
     * 实施日期
     */
    @TableField("implementation_date")
    private LocalDateTime implementationDate;

    /**
     * 废止日期
     */
    @TableField("expiration_date")
    private LocalDateTime expirationDate;

    /**
     * 是否现行有效 (true-有效 false-已废止)
     */
    @TableField("is_valid")
    private Boolean isValid;

    /**
     * 适用领域 (中药 化药 生物制品)
     */
    @TableField("applicable_field")
    private String applicableField;

    /**
     * 法规原文 URL
     */
    @TableField("original_url")
    private String originalUrl;

    /**
     * PDF 文件路径
     */
    @TableField("pdf_path")
    private String pdfPath;

    /**
     * 法规摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 法规全文
     */
    @TableField("full_text")
    private String fullText;

    /**
     * 核心条款 (JSON 数组)
     */
    @TableField("key_provisions")
    private String keyProvisions;

    /**
     * 涉及药材
     */
    @TableField("related_herbs")
    private String relatedHerbs;

    /**
     * 涉及方剂
     */
    @TableField("related_formulas")
    private String relatedFormulas;

    /**
     * 涉及适应症 (JSON 数组)
     */
    @TableField("related_indications")
    private String relatedIndications;

    /**
     * 相关法规 (JSON 数组)
     */
    @TableField("related_regulations")
    private String relatedRegulations;

    /**
     * 变更内容描述 (如为修订版)
     */
    @TableField("change_description")
    private String changeDescription;

    /**
     * 对研发的影响评估
     */
    @TableField("impact_assessment")
    private String impactAssessment;

    /**
     * 执行要点
     */
    @TableField("execution_points")
    private String executionPoints;

    /**
     * 解读文章 URL(JSON 数组)
     */
    @TableField("interpretation_urls")
    private String interpretationUrls;

    /**
     * 关键词标签 (JSON 数组)
     */
    @TableField("tags")
    private String tags;

    /**
     * 关注级别 (CRITICAL-关键 IMPORTANT-重要 NORMAL-普通)
     */
    @TableField("importance")
    private String importance;

    /**
     * 是否需要跟进
     */
    @TableField("need_follow_up")
    private Boolean needFollowUp;

    /**
     * 跟进状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成)
     */
    @TableField("follow_up_status")
    private String followUpStatus;

    /**
     * 跟进人
     */
    @TableField("follow_up_by")
    private String followUpBy;

    /**
     * 最后跟进时间
     */
    @TableField("last_follow_up_time")
    private LocalDateTime lastFollowUpTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

}
