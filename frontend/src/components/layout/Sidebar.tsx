import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { BarChart3, CheckSquare, Calendar, Clock, AlertTriangle, CheckCircle, FolderOpen, Tag, Settings, LogOut, Home, Search, Plus, X } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import type { AppDispatch, RootState } from '../../store';
import { logout } from '../../store/slices/authSlice';
import { ROUTES } from '../../constants';
import Logo from '../common/Logo';

interface SidebarProps {
  onClose?: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ onClose }) => {
  const location = useLocation();
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);

  const handleLogout = async () => {
    try {
      await dispatch(logout()).unwrap();
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const navigation = [
    { name: 'All Tasks', href: ROUTES.TASKS, icon: CheckSquare, emoji: '📋' },
    { name: 'Today', href: `${ROUTES.TASKS}?filter=today`, icon: Calendar, emoji: '📅' },
    { name: 'Upcoming', href: `${ROUTES.TASKS}?filter=upcoming`, icon: Clock, emoji: '⏰' },
    { name: 'Overdue', href: `${ROUTES.TASKS}?filter=overdue`, icon: AlertTriangle, emoji: '⚠️' },
    { name: 'Completed', href: `${ROUTES.TASKS}?filter=completed`, icon: CheckCircle, emoji: '✅' },
    { name: 'Categories', href: ROUTES.CATEGORIES, icon: FolderOpen, emoji: '📂' },
    { name: 'Stats', href: ROUTES.DASHBOARD, icon: BarChart3, emoji: '📊' },
  ];

  const isActive = (href: string) => {
    if (href === ROUTES.DASHBOARD) {
      return location.pathname === href;
    }
    return location.pathname.startsWith(href);
  };

  const renderNavItem = (item: any) => {
    const Icon = item.icon;
    const active = isActive(item.href);
    
    return (
      <Link
        key={item.name}
        to={item.href}
        className={`nav-item ${active ? 'active' : ''}`}
        onClick={onClose}
      >
        <span className="nav-icon">{item.emoji}</span>
        <span>{item.name}</span>
      </Link>
    );
  };

  return (
    <div className="flex flex-col h-full">
      {/* Logo section with mobile close button */}
      <div className="logo-section">
        <div className="flex items-center justify-between">
          <div className="logo">
            <div className="logo-icon">📋</div>
            <span>TodoApp</span>
          </div>
          {onClose && (
            <button
              type="button"
              className="md:hidden p-2 rounded-md text-white hover:bg-white hover:bg-opacity-20"
              onClick={onClose}
            >
              <X className="h-6 w-6" />
            </button>
          )}
        </div>
      </div>
      
      {/* Navigation */}
      <nav className="nav-menu flex-1">
        {navigation.map((item) => renderNavItem(item))}
      </nav>

      {/* Bottom section with user info and logout */}
      <div className="user-section">
        <div className="user-info">
          <div className="user-avatar">
            {user?.name?.charAt(0) || user?.email?.charAt(0) || 'U'}
          </div>
          <div className="user-details">
            <h4>{user?.name || 'User'}</h4>
            <p>{user?.email || 'user@example.com'}</p>
          </div>
        </div>
        
        {/* Logout button */}
        <button
          onClick={handleLogout}
          className="w-full mt-3 p-2 text-left text-white hover:bg-white hover:bg-opacity-20 rounded-lg transition-all duration-200 flex items-center gap-2"
        >
          <LogOut className="h-4 w-4" />
          <span className="text-sm">Logout</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar; 