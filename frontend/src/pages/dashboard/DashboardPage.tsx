import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useAppDispatch } from '../../store/hooks';
import { Plus, Calendar, Clock, AlertTriangle, CheckCircle, TrendingUp, CheckSquare, ArrowUp } from 'lucide-react';
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
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
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
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const handleCreateTask = async (taskData: CreateTaskRequest | UpdateTaskRequest) => {
    try {
      const result = await dispatch(createTask(taskData as CreateTaskRequest)).unwrap();
      console.log('Task created successfully:', result);
      setShowTaskModal(false);
    } catch (error) {
      console.error('Failed to create task:', error);
    }
  };

  const StatCard: React.FC<{ title: string; value: number; icon: React.ReactNode; type: 'total' | 'completed' | 'pending' | 'overdue'; trend?: number }> = ({
    title,
    value,
    icon,
    type,
    trend,
  }) => (
    <div className={`stat-card ${type}`}>
      <div className="stat-header">
        <span className="stat-title">{title}</span>
        <div className={`stat-icon ${type}`}>{icon}</div>
      </div>
      <div className="stat-number">{value}</div>
      {trend !== undefined && (
        <div className="stat-change">
          {trend > 0 ? '↗' : '↘'} {Math.abs(trend)}%
        </div>
      )}
    </div>
  );

  const TaskItem: React.FC<{ task: Task; showCategory?: boolean }> = ({ task, showCategory = true }) => (
    <div className="flex items-center justify-between p-4 border-b border-gray-100 last:border-b-0">
      <div className="flex items-center gap-3">
        <div className="flex-shrink-0">
          {getStatusIcon(task.status)}
        </div>
        <div className="flex-1 min-w-0">
          <h3 className={`text-sm font-medium ${task.status === 'completed' ? 'line-through text-gray-500' : 'text-gray-900'}`}>
            {task.title}
          </h3>
          {task.description && (
            <p className="text-xs text-gray-500 mt-1">{task.description}</p>
          )}
          {showCategory && task.category && (
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800 mt-1">
              {task.category.name}
            </span>
          )}
        </div>
      </div>
      <div className="flex items-center gap-2">
        {task.dueDate && (
          <span className="text-xs text-gray-500">
            {formatDate(task.dueDate)}
          </span>
        )}
        <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(task.priority)}`}>
          {task.priority}
        </span>
      </div>
    </div>
  );

  const todayTasks = tasks.filter(task => {
    if (task.status !== 'pending' || !task.dueDate) return false;
    const today = new Date().toDateString();
    return new Date(task.dueDate).toDateString() === today;
  });

  const upcomingTasks = tasks.filter(task => {
    if (task.status !== 'pending' || !task.dueDate) return false;
    const today = new Date();
    const taskDate = new Date(task.dueDate);
    const nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    return taskDate > today && taskDate <= nextWeek;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <header className="modern-header">
        <div className="welcome-section">
          <div className="welcome-text">
            <h1>Welcome back, {user?.name || 'User'}! 👋</h1>
            <p>Here's what you have planned for today</p>
          </div>
          <button 
            className="add-task-btn"
            onClick={() => setShowTaskModal(true)}
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Task
          </button>
        </div>
        
        {/* Stats Grid */}
        <div className="stats-grid">
          <StatCard
            title="Total Tasks"
            value={statistics.totalTasks}
            icon="📋"
            type="total"
            trend={5}
          />
          <StatCard
            title="Completed"
            value={statistics.completedTasks}
            icon="✅"
            type="completed"
            trend={12}
          />
          <StatCard
            title="Pending"
            value={statistics.pendingTasks}
            icon="⏳"
            type="pending"
            trend={-2}
          />
          <StatCard
            title="Overdue"
            value={statistics.overdueTasks}
            icon="⚠️"
            type="overdue"
            trend={-50}
          />
        </div>
      </header>

      {/* Quick Add Task */}
      <section className="quick-add-section">
        <h2 className="quick-add-title">Quick Add Task</h2>
        <p style={{ color: '#6b7280', marginBottom: '1rem' }}>Add a new task quickly without opening the full form</p>
        <form 
          className="quick-add-form"
          onSubmit={(e) => {
            e.preventDefault();
            handleQuickAdd();
          }}
        >
          <input
            type="text"
            className="quick-add-input"
            placeholder="Enter your task here..."
            value={quickAddTitle}
            onChange={(e) => setQuickAddTitle(e.target.value)}
          />
          <button type="submit" className="quick-add-submit">
            Add Task
          </button>
        </form>
      </section>

      {/* Today's Tasks */}
      <section className="task-section">
        <div className="section-header">
          <h2 className="section-title">Today's Tasks</h2>
          <span className="task-count">{todayTasks.length} tasks</span>
        </div>
        {todayTasks.length > 0 ? (
          <div className="space-y-2">
            {todayTasks.slice(0, 5).map((task) => (
              <TaskItem key={task.id} task={task} />
            ))}
            {todayTasks.length > 5 && (
              <div className="text-center py-4">
                <button className="text-sm text-blue-600 hover:text-blue-800">
                  View all {todayTasks.length} tasks
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="empty-state">
            <div className="empty-icon">📅</div>
            <div className="empty-title">No tasks for today</div>
            <div className="empty-subtitle">Enjoy your free day!</div>
          </div>
        )}
      </section>

      {/* Upcoming Tasks */}
      <section className="task-section">
        <div className="section-header">
          <h2 className="section-title">Upcoming Tasks</h2>
          <span className="task-count">{upcomingTasks.length} tasks</span>
        </div>
        {upcomingTasks.length > 0 ? (
          <div className="space-y-2">
            {upcomingTasks.slice(0, 5).map((task) => (
              <TaskItem key={task.id} task={task} />
            ))}
            {upcomingTasks.length > 5 && (
              <div className="text-center py-4">
                <button className="text-sm text-blue-600 hover:text-blue-800">
                  View all {upcomingTasks.length} tasks
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="empty-state">
            <div className="empty-icon">⏰</div>
            <div className="empty-title">No upcoming tasks</div>
            <div className="empty-subtitle">You're all caught up!</div>
          </div>
        )}
      </section>

      {/* Task Modal */}
      <TaskModal
        isOpen={showTaskModal}
        onClose={() => setShowTaskModal(false)}
        onSubmit={handleCreateTask}
        categories={categories}
        tags={tags}
      />
    </div>
  );
};

export default DashboardPage; 