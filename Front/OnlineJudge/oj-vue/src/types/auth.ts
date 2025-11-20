export type UserRole = 'student' | 'teacher' | 'admin';

export interface AuthUserVO {
  id: number;
  username: string;
  email?: string | null;
  avatar?: string | null;
  role: UserRole;
  token: string;
  details: Record<string, unknown>;
}

export interface LoginRequest {
  username: string;
  password: string;
  role: UserRole;
}
