export interface RegisterRequest {
  username: string;
  password: string;
  role: 'student' | 'teacher' | 'admin';
  email?: string;
  name?: string;
}

export interface VerifyEmailRequest {
  username: string;
  code: string;
}

export interface ForgotPasswordSendCodeRequest {
  username: string;
}

export interface ForgotPasswordVerifyRequest {
  username: string;
  code: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
