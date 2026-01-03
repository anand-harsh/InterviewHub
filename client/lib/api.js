import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

class ApiService {
  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              const response = await this.client.post('/auth/refresh-token', { refreshToken });

              if (response.data.data) {
                localStorage.setItem('accessToken', response.data.data.accessToken);
                localStorage.setItem('refreshToken', response.data.data.refreshToken);
                originalRequest.headers.Authorization = `Bearer ${response.data.data.accessToken}`;
                return this.client(originalRequest);
              }
            }
          } catch (refreshError) {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async register(data) {
    const response = await this.client.post('/auth/register', data);
    return response.data;
  }

  async login(data) {
    const response = await this.client.post('/auth/login', data);
    
    if (response.data.data) {
      localStorage.setItem('accessToken', response.data.data.accessToken);
      localStorage.setItem('refreshToken', response.data.data.refreshToken);
    }
    
    return response.data;
  }

  async logout() {
    try {
      await this.client.post('/auth/logout');
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }

  async verifyEmail(token) {
    const response = await this.client.post('/auth/verify-email', { token });
    return response.data;
  }

  async resendVerification(email) {
    const response = await this.client.post('/auth/resend-verification', { email });
    return response.data;
  }

  async forgotPassword(email) {
    const response = await this.client.post('/auth/forgot-password', { email });
    return response.data;
  }

  async resetPassword(data) {
    const response = await this.client.post('/auth/reset-password', data);
    return response.data;
  }

  async changePassword(data) {
    const response = await this.client.post('/auth/change-password', data);
    return response.data;
  }

  // Profile endpoints
  async getProfile() {
    const response = await this.client.get('/profile');
    return response.data;
  }

  async createProfile(data) {
    const response = await this.client.post('/profile', data);
    return response.data;
  }

  async updateProfile(data) {
    const response = await this.client.put('/profile', data);
    return response.data;
  }

  async updateAvailability(availableForInterview) {
    const response = await this.client.patch('/profile/availability', { availableForInterview });
    return response.data;
  }

  async deactivateAccount(reason) {
    const response = await this.client.patch('/profile/deactivate', { reason });
    return response.data;
  }

  async reactivateAccount() {
    const response = await this.client.patch('/profile/reactivate');
    return response.data;
  }

  async getCompletionScore() {
    const response = await this.client.get('/profile/completion-score');
    return response.data;
  }

  // Skills endpoints
  async getAllSkills(params) {
    const response = await this.client.get('/skills', { params });
    return response.data;
  }

  async getSkillById(id) {
    const response = await this.client.get(`/skills/${id}`);
    return response.data;
  }

  async getSkillCategories() {
    const response = await this.client.get('/skills/categories');
    return response.data;
  }

  async addSkillsToProfile(data) {
    const response = await this.client.post('/profile/skills', data);
    return response.data;
  }

  async removeSkillFromProfile(skillId) {
    const response = await this.client.delete(`/profile/skills/${skillId}`);
    return response.data;
  }

  async getProfileSkills() {
    const response = await this.client.get('/profile/skills');
    return response.data;
  }
}

export const api = new ApiService();
export default api;