# TODO Web Application - UI Mockups & Design System

## 🎨 Design Philosophy

### Modern Design Principles
- **Minimalist**: Clean, uncluttered interface with plenty of whitespace
- **Intuitive**: Self-explanatory navigation and interactions
- **Accessible**: High contrast, keyboard navigation, screen reader friendly
- **Responsive**: Seamless experience across desktop, tablet, and mobile
- **Performance**: Fast loading with smooth animations and transitions

### Color Palette
```css
:root {
  /* Primary Colors */
  --primary-50: #eff6ff;
  --primary-500: #3b82f6;   /* Main brand color */
  --primary-600: #2563eb;
  --primary-700: #1d4ed8;

  /* Neutral Colors */
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-500: #6b7280;
  --gray-700: #374151;
  --gray-900: #111827;

  /* Semantic Colors */
  --success-500: #10b981;   /* Completed tasks */
  --warning-500: #f59e0b;   /* Due soon */
  --error-500: #ef4444;     /* Overdue */
  --info-500: #06b6d4;      /* Information */

  /* Priority Colors */
  --priority-high: #ef4444;
  --priority-medium: #f59e0b;
  --priority-low: #10b981;
}
```

### Typography
```css
/* Font Stack */
--font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
--font-mono: 'JetBrains Mono', 'Fira Code', monospace;

/* Type Scale */
--text-xs: 0.75rem;    /* 12px */
--text-sm: 0.875rem;   /* 14px */
--text-base: 1rem;     /* 16px */
--text-lg: 1.125rem;   /* 18px */
--text-xl: 1.25rem;    /* 20px */
--text-2xl: 1.5rem;    /* 24px */
--text-3xl: 1.875rem;  /* 30px */
```

## 📱 Responsive Breakpoints
```css
/* Mobile First Approach */
--mobile: 320px;     /* Small phones */
--tablet: 768px;     /* Tablets */
--desktop: 1024px;   /* Desktops */
--wide: 1280px;      /* Large screens */
```

---

## 1. 🔐 Authentication Pages

### 1.1 Login Page
```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                    ✓ TodoApp                                    │
│                                                                 │
│              Welcome back! Sign in to your account             │
│                                                                 │
│    ┌───────────────────────────────────────────────────────┐   │
│    │ Email address                                         │   │
│    │ ┌─────────────────────────────────────────────────┐   │   │
│    │ │ john@example.com                                 │   │   │
│    │ └─────────────────────────────────────────────────┘   │   │
│    └───────────────────────────────────────────────────────┘   │
│                                                                 │
│    ┌───────────────────────────────────────────────────────┐   │
│    │ Password                                              │   │
│    │ ┌─────────────────────────────────────────────────┐   │   │
│    │ │ ••••••••••••                                 👁  │   │   │
│    │ └─────────────────────────────────────────────────┘   │   │
│    └───────────────────────────────────────────────────────┘   │
│                                                                 │
│    ☐ Remember me              Forgot password? →               │
│                                                                 │
│    ┌─────────────────────────────────────────────────────────┐ │
│    │                   Sign In                                │ │
│    └─────────────────────────────────────────────────────────┘ │
│                                                                 │
│    ─────────────────── or continue with ──────────────────     │
│                                                                 │
│    ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│    │ 🔍 Google│  │ 📘 GitHub │  │ 🐙 GitLab │                   │
│    └──────────┘  └──────────┘  └──────────┘                   │
│                                                                 │
│              Don't have an account? Sign up here →             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Registration Page
```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                    ✓ TodoApp                                    │
│                                                                 │
│                  Create your account                            │
│                                                                 │
│    ┌───────────────────────────────────────────────────────┐   │
│    │ Full Name                                             │   │
│    │ ┌─────────────────────────────────────────────────┐   │   │
│    │ │ John Doe                                         │   │   │
│    │ └─────────────────────────────────────────────────┘   │   │
│    └───────────────────────────────────────────────────────┘   │
│                                                                 │
│    ┌───────────────────────────────────────────────────────┐   │
│    │ Email address                                         │   │
│    │ ┌─────────────────────────────────────────────────┐   │   │
│    │ │ john@example.com                                 │   │   │
│    │ └─────────────────────────────────────────────────┘   │   │
│    └───────────────────────────────────────────────────────┘   │
│                                                                 │
│    ┌───────────────────────────────────────────────────────┐   │
│    │ Password                                              │   │
│    │ ┌─────────────────────────────────────────────────┐   │   │
│    │ │ ••••••••••••                              👁     │   │   │
│    │ └─────────────────────────────────────────────────┘   │   │
│    │ ✓ 8+ characters  ✓ Mixed case  ✓ Numbers  ✓ Symbols │   │
│    └───────────────────────────────────────────────────────┘   │
│                                                                 │
│    ☑ I agree to the Terms of Service and Privacy Policy        │
│                                                                 │
│    ┌─────────────────────────────────────────────────────────┐ │
│    │                   Create Account                         │ │
│    └─────────────────────────────────────────────────────────┘ │
│                                                                 │
│              Already have an account? Sign in here →           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 🏠 Dashboard/Home View

