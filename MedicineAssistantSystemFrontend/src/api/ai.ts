import { apiPostJson } from './http'

export type AiTaskRequest = { input: string }
export type AiTaskResponse = { output: string }

export const aiApi = {
  projectEvaluation: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/project-evaluation', { input }),
  formulaCompatibility: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/formula-compatibility', { input }),
  mechanismInference: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/mechanism-inference', { input }),
  targetPrediction: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/target-prediction', { input }),
  literatureAnalysis: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/literature-analysis', { input }),
  patentAnalysis: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/patent-analysis', { input }),
  experimentDesign: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/experiment-design', { input }),
  reportGeneration: (input: string) =>
    apiPostJson<AiTaskResponse, AiTaskRequest>('/api/ai/report-generation', { input })
}
