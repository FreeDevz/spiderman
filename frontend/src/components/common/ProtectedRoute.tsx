import React from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';
import type { RootState } from '../../store';
import { ROUTES } from '../../constants';

const ProtectedRoute: React.FC = () => {
  const { isAuthenticated, loading } = useSelector((state: RootState) => state.auth);

  // Show loading spinner while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} replace />;
  }

  // Render the protected route content
  return <Outlet />;
};

export default ProtectedRoute; 