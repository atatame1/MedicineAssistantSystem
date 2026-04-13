import type { FormField } from './formSchemaTypes'
import {
  diseaseCategoryOptions,
  diseaseSeverityOptions,
  herbToxicityOptions,
  importanceLevelOptions,
  literatureResearchTypeOptions,
  literatureTypeOptions,
  patentApplicantTypeOptions,
  patentInfringementRiskOptions,
  patentStatusOptions,
  patentTypeOptions,
  regulationApplicableFieldOptions,
  regulationCategoryOptions,
  regulationIssuingAuthorityTypeOptions,
  regulationTypeOptions,
  targetPathwayEffectOptions,
  targetPathwayEvidenceLevelOptions,
  targetPathwayTargetTypeOptions,
  targetPathwayTypeOptions
} from './knowledgeEnums'

export const herbCreate: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'source', label: '来源' },
  { prop: 'effects', label: '功效', type: 'textarea' },
  { prop: 'category', label: '分类标签' }
]

export const herbEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'pinyin', label: '拼音' },
  { prop: 'aliases', label: '别名' },
  { prop: 'source', label: '来源' },
  { prop: 'medicinalPart', label: '药用部位' },
  { prop: 'nature', label: '性(四性)' },
  { prop: 'taste', label: '味(五味)' },
  { prop: 'meridian', label: '归经' },
  { prop: 'effects', label: '功效', type: 'textarea' },
  { prop: 'indications', label: '主治病症', type: 'textarea' },
  { prop: 'usage', label: '用法用量', type: 'textarea' },
  { prop: 'processing', label: '炮制', type: 'textarea' },
  { prop: 'origin', label: '产地' },
  { prop: 'collectionTime', label: '采集时间' },
  { prop: 'storage', label: '贮藏', type: 'textarea' },
  { prop: 'toxicity', label: '毒性', type: 'select', options: herbToxicityOptions },
  { prop: 'contraindications', label: '禁忌', type: 'textarea' },
  { prop: 'relatedFormulas', label: '相关方剂(JSON)', type: 'textarea' },
  { prop: 'activeComponents', label: '活性成分(JSON)', type: 'textarea' },
  { prop: 'imageUrl', label: '图片URL' },
  { prop: 'category', label: '分类标签' }
]

export const formulaCreate: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'source', label: '出处' },
  { prop: 'category', label: '分类' },
  { prop: 'effects', label: '功效', type: 'textarea' },
  { prop: 'indications', label: '主治病症', type: 'textarea' }
]

export const formulaEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'aliases', label: '别名' },
  { prop: 'source', label: '出处' },
  { prop: 'era', label: '年代' },
  { prop: 'category', label: '分类' },
  { prop: 'composition', label: '组方结构', type: 'textarea' },
  { prop: 'junYao', label: '君药(JSON)', type: 'textarea' },
  { prop: 'chenYao', label: '臣药(JSON)', type: 'textarea' },
  { prop: 'zuoYao', label: '佐药(JSON)', type: 'textarea' },
  { prop: 'shiYao', label: '使药(JSON)', type: 'textarea' },
  { prop: 'totalDose', label: '总剂量' },
  { prop: 'effects', label: '功效', type: 'textarea' },
  { prop: 'indications', label: '主治病症', type: 'textarea' },
  { prop: 'syndromes', label: '主治证候', type: 'textarea' },
  { prop: 'clinicalManifestations', label: '临床表现', type: 'textarea' },
  { prop: 'modernApplications', label: '现代应用', type: 'textarea' },
  { prop: 'usage', label: '用法用量', type: 'textarea' },
  { prop: 'contraindications', label: '配伍禁忌', type: 'textarea' },
  { prop: 'clinicalCaseCount', label: '临床案例数', type: 'number' },
  { prop: 'efficacyScore', label: '有效性评分', type: 'number' },
  { prop: 'imageUrl', label: '图片URL' },
  { prop: 'relatedHerbs', label: '相关药材', type: 'textarea' },
  { prop: 'relatedFormulas', label: '相关方剂', type: 'textarea' },
  { prop: 'similarFormulas', label: '相似方剂', type: 'textarea' }
]

