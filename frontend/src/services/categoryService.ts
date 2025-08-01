import { apiService } from './api';
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../types';

export const categoryService = {
  // Get all categories
  getCategories: async (): Promise<Category[]> => {
    const response = await apiService.get<Category[]>('/categories');
    return response;
  },

  // Get category by ID
  getCategory: async (id: number): Promise<Category> => {
    const response = await apiService.get<Category>(`/categories/${id}`);
    return response;
  },

  // Create new category
  createCategory: async (categoryData: CreateCategoryRequest): Promise<Category> => {
    const response = await apiService.post<Category>('/categories', categoryData);
    return response;
  },

  // Update category
  updateCategory: async (id: number, categoryData: UpdateCategoryRequest): Promise<Category> => {
    const response = await apiService.put<Category>(`/categories/${id}`, categoryData);
    return response;
  },

  // Delete category
  deleteCategory: async (id: number): Promise<void> => {
    await apiService.delete(`/categories/${id}`);
  },
}; 