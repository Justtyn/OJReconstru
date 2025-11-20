import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosRequestHeaders } from 'axios';
import type { ApiResponse } from '@/types';
import { useAuthStore } from '@/stores/auth';
import { pinia } from '@/stores';

const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  timeout: 15000,
});

instance.interceptors.request.use((config) => {
  const authStore = useAuthStore(pinia);
  if (authStore.token && !config.headers?.Authorization) {
    config.headers = {
      ...config.headers,
      Authorization: authStore.token,
    } as AxiosRequestHeaders;
  }
  return config;
});

instance.interceptors.response.use(
  (response: any) => {
    const payload = response.data as ApiResponse<unknown>;
    if (typeof payload?.code !== 'number') {
      return Promise.reject(new Error('接口响应格式异常'));
    }
    if (payload.code !== 0) {
      return Promise.reject(payload);
    }
    return payload.data as any;
  },
  (error) => {
    const status = error.response?.status;
    if (status === 401) {
      const authStore = useAuthStore(pinia);
      authStore.forceLogout();
    }
    return Promise.reject(error);
  }
);

type HttpClient = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>;
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>;
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>;
};

const http: HttpClient = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return instance.get<any, T>(url, config);
  },
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.post<any, T>(url, data, config);
  },
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return instance.put<any, T>(url, data, config);
  },
  delete<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return instance.delete<any, T>(url, config);
  },
};

export default http;
