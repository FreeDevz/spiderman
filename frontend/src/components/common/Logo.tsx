import React from 'react';

interface LogoProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const Logo: React.FC<LogoProps> = ({ size = 'md', className = '' }) => {
  const sizeClasses = {
    sm: 'w-8 h-8',
    md: 'w-12 h-12',
    lg: 'w-16 h-16'
  };

  return (
    <div className={`${sizeClasses[size]} ${className}`}>
      <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
        {/* Background circle with gradient */}
        <defs>
          <linearGradient id="bgGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style={{ stopColor: '#667eea', stopOpacity: 1 }} />
            <stop offset="100%" style={{ stopColor: '#764ba2', stopOpacity: 1 }} />
          </linearGradient>
          
          <linearGradient id="checkGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style={{ stopColor: '#4ade80', stopOpacity: 1 }} />
            <stop offset="100%" style={{ stopColor: '#22c55e', stopOpacity: 1 }} />
          </linearGradient>
          
          <linearGradient id="dashGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style={{ stopColor: '#60a5fa', stopOpacity: 1 }} />
            <stop offset="100%" style={{ stopColor: '#3b82f6', stopOpacity: 1 }} />
          </linearGradient>
        </defs>
        
        {/* Main background circle */}
        <circle cx="100" cy="100" r="90" fill="url(#bgGradient)" opacity="0.9"/>
        
        {/* Checklist items representation */}
        <g transform="translate(40, 50)">
          {/* Completed task (with checkmark) */}
          <rect x="0" y="0" width="120" height="8" rx="4" fill="white" opacity="0.2"/>
          <circle cx="10" cy="4" r="6" fill="url(#checkGradient)"/>
          <path d="M6 4 L9 7 L14 1" stroke="white" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
          
          {/* Pending task */}
          <rect x="0" y="25" width="120" height="8" rx="4" fill="white" opacity="0.3"/>
          <circle cx="10" cy="29" r="6" fill="white" opacity="0.4"/>
          
          {/* Another pending task */}
          <rect x="0" y="50" width="90" height="8" rx="4" fill="white" opacity="0.25"/>
          <circle cx="10" cy="54" r="6" fill="white" opacity="0.4"/>
        </g>
        
        {/* Dashboard/Analytics element */}
        <g transform="translate(130, 110)">
          {/* Mini chart bars representing dashboard */}
          <rect x="0" y="15" width="8" height="25" rx="2" fill="url(#dashGradient)" opacity="0.8"/>
          <rect x="12" y="8" width="8" height="32" rx="2" fill="url(#dashGradient)" opacity="0.9"/>
          <rect x="24" y="20" width="8" height="20" rx="2" fill="url(#dashGradient)" opacity="0.7"/>
          
          {/* Small circle indicator */}
          <circle cx="20" cy="0" r="3" fill="#fbbf24"/>
        </g>
        
        {/* Subtle geometric accent */}
        <circle cx="160" cy="60" r="2" fill="white" opacity="0.4"/>
        <circle cx="50" cy="150" r="2" fill="white" opacity="0.3"/>
        
        {/* App name */}
        <text x="100" y="175" fontFamily="Arial, sans-serif" fontSize="18" fontWeight="bold" textAnchor="middle" fill="white" opacity="0.9">TODO</text>
      </svg>
    </div>
  );
};

export default Logo; 