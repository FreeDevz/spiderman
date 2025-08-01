import React from 'react';
import { X, ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-react';
import type { SortOption } from '../../types';
import Button from '../common/Button';

interface TaskSortProps {
  sort: SortOption | null;
  onSortChange: (sort: SortOption) => void;
  onClearSort: () => void;
}

const TaskSort: React.FC<TaskSortProps> = ({
  sort,
  onSortChange,
  onClearSort,
}) => {
  const sortOptions = [
    { field: 'createdAt', label: 'Created Date' },
    { field: 'updatedAt', label: 'Updated Date' },
    { field: 'dueDate', label: 'Due Date' },
    { field: 'priority', label: 'Priority' },
    { field: 'title', label: 'Title' },
  ];

  const getSortIcon = (field: string) => {
    if (!sort || sort.field !== field) {
      return <ArrowUpDown className="h-4 w-4" />;
    }
    return sort.direction === 'asc' ? <ArrowUp className="h-4 w-4" /> : <ArrowDown className="h-4 w-4" />;
  };

  const handleSortClick = (field: string) => {
    if (sort && sort.field === field) {
      // Toggle direction if same field
      onSortChange({
        field: field as any,
        direction: sort.direction === 'asc' ? 'desc' : 'asc',
      });
    } else {
      // Set new field with default ascending direction
      onSortChange({
        field: field as any,
        direction: 'asc',
      });
    }
  };

  return (
    <div className="mt-4 p-4 bg-gray-50 rounded-lg">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-gray-900">Sort By</h3>
        {sort && (
          <Button
            variant="outline"
            size="sm"
            onClick={onClearSort}
            icon={<X className="h-4 w-4" />}
          >
            Clear Sort
          </Button>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
        {sortOptions.map((option) => (
          <button
            key={option.field}
            onClick={() => handleSortClick(option.field)}
            className={`flex items-center justify-between px-3 py-2 rounded-lg border transition-colors ${
              sort && sort.field === option.field
                ? 'border-primary-500 bg-primary-50 text-primary-700'
                : 'border-gray-300 bg-white text-gray-700 hover:border-gray-400 hover:bg-gray-50'
            }`}
          >
            <span className="text-sm font-medium">{option.label}</span>
            {getSortIcon(option.field)}
          </button>
        ))}
      </div>

      {/* Current Sort Display */}
      {sort && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <h4 className="text-sm font-medium text-gray-700 mb-2">Current Sort:</h4>
          <div className="flex items-center space-x-2">
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-primary-100 text-primary-800">
              {sortOptions.find(opt => opt.field === sort.field)?.label} 
              ({sort.direction === 'asc' ? 'A-Z' : 'Z-A'})
            </span>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskSort; 