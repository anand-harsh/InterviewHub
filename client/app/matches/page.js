'use client';

import { useEffect, useState } from 'react';
import ProtectedRoute from '../components/ProtectedRoute';
import LoadingSpinner from '../components/LoadingSpinner';
import api from '../../lib/api';

function getScoreColor(score) {
  if (score >= 80) return 'bg-green-500';
  if (score >= 60) return 'bg-amber-500';
  if (score >= 40) return 'bg-orange-500';
  return 'bg-red-500';
}

export default function MatchSuggestionsPage() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadMatches();
  }, []);

  const loadMatches = async () => {
    try {
      const res = await api.getMatchSuggestions({
        minScore: 20,
        page: 0,
        size: 50,
      });

      // ApiResponse<PageResponse<MatchResponse>>
      setMatches(res.data.content);
    } catch (err) {
      setError('Failed to load match suggestions');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen />;

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-amber-50 pt-28 pb-12 px-4">
        <div className="max-w-6xl mx-auto">

          {/* Header */}
          <div className="bg-white rounded-2xl shadow-xl border border-amber-100 mb-6 overflow-hidden">
            <div className="h-1.5 bg-gradient-to-r from-amber-500 via-orange-500 to-amber-600" />
            <div className="p-6">
              <h1 className="text-2xl font-bold text-gray-900">
                Match Suggestions
              </h1>
              <p className="text-gray-600 mt-1">
                Profiles with compatibility above 20%
              </p>
            </div>
          </div>

          {error && (
            <div className="bg-red-50 border-l-4 border-red-400 p-4 rounded-r-md mb-6">
              <p className="text-red-700">{error}</p>
            </div>
          )}

          {/* Match List */}
          <div className="bg-white rounded-2xl shadow-xl border border-amber-100 overflow-hidden">
            <div className="divide-y divide-amber-100">

              {matches.map((m) => (
                <div key={m.userId} className="p-6 hover:bg-amber-50 transition">
                  <div className="flex flex-col lg:flex-row gap-6">

                    {/* Left Info */}
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-amber-600 to-orange-600 text-white flex items-center justify-center font-bold">
                          {m.currentJobRole?.charAt(0)}
                        </div>
                        <div>
                          <p className="font-semibold text-gray-900">
                            {m.currentJobRole}
                          </p>
                          <p className="text-sm text-gray-600">
                            {m.company} • {m.yearsOfExperience} yrs • {m.timezone}
                          </p>
                        </div>
                      </div>

                      <p className="mt-3 text-sm text-gray-700">
                        <span className="font-medium">Target:</span> {m.targetRole}
                      </p>

                      {m.matchReasons?.length > 0 && (
                        <div className="mt-2 flex flex-wrap gap-2">
                          {m.matchReasons.map((r, i) => (
                            <span
                              key={i}
                              className="px-3 py-1 text-xs rounded-full bg-amber-100 text-amber-800 font-medium"
                            >
                              {r}
                            </span>
                          ))}
                        </div>
                      )}

                      {m.matchingSkills?.length > 0 && (
                        <p className="mt-2 text-sm text-gray-600">
                          <span className="font-medium">Shared skills:</span>{' '}
                          {Array.from(m.matchingSkills).join(', ')}
                        </p>
                      )}
                    </div>

                    {/* Match Score */}
                    <div className="lg:w-64">
                      <div className="flex justify-between mb-1">
                        <span className="text-sm font-medium text-gray-700">
                          Match Score
                        </span>
                        <span className="font-bold text-gray-900">
                          {m.matchScore}%
                        </span>
                      </div>

                      <div className="w-full h-3 bg-gray-200 rounded-full overflow-hidden">
                        <div
                          className={`h-full ${getScoreColor(m.matchScore)}`}
                          style={{ width: `${m.matchScore}%` }}
                        />
                      </div>

                      <p className="mt-2 text-xs text-gray-600">
                        {m.matchReason}
                      </p>
                    </div>

                  </div>
                </div>
              ))}

              {matches.length === 0 && (
                <div className="p-8 text-center text-gray-600">
                  No matches found above 20% compatibility.
                </div>
              )}

            </div>
          </div>

        </div>
      </div>
    </ProtectedRoute>
  );
}
