import type { PageQuery } from '@/types/pagination';

export interface AdminUser {
  id: string;
  username: string;
  password: string;
  name?: string | null;
  sex?: string | null;
  birth?: string | null;
  email?: string | null;
  phone?: string | null;
  avatar?: string | null;
  lastLoginTime?: string | null;
  lastLoginIp?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface Teacher {
  id: string;
  username: string;
  password: string;
  name?: string | null;
  sex?: string | null;
  phone?: string | null;
  email?: string | null;
  avatar?: string | null;
  title?: string | null;
  lastLoginTime?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export type AdminQuery = PageQuery & {
  keyword?: string;
};

export type TeacherQuery = PageQuery & {
  keyword?: string;
};

export interface AdminUpsertRequest {
  username: string;
  password?: string;
  name?: string;
  sex?: string;
  birth?: string;
  email?: string;
  phone?: string;
  avatar?: string;
}

export interface TeacherUpsertRequest {
  username: string;
  password?: string;
  name?: string;
  sex?: string;
  phone?: string;
  email?: string;
  avatar?: string;
  title?: string;
}