export const componentCreate: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'chemicalFormula', label: '化学式' },
  { prop: 'casNumber', label: 'CAS号' },
  { prop: 'bioactivity', label: '生物活性', type: 'textarea' }
]

export const componentEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'aliases', label: '别名' },
  { prop: 'chemicalFormula', label: '化学式' },
  { prop: 'molecularWeight', label: '分子量', type: 'number' },
  { prop: 'casNumber', label: 'CAS号' },
  { prop: 'chemicalStructure', label: '结构(SMILES)', type: 'textarea' },
  { prop: 'structureUrl', label: '结构图URL' },
  { prop: 'sourceHerbs', label: '来源药材(JSON)', type: 'textarea' },
  { prop: 'bioactivity', label: '生物活性', type: 'textarea' },
  { prop: 'pharmacologicalEffects', label: '药理作用(JSON)', type: 'textarea' },
  { prop: 'potentialTargets', label: '潜在靶点(JSON)', type: 'textarea' },
  { prop: 'solubility', label: '溶解性' },
  { prop: 'logp', label: 'LogP', type: 'number' },
  { prop: 'molecularWeightDa', label: '分子量Da', type: 'number' },
  { prop: 'hBondDonorCount', label: '氢键供体', type: 'number' },
  { prop: 'hBondAcceptorCount', label: '氢键受体', type: 'number' },
  { prop: 'rotatableBondCount', label: '旋转键数', type: 'number' },
  { prop: 'polarSurfaceArea', label: '极性表面积', type: 'number' },
  { prop: 'drugLikeScore', label: '类药性', type: 'number' },
  { prop: 'toxicity', label: '毒性评价', type: 'textarea' },
  { prop: 'admeProperties', label: 'ADME', type: 'textarea' },
  { prop: 'sourceDatabase', label: '来源数据库' }
]

export const diseaseCreate: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'category', label: '分类', type: 'select', options: diseaseCategoryOptions },
  { prop: 'icdCode', label: 'ICD编码' },
  { prop: 'overview', label: '概述', type: 'textarea' }
]

export const diseaseEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'aliases', label: '别名' },
  { prop: 'tcmName', label: '中医病名' },
  { prop: 'tcmSyndrome', label: '中医证候', type: 'textarea' },
  { prop: 'category', label: '分类', type: 'select', options: diseaseCategoryOptions },
  { prop: 'subCategory', label: '亚类' },
  { prop: 'icdCode', label: 'ICD编码' },
  { prop: 'modernName', label: '现代医学名称', type: 'textarea' },
  { prop: 'overview', label: '概述', type: 'textarea' },
  { prop: 'etiologyPathogenesis', label: '病因病机', type: 'textarea' },
  { prop: 'clinicalManifestations', label: '临床表现', type: 'textarea' },
  { prop: 'diagnosticCriteria', label: '诊断标准', type: 'textarea' },
  { prop: 'differentialDiagnosis', label: '鉴别诊断', type: 'textarea' },
  { prop: 'commonSyndromes', label: '常见证型(JSON)', type: 'textarea' },
  { prop: 'commonFormulas', label: '常用方剂(JSON)', type: 'textarea' },
  { prop: 'commonHerbs', label: '常用药材(JSON)', type: 'textarea' },
  { prop: 'relatedTargets', label: '关联靶点(JSON)', type: 'textarea' },
  { prop: 'relatedPathways', label: '关联通路(JSON)', type: 'textarea' },
  { prop: 'epidemiology', label: '流行病学', type: 'textarea' },
  { prop: 'prognosis', label: '预后', type: 'textarea' },
  { prop: 'prevention', label: '预防', type: 'textarea' },
  { prop: 'severity', label: '严重程度', type: 'select', options: diseaseSeverityOptions },
  { prop: 'applicablePopulation', label: '适用人群' },
  { prop: 'relatedDiseases', label: '相关疾病(JSON)', type: 'textarea' },
  { prop: 'remark', label: '备注', type: 'textarea' },
  { prop: 'createBy', label: '创建人' },
  { prop: 'updateBy', label: '更新人' }
]

