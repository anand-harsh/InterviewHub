'use client';

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

const ProtectedRoute = ({ children, requireProfile = false }) => {
  const { user, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!loading) {
      if (!user) {
        router.push('/login');
      } else if (requireProfile && !user.profileCompleted) {
        router.push('/create-profile');
      }
    }
  }, [user, loading, requireProfile, router]);

  if (loading) {
    return <LoadingSpinner fullScreen message="Authenticating..." />;
  }

  if (!user) {
    return null;
  }

  if (requireProfile && !user.profileCompleted) {
    return null;
  }

  return <>{children}</>;
};

export default ProtectedRoute;