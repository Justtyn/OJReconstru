import http from '@/services/http';
import type {
  AnalyticsRangeQuery,
  AnalyticsTimeseriesQuery,
  AnalyticsStatusCount,
  AnalyticsSummary,
  AnalyticsTimeseriesPoint,
} from '@/types';

const normalizeTimeseriesParams = (params?: AnalyticsTimeseriesQuery) => {
  const { granularity = 'day', startTime, endTime } = params || {};
  return { granularity, startTime, endTime };
};

export const analyticsService = {
  fetchSummary() {
    return http.get<AnalyticsSummary>('/api/analytics/summary');
  },
  fetchSubmissionStatus(params: AnalyticsRangeQuery = {}) {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/submissions/status', { params });
  },
  fetchProblemStatus(params: AnalyticsRangeQuery = {}) {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/problems/status', { params });
  },
  fetchSolutionStatus(params: AnalyticsRangeQuery = {}) {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/solutions/status', { params });
  },
  fetchDiscussionStatus(params: AnalyticsRangeQuery = {}) {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/discussions/status', { params });
  },
  fetchLoginStatus(params: AnalyticsRangeQuery = {}) {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/logins/status', { params });
  },
  fetchSubmissionTimeseries(params: AnalyticsTimeseriesQuery = {}) {
    return http.get<AnalyticsTimeseriesPoint[]>('/api/analytics/submissions/timeseries', {
      params: normalizeTimeseriesParams(params),
    });
  },
  fetchLoginTimeseries(params: AnalyticsTimeseriesQuery = {}) {
    return http.get<AnalyticsTimeseriesPoint[]>('/api/analytics/logins/timeseries', {
      params: normalizeTimeseriesParams(params),
    });
  },
  fetchProblemTimeseries(params: AnalyticsTimeseriesQuery = {}) {
    return http.get<AnalyticsTimeseriesPoint[]>('/api/analytics/problems/timeseries', {
      params: normalizeTimeseriesParams(params),
    });
  },
  fetchSolutionTimeseries(params: AnalyticsTimeseriesQuery = {}) {
    return http.get<AnalyticsTimeseriesPoint[]>('/api/analytics/solutions/timeseries', {
      params: normalizeTimeseriesParams(params),
    });
  },
  fetchDiscussionTimeseries(params: AnalyticsTimeseriesQuery = {}) {
    return http.get<AnalyticsTimeseriesPoint[]>('/api/analytics/discussions/timeseries', {
      params: normalizeTimeseriesParams(params),
    });
  },
};
