import { apiService } from './api';
import type { 
  Task, 
  CreateTaskRequest, 
  UpdateTaskRequest, 
  TaskFilters, 
  SortOption,
  PaginationInfo 
} from '../types';
import { DEFAULT_PAGE_SIZE } from '../constants';

export const taskService = {
  // Get all tasks with filters and pagination
  getTasks: async (
    filters?: TaskFilters,
    sort?: SortOption,
    page: number = 1,
    limit: number = DEFAULT_PAGE_SIZE
  ): Promise<{ tasks: Task[]; pagination: PaginationInfo }> => {
    const params = {
      page,
      limit,
      ...filters,
      sortBy: sort?.field,
      sortDirection: sort?.direction,
    };
    
    const response = await apiService.get<{ tasks: Task[]; pagination: PaginationInfo }>('/tasks', params);
    return response;
  },

  // Get task by ID
  getTask: async (id: number): Promise<Task> => {
    const response = await apiService.get<Task>(`/tasks/${id}`);
    return response;
  },

  // Create new task
  createTask: async (taskData: CreateTaskRequest): Promise<Task> => {
    const response = await apiService.post<Task>('/tasks', taskData);
    return response;
  },

  // Update task
  updateTask: async (id: number, taskData: UpdateTaskRequest): Promise<Task> => {
    const response = await apiService.put<Task>(`/tasks/${id}`, taskData);
    return response;
  },

  // Delete task
  deleteTask: async (id: number): Promise<void> => {
    await apiService.delete(`/tasks/${id}`);
  },

  // Update task status
  updateTaskStatus: async (id: number, status: 'pending' | 'completed'): Promise<Task> => {
    const response = await apiService.patch<Task>(`/tasks/${id}/status`, { status });
    return response;
  },

  // Bulk operations
  bulkUpdateTasks: async (taskIds: number[], updates: Partial<UpdateTaskRequest>): Promise<Task[]> => {
    const response = await apiService.post<Task[]>('/tasks/bulk', {
      taskIds,
      updates,
    });
    return response;
  },

  bulkDeleteTasks: async (taskIds: number[]): Promise<void> => {
    await apiService.post('/tasks/bulk/delete', { taskIds });
  },

  bulkUpdateStatus: async (taskIds: number[], status: 'pending' | 'completed'): Promise<Task[]> => {
    const response = await apiService.post<Task[]>('/tasks/bulk/status', {
      taskIds,
      status,
    });
    return response;
  },

  // Export tasks
  exportTasks: async (format: 'json' | 'csv' = 'json'): Promise<Blob> => {
    const response = await apiService.get(`/tasks/export?format=${format}`, {}, {
      responseType: 'blob',
    });
    return response as Blob;
  },

  // Import tasks
  importTasks: async (file: File): Promise<{ imported: number; errors: string[] }> => {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await apiService.post<{ imported: number; errors: string[] }>('/tasks/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response;
  },

  // Dashboard specific endpoints
  getTodayTasks: async (): Promise<Task[]> => {
    const response = await apiService.get<Task[]>('/dashboard/today');
    return response;
  },

  getUpcomingTasks: async (): Promise<Task[]> => {
    const response = await apiService.get<Task[]>('/dashboard/upcoming');
    return response;
  },

  getOverdueTasks: async (): Promise<Task[]> => {
    const response = await apiService.get<Task[]>('/dashboard/overdue');
    return response;
  },

  getCompletedTasks: async (page: number = 1, limit: number = DEFAULT_PAGE_SIZE): Promise<{ tasks: Task[]; pagination: PaginationInfo }> => {
    const response = await apiService.get<{ tasks: Task[]; pagination: PaginationInfo }>('/tasks', {
      status: 'completed',
      page,
      limit,
    });
    return response;
  },

  // Search tasks
  searchTasks: async (query: string, filters?: TaskFilters): Promise<Task[]> => {
    const response = await apiService.get<Task[]>('/tasks/search', {
      q: query,
      ...filters,
    });
    return response;
  },

  // Get tasks by category
  getTasksByCategory: async (categoryId: number, page: number = 1, limit: number = DEFAULT_PAGE_SIZE): Promise<{ tasks: Task[]; pagination: PaginationInfo }> => {
    const response = await apiService.get<{ tasks: Task[]; pagination: PaginationInfo }>('/tasks', {
      categoryId,
      page,
      limit,
    });
    return response;
  },

  // Get tasks by tag
  getTasksByTag: async (tagName: string, page: number = 1, limit: number = DEFAULT_PAGE_SIZE): Promise<{ tasks: Task[]; pagination: PaginationInfo }> => {
    const response = await apiService.get<{ tasks: Task[]; pagination: PaginationInfo }>('/tasks', {
      tags: [tagName],
      page,
      limit,
    });
    return response;
  },
}; 