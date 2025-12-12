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
}
