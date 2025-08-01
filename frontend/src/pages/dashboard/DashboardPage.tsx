import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useAppDispatch } from '../../store/hooks';
import { Plus, Calendar, Clock, AlertTriangle, CheckCircle, TrendingUp, CheckSquare } from 'lucide-react';
import type { RootState } from '../../store';
import { 
  fetchTasks,
  createTask,
  clearFilters,
  clearSort,
  clearSearchQuery,
  deselectAllTasks
} from '../../store/slices/taskSlice';
import { fetchCategories } from '../../store/slices/categorySlice';
import { fetchTags } from '../../store/slices/tagSlice';
import type { DashboardStatistics, Task, CreateTaskRequest, UpdateTaskRequest } from '../../types';
import Button from '../../components/common/Button';
import TaskModal from '../../components/tasks/TaskModal';
import { formatDate, getPriorityColor, getStatusIcon } from '../../utils/taskUtils';

const DashboardPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { user } = useSelector((state: RootState) => state.auth);
  const { tasks, loading } = useSelector((state: RootState) => state.tasks);
  const { categories } = useSelector((state: RootState) => state.categories);
  const { tags } = useSelector((state: RootState) => state.tags);

  const [statistics, setStatistics] = useState<DashboardStatistics>({
    totalTasks: 0,
    completedTasks: 0,
    pendingTasks: 0,
    overdueTasks: 0,
    todayTasks: 0,
    upcomingTasks: 0,
    completionRate: 0,
  });
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [quickAddTitle, setQuickAddTitle] = useState('');

    useEffect(() => {
    // Clear any existing task state to ensure clean dashboard view
    dispatch(clearFilters());
    dispatch(clearSort());
    dispatch(clearSearchQuery());
    dispatch(deselectAllTasks());
    
    // Fetch all tasks for dashboard statistics and overview
    dispatch(fetchTasks({}));
    dispatch(fetchCategories());
    dispatch(fetchTags());
  }, [dispatch]);

  useEffect(() => {
    // Calculate statistics from all tasks
    console.log('Calculating statistics for tasks:', tasks.length, tasks);
    const totalTasks = tasks.length;
    const completedTasks = tasks.filter(task => task.status === 'completed').length;
    const pendingTasks = tasks.filter(task => task.status === 'pending').length;
    const overdueTasks = tasks.filter(task => {
      if (task.status !== 'pending' || !task.dueDate) return false;
      return new Date(task.dueDate) < new Date();
    }).length;
    const todayTasks = tasks.filter(task => {
      if (task.status !== 'pending' || !task.dueDate) return false;
      const today = new Date().toDateString();
      return new Date(task.dueDate).toDateString() === today;
    }).length;
    const upcomingTasks = tasks.filter(task => {
      if (task.status !== 'pending' || !task.dueDate) return false;
      const today = new Date();
      const taskDate = new Date(task.dueDate);
      const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
      return taskDate > today && taskDate <= nextWeek;
    }).length;
    const completionRate = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;

    const newStatistics = {
      totalTasks,
      completedTasks,
      pendingTasks,
      overdueTasks,
      todayTasks,
      upcomingTasks,
      completionRate,
    };
    
    console.log('New statistics:', newStatistics);
    setStatistics(newStatistics);
  }, [tasks]);

  const handleQuickAdd = async () => {
    if (!quickAddTitle.trim()) return;

    try {
      console.log('Creating task:', quickAddTitle.trim());
      const result = await dispatch(createTask({
        title: quickAddTitle.trim(),
        priority: 'medium',
      })).unwrap();
      console.log('Task created successfully:', result);
      setQuickAddTitle('');
      
      // Force refresh the task list
      console.log('Refreshing task list...');
      await dispatch(fetchTasks({})).unwrap();
      console.log('Task list refreshed');
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const handleCreateTask = async (taskData: CreateTaskRequest | UpdateTaskRequest) => {
    try {
      if ('title' in taskData && taskData.title) {
        console.log('Creating task from modal:', taskData);
        const result = await dispatch(createTask(taskData as CreateTaskRequest)).unwrap();
        console.log('Task created successfully from modal:', result);
        setShowTaskModal(false);
        
        // Force refresh the task list
        console.log('Refreshing task list from modal...');
        await dispatch(fetchTasks({})).unwrap();
        console.log('Task list refreshed from modal');
      }
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const StatCard: React.FC<{ title: string; value: number; icon: React.ReactNode; color: string; trend?: number }> = ({
    title,
    value,
    icon,
    color,
    trend,
  }) => (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <div className={`p-3 rounded-full ${color}`}>
            {icon}
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-gray-600">{title}</p>
            <p className="text-2xl font-semibold text-gray-900">{value}</p>
          </div>
        </div>
        {trend !== undefined && (
          <div className={`flex items-center text-sm ${trend >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            <TrendingUp className={`h-4 w-4 mr-1 ${trend < 0 ? 'transform rotate-180' : ''}`} />
            {Math.abs(trend)}%
          </div>
        )}
      </div>
    </div>
  );

  const TaskItem: React.FC<{ task: Task; showCategory?: boolean }> = ({ task, showCategory = true }) => (
    <div className="flex items-center space-x-2 p-2 bg-gray-50 rounded-md hover:bg-gray-100 transition-colors">
      <div className="flex-1 min-w-0">
        <p className={`text-sm font-medium ${task.status === 'completed' ? 'line-through text-gray-500' : 'text-gray-900'}`}>
          {task.title}
        </p>
        <div className="flex items-center space-x-2 mt-1">
          {showCategory && task.category && (
            <span 
              className="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium"
              style={{ backgroundColor: `${task.category.color}20`, color: task.category.color }}
            >
              {task.category.name}
            </span>
          )}
          <span className={`inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium ${getPriorityColor(task.priority)}`}>
            {task.priority}
          </span>
          {task.dueDate && (
            <span className="text-xs text-gray-500">
              {formatDate(task.dueDate)}
            </span>
          )}
        </div>
      </div>
    </div>
  );

  if (loading && statistics.totalTasks === 0) {
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
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">Welcome back, {user?.name}!</p>
        </div>
        <Button
          variant="primary"
          icon={<Plus className="h-4 w-4" />}
          onClick={() => setShowTaskModal(true)}
        >
          Add Task
        </Button>
      </div>

      {/* Statistics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Tasks"
          value={statistics.totalTasks}
          icon={<CheckCircle className="h-6 w-6 text-white" />}
          color="bg-blue-500"
        />
        <StatCard
          title="Completed"
          value={statistics.completedTasks}
          icon={<CheckCircle className="h-6 w-6 text-white" />}
          color="bg-green-500"
          trend={statistics.completionRate}
        />
        <StatCard
          title="Pending"
          value={statistics.pendingTasks}
          icon={<Clock className="h-6 w-6 text-white" />}
          color="bg-yellow-500"
        />
        <StatCard
          title="Overdue"
          value={statistics.overdueTasks}
          icon={<AlertTriangle className="h-6 w-6 text-white" />}
          color="bg-red-500"
        />
      </div>

      {/* Quick Add Task */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Add Task</h2>
        <div className="flex space-x-4">
          <input
            type="text"
            value={quickAddTitle}
            onChange={(e) => setQuickAddTitle(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleQuickAdd()}
            placeholder="What needs to be done?"
            className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
          <Button 
            variant="primary" 
            onClick={handleQuickAdd}
            disabled={!quickAddTitle.trim()}
          >
            Add
          </Button>
        </div>
      </div>

      {/* Today's Tasks and Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Today's Tasks</h2>
            <span className="text-sm text-gray-500">{statistics.todayTasks} tasks</span>
          </div>
          
          {statistics.todayTasks === 0 ? (
            <div className="text-center py-8">
              <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">No tasks for today</p>
            </div>
          ) : (
            <div className="space-y-2">
              {tasks.filter(task => {
                if (task.status !== 'pending' || !task.dueDate) return false;
                const today = new Date().toDateString();
                return new Date(task.dueDate).toDateString() === today;
              }).slice(0, 3).map((task) => (
                <TaskItem key={task.id} task={task} showCategory={false} />
              ))}
            </div>
          )}
          
          {statistics.todayTasks > 3 && (
            <div className="mt-4 text-center">
              <Button variant="outline" size="sm">
                View all today's tasks
              </Button>
            </div>
          )}
        </div>

        {/* Upcoming Tasks */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Upcoming Tasks</h2>
            <span className="text-sm text-gray-500">{statistics.upcomingTasks} tasks</span>
          </div>
          
          {statistics.upcomingTasks === 0 ? (
            <div className="text-center py-8">
              <Clock className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500">No upcoming tasks</p>
            </div>
          ) : (
            <div className="space-y-2">
              {tasks.filter(task => {
                if (task.status !== 'pending' || !task.dueDate) return false;
                const today = new Date();
                const taskDate = new Date(task.dueDate);
                const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
                return taskDate > today && taskDate <= nextWeek;
              }).slice(0, 3).map((task) => (
                <TaskItem key={task.id} task={task} showCategory={false} />
              ))}
            </div>
          )}
          
          {statistics.upcomingTasks > 3 && (
            <div className="mt-4 text-center">
              <Button variant="outline" size="sm">
                View all upcoming tasks
              </Button>
            </div>
          )}
        </div>
      </div>



      {/* Overdue Tasks */}
      {statistics.overdueTasks > 0 && (
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-red-900">Overdue Tasks</h2>
            <span className="text-sm text-red-500">{statistics.overdueTasks} tasks</span>
          </div>
          
          <div className="space-y-3">
            {tasks.filter(task => {
              if (task.status !== 'pending' || !task.dueDate) return false;
              return new Date(task.dueDate) < new Date();
            }).slice(0, 3).map((task) => (
              <TaskItem key={task.id} task={task} />
            ))}
          </div>
          
          {statistics.overdueTasks > 3 && (
            <div className="mt-4 text-center">
              <Button variant="outline" size="sm" className="text-red-600 border-red-300 hover:bg-red-50">
                View all overdue
              </Button>
            </div>
          )}
        </div>
      )}

      {/* Task Modal */}
      {showTaskModal && (
        <TaskModal
          isOpen={showTaskModal}
          onClose={() => setShowTaskModal(false)}
          categories={categories}
          tags={tags}
          onSubmit={handleCreateTask}
        />
      )}
    </div>
  );
};

export default DashboardPage; 