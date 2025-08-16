import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useNotification } from '../hooks/useNotification';
import { Activity } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { login, user, token } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { addNotification } = useNotification();

  // Redirect if already logged in
  React.useEffect(() => {
    if (user && token) {
      navigate('/dashboard', { replace: true });
    }
  }, [user, token, navigate]);

  const from = (location.state as any)?.from?.pathname || '/dashboard';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await login(email, password);
        console.log('Login successful');
      addNotification('Welcome back! Successfully logged in.', 'success');
      navigate(from, { replace: true });
    } catch (error) {
      addNotification(
        error instanceof Error ? error.message : 'Login failed. Please try again.',
        'error'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-teal-900 flex items-center justify-center px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="bg-slate-800 rounded-lg shadow-xl p-8">
          <div className="text-center mb-8">
            <div className="flex justify-center mb-4">
              <Activity className="w-12 h-12 text-teal-400" />
            </div>
            <h2 className="text-3xl font-bold text-white">Sign In</h2>
            <p className="text-slate-400 mt-2">Welcome back to ActivityTracker</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-slate-300 mb-2">
                Email Address
              </label>
              <input
                id="email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                placeholder="Enter your email"
                disabled={isLoading}
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-300 mb-2">
                Password
              </label>
              <input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                placeholder="Enter your password"
                disabled={isLoading}
              />
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-teal-600 hover:bg-teal-700 disabled:bg-teal-800 text-white py-3 rounded-lg font-semibold transition-colors flex items-center justify-center"
            >
              {isLoading ? <LoadingSpinner size="sm" /> : 'Sign In'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-slate-400">
              Don't have an account?{' '}
              <Link to="/register" className="text-teal-400 hover:text-teal-300 font-medium">
                Sign up
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;