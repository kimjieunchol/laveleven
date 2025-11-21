// src/services/api.js
import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api";

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: JWT 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 에러 처리
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      window.location.href = "/";
    }
    return Promise.reject(error);
  }
);

// ============================================
// Auth API
// ============================================
export const authAPI = {
  login: (username, password) =>
    apiClient.post("/auth/login", { username, password }),

  checkDuplicate: (username) =>
    apiClient.get("/auth/check-duplicate", { params: { username } }),

  logout: () => apiClient.post("/auth/logout"),

  refresh: (refreshToken) => apiClient.post("/auth/refresh", { refreshToken }),
};

// ============================================
// User API
// ============================================
export const userAPI = {
  getAllUsers: () => apiClient.get("/users"),

  getUserById: (id) => apiClient.get(`/users/${id}`),

  createUser: (userData) => apiClient.post("/users", userData),

  updateUser: (id, userData) => apiClient.put(`/users/${id}`, userData),

  deleteUser: (id) => apiClient.delete(`/users/${id}`),
};

// ============================================
// Item API
// ============================================
export const itemAPI = {
  getAllItems: () => apiClient.get("/items"),

  getItemById: (id) => apiClient.get(`/items/${id}`),

  searchItems: (keyword) =>
    apiClient.get("/items/search", { params: { keyword } }),

  createItem: (itemData) => apiClient.post("/items", itemData),

  updateItem: (id, itemData) => apiClient.put(`/items/${id}`, itemData),

  deleteItem: (id) => apiClient.delete(`/items/${id}`),
};

// ============================================
// History API
// ============================================
export const historyAPI = {
  getAllHistories: () => apiClient.get("/histories"),

  getHistoriesByItem: (itemId) => apiClient.get(`/histories/item/${itemId}`),

  getHistoriesByItemAndStep: (itemId, stepName) =>
    apiClient.get(`/histories/item/${itemId}/step/${stepName}`),

  createHistory: (itemId, historyData) =>
    apiClient.post(`/histories/item/${itemId}`, historyData),

  getHistoryById: (id) => apiClient.get(`/histories/${id}`),
};

// ============================================
// Pipeline API
// ============================================
export const pipelineAPI = {
  processOcr: (file) => {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient.post("/pipeline/ocr", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  processStructure: (data) => apiClient.post("/pipeline/structure", data),

  processTranslate: (data) => apiClient.post("/pipeline/translate", data),

  processHtml: (data) => apiClient.post("/pipeline/html", data),

  processFullPipeline: (file, targetCountry, generateHtml) => {
    const formData = new FormData();
    formData.append("file", file);
    if (targetCountry) formData.append("targetCountry", targetCountry);
    formData.append("generateHtml", generateHtml);
    return apiClient.post("/pipeline/full", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  saveScan: (itemId, imageUrl, ocrResult) =>
    apiClient.post("/pipeline/save/scan", ocrResult, {
      params: { itemId, imageUrl },
    }),

  saveSchema: (itemId, data) =>
    apiClient.post("/pipeline/save/schema", data, { params: { itemId } }),

  saveTranslate: (itemId, data) =>
    apiClient.post("/pipeline/save/translate", data, { params: { itemId } }),

  saveSketch: (itemId, html) =>
    apiClient.post("/pipeline/save/sketch", { html }, { params: { itemId } }),

  getScan: (itemId) => apiClient.get(`/pipeline/get/scan/${itemId}`),

  getSchema: (itemId) => apiClient.get(`/pipeline/get/schema/${itemId}`),

  getTranslate: (itemId) => apiClient.get(`/pipeline/get/translate/${itemId}`),

  getSketch: (itemId) => apiClient.get(`/pipeline/get/sketch/${itemId}`),
};

// ============================================
// Stats API
// ============================================
export const statsAPI = {
  getTeamStats: () => apiClient.get("/stats/teams"),

  getTotalStats: () => apiClient.get("/stats/total"),
};

export default apiClient;
