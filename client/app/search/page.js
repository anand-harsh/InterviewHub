"use client";

import { useState } from "react";
import { api } from "../../lib/api";
import ProtectedRoute from "../components/ProtectedRoute";
import LoadingSpinner from "../components/LoadingSpinner";
import { useRouter } from "next/navigation";

export default function SearchPage() {
  const router = useRouter();

  const [filters, setFilters] = useState({
    currentJobRole: "",
    targetRole: "",
    company: "",
    skills: "",
    minExperience: "",
    maxExperience: "",
    timezone: "",
    availableOnly: true,
  });

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const searchProfiles = async () => {
    setLoading(true);
    setHasSearched(true);

    try {
      // 🔑 remove empty filters
      const cleanedParams = Object.fromEntries(
        Object.entries(filters).filter(
          ([key, v]) =>
            key !== "availableOnly" && v !== "" && v !== null && v !== undefined
        )
      );

      if (Object.keys(cleanedParams).length === 0) {
        setResults([]);
        return;
      }

      const res = await api.searchMatches({
        ...cleanedParams,
        page: 0,
        size: 20,
      });

      if (res?.success) {
        setResults(res.data.content || []);
      } else {
        setResults([]);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-amber-50 pt-28 px-4 pb-10">
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* ================= FILTERS ================= */}
          <div className="bg-white rounded-xl shadow border border-amber-100 p-4">
            <h3 className="font-bold text-gray-900 mb-4">Filters</h3>

            <FilterInput
              label="Current Role"
              name="currentJobRole"
              value={filters.currentJobRole}
              onChange={handleChange}
            />
            <FilterInput
              label="Target Role"
              name="targetRole"
              value={filters.targetRole}
              onChange={handleChange}
            />
            <FilterInput
              label="Company"
              name="company"
              value={filters.company}
              onChange={handleChange}
            />
            <FilterInput
              label="Skills (comma separated)"
              name="skills"
              value={filters.skills}
              onChange={handleChange}
            />

            <div className="flex gap-2">
              <FilterInput
                label="Min Exp"
                name="minExperience"
                type="number"
                value={filters.minExperience}
                onChange={handleChange}
              />
              <FilterInput
                label="Max Exp"
                name="maxExperience"
                type="number"
                value={filters.maxExperience}
                onChange={handleChange}
              />
            </div>

            <FilterInput
              label="Timezone"
              name="timezone"
              value={filters.timezone}
              onChange={handleChange}
            />

            <label className="flex items-center gap-2 text-sm mt-3">
              <input
                type="checkbox"
                name="availableOnly"
                checked={filters.availableOnly}
                onChange={handleChange}
              />
              Only available users
            </label>

            <button
              onClick={searchProfiles}
              className="mt-4 w-full bg-amber-600 text-white py-2 rounded-lg font-semibold hover:bg-amber-700 transition"
            >
              Apply Filters
            </button>
          </div>

          {/* ================= RESULTS ================= */}
          <div className="lg:col-span-3">
            {loading ? (
              <LoadingSpinner />
            ) : !hasSearched ? (
              <div className="bg-white rounded-xl shadow border border-amber-100 p-10 text-center text-gray-600">
                Apply filters and click <b>Apply Filters</b> to search profiles
              </div>
            ) : results.length === 0 ? (
              <div className="bg-white rounded-xl shadow border border-amber-100 p-10 text-center text-gray-600">
                No profiles found matching your filters
              </div>
            ) : (
              <div className="bg-white rounded-xl shadow border border-amber-100 divide-y">
                {results.map((r) => (
                  <div
                    key={r.userId}
                    className="p-5 flex justify-between items-center hover:bg-amber-50 transition"
                  >
                    <div>
                      <h3 className="font-semibold text-gray-900">
                        {r.currentJobRole}
                      </h3>
                      <p className="text-sm text-gray-600">
                        {r.company} • {r.yearsOfExperience} yrs • {r.timezone}
                      </p>
                      <p className="text-xs text-amber-700 mt-1">
                        {r.matchReason}
                      </p>
                    </div>

                    <div className="flex gap-3">
                      <button className="px-4 py-2 bg-amber-500 text-white rounded-lg text-sm hover:bg-amber-600">
                        Send Request
                      </button>
                      <button
                        onClick={() => router.push(`/profile/${r.userId}`)}
                        className="px-4 py-2 border border-amber-500 text-amber-700 rounded-lg text-sm hover:bg-amber-50"
                      >
                        View Profile
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}

function FilterInput({ label, ...props }) {
  return (
    <div className="mb-3">
      <label className="text-sm font-medium text-gray-700">{label}</label>
      <input
        {...props}
        className="w-full mt-1 px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
      />
    </div>
  );
}
