-- TodoApp Database Schema Initialization
-- This script creates all necessary tables for the TODO application
-- Runs automatically when the PostgreSQL container starts

-- Enable UUID extension for better ID generation (optional)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    avatar_url TEXT,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add comment for documentation
COMMENT ON TABLE users IS 'User accounts and authentication information';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.email_verified IS 'Email verification status for account activation';

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT '#3B82F6',
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, name)
);

-- Add comment and constraints
COMMENT ON TABLE categories IS 'User-defined task categories with color coding';
COMMENT ON COLUMN categories.color IS 'Hex color code for category visualization';

-- Add check constraint for valid hex color
ALTER TABLE categories ADD CONSTRAINT check_color_format 
    CHECK (color ~ '^#[0-9A-Fa-f]{6}$');

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'completed', 'deleted')),
    priority VARCHAR(10) DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high')),
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add comments
COMMENT ON TABLE tasks IS 'Main task/todo items with status and priority management';
COMMENT ON COLUMN tasks.status IS 'Task status: pending, completed, or soft-deleted';
COMMENT ON COLUMN tasks.priority IS 'Task priority level: low, medium, or high';
COMMENT ON COLUMN tasks.completed_at IS 'Timestamp when task was marked as completed';

-- Add constraint to ensure completed_at is set when status is completed
ALTER TABLE tasks ADD CONSTRAINT check_completed_at 
    CHECK ((status = 'completed' AND completed_at IS NOT NULL) 
           OR (status != 'completed' AND completed_at IS NULL));

-- Tags table
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(30) NOT NULL,
    color VARCHAR(7) DEFAULT '#6B7280',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, name)
);

-- Add comments
COMMENT ON TABLE tags IS 'User-defined tags for flexible task organization';
COMMENT ON COLUMN tags.name IS 'Tag name, unique per user';

-- Task-Tags junction table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS task_tags (
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (task_id, tag_id)
);

-- Add comment
COMMENT ON TABLE task_tags IS 'Junction table for many-to-many relationship between tasks and tags';

-- User settings table
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    theme VARCHAR(10) DEFAULT 'light' CHECK (theme IN ('light', 'dark', 'auto')),
    notifications_enabled BOOLEAN DEFAULT TRUE,
    timezone VARCHAR(50) DEFAULT 'UTC',
    language VARCHAR(5) DEFAULT 'en',
    date_format VARCHAR(20) DEFAULT 'MM/DD/YYYY',
    time_format VARCHAR(5) DEFAULT '12h',
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    task_reminders BOOLEAN DEFAULT TRUE,
    daily_digest BOOLEAN DEFAULT FALSE,
    weekly_report BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add comments
COMMENT ON TABLE user_settings IS 'User preferences and application settings';
COMMENT ON COLUMN user_settings.theme IS 'UI theme preference: light, dark, or auto';
COMMENT ON COLUMN user_settings.timezone IS 'User timezone for date/time display';

-- Notifications table (for in-app notifications)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    scheduled_for TIMESTAMP,
    metadata JSONB
);

-- Add comments
COMMENT ON TABLE notifications IS 'In-app notifications and reminders';
COMMENT ON COLUMN notifications.type IS 'Notification type: due_reminder, task_completed, achievement, etc.';
COMMENT ON COLUMN notifications.metadata IS 'Additional notification data in JSON format';

-- Create a view for dashboard statistics
CREATE OR REPLACE VIEW user_dashboard_stats AS
SELECT 
    u.id as user_id,
    u.name as user_name,
    COUNT(t.id) as total_tasks,
    COUNT(CASE WHEN t.status = 'completed' THEN 1 END) as completed_tasks,
    COUNT(CASE WHEN t.status = 'pending' THEN 1 END) as pending_tasks,
    COUNT(CASE WHEN t.due_date < NOW() AND t.status = 'pending' THEN 1 END) as overdue_tasks,
    COUNT(CASE WHEN DATE(t.due_date) = CURRENT_DATE AND t.status = 'pending' THEN 1 END) as today_tasks,
    COUNT(CASE WHEN t.due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days' AND t.status = 'pending' THEN 1 END) as upcoming_tasks
FROM users u 
LEFT JOIN tasks t ON u.id = t.user_id AND t.status != 'deleted'
GROUP BY u.id, u.name;

-- Add comment
COMMENT ON VIEW user_dashboard_stats IS 'Aggregated statistics for user dashboard display';

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add updated_at triggers to relevant tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tags_updated_at BEFORE UPDATE ON tags
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_settings_updated_at BEFORE UPDATE ON user_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create function to automatically set completed_at when task is completed
CREATE OR REPLACE FUNCTION set_completed_at()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'completed' AND OLD.status != 'completed' THEN
        NEW.completed_at = CURRENT_TIMESTAMP;
    ELSIF NEW.status != 'completed' THEN
        NEW.completed_at = NULL;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add trigger for automatic completed_at handling
CREATE TRIGGER set_task_completed_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION set_completed_at();

-- Print success message
DO $$
BEGIN
    RAISE NOTICE 'TodoApp database schema created successfully!';
    RAISE NOTICE 'Tables created: users, categories, tasks, tags, task_tags, user_settings, notifications';
    RAISE NOTICE 'Views created: user_dashboard_stats';
    RAISE NOTICE 'Triggers created: updated_at triggers, completed_at trigger';
END $$; 