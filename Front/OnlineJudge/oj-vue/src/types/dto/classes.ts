import type { PageQuery } from '@/types/pagination';

export interface Classes {
  id: string;
  name: string;
  creatorId?: string;
  creatorName?: string;
  code?: string;
  startDate?: string;
  endDate?: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClassesRequest {
  name: string;
  creatorId?: string;
  code?: string;
  startDate?: string;
  endDate?: string;
  description?: string;
}

export interface ClassesQuery extends PageQuery {
  keyword?: string;
}

export interface ClassesMember {
  id: string;
  classId: string;
  memberType?: string;
  studentId?: string;
  teacherId?: string;
  joinedAt?: string;
  leftAt?: string;
}

export interface ClassesMemberRequest {
  classId: string;
  memberType: string;
  studentId?: string;
  teacherId?: string;
  joinedAt?: string;
  leftAt?: string;
}

export interface ClassesMemberQuery extends PageQuery {
  classId?: string;
}
