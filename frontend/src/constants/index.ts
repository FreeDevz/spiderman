// API Configuration
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'auth_token',
  REFRESH_TOKEN: 'refresh_token',
  USER: 'user',
  THEME: 'theme',
  NOTIFICATIONS: 'notifications_enabled',
} as const;

// Task Priorities
export const TASK_PRIORITIES = {
  LOW: 'low',
  MEDIUM: 'medium',
  HIGH: 'high',
} as const;

// Task Statuses
export const TASK_STATUSES = {
  PENDING: 'pending',
  COMPLETED: 'completed',
  DELETED: 'deleted',
} as const;

// Default Categories
export const DEFAULT_CATEGORIES = [
  { name: 'Personal', color: '#3B82F6' },
  { name: 'Work', color: '#EF4444' },
  { name: 'Shopping', color: '#10B981' },
  { name: 'Health', color: '#F59E0B' },
  { name: 'Learning', color: '#8B5CF6' },
] as const;

// Priority Colors
export const PRIORITY_COLORS = {
  low: '#10B981',
  medium: '#F59E0B',
  high: '#EF4444',
} as const;

// Priority Labels
export const PRIORITY_LABELS = {
  low: 'Low',
  medium: 'Medium',
  high: 'High',
} as const;

// Status Labels
export const STATUS_LABELS = {
  pending: 'Pending',
  completed: 'Completed',
  deleted: 'Deleted',
} as const;

// Pagination
export const DEFAULT_PAGE_SIZE = 20;
export const MAX_PAGE_SIZE = 100;

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  INPUT: 'yyyy-MM-dd',
  TIME: 'HH:mm',
  DATETIME: 'MMM dd, yyyy HH:mm',
  RELATIVE: 'relative',
} as const;

// Validation Rules
export const VALIDATION_RULES = {
  PASSWORD_MIN_LENGTH: 8,
  TASK_TITLE_MAX_LENGTH: 100,
  TASK_DESCRIPTION_MAX_LENGTH: 500,
  CATEGORY_NAME_MAX_LENGTH: 50,
  TAG_NAME_MAX_LENGTH: 30,
} as const;

// Animation Durations
export const ANIMATION_DURATIONS = {
  FAST: 150,
  NORMAL: 300,
  SLOW: 500,
} as const;

// Breakpoints
export const BREAKPOINTS = {
  MOBILE: 320,
  TABLET: 768,
  DESKTOP: 1024,
  WIDE: 1280,
} as const;

// Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  TASKS: '/tasks',
  CATEGORIES: '/categories',
  TAGS: '/tags',
  SETTINGS: '/settings',
  PROFILE: '/profile',
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  FORBIDDEN: 'Access denied.',
  NOT_FOUND: 'The requested resource was not found.',
  VALIDATION_ERROR: 'Please check your input and try again.',
  SERVER_ERROR: 'An unexpected error occurred. Please try again later.',
  INVALID_CREDENTIALS: 'Invalid email or password.',
  EMAIL_ALREADY_EXISTS: 'An account with this email already exists.',
  PASSWORDS_DONT_MATCH: 'Passwords do not match.',
  WEAK_PASSWORD: 'Password must be at least 8 characters long and contain mixed case, numbers, and special characters.',
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  TASK_CREATED: 'Task created successfully!',
  TASK_UPDATED: 'Task updated successfully!',
  TASK_DELETED: 'Task deleted successfully!',
  TASK_COMPLETED: 'Task marked as completed!',
  CATEGORY_CREATED: 'Category created successfully!',
  CATEGORY_UPDATED: 'Category updated successfully!',
  CATEGORY_DELETED: 'Category deleted successfully!',
  TAG_CREATED: 'Tag created successfully!',
  TAG_UPDATED: 'Tag updated successfully!',
  TAG_DELETED: 'Tag deleted successfully!',
  PROFILE_UPDATED: 'Profile updated successfully!',
  SETTINGS_UPDATED: 'Settings updated successfully!',
  LOGIN_SUCCESS: 'Welcome back!',
  REGISTER_SUCCESS: 'Account created successfully!',
  LOGOUT_SUCCESS: 'Logged out successfully!',
} as const;

// Icons
export const ICONS = {
  TASK: '📝',
  COMPLETED: '✅',
  PENDING: '⏰',
  OVERDUE: '🔥',
  CATEGORY: '📋',
  TAG: '🏷',
  SETTINGS: '⚙',
  PROFILE: '👤',
  LOGOUT: '🔓',
  ADD: '➕',
  EDIT: '✏',
  DELETE: '🗑',
  SEARCH: '🔍',
  FILTER: '🔽',
  SORT: '📊',
  CALENDAR: '📅',
  CLOCK: '🕐',
  BELL: '🔔',
  CHECK: '✓',
  CLOSE: '✕',
  ARROW_LEFT: '←',
  ARROW_RIGHT: '→',
  ARROW_UP: '↑',
  ARROW_DOWN: '↓',
} as const; 