export const targetPathwayCreate: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'type', label: '类型', type: 'select', options: targetPathwayTypeOptions },
  { prop: 'pathwayName', label: '信号通路' },
  { prop: 'geneSymbol', label: '基因符号' }
]

export const targetPathwayEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'aliases', label: '别名' },
  { prop: 'type', label: '类型', type: 'select', options: targetPathwayTypeOptions },
  { prop: 'targetType', label: '靶点类型', type: 'select', options: targetPathwayTargetTypeOptions },
  { prop: 'geneSymbol', label: '基因符号' },
  { prop: 'geneId', label: '基因ID' },
  { prop: 'proteinName', label: '蛋白质名称' },
  { prop: 'uniprotId', label: 'UniProt' },
  { prop: 'pathwayName', label: '信号通路名称' },
  { prop: 'keggPathwayId', label: 'KEGG通路ID' },
  { prop: 'pathwayCategory', label: '通路类别' },
  { prop: 'function', label: '功能', type: 'textarea' },
  { prop: 'biologicalProcess', label: '生物学过程', type: 'textarea' },
  { prop: 'cellularComponent', label: '细胞组分', type: 'textarea' },
  { prop: 'molecularFunction', label: '分子功能', type: 'textarea' },
  { prop: 'relatedDiseases', label: '关联疾病(JSON)', type: 'textarea' },
  { prop: 'relatedDrugs', label: '关联药物(JSON)', type: 'textarea' },
  { prop: 'relatedComponents', label: '关联成分(JSON)', type: 'textarea' },
  { prop: 'upstreamRegulators', label: '上游调控', type: 'textarea' },
  { prop: 'downstreamEffectors', label: '下游效应', type: 'textarea' },
  { prop: 'effect', label: '效应', type: 'select', options: targetPathwayEffectOptions },
  { prop: 'mechanismDescription', label: '机制说明', type: 'textarea' },
  { prop: 'evidenceLevel', label: '证据等级', type: 'select', options: targetPathwayEvidenceLevelOptions },
  { prop: 'literatureCount', label: '文献数', type: 'number' },
  { prop: 'confidenceScore', label: '置信度', type: 'number' }
]

export const patentCreate: FormField[] = [
  { prop: 'name', label: '名称', type: 'textarea' },
  { prop: 'patentNumber', label: '专利号' },
  { prop: 'applicant', label: '申请人' },
  { prop: 'patentType', label: '专利类型', type: 'select', options: patentTypeOptions },
  { prop: 'status', label: '状态', type: 'select', options: patentStatusOptions },
  { prop: 'applicationDate', label: '申请日', type: 'datetime' }
]

