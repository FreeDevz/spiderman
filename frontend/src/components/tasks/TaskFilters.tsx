import React from 'react';
import { X } from 'lucide-react';
import type { TaskFilters, Category, Tag } from '../../types';
import Button from '../common/Button';

interface TaskFiltersProps {
  filters: TaskFilters;
  categories: Category[];
  tags: Tag[];
  onFilterChange: (filters: Partial<TaskFilters>) => void;
  onClearFilters: () => void;
}

const TaskFilters: React.FC<TaskFiltersProps> = ({
  filters,
  categories,
  tags,
  onFilterChange,
  onClearFilters,
}) => {
  const hasActiveFilters = Object.keys(filters).some(key => {
    const value = filters[key as keyof TaskFilters];
    return value && value !== 'all' && (Array.isArray(value) ? value.length > 0 : true);
  });

  return (
    <div className="mt-4 p-4 bg-gray-50 rounded-lg">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-gray-900">Filters</h3>
        {hasActiveFilters && (
          <Button
            variant="outline"
            size="sm"
            onClick={onClearFilters}
            icon={<X className="h-4 w-4" />}
          >
            Clear All
          </Button>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Status Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Status
          </label>
          <select
            value={filters.status || 'all'}
            onChange={(e) => onFilterChange({ status: e.target.value as any })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="all">All Status</option>
            <option value="pending">Pending</option>
            <option value="completed">Completed</option>
          </select>
        </div>

        {/* Priority Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Priority
          </label>
          <select
            value={filters.priority || 'all'}
            onChange={(e) => onFilterChange({ priority: e.target.value as any })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="all">All Priorities</option>
            <option value="high">High</option>
            <option value="medium">Medium</option>
            <option value="low">Low</option>
          </select>
        </div>

        {/* Category Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Category
          </label>
          <select
            value={filters.categoryId || ''}
            onChange={(e) => onFilterChange({ categoryId: e.target.value ? Number(e.target.value) : undefined })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">All Categories</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </div>

        {/* Due Date Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Due Date
          </label>
          <select
            value={filters.dueDate || 'all'}
            onChange={(e) => onFilterChange({ dueDate: e.target.value as any })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="all">All Dates</option>
            <option value="today">Today</option>
            <option value="tomorrow">Tomorrow</option>
            <option value="week">This Week</option>
            <option value="overdue">Overdue</option>
          </select>
        </div>
      </div>

      {/* Tags Filter */}
      <div className="mt-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Tags
        </label>
        <div className="flex flex-wrap gap-2">
          {tags.map((tag) => (
            <button
              key={tag.id}
              onClick={() => {
                const currentTags = filters.tags || [];
                const newTags = currentTags.includes(tag.name)
                  ? currentTags.filter(t => t !== tag.name)
                  : [...currentTags, tag.name];
                onFilterChange({ tags: newTags });
              }}
              className={`px-3 py-1 rounded-full text-sm font-medium transition-colors ${
                filters.tags?.includes(tag.name)
                  ? 'bg-primary-100 text-primary-800'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              #{tag.name}
            </button>
          ))}
        </div>
      </div>

      {/* Active Filters Display */}
      {hasActiveFilters && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <h4 className="text-sm font-medium text-gray-700 mb-2">Active Filters:</h4>
          <div className="flex flex-wrap gap-2">
            {filters.status && filters.status !== 'all' && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-800">
                Status: {filters.status}
                <button
                  onClick={() => onFilterChange({ status: undefined })}
                  className="ml-1 text-blue-600 hover:text-blue-800"
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            )}
            {filters.priority && filters.priority !== 'all' && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-green-100 text-green-800">
                Priority: {filters.priority}
                <button
                  onClick={() => onFilterChange({ priority: undefined })}
                  className="ml-1 text-green-600 hover:text-green-800"
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            )}
            {filters.categoryId && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-purple-100 text-purple-800">
                Category: {categories.find(c => c.id === filters.categoryId)?.name}
                <button
                  onClick={() => onFilterChange({ categoryId: undefined })}
                  className="ml-1 text-purple-600 hover:text-purple-800"
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            )}
            {filters.dueDate && filters.dueDate !== 'all' && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-yellow-100 text-yellow-800">
                Due: {filters.dueDate}
                <button
                  onClick={() => onFilterChange({ dueDate: undefined })}
                  className="ml-1 text-yellow-600 hover:text-yellow-800"
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            )}
            {filters.tags && filters.tags.length > 0 && (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-orange-100 text-orange-800">
                Tags: {filters.tags.join(', ')}
                <button
                  onClick={() => onFilterChange({ tags: undefined })}
                  className="ml-1 text-orange-600 hover:text-orange-800"
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskFilters; 