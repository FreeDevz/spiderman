import { apiService } from './api';
import type { LoginRequest, RegisterRequest, AuthResponse, User, UserSettings } from '../types';
import { STORAGE_KEYS } from '../constants';

export const authService = {
  // Login user
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiService.post<AuthResponse>('/auth/login', credentials);
    return response;
  },

  // Register new user
  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiService.post<AuthResponse>('/auth/register', userData);
    return response;
  },

  // Logout user
  logout: async (): Promise<void> => {
    try {
      await apiService.post('/auth/logout');
    } catch (error) {
      // Continue with logout even if API call fails
      console.warn('Logout API call failed, continuing with local cleanup');
    } finally {
      // Clear local storage
      localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
      localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
      localStorage.removeItem(STORAGE_KEYS.USER);
    }
  },

  // Refresh token
  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await apiService.post<AuthResponse>('/auth/refresh', { refreshToken });
    return response;
  },

  // Get current user profile
  getProfile: async (): Promise<User> => {
    const response = await apiService.get<User>('/users/profile');
    return response;
  },

  // Update user profile
  updateProfile: async (userData: Partial<User>): Promise<User> => {
    const response = await apiService.put<User>('/users/profile', userData);
    return response;
  },

  // Get user settings
  getSettings: async (): Promise<UserSettings> => {
    const response = await apiService.get<UserSettings>('/users/settings');
    return response;
  },

  // Update user settings
  updateSettings: async (settings: Partial<UserSettings>): Promise<UserSettings> => {
    const response = await apiService.put<UserSettings>('/users/settings', settings);
    return response;
  },

  // Delete user account
  deleteAccount: async (): Promise<void> => {
    await apiService.delete('/users/account');
    // Clear local storage after account deletion
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
  },

  // Check if user is authenticated
  isAuthenticated: (): boolean => {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    return !!token;
  },

  // Get stored user data
  getStoredUser: (): User | null => {
    const userData = localStorage.getItem(STORAGE_KEYS.USER);
    return userData ? JSON.parse(userData) : null;
  },

  // Store user data
  storeUser: (user: User): void => {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
  },

  // Store auth tokens
  storeTokens: (token: string, refreshToken: string): void => {
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
  },

  // Get stored token
  getToken: (): string | null => {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  },

  // Get stored refresh token
  getRefreshToken: (): string | null => {
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  },
}; 