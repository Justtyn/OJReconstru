import type { PageQuery } from '@/types/pagination';

export interface Student {
  id: string;
  username: string;
  password?: string;
  name?: string | null;
  sex?: string | null;
  birth?: string | null;
  phone?: string | null;
  email?: string | null;
  avatar?: string | null;
  background?: string | null;
  ac?: number;
  submit?: number;
  school?: string | null;
  score?: number;
  lastLoginTime?: string | null;
  lastLanguage?: string | null;
  createTime?: string | null;
  lastVisitTime?: string | null;
  dailyChallenge?: string | null;
  registerIp?: string | null;
  lastLoginIp?: string | null;
  bio?: string | null;
  isVerified?: boolean;
  updatedAt?: string | null;
}

export type StudentQuery = PageQuery & {
  username?: string;
  email?: string;
};

export interface StudentUpsertRequest {
  username: string;
  password?: string;
  name?: string;
  sex?: string;
  birth?: string;
  phone?: string;
  email?: string;
  avatar?: string;
  background?: string;
  school?: string;
  score?: number;
  bio?: string;
}
