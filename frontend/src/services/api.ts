import axios from 'axios';
import { API_BASE_URL, STORAGE_KEYS } from '../constants';
import type { ApiResponse, ErrorResponse, AuthResponse } from '../types';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Extend axios config type for retry functionality
declare module 'axios' {
  interface InternalAxiosRequestConfig {
    _retry?: boolean;
  }
}

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors and token refresh
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
        if (refreshToken) {
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
            refreshToken,
          });
          
          const { token } = response.data as AuthResponse;
          localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
          
          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.USER);
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

// Generic API methods
export const apiService = {
  get: async <T>(url: string, params?: any, config?: any): Promise<T> => {
    const response = await api.get(url, { params, ...config });
    return response.data;
  },

  post: async <T>(url: string, data?: any, config?: any): Promise<T> => {
    const response = await api.post(url, data, config);
    return response.data;
  },

  put: async <T>(url: string, data?: any): Promise<T> => {
    const response = await api.put(url, data);
    return response.data;
  },

  patch: async <T>(url: string, data?: any): Promise<T> => {
    const response = await api.patch(url, data);
    return response.data;
  },

  delete: async <T>(url: string): Promise<T> => {
    const response = await api.delete(url);
    return response.data;
  },
};

export default api; 