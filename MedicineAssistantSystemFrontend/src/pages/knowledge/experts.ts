export type ExpertItem = {
  key: string
  slug?: string
  name: string
  tag: string
  /** 后端 SiliconFlow 音色键，缺省为 chenkeji */
  voiceKey?: string
}

export const EXPERTS: ExpertItem[] = [
  { key: '李佃贵', slug: 'li-dian-gui', name: '李佃贵', tag: '国医大师 · 脾胃病',voiceKey:'lidiangui'},
  { key: '王玉川', slug: 'wang-yu-chuan', name: '王玉川', tag: '国医大师 · 内经研究',voiceKey:'wangyuchuan'},
  { key: '程莘农', slug: 'cheng-shen-nong', name: '程莘农', tag: '国医大师 · 针灸',voiceKey:'chengshennong' },
  { key: '裘法祖', slug: 'qiu-fa-zu', name: '裘法祖', tag: '外科学 · 器官移植',voiceKey:'qiufazu'},
  { key: '陈可冀', slug: 'chen-ke-ji', name: '陈可冀', tag: '中西医结合 · 心血管病', voiceKey: 'chenkeji' },
  { key: '孙燕', slug: 'sun-yan', name: '孙燕', tag: '肿瘤内科' ,voiceKey:'sunyan'},

]

export function getExpertByKey(key: string) {
  return EXPERTS.find((e) => e.key === key || e.slug === key)
}
