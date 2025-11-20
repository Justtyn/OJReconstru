export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResult<T> {
  records: T[];
  current: number;
  size: number;
  total: number;
  pages: number;
  orders: Array<{ column: string; asc: boolean }>;
}

export type Nullable<T> = T | null;
