'use client';

import Link from 'next/link';
import SkillBadge from './SkillBadge';

export default function MatchCard({ match }) {
  return (
    <div className="bg-white rounded-2xl p-6 shadow-lg border border-amber-100 hover:shadow-xl transition group">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-gradient-to-br from-amber-500 to-orange-500 rounded-full flex items-center justify-center">
            <span className="text-white font-bold text-lg">
              {match.name?.[0]?.toUpperCase()}
            </span>
          </div>
          <div>
            <h3 className="font-bold text-gray-900 text-lg">{match.name}</h3>
            <p className="text-gray-600 text-sm">{match.currentJobRole} at {match.company}</p>
          </div>
        </div>
        
        <div className="flex flex-col items-end gap-1">
          <div className="px-3 py-1 bg-gradient-to-r from-amber-100 to-orange-100 rounded-full">
            <span className="text-amber-900 font-bold text-sm">{match.matchScore}% Match</span>
          </div>
          {match.availableForInterview && (
            <span className="text-green-600 text-xs font-medium">● Available</span>
          )}
        </div>
      </div>

      <div className="space-y-3 mb-4">
        <div className="flex items-center gap-2 text-sm">
          <svg className="w-4 h-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
          <span className="text-gray-700">{match.yearsOfExperience} years experience</span>
        </div>
        
        <div className="flex items-center gap-2 text-sm">
          <svg className="w-4 h-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span className="text-gray-700">{match.timezone}</span>
        </div>

        <div className="flex items-center gap-2 text-sm">
          <svg className="w-4 h-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
          </svg>
          <span className="text-gray-700">Target: {match.targetRole}</span>
        </div>
      </div>

      <div className="mb-4">
        <p className="text-amber-700 text-sm font-medium mb-2">
          <svg className="w-4 h-4 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          {match.matchReason}
        </p>
      </div>

      {match.matchingSkills && match.matchingSkills.length > 0 && (
        <div className="mb-4">
          <p className="text-xs text-gray-500 mb-2">Matching Skills:</p>
          <div className="flex flex-wrap gap-2">
            {match.matchingSkills.slice(0, 5).map((skill, index) => (
              <SkillBadge key={index} name={skill} variant="match" />
            ))}
            {match.matchingSkills.length > 5 && (
              <span className="text-xs text-gray-500 px-2 py-1">
                +{match.matchingSkills.length - 5} more
              </span>
            )}
          </div>
        </div>
      )}

      {match.bio && (
        <p className="text-gray-600 text-sm mb-4 line-clamp-2">{match.bio}</p>
      )}

      <Link
        href={`/matches/${match.userId}`}
        className="block w-full text-center bg-gradient-to-r from-amber-600 to-orange-600 text-white py-2.5 rounded-xl font-semibold hover:from-amber-700 hover:to-orange-700 transition shadow-md hover:shadow-lg"
      >
        View Profile & Send Request
      </Link>
    </div>
  );
}