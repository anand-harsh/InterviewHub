'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { api } from '../../lib/api';
import Navbar from '../components/Navbar';
import ProtectedRoute from '../components/ProtectedRoute';
import LoadingSpinner from '../components/LoadingSpinner';
import SkillBadge from '../components/SkillBadge';

const TIMEZONES = ['PST', 'MST', 'CST', 'EST', 'GMT', 'CET', 'IST', 'JST', 'AEST'];

export default function EditProfilePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const isFirstTime = searchParams.get('firstTime') === 'true';

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    currentJobRole: '',
    company: '',
    yearsOfExperience: 0,
    timezone: 'EST',
    targetRole: '',
    bio: '',
    profileVisibility: 'PUBLIC',
  });

  const [skills, setSkills] = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [skillSearch, setSkillSearch] = useState('');
  const [skillSearchResults, setSkillSearchResults] = useState([]);
  const [showSkillSearch, setShowSkillSearch] = useState(false);

  useEffect(() => {
    loadProfile();
    loadSkills();
  }, []);

  const loadProfile = async () => {
    try {
      const res = await api.getProfile();
      if (res?.data) {
        setFormData({
          currentJobRole: res.data.currentJobRole || '',
          company: res.data.company || '',
          yearsOfExperience: res.data.yearsOfExperience ?? 0,
          timezone: res.data.timezone || 'EST',
          targetRole: res.data.targetRole || '',
          bio: res.data.bio || '',
          profileVisibility: res.data.profileVisibility || 'PUBLIC',
        });
        setSelectedSkills(res.data.skills || []);
      }
    } catch (_) {
      // first time user → ignore
    } finally {
      setLoading(false);
    }
  };

  const loadSkills = async () => {
    try {
      const res = await api.getAllSkills({ page: 0, size: 100 });
      setSkills(res?.data?.content || []);
    } catch (e) {
      console.error('Skill load failed', e);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'yearsOfExperience' ? Number(value) : value,
    }));
  };

  const handleSkillSearch = (e) => {
    const q = e.target.value;
    setSkillSearch(q);

    if (!q) {
      setShowSkillSearch(false);
      return;
    }

    setSkillSearchResults(
      skills.filter(
        (s) =>
          s.name.toLowerCase().includes(q.toLowerCase()) &&
          !selectedSkills.some((x) => x.id === s.id)
      )
    );
    setShowSkillSearch(true);
  };

  const addSkill = (skill) => {
    setSelectedSkills((prev) => [...prev, skill]);
    setSkillSearch('');
    setShowSkillSearch(false);
  };

  const removeSkill = (id) => {
    setSelectedSkills((prev) => prev.filter((s) => s.id !== id));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      if (isFirstTime) {
        await api.createProfile(formData);
      } else {
        await api.updateProfile(formData);
      }

      await api.addSkillsToProfile({
        skillIds: selectedSkills.map((s) => s.id),
      });

      setSuccess('Profile saved successfully');
      setTimeout(() => router.push('/dashboard'), 1200);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to save profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <ProtectedRoute>
        <LoadingSpinner fullScreen message="Loading profile..." />
      </ProtectedRoute>
    );
  }

  return (
    <ProtectedRoute>
      <Navbar />

      <div className="min-h-screen bg-amber-50 py-10 px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-2xl shadow-xl p-8 border border-amber-100">
          <h1 className="text-3xl font-bold mb-6">
            {isFirstTime ? 'Create Profile' : 'Edit Profile'}
          </h1>

          {error && <p className="text-red-600 mb-4">{error}</p>}
          {success && <p className="text-green-600 mb-4">{success}</p>}

          <form onSubmit={handleSubmit} className="space-y-6">
            <input
              name="currentJobRole"
              value={formData.currentJobRole}
              onChange={handleChange}
              placeholder="Current Role"
              className="input"
              required
            />

            <input
              name="company"
              value={formData.company}
              onChange={handleChange}
              placeholder="Company"
              className="input"
              required
            />

            <input
              type="number"
              name="yearsOfExperience"
              value={formData.yearsOfExperience}
              onChange={handleChange}
              min="0"
              className="input"
              required
            />

            <select
              name="timezone"
              value={formData.timezone}
              onChange={handleChange}
              className="input"
            >
              {TIMEZONES.map((t) => (
                <option key={t}>{t}</option>
              ))}
            </select>

            <input
              name="targetRole"
              value={formData.targetRole}
              onChange={handleChange}
              placeholder="Target Role"
              className="input"
              required
            />

            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              placeholder="Bio"
              className="input"
            />

            {/* SKILLS */}
            <div>
              <input
                value={skillSearch}
                onChange={handleSkillSearch}
                placeholder="Search skills"
                className="input"
              />

              {showSkillSearch && (
                <div className="border rounded mt-2 bg-white">
                  {skillSearchResults.map((s) => (
                    <div
                      key={s.id}
                      onClick={() => addSkill(s)}
                      className="px-3 py-2 hover:bg-amber-100 cursor-pointer"
                    >
                      {s.name}
                    </div>
                  ))}
                </div>
              )}

              <div className="flex flex-wrap mt-3 gap-2">
                {selectedSkills.map((s) => (
                  <SkillBadge key={s.id} skill={s} onRemove={removeSkill} />
                ))}
              </div>
            </div>

            <button
              disabled={saving}
              className="w-full bg-amber-500 hover:bg-amber-600 text-white py-3 rounded-xl font-semibold"
            >
              {saving ? 'Saving...' : 'Save Profile'}
            </button>
          </form>
        </div>
      </div>
    </ProtectedRoute>
  );
}
