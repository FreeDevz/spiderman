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

  const StatCard: React.FC<{ title: string; value: number; icon: React.ReactNode; gradient: string; trend?: number }> = ({
    title,
    value,
    icon,
    gradient,
    trend,
  }) => (
    <Card className="overflow-hidden">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center">
            <div className={`p-3 rounded-xl ${gradient} shadow-lg`}>
              {icon}
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">{title}</p>
              <p className="text-2xl font-bold text-gray-900">{value}</p>
            </div>
          </div>
          {trend !== undefined && (
            <div className={`flex items-center text-sm font-medium ${trend >= 0 ? 'text-success-600' : 'text-error-600'}`}>
              <div className="mr-4 flex-shrink-0">
                <ArrowUp className={`h-4 w-4 ${trend < 0 ? 'transform rotate-180' : ''}`} />
              </div>
              <span className="flex-shrink-0">{Math.abs(trend)}%</span>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );

  const TaskItem: React.FC<{ task: Task; showCategory?: boolean }> = ({ task, showCategory = true }) => (
    <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-all duration-200 border border-gray-100">
      <div className="flex-1 min-w-0">
        <p className={`text-sm font-medium ${task.status === 'completed' ? 'line-through text-gray-500' : 'text-gray-900'}`}>
          {task.title}
        </p>
        <div className="flex items-center space-x-2 mt-1">
          {showCategory && task.category && (
            <span 
              className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium shadow-sm"
              style={{ backgroundColor: `${task.category.color}20`, color: task.category.color }}
            >
              {task.category.name}
            </span>
          )}
          <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium shadow-sm ${getPriorityColor(task.priority)}`}>
            {task.priority}
          </span>
          {task.dueDate && (
            <span className="text-xs text-gray-500 font-medium">
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
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold bg-gradient-primary bg-clip-text text-transparent">Dashboard</h1>
          <p className="text-gray-600 text-lg">Welcome back, {user?.name}! 👋</p>
        </div>
        <Button
          variant="default"
          size="lg"
          onClick={() => setShowTaskModal(true)}
          className="shadow-lg"
        >
          <Plus className="h-5 w-5 mr-2" />
          Add Task
        </Button>
      </div>

      {/* Statistics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Tasks"
          value={statistics.totalTasks}
          icon={<CheckSquare className="h-6 w-6 text-white" />}
          gradient="bg-gradient-primary"
        />
        <StatCard
          title="Completed"
          value={statistics.completedTasks}
          icon={<CheckCircle className="h-6 w-6 text-white" />}
          gradient="bg-gradient-success"
          trend={statistics.completionRate}
        />
        <StatCard
          title="Pending"
          value={statistics.pendingTasks}
          icon={<Clock className="h-6 w-6 text-white" />}
          gradient="bg-warning-400"
        />
        <StatCard
          title="Overdue"
          value={statistics.overdueTasks}
          icon={<AlertTriangle className="h-6 w-6 text-white" />}
          gradient="bg-error-500"
        />
      </div>

      {/* Quick Add Task */}
      <Card className="shadow-lg border-0">
        <CardHeader>
          <CardTitle className="text-xl">Quick Add Task</CardTitle>
          <CardDescription>Add a new task quickly without opening the full form</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex space-x-4">
            <Input
              type="text"
              value={quickAddTitle}
              onChange={(e) => setQuickAddTitle(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleQuickAdd()}
              placeholder="What needs to be done?"
              className="flex-1"
            />
            <Button 
              variant="success" 
              onClick={handleQuickAdd}
              disabled={!quickAddTitle.trim()}
              className="px-6"
            >
              Add Task
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Today's Tasks and Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="shadow-lg border-0">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-xl">Today's Tasks</CardTitle>
              <span className="text-sm font-medium text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
                {statistics.todayTasks} tasks
              </span>
            </div>
          </CardHeader>
          <CardContent>
            {statistics.todayTasks === 0 ? (
              <div className="text-center py-8">
                <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500 font-medium">No tasks for today</p>
                <p className="text-sm text-gray-400 mt-1">Enjoy your free day!</p>
              </div>
            ) : (
              <div className="space-y-3">
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
          </CardContent>
        </Card>

        {/* Upcoming Tasks */}
        <Card className="shadow-lg border-0">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-xl">Upcoming Tasks</CardTitle>
              <span className="text-sm font-medium text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
                {statistics.upcomingTasks} tasks
              </span>
            </div>
          </CardHeader>
          <CardContent>
            {statistics.upcomingTasks === 0 ? (
              <div className="text-center py-8">
                <Clock className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500 font-medium">No upcoming tasks</p>
                <p className="text-sm text-gray-400 mt-1">You're all caught up!</p>
              </div>
            ) : (
              <div className="space-y-3">
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
          </CardContent>
        </Card>
      </div>

      {/* Overdue Tasks */}
      {statistics.overdueTasks > 0 && (
        <Card className="shadow-lg border-0 border-l-4 border-l-error-500">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-xl text-error-700">Overdue Tasks</CardTitle>
              <span className="text-sm font-medium text-error-600 bg-error-50 px-3 py-1 rounded-full">
                {statistics.overdueTasks} tasks
              </span>
            </div>
          </CardHeader>
          <CardContent>
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
                <Button variant="outline" size="sm" className="text-error-600 border-error-300 hover:bg-error-50">
                  View all overdue
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
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