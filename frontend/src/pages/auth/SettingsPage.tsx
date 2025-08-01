import React from 'react';

const SettingsPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
        <p className="text-gray-600">Manage your account preferences and settings</p>
      </div>
      
      <div className="bg-white rounded-lg shadow p-8 text-center">
        <h2 className="text-lg font-semibold text-gray-900 mb-2">User Settings</h2>
        <p className="text-gray-500">This page will contain user settings, theme preferences, and notification settings.</p>
      </div>
    </div>
  );
};

export default SettingsPage; 