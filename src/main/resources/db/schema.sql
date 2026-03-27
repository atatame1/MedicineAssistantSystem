-- 中药新药研发辅助系统数据库建表语句
-- 数据库：MySQL 8.0+

-- 删除已存在的表（按依赖关系逆序）
DROP TABLE IF EXISTS `user_task`;
DROP TABLE IF EXISTS `user_favorite`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `project_member`;
DROP TABLE IF EXISTS `project_document`;
DROP TABLE IF EXISTS `project_decision`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `regulation`;
DROP TABLE IF EXISTS `target_pathway`;
DROP TABLE IF EXISTS `patent`;
DROP TABLE IF EXISTS `literature`;
DROP TABLE IF EXISTS `formula`;
DROP TABLE IF EXISTS `disease`;
DROP TABLE IF EXISTS `component`;
DROP TABLE IF EXISTS `herb`;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(100) COMMENT '用户昵称',
  `email` VARCHAR(100) COMMENT '用户邮箱',
  `phone` VARCHAR(20) COMMENT '手机号码',
  `gender` VARCHAR(10) COMMENT '性别 (MALE-男 FEMALE-女 OTHER-其他)',
  `avatar_url` VARCHAR(500) COMMENT '头像 URL',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态 (ACTIVE-正常 DISABLED-禁用 DELETED-已删除)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户账户';

