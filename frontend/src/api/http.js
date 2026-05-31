import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || error.message || '요청 처리 중 오류가 발생했습니다.';
    return Promise.reject(new Error(message));
  }
);

export const unwrap = (res) => res.data?.data ?? res.data;
export default http;
