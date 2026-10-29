// Backend server ka Base API URL
const BASE_URL = 'https://interviewai-s40n.onrender.com/api';/**
 * Universal Request Helper:
 * Automatically passes JWT token in headers and handles unauthorized redirects
 */
const request = async (endpoint, options = {}) => {
    const token = localStorage.getItem('token');

    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
    };

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, {
            ...options,
            headers,
        });

        if (response.status === 401 || response.status === 403) {
            // Agar user admin nahi hai to sirf error throw karega, reload nahi
            if (endpoint.startsWith('/admin')) {
                throw new Error('Access denied: Admin privileges required.');
            }
            localStorage.removeItem('token');
            window.location.reload();
            throw new Error('Session expired. Please log in again.');
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const errorMsg = data?.message || data?.error || 'Server error occurred';
            throw new Error(errorMsg);
        }

        return data;
    } catch (error) {
        throw error;
    }
};

export const api = {
    // 1. AUTHENTICATION MODULE
    login: (credentials) =>
        request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        }),

    register: (payload) =>
        request('/users/register', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),

    loginWithGoogle: (payload) =>
        request('/auth/google', {
            method: 'POST',
            body: JSON.stringify(payload),
        }),

    // 2. DASHBOARD & PROFILE
    getDashboard: () => request('/dashboard'),
    getProfile: () => request('/users/me'),

    // 3. INTERVIEWS
    getMyInterviews: () => request('/interviews/my'),
    createInterview: (payload) =>
        request('/interviews', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),

    // 4. QUESTIONS & AI EVALUATION
    getQuestions: (interviewId) =>
        request(`/interviews/${interviewId}/questions`),

    generateAiQuestions: (interviewId, count = 5) =>
        request(`/interviews/${interviewId}/questions/generate`, {
            method: 'POST',
            body: JSON.stringify({ questionCount: count }),
        }),

    submitAnswer: (interviewId, questionId, answerText) =>
        request(`/interviews/${interviewId}/questions/${questionId}/answer`, {
            method: 'POST',
            body: JSON.stringify({ answerText }),
        }),

    getReport: (interviewId) =>
        request(`/interviews/${interviewId}/report`),

    // 5. RESUME & ATS CHECKER
    analyzeResume: (payload) =>
        request('/resume/analyze', {
            method: 'POST',
            body: JSON.stringify(payload)
        }),

    uploadResumeFile: async (file, jobDescription = '') => {
        const token = localStorage.getItem('token');
        const formData = new FormData();
        formData.append('file', file);
        if (jobDescription) {
            formData.append('jobDescription', jobDescription);
        }

        const response = await fetch(`${BASE_URL}/resume/upload`, {
            method: 'POST',
            headers: {
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
            },
            body: formData,
        });

        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem('token');
            window.location.reload();
            throw new Error('Session expired. Please log in again.');
        }

        const data = await response.json().catch(() => null);
        if (!response.ok) {
            throw new Error(data?.message || data?.error || 'Resume analysis failed.');
        }
        return data;
    },

    // 6. ADMIN MODULE APIS
    getAdminStats: () => request('/admin/stats'),
    getAdminUsers: () => request('/admin/users'),
    updateUserRole: (userId, role) =>
        request(`/admin/users/${userId}/role`, {
            method: 'PUT',
            body: JSON.stringify({ role }),
        }),
    deleteUser: (userId) =>
        request(`/admin/users/${userId}`, {
            method: 'DELETE',
        }),
    getAdminInterviews: () => request('/admin/interviews'),
};