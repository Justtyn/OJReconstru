import axios from 'axios';
import type { ApiResponse } from '@/types';
import { useAuthStore } from '@/stores/auth';
import { pinia } from '@/stores';

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  timeout: 15000,
});

http.interceptors.request.use((config) => {
  const authStore = useAuthStore(pinia);
  if (authStore.token && !config.headers?.Authorization) {
    config.headers = {
      ...config.headers,
      Authorization: authStore.token,
    };
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>;
    if (typeof payload?.code !== 'number') {
      return Promise.reject(new Error('接口响应格式异常'));
    }
    if (payload.code !== 0) {
      return Promise.reject(payload);
    }
    return payload.data;
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

export default http;
