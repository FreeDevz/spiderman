// User types
export interface User {
  id: number;
  email: string;
  name: string;
  avatarUrl?: string;
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserSettings {
  id: number;
  userId: number;
  theme: 'light' | 'dark' | 'auto';
  notificationsEnabled: boolean;
  timezone: string;
  createdAt: string;
  updatedAt: string;
}

// Task types
export interface Task {
  id: number;
  userId: number;
  categoryId?: number;
  title: string;
  description?: string;
  status: 'pending' | 'completed' | 'deleted';
  priority: 'low' | 'medium' | 'high';
  dueDate?: string;
  completedAt?: string;
  createdAt: string;
  updatedAt: string;
  category?: Category;
  tags?: Tag[];
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  priority?: 'low' | 'medium' | 'high';
  dueDate?: string;
  categoryId?: number;
  tags?: string[];
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  priority?: 'low' | 'medium' | 'high';
  dueDate?: string;
  categoryId?: number;
  tags?: string[];
}

// Category types
export interface Category {
  id: number;
  userId: number;
  name: string;
  color: string;
  createdAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  color: string;
  description?: string;
}

export interface UpdateCategoryRequest {
  name?: string;
  color?: string;
  description?: string;
}

// Tag types
export interface Tag {
  id: number;
  userId: number;
  name: string;
  createdAt: string;
}

export interface CreateTagRequest {
  name: string;
  color?: string;
}

export interface UpdateTagRequest {
  name?: string;
  color?: string;
}

// Authentication types
export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
  confirmPassword: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}

// Dashboard types
export interface DashboardStatistics {
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  overdueTasks: number;
  todayTasks: number;
  upcomingTasks: number;
  completionRate: number;
}

export interface RecentActivity {
  id: number;
  type: 'task_created' | 'task_completed' | 'task_updated' | 'category_created' | 'tag_added';
  description: string;
  timestamp: string;
  metadata?: Record<string, any>;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  pagination?: PaginationInfo;
}

export interface PaginationInfo {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

export interface ErrorResponse {
  success: false;
  message: string;
  errors?: Record<string, string[]>;
}

// UI types
export interface TaskFilters {
  status?: 'all' | 'pending' | 'completed';
  priority?: 'all' | 'low' | 'medium' | 'high';
  categoryId?: number;
  tags?: string[];
  search?: string;
  dueDate?: 'all' | 'today' | 'tomorrow' | 'week' | 'overdue';
}

export interface SortOption {
  field: 'createdAt' | 'updatedAt' | 'dueDate' | 'priority' | 'title';
  direction: 'asc' | 'desc';
}

// Notification types
export interface Notification {
  id: number;
  userId: number;
  type: 'due_reminder' | 'task_completed' | 'achievement' | 'system';
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  scheduledFor?: string;
  metadata?: Record<string, any>;
}

export interface NotificationSettings {
  emailNotifications: boolean;
  browserPushNotifications: boolean;
  dueDateReminders: boolean;
  reminderTime: '1_hour' | '1_day' | '1_week';
  taskCompletionCelebrations: boolean;
  weeklyProgressSummary: boolean;
} 