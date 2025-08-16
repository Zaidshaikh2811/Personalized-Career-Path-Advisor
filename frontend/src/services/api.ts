import axios, { AxiosResponse } from 'axios';
import { User, Activity, Recommendation, ApiError } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'; // Gateway URL

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: async (email: string, password: string): Promise<{ token: string; user: User }> => {
    const response: AxiosResponse<{ token: string; user: User }> = await api.post('/api/v1/auth/login', {
      email,
      password,
    });
      console.log('Login response:', response.data);
    return response.data;
  },

  register: async (username: string, email: string, password: string): Promise<{ token: string; user: User }> => {
    const response: AxiosResponse<{ token: string; user: User }> = await api.post('/api/v1/auth/register', {
      username,
      email,
      password,
    });
    return response.data;
  },
};

export const userAPI = {
  getProfile: async (id: string): Promise<User> => {
    const response: AxiosResponse<User> = await api.get(`/api/v1/users/${id}`);
    return response.data;
  },

  updateProfile: async (id: string, userData: Partial<User>): Promise<User> => {
    const response: AxiosResponse<User> = await api.put(`/api/v1/users/${id}`, userData);
    return response.data;
  },
};

export const activityAPI = {
  getActivities: async (): Promise<Activity[]> => {
    const response: AxiosResponse<Activity[]> = await api.get('/api/v1/activities');
    return response.data;
  },

  createActivity: async (activityData: Omit<Activity, 'id' | 'timestamp'>): Promise<Activity> => {
    const response: AxiosResponse<Activity> = await api.post('/api/v1/activities/create', activityData);
    return response.data;
  },

  deleteActivity: async (id: string): Promise<void> => {
    await api.delete(`/api/v1/activities/${id}`);
  },
};

export const aiAPI = {
  getRecommendations: async (activityId: string): Promise<Recommendation[]> => {
    const response: AxiosResponse<Recommendation[]> = await api.post('/api/v1/ai/analyze', {
      activityId,
    });
    return response.data;
  },

  getAllRecommendations: async (): Promise<Recommendation[]> => {
    const response: AxiosResponse<Recommendation[]> = await api.get('/api/v1/recommendations');
    return response.data;
  },
};

export const handleApiError = (error: any): ApiError => {
  if (error.response) {
    return {
      message: error.response.data?.message || 'An error occurred',
      status: error.response.status,
    };
  } else if (error.request) {
    return {
      message: 'Network error. Please check your connection.',
      status: 0,
    };
  } else {
    return {
      message: 'An unexpected error occurred',
      status: 500,
    };
  }
};

export default api;