"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { api } from "../../lib/api";
import Navbar from "../components/Navbar";
import ProtectedRoute from "../components/ProtectedRoute";
import LoadingSpinner from "../components/LoadingSpinner";
import SkillBadge from "../components/SkillBadge";

export default function DashboardPage() {
  const router = useRouter();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [oauthStatus, setOAuthStatus] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [profileRes, oauthRes] = await Promise.all([
        api.getProfile(),
        api.getOAuthStatus().catch(() => null),
      ]);

      if (profileRes.success) {
        setProfile(profileRes.data);
      }

      if (oauthRes?.success) {
        setOAuthStatus(oauthRes.data);
      }
    } catch (error) {
      if (error.response?.status === 404) {
        // No profile yet, redirect to create profile
        router.push("/edit-profile?firstTime=true");
      }
    } finally {
      setLoading(false);
    }
  };

const handleConnectCalendar = () => {
  const token = localStorage.getItem("accessToken");

  if (!token) {
    router.push("/login");
    return;
  }

  // Use browser redirect, NOT axios
  window.location.href =
    `http://localhost:5678/api/oauth/google/authorize?token=${token}`;
};



  if (loading) {
    return (
      <ProtectedRoute>
        <LoadingSpinner fullScreen message="Loading dashboard..." />
      </ProtectedRoute>
    );
  }

  if (!profile) {
    return (
      <ProtectedRoute>
        <Navbar />
        <div className="min-h-screen bg-amber-50 py-12 px-4">
          <div className="max-w-2xl mx-auto text-center">
            <div className="bg-white rounded-2xl p-12 shadow-xl border border-amber-100">
              <div className="w-20 h-20 bg-gradient-to-br from-amber-600 to-orange-600 rounded-2xl mx-auto mb-6 flex items-center justify-center">
                <svg
                  className="w-12 h-12 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">
                Complete Your Profile
              </h2>
              <p className="text-gray-600 mb-8">
                Let's set up your profile to start finding interview partners
              </p>
              <Link
                href="/edit-profile?firstTime=true"
                className="inline-flex items-center gap-2 bg-gradient-to-r from-amber-600 to-orange-600 text-white px-8 py-4 rounded-xl font-semibold hover:from-amber-700 hover:to-orange-700 transition shadow-lg"
              >
                Create Profile
                <svg
                  className="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 5l7 7-7 7"
                  />
                </svg>
              </Link>
            </div>
          </div>
        </div>
      </ProtectedRoute>
    );
  }

  return (
    <ProtectedRoute>
      <Navbar />
      <div className="min-h-screen bg-amber-50 py-8 px-4">
        <div className="max-w-7xl mx-auto">
          {/* Welcome Section */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">
              Welcome back!
            </h1>
            <p className="text-gray-600 text-lg">
              Ready to practice your interview skills?
            </p>
          </div>

          {/* Profile Completion Alert */}
          {profile.profileCompletionScore < 100 && (
            <div className="mb-8 bg-amber-100 border border-amber-200 rounded-2xl p-6">
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 bg-amber-600 rounded-xl flex items-center justify-center flex-shrink-0">
                    <svg
                      className="w-6 h-6 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                      />
                    </svg>
                  </div>
                  <div>
                    <h3 className="font-bold text-gray-900 text-lg mb-1">
                      Complete Your Profile
                    </h3>
                    <p className="text-gray-700 mb-3">
                      Your profile is {profile.profileCompletionScore}%
                      complete. Complete it to get better matches!
                    </p>
                    <Link
                      href="/edit-profile"
                      className="inline-flex items-center gap-2 bg-amber-600 text-white px-6 py-2.5 rounded-lg font-semibold hover:bg-amber-700 transition text-sm"
                    >
                      Complete Profile
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M9 5l7 7-7 7"
                        />
                      </svg>
                    </Link>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold text-amber-900">
                    {profile.profileCompletionScore}%
                  </div>
                  <div className="text-sm text-gray-600">Complete</div>
                </div>
              </div>
            </div>
          )}

          {/* Google Calendar Integration */}
          {/* Google Calendar Integration */}
          <div className="mb-8 bg-white border border-amber-200 rounded-xl p-5 shadow-md">
            <div className="flex items-center justify-between gap-4">
              <div className="flex items-center gap-4">
                {/* Icon */}
                <div
                  className={`w-11 h-11 rounded-lg flex items-center justify-center ${
                    oauthStatus?.googleCalendar?.connected
                      ? "bg-green-100"
                      : "bg-amber-100"
                  }`}
                >
                  <svg
                    className={`w-6 h-6 ${
                      oauthStatus?.googleCalendar?.connected
                        ? "text-green-600"
                        : "text-amber-600"
                    }`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                    />
                  </svg>
                </div>

                {/* Text */}
                <div>
                  <h3 className="font-semibold text-gray-900">
                    Google Calendar
                  </h3>

                  {oauthStatus?.googleCalendar?.connected ? (
                    <p className="text-sm text-green-600 flex items-center gap-2">
                      <span className="relative flex h-2 w-2">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-2 w-2 bg-green-500"></span>
                      </span>
                      Already connected
                    </p>
                  ) : (
                    <p className="text-sm text-amber-700">
                      Sync interview schedules automatically
                    </p>
                  )}
                </div>
              </div>

              {/* Button */}
              <button
                onClick={handleConnectCalendar}
                disabled={oauthStatus?.googleCalendar?.connected}
                className={`px-5 py-2 rounded-lg text-sm font-semibold transition
        ${
          oauthStatus?.googleCalendar?.connected
            ? "bg-green-100 text-green-700 cursor-not-allowed shadow-inner"
            : "bg-amber-500 text-white hover:bg-amber-600 shadow-md"
        }`}
              >
                {oauthStatus?.googleCalendar?.connected
                  ? "Connected"
                  : "Connect Calendar"}
              </button>
            </div>
          </div>

          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-white rounded-2xl p-6 shadow-lg border border-amber-100">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                  <svg
                    className="w-6 h-6 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-1">
                Current Role
              </h3>
              <p className="text-2xl font-bold text-gray-900">
                {profile.currentRole}
              </p>
              <p className="text-gray-500 text-sm mt-1">{profile.company}</p>
            </div>

            <div className="bg-white rounded-2xl p-6 shadow-lg border border-amber-100">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                  <svg
                    className="w-6 h-6 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"
                    />
                  </svg>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-1">
                Target Role
              </h3>
              <p className="text-2xl font-bold text-gray-900">
                {profile.targetRole}
              </p>
            </div>

            <div className="bg-white rounded-2xl p-6 shadow-lg border border-amber-100">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                  <svg
                    className="w-6 h-6 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-1">
                Experience
              </h3>
              <p className="text-2xl font-bold text-gray-900">
                {profile.yearsOfExperience} Years
              </p>
              <p className="text-gray-500 text-sm mt-1">{profile.timezone}</p>
            </div>
          </div>

          {/* Skills */}
          {profile.skills && profile.skills.length > 0 && (
            <div className="bg-white rounded-2xl p-6 shadow-lg border border-amber-100 mb-8">
              <h3 className="font-bold text-gray-900 text-lg mb-4">
                Your Skills
              </h3>
              <div className="flex flex-wrap gap-2">
                {profile.skills.map((skill) => (
                  <SkillBadge
                    key={skill.id}
                    name={skill.name}
                    category={skill.category}
                  />
                ))}
              </div>
            </div>
          )}

          {/* Quick Actions */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Link
              href="/matches"
              className="bg-gradient-to-br from-amber-500 to-orange-500 rounded-2xl p-8 shadow-xl hover:shadow-2xl transition group"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
                  <svg
                    className="w-8 h-8 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                    />
                  </svg>
                </div>
                <svg
                  className="w-6 h-6 text-white group-hover:translate-x-1 transition"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 5l7 7-7 7"
                  />
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-white mb-2">
                Find Matches
              </h3>
              <p className="text-amber-50">
                Discover interview partners that match your goals
              </p>
            </Link>

            <Link
              href="/search"
              className="bg-white rounded-2xl p-8 shadow-xl border-2 border-amber-200 hover:border-amber-400 hover:shadow-2xl transition group"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="w-14 h-14 bg-gradient-to-br from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                  <svg
                    className="w-8 h-8 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                    />
                  </svg>
                </div>
                <svg
                  className="w-6 h-6 text-amber-600 group-hover:translate-x-1 transition"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 5l7 7-7 7"
                  />
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                Search Users
              </h3>
              <p className="text-gray-600">
                Find specific roles, companies, or skills
              </p>
            </Link>
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}
