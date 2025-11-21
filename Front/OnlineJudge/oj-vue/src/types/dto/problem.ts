import type { PageQuery } from '@/types/pagination';

export type ProblemDifficulty = 'easy' | 'medium' | 'hard' | string;

export interface Problem {
  id: string;
  name: string;
  description?: string;
  descriptionInput?: string;
  descriptionOutput?: string;
  sampleInput?: string;
  sampleOutput?: string;
  hint?: string;
  dailyChallenge?: string;
  difficulty?: ProblemDifficulty;
  timeLimitMs?: number;
  memoryLimitKb?: number;
  source?: string | null;
  acCount?: number;
  submitCount?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProblemQuery extends PageQuery {
  keyword?: string;
  difficulty?: ProblemDifficulty;
  isActive?: boolean;
}

export interface ProblemUpsertRequest {
  name: string;
  description?: string;
  descriptionInput?: string;
  descriptionOutput?: string;
  sampleInput?: string;
  sampleOutput?: string;
  hint?: string;
  dailyChallenge?: string;
  difficulty?: ProblemDifficulty;
  timeLimitMs?: number;
  memoryLimitKb?: number;
  source?: string | null;
  isActive?: boolean;
}

export interface ProblemCase {
  id: string;
  problemId: string;
  inputData: string;
  outputData: string;
  isSample?: boolean;
}

export interface ProblemCaseQuery extends PageQuery {
  problemId: string;
  isSample?: boolean;
}

export interface ProblemCaseUpsertRequest {
  inputData: string;
  outputData: string;
  isSample?: boolean;
}
