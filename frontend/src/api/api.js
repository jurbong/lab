import http, { unwrap } from './http';

const cleanParams = (params = {}) => {
  const next = {};
  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) next[key] = value;
  });
  return next;
};

export const authApi = {
  signup: async (payload) => unwrap(await http.post('/api/auth/signup', payload)),
  login: async (payload) => unwrap(await http.post('/api/auth/login', payload)),
};

export const optionApi = {
  roles: async () => unwrap(await http.get('/api/options/roles')),
  statuses: async () => unwrap(await http.get('/api/options/statuses')),
  adminDepartments: async () => unwrap(await http.get('/api/options/admin-departments')),
  units: async () => unwrap(await http.get('/api/options/units')),
  riskLevels: async () => unwrap(await http.get('/api/options/risk-levels')),
};

export const departmentApi = {
  list: async (keyword = '') => unwrap(await http.get('/api/departments', { params: cleanParams({ keyword }) })),
};

export const userApi = {
  list: async (params) => unwrap(await http.get('/api/users', { params: cleanParams(params) })),
  options: async (params) => unwrap(await http.get('/api/users/options', { params: cleanParams(params) })),
  pending: async () => unwrap(await http.get('/api/users/pending')),
  detail: async (id) => unwrap(await http.get(`/api/users/${id}`)),
  approve: async (id, payload) => unwrap(await http.patch(`/api/users/${id}/approve`, payload)),
  reject: async (id) => unwrap(await http.patch(`/api/users/${id}/reject`)),
};

export const laboratoryApi = {
  list: async (params) => unwrap(await http.get('/api/laboratories', { params: cleanParams(params) })),
  my: async () => unwrap(await http.get('/api/laboratories/my')),
  options: async (params) => unwrap(await http.get('/api/laboratories/options', { params: cleanParams(params) })),
  detail: async (id) => unwrap(await http.get(`/api/laboratories/${id}`)),
  create: async (formData) => unwrap(await http.post('/api/laboratories', formData)),
};

export const chemicalApi = {
  list: async (params) => unwrap(await http.get('/api/chemicals', { params: cleanParams(params) })),
  detail: async (id) => unwrap(await http.get(`/api/chemicals/${id}`)),
  create: async (payload) => unwrap(await http.post('/api/chemicals', payload)),
};

export const wasteApi = {
  list: async (params) => unwrap(await http.get('/api/wastes', { params: cleanParams(params) })),
  detail: async (id) => unwrap(await http.get(`/api/wastes/${id}`)),
  create: async (payload) => unwrap(await http.post('/api/wastes', payload)),
};

export const inspectionApi = {
  list: async (params) => unwrap(await http.get('/api/inspection-forms', { params: cleanParams(params) })),
  detail: async (id) => unwrap(await http.get(`/api/inspection-forms/${id}`)),
  create: async (formData) => unwrap(await http.post('/api/inspection-forms', formData)),
};

export const educationApi = {
  list: async (params) => unwrap(await http.get('/api/education-videos', { params: cleanParams(params) })),
  detail: async (id) => unwrap(await http.get(`/api/education-videos/${id}`)),
  create: async (formData) => unwrap(await http.post('/api/education-videos', formData)),
};