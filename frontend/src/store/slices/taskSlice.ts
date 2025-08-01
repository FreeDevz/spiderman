import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { taskService } from '../../services/taskService';
import type { Task, CreateTaskRequest, UpdateTaskRequest, TaskFilters, SortOption, PaginationInfo } from '../../types';

// Async thunks
export const fetchTasks = createAsyncThunk(
  'tasks/fetchTasks',
  async ({ filters, sort, page, limit }: {
    filters?: TaskFilters;
    sort?: SortOption;
    page?: number;
    limit?: number;
  }) => {
    const response = await taskService.getTasks(filters, sort, page, limit);
    return response;
  }
);

export const fetchTask = createAsyncThunk(
  'tasks/fetchTask',
  async (id: number) => {
    const task = await taskService.getTask(id);
    return task;
  }
);

export const createTask = createAsyncThunk(
  'tasks/createTask',
  async (taskData: CreateTaskRequest) => {
    const task = await taskService.createTask(taskData);
    return task;
  }
);

export const updateTask = createAsyncThunk(
  'tasks/updateTask',
  async ({ id, taskData }: { id: number; taskData: UpdateTaskRequest }) => {
    const task = await taskService.updateTask(id, taskData);
    return task;
  }
);

export const deleteTask = createAsyncThunk(
  'tasks/deleteTask',
  async (id: number) => {
    await taskService.deleteTask(id);
    return id;
  }
);

export const updateTaskStatus = createAsyncThunk(
  'tasks/updateTaskStatus',
  async ({ id, status }: { id: number; status: 'pending' | 'completed' }) => {
    const task = await taskService.updateTaskStatus(id, status);
    return task;
  }
);

export const bulkUpdateTasks = createAsyncThunk(
  'tasks/bulkUpdateTasks',
  async ({ taskIds, updates }: { taskIds: number[]; updates: Partial<UpdateTaskRequest> }) => {
    const tasks = await taskService.bulkUpdateTasks(taskIds, updates);
    return tasks;
  }
);

export const bulkDeleteTasks = createAsyncThunk(
  'tasks/bulkDeleteTasks',
  async (taskIds: number[]) => {
    await taskService.bulkDeleteTasks(taskIds);
    return taskIds;
  }
);

export const bulkUpdateStatus = createAsyncThunk(
  'tasks/bulkUpdateStatus',
  async ({ taskIds, status }: { taskIds: number[]; status: 'pending' | 'completed' }) => {
    const tasks = await taskService.bulkUpdateStatus(taskIds, status);
    return tasks;
  }
);

export const searchTasks = createAsyncThunk(
  'tasks/searchTasks',
  async ({ query, filters }: { query: string; filters?: TaskFilters }) => {
    const tasks = await taskService.searchTasks(query, filters);
    return tasks;
  }
);

export const fetchTodayTasks = createAsyncThunk(
  'tasks/fetchTodayTasks',
  async () => {
    const tasks = await taskService.getTodayTasks();
    return tasks;
  }
);

export const fetchUpcomingTasks = createAsyncThunk(
  'tasks/fetchUpcomingTasks',
  async () => {
    const tasks = await taskService.getUpcomingTasks();
    return tasks;
  }
);

export const fetchOverdueTasks = createAsyncThunk(
  'tasks/fetchOverdueTasks',
  async () => {
    const tasks = await taskService.getOverdueTasks();
    return tasks;
  }
);

interface TaskState {
  tasks: Task[];
  todayTasks: Task[];
  upcomingTasks: Task[];
  overdueTasks: Task[];
  selectedTasks: number[];
  currentTask: Task | null;
  loading: boolean;
  error: string | null;
  pagination: PaginationInfo | null;
  filters: TaskFilters;
  sort: SortOption | null;
  searchQuery: string;
}

const initialState: TaskState = {
  tasks: [],
  todayTasks: [],
  upcomingTasks: [],
  overdueTasks: [],
  selectedTasks: [],
  currentTask: null,
  loading: false,
  error: null,
  pagination: null,
  filters: {},
  sort: null,
  searchQuery: '',
};

