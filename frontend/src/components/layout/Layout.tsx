import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { Menu } from 'lucide-react';
import Sidebar from './Sidebar';

const Layout: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isDesktop, setIsDesktop] = useState(false);

  useEffect(() => {
    const checkScreenSize = () => {
      setIsDesktop(window.innerWidth >= 768);
    };

    checkScreenSize();
    window.addEventListener('resize', checkScreenSize);
    return () => window.removeEventListener('resize', checkScreenSize);
  }, []);

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Instagram-style layout with persistent sidebar */}
      <div style={{ display: 'flex', height: '100vh' }}>
        {/* Desktop Sidebar - Always visible on medium screens and above */}
        {isDesktop && (
          <div 
            style={{
              width: '256px',
              flexShrink: 0,
              backgroundColor: 'white',
              borderRight: '1px solid #e5e7eb',
              boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Sidebar />
          </div>
        )}

        {/* Mobile sidebar overlay */}
        {sidebarOpen && (
          <div
            className="fixed inset-0 z-40 bg-black bg-opacity-50 md:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}

        {/* Mobile sidebar */}
        <div className={`md:hidden fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 ease-in-out shadow-xl ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        }`}>
          <Sidebar onClose={() => setSidebarOpen(false)} />
        </div>

        {/* Main content area */}
        <div style={{ flex: 1, backgroundColor: '#f9fafb', minWidth: 0 }}>
          {/* Mobile header - only show on mobile */}
          {!isDesktop && (
            <div className="bg-white border-b border-gray-200 px-4 py-3">
              <button
                type="button"
                className="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
                onClick={() => setSidebarOpen(true)}
              >
                <Menu className="h-6 w-6" />
              </button>
            </div>
          )}

          {/* Page content */}
          <div style={{ padding: '24px' }}>
            <div style={{ maxWidth: '896px', margin: '0 auto' }}>
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Layout; 