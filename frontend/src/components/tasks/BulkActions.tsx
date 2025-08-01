import React from 'react';
import { CheckCircle, Circle, Trash2, X } from 'lucide-react';
import Button from '../common/Button';

interface BulkActionsProps {
  selectedCount: number;
  onMarkComplete: () => void;
  onMarkPending: () => void;
  onDelete: () => void;
  onSelectAll: () => void;
  onDeselectAll: () => void;
}

const BulkActions: React.FC<BulkActionsProps> = ({
  selectedCount,
  onMarkComplete,
  onMarkPending,
  onDelete,
  onSelectAll,
  onDeselectAll,
}) => {
  return (
    <div className="bg-primary-50 border border-primary-200 rounded-lg p-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <span className="text-sm font-medium text-primary-900">
            {selectedCount} task{selectedCount !== 1 ? 's' : ''} selected
          </span>
          
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              onClick={onMarkComplete}
              icon={<CheckCircle className="h-4 w-4" />}
            >
              Mark Complete
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={onMarkPending}
              icon={<Circle className="h-4 w-4" />}
            >
              Mark Pending
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={onDelete}
              icon={<Trash2 className="h-4 w-4" />}
              className="text-red-600 border-red-300 hover:bg-red-50"
            >
              Delete
            </Button>
          </div>
        </div>
        
        <div className="flex items-center space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={onSelectAll}
          >
            Select All
          </Button>
          
          <Button
            variant="outline"
            size="sm"
            onClick={onDeselectAll}
            icon={<X className="h-4 w-4" />}
          >
            Clear Selection
          </Button>
        </div>
      </div>
    </div>
  );
};

export default BulkActions; 