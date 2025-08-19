export interface User {
  id: string;

  email: string;
  name?: string;
  firstName?: string;
  lastName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Activity {
  id: string;
  title: string;
  description: string;
  timestamp: string;
  status?: 'completed' | 'in_progress' | 'planned';
  userId?: string;
  activityType: string;
  duration: number;
  caloriesBurned: number;
  startTime: string;
  additionalMetrics?: Record<string, any>;

}

export interface Recommendation {
  id: string;
  activityId: string;
  userId: string;
  activityType: string;
  recommendationText: string;
  improvements: string[];
  suggestions: string[];
  safety: string[];
  createdAt: string;
  updatedAt?: string | null;
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: ( email: string, password: string, name: string, firstName: string, lastName: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

export interface ApiError {
  message: string;
  status: number;
}