export const patentEdit: FormField[] = [
  { prop: 'name', label: '名称', type: 'textarea' },
  { prop: 'nameEnglish', label: '英文名称', type: 'textarea' },
  { prop: 'patentNumber', label: '专利号' },
  { prop: 'ipcClassification', label: 'IPC' },
  { prop: 'pctNumber', label: 'PCT号' },
  { prop: 'applicationNumber', label: '申请号' },
  { prop: 'publicationNumber', label: '公开号' },
  { prop: 'applicationDate', label: '申请日', type: 'datetime' },
  { prop: 'grantDate', label: '授权日', type: 'datetime' },
  { prop: 'patentType', label: '专利类型', type: 'select', options: patentTypeOptions },
  { prop: 'status', label: '状态', type: 'select', options: patentStatusOptions },
  { prop: 'countryRegion', label: '国家/地区' },
  { prop: 'applicant', label: '申请人' },
  { prop: 'applicantType', label: '申请人类型', type: 'select', options: patentApplicantTypeOptions },
  { prop: 'inventors', label: '发明人(JSON)', type: 'textarea' },
  { prop: 'agency', label: '代理机构' },
  { prop: 'attorney', label: '代理人' },
  { prop: 'abstractContent', label: '摘要', type: 'textarea' },
  { prop: 'abstractEnglish', label: '摘要英文', type: 'textarea' },
  { prop: 'claims', label: '权利要求(JSON)', type: 'textarea' },
  { prop: 'technicalField', label: '技术领域', type: 'textarea' },
  { prop: 'backgroundTechnology', label: '背景技术', type: 'textarea' },
  { prop: 'inventionContent', label: '发明内容', type: 'textarea' },
  { prop: 'figureDescription', label: '附图说明', type: 'textarea' },
  { prop: 'implementationDetails', label: '实施方式', type: 'textarea' },
  { prop: 'relatedHerbs', label: '涉及药材(JSON)', type: 'textarea' },
  { prop: 'relatedFormulas', label: '涉及方剂(JSON)', type: 'textarea' },
  { prop: 'relatedComponents', label: '涉及成分(JSON)', type: 'textarea' },
  { prop: 'relatedIndications', label: '适应症(JSON)', type: 'textarea' },
  { prop: 'relatedTargets', label: '靶点(JSON)', type: 'textarea' },
  { prop: 'relatedPathways', label: '通路(JSON)', type: 'textarea' },
  { prop: 'innovations', label: '创新点', type: 'textarea' },
  { prop: 'technicalAdvantages', label: '技术优势', type: 'textarea' },
  { prop: 'infringementRisk', label: '侵权风险', type: 'select', options: patentInfringementRiskOptions },
  { prop: 'infringementDescription', label: '风险说明', type: 'textarea' },
  { prop: 'patentValueScore', label: '价值评分', type: 'number' },
  { prop: 'citationCount', label: '被引次数', type: 'number' },
  { prop: 'familySize', label: '同族数', type: 'number' },
  { prop: 'familyPatents', label: '同族专利(JSON)', type: 'textarea' },
  { prop: 'similarPatents', label: '相似专利(JSON)', type: 'textarea' },
  { prop: 'fullTextPath', label: '全文PDF路径' },
  { prop: 'figuresUrl', label: '附图URL' },
  { prop: 'isAnalyzed', label: '已分析', type: 'switch' },
  { prop: 'analyzedBy', label: '分析人' },
  { prop: 'analyzedTime', label: '分析时间', type: 'datetime' },
  { prop: 'createBy', label: '创建人' },
  { prop: 'updateBy', label: '更新人' },
  { prop: 'remark', label: '备注', type: 'textarea' }
]

export const regulationCreate: FormField[] = [
  { prop: 'name', label: '法规名称' },
  { prop: 'regulationNumber', label: '文号' },
  { prop: 'issuingAuthority', label: '发布机构' },
  { prop: 'regulationType', label: '法规类型', type: 'select', options: regulationTypeOptions },
  { prop: 'category', label: '分类', type: 'select', options: regulationCategoryOptions },
  { prop: 'effectiveDate', label: '生效日期', type: 'datetime' }
]

export const regulationEdit: FormField[] = [
  { prop: 'name', label: '名称' },
  { prop: 'nameEnglish', label: '英文名称' },
  { prop: 'regulationNumber', label: '文号' },
  { prop: 'issuingAuthority', label: '发布机构' },
  { prop: 'issuingAuthorityType', label: '机构类型', type: 'select', options: regulationIssuingAuthorityTypeOptions },
  { prop: 'regulationType', label: '法规类型', type: 'select', options: regulationTypeOptions },
  { prop: 'category', label: '分类', type: 'select', options: regulationCategoryOptions },
  { prop: 'subCategory', label: '亚类' },
  { prop: 'effectiveDate', label: '生效日期', type: 'datetime' },
  { prop: 'implementationDate', label: '实施日期', type: 'datetime' },
  { prop: 'expirationDate', label: '废止日期', type: 'datetime' },
  { prop: 'isValid', label: '现行有效', type: 'switch' },
  { prop: 'applicableField', label: '适用领域', type: 'select', options: regulationApplicableFieldOptions },
  { prop: 'originalUrl', label: '原文URL' },
  { prop: 'pdfPath', label: 'PDF路径' },
  { prop: 'summary', label: '摘要', type: 'textarea' },
  { prop: 'fullText', label: '全文', type: 'textarea' },
  { prop: 'keyProvisions', label: '核心条款(JSON)', type: 'textarea' },
  { prop: 'relatedHerbs', label: '涉及药材', type: 'textarea' },
  { prop: 'relatedFormulas', label: '涉及方剂', type: 'textarea' },
  { prop: 'relatedIndications', label: '适应症(JSON)', type: 'textarea' },
  { prop: 'relatedRegulations', label: '相关法规(JSON)', type: 'textarea' },
  { prop: 'changeDescription', label: '变更说明', type: 'textarea' },
  { prop: 'impactAssessment', label: '影响评估', type: 'textarea' },
  { prop: 'executionPoints', label: '执行要点', type: 'textarea' },
  { prop: 'interpretationUrls', label: '解读URL(JSON)', type: 'textarea' },
  { prop: 'tags', label: '标签(JSON)', type: 'textarea' },
  { prop: 'importance', label: '关注级别', type: 'select', options: importanceLevelOptions }
]

