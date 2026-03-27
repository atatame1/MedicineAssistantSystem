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
    private String name;

    /**
     * 法规名称英文
     */
    private String nameEnglish;

    /**
     * 法规编号/文号
     */
    private String regulationNumber;

    /**
     * 发布机构
     */
    private String issuingAuthority;

    /**
     * 发布机构类型 (NMPA-国家药监局 WHO-世卫组织 FDA-美国 FDA EMA-欧盟 EMA ICH-国际人用药品注册技术协调会)
     */
    private String issuingAuthorityType;

    /**
     * 法规类型 (REGULATION-法规 GUIDELINE-指导原则 NOTICE-通知 ANNOUNCEMENT-公告)
     */
    private String regulationType;

    /**
     * 法规分类 (注册管理 药学研究 非临床研究 临床研究 上市后研究)
     */
    private String category;

    /**
     * 法规亚类
     */
    private String subCategory;

    /**
     * 发布/生效日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 实施日期
     */
    private LocalDateTime implementationDate;

    /**
     * 废止日期
     */
    private LocalDateTime Date;

    /**
     * 是否现行有效 (true-有效 false-已废止)
     */
    private Boolean isValid;

    /**
     * 适用领域 (中药 化药 生物制品)
     */
    private String applicableField;

    /**
     * 法规原文 URL
     */
    private String originalUrl;

    /**
     * PDF 文件路径
     */
    private String pdfPath;

    /**
     * 法规摘要
     */
    private String summary;

    /**
     * 法规全文
     */
    private String fullText;

    /**
     * 核心条款 (JSON 数组)
     */
    private String keyProvisions;

    /**
     * 涉及药材
     */
    private String relatedHerbs;

    /**
     * 涉及方剂
     */
    private String relatedFormulas;

    /**
     * 涉及适应症 (JSON 数组)
     */
    private String relatedIndications;

    /**
     * 相关法规 (JSON 数组)
     */
    private String relatedRegulations;

    /**
     * 变更内容描述 (如为修订版)
     */
    private String changeDescription;

    /**
     * 对研发的影响评估
     */
    private String impactAssessment;

    /**
     * 执行要点
     */
    private String executionPoints;

    /**
     * 解读文章 URL(JSON 数组)
     */
    private String interpretationUrls;

    /**
     * 关键词标签 (JSON 数组)
     */
    private String tags;

    /**
     * 关注级别 (CRITICAL-关键 IMPORTANT-重要 NORMAL-普通)
     */
    private String importance;

    /**
     * 是否需要跟进
     */
    private Boolean needFollowUp;

    /**
     * 跟进状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成)
     */
    private String followUpStatus;

    /**
     * 跟进人
     */
    private String followUpBy;

    /**
     * 最后跟进时间
     */
    private LocalDateTime lastFollowUpTime;

    /**
     * 备注
     */
    private String remark;

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

}
