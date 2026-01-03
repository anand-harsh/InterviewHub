"use client";

import React from "react";
import Link from "next/link";
import { useAuth } from "../context/AuthContext";
import { FaUser, FaSearch, FaEnvelope, FaSignOutAlt } from "react-icons/fa";

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 pt-6 px-4">
      <div className="max-w-5xl mx-auto">
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-amber-400/20 via-orange-400/20 to-amber-400/20 rounded-2xl blur-xl"></div>
          <div className="absolute inset-0 bg-gradient-to-r from-amber-500/10 via-orange-500/10 to-amber-500/10 rounded-2xl blur-2xl"></div>

          <div className="relative bg-white/70 backdrop-blur-xl border border-white/40 rounded-2xl shadow-2xl px-6 py-3 ring-1 ring-amber-200/30">
            <div className="flex justify-between items-center">
              <Link href="/" className="flex items-center space-x-3 group">
                <div className="w-10 h-10 bg-gradient-to-br from-amber-600 to-orange-600 rounded-xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-all group-hover:scale-105">
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
                      d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                    />
                  </svg>
                </div>
                <span className="text-lg font-bold bg-gradient-to-r from-amber-700 to-orange-700 bg-clip-text text-transparent group-hover:from-amber-800 group-hover:to-orange-800 transition">
                  Interview Matcher
                </span>
              </Link>

              {user && (
                <div className="flex items-center space-x-1">
                  <Link
                    href="/profile"
                    className="flex items-center space-x-2 text-gray-700 hover:text-amber-700 hover:bg-amber-100/50 backdrop-blur-sm px-3 py-2 rounded-xl transition-all font-medium hover:shadow-md"
                  >
                    <FaUser className="text-sm" />
                    <span className="hidden sm:inline">Profile</span>
                  </Link>

                  <Link
                    href="/search"
                    className="flex items-center space-x-2 text-gray-700 hover:text-amber-700 hover:bg-amber-100/50 backdrop-blur-sm px-3 py-2 rounded-xl transition-all font-medium hover:shadow-md"
                  >
                    <FaSearch className="text-sm" />
                    <span className="hidden sm:inline">Search</span>
                  </Link>

                  <Link
                    href="/requests"
                    className="flex items-center space-x-2 text-gray-700 hover:text-amber-700 hover:bg-amber-100/50 backdrop-blur-sm px-3 py-2 rounded-xl transition-all font-medium hover:shadow-md"
                  >
                    <FaEnvelope className="text-sm" />
                    <span className="hidden sm:inline">Requests</span>
                  </Link>

                  <Link
                    href="/matches"
                    className="flex items-center space-x-2 text-gray-700 hover:text-amber-700 hover:bg-amber-100/50 backdrop-blur-sm px-3 py-2 rounded-xl transition-all font-medium hover:shadow-md"
                  >
                    <FaEnvelope className="text-sm" />
                    <span className="hidden sm:inline">Matches</span>
                  </Link>
                  <Link
                    href="/dashboard"
                    className="flex items-center space-x-2 text-gray-700 hover:text-amber-700 hover:bg-amber-100/50 backdrop-blur-sm px-3 py-2 rounded-xl transition-all font-medium hover:shadow-md"
                  >
                    <FaEnvelope className="text-sm" />
                    <span className="hidden sm:inline">Dashboard</span>
                  </Link>
                  <button
                    onClick={logout}
                    className="flex items-center space-x-2 text-white bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-700 hover:to-orange-700 px-4 py-2 rounded-xl transition-all font-medium shadow-lg hover:shadow-xl ml-2 hover:scale-105"
                  >
                    <FaSignOutAlt className="text-sm" />
                    <span className="hidden sm:inline">Logout</span>
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
