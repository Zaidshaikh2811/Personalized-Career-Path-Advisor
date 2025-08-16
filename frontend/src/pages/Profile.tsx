import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNotification } from '../hooks/useNotification';
import { User } from '../types';
import { userAPI, handleApiError } from '../services/api';
import { User as UserIcon, Edit3, Save, X } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const Profile: React.FC = () => {
  const { user: authUser } = useAuth();
  const { addNotification } = useNotification();
  
  const [user, setUser] = useState<User | null>(authUser);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  
  // Form state
  const [username, setUsername] = useState(authUser?.username || '');
  const [email, setEmail] = useState(authUser?.email || '');

  useEffect(() => {
    if (authUser?.id) {
      loadUserProfile();
    }
  }, [authUser?.id]);

  const loadUserProfile = async () => {
    if (!authUser?.id) return;
    
    try {
      setIsLoading(true);
      const userData = await userAPI.getProfile(authUser.id);
      setUser(userData);
      setUsername(userData.username);
      setEmail(userData.email);
    } catch (error) {
      const apiError = handleApiError(error);
      addNotification(apiError.message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSave = async () => {
    if (!user?.id) return;

    if (!username.trim()) {
      addNotification('Username cannot be empty', 'error');
      return;
    }

    if (!email.trim()) {
      addNotification('Email cannot be empty', 'error');
      return;
    }

    try {
      setIsSaving(true);
      const updatedUser = await userAPI.updateProfile(user.id, {
        username: username.trim(),
        email: email.trim(),
      });
      
      setUser(updatedUser);
      setIsEditing(false);
      addNotification('Profile updated successfully!', 'success');
      
      // Update auth user data in localStorage
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        const parsedUser = JSON.parse(storedUser);
        const newUserData = { ...parsedUser, ...updatedUser };
        localStorage.setItem('user', JSON.stringify(newUserData));
      }
    } catch (error) {
      const apiError = handleApiError(error);
      addNotification(apiError.message, 'error');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancel = () => {
    if (user) {
      setUsername(user.username);
      setEmail(user.email);
    }
    setIsEditing(false);
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="text-center py-12">
        <p className="text-slate-400">Unable to load profile data</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-slate-800 rounded-lg border border-slate-700 overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-teal-900/30 to-slate-700/30 px-6 py-8">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 bg-teal-600 rounded-full flex items-center justify-center">
              <UserIcon className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-white">{user.username}</h1>
              <p className="text-slate-300">{user.email}</p>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-white">Profile Information</h2>
            {!isEditing ? (
              <button
                onClick={() => setIsEditing(true)}
                className="flex items-center space-x-2 text-teal-400 hover:text-teal-300 transition-colors"
              >
                <Edit3 className="w-4 h-4" />
                <span>Edit Profile</span>
              </button>
            ) : (
              <div className="flex items-center space-x-2">
                <button
                  onClick={handleSave}
                  disabled={isSaving}
                  className="flex items-center space-x-2 bg-teal-600 hover:bg-teal-700 disabled:bg-teal-800 text-white px-4 py-2 rounded-lg transition-colors"
                >
                  {isSaving ? <LoadingSpinner size="sm" /> : <Save className="w-4 h-4" />}
                  <span>{isSaving ? 'Saving...' : 'Save'}</span>
                </button>
                <button
                  onClick={handleCancel}
                  className="flex items-center space-x-2 text-slate-400 hover:text-white px-4 py-2 rounded-lg transition-colors"
                >
                  <X className="w-4 h-4" />
                  <span>Cancel</span>
                </button>
              </div>
            )}
          </div>

          <div className="space-y-6">
            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Username
              </label>
              {isEditing ? (
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                  placeholder="Enter your username"
                  disabled={isSaving}
                />
              ) : (
                <div className="w-full px-4 py-3 bg-slate-700 rounded-lg text-white">
                  {user.username}
                </div>
              )}
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Email Address
              </label>
              {isEditing ? (
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                  placeholder="Enter your email"
                  disabled={isSaving}
                />
              ) : (
                <div className="w-full px-4 py-3 bg-slate-700 rounded-lg text-white">
                  {user.email}
                </div>
              )}
            </div>

            {/* Account Info */}
            <div className="border-t border-slate-700 pt-6">
              <h3 className="text-lg font-medium text-white mb-4">Account Information</h3>
              <div className="grid md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-400 mb-1">
                    User ID
                  </label>
                  <div className="text-slate-300 font-mono text-sm">
                    {user.id}
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-400 mb-1">
                    Member Since
                  </label>
                  <div className="text-slate-300">
                    {formatDate(user.createdAt)}
                  </div>
                </div>
                {user.updatedAt && (
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-slate-400 mb-1">
                      Last Updated
                    </label>
                    <div className="text-slate-300">
                      {formatDate(user.updatedAt)}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;