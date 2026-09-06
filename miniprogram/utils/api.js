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

const uploadImage = (filePath, dir = 'images/') => {
  const token = wx.getStorageSync('token') || '';
  const targetUrl = `${BASE_URL}/upload/image`;
  console.log('[DEBUG OSS UPLOAD] 1. 准备发起上传图片至阿里云 OSS:', {
    targetUrl: targetUrl,
    filePath: filePath,
    dir: dir
  });
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: targetUrl,
      filePath: filePath,
      name: 'file',
      formData: { dir: dir },
      header: {
        'Authorization': `Bearer ${token}`
      },
      success: (res) => {
        console.log('[DEBUG OSS UPLOAD] 2. 收到上传接口原始 HTTP 状态码:', res.statusCode, '原始 Body:', res.data);
        try {
          const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
          if (res.statusCode === 200 && data && data.code === 200) {
            console.log('[DEBUG OSS UPLOAD] 3. 阿里云 OSS 图片上传成功，解析数据:', data.data);
            resolve(data.data); // 返回 { relativePath, fullUrl }
          } else {
            console.error('[DEBUG OSS UPLOAD] ❌ 上传接口业务报错:', data);
            wx.showToast({ title: (data && data.message) || '上传阿里云服务器失败', icon: 'none' });
            reject(data);
          }
        } catch (e) {
          console.error('[DEBUG OSS UPLOAD] ❌ JSON 解析失败:', e);
          wx.showToast({ title: '解析上传结果失败', icon: 'none' });
          reject(e);
        }
      },
      fail: (err) => {
        console.error('[DEBUG OSS UPLOAD] ❌ 网络层或微信 uploadFile 失败:', err);
        wx.showToast({ title: '网络开小差，图片上传失败', icon: 'none' });
        reject(err);
      }
    });
  });
};

const getImageUrl = (url) => {
 if (!url) return '';
 if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('wxfile://')) {
 return url;
 }
 const host = BASE_URL.replace(/\/api$/, '');
 return url.startsWith('/') ? `${host}${url}` : `${host}/${url}`;
};

module.exports = {
 BASE_URL: BASE_URL,
 getImageUrl: getImageUrl,
 uploadImage: uploadImage,
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
 getItemDemands: (danceClassName) => request(`/item-demand/list${danceClassName ? '?danceClassName=' + encodeURIComponent(danceClassName) : ''}`, 'GET'),
 createItemDemand: (data) => request('/item-demand/add', 'POST', data, true),
 updateItemDemand: (data) => request('/item-demand/update', 'POST', data, true),
 enrollItemDemand: (data) => request('/item-demand/enroll', 'POST', data, true),
 exportItemDemands: () => request('/item-demand/export', 'GET'),
 submitThought: (data) => request('/thought/publish', 'POST', data, true),
 getThoughts: (type) => request(`/thought/list${type ? '?type=' + type : ''}`, 'GET'),
 likeThought: (id) => request('/thought/like', 'POST', { id: id }, true),
 getStudentProfiles: (gradeLevel) => request(`/student-profile/scores${gradeLevel ? '?gradeLevel=' + encodeURIComponent(gradeLevel) : ''}`, 'GET'),
 getMyStudentProfile: (studentId) => request(`/student-profile/my${studentId ? '?studentId=' + studentId : ''}`, 'GET'),
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
 deleteWorkGroup: (groupId) => request(`/work-group/delete?groupId=${groupId}`, 'POST', {}, true),

 // 教师师资配置 API
 getTeacherList: () => request('/teacher/list', 'GET'),
 saveTeacher: (data) => request('/teacher/create', 'POST', data, true)
};
