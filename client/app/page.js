'use client';

import Link from 'next/link';

export default function Home() {
  return (
    <div className="min-h-screen bg-amber-50">
      {/* Decorative elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 right-20 w-96 h-96 bg-amber-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-pulse"></div>
        <div className="absolute bottom-20 left-20 w-96 h-96 bg-orange-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-pulse"></div>
      </div>

      <div className="relative z-10 max-w-6xl mx-auto px-4 pt-32 pb-16">
        {/* Hero Section */}
        <div className="text-center mb-16">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-amber-600 to-orange-600 rounded-2xl mb-6 shadow-xl">
            <svg className="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          
          <h1 className="text-6xl font-bold text-gray-900 mb-6">
            Master Your
            <span className="block bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">
              Interview Skills
            </span>
          </h1>
          
          <p className="text-xl text-gray-600 max-w-2xl mx-auto mb-8">
            Connect with peers, practice mock interviews, and ace your next job opportunity.
            Build confidence through real interview scenarios.
          </p>

          <div className="flex gap-4 justify-center">
            <Link
              href="/register"
              className="inline-flex items-center gap-2 bg-gradient-to-r from-amber-600 to-orange-600 text-white px-8 py-4 rounded-xl font-semibold hover:from-amber-700 hover:to-orange-700 transition shadow-lg hover:shadow-xl text-lg"
            >
              Get Started Free
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
            
            <Link
              href="/login"
              className="inline-flex items-center gap-2 bg-white text-amber-700 border-2 border-amber-600 px-8 py-4 rounded-xl font-semibold hover:bg-amber-50 transition shadow-md hover:shadow-lg text-lg"
            >
              Sign In
            </Link>
          </div>
        </div>

        {/* Features Section */}
        <div className="grid md:grid-cols-3 gap-8 mt-20">
          <div className="bg-white rounded-2xl p-8 shadow-xl border border-amber-100 hover:shadow-2xl transition">
            <div className="w-14 h-14 bg-gradient-to-br from-amber-600 to-orange-600 rounded-xl flex items-center justify-center mb-4 shadow-md">
              <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">Find Your Match</h3>
            <p className="text-gray-600">Connect with peers who share your interview goals and skill level.</p>
          </div>

          <div className="bg-white rounded-2xl p-8 shadow-xl border border-amber-100 hover:shadow-2xl transition">
            <div className="w-14 h-14 bg-gradient-to-br from-amber-600 to-orange-600 rounded-xl flex items-center justify-center mb-4 shadow-md">
              <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">Schedule Easily</h3>
            <p className="text-gray-600">Book mock interviews at times that work for both you and your partner.</p>
          </div>

          <div className="bg-white rounded-2xl p-8 shadow-xl border border-amber-100 hover:shadow-2xl transition">
            <div className="w-14 h-14 bg-gradient-to-br from-amber-600 to-orange-600 rounded-xl flex items-center justify-center mb-4 shadow-md">
              <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">Practice & Improve</h3>
            <p className="text-gray-600">Get real feedback and improve your interview skills with every session.</p>
          </div>
        </div>

        {/* CTA Section */}
        <div className="mt-20 bg-gradient-to-r from-amber-600 to-orange-600 rounded-3xl p-12 shadow-2xl">
          <div className="text-center text-white">
            <h2 className="text-4xl font-bold mb-4">Ready to Start?</h2>
            <p className="text-xl mb-8 text-amber-50">Join hundreds of professionals practicing together</p>
            <Link
              href="/register"
              className="inline-flex items-center gap-2 bg-white text-amber-700 px-8 py-4 rounded-xl font-semibold hover:bg-amber-50 transition shadow-lg hover:shadow-xl text-lg"
            >
              Create Your Account
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}