### 2.1 Desktop Dashboard
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ ┌─────────┐    TodoApp                                    🔔  ⚙  👤 John Doe  ▼        │
│ │ ☰ Menu  │                                                                              │
│ └─────────┘                                                                              │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  📊 Dashboard                                                   📅 Today: Dec 15, 2023 │
│                                                                                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                      │
│  │    📝       │ │    ✅       │ │    ⏰       │ │    🔥       │                      │
│  │     42      │ │     28      │ │     14      │ │     3       │                      │
│  │Total Tasks  │ │ Completed   │ │  Pending    │ │  Overdue    │                      │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘                      │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │ ➕ Quick Add Task                                                                  │   │
│  │ ┌─────────────────────────────────────────────────────────────────────────────┐ │   │
│  │ │ What needs to be done?                                                      │ │   │
│  │ └─────────────────────────────────────────────────────────────────────────────┘ │   │
│  │ 📋 Category  📅 Due Date  ⭐ Priority  🏷 Tags                        [ Add ] │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────┐  ┌─────────────────────────────────────────────┐  │
│  │ 📅 Today's Tasks (5)            │  │ 📈 Recent Activity                          │  │
│  │ ┌─────────────────────────────┐ │  │ ┌─────────────────────────────────────────┐ │  │
│  │ │ ☐ Review project proposal   │ │  │ │ ✅ Completed "Design mockups"          │ │  │
│  │ │   📋 Work  🔴 High  ⏰ 2PM  │ │  │ │    2 minutes ago                       │ │  │
│  │ │                             │ │  │ │                                         │ │  │
│  │ │ ☐ Buy groceries            │ │  │ │ ✅ Completed "Write documentation"      │ │  │
│  │ │   📋 Personal  🟡 Medium   │ │  │ │    15 minutes ago                      │ │  │
│  │ │                             │ │  │ │                                         │ │  │
│  │ │ ☑ Morning workout          │ │  │ │ 📝 Created "Plan team meeting"        │ │  │
│  │ │   📋 Health  🟢 Low        │ │  │ │    1 hour ago                          │ │  │
│  │ │                             │ │  │ │                                         │ │  │
│  │ │ ☐ Call mom                 │ │  │ │ 🏷 Added tag "urgent" to task         │ │  │
│  │ │   📋 Personal  🟢 Low      │ │  │ │    2 hours ago                         │ │  │
│  │ │                             │ │  │ │                                         │ │  │
│  │ │ ☐ Prepare presentation     │ │  │ │ 📋 Created new category "Learning"     │ │  │
│  │ │   📋 Work  🔴 High  📅 Mon │ │  │ │    Yesterday                           │ │  │
│  │ └─────────────────────────────┘ │  │ └─────────────────────────────────────────┘ │  │
│  │ View all tasks →               │  │ View all activity →                       │  │
│  └─────────────────────────────────┘  └─────────────────────────────────────────────┘  │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Sidebar Navigation
```
┌─────────────────────┐
│ 📊 Dashboard        │ ← Active
│ 📝 All Tasks        │
│ 📅 Today            │
│ ⏰ Upcoming         │
│ 🔥 Overdue          │
│ ✅ Completed        │
│ ─────────────────── │
│ 📋 Categories       │
│   📁 Work (12)      │
│   🏠 Personal (8)   │
│   🛒 Shopping (3)   │
│   + Add Category    │
│ ─────────────────── │
│ 🏷 Tags             │
│   # urgent (5)      │
│   # meeting (3)     │
│   # review (2)      │
│   + Add Tag         │
│ ─────────────────── │
│ ⚙ Settings         │
│ 📤 Export Data      │
│ 📥 Import Data      │
│ 🔓 Logout          │
└─────────────────────┘
```

