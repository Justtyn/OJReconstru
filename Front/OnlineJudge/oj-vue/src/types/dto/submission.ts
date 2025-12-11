import type { PageQuery } from '@/types/pagination';

export interface Submission {
  id: string;
  problemId: string;
  homeworkId?: string | null;
  languageId?: number;
  overallStatusId?: number;
  overallStatusCode?: string;
  overallStatusName?: string;
  passedCaseCount?: number;
  totalCaseCount?: number;
  score?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface SubmissionDetail extends Submission {
  sourceCode?: string;
  testcaseResults?: TestcaseResult[];
}

export interface TestcaseResult {
  testcaseId: string;
  statusId?: number;
  statusDescription?: string;
  stdout?: string | null;
  stderr?: string | null;
  compileOutput?: string | null;
  message?: string | null;
  timeUsed?: number;
  memoryUsed?: number;
}

export interface SubmissionQuery extends PageQuery {
  problemId?: string | number;
  homeworkId?: string | number | null;
  userId?: string | number;
  languageId?: number;
  overallStatusId?: number;
}
