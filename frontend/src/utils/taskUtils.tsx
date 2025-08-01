import React from 'react';
import { CheckCircle, Circle, Clock, AlertTriangle } from 'lucide-react';

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = date.getTime() - now.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 0) {
    return 'Today';
  } else if (diffDays === 1) {
    return 'Tomorrow';
  } else if (diffDays === -1) {
    return 'Yesterday';
  } else if (diffDays > 0 && diffDays <= 7) {
    return date.toLocaleDateString('en-US', { weekday: 'long' });
  } else {
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined
    });
  }
};

export const getPriorityColor = (priority: string): string => {
  switch (priority.toLowerCase()) {
    case 'high':
      return 'bg-red-100 text-red-800';
    case 'medium':
      return 'bg-yellow-100 text-yellow-800';
    case 'low':
      return 'bg-green-100 text-green-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getStatusIcon = (status: string, className: string = 'h-5 w-5') => {
  switch (status) {
    case 'completed':
      return <CheckCircle className={`${className} text-green-600`} />;
    case 'pending':
      return <Circle className={`${className} text-gray-400`} />;
    default:
      return <Circle className={`${className} text-gray-400`} />;
  }
};

export const getDueDateStatus = (dueDate?: string): 'overdue' | 'due-soon' | 'on-time' | 'no-due-date' => {
  if (!dueDate) return 'no-due-date';
  
  const now = new Date();
  const due = new Date(dueDate);
  const diffTime = due.getTime() - now.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays < 0) return 'overdue';
  if (diffDays <= 1) return 'due-soon';
  return 'on-time';
};

export const getDueDateIcon = (dueDate?: string) => {
  const status = getDueDateStatus(dueDate);
  
  switch (status) {
    case 'overdue':
      return <AlertTriangle className="h-4 w-4 text-red-500" />;
    case 'due-soon':
      return <Clock className="h-4 w-4 text-yellow-500" />;
    case 'on-time':
      return <Clock className="h-4 w-4 text-green-500" />;
    default:
      return null;
  }
};

export const getDueDateText = (dueDate?: string): string => {
  const status = getDueDateStatus(dueDate);
  
  switch (status) {
    case 'overdue':
      return 'Overdue';
    case 'due-soon':
      return 'Due soon';
    case 'on-time':
      return formatDate(dueDate!);
    default:
      return 'No due date';
  }
};

export const getDueDateColor = (dueDate?: string): string => {
  const status = getDueDateStatus(dueDate);
  
  switch (status) {
    case 'overdue':
      return 'text-red-600';
    case 'due-soon':
      return 'text-yellow-600';
    case 'on-time':
      return 'text-green-600';
    default:
      return 'text-gray-500';
  }
};

export const sortTasks = (tasks: any[], sortBy: string, sortDirection: 'asc' | 'desc' = 'asc') => {
  return [...tasks].sort((a, b) => {
    let aValue: any;
    let bValue: any;

    switch (sortBy) {
      case 'title':
        aValue = a.title.toLowerCase();
        bValue = b.title.toLowerCase();
        break;
      case 'priority':
        const priorityOrder = { high: 3, medium: 2, low: 1 };
        aValue = priorityOrder[a.priority as keyof typeof priorityOrder] || 0;
        bValue = priorityOrder[b.priority as keyof typeof priorityOrder] || 0;
        break;
      case 'dueDate':
        aValue = a.dueDate ? new Date(a.dueDate).getTime() : 0;
        bValue = b.dueDate ? new Date(b.dueDate).getTime() : 0;
        break;
      case 'createdAt':
        aValue = new Date(a.createdAt).getTime();
        bValue = new Date(b.createdAt).getTime();
        break;
      case 'updatedAt':
        aValue = new Date(a.updatedAt).getTime();
        bValue = new Date(b.updatedAt).getTime();
        break;
      default:
        aValue = a[sortBy];
        bValue = b[sortBy];
    }

    if (sortDirection === 'asc') {
      return aValue > bValue ? 1 : -1;
    } else {
      return aValue < bValue ? 1 : -1;
    }
  });
};

export const filterTasks = (tasks: any[], filters: any) => {
  return tasks.filter(task => {
    // Status filter
    if (filters.status && filters.status !== 'all' && task.status !== filters.status) {
      return false;
    }

    // Priority filter
    if (filters.priority && filters.priority !== 'all' && task.priority !== filters.priority) {
      return false;
    }

    // Category filter
    if (filters.categoryId && task.categoryId !== filters.categoryId) {
      return false;
    }

    // Tags filter
    if (filters.tags && filters.tags.length > 0) {
      const taskTags = task.tags?.map((tag: any) => tag.name) || [];
      const hasMatchingTag = filters.tags.some((tag: string) => taskTags.includes(tag));
      if (!hasMatchingTag) {
        return false;
      }
    }

    // Due date filter
    if (filters.dueDate && filters.dueDate !== 'all') {
      const now = new Date();
      const taskDueDate = task.dueDate ? new Date(task.dueDate) : null;
      
      switch (filters.dueDate) {
        case 'today':
          if (!taskDueDate || taskDueDate.toDateString() !== now.toDateString()) {
            return false;
          }
          break;
        case 'tomorrow':
          const tomorrow = new Date(now);
          tomorrow.setDate(tomorrow.getDate() + 1);
          if (!taskDueDate || taskDueDate.toDateString() !== tomorrow.toDateString()) {
            return false;
          }
          break;
        case 'week':
          const weekFromNow = new Date(now);
          weekFromNow.setDate(weekFromNow.getDate() + 7);
          if (!taskDueDate || taskDueDate > weekFromNow) {
            return false;
          }
          break;
        case 'overdue':
          if (!taskDueDate || taskDueDate >= now) {
            return false;
          }
          break;
      }
    }

    // Search filter
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      const matchesTitle = task.title.toLowerCase().includes(searchTerm);
      const matchesDescription = task.description?.toLowerCase().includes(searchTerm) || false;
      if (!matchesTitle && !matchesDescription) {
        return false;
      }
    }

    return true;
  });
}; 