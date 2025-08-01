import { apiService } from './api';
import type { Tag, CreateTagRequest, UpdateTagRequest } from '../types';

export const tagService = {
  // Get all tags
  getTags: async (): Promise<Tag[]> => {
    const response = await apiService.get<Tag[]>('/tags');
    return response;
  },

  // Get tag by ID
  getTag: async (id: number): Promise<Tag> => {
    const response = await apiService.get<Tag>(`/tags/${id}`);
    return response;
  },

  // Create new tag
  createTag: async (tagData: CreateTagRequest): Promise<Tag> => {
    const response = await apiService.post<Tag>('/tags', tagData);
    return response;
  },

  // Update tag
  updateTag: async (id: number, tagData: UpdateTagRequest): Promise<Tag> => {
    const response = await apiService.put<Tag>(`/tags/${id}`, tagData);
    return response;
  },

  // Delete tag
  deleteTag: async (id: number): Promise<void> => {
    await apiService.delete(`/tags/${id}`);
  },
}; 