---

## 3. 📝 Task Management Views

### 3.1 All Tasks View
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ 📝 All Tasks                                                         👤 John Doe  ▼     │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 🔍 Search tasks...                    🔽 All Categories  🔽 All Priorities  🔽 All │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ Sort by: 📅 Due Date ▼    View: 📋 List ▼    42 tasks    ☐ Show completed           │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ ☐ Design new homepage layout                                              ⋮        │ │
│ │   📋 Work • 🔴 High Priority • 📅 Due tomorrow • 🏷 design, urgent                │ │
│ │   Create wireframes and mockups for the new company homepage design       │ Edit   │ │
│ │                                                                            │ Delete │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ ☐ Buy groceries for weekend                                               ⋮        │ │
│ │   📋 Personal • 🟡 Medium Priority • 📅 Due Friday • 🏷 shopping                   │ │
│ │   Milk, bread, eggs, vegetables, chicken, pasta                           │ Edit   │ │
│ │                                                                            │ Delete │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ ☑ Complete project documentation                                          ⋮        │ │
│ │   📋 Work • 🟢 Low Priority • ✅ Completed today • 🏷 documentation              │ │
│ │   Finalize all technical documentation for Q4 project                     │ Edit   │ │
│ │                                                                            │ Delete │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ ☐ Schedule dentist appointment                                             ⋮        │ │
│ │   📋 Health • 🟡 Medium Priority • 📅 No due date • 🏷 health, appointment        │ │
│ │   Call Dr. Smith's office to schedule regular checkup                     │ Edit   │ │
│ │                                                                            │ Delete │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ ☐ Prepare for team meeting                                                ⋮        │ │
│ │   📋 Work • 🔴 High Priority • 📅 Overdue (2 days) • 🏷 meeting, preparation      │ │
│ │   Review agenda, prepare slides, and gather feedback from last sprint     │ Edit   │ │
│ │                                                                            │ Delete │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │                                  ➕ Add New Task                                   │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│                              ← Previous | Page 1 of 3 | Next →                       │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Task Creation Modal
```
┌─────────────────────────────────────────────────────────────────┐
│                        ✕                                        │
│                   Create New Task                               │
│                                                                 │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Task Title *                                                │ │
│ │ ┌─────────────────────────────────────────────────────────┐ │ │
│ │ │ Enter task title...                                     │ │ │
│ │ └─────────────────────────────────────────────────────────┘ │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Description                                                 │ │
│ │ ┌─────────────────────────────────────────────────────────┐ │ │
│ │ │                                                         │ │ │
│ │ │ Add more details about this task...                     │ │ │
│ │ │                                                         │ │ │
│ │ └─────────────────────────────────────────────────────────┘ │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ ┌─────────────────────┐ ┌─────────────────────┐                │
│ │ Category            │ │ Priority            │                │
│ │ ┌─────────────────┐ │ │ ┌─────────────────┐ │                │
│ │ │ 📋 Work      ▼ │ │ │ │ 🟡 Medium    ▼ │ │                │
│ │ └─────────────────┘ │ │ └─────────────────┘ │                │
│ └─────────────────────┘ └─────────────────────┘                │
│                                                                 │
│ ┌─────────────────────┐ ┌─────────────────────┐                │
│ │ Due Date            │ │ Tags                │                │
│ │ ┌─────────────────┐ │ │ ┌─────────────────┐ │                │
│ │ │ 📅 Select... ▼ │ │ │ │ + Add tags...   │ │                │
│ │ └─────────────────┘ │ │ └─────────────────┘ │                │
│ └─────────────────────┘ └─────────────────────┘                │
│                                                                 │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │                       🏷 Suggested Tags                      │ │
│ │  urgent    meeting    review    personal    work    health  │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│          ┌─────────────┐        ┌─────────────────┐            │
│          │   Cancel    │        │   Create Task   │            │
│          └─────────────┘        └─────────────────┘            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 Task Quick Edit (Inline)
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ ☐ Design new homepage layout                                              ✓ Save ✕   │
│   ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│   │ Design new homepage layout                                                      │   │
│   └─────────────────────────────────────────────────────────────────────────────────┘   │
│   📋 Work ▼ • 🔴 High ▼ • 📅 Tomorrow ▼ • 🏷 design, urgent ✕ + Add             │
│   ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│   │ Create wireframes and mockups for the new company homepage...              │   │
│   │                                                                                 │   │
│   └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 📱 Mobile Views

### 4.1 Mobile Dashboard
```
┌─────────────────────────┐
│ ☰                   ⚙   │
│                         │
│      ✓ TodoApp          │
│                         │
│ ┌─────┐ ┌─────┐ ┌─────┐ │
│ │ 42  │ │ 28  │ │ 14  │ │
│ │Total│ │Done │ │Todo │ │
│ └─────┘ └─────┘ └─────┘ │
│                         │
│ ┌─────────────────────┐ │
│ │ + Add new task...   │ │
│ └─────────────────────┘ │
│                         │
│ 📅 Today (5 tasks)      │
│ ┌─────────────────────┐ │
│ │ ☐ Review proposal   │ │
│ │   Work • High • 2PM │ │
│ ├─────────────────────┤ │
│ │ ☐ Buy groceries     │ │
│ │   Personal • Medium │ │
│ ├─────────────────────┤ │
│ │ ☑ Morning workout   │ │
│ │   Health • Complete │ │
│ └─────────────────────┘ │
│                         │
│ View all tasks →        │
│                         │
│ ┌─────────────────────┐ │
│ │ 🏠 📝 📅 ⚙ 👤      │ │
│ └─────────────────────┘ │
└─────────────────────────┘
```

### 4.2 Mobile Task List
```
┌─────────────────────────┐
│ ← All Tasks         ⋮   │
│                         │
│ 🔍 Search...           │
│                         │
│ 🔽 Filter  🔽 Sort      │
│                         │
│ ┌─────────────────────┐ │
│ │ ☐ Design homepage   │ │
│ │   Work • High       │ │
│ │   📅 Tomorrow       │ │
│ │   🏷 design urgent  │ │
│ │                 ⋮   │ │
│ ├─────────────────────┤ │
│ │ ☐ Buy groceries     │ │
│ │   Personal • Medium │ │
│ │   📅 Friday         │ │
│ │   🏷 shopping       │ │
│ │                 ⋮   │ │
│ ├─────────────────────┤ │
│ │ ☑ Documentation     │ │
│ │   Work • Complete   │ │
│ │   📅 Today          │ │
│ │   🏷 docs           │ │
│ │                 ⋮   │ │
│ └─────────────────────┘ │
│                         │
│ ┌─────────────────────┐ │
│ │        + Add        │ │
│ └─────────────────────┘ │
│                         │
│ ┌─────────────────────┐ │
│ │ 🏠 📝 📅 ⚙ 👤      │ │
│ └─────────────────────┘ │
└─────────────────────────┘
```

---

## 5. ⚙️ Settings & Profile

### 5.1 User Settings Page
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ ⚙ Settings                                                       👤 John Doe  ▼      │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 👤 Profile                                                                         │ │
│ │                                                                                     │ │
│ │ ┌─────────────────────┐ ┌─────────────────────────────────────────────────────┐   │ │
│ │ │        👤           │ │ Full Name                                           │   │ │
│ │ │                     │ │ ┌─────────────────────────────────────────────┐   │   │ │
│ │ │     [Upload]        │ │ │ John Doe                                    │   │   │ │
│ │ │                     │ │ └─────────────────────────────────────────────┘   │   │ │
│ │ └─────────────────────┘ │                                                     │   │ │
│ │                         │ Email Address                                       │   │ │
│ │                         │ ┌─────────────────────────────────────────────┐   │   │ │
│ │                         │ │ john.doe@example.com                        │   │   │ │
│ │                         │ └─────────────────────────────────────────────┘   │   │ │
│ │                         └─────────────────────────────────────────────────────┘   │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 🎨 Appearance                                                                      │ │
│ │                                                                                     │ │
│ │ Theme                                                                               │ │
│ │ ○ Light Mode  ● Dark Mode  ○ System Default                                       │ │
│ │                                                                                     │ │
│ │ Font Size                                                                           │ │
│ │ ○ Small  ● Medium  ○ Large                                                         │ │
│ │                                                                                     │ │
│ │ ☑ High Contrast Mode                                                               │ │
│ │ ☑ Reduce Motion                                                                    │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 🔔 Notifications                                                                   │ │
│ │                                                                                     │ │
│ │ ☑ Email Notifications                                                              │ │
│ │ ☑ Browser Push Notifications                                                       │ │
│ │ ☑ Due Date Reminders                                                               │ │
│ │   ┌─────────────────────────────────────────────────────────────────────────┐     │ │
│ │   │ Remind me: ● 1 hour before  ○ 1 day before  ○ 1 week before           │     │ │
│ │   └─────────────────────────────────────────────────────────────────────────┘     │ │
│ │ ☑ Task Completion Celebrations                                                     │ │
│ │ ☐ Weekly Progress Summary                                                          │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 🔒 Privacy & Security                                                              │ │
│ │                                                                                     │ │
│ │ ┌─────────────────────────┐  ┌─────────────────────────┐                         │ │
│ │ │    Change Password      │  │    Export My Data       │                         │ │
│ │ └─────────────────────────┘  └─────────────────────────┘                         │ │
│ │                                                                                     │ │
│ │ ┌─────────────────────────┐  ┌─────────────────────────┐                         │ │
│ │ │    Delete Account       │  │    Download Data        │                         │ │
│ │ └─────────────────────────┘  └─────────────────────────┘                         │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │                                 💾 Save Changes                                   │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. 🏷️ Category & Tag Management

### 6.1 Category Management
```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ 📋 Categories                                                    👤 John Doe  ▼      │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│ Organize your tasks with custom categories                                             │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ ➕ Create New Category                                                             │ │
│ │                                                                                     │ │
│ │ ┌───────────────────────────┐ ┌─────────────────┐ ┌───────────────────────────┐   │ │
│ │ │ Category Name             │ │ Color           │ │                           │   │ │
│ │ │ ┌───────────────────────┐ │ │ ┌─────────────┐ │ │      [ Create ]          │   │ │
│ │ │ │ Enter name...         │ │ │ │ 🔵 Blue  ▼ │ │ │                           │   │ │
│ │ │ └───────────────────────┘ │ │ └─────────────┘ │ │                           │   │ │
│ │ └───────────────────────────┘ └─────────────────┘ └───────────────────────────┘   │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
│ ┌─────────────────────────────────────────────────────────────────────────────────────┐ │
│ │ 📁 Work                              🔵        12 tasks                         ⋮   │ │
│ │ Professional tasks and projects                                                     │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ 🏠 Personal                          🟢         8 tasks                         ⋮   │ │
│ │ Personal errands and activities                                                     │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ 🛒 Shopping                          🟡         3 tasks                         ⋮   │ │
│ │ Shopping lists and purchases                                                        │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ 💪 Health                            🟠         5 tasks                         ⋮   │ │
│ │ Health and fitness related tasks                                                    │ │
│ ├─────────────────────────────────────────────────────────────────────────────────────┤ │
│ │ 📚 Learning                          🟣         2 tasks                         ⋮   │ │
│ │ Educational and skill development                                                   │ │
│ └─────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. 🎨 Component Library

