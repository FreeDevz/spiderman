import React from 'react';

const ProfilePage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Profile</h1>
        <p className="text-gray-600">Manage your personal information</p>
      </div>
      
      <div className="bg-white rounded-lg shadow p-8 text-center">
        <h2 className="text-lg font-semibold text-gray-900 mb-2">User Profile</h2>
        <p className="text-gray-500">This page will contain user profile management and account information.</p>
      </div>
    </div>
  );
};

export default ProfilePage; 