import http from '@/services/http';
import type { AnalyticsStatusCount, AnalyticsSummary } from '@/types';

export const analyticsService = {
  fetchSummary() {
    return http.get<AnalyticsSummary>('/api/analytics/summary');
  },
  fetchSubmissionStatus() {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/submissions/status');
  },
  fetchProblemStatus() {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/problems/status');
  },
  fetchLoginStatus() {
    return http.get<AnalyticsStatusCount[]>('/api/analytics/logins/status');
  },
};
