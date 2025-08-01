import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useAppDispatch } from '../../store/hooks';
import { 
  Plus, 
  Search, 
  Filter, 
  SortAsc, 
  MoreVertical, 
  Edit, 
  Trash2, 
  CheckCircle, 
  Circle,
  Calendar,
  Tag,
  Clock,
  AlertTriangle,
  Download,
  Upload,
  Eye,
  EyeOff
} from 'lucide-react';
import type { RootState } from '../../store';
import { 
  fetchTasks, 
  createTask, 
  updateTask, 
  deleteTask, 
  updateTaskStatus,
  bulkUpdateTasks,
  bulkDeleteTasks,
  bulkUpdateStatus,
  searchTasks,
  setFilters,
  clearFilters,
  setSort,
  clearSort,
  setSearchQuery,
  clearSearchQuery,
  selectTask,
  deselectTask,
  selectAllTasks,
  deselectAllTasks
} from '../../store/slices/taskSlice';
import { fetchCategories } from '../../store/slices/categorySlice';
import { fetchTags } from '../../store/slices/tagSlice';
import type { Task, CreateTaskRequest, UpdateTaskRequest, SortOption, TaskFilters } from '../../types';
import Button from '../../components/common/Button';
import TaskModal from '../../components/tasks/TaskModal';
import TaskFiltersComponent from '../../components/tasks/TaskFilters';
import TaskSort from '../../components/tasks/TaskSort';
import BulkActions from '../../components/tasks/BulkActions';
import Pagination from '../../components/common/Pagination';
import { formatDate, getPriorityColor, getStatusIcon } from '../../utils/taskUtils';

const TasksPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { 
    tasks, 
    loading, 
    error, 
    selectedTasks, 
    pagination, 
    filters, 
    sort, 
    searchQuery 
  } = useSelector((state: RootState) => state.tasks);
  const { categories } = useSelector((state: RootState) => state.categories);
  const { tags } = useSelector((state: RootState) => state.tags);

  const [showTaskModal, setShowTaskModal] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [showFilters, setShowFilters] = useState(false);
  const [showSort, setShowSort] = useState(false);
  const [viewMode, setViewMode] = useState<'list' | 'grid'>('list');
  const [showCompleted, setShowCompleted] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    console.log('TasksPage: Component mounted, resetting state');
    // Reset task state when entering the tasks page
    dispatch(clearFilters());
    dispatch(clearSort());
    dispatch(clearSearchQuery());
    dispatch(deselectAllTasks());
    
    dispatch(fetchCategories());
    dispatch(fetchTags());
    
    // Mark as initialized
    setIsInitialized(true);
  }, [dispatch]);

  // Cleanup effect when component unmounts
  useEffect(() => {
    return () => {
      // Clean up when leaving the tasks page
      dispatch(deselectAllTasks());
    };
  }, [dispatch]);

  useEffect(() => {
    // Only fetch tasks after the component is properly initialized
    if (!isInitialized) return;
    
    // Clear any existing search query when entering the tasks page
    if (searchQuery) {
      dispatch(clearSearchQuery());
    }
    
    const params = {
      filters: showCompleted ? filters : { ...filters, status: 'pending' as const },
      sort: sort || undefined,
      page: currentPage,
    };
    
    // Force a fresh fetch for the tasks page to ensure we get the latest data
    // and don't rely on cached state from the dashboard
    dispatch(fetchTasks(params));
  }, [dispatch, filters, sort, currentPage, showCompleted, searchQuery, isInitialized]);

  const handleCreateTask = async (taskData: CreateTaskRequest | UpdateTaskRequest) => {
    try {
      if ('title' in taskData && taskData.title) {
        await dispatch(createTask(taskData as CreateTaskRequest)).unwrap();
        setShowTaskModal(false);
      }
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const handleUpdateTask = async (taskData: CreateTaskRequest | UpdateTaskRequest) => {
    if (!editingTask) return;
    try {
      if ('title' in taskData) {
        await dispatch(updateTask({ id: editingTask.id, taskData: taskData as UpdateTaskRequest })).unwrap();
        setShowTaskModal(false);
        setEditingTask(null);
      }
    } catch (error) {
      console.error('Failed to update task:', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await dispatch(deleteTask(taskId)).unwrap();
      } catch (error) {
        console.error('Failed to delete task:', error);
      }
    }
  };

  const handleStatusToggle = async (taskId: number, currentStatus: string) => {
    const newStatus = currentStatus === 'completed' ? 'pending' : 'completed';
    try {
      await dispatch(updateTaskStatus({ id: taskId, status: newStatus })).unwrap();
    } catch (error) {
      console.error('Failed to update task status:', error);
    }
  };

  const handleBulkStatusUpdate = async (status: 'pending' | 'completed') => {
    if (selectedTasks.length === 0) return;
    try {
      await dispatch(bulkUpdateStatus({ taskIds: selectedTasks, status })).unwrap();
      dispatch(deselectAllTasks());
    } catch (error) {
      console.error('Failed to update bulk status:', error);
    }
  };

  const handleBulkDelete = async () => {
    if (selectedTasks.length === 0) return;
    if (window.confirm(`Are you sure you want to delete ${selectedTasks.length} tasks?`)) {
      try {
        await dispatch(bulkDeleteTasks(selectedTasks)).unwrap();
        dispatch(deselectAllTasks());
      } catch (error) {
        console.error('Failed to delete tasks:', error);
      }
    }
  };

  const handleSearch = (query: string) => {
    dispatch(setSearchQuery(query));
    if (query.trim()) {
      dispatch(searchTasks({ query, filters }));
    } else {
      dispatch(clearSearchQuery());
    }
  };

  const handleFilterChange = (newFilters: Partial<TaskFilters>) => {
    dispatch(setFilters(newFilters));
    setCurrentPage(1);
  };

  const handleSortChange = (newSort: SortOption) => {
    dispatch(setSort(newSort));
    setCurrentPage(1);
  };

  const handleClearFilters = () => {
    dispatch(clearFilters());
    dispatch(clearSort());
    dispatch(clearSearchQuery());
    setCurrentPage(1);
  };

  const handleTaskSelect = (taskId: number) => {
    if (selectedTasks.includes(taskId)) {
      dispatch(deselectTask(taskId));
    } else {
      dispatch(selectTask(taskId));
    }
  };

  const handleSelectAll = () => {
    if (selectedTasks.length === tasks.length) {
      dispatch(deselectAllTasks());
    } else {
      dispatch(selectAllTasks());
    }
  };

  const openEditModal = (task: Task) => {
    setEditingTask(task);
    setShowTaskModal(true);
  };

  const closeTaskModal = () => {
    setShowTaskModal(false);
    setEditingTask(null);
  };

  const TaskCard: React.FC<{ task: Task }> = ({ task }) => (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex items-start space-x-3 flex-1">
          <input
            type="checkbox"
            checked={selectedTasks.includes(task.id)}
            onChange={() => handleTaskSelect(task.id)}
            className="mt-1 h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <button
            onClick={() => handleStatusToggle(task.id, task.status)}
            className="mt-1 text-gray-400 hover:text-primary-600 transition-colors"
          >
            {getStatusIcon(task.status, 'h-5 w-5')}
          </button>
          <div className="flex-1 min-w-0">
            <h3 className={`text-sm font-medium text-gray-900 truncate ${
              task.status === 'completed' ? 'line-through text-gray-500' : ''
            }`}>
              {task.title}
            </h3>
            {task.description && (
              <p className="text-xs text-gray-500 mt-1 line-clamp-2">
                {task.description}
              </p>
            )}
            <div className="flex items-center space-x-2 mt-2">
              {task.category && (
                <span 
                  className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                  style={{ backgroundColor: `${task.category.color}20`, color: task.category.color }}
                >
                  {task.category.name}
                </span>
              )}
              <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(task.priority)}`}>
                {task.priority}
              </span>
              {task.dueDate && (
                <span className="inline-flex items-center text-xs text-gray-500">
                  <Calendar className="h-3 w-3 mr-1" />
                  {formatDate(task.dueDate)}
                </span>
              )}
            </div>
            {task.tags && task.tags.length > 0 && (
              <div className="flex items-center space-x-1 mt-2">
                {task.tags.slice(0, 3).map((tag) => (
                  <span key={tag.id} className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-700">
                    #{tag.name}
                  </span>
                ))}
                {task.tags.length > 3 && (
                  <span className="text-xs text-gray-500">+{task.tags.length - 3}</span>
                )}
              </div>
            )}
          </div>
        </div>
        <div className="flex items-center space-x-1">
          <button
            onClick={() => openEditModal(task)}
            className="p-1 text-gray-400 hover:text-primary-600 transition-colors"
          >
            <Edit className="h-4 w-4" />
          </button>
          <button
            onClick={() => handleDeleteTask(task.id)}
            className="p-1 text-gray-400 hover:text-red-600 transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );

  const TaskRow: React.FC<{ task: Task }> = ({ task }) => (
    <div className="bg-white border-b border-gray-200 hover:bg-gray-50 transition-colors">
      <div className="flex items-center space-x-4 px-6 py-4">
        <input
          type="checkbox"
          checked={selectedTasks.includes(task.id)}
          onChange={() => handleTaskSelect(task.id)}
          className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
        />
        <button
          onClick={() => handleStatusToggle(task.id, task.status)}
          className="text-gray-400 hover:text-primary-600 transition-colors"
        >
          {getStatusIcon(task.status, 'h-5 w-5')}
        </button>
        <div className="flex-1 min-w-0">
          <h3 className={`text-sm font-medium text-gray-900 truncate ${
            task.status === 'completed' ? 'line-through text-gray-500' : ''
          }`}>
            {task.title}
          </h3>
          {task.description && (
            <p className="text-xs text-gray-500 mt-1 truncate">
              {task.description}
            </p>
          )}
        </div>
        <div className="flex items-center space-x-4 text-sm text-gray-500">
          {task.category && (
            <span 
              className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
              style={{ backgroundColor: `${task.category.color}20`, color: task.category.color }}
            >
              {task.category.name}
            </span>
          )}
          <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(task.priority)}`}>
            {task.priority}
          </span>
          {task.dueDate && (
            <span className="inline-flex items-center">
              <Calendar className="h-4 w-4 mr-1" />
              {formatDate(task.dueDate)}
            </span>
          )}
          <div className="flex items-center space-x-1">
            <button
              onClick={() => openEditModal(task)}
              className="p-1 text-gray-400 hover:text-primary-600 transition-colors"
            >
              <Edit className="h-4 w-4" />
            </button>
            <button
              onClick={() => handleDeleteTask(task.id)}
              className="p-1 text-gray-400 hover:text-red-600 transition-colors"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  if (loading && tasks.length === 0) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">All Tasks</h1>
          <p className="text-gray-600">Manage and organize your tasks</p>
        </div>
        <div className="flex items-center space-x-3">
          <Button
            variant="outline"
            size="sm"
            icon={<Download className="h-4 w-4" />}
          >
            Export
          </Button>
          <Button
            variant="outline"
            size="sm"
            icon={<Upload className="h-4 w-4" />}
          >
            Import
          </Button>
          <Button
            variant="primary"
            icon={<Plus className="h-4 w-4" />}
            onClick={() => setShowTaskModal(true)}
          >
            Add Task
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="flex items-center space-x-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search tasks..."
              value={searchQuery}
              onChange={(e) => handleSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
          <Button
            variant="outline"
            size="sm"
            icon={<Filter className="h-4 w-4" />}
            onClick={() => setShowFilters(!showFilters)}
          >
            Filters
          </Button>
          <Button
            variant="outline"
            size="sm"
            icon={<SortAsc className="h-4 w-4" />}
            onClick={() => setShowSort(!showSort)}
          >
            Sort
          </Button>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 rounded ${viewMode === 'list' ? 'bg-primary-100 text-primary-600' : 'text-gray-400'}`}
            >
              <Eye className="h-4 w-4" />
            </button>
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 rounded ${viewMode === 'grid' ? 'bg-primary-100 text-primary-600' : 'text-gray-400'}`}
            >
              <EyeOff className="h-4 w-4" />
            </button>
          </div>
          <label className="flex items-center space-x-2">
            <input
              type="checkbox"
              checked={showCompleted}
              onChange={(e) => setShowCompleted(e.target.checked)}
              className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
            />
            <span className="text-sm text-gray-600">Show completed</span>
          </label>
        </div>

        {/* Filters Panel */}
        {showFilters && (
          <TaskFiltersComponent
            filters={filters}
            categories={categories}
            tags={tags}
            onFilterChange={handleFilterChange}
            onClearFilters={handleClearFilters}
          />
        )}

        {/* Sort Panel */}
        {showSort && (
          <TaskSort
            sort={sort}
            onSortChange={handleSortChange}
            onClearSort={() => dispatch(clearSort())}
          />
        )}
      </div>

      {/* Bulk Actions */}
      {selectedTasks.length > 0 && (
        <BulkActions
          selectedCount={selectedTasks.length}
          onMarkComplete={() => handleBulkStatusUpdate('completed')}
          onMarkPending={() => handleBulkStatusUpdate('pending')}
          onDelete={handleBulkDelete}
          onSelectAll={handleSelectAll}
          onDeselectAll={() => dispatch(deselectAllTasks())}
        />
      )}

      {/* Tasks List */}
      <div className="bg-white rounded-lg shadow">
        {viewMode === 'list' && (
          <div className="overflow-hidden">
            <div className="bg-gray-50 px-6 py-3 border-b border-gray-200">
              <div className="flex items-center space-x-4">
                <input
                  type="checkbox"
                  checked={selectedTasks.length === tasks.length && tasks.length > 0}
                  onChange={handleSelectAll}
                  className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <span className="text-sm font-medium text-gray-700">Task</span>
                <span className="text-sm font-medium text-gray-700">Category</span>
                <span className="text-sm font-medium text-gray-700">Priority</span>
                <span className="text-sm font-medium text-gray-700">Due Date</span>
                <span className="text-sm font-medium text-gray-700">Actions</span>
              </div>
            </div>
            <div className="divide-y divide-gray-200">
              {tasks.map((task) => (
                <TaskRow key={task.id} task={task} />
              ))}
            </div>
          </div>
        )}

        {viewMode === 'grid' && (
          <div className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {tasks.map((task) => (
                <TaskCard key={task.id} task={task} />
              ))}
            </div>
          </div>
        )}

        {tasks.length === 0 && !loading && (
          <div className="text-center py-12">
            <div className="text-gray-400 mb-4">
              <CheckCircle className="h-12 w-12 mx-auto" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No tasks found</h3>
            <p className="text-gray-500 mb-4">
              {searchQuery || Object.keys(filters).length > 0
                ? 'Try adjusting your search or filters'
                : 'Get started by creating your first task'}
            </p>
            {!searchQuery && Object.keys(filters).length === 0 && (
              <Button
                variant="primary"
                icon={<Plus className="h-4 w-4" />}
                onClick={() => setShowTaskModal(true)}
              >
                Create your first task
              </Button>
            )}
          </div>
        )}
      </div>

      {/* Pagination */}
      {pagination && pagination.totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={pagination.totalPages}
          onPageChange={setCurrentPage}
        />
      )}

      {/* Task Modal */}
      {showTaskModal && (
        <TaskModal
          isOpen={showTaskModal}
          onClose={closeTaskModal}
          task={editingTask}
          categories={categories}
          tags={tags}
          onSubmit={editingTask ? handleUpdateTask : handleCreateTask}
        />
      )}
    </div>
  );
};

export default TasksPage; 