const taskSlice = createSlice({
  name: 'tasks',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setFilters: (state, action) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    clearFilters: (state) => {
      state.filters = {};
    },
    setSort: (state, action) => {
      state.sort = action.payload;
    },
    clearSort: (state) => {
      state.sort = null;
    },
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
    clearSearchQuery: (state) => {
      state.searchQuery = '';
    },
    selectTask: (state, action) => {
      const taskId = action.payload;
      if (!state.selectedTasks.includes(taskId)) {
        state.selectedTasks.push(taskId);
      }
    },
    deselectTask: (state, action) => {
      const taskId = action.payload;
      state.selectedTasks = state.selectedTasks.filter(id => id !== taskId);
    },
    selectAllTasks: (state) => {
      state.selectedTasks = state.tasks.map(task => task.id);
    },
    deselectAllTasks: (state) => {
      state.selectedTasks = [];
    },
    setCurrentTask: (state, action) => {
      state.currentTask = action.payload;
    },
    clearCurrentTask: (state) => {
      state.currentTask = null;
    },
  },
  extraReducers: (builder) => {
    // Fetch tasks
    builder
      .addCase(fetchTasks.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = action.payload.tasks;
        state.pagination = action.payload.pagination;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch tasks';
      });

    // Fetch single task
    builder
      .addCase(fetchTask.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTask.fulfilled, (state, action) => {
        state.loading = false;
        state.currentTask = action.payload;
      })
      .addCase(fetchTask.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch task';
      });

    // Create task
    builder
      .addCase(createTask.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createTask.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks.unshift(action.payload);
        // Update today tasks if it's due today
        const today = new Date().toDateString();
        if (action.payload.dueDate && new Date(action.payload.dueDate).toDateString() === today) {
          state.todayTasks.unshift(action.payload);
        }
      })
      .addCase(createTask.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create task';
      });

    // Update task
    builder
      .addCase(updateTask.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateTask.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.tasks.findIndex(task => task.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        if (state.currentTask?.id === action.payload.id) {
          state.currentTask = action.payload;
        }
      })
      .addCase(updateTask.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update task';
      });

    // Delete task
    builder
      .addCase(deleteTask.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteTask.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = state.tasks.filter(task => task.id !== action.payload);
        state.todayTasks = state.todayTasks.filter(task => task.id !== action.payload);
        state.upcomingTasks = state.upcomingTasks.filter(task => task.id !== action.payload);
        state.overdueTasks = state.overdueTasks.filter(task => task.id !== action.payload);
        state.selectedTasks = state.selectedTasks.filter(id => id !== action.payload);
      })
      .addCase(deleteTask.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete task';
      });

    // Update task status
    builder
      .addCase(updateTaskStatus.fulfilled, (state, action) => {
        const updatedTask = action.payload;
        const updateTaskInList = (tasks: Task[]) => {
          const index = tasks.findIndex(task => task.id === updatedTask.id);
          if (index !== -1) {
            tasks[index] = updatedTask;
          }
        };
        
        updateTaskInList(state.tasks);
        updateTaskInList(state.todayTasks);
        updateTaskInList(state.upcomingTasks);
        updateTaskInList(state.overdueTasks);
        
        if (state.currentTask?.id === updatedTask.id) {
          state.currentTask = updatedTask;
        }
      });

    // Bulk operations
    builder
      .addCase(bulkUpdateTasks.fulfilled, (state, action) => {
        const updatedTasks = action.payload;
        const updateTasksInList = (tasks: Task[]) => {
          updatedTasks.forEach(updatedTask => {
            const index = tasks.findIndex(task => task.id === updatedTask.id);
            if (index !== -1) {
              tasks[index] = updatedTask;
            }
          });
        };
        
        updateTasksInList(state.tasks);
        updateTasksInList(state.todayTasks);
        updateTasksInList(state.upcomingTasks);
        updateTasksInList(state.overdueTasks);
      });

    builder
      .addCase(bulkDeleteTasks.fulfilled, (state, action) => {
        const deletedIds = action.payload;
        const removeTasksFromList = (tasks: Task[]) => {
          return tasks.filter(task => !deletedIds.includes(task.id));
        };
        
        state.tasks = removeTasksFromList(state.tasks);
        state.todayTasks = removeTasksFromList(state.todayTasks);
        state.upcomingTasks = removeTasksFromList(state.upcomingTasks);
        state.overdueTasks = removeTasksFromList(state.overdueTasks);
        state.selectedTasks = state.selectedTasks.filter(id => !deletedIds.includes(id));
      });

    builder
      .addCase(bulkUpdateStatus.fulfilled, (state, action) => {
        const updatedTasks = action.payload;
        const updateTasksInList = (tasks: Task[]) => {
          updatedTasks.forEach(updatedTask => {
            const index = tasks.findIndex(task => task.id === updatedTask.id);
            if (index !== -1) {
              tasks[index] = updatedTask;
            }
          });
        };
        
        updateTasksInList(state.tasks);
        updateTasksInList(state.todayTasks);
        updateTasksInList(state.upcomingTasks);
        updateTasksInList(state.overdueTasks);
      });

    // Search tasks
    builder
      .addCase(searchTasks.fulfilled, (state, action) => {
        state.tasks = action.payload;
        state.pagination = null;
      });

    // Dashboard tasks
    builder
      .addCase(fetchTodayTasks.fulfilled, (state, action) => {
        state.todayTasks = action.payload;
      })
      .addCase(fetchUpcomingTasks.fulfilled, (state, action) => {
        state.upcomingTasks = action.payload;
      })
      .addCase(fetchOverdueTasks.fulfilled, (state, action) => {
        state.overdueTasks = action.payload;
      });
  },
});

export const {
  clearError,
  setFilters,
  clearFilters,
  setSort,
  clearSort,
  setSearchQuery,
  clearSearchQuery,
  selectTask,
  deselectTask,
  selectAllTasks,
  deselectAllTasks,
  setCurrentTask,
  clearCurrentTask,
} = taskSlice.actions;

export default taskSlice.reducer; 