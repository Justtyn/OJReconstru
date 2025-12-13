import http from '@/services/http';
import type {
  AuthUserVO,
  LoginRequest,
  RegisterRequest,
  VerifyEmailRequest,
  ResendRegisterCodeRequest,
  ForgotPasswordSendCodeRequest,
  ForgotPasswordVerifyRequest,
  ChangePasswordRequest,
} from '@/types';

const login = (payload: LoginRequest) => http.post<AuthUserVO>('/api/auth/login', payload);
const register = (payload: RegisterRequest) => http.post<AuthUserVO | null>('/api/auth/register', payload);
const verifyEmail = (payload: VerifyEmailRequest) => http.post<AuthUserVO>('/api/auth/verifyEmail', payload);
const resendVerifyCode = (payload: ResendRegisterCodeRequest) =>
  http.post<void>('/api/auth/register/resendCode', payload);
const fetchProfile = () => http.get<AuthUserVO>('/api/auth/users/me');
const logout = () => http.post<void>('/api/auth/logout');
const sendForgotCode = (payload: ForgotPasswordSendCodeRequest) =>
  http.post<void>('/api/auth/password/forgot/sendCode', payload);
const verifyForgotPassword = (payload: ForgotPasswordVerifyRequest) =>
  http.post<void>('/api/auth/password/forgot/verify', payload);
const changePassword = (payload: ChangePasswordRequest) => http.post<void>('/api/auth/password/change', payload);

export const authService = {
  login,
  register,
  verifyEmail,
  resendVerifyCode,
  fetchProfile,
  logout,
  sendForgotCode,
  verifyForgotPassword,
  changePassword,
};
