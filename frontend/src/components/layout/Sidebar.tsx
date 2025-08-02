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
    { name: 'Home', href: ROUTES.DASHBOARD, icon: Home },
    { name: 'All Tasks', href: ROUTES.TASKS, icon: CheckSquare },
    { name: 'Today', href: `${ROUTES.TASKS}?filter=today`, icon: Calendar },
    { name: 'Upcoming', href: `${ROUTES.TASKS}?filter=upcoming`, icon: Clock },
    { name: 'Overdue', href: `${ROUTES.TASKS}?filter=overdue`, icon: AlertTriangle },
    { name: 'Completed', href: `${ROUTES.TASKS}?filter=completed`, icon: CheckCircle },
    { name: 'Categories', href: ROUTES.CATEGORIES, icon: FolderOpen },
    { name: 'Tags', href: ROUTES.TAGS, icon: Tag },
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
        className={`flex items-center px-3 py-3 text-sm font-medium rounded-lg transition-all duration-200 mb-1 ${
          active
            ? 'text-gray-900 bg-gray-100'
            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
        }`}
        onClick={onClose}
      >
        <Icon className={`mr-3 h-5 w-5 ${active ? 'text-gray-900' : 'text-gray-500'}`} />
        {item.name}
      </Link>
    );
  };

  return (
    <div className="flex flex-col h-full bg-gray-50">
      {/* Logo section with mobile close button */}
      <div className="flex items-center justify-between h-16 px-4 border-b border-gray-200">
        <div className="flex items-center space-x-3">
          <Logo size="sm" />
          <h2 className="text-lg font-bold text-gray-900">TodoApp</h2>
        </div>
        {onClose && (
          <button
            type="button"
            className="md:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
            onClick={onClose}
          >
            <X className="h-6 w-6" />
          </button>
        )}
      </div>
      
      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {navigation.map((item) => renderNavItem(item))}
      </nav>

      {/* Bottom section with user info and logout */}
      <div className="border-t border-gray-200 p-4">
        {/* User profile */}
        <div className="flex items-center mb-4">
          <div className="h-8 w-8 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center">
            <span className="text-sm font-semibold text-white">
              {user?.name?.charAt(0)?.toUpperCase() || 'U'}
            </span>
          </div>
          <div className="ml-3">
            <p className="text-sm font-medium text-gray-900">{user?.name || 'User'}</p>
            <p className="text-xs text-gray-500">{user?.email}</p>
          </div>
        </div>

        {/* Settings and Logout */}
        <div className="space-y-1">
          <Link
            to={ROUTES.SETTINGS}
            className="flex items-center px-3 py-2 text-sm text-gray-600 hover:bg-gray-50 hover:text-gray-900 rounded-lg transition-colors"
            onClick={onClose}
          >
            <Settings className="mr-3 h-4 w-4" />
            Settings
          </Link>
          
          <button
            type="button"
            onClick={handleLogout}
            className="flex items-center w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50 hover:text-red-700 rounded-lg transition-colors"
          >
            <LogOut className="mr-3 h-4 w-4" />
            Logout
          </button>
        </div>
      </div>
    </div>
  );
};

export default Sidebar; 