import type { PageQuery } from '@/types/pagination';

export interface Classes {
  id: string;
  name: string;
  creatorId?: string;
  code?: string;
  startDate?: string;
  endDate?: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClassesRequest {
  name: string;
  creatorId?: number;
  code?: string;
  startDate?: string;
  endDate?: string;
  description?: string;
}

export interface ClassesQuery extends PageQuery {
  keyword?: string;
}
