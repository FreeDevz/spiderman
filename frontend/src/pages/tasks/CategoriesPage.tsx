import React from 'react';

const CategoriesPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Categories</h1>
        <p className="text-gray-600">Organize your tasks with custom categories</p>
      </div>
      
      <div className="bg-white rounded-lg shadow p-8 text-center">
        <h2 className="text-lg font-semibold text-gray-900 mb-2">Category Management</h2>
        <p className="text-gray-500">This page will contain category creation, editing, and management functionality.</p>
      </div>
    </div>
  );
};

export default CategoriesPage; 