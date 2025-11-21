import type { PageQuery } from '@/types/pagination';

export type LoginRole = 'student' | 'teacher' | 'admin' | string;

export interface LoginLog {
  id: string;
  role: LoginRole;
  userId: string;
  username: string;
  ipAddress?: string;
  location?: string;
  userAgent?: string;
  device?: string;
  loginTime?: string;
  logoutTime?: string | null;
  success?: boolean;
  failReason?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginLogQuery extends PageQuery {
  role?: LoginRole;
  userId?: string;
}
