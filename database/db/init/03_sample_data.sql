-- TodoApp Sample Data for Development
-- This script inserts sample data for development and testing
-- Creates a demo user with sample tasks, categories, and tags

-- Insert demo user (password: "password")
-- BCrypt hash generated for "password" with cost 10
INSERT INTO users (email, password_hash, name, email_verified) VALUES 
('demo@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5JQED8XPZz8ey6Kgbr4AQvtWuEqSb0K', 'Demo User', true),
('john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5JQED8XPZz8ey6Kgbr4AQvtWuEqSb0K', 'John Doe', true),
('jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5JQED8XPZz8ey6Kgbr4AQvtWuEqSb0K', 'Jane Smith', false)
ON CONFLICT (email) DO NOTHING;

-- Get user IDs for reference
DO $$
DECLARE
    demo_user_id BIGINT;
    john_user_id BIGINT;
    jane_user_id BIGINT;
    
    -- Category IDs
    work_category_id BIGINT;
    personal_category_id BIGINT;
    shopping_category_id BIGINT;
    health_category_id BIGINT;
    learning_category_id BIGINT;
    
    -- Tag IDs
    urgent_tag_id BIGINT;
    meeting_tag_id BIGINT;
    review_tag_id BIGINT;
    home_tag_id BIGINT;
    exercise_tag_id BIGINT;
    
    -- Task IDs for tag associations
    task1_id BIGINT;
    task2_id BIGINT;
    task3_id BIGINT;
    task4_id BIGINT;
    task5_id BIGINT;