-- ============================================
-- 用户收藏表
-- ============================================
CREATE TABLE `user_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `favorite_id` BIGINT NOT NULL COMMENT '被收藏实体 ID',
  `favorite_type` VARCHAR(50) NOT NULL COMMENT '被收藏实体的类型 (HERB/FORMULA/DISEASE/COMPONENT/LITERATURE/PATENT)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_favorite` (`user_id`, `favorite_id`, `favorite_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_favorite_id` (`favorite_id`),
  KEY `idx_favorite_type` (`favorite_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ============================================
-- 中药材表
-- ============================================
CREATE TABLE `herb` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(100) NOT NULL COMMENT '药材名称',
  `pinyin` VARCHAR(100) COMMENT '拼音',
  `aliases` VARCHAR(500) COMMENT '药材别名',
  `source` TEXT COMMENT '药材来源 (植物/动物/矿物来源)',
  `medicinal_part` VARCHAR(200) COMMENT '药用部位',
  `nature` VARCHAR(100) COMMENT '性味 (四性：寒热温凉)',
  `taste` VARCHAR(100) COMMENT '性味 (五味：酸苦甘辛咸)',
  `meridian` VARCHAR(200) COMMENT '归经 (归哪些经脉)',
  `effects` TEXT COMMENT '功效 (功效描述)',
  `indications` TEXT COMMENT '主治病症',
  `usage` VARCHAR(500) COMMENT '用法用量',
  `processing` TEXT COMMENT '炮制方法',
  `origin` VARCHAR(200) COMMENT '产地',
  `collection_time` VARCHAR(100) COMMENT '采集时间',
  `storage` VARCHAR(200) COMMENT '贮藏方法',
  `toxicity` VARCHAR(20) COMMENT '毒性 (NONE-无毒 LOW-低毒 MEDIUM-中毒 HIGH-剧毒)',
  `contraindications` TEXT COMMENT '禁忌',
  `related_formulas` TEXT COMMENT '相关方剂 (JSON 数组)',
  `active_components` TEXT COMMENT '活性成分 (JSON 数组)',
  `image_url` VARCHAR(500) COMMENT '图片 URL',
  `category` VARCHAR(100) COMMENT '分类标签',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_herb_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_nature` (`nature`),
  KEY `idx_toxicity` (`toxicity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='中药材信息库';

-- ============================================
-- 成分表 (化学成分库)
-- ============================================
CREATE TABLE `component` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '成分名称',
  `aliases` VARCHAR(500) COMMENT '成分别名',
  `chemical_formula` VARCHAR(100) COMMENT '化学式',
  `molecular_weight` DECIMAL(10,2) COMMENT '分子量',
  `cas_number` VARCHAR(50) COMMENT 'CAS 号',
  `chemical_structure` TEXT COMMENT '化学结构式 (SMILES 格式)',
  `structure_url` VARCHAR(500) COMMENT '结构图片 URL',
  `source_herbs` TEXT COMMENT '成分来源药材 (JSON 数组)',
  `bioactivity` TEXT COMMENT '生物活性',
  `pharmacological_effects` TEXT COMMENT '药理作用 (JSON 数组)',
  `potential_targets` TEXT COMMENT '潜在靶点 (JSON 数组)',
  `solubility` VARCHAR(200) COMMENT '溶解性',
  `logp` DECIMAL(8,4) COMMENT 'LogP 值',
  `molecular_weight_da` DECIMAL(10,2) COMMENT '分子量 (Da)',
  `h_bond_donor_count` INT COMMENT '氢键供体数',
  `h_bond_acceptor_count` INT COMMENT '氢键受体数',
  `rotatable_bond_count` INT COMMENT '旋转键数',
  `polar_surface_area` DECIMAL(8,2) COMMENT '极性表面积',
  `drug_like_score` INT COMMENT '类药性 (符合 Lipinski 规则的数量)',
  `toxicity` VARCHAR(100) COMMENT '毒性评价',
  `adme_properties` TEXT COMMENT 'ADME 属性',
  `source_database` VARCHAR(100) COMMENT '来源数据库',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_component_name` (`name`),
  UNIQUE KEY `uk_cas_number` (`cas_number`),
  KEY `idx_source_database` (`source_database`),
  KEY `idx_bioactivity` (`bioactivity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='化学成分库';

-- ============================================
-- 疾病表
-- ============================================
CREATE TABLE `disease` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '疾病名称',
  `aliases` VARCHAR(500) COMMENT '疾病别名',
  `tcm_name` VARCHAR(200) COMMENT '中医病名',
  `tcm_syndrome` VARCHAR(200) COMMENT '中医证候分类',
  `category` VARCHAR(100) COMMENT '疾病分类 (内科 外科 妇科 儿科等)',
  `sub_category` VARCHAR(100) COMMENT '疾病亚类',
  `icd_code` VARCHAR(50) COMMENT 'ICD 编码',
  `modern_name` VARCHAR(200) COMMENT '现代医学名称',
  `overview` TEXT COMMENT '疾病概述',
  `etiology_pathogenesis` TEXT COMMENT '病因病机',
  `clinical_manifestations` TEXT COMMENT '临床表现',
  `diagnostic_criteria` TEXT COMMENT '诊断标准',
  `differential_diagnosis` TEXT COMMENT '鉴别诊断',
  `common_syndromes` TEXT COMMENT '常见证型 (JSON 数组)',
  `common_formulas` TEXT COMMENT '常用方剂 (JSON 数组)',
  `common_herbs` TEXT COMMENT '常用药材 (JSON 数组)',
  `related_targets` TEXT COMMENT '关联靶点 (JSON 数组)',
  `related_pathways` TEXT COMMENT '关联通路 (JSON 数组)',
  `epidemiology` TEXT COMMENT '流行病学数据',
  `prognosis` TEXT COMMENT '预后评估',
  `prevention` TEXT COMMENT '预防建议',
  `severity` VARCHAR(20) COMMENT '疾病严重程度 (MILD-轻度 MODERATE-中度 SEVERE-重度)',
  `applicable_population` TEXT COMMENT '适用人群',
  `related_diseases` TEXT COMMENT '相关疾病 (JSON 数组)',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(100) COMMENT '创建人',
  `update_by` VARCHAR(100) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_disease_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_severity` (`severity`),
  KEY `idx_icd_code` (`icd_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='疾病分类库';

-- ============================================
-- 方剂表
-- ============================================
CREATE TABLE `formula` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '方剂名称',
  `aliases` VARCHAR(500) COMMENT '方剂别名',
  `source` VARCHAR(300) COMMENT '方剂出处 (典籍名称)',
  `era` VARCHAR(100) COMMENT '方剂年代',
  `category` VARCHAR(100) COMMENT '方剂分类 (解表剂 泻下剂 和解剂 清热剂等)',
  `composition` TEXT COMMENT '组方结构',
  `jun_yao` TEXT COMMENT '君药 (JSON 数组，包含药材名称和用量)',
  `chen_yao` TEXT COMMENT '臣药 (JSON 数组，包含药材名称和用量)',
  `zuo_yao` TEXT COMMENT '佐药 (JSON 数组，包含药材名称和用量)',
  `shi_yao` TEXT COMMENT '使药 (JSON 数组，包含药材名称和用量)',
  `total_dose` VARCHAR(200) COMMENT '总剂量',
  `effects` TEXT COMMENT '功效',
  `indications` TEXT COMMENT '主治病症',
  `syndromes` TEXT COMMENT '主治证候',
  `clinical_manifestations` TEXT COMMENT '临床表现',
  `modern_applications` TEXT COMMENT '现代应用',
  `usage` VARCHAR(500) COMMENT '用法用量',
  `contraindications` TEXT COMMENT '配伍禁忌',
  `modifications` TEXT COMMENT '加减变化',
  `modern_research` TEXT COMMENT '现代研究 (JSON 数组)',
  `clinical_case_count` INT COMMENT '临床验证案例数',
  `efficacy_score` DECIMAL(3,1) COMMENT '有效性评分 (0-10)',
  `image_url` VARCHAR(500) COMMENT '方剂图片 URL',
  `related_herbs` TEXT COMMENT '相关药材 (JSON 数组)',
  `related_formulas` TEXT COMMENT '相关方剂 (JSON 数组)',
  `similar_formulas` TEXT COMMENT '相似方剂推荐 (JSON 数组)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_formula_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_era` (`era`),
  KEY `idx_efficacy_score` (`efficacy_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经典方剂库';

-- ============================================
-- 文献表
-- ============================================
CREATE TABLE `literature` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `title` VARCHAR(500) NOT NULL COMMENT '文献标题',
  `title_english` VARCHAR(500) COMMENT '文献标题英文',
  `authors` TEXT COMMENT '作者列表 (JSON 数组)',
  `publish_year` INT COMMENT '发表年份',
  `publish_month` INT COMMENT '发表月份',
  `journal_name` VARCHAR(300) COMMENT '期刊名称',
  `issn` VARCHAR(50) COMMENT 'ISSN 号',
  `volume` VARCHAR(50) COMMENT '卷号',
  `issue` VARCHAR(50) COMMENT '期号',
  `pages` VARCHAR(100) COMMENT '页码范围',
  `doi` VARCHAR(200) COMMENT 'DOI 号',
  `pubmed_id` VARCHAR(50) COMMENT 'PubMed ID',
  `wos_number` VARCHAR(100) COMMENT 'WOS 号',
  `abstract_content` TEXT COMMENT '文献摘要',
  `abstract_english` TEXT COMMENT '摘要英文',
  `keywords` TEXT COMMENT '关键词 (JSON 数组)',
  `keywords_english` TEXT COMMENT '关键词英文 (JSON 数组)',
  `literature_type` VARCHAR(50) COMMENT '文献类型 (ORIGINAL_RESEARCH-原创研究 REVIEW-综述 CASE_REPORT-病例报道 META_ANALYSIS-荟萃分析)',
  `research_type` VARCHAR(50) COMMENT '研究类型 (IN_VITRO-体外 IN_VIVO-体内 CLINICAL-临床)',
  `subjects` TEXT COMMENT '研究对象 (JSON 数组)',
  `related_herbs` TEXT COMMENT '涉及药材 (JSON 数组)',
  `related_formulas` TEXT COMMENT '涉及方剂 (JSON 数组)',
  `related_components` TEXT COMMENT '涉及成分 (JSON 数组)',
  `related_targets` TEXT COMMENT '涉及靶点 (JSON 数组)',
  `related_pathways` TEXT COMMENT '涉及通路 (JSON 数组)',
  `related_diseases` TEXT COMMENT '涉及疾病 (JSON 数组)',
  `methodology` TEXT COMMENT '研究方法',
  `main_findings` TEXT COMMENT '主要发现',
  `conclusion` TEXT COMMENT '结论',
  `innovations` TEXT COMMENT '创新点',
  `technical_route` TEXT COMMENT '技术路线',
  `citation_count` INT DEFAULT 0 COMMENT '被引次数',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `full_text_url` VARCHAR(500) COMMENT '全文链接 URL',
  `abstract_url` VARCHAR(500) COMMENT '摘要链接 URL',
  `pdf_path` VARCHAR(500) COMMENT 'PDF 文件路径',
  `file_size` BIGINT COMMENT '文件大小 (字节)',
  `tags` TEXT COMMENT '文献分类标签 (JSON 数组)',
  `importance` VARCHAR(20) COMMENT '重要程度 (CRITICAL-关键 IMPORTANT-重要 NORMAL-普通)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_literature_doi` (`doi`),
  KEY `idx_publish_year` (`publish_year`),
  KEY `idx_journal_name` (`journal_name`),
  KEY `idx_literature_type` (`literature_type`),
  KEY `idx_importance` (`importance`),
  KEY `idx_pubmed_id` (`pubmed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文献库';

-- ============================================
-- 专利表
-- ============================================
CREATE TABLE `patent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(300) NOT NULL COMMENT '专利名称',
  `name_english` VARCHAR(300) COMMENT '专利名称英文',
  `patent_number` VARCHAR(100) COMMENT '专利号',
  `ipc_classification` VARCHAR(200) COMMENT '国际专利分类号 (IPC)',
  `pct_number` VARCHAR(100) COMMENT 'PCT 号',
  `application_number` VARCHAR(100) COMMENT '申请号',
  `publication_number` VARCHAR(100) COMMENT '公开号',
  `application_date` DATETIME COMMENT '专利申请日期',
  `grant_date` DATETIME COMMENT '专利授权日期',
  `patent_type` VARCHAR(50) COMMENT '专利类型 (INVENTION-发明 UTILITY-实用新型 DESIGN-外观设计)',
  `status` VARCHAR(20) COMMENT '专利状态 (PENDING-待审 GRANTED-已授权 REJECTED-被驳回 INVALID-无效)',
  `country_region` VARCHAR(100) COMMENT '所属国家/地区',
  `applicant` VARCHAR(300) COMMENT '申请人/专利权人',
  `applicant_type` VARCHAR(50) COMMENT '申请人类型 (INDIVIDUAL-个人 ORGANIZATION-机构 UNIVERSITY-高校)',
  `inventors` TEXT COMMENT '发明人/设计人 (JSON 数组)',
  `agency` VARCHAR(300) COMMENT '代理机构',
  `attorney` VARCHAR(200) COMMENT '代理人',
  `abstract_content` TEXT COMMENT '专利摘要',
  `abstract_english` TEXT COMMENT '摘要英文',
  `claims` TEXT COMMENT '权利要求书 (JSON 数组)',
  `technical_field` TEXT COMMENT '技术领域',
  `background_technology` TEXT COMMENT '背景技术',
  `invention_content` TEXT COMMENT '发明内容',
  `figure_description` TEXT COMMENT '附图说明',
  `implementation_details` TEXT COMMENT '具体实施方式',
  `related_herbs` TEXT COMMENT '涉及药材 (JSON 数组)',
  `related_formulas` TEXT COMMENT '涉及方剂 (JSON 数组)',
  `related_components` TEXT COMMENT '涉及成分 (JSON 数组)',
  `related_indications` TEXT COMMENT '涉及适应症 (JSON 数组)',
  `related_targets` TEXT COMMENT '涉及靶点 (JSON 数组)',
  `related_pathways` TEXT COMMENT '涉及通路 (JSON 数组)',
  `innovations` TEXT COMMENT '创新点',
  `technical_advantages` TEXT COMMENT '技术优势',
  `infringement_risk` VARCHAR(20) COMMENT '潜在侵权风险 (HIGH-高风险 MEDIUM-中风险 LOW-低风险 NONE-无风险)',
  `infringement_description` TEXT COMMENT '侵权风险描述',
  `patent_value_score` DECIMAL(3,1) COMMENT '专利价值评分 (0-10)',
  `citation_count` INT DEFAULT 0 COMMENT '被引次数',
  `family_size` INT DEFAULT 0 COMMENT '同族专利数',
  `family_patents` TEXT COMMENT '同族专利 (JSON 数组)',
  `similar_patents` TEXT COMMENT '相似专利 (JSON 数组)',
  `full_text_path` VARCHAR(500) COMMENT '专利全文 PDF 路径',
  `figures_url` VARCHAR(500) COMMENT '专利说明书图片 URL',
  `is_analyzed` TINYINT(1) DEFAULT 0 COMMENT '是否已分析',
  `analyzed_by` VARCHAR(100) COMMENT '分析人',
  `analyzed_time` DATETIME COMMENT '分析时间',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(100) COMMENT '创建人',
  `update_by` VARCHAR(100) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_patent_number` (`patent_number`),
  KEY `idx_patent_type` (`patent_type`),
  KEY `idx_status` (`status`),
  KEY `idx_infringement_risk` (`infringement_risk`),
  KEY `idx_applicant` (`applicant`),
  KEY `idx_application_date` (`application_date`),
  KEY `idx_grant_date` (`grant_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专利库';

-- ============================================
-- 靶点通路表
-- ============================================
CREATE TABLE `target_pathway` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(300) NOT NULL COMMENT '靶点/通路名称',
  `aliases` VARCHAR(500) COMMENT '名称别名',
  `type` VARCHAR(20) NOT NULL COMMENT '类型 (TARGET-靶点 PATHWAY-通路)',
  `target_type` VARCHAR(50) COMMENT '靶点类型 (GENE-基因 PROTEIN-蛋白 RECEPTOR-受体 ENZYME-酶)',
  `gene_symbol` VARCHAR(100) COMMENT '靶点基因符号',
  `gene_id` VARCHAR(100) COMMENT '靶点基因 ID',
  `protein_name` VARCHAR(300) COMMENT '蛋白质名称',
  `uniprot_id` VARCHAR(100) COMMENT 'UniProt ID',
  `pathway_name` VARCHAR(300) COMMENT '信号通路名称',
  `kegg_pathway_id` VARCHAR(50) COMMENT 'KEGG 通路 ID',
  `pathway_category` VARCHAR(100) COMMENT '信号通路类别',
  `function` TEXT COMMENT '靶点功能描述',
  `biological_process` TEXT COMMENT '生物学过程',
  `cellular_component` TEXT COMMENT '细胞组分',
  `molecular_function` TEXT COMMENT '分子功能',
  `related_diseases` TEXT COMMENT '关联疾病 (JSON 数组)',
  `related_drugs` TEXT COMMENT '关联药物/药材 (JSON 数组)',
  `related_components` TEXT COMMENT '关联成分 (JSON 数组)',
  `upstream_regulators` TEXT COMMENT '上游调控因子',
  `downstream_effectors` TEXT COMMENT '下游效应分子',
  `effect` VARCHAR(20) COMMENT '激活/抑制效应 (ACTIVATE-激活 INHIBIT-抑制 REGULATE-调控)',
  `mechanism_description` TEXT COMMENT '作用机制描述',
  `evidence_level` VARCHAR(20) COMMENT '证据等级 (HIGH-高 MEDIUM-中 LOW-低)',
  `literature_count` INT DEFAULT 0 COMMENT '文献数量',
  `confidence_score` DECIMAL(3,2) COMMENT '置信度评分 (0-1)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_target_pathway_name` (`name`, `type`),
  KEY `idx_type` (`type`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_kegg_pathway_id` (`kegg_pathway_id`),
  KEY `idx_pathway_category` (`pathway_category`),
  KEY `idx_evidence_level` (`evidence_level`),
  KEY `idx_gene_symbol` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='靶点与信号通路库';

-- ============================================
-- 法规表
-- ============================================
CREATE TABLE `regulation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` VARCHAR(300) NOT NULL COMMENT '法规名称',
  `name_english` VARCHAR(300) COMMENT '法规名称英文',
  `regulation_number` VARCHAR(100) COMMENT '法规编号/文号',
  `issuing_authority` VARCHAR(200) COMMENT '发布机构',
  `issuing_authority_type` VARCHAR(20) COMMENT '发布机构类型 (NMPA-国家药监局 WHO-世卫组织 FDA-美国 FDA EMA-欧盟 EMA ICH-国际人用药品注册技术协调会)',
  `regulation_type` VARCHAR(50) COMMENT '法规类型 (REGULATION-法规 GUIDELINE-指导原则 NOTICE-通知 ANNOUNCEMENT-公告)',
  `category` VARCHAR(100) COMMENT '法规分类 (注册管理 药学研究 非临床研究 临床研究 上市后研究)',
  `sub_category` VARCHAR(100) COMMENT '法规亚类',
  `effective_date` DATE COMMENT '发布/生效日期',
  `implementation_date` DATE COMMENT '实施日期',
  `expiration_date` DATE COMMENT '废止日期',
  `is_valid` TINYINT(1) DEFAULT 1 COMMENT '是否现行有效 (true-有效 false-已废止)',
  `applicable_field` VARCHAR(50) COMMENT '适用领域 (中药 化药 生物制品)',
  `original_url` VARCHAR(500) COMMENT '法规原文 URL',
  `pdf_path` VARCHAR(500) COMMENT 'PDF 文件路径',
  `summary` TEXT COMMENT '法规摘要',
  `full_text` LONGTEXT COMMENT '法规全文',
  `key_provisions` TEXT COMMENT '核心条款 (JSON 数组)',
  `related_herbs` TEXT COMMENT '涉及药材',
  `related_formulas` TEXT COMMENT '涉及方剂',
  `related_indications` TEXT COMMENT '涉及适应症 (JSON 数组)',
  `related_regulations` TEXT COMMENT '相关法规 (JSON 数组)',
  `change_description` TEXT COMMENT '变更内容描述 (如为修订版)',
  `impact_assessment` TEXT COMMENT '对研发的影响评估',
  `execution_points` TEXT COMMENT '执行要点',
  `interpretation_urls` TEXT COMMENT '解读文章 URL(JSON 数组)',
  `tags` TEXT COMMENT '关键词标签 (JSON 数组)',
  `importance` VARCHAR(20) COMMENT '关注级别 (CRITICAL-关键 IMPORTANT-重要 NORMAL-普通)',
  `need_follow_up` TINYINT(1) DEFAULT 0 COMMENT '是否需要跟进',
  `follow_up_status` VARCHAR(20) COMMENT '跟进状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成)',
  `follow_up_by` VARCHAR(100) COMMENT '跟进人',
  `last_follow_up_time` DATETIME COMMENT '最后跟进时间',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` VARCHAR(100) COMMENT '创建人',
  `update_by` VARCHAR(100) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_regulation_number` (`regulation_number`),
  KEY `idx_issuing_authority_type` (`issuing_authority_type`),
  KEY `idx_regulation_type` (`regulation_type`),
  KEY `idx_category` (`category`),
  KEY `idx_is_valid` (`is_valid`),
  KEY `idx_applicable_field` (`applicable_field`),
  KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='新药研发法规和指引库';

-- ============================================
-- 项目表
-- ============================================
CREATE TABLE `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `project_name` VARCHAR(300) NOT NULL COMMENT '项目名称',
  `herb_name` VARCHAR(200) COMMENT '药材名称',
  `formula_name` VARCHAR(200) COMMENT '方剂名称',
  `indication` VARCHAR(500) COMMENT '适应症',
  `phase` VARCHAR(50) COMMENT '项目阶段 (EXPLORATION-探索期 VERIFICATION-验证期 OPTIMIZATION-优化期 DECLARATION-申报期)',
  `projector_id` BIGINT COMMENT '项目负责人用户 ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PLANNING' COMMENT '项目状态 (PLANNING-立项中 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消)',
  `start_time` DATETIME COMMENT '立项时间',
  `planned_end_time` DATETIME COMMENT '计划完成时间',
  `actual_end_time` DATETIME COMMENT '实际完成时间',
  `description` TEXT COMMENT '项目描述',
  `budget` DECIMAL(15,2) COMMENT '预算金额',
  `priority` VARCHAR(20) COMMENT '优先级 (HIGH-高 MEDIUM-中 LOW-低)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_name` (`project_name`),
  KEY `idx_phase` (`phase`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_projector_id` (`projector_id`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='中药新药研发项目';

-- ============================================
-- 项目决策表
-- ============================================
CREATE TABLE `project_decision` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `project_id` BIGINT NOT NULL COMMENT '项目 ID',
  `decision_type` VARCHAR(50) NOT NULL COMMENT '决策类型 (FORMULATION-配方决策 EXPERIMENT-实验决策 RESOURCE-资源决策 RISK-风险控制)',
  `title` VARCHAR(300) NOT NULL COMMENT '决策标题',
  `content` TEXT NOT NULL COMMENT '决策内容',
  `ai_recommendation` TEXT COMMENT 'AI 分析建议',
  `expert_conclusion` TEXT COMMENT '专家结论',
  `evidence_chain` TEXT COMMENT '证据链 (JSON 格式)',
  `confidence` DECIMAL(3,2) COMMENT '置信度 (0-1)',
  `projector_id` BIGINT COMMENT '决策人用户 ID',
  `version` INT DEFAULT 1 COMMENT '版本号',
  `document_id` BIGINT COMMENT '关联文档 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_decision_type` (`decision_type`),
  KEY `idx_projector_id` (`projector_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目决策记录表';

-- ============================================
-- 项目文档表
-- ============================================
CREATE TABLE `project_document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `project_id` BIGINT NOT NULL COMMENT '项目 ID',
  `doc_type` VARCHAR(50) NOT NULL COMMENT '文档类型 (PROJECT_REPORT-项目报告 RESEARCH-研究文档 EXPERIMENT-实验记录 TECHNICAL-技术资料)',
  `doc_name` VARCHAR(300) NOT NULL COMMENT '文档名称',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
  `file_size` BIGINT COMMENT '文件大小 (字节)',
  `file_type` VARCHAR(50) COMMENT '文件类型',
  `version` VARCHAR(50) COMMENT '版本号',
  `upload_by` VARCHAR(100) COMMENT '上传人',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `tags` VARCHAR(500) COMMENT '分类标签 (逗号分隔)',
  `summary` TEXT COMMENT '摘要/描述',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_doc_type` (`doc_type`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_upload_by` (`upload_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目文档管理表';

-- ============================================
-- 项目成员表
-- ============================================
CREATE TABLE `project_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `project_id` BIGINT NOT NULL COMMENT '项目 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role` VARCHAR(20) NOT NULL COMMENT '用户角色 (LEAD-负责人 MEMBER-普通成员 REVIEWER-评审员 OBSERVER-观察员)',
  `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`, `user_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目用户关联表';

-- ============================================
-- 用户任务表
-- ============================================
CREATE TABLE `user_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `title` VARCHAR(300) NOT NULL COMMENT '任务标题',
  `description` TEXT COMMENT '任务描述',
  `task_type` VARCHAR(50) COMMENT '任务类型 (RESEARCH-研究任务 APPROVAL-审批任务 EXPERIMENT-实验任务 REPORT-报告任务 LEARNING-学习任务)',
  `priority` VARCHAR(20) COMMENT '任务优先级 (HIGH-高 MEDIUM-中 LOW-低)',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '任务状态 (PENDING-待处理 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消 OVERDUE-已逾期)',
  `project_id` BIGINT COMMENT '关联项目 ID',
  `project_name` VARCHAR(300) COMMENT '关联项目名',
  `decision_id` BIGINT COMMENT '关联决策 ID',
  `document_id` BIGINT COMMENT '关联文档 ID',
  `ai_analysis_id` BIGINT COMMENT '关联 AI 分析 ID',
  `assignee_id` BIGINT COMMENT '负责人 ID',
  `assignee_name` VARCHAR(100) COMMENT '负责人姓名',
  `create_by` BIGINT COMMENT '创建人 ID',
  `create_by_name` VARCHAR(100) COMMENT '创建人姓名',
  `source` VARCHAR(20) COMMENT '任务来源 (MANUAL-手动创建 AUTO-AI 自动生成 SYSTEM-系统任务)',
  `deadline` DATETIME COMMENT '截止时间',
  `actual_completion_time` DATETIME COMMENT '实际完成时间',
  `tags` TEXT COMMENT '任务分类标签 (JSON 数组)',
  `reminder_settings` TEXT COMMENT '任务提醒设置 (JSON 对象)',
  `completion_score` INT COMMENT '任务评分 (0-5)',
  `completion_feedback` TEXT COMMENT '任务完成反馈',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_priority` (`priority`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_assignee_id` (`assignee_id`),
  KEY `idx_deadline` (`deadline`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户任务表';

