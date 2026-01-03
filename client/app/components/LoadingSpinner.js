'use client';

import React from 'react';

const LoadingSpinner = ({ message = 'Loading...', fullScreen = false }) => {
  const containerClass = fullScreen 
    ? "fixed inset-0 flex items-center justify-center bg-amber-50 z-50"
    : "flex flex-col items-center justify-center min-h-[200px]";

  return (
    <div className={containerClass}>
      <div className="relative">
        <div className="w-16 h-16 border-4 border-amber-200 border-t-amber-600 rounded-full animate-spin"></div>
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="w-8 h-8 bg-gradient-to-br from-amber-400 to-orange-500 rounded-full animate-pulse"></div>
        </div>
      </div>
      <p className="mt-6 text-gray-700 font-medium">{message}</p>
    </div>
  );
};

export default LoadingSpinner;