### 7.1 Button States
```css
/* Primary Button */
.btn-primary {
  background: var(--primary-500);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: var(--primary-600);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* Secondary Button */
.btn-secondary {
  background: transparent;
  color: var(--primary-500);
  border: 2px solid var(--primary-500);
  border-radius: 8px;
  padding: 10px 22px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: var(--primary-500);
  color: white;
}
```

### 7.2 Input Fields
```css
.input-field {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--gray-200);
  border-radius: 8px;
  font-size: var(--text-base);
  transition: all 0.2s ease;
  background: white;
}

.input-field:focus {
  outline: none;
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-field:invalid {
  border-color: var(--error-500);
}
```

### 7.3 Priority Indicators
```css
.priority-high {
  color: var(--priority-high);
  background: rgba(239, 68, 68, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: var(--text-xs);
  font-weight: 600;
}

.priority-medium {
  color: var(--priority-medium);
  background: rgba(245, 158, 11, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: var(--text-xs);
  font-weight: 600;
}

.priority-low {
  color: var(--priority-low);
  background: rgba(16, 185, 129, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: var(--text-xs);
  font-weight: 600;
}
```

---

## 8. 📐 Layout Specifications

### 8.1 Grid System
```css
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.grid {
  display: grid;
  gap: 24px;
}

.grid-2 { grid-template-columns: repeat(2, 1fr); }
.grid-3 { grid-template-columns: repeat(3, 1fr); }
.grid-4 { grid-template-columns: repeat(4, 1fr); }

@media (max-width: 768px) {
  .grid-2, .grid-3, .grid-4 {
    grid-template-columns: 1fr;
  }
}
```

