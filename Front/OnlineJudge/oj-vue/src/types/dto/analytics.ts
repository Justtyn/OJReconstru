export interface AnalyticsSummary {
  submissionTotal: number;
  submissionAccepted: number;
  problemActive: number;
  problemInactive: number;
  solutionTotal: number;
  solutionActive: number;
  discussionTotal: number;
  discussionActive: number;
  loginSuccess: number;
  loginFail: number;
}

export interface AnalyticsStatusCount {
  statusId: number | string;
  total: number;
  name?: string;
  code?: string;
  percentage?: number;
}

export type AnalyticsGranularity = 'day' | 'week' | 'month' | 'year' | 'all';

export interface AnalyticsTimeseriesPoint {
  label: string;
  total: number;
  success?: number;
  fail?: number;
  bucketStart?: string;
  bucketEnd?: string | null;
}

export interface AnalyticsRangeQuery {
  startTime?: string;
  endTime?: string;
}

export type AnalyticsTimeseriesQuery = AnalyticsRangeQuery & {
  granularity?: AnalyticsGranularity;
};