BEGIN
    -- Get user IDs
    SELECT id INTO demo_user_id FROM users WHERE email = 'demo@example.com';
    SELECT id INTO john_user_id FROM users WHERE email = 'john.doe@example.com';
    SELECT id INTO jane_user_id FROM users WHERE email = 'jane.smith@example.com';
    
    -- Insert sample categories for demo user
    INSERT INTO categories (user_id, name, color) VALUES 
    (demo_user_id, 'Work', '#EF4444'),
    (demo_user_id, 'Personal', '#3B82F6'),
    (demo_user_id, 'Shopping', '#10B981'),
    (demo_user_id, 'Health & Fitness', '#F59E0B'),
    (demo_user_id, 'Learning', '#8B5CF6')
    ON CONFLICT (user_id, name) DO NOTHING;
    
    -- Get category IDs
    SELECT id INTO work_category_id FROM categories WHERE user_id = demo_user_id AND name = 'Work';
    SELECT id INTO personal_category_id FROM categories WHERE user_id = demo_user_id AND name = 'Personal';
    SELECT id INTO shopping_category_id FROM categories WHERE user_id = demo_user_id AND name = 'Shopping';
    SELECT id INTO health_category_id FROM categories WHERE user_id = demo_user_id AND name = 'Health & Fitness';
    SELECT id INTO learning_category_id FROM categories WHERE user_id = demo_user_id AND name = 'Learning';
    
    -- Insert sample categories for other users
    INSERT INTO categories (user_id, name, color) VALUES 
    (john_user_id, 'Work', '#DC2626'),
    (john_user_id, 'Projects', '#7C3AED'),
    (jane_user_id, 'Personal', '#059669'),
    (jane_user_id, 'Travel', '#0EA5E9')
    ON CONFLICT (user_id, name) DO NOTHING;
    
    -- Insert sample tags for demo user
    INSERT INTO tags (user_id, name) VALUES 
    (demo_user_id, 'urgent'),
    (demo_user_id, 'meeting'),
    (demo_user_id, 'review'),
    (demo_user_id, 'home'),
    (demo_user_id, 'exercise'),
    (demo_user_id, 'documentation'),
    (demo_user_id, 'planning'),
    (demo_user_id, 'research')
    ON CONFLICT (user_id, name) DO NOTHING;
    
    -- Get tag IDs
    SELECT id INTO urgent_tag_id FROM tags WHERE user_id = demo_user_id AND name = 'urgent';
    SELECT id INTO meeting_tag_id FROM tags WHERE user_id = demo_user_id AND name = 'meeting';
    SELECT id INTO review_tag_id FROM tags WHERE user_id = demo_user_id AND name = 'review';
    SELECT id INTO home_tag_id FROM tags WHERE user_id = demo_user_id AND name = 'home';
    SELECT id INTO exercise_tag_id FROM tags WHERE user_id = demo_user_id AND name = 'exercise';
    
    -- Insert sample tasks for demo user
    INSERT INTO tasks (user_id, category_id, title, description, priority, status, due_date) VALUES 
    (demo_user_id, work_category_id, 'Review project proposal', 'Review the Q4 project proposal document and provide feedback on technical requirements', 'high', 'pending', CURRENT_TIMESTAMP + INTERVAL '2 hours'),
    (demo_user_id, work_category_id, 'Prepare team meeting slides', 'Create presentation for tomorrow''s sprint planning meeting', 'high', 'pending', CURRENT_TIMESTAMP + INTERVAL '1 day'),
    (demo_user_id, personal_category_id, 'Buy groceries', 'Milk, bread, eggs, vegetables, chicken breast, pasta', 'medium', 'pending', CURRENT_TIMESTAMP + INTERVAL '2 days'),
    (demo_user_id, health_category_id, 'Morning workout', 'Go for a 30-minute run or gym session', 'low', 'pending', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
    (demo_user_id, learning_category_id, 'Complete React course', 'Finish the advanced React course on platform', 'medium', 'pending', CURRENT_TIMESTAMP + INTERVAL '1 week'),
    (demo_user_id, personal_category_id, 'Call mom', 'Weekly check-in call with family', 'medium', 'pending', CURRENT_TIMESTAMP + INTERVAL '3 days'),
    (demo_user_id, work_category_id, 'Code review', 'Review pull requests from team members', 'medium', 'pending', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (demo_user_id, shopping_category_id, 'Buy birthday gift', 'Find and purchase birthday gift for Sarah', 'high', 'pending', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    (demo_user_id, work_category_id, 'Update documentation', 'Update API documentation with recent changes', 'low', 'pending', CURRENT_TIMESTAMP + INTERVAL '5 days'),
    (demo_user_id, health_category_id, 'Schedule dentist appointment', 'Call Dr. Smith''s office to schedule regular checkup', 'medium', 'pending', NULL);
    
    -- Get task IDs for tag associations (using a different approach since RETURNING only gets last ID)
    SELECT id INTO task1_id FROM tasks WHERE user_id = demo_user_id AND title = 'Review project proposal';
    SELECT id INTO task2_id FROM tasks WHERE user_id = demo_user_id AND title = 'Prepare team meeting slides';
    SELECT id INTO task3_id FROM tasks WHERE user_id = demo_user_id AND title = 'Buy groceries';
    SELECT id INTO task4_id FROM tasks WHERE user_id = demo_user_id AND title = 'Morning workout';
    SELECT id INTO task5_id FROM tasks WHERE user_id = demo_user_id AND title = 'Buy birthday gift';
    
    -- Update some tasks to completed status (this will trigger the completed_at setting)
    UPDATE tasks SET status = 'completed' WHERE user_id = demo_user_id AND title = 'Morning workout';
    UPDATE tasks SET status = 'completed' WHERE user_id = demo_user_id AND title = 'Code review';
    
    -- Insert task-tag relationships
    INSERT INTO task_tags (task_id, tag_id) VALUES 
    (task1_id, urgent_tag_id),
    (task1_id, review_tag_id),
    (task2_id, urgent_tag_id),
    (task2_id, meeting_tag_id),
    (task3_id, home_tag_id),
    (task4_id, exercise_tag_id),
    (task5_id, urgent_tag_id)
    ON CONFLICT (task_id, tag_id) DO NOTHING;
    
    -- Insert some tasks for other users
    INSERT INTO tasks (user_id, category_id, title, description, priority, status, due_date) VALUES 
    (john_user_id, (SELECT id FROM categories WHERE user_id = john_user_id AND name = 'Work' LIMIT 1), 'Client presentation', 'Prepare slides for client meeting', 'high', 'pending', CURRENT_TIMESTAMP + INTERVAL '1 day'),
    (jane_user_id, (SELECT id FROM categories WHERE user_id = jane_user_id AND name = 'Personal' LIMIT 1), 'Plan vacation', 'Research and plan summer vacation destinations', 'medium', 'pending', CURRENT_TIMESTAMP + INTERVAL '2 weeks')
    ON CONFLICT DO NOTHING;
    
    -- Insert user settings for demo user
    INSERT INTO user_settings (user_id, theme, notifications_enabled, timezone, language, date_format, time_format) VALUES 
    (demo_user_id, 'light', true, 'America/New_York', 'en', 'MM/DD/YYYY', '12h'),
    (john_user_id, 'dark', true, 'Europe/London', 'en', 'DD/MM/YYYY', '24h'),
    (jane_user_id, 'auto', false, 'Asia/Tokyo', 'en', 'YYYY-MM-DD', '24h')
    ON CONFLICT (user_id) DO NOTHING;
    
    -- Insert sample notifications for demo user
    INSERT INTO notifications (user_id, type, title, message, read, scheduled_for, metadata) VALUES 
    (demo_user_id, 'due_reminder', 'Task Due Soon', 'Your task "Review project proposal" is due in 2 hours', false, CURRENT_TIMESTAMP + INTERVAL '2 hours', '{"task_id": 1, "priority": "high"}'),
    (demo_user_id, 'task_completed', 'Task Completed!', 'Great job completing "Morning workout"', true, NULL, '{"task_id": 4, "completion_time": "2023-12-15T08:30:00Z"}'),
    (demo_user_id, 'achievement', 'Streak Achievement', 'You''ve completed tasks for 3 days in a row!', false, NULL, '{"streak_count": 3, "achievement_type": "daily_streak"}'),
    (demo_user_id, 'due_reminder', 'Overdue Task', 'Your task "Buy birthday gift" is overdue', false, NULL, '{"task_id": 8, "days_overdue": 2}')
    ON CONFLICT DO NOTHING;
    
    -- Print success message
    RAISE NOTICE 'Sample data inserted successfully!';
    RAISE NOTICE 'Demo user credentials: demo@example.com / password';
    RAISE NOTICE 'Other test users: john.doe@example.com / password, jane.smith@example.com / password';
    RAISE NOTICE 'Categories: % created for demo user', (SELECT COUNT(*) FROM categories WHERE user_id = demo_user_id);
    RAISE NOTICE 'Tasks: % created for demo user', (SELECT COUNT(*) FROM tasks WHERE user_id = demo_user_id);
    RAISE NOTICE 'Tags: % created for demo user', (SELECT COUNT(*) FROM tags WHERE user_id = demo_user_id);
    RAISE NOTICE 'Notifications: % created for demo user', (SELECT COUNT(*) FROM notifications WHERE user_id = demo_user_id);
END $$;

-- Update some tasks to have realistic completed_at timestamps
UPDATE tasks 
SET completed_at = created_at + INTERVAL '2 hours'
WHERE status = 'completed' AND completed_at IS NULL;

-- Create some additional sample data for testing edge cases
INSERT INTO tasks (user_id, category_id, title, description, priority, status, due_date, created_at) 
SELECT 
    (SELECT id FROM users WHERE email = 'demo@example.com'),
    (SELECT id FROM categories WHERE user_id = (SELECT id FROM users WHERE email = 'demo@example.com') AND name = 'Work'),
    'Task ' || generate_series,
    'Sample task description for testing task #' || generate_series,
    CASE 
        WHEN generate_series % 3 = 0 THEN 'high'
        WHEN generate_series % 3 = 1 THEN 'medium'
        ELSE 'low'
    END,
    'pending',
    CURRENT_TIMESTAMP + (generate_series || ' days')::INTERVAL,
    CURRENT_TIMESTAMP - (random() * 30 || ' days')::INTERVAL
FROM generate_series(1, 15)
ON CONFLICT DO NOTHING;

-- Update some generated tasks to completed status
UPDATE tasks 
SET status = 'completed' 
WHERE user_id = (SELECT id FROM users WHERE email = 'demo@example.com') 
  AND title LIKE 'Task %' 
  AND CAST(SUBSTRING(title FROM 'Task (\d+)') AS INTEGER) % 4 = 0;

-- Final statistics
DO $$
DECLARE
    total_users INTEGER;
    total_categories INTEGER;
    total_tasks INTEGER;
    total_tags INTEGER;
    total_notifications INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_users FROM users;
    SELECT COUNT(*) INTO total_categories FROM categories;
    SELECT COUNT(*) INTO total_tasks FROM tasks;
    SELECT COUNT(*) INTO total_tags FROM tags;
    SELECT COUNT(*) INTO total_notifications FROM notifications;
    
    RAISE NOTICE '=== TodoApp Sample Data Summary ===';
    RAISE NOTICE 'Users: %', total_users;
    RAISE NOTICE 'Categories: %', total_categories;
    RAISE NOTICE 'Tasks: %', total_tasks;
    RAISE NOTICE 'Tags: %', total_tags;
    RAISE NOTICE 'Notifications: %', total_notifications;
    RAISE NOTICE '=================================';
    RAISE NOTICE 'Ready for development and testing!';
END $$; 