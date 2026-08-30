import axios from 'axios'

const API_BASE_URL = 'https://52ddup.com/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  config => {
    const currentUserId = localStorage.getItem('currentUserId') || '6'
    config.headers['X-User-Id'] = currentUserId
    return config
  },
  error => Promise.reject(error)
)

api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Request Error:', error)
    return Promise.reject(error)
  }
)

export default {
  // Auth APIs
  wxLogin(data) { return api.post('/auth/wx-login', data) },

  // User & Approval APIs
  getUsers() { return api.get('/user/list') },
  getUserInfo(id) { return api.get(`/user/info/${id}`) },
  getPendingApprovals(role) { return api.get(`/user/pending-approvals?approverRole=${role || 'SUPER_ADMIN'}`) },
  approveUser(userId, status) { return api.post(`/user/approve?userId=${userId}&status=${status}`) },
  registerUser(data) { return api.post('/user/register', data) },

  // Schedule & Dress Code APIs
  getSchedules() { return api.get('/schedule/list') },
  createSchedule(data) { return api.post('/schedule/create', data) },
  applyLeave(data) { return api.post('/schedule/leave', data) },
  applyMakeup(data) { return api.post('/schedule/makeup', data) },

  // Body Metrics APIs
  getStudentMetrics(studentId) { return api.get(`/metric/student/${studentId}`) },
  saveMetric(data) { return api.post('/metric/save', data) },

  // Volunteer Task APIs
  getVolunteerTasks() { return api.get('/volunteer/tasks') },
  enrollVolunteer(data) { return api.post('/volunteer/enroll', data) },

  // Mentorship APIs
  getMentorships() { return api.get('/mentorship/list') },
  checkinMentorship(data) { return api.post('/mentorship/checkin', data) },

  // Q&A Messages APIs
  getMyMessages(userId) { return api.get(`/qa/my-messages?userId=${userId}`) },
  askQuestion(data) { return api.post('/qa/ask', data) },
  replyQuestion(data) { return api.post('/qa/reply', data) },
  featureQuestion(data) { return api.post('/qa/feature', data) },
  getFeaturedQA() { return api.get('/qa/featured-list') },

  // Purchase APIs
  getPurchases() { return api.get('/purchase/list') },

  // Student Profile Maintenance APIs
  getStudentProfile(studentId) { return api.get(`/student-profile/my?studentId=${studentId || 6}`) },
  saveStudentProfile(data) { return api.post('/student-profile/save', data) },

  // 新增 4 大定制 API
  exportStudents() { return api.get('/export/students') },
  getItemDemands() { return api.get('/item-demand/list') },
  updateItemDemand(data) { return api.post('/item-demand/update', data) },
  exportItemDemands() { return api.get('/item-demand/export') },
  publishDynamicPurchase(data) { return api.post('/purchase/create-dynamic', data) },
  getThoughts(type) { return api.get(`/thought/list?type=${type || ''}`) },
  publishThought(data) { return api.post('/thought/publish', data) }
}
