import http from '@/services/http';
import type { Classes, StudentJoinClassRequest } from '@/types';

export const studentClassService = {
  fetchCurrent() {
    return http.get<Classes>('/api/student/classes');
  },
  join(payload: StudentJoinClassRequest) {
    return http.post<Classes>('/api/student/classes/join', payload);
  },
  leave() {
    return http.post<void>('/api/student/classes/leave');
  },
};
