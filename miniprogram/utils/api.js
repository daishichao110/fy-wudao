// 微信小程序开发与调试配置: 本地 8080 端口或局域网 IP 直连
const BASE_URL = 'https://52ddup.com/api';

const request = (url, method = 'GET', data = {}, showErrorToast = false) => {
  const token = wx.getStorageSync('token') || '';
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}${url}`,
      method: method,
      data: data,
      header: {
        'content-type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          resolve(res.data);
        } else {
          if (showErrorToast) {
            wx.showToast({ title: (res.data && res.data.message) || '请求后端失败', icon: 'none' });
          }
          reject(res.data);
        }
      },
      fail: (err) => {
        console.error('API 请求网络异常:', url, err);
        if (showErrorToast) {
          wx.showToast({ title: '无法连接后端服务器，请检查 8080 端口与局域网 IP', icon: 'none' });
        }
        reject(err);
      }
    });
  });
};

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) {
    const imgIndex = url.indexOf('/image');
    if (imgIndex !== -1) {
      url = url.substring(imgIndex);
    } else {
      return url;
    }
  }
  const host = BASE_URL.replace(/\/api$/, '');
  return url.startsWith('/') ? `${host}${url}` : `${host}/${url}`;
};

module.exports = {
  BASE_URL: BASE_URL,
  getImageUrl: getImageUrl,
  request: request,
  wxLogin: (data) => request('/auth/wx-login', 'POST', data, true),
  applyLoginPermission: (data) => request('/auth/apply-login', 'POST', data, true),

  // 100% 真实后端数据库公告与教师 API
  getNoticeList: () => request('/notice/list', 'GET'),
  createNotice: (data) => request('/notice/create', 'POST', data),

  getTeacherList: () => request('/teacher/list', 'GET'),
  createTeacher: (data) => request('/teacher/create', 'POST', data),

  getSchedules: (danceClassName) => request(`/schedule/list${danceClassName ? '?danceClassName=' + encodeURIComponent(danceClassName) : ''}`, 'GET'),
  createSchedule: (data) => request('/schedule/create', 'POST', data),
  applyLeave: (data) => request('/leave-makeup/apply-leave', 'POST', data),
  applyMakeup: (data) => request('/leave-makeup/apply-makeup', 'POST', data),
  getPendingUsers: (danceClassName) => request(`/user/pending-approvals${danceClassName ? '?danceClassName=' + encodeURIComponent(danceClassName) : ''}`, 'GET'),
  approveUser: (userId, status) => request(`/user/approve?userId=${userId}&status=${status}`, 'POST'),
  createDynamicPurchase: (data) => request('/purchase/create-dynamic', 'POST', data, true),
  getItemDemands: () => request('/item-demand/list', 'GET'),
  updateItemDemand: (data) => request('/item-demand/update', 'POST', data, true),
  submitThought: (data) => request('/thought/publish', 'POST', data, true),
  getThoughts: (type) => request(`/thought/list${type ? '?type=' + type : ''}`, 'GET'),
  likeThought: (id) => request('/thought/like', 'POST', { id: id }, true),
  getMyStudentProfile: (studentId) => request(`/student-profile/my?studentId=${studentId || 6}`, 'GET'),
  submitStudentProfile: (data) => request('/student-profile/save', 'POST', data, true),

  // 家委协同任务 API
  getVolunteerTasks: (danceClassName) => request(`/volunteer/tasks${danceClassName ? '?danceClassName=' + encodeURIComponent(danceClassName) : ''}`, 'GET'),
  createVolunteerTask: (data) => request('/volunteer/createTask', 'POST', data),
  assignVolunteerTask: (data) => request('/volunteer/assignTask', 'POST', data),
  enrollVolunteerTask: (data) => request('/volunteer/enroll', 'POST', data),

  // 7天轮值看护 API
  getDutySchedules: (danceClassName) => request(`/volunteer/duty/list${danceClassName ? '?danceClassName=' + encodeURIComponent(danceClassName) : ''}`, 'GET'),
  claimDutySchedule: (data) => request('/volunteer/duty/claim', 'POST', data),

  // 大型演出与风采展示 Banner API
  getBannerList: () => request('/banner/list', 'GET'),
  publishBanner: (data) => request('/banner/publish', 'POST', data, true),

  // 舞团工作小组 API
  getWorkGroups: () => request('/work-group/list', 'GET'),
  saveWorkGroup: (data) => request('/work-group/save', 'POST', data, true),
  deleteWorkGroup: (groupId) => request(`/work-group/delete?groupId=${groupId}`, 'POST', {}, true)
};
