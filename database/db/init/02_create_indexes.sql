-- TodoApp Database Indexes for Performance Optimization
-- This script creates indexes on frequently queried columns
-- Runs after schema creation for optimal performance

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Tasks table indexes (most important for performance)
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_category_id ON tasks(category_id);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);
CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks(created_at);
CREATE INDEX IF NOT EXISTS idx_tasks_updated_at ON tasks(updated_at);
CREATE INDEX IF NOT EXISTS idx_tasks_completed_at ON tasks(completed_at);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_tasks_user_status ON tasks(user_id, status);
CREATE INDEX IF NOT EXISTS idx_tasks_user_due_date ON tasks(user_id, due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_user_priority ON tasks(user_id, priority);
CREATE INDEX IF NOT EXISTS idx_tasks_status_due_date ON tasks(status, due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_user_status_due_date ON tasks(user_id, status, due_date);

-- Index for overdue tasks query (removed CURRENT_TIMESTAMP as it's not immutable)
-- Use a regular composite index instead
CREATE INDEX IF NOT EXISTS idx_tasks_status_due_pending ON tasks(status, due_date, user_id) 
    WHERE status = 'pending';

-- Index for today's tasks query (removed CURRENT_DATE as it's not immutable)
-- Use a regular composite index instead
CREATE INDEX IF NOT EXISTS idx_tasks_due_date_status ON tasks(due_date, status, user_id);

-- Categories table indexes
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories(user_id);
CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(user_id, name);
CREATE INDEX IF NOT EXISTS idx_categories_created_at ON categories(created_at);

-- Tags table indexes
CREATE INDEX IF NOT EXISTS idx_tags_user_id ON tags(user_id);
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(user_id, name);
CREATE INDEX IF NOT EXISTS idx_tags_created_at ON tags(created_at);

-- Task-Tags junction table indexes
CREATE INDEX IF NOT EXISTS idx_task_tags_task_id ON task_tags(task_id);
CREATE INDEX IF NOT EXISTS idx_task_tags_tag_id ON task_tags(tag_id);
CREATE INDEX IF NOT EXISTS idx_task_tags_created_at ON task_tags(created_at);

-- User settings table indexes
CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON user_settings(user_id);

-- Notifications table indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications(user_id, read);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_scheduled_for ON notifications(scheduled_for);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- Composite index for unread notifications
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread_created ON notifications(user_id, read, created_at)
    WHERE read = FALSE;

-- Partial indexes for better performance on common queries
CREATE INDEX IF NOT EXISTS idx_tasks_pending ON tasks(user_id, created_at) 
    WHERE status = 'pending';

CREATE INDEX IF NOT EXISTS idx_tasks_completed ON tasks(user_id, completed_at) 
    WHERE status = 'completed';

-- GIN index for JSONB metadata in notifications (for advanced querying)
CREATE INDEX IF NOT EXISTS idx_notifications_metadata_gin ON notifications USING GIN (metadata);

-- Text search indexes for title and description (optional, for future search functionality)
-- Uncomment if full-text search is needed
-- CREATE INDEX IF NOT EXISTS idx_tasks_title_fts ON tasks USING GIN (to_tsvector('english', title));
-- CREATE INDEX IF NOT EXISTS idx_tasks_description_fts ON tasks USING GIN (to_tsvector('english', description));
-- CREATE INDEX IF NOT EXISTS idx_tasks_combined_fts ON tasks USING GIN (to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- Create statistics for better query planning
ANALYZE users;
ANALYZE categories;
ANALYZE tasks;
ANALYZE tags;
ANALYZE task_tags;
ANALYZE user_settings;
ANALYZE notifications;

-- Print success message with index count
DO $$
DECLARE
    index_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO index_count 
    FROM pg_indexes 
    WHERE schemaname = 'public' AND tablename IN ('users', 'categories', 'tasks', 'tags', 'task_tags', 'user_settings', 'notifications');
    
    RAISE NOTICE 'TodoApp database indexes created successfully!';
    RAISE NOTICE 'Total indexes created: %', index_count;
    RAISE NOTICE 'Performance optimization complete.';
END $$; 