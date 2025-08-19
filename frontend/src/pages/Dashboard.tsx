import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNotification } from '../hooks/useNotification';
import { Activity, Recommendation } from '../types';
import { activityAPI, aiAPI, handleApiError } from '../services/api';
import { Plus, Clock, Trash2, Brain, TrendingUp, Star } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import { toast } from 'react-toastify';

const activityTypes = [
  'RUNNING', 'WALKING', 'SWIMMING', 'WEIGHT_TRAINING', 'CYCLING', 'YOGA',
  'HIIT', 'DANCE', 'BOXING', 'PILATES', 'AEROBICS', 'STRETCHING',
  'MARTIAL_ARTS', 'ROCK_CLIMBING', 'HIKING', 'SKATING', 'SKIING',
  'SNOWBOARDING', 'SURFING', 'GYMNASTICS', 'CROSS_TRAINING'
];

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const { addNotification } = useNotification();
  
  const [activities, setActivities] = useState<Activity[]>([]);
  const [recommendations, setRecommendations] = useState<Recommendation[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [isLoadingRecommendations, setIsLoadingRecommendations] = useState(false);
  
  // Form state
  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState<'planned' | 'in_progress' | 'completed'>('planned');
  const [activityType, setActivityType] = useState('');
  const [duration, setDuration] = useState(1);
  const [caloriesBurned, setCaloriesBurned] = useState(0);
  const [startTime, setStartTime] = useState('');
  const [additionalMetrics, setAdditionalMetrics] = useState(''); // JSON string

  // Recommendation modal state
  const [selectedRecommendation, setSelectedRecommendation] = useState<Recommendation | null>(null);
  const [showRecommendationModal, setShowRecommendationModal] = useState(false);

  // Pagination and sorting state
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [sortBy, setSortBy] = useState('startTime');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');
  const [filterActivityType, setFilterActivityType] = useState('');
  const [totalPages, setTotalPages] = useState(1);

  // Recommendation pagination and sorting state
  const [recommendationPage, setRecommendationPage] = useState(0);
  const [recommendationSize, setRecommendationSize] = useState(5);
  const [recommendationSortBy, setRecommendationSortBy] = useState('createdAt');
  const [recommendationSortDirection, setRecommendationSortDirection] = useState<'asc' | 'desc'>('desc');
  const [recommendationTotalPages, setRecommendationTotalPages] = useState(1);

  useEffect(() => {
    let isActive = true;
    const fetchActivities = async () => {
      setIsLoading(true);
      try {
        const response = await activityAPI.getActivities(page, size, sortBy, sortDirection, { activityType: filterActivityType });
        if (isActive) {
          setActivities(response.content);
          setTotalPages(response.totalPages);
        }
      } catch (error) {
        if (isActive) {
          const apiError = handleApiError(error);
          toast.error(apiError.message);
        }
      } finally {
        if (isActive) setIsLoading(false);
      }
    };
    fetchActivities();
    return () => { isActive = false; };
  }, [page, size, sortBy, sortDirection, filterActivityType]);

  useEffect(() => {
    let isActive = true;
    const fetchRecommendations = async () => {
      setIsLoadingRecommendations(true);
      try {
        const recs = await aiAPI.getAllRecommendations(
          recommendationPage,
          recommendationSize,
          recommendationSortBy,
          recommendationSortDirection
        );
        if (isActive) {
          setRecommendations(recs.content);
          setRecommendationTotalPages(recs.totalPages);
          toast.info('AI recommendations updated!');
        }
      } catch (error) {
        if (isActive) {
          const apiError = handleApiError(error);
          toast.error(`Recommendations: ${apiError.message}`);
        }
      } finally {
        if (isActive) setIsLoadingRecommendations(false);
      }
    };
    fetchRecommendations();
    return () => { isActive = false; };
  }, [recommendationPage, recommendationSize, recommendationSortBy, recommendationSortDirection]);

  const createActivity = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!title.trim()) {
      toast.error('Please enter a title for your activity');
      return;
    }
    if (!activityType) {
      toast.error('Please select an activity type');
      return;
    }
    if (!duration || duration < 1) {
      toast.error('Duration must be at least 1 minute');
      return;
    }
    if (caloriesBurned < 0) {
      toast.error('Calories burned must be non-negative');
      return;
    }
    if (!startTime) {
      toast.error('Please select a start time');
      return;
    }
    let metricsObj = undefined;
    if (additionalMetrics.trim()) {
      try {
        metricsObj = JSON.parse(additionalMetrics);
      } catch {
        toast.error('Additional metrics must be valid JSON');
        return;
      }
    }
    try {
      setIsCreating(true);
      const newActivity = await activityAPI.createActivity({
        title,
        description,
        status,
        activityType,
        duration,
        caloriesBurned,
        startTime,
        additionalMetrics: metricsObj,
      });
      setActivities(prev => [newActivity, ...prev]);
      setTitle('');
      setDescription('');
      setStatus('planned');
      setActivityType('');
      setDuration(1);
      setCaloriesBurned(0);
      setStartTime('');
      setAdditionalMetrics('');
      setShowForm(false);
      toast.success('Activity created successfully!');

      loadRecommendations(newActivity.id);
    } catch (error) {
      const apiError = handleApiError(error);
      toast.error(apiError.message);
    } finally {
      setIsCreating(false);
    }
  };

  const deleteActivity = async (id: string) => {
    try {

      await activityAPI.deleteActivity(id);
      setActivities(prev => prev.filter(activity => activity.id !== id));
      toast.success('Activity deleted successfully');
    } catch (error) {
      const apiError = handleApiError(error);
      toast.error(apiError.message);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'completed':
        return 'text-emerald-400 bg-emerald-900/30';
      case 'in_progress':
        return 'text-yellow-400 bg-yellow-900/30';
      case 'planned':
        return 'text-blue-400 bg-blue-900/30';
      default:
        return 'text-slate-400 bg-slate-900/30';
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className=" ">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-teal-900/30 to-slate-800/30 rounded-lg p-6 border border-slate-700">
        <h1 className="text-3xl font-bold text-white mb-2">
          Welcome back, {user?.username}!
        </h1>
        <p className="text-slate-300">
          Ready to track some activities and get AI-powered insights?
        </p>
      </div>

      <div className="grid lg:grid-cols-3 gap-8 mt-20">
        {/* Activities Section */}
        <div className="lg:col-span-2 space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-semibold text-white flex items-center">
              <Clock className="w-6 h-6 mr-2 text-teal-400" />
              Your Activities
            </h2>
            <button
              onClick={() => setShowForm(!showForm)}
              className="bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-lg flex items-center space-x-2 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>Add Activity</span>
            </button>
          </div>

          {/* Improved Filter, Sort, Pagination Controls */}
          <div className="bg-slate-800 rounded-lg p-4 mb-4 border border-slate-700 flex flex-wrap items-center gap-4">
            <span className="font-semibold text-teal-400 mr-2 flex items-center">
              <TrendingUp className="w-4 h-4 mr-1" /> Filter Activities
            </span>
            <select
              value={filterActivityType}
              onChange={e => setFilterActivityType(e.target.value)}
              className="bg-slate-700 border border-teal-500 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-teal-500"
            >
              <option value="">All Types</option>
              {activityTypes.map(type => (
                <option key={type} value={type}>{type.replace(/_/g, ' ')}</option>
              ))}
            </select>
            <button
              onClick={() => setFilterActivityType('')}
              className="bg-teal-600 hover:bg-teal-700 text-white px-3 py-1 rounded-lg transition-colors"
              disabled={!filterActivityType}
            >
              Clear Filter
            </button>
            {/* Sorting and Pagination Controls */}
            <select value={sortBy} onChange={e => setSortBy(e.target.value)} className="bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-teal-500">
              <option value="startTime">Start Time</option>
              <option value="duration">Duration</option>
              {/* Add more sort options as needed */}
            </select>
            <select value={sortDirection} onChange={e => setSortDirection(e.target.value as 'asc' | 'desc')} className="bg-slate-700 border border-slate-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-teal-500">
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
            <button disabled={page === 0} onClick={() => setPage(page - 1)} className="bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg disabled:opacity-50 transition-colors">
              Previous
            </button>
            <span className="text-slate-300">
              Page {page + 1} of {totalPages}
            </span>
            <button disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)} className="bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg disabled:opacity-50 transition-colors">
              Next
            </button>
          </div>

          {/* Add Activity Form */}
          {showForm && (
            <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
              <h3 className="text-lg font-semibold text-white mb-4">Create New Activity</h3>
              <form onSubmit={createActivity} className="space-y-4">
                <div>
                  <label htmlFor="title" className="block text-sm font-medium text-slate-300 mb-2">
                    Title
                  </label>
                  <input
                    id="title"
                    type="text"
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    placeholder="Enter activity title"
                    disabled={isCreating}
                  />
                </div>
                <div>
                  <label htmlFor="description" className="block text-sm font-medium text-slate-300 mb-2">
                    Description
                  </label>
                  <textarea
                    id="description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    placeholder="Enter activity description"
                    rows={3}
                    disabled={isCreating}
                  />
                </div>
                <div>
                  <label htmlFor="status" className="block text-sm font-medium text-slate-300 mb-2">
                    Status
                  </label>
                  <select
                    id="status"
                    value={status}
                    onChange={(e) => setStatus(e.target.value as 'planned' | 'in_progress' | 'completed')}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-teal-500"
                    disabled={isCreating}
                  >
                    <option value="planned">Planned</option>
                    <option value="in_progress">In Progress</option>
                    <option value="completed">Completed</option>
                  </select>
                </div>
                <div>
                  <label htmlFor="activityType" className="block text-sm font-medium text-slate-300 mb-2">
                    Activity Type
                  </label>
                  <select
                    id="activityType"
                    required
                    value={activityType}
                    onChange={(e) => setActivityType(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-teal-500"
                    disabled={isCreating}
                  >
                    <option value="">Select activity type</option>
                    {activityTypes.map(type => (
                      <option key={type} value={type}>{type.replace(/_/g, ' ')}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label htmlFor="duration" className="block text-sm font-medium text-slate-300 mb-2">
                    Duration (minutes)
                  </label>
                  <input
                    id="duration"
                    type="number"
                    min="1"
                    value={duration}
                    onChange={(e) => setDuration(Number(e.target.value))}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    placeholder="Enter duration in minutes"
                    disabled={isCreating}
                  />
                </div>
                <div>
                  <label htmlFor="caloriesBurned" className="block text-sm font-medium text-slate-300 mb-2">
                    Calories Burned
                  </label>
                  <input
                    id="caloriesBurned"
                    type="number"
                    value={caloriesBurned}
                    onChange={(e) => setCaloriesBurned(Number(e.target.value))}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    placeholder="Enter calories burned"
                    disabled={isCreating}
                  />
                </div>
                <div>
                  <label htmlFor="startTime" className="block text-sm font-medium text-slate-300 mb-2">
                    Start Time
                  </label>
                  <input
                    id="startTime"
                    type="datetime-local"
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    disabled={isCreating}
                  />
                </div>
                <div>
                  <label htmlFor="additionalMetrics" className="block text-sm font-medium text-slate-300 mb-2">
                    Additional Metrics (JSON)
                  </label>
                  <textarea
                    id="additionalMetrics"
                    value={additionalMetrics}
                    onChange={(e) => setAdditionalMetrics(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500"
                    placeholder='{"key": "value"}'
                    rows={3}
                    disabled={isCreating}
                  />
                </div>
                <div className="flex items-center space-x-3">
                  <button
                    type="submit"
                    disabled={isCreating}
                    className="bg-teal-600 hover:bg-teal-700 disabled:bg-teal-800 text-white px-6 py-2 rounded-lg font-medium transition-colors flex items-center space-x-2"
                  >
                    {isCreating ? <LoadingSpinner size="sm" /> : <Plus className="w-4 h-4" />}
                    <span>{isCreating ? 'Creating...' : 'Create Activity'}</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowForm(false)}
                    className="text-slate-400 hover:text-white px-4 py-2 transition-colors"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Activities List */}
          <div className="space-y-4">
            {activities.length === 0 ? (
              <div className="bg-slate-800 rounded-lg p-8 text-center border border-slate-700">
                <Clock className="w-12 h-12 text-slate-500 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-slate-300 mb-2">No activities yet</h3>
                <p className="text-slate-500 mb-4">Create your first activity to get started!</p>
                <button
                  onClick={() => setShowForm(true)}
                  className="bg-teal-600 hover:bg-teal-700 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                >
                  Add Activity
                </button>
              </div>
            ) : (
              activities?.map((activity) => (
                <div
                  key={activity?.id}
                  className="bg-slate-800 rounded-lg p-6 border border-slate-700 hover:border-slate-600 transition-colors"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-grow">
                      <h3 className="text-lg font-semibold text-white mb-2">{activity?.activityType}</h3>
                      <div className="text-slate-300 mb-2 text-sm">
                        <span className="mr-4">Duration: <span className="font-semibold text-white">{activity?.duration} min</span></span>
                        <span className="mr-4">Calories: <span className="font-semibold text-white">{activity?.caloriesBurned}</span></span>
                        <span className="mr-4">Start: <span className="font-semibold text-white">{activity?.startTime ? formatDate(activity.startTime) : '-'}</span></span>
                      </div>
                      {activity?.description && (
                        <p className="text-slate-300 mb-2">{activity?.description}</p>
                      )}
                      {activity?.additionalMetrics && (
                        <div className="bg-slate-700 rounded p-3 mb-2">
                          <h4 className="text-xs font-bold text-teal-400 mb-1">Additional Metrics:</h4>
                          <ul className="text-xs text-slate-200">
                            {Object.entries(activity.additionalMetrics).map(([key, value]) => (
                              <li key={key}><span className="font-semibold text-teal-300">{key}:</span> {String(value)}</li>
                            ))}
                          </ul>
                        </div>
                      )}
                      <div className="flex items-center space-x-4">
                        <span
                          className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(activity.status || 'planned')}`}
                        >
                          {activity.status || 'planned'}
                        </span>
                        <span className="text-slate-500 text-sm">
                          {activity?.timestamp ? formatDate(activity.timestamp) : ''}
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => deleteActivity(activity?.id)}
                      className="text-slate-500 hover:text-red-400 p-2 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* AI Recommendations Sidebar */}
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-white flex items-center">
              <Brain className="w-5 h-5 mr-2 text-teal-400" />
              AI Insights
            </h2>
            {activities?.length > 0 && (
              <button
                onClick={() => loadRecommendations()}
                disabled={isLoadingRecommendations}
                className="text-teal-400 hover:text-teal-300 text-sm transition-colors disabled:opacity-50"
              >
                {isLoadingRecommendations ? 'Loading...' : 'Refresh'}
              </button>
            )}
          </div>

          {/* Recommendation Pagination Controls */}
          <div className="flex items-center justify-between mb-2">
            <button
              onClick={() => setRecommendationPage(recommendationPage - 1)}
              disabled={recommendationPage === 0 || isLoadingRecommendations}
              className="bg-slate-700 hover:bg-slate-600 text-white px-3 py-1 rounded-lg disabled:opacity-50 transition-colors"
            >
              Previous
            </button>
            <span className="text-slate-300">
              Page {recommendationPage + 1} of {recommendationTotalPages}
            </span>
            <button
              onClick={() => setRecommendationPage(recommendationPage + 1)}
              disabled={recommendationPage + 1 >= recommendationTotalPages || isLoadingRecommendations}
              className="bg-slate-700 hover:bg-slate-600 text-white px-3 py-1 rounded-lg disabled:opacity-50 transition-colors"
            >
              Next
            </button>
          </div>

          <div className="bg-slate-800 rounded-lg border border-slate-700">
            {isLoadingRecommendations ? (
              <div className="p-6 text-center">
                <LoadingSpinner size="md" />
                <p className="text-slate-400 mt-2">Analyzing your activities...</p>
              </div>
            ) : recommendations.length > 0 ? (
              <div className="divide-y divide-slate-700">
                {recommendations.map((rec, index) => (
                  <div
                    key={rec.id || index}
                    className="p-4 cursor-pointer hover:bg-slate-700 rounded transition-colors"
                    onClick={() => { setSelectedRecommendation(rec); setShowRecommendationModal(true); }}
                    title="View full recommendation"
                  >
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-bold text-teal-400 text-sm">{rec.activityType}</span>
                      <span className="text-xs text-slate-400">{rec.createdAt ? formatDate(rec.createdAt) : ''}</span>
                    </div>
                    <div className="text-slate-200 text-xs truncate max-w-xs">
                      {rec.recommendationText?.split('\n')[0] || 'View details'}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-6 text-center">
                <Brain className="w-8 h-8 text-slate-500 mx-auto mb-2" />
                <p className="text-slate-400 text-sm">
                  {activities.length === 0
                    ? 'Add some activities to get AI recommendations'
                    : 'No recommendations available yet'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Recommendation Modal */}
      {showRecommendationModal && selectedRecommendation && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60">
          <div className="bg-slate-900 rounded-lg shadow-lg max-w-lg w-full p-6 relative max-h-[90vh] overflow-y-auto">
            <button
              className="absolute top-2 right-2 text-slate-400 hover:text-white"
              onClick={() => setShowRecommendationModal(false)}
              aria-label="Close"
            >
              &times;
            </button>
            <h2 className="text-xl font-bold text-teal-400 mb-2">{selectedRecommendation.activityType}</h2>
            <div className="text-xs text-slate-400 mb-2">{selectedRecommendation.createdAt ? formatDate(selectedRecommendation.createdAt) : ''}</div>
            <p className="text-slate-200 text-sm mb-4 whitespace-pre-line">{selectedRecommendation.recommendationText}</p>
            {selectedRecommendation.improvements && selectedRecommendation.improvements.length > 0 && (
              <div className="mb-3">
                <h4 className="text-xs font-bold text-yellow-400 mb-1">Improvements:</h4>
                <ul className="list-disc ml-5 text-xs text-slate-300">
                  {selectedRecommendation.improvements.map((item, i) => (
                    <li key={i}>{item}</li>
                  ))}
                </ul>
              </div>
            )}
            {selectedRecommendation.suggestions && selectedRecommendation.suggestions.length > 0 && (
              <div className="mb-3">
                <h4 className="text-xs font-bold text-teal-300 mb-1">Suggestions:</h4>
                <ul className="list-disc ml-5 text-xs text-slate-300">
                  {selectedRecommendation.suggestions.map((item, i) => (
                    <li key={i}>{item}</li>
                  ))}
                </ul>
              </div>
            )}
            {selectedRecommendation.safety && selectedRecommendation.safety.length > 0 && (
              <div className="mb-3">
                <h4 className="text-xs font-bold text-red-400 mb-1">Safety:</h4>
                <ul className="list-disc ml-5 text-xs text-slate-300">
                  {selectedRecommendation.safety.map((item, i) => (
                    <li key={i}>{item}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;

