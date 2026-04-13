import type { SelectOption } from './formSchemaTypes'

export const herbToxicityOptions: SelectOption[] = [
  { label: '无毒 (NONE)', value: 'NONE' },
  { label: '低毒 (LOW)', value: 'LOW' },
  { label: '中毒 (MEDIUM)', value: 'MEDIUM' },
  { label: '剧毒 (HIGH)', value: 'HIGH' }
]

export const literatureTypeOptions: SelectOption[] = [
  { label: '原创研究 (ORIGINAL_RESEARCH)', value: 'ORIGINAL_RESEARCH' },
  { label: '综述 (REVIEW)', value: 'REVIEW' },
  { label: '病例报道 (CASE_REPORT)', value: 'CASE_REPORT' },
  { label: '荟萃分析 (META_ANALYSIS)', value: 'META_ANALYSIS' }
]

export const literatureResearchTypeOptions: SelectOption[] = [
  { label: '体外 (IN_VITRO)', value: 'IN_VITRO' },
  { label: '体内 (IN_VIVO)', value: 'IN_VIVO' },
  { label: '临床 (CLINICAL)', value: 'CLINICAL' }
]

export const importanceLevelOptions: SelectOption[] = [
  { label: '1', value: 1 },
  { label: '2', value: 2 },
  { label: '3', value: 3 },
  { label: '4', value: 4 },
  { label: '5', value: 5 }
]

export const regulationIssuingAuthorityTypeOptions: SelectOption[] = [
  { label: '国家药监局 (NMPA)', value: 'NMPA' },
  { label: '世卫组织 (WHO)', value: 'WHO' },
  { label: '美国 FDA (FDA)', value: 'FDA' },
  { label: '欧盟 EMA (EMA)', value: 'EMA' },
  { label: 'ICH', value: 'ICH' }
]

export const regulationTypeOptions: SelectOption[] = [
  { label: '法规 (REGULATION)', value: 'REGULATION' },
  { label: '指导原则 (GUIDELINE)', value: 'GUIDELINE' },
  { label: '通知 (NOTICE)', value: 'NOTICE' },
  { label: '公告 (ANNOUNCEMENT)', value: 'ANNOUNCEMENT' }
]

export const regulationCategoryOptions: SelectOption[] = [
  { label: '注册管理', value: '注册管理' },
  { label: '药学研究', value: '药学研究' },
  { label: '非临床研究', value: '非临床研究' },
  { label: '临床研究', value: '临床研究' },
  { label: '上市后研究', value: '上市后研究' }
]

export const regulationApplicableFieldOptions: SelectOption[] = [
  { label: '中药', value: '中药' },
  { label: '化药', value: '化药' },
  { label: '生物制品', value: '生物制品' }
]

export const patentTypeOptions: SelectOption[] = [
  { label: '发明 (INVENTION)', value: 'INVENTION' },
  { label: '实用新型 (UTILITY_MODEL)', value: 'UTILITY_MODEL' },
  { label: '外观设计 (DESIGN)', value: 'DESIGN' }
]

export const patentStatusOptions: SelectOption[] = [
  { label: '待审 (PENDING)', value: 'PENDING' },
  { label: '已授权 (GRANTED)', value: 'GRANTED' },
  { label: '被驳回 (REJECTED)', value: 'REJECTED' },
  { label: '无效 (INVALID)', value: 'INVALID' }
]

export const patentApplicantTypeOptions: SelectOption[] = [
  { label: '个人 (INDIVIDUAL)', value: 'INDIVIDUAL' },
  { label: '机构 (ORGANIZATION)', value: 'ORGANIZATION' },
  { label: '高校 (UNIVERSITY)', value: 'UNIVERSITY' }
]

export const patentInfringementRiskOptions: SelectOption[] = [
  { label: '高风险 (HIGH)', value: 'HIGH' },
  { label: '中风险 (MEDIUM)', value: 'MEDIUM' },
  { label: '低风险 (LOW)', value: 'LOW' },
  { label: '无风险 (NONE)', value: 'NONE' }
]

export const targetPathwayTypeOptions: SelectOption[] = [
  { label: '靶点 (TARGET)', value: 'TARGET' },
  { label: '通路 (PATHWAY)', value: 'PATHWAY' }
]

export const targetPathwayTargetTypeOptions: SelectOption[] = [
  { label: '基因 (GENE)', value: 'GENE' },
  { label: '蛋白 (PROTEIN)', value: 'PROTEIN' },
  { label: '受体 (RECEPTOR)', value: 'RECEPTOR' },
  { label: '酶 (ENZYME)', value: 'ENZYME' }
]

export const targetPathwayEffectOptions: SelectOption[] = [
  { label: '激活 (ACTIVATE)', value: 'ACTIVATE' },
  { label: '抑制 (INHIBIT)', value: 'INHIBIT' },
  { label: '调控 (REGULATE)', value: 'REGULATE' }
]

export const targetPathwayEvidenceLevelOptions: SelectOption[] = [
  { label: '高 (HIGH)', value: 'HIGH' },
  { label: '中 (MEDIUM)', value: 'MEDIUM' },
  { label: '低 (LOW)', value: 'LOW' }
]

export const diseaseCategoryOptions: SelectOption[] = [
  { label: '内科', value: '内科' },
  { label: '外科', value: '外科' },
  { label: '妇科', value: '妇科' },
  { label: '儿科', value: '儿科' },
  { label: '骨科', value: '骨科' },
  { label: '五官科', value: '五官科' },
  { label: '其他', value: '其他' }
]

export const diseaseSeverityOptions: SelectOption[] = [
  { label: '轻', value: '轻' },
  { label: '中', value: '中' },
  { label: '重', value: '重' },
  { label: '危重', value: '危重' }
]
