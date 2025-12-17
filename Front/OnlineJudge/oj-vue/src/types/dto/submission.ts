import type { PageQuery } from '@/types/pagination';

export interface Submission {
  id: string;
  problemId: string;
  studentId?: string;
  studentUsername?: string;
  studentName?: string | null;
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

export interface SubmissionCreateRequest {
  problemId: string;
  homeworkId?: string | null;
  studentId: string;
  languageId: number;
  sourceCode: string;
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
  problemId?: string;
  homeworkId?: string | null;
  studentId?: string;
  statusId?: number;
  languageId?: number;
}
