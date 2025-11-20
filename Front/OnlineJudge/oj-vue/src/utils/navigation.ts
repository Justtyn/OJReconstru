import type { UserRole } from '@/types';

export const resolveDefaultRoute = (role?: UserRole | null) => {
  if (!role || role === 'student') return '/home';
  return '/admin/overview';
};