### 8.2 Spacing System
```css
:root {
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
}
```

### 8.3 Animation Guidelines
```css
/* Micro-interactions */
.fade-in {
  animation: fadeIn 0.3s ease-out;
}

.slide-up {
  animation: slideUp 0.3s ease-out;
}

.scale-in {
  animation: scaleIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes scaleIn {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
```

---

## 9. ♿ Accessibility Guidelines

### 9.1 Color Contrast
- **Text on background**: Minimum 4.5:1 ratio
- **Large text**: Minimum 3:1 ratio
- **Interactive elements**: Minimum 3:1 ratio for focus indicators

### 9.2 Keyboard Navigation
```css
/* Focus indicators */
.focus-visible {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
}

/* Skip to content link */
.skip-link {
  position: absolute;
  top: -40px;
  left: 6px;
  background: var(--primary-500);
  color: white;
  padding: 8px;
  text-decoration: none;
  border-radius: 4px;
  z-index: 1000;
}

.skip-link:focus {
  top: 6px;
}
```

### 9.3 Screen Reader Support
```html
<!-- ARIA labels and landmarks -->
<main role="main" aria-label="Task Dashboard">
<nav role="navigation" aria-label="Main navigation">
<section aria-labelledby="today-tasks">
  <h2 id="today-tasks">Today's Tasks</h2>
</section>

<!-- Status announcements -->
<div aria-live="polite" id="status-announcements"></div>

<!-- Form labels -->
<label for="task-title">Task Title (required)</label>
<input id="task-title" type="text" required aria-describedby="title-help">
<div id="title-help">Enter a descriptive title for your task</div>
```

This modern, sleek UI design provides a comprehensive foundation for your TODO application with excellent user experience, accessibility, and responsive design across all devices. 