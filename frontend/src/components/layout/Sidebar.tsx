import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { X, BarChart3, CheckSquare, Calendar, Clock, AlertTriangle, CheckCircle, FolderOpen, Tag, Settings, Download, Upload, LogOut } from 'lucide-react';
import { useDispatch } from 'react-redux';
import type { AppDispatch } from '../../store';
import { logout } from '../../store/slices/authSlice';
import { ROUTES } from '../../constants';

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ open, onClose }) => {
  const location = useLocation();
  const dispatch = useDispatch<AppDispatch>();

  const handleLogout = async () => {
    try {
      await dispatch(logout()).unwrap();
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const navigation = [
    { name: 'Dashboard', href: ROUTES.DASHBOARD, icon: BarChart3 },
    { name: 'All Tasks', href: ROUTES.TASKS, icon: CheckSquare },
    { name: 'Today', href: `${ROUTES.TASKS}?filter=today`, icon: Calendar },
    { name: 'Upcoming', href: `${ROUTES.TASKS}?filter=upcoming`, icon: Clock },
    { name: 'Overdue', href: `${ROUTES.TASKS}?filter=overdue`, icon: AlertTriangle },
    { name: 'Completed', href: `${ROUTES.TASKS}?filter=completed`, icon: CheckCircle },
  ];

  const organization = [
    { name: 'Categories', href: ROUTES.CATEGORIES, icon: FolderOpen },
    { name: 'Tags', href: ROUTES.TAGS, icon: Tag },
  ];

  const tools = [
    { name: 'Settings', href: ROUTES.SETTINGS, icon: Settings },
    { name: 'Export Data', href: '/export', icon: Download },
    { name: 'Import Data', href: '/import', icon: Upload },
  ];

  const isActive = (href: string) => {
    if (href === ROUTES.DASHBOARD) {
      return location.pathname === href;
    }
    return location.pathname.startsWith(href);
  };

  return (
    <>
      {/* Mobile sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:hidden ${open ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex items-center justify-between h-16 px-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">TodoApp</h2>
          <button
            type="button"
            className="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
            onClick={onClose}
          >
            <X className="h-6 w-6" />
          </button>
        </div>
        
        <nav className="mt-5 px-2 space-y-1">
          {/* Main Navigation */}
          <div className="space-y-1">
            {navigation.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                    isActive(item.href)
                      ? 'bg-primary-100 text-primary-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                  onClick={onClose}
                >
                  <Icon className="mr-3 h-5 w-5" />
                  {item.name}
                </Link>
              );
            })}
          </div>

          {/* Divider */}
          <div className="border-t border-gray-200 pt-4">
            <h3 className="px-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
              Organization
            </h3>
            <div className="mt-2 space-y-1">
              {organization.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                      isActive(item.href)
                        ? 'bg-primary-100 text-primary-700'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                    }`}
                    onClick={onClose}
                  >
                    <Icon className="mr-3 h-5 w-5" />
                    {item.name}
                  </Link>
                );
              })}
            </div>
          </div>

          {/* Divider */}
          <div className="border-t border-gray-200 pt-4">
            <h3 className="px-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
              Tools
            </h3>
            <div className="mt-2 space-y-1">
              {tools.map((item) => {
                const Icon = item.icon;
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                      isActive(item.href)
                        ? 'bg-primary-100 text-primary-700'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                    }`}
                    onClick={onClose}
                  >
                    <Icon className="mr-3 h-5 w-5" />
                    {item.name}
                  </Link>
                );
              })}
            </div>
          </div>

          {/* Logout */}
          <div className="border-t border-gray-200 pt-4">
            <button
              type="button"
              onClick={handleLogout}
              className="group flex items-center w-full px-2 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-gray-50 hover:text-gray-900 transition-colors"
            >
              <LogOut className="mr-3 h-5 w-5" />
              Logout
            </button>
          </div>
        </nav>
      </div>

      {/* Desktop sidebar */}
      <div className="hidden lg:flex lg:flex-shrink-0">
        <div className="flex flex-col w-64">
          <div className="flex flex-col h-0 flex-1 bg-white shadow-lg">
            <div className="flex items-center h-16 px-4 border-b border-gray-200">
              <h2 className="text-lg font-semibold text-gray-900">TodoApp</h2>
            </div>
            
            <nav className="flex-1 px-2 py-4 space-y-1 overflow-y-auto">
              {/* Main Navigation */}
              <div className="space-y-1">
                {navigation.map((item) => {
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                        isActive(item.href)
                          ? 'bg-primary-100 text-primary-700'
                          : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                      }`}
                    >
                      <Icon className="mr-3 h-5 w-5" />
                      {item.name}
                    </Link>
                  );
                })}
              </div>

              {/* Divider */}
              <div className="border-t border-gray-200 pt-4">
                <h3 className="px-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Organization
                </h3>
                <div className="mt-2 space-y-1">
                  {organization.map((item) => {
                    const Icon = item.icon;
                    return (
                      <Link
                        key={item.name}
                        to={item.href}
                        className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                          isActive(item.href)
                            ? 'bg-primary-100 text-primary-700'
                            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                        }`}
                      >
                        <Icon className="mr-3 h-5 w-5" />
                        {item.name}
                      </Link>
                    );
                  })}
                </div>
              </div>

              {/* Divider */}
              <div className="border-t border-gray-200 pt-4">
                <h3 className="px-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Tools
                </h3>
                <div className="mt-2 space-y-1">
                  {tools.map((item) => {
                    const Icon = item.icon;
                    return (
                      <Link
                        key={item.name}
                        to={item.href}
                        className={`group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                          isActive(item.href)
                            ? 'bg-primary-100 text-primary-700'
                            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                        }`}
                      >
                        <Icon className="mr-3 h-5 w-5" />
                        {item.name}
                      </Link>
                    );
                  })}
                </div>
              </div>

              {/* Logout */}
              <div className="border-t border-gray-200 pt-4">
                <button
                  type="button"
                  onClick={handleLogout}
                  className="group flex items-center w-full px-2 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-gray-50 hover:text-gray-900 transition-colors"
                >
                  <LogOut className="mr-3 h-5 w-5" />
                  Logout
                </button>
              </div>
            </nav>
          </div>
        </div>
      </div>
    </>
  );
};

export default Sidebar; 