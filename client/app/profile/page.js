'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { FaEdit, FaBriefcase, FaBuilding, FaClock, FaGlobe } from 'react-icons/fa';
import api from '../../lib/api';
import ProtectedRoute from '../components/ProtectedRoute';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Profile() {
  const router = useRouter();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const response = await api.getProfile();
      setProfile(response.data);
    } catch (err) {
      if (err.response?.status === 404) {
        router.push('/create-profile');
      } else {
        setError('Failed to load profile');
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  if (error) {
    return (
      <div className="min-h-screen bg-amber-50 pt-28 pb-12 px-4">
        <div className="max-w-4xl mx-auto">
          <div className="bg-red-50 border-l-4 border-red-400 p-4 rounded-r-md">
            <p className="text-red-700">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="min-h-screen bg-amber-50 pt-28 pb-12 px-4">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">No Profile Found</h2>
          <Link
            href="/create-profile"
            className="inline-block bg-gradient-to-r from-amber-600 to-orange-600 text-white px-6 py-3 rounded-xl hover:from-amber-700 hover:to-orange-700 font-semibold shadow-md"
          >
            Create Profile
          </Link>
        </div>
      </div>
    );
  }

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-amber-50 pt-28 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-20 right-20 w-72 h-72 bg-amber-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-pulse"></div>
          <div className="absolute bottom-20 left-20 w-72 h-72 bg-orange-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-pulse"></div>
        </div>

        <div className="max-w-4xl mx-auto relative z-10">
          {/* Header Card */}
          <div className="bg-white rounded-2xl shadow-xl border border-amber-100 overflow-hidden mb-6">
            <div className="h-1.5 bg-gradient-to-r from-amber-500 via-orange-500 to-amber-600"></div>
            
            <div className="p-8">
              <div className="flex flex-col sm:flex-row justify-between items-start gap-4">
                <div className="flex items-center gap-4">
                  <div className="w-20 h-20 bg-gradient-to-br from-amber-600 to-orange-600 rounded-2xl flex items-center justify-center shadow-lg flex-shrink-0">
                    <span className="text-3xl font-bold text-white">
                      {profile.currentRole.charAt(0)}
                    </span>
                  </div>
                  <div>
                    <h1 className="text-3xl font-bold text-gray-900">{profile.currentRole}</h1>
                    <p className="text-gray-600 mt-1">{profile.company}</p>
                  </div>
                </div>
                <Link
                  href="/edit-profile"
                  className="inline-flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-amber-600 to-orange-600 text-white rounded-xl font-semibold hover:from-amber-700 hover:to-orange-700 transition shadow-md"
                >
                  <FaEdit />
                  Edit Profile
                </Link>
              </div>
            </div>
          </div>

          {/* Main Content Card */}
          <div className="bg-white rounded-2xl shadow-xl border border-amber-100 overflow-hidden">
            <div className="h-1.5 bg-gradient-to-r from-amber-500 via-orange-500 to-amber-600"></div>
            
            <div className="p-8 space-y-8">
              {/* Current Position */}
              <div>
                <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <div className="w-1 h-6 bg-gradient-to-b from-amber-600 to-orange-600 rounded-full"></div>
                  Current Position
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center flex-shrink-0 shadow-sm">
                        <FaBriefcase className="text-amber-600" />
                      </div>
                      <div>
                        <p className="text-xs text-gray-600 font-medium mb-1">Role</p>
                        <p className="font-semibold text-gray-900">{profile.currentRole}</p>
                      </div>
                    </div>
                  </div>

                  <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center flex-shrink-0 shadow-sm">
                        <FaBuilding className="text-amber-600" />
                      </div>
                      <div>
                        <p className="text-xs text-gray-600 font-medium mb-1">Company</p>
                        <p className="font-semibold text-gray-900">{profile.company}</p>
                      </div>
                    </div>
                  </div>

                  <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center flex-shrink-0 shadow-sm">
                        <FaClock className="text-amber-600" />
                      </div>
                      <div>
                        <p className="text-xs text-gray-600 font-medium mb-1">Experience</p>
                        <p className="font-semibold text-gray-900">{profile.yearsOfExperience} years</p>
                      </div>
                    </div>
                  </div>

                  <div className="p-4 bg-amber-50 rounded-xl border border-amber-100">
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center flex-shrink-0 shadow-sm">
                        <FaGlobe className="text-amber-600" />
                      </div>
                      <div>
                        <p className="text-xs text-gray-600 font-medium mb-1">Timezone</p>
                        <p className="font-semibold text-gray-900">{profile.timezone}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Target Role */}
              <div>
                <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <div className="w-1 h-6 bg-gradient-to-b from-amber-600 to-orange-600 rounded-full"></div>
                  Target Role
                </h2>
                <div className="p-5 bg-gradient-to-br from-amber-50 to-orange-50 rounded-xl border border-amber-200">
                  <p className="text-amber-900 font-semibold text-lg">{profile.targetRole}</p>
                </div>
              </div>

              {/* Skills */}
              <div>
                <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <div className="w-1 h-6 bg-gradient-to-b from-amber-600 to-orange-600 rounded-full"></div>
                  Skills
                </h2>
                <div className="flex flex-wrap gap-2">
                  {profile.skills.map((skill) => (
                    <span
                      key={skill.id}
                      className="px-4 py-2 bg-white text-amber-800 rounded-full font-medium border border-amber-200 shadow-sm hover:shadow-md transition"
                    >
                      {skill.name}
                    </span>
                  ))}
                </div>
              </div>

              {/* Bio */}
              {profile.bio && (
                <div>
                  <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                    <div className="w-1 h-6 bg-gradient-to-b from-amber-600 to-orange-600 rounded-full"></div>
                    About
                  </h2>
                  <p className="text-gray-700 leading-relaxed bg-gray-50 p-5 rounded-xl border border-gray-200">
                    {profile.bio}
                  </p>
                </div>
              )}

              {/* Availability */}
              <div>
                <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
                  <div className="w-1 h-6 bg-gradient-to-b from-amber-600 to-orange-600 rounded-full"></div>
                  Availability
                </h2>
                <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl border border-gray-200">
                  <div className={`w-3 h-3 rounded-full ${profile.availableForInterview ? 'bg-green-500' : 'bg-gray-400'} shadow-lg`} />
                  <span className="text-gray-700 font-medium">
                    {profile.availableForInterview
                      ? 'Available for interview practice'
                      : 'Not available for interviews'}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Link
              href="/search"
              className="flex items-center justify-center gap-2 bg-gradient-to-r from-amber-600 to-orange-600 text-white py-3.5 px-6 rounded-xl font-semibold hover:from-amber-700 hover:to-orange-700 transition shadow-md hover:shadow-lg"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              Find Interview Partners
            </Link>
            <Link
              href="/requests"
              className="flex items-center justify-center gap-2 bg-white text-amber-700 border-2 border-amber-600 py-3.5 px-6 rounded-xl font-semibold hover:bg-amber-50 transition shadow-sm hover:shadow-md"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              View Requests
            </Link>
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}