import React from 'react';
import { Link } from 'react-router-dom';
import { Activity, Brain, TrendingUp, Users } from 'lucide-react';

const Landing: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-teal-900">
      {/* Header */}
      <header className="relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex justify-between items-center">
            <div className="flex items-center space-x-2">
              <Activity className="w-8 h-8 text-teal-400" />
              <h1 className="text-2xl font-bold text-white">ActivityTracker</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link
                to="/login"
                className="text-slate-300 hover:text-white px-4 py-2 rounded-md transition-colors"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="bg-teal-600 hover:bg-teal-700 text-white px-6 py-2 rounded-lg font-medium transition-colors"
              >
                Sign Up
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="relative z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="text-center">
            <h2 className="text-5xl md:text-6xl font-bold text-white mb-6">
              Track your activities.
              <br />
              <span className="text-teal-400">Get smart insights.</span>
            </h2>
            <p className="text-xl text-slate-300 mb-12 max-w-3xl mx-auto">
              Harness the power of AI to understand your activity patterns, receive personalized 
              recommendations, and optimize your daily routines for better productivity and well-being.
            </p>
            <div className="flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-6">
              <Link
                to="/register"
                className="bg-teal-600 hover:bg-teal-700 text-white px-8 py-4 rounded-lg text-lg font-semibold transition-colors shadow-lg hover:shadow-xl"
              >
                Get Started Free
              </Link>
              <Link
                to="/login"
                className="border border-slate-500 hover:border-slate-400 text-slate-300 hover:text-white px-8 py-4 rounded-lg text-lg font-semibold transition-colors"
              >
                Sign In
              </Link>
            </div>
          </div>
        </div>

        {/* Features Section */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="bg-slate-800 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Brain className="w-8 h-8 text-teal-400" />
              </div>
              <h3 className="text-xl font-semibold text-white mb-2">AI-Powered Insights</h3>
              <p className="text-slate-400">
                Get intelligent recommendations based on your activity patterns and preferences.
              </p>
            </div>
            <div className="text-center">
              <div className="bg-slate-800 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <TrendingUp className="w-8 h-8 text-teal-400" />
              </div>
              <h3 className="text-xl font-semibold text-white mb-2">Track Progress</h3>
              <p className="text-slate-400">
                Monitor your activities and see your improvement over time with detailed analytics.
              </p>
            </div>
            <div className="text-center">
              <div className="bg-slate-800 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Users className="w-8 h-8 text-teal-400" />
              </div>
              <h3 className="text-xl font-semibold text-white mb-2">Personalized Experience</h3>
              <p className="text-slate-400">
                Tailored recommendations and insights that adapt to your unique lifestyle.
              </p>
            </div>
          </div>
        </div>
      </main>

      {/* Background decoration */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-32 w-80 h-80 bg-teal-400 rounded-full opacity-5 blur-3xl"></div>
        <div className="absolute -bottom-40 -left-32 w-80 h-80 bg-teal-400 rounded-full opacity-5 blur-3xl"></div>
      </div>
    </div>
  );
};

export default Landing;