export const literatureCreate: FormField[] = [
  { prop: 'title', label: '标题' },
  { prop: 'journalName', label: '期刊' },
  { prop: 'abstractContent', label: '文献摘要', type: 'textarea' },
  { prop: 'publishYear', label: '发表年', type: 'year' },
  { prop: 'literatureType', label: '文献类型', type: 'select', options: literatureTypeOptions }
]

export const literatureEdit: FormField[] = [
  { prop: 'title', label: '标题' },
  { prop: 'titleEnglish', label: '英文标题', type: 'textarea' },
  { prop: 'authors', label: '作者(JSON)', type: 'textarea' },
  { prop: 'publishYear', label: '年', type: 'number' },
  { prop: 'publishMonth', label: '月', type: 'number' },
  { prop: 'journalName', label: '期刊' },
  { prop: 'issn', label: 'ISSN' },
  { prop: 'volume', label: '卷' },
  { prop: 'issue', label: '期' },
  { prop: 'pages', label: '页码' },
  { prop: 'doi', label: 'DOI' },
  { prop: 'pubmedId', label: 'PubMed' },
  { prop: 'wosNumber', label: 'WOS' },
  { prop: 'abstractContent', label: '摘要', type: 'textarea' },
  { prop: 'abstractEnglish', label: '摘要英文', type: 'textarea' },
  { prop: 'keywords', label: '关键词(JSON)', type: 'textarea' },
  { prop: 'keywordsEnglish', label: '关键词英(JSON)', type: 'textarea' },
  { prop: 'literatureType', label: '文献类型', type: 'select', options: literatureTypeOptions },
  { prop: 'researchType', label: '研究类型', type: 'select', options: literatureResearchTypeOptions },
  { prop: 'subjects', label: '研究对象(JSON)', type: 'textarea' },
  { prop: 'relatedHerbs', label: '药材(JSON)', type: 'textarea' },
  { prop: 'relatedFormulas', label: '方剂(JSON)', type: 'textarea' },
  { prop: 'relatedComponents', label: '成分(JSON)', type: 'textarea' },
  { prop: 'relatedTargets', label: '靶点(JSON)', type: 'textarea' },
  { prop: 'relatedPathways', label: '通路(JSON)', type: 'textarea' },
  { prop: 'relatedDiseases', label: '疾病(JSON)', type: 'textarea' },
  { prop: 'methodology', label: '研究方法', type: 'textarea' },
  { prop: 'mainFindings', label: '主要发现', type: 'textarea' },
  { prop: 'conclusion', label: '结论', type: 'textarea' },
  { prop: 'innovations', label: '创新点', type: 'textarea' },
  { prop: 'technicalRoute', label: '技术路线', type: 'textarea' },
  { prop: 'citationCount', label: '被引', type: 'number' },
  { prop: 'downloadCount', label: '下载', type: 'number' },
  { prop: 'fullTextUrl', label: '全文URL' },
  { prop: 'abstractUrl', label: '摘要URL' },
  { prop: 'tags', label: '标签(JSON)', type: 'textarea' },
  { prop: 'importance', label: '重要度', type: 'select', options: importanceLevelOptions }
]
