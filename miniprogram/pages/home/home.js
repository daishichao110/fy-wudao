const api = require('../../utils/api.js');

Page({
  data: {
    noticeList: [],
    previewTeachers: [],
    bannerList: [],
    showSchoolIntroModal: false,
    showHomeTeacherModal: false,
    selectedTeacher: null,
    showNoticeDetailModal: false,
    selectedNotice: null,
    
    // 大型演出与风采展示 Banner 专有状态
    showBannerDetailModal: false,
    selectedBanner: null,
    showPublishBannerModal: false,
    canPublishBanner: false,

    badgeOptions: ['🎪 大型演出', '🏆 风采展示'],
    badgeIndex: 0,
    
    publishBannerForm: {
      title: '',
      subtitle: '',
      badge: '🎪 大型演出',
      eventDate: '',
      location: '',
      content: '',
      imageUrl: 'http://172.20.10.4:8080/image/banner1.jpg'
    }
  },

  onLoad() {
    this.checkPermissions();
    this.loadBanners();
    this.loadNotices();
    this.loadTeachers();
  },

  onShow() {
    this.checkPermissions();
    this.loadBanners();
    this.loadNotices();
    this.loadTeachers();
  },

  onPullDownRefresh() {
    this.loadBanners();
    this.loadNotices();
    this.loadTeachers(() => {
      wx.stopPullDownRefresh();
    });
  },

  checkPermissions() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || '';
    const canPublish = (role === 'SUPER_ADMIN' || role === 'TEACHER' || role === 'COMMITTEE');
    this.setData({ canPublishBanner: canPublish });
  },

  loadBanners() {
    api.getBannerList().then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ bannerList: list });
    }).catch(err => {
      console.log('读取后端 Banner API 异常:', err);
    });
  },

  loadNotices() {
    api.getNoticeList().then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ noticeList: list });
    }).catch(err => {
      console.log('读取后端公告 API 异常:', err);
      this.setData({ noticeList: [] });
    });
  },

  loadTeachers(cb) {
    api.getTeacherList().then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ previewTeachers: list.slice(0, 2) });
      if (cb) cb();
    }).catch(err => {
      console.log('读取后端教师列表 API 异常:', err);
      this.setData({ previewTeachers: [] });
      if (cb) cb();
    });
  },

  // 🎪 点击 Banner 打开演出/风采活动详情 Modal
  handleBannerClick(e) {
    const banner = e.currentTarget.dataset.banner;
    if (banner) {
      this.setData({
        selectedBanner: banner,
        showBannerDetailModal: true
      });
    }
  },

  closeBannerDetailModal() {
    this.setData({ showBannerDetailModal: false });
  },

  // 🎨 打开管理员 & 家委发布 Modal
  openPublishBannerModal() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      'publishBannerForm.creatorName': userInfo.realName || userInfo.parentName || '管理员/家委',
      'publishBannerForm.creatorRole': userInfo.roleType || 'COMMITTEE',
      'publishBannerForm.eventDate': this.getTodayDate(),
      showPublishBannerModal: true
    });
  },

  closePublishBannerModal() {
    this.setData({ showPublishBannerModal: false });
  },

  onBadgeChange(e) {
    const idx = Number(e.detail.value);
    const badge = this.data.badgeOptions[idx] || '🎪 大型演出';
    this.setData({
      badgeIndex: idx,
      'publishBannerForm.badge': badge
    });
  },

  // 📷 上传或选定活动照片 (单张图片)
  chooseBannerImage() {
    wx.showActionSheet({
      itemList: ['📷 从相册选择 / 现场拍摄照片', '🖼️ 使用国家大剧院首演剧照', '🖼️ 使用后台幕后花絮剧照'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.chooseMedia({
            count: 1,
            mediaType: ['image'],
            sourceType: ['album', 'camera'],
            success: (chooseRes) => {
              if (chooseRes.tempFiles && chooseRes.tempFiles.length > 0) {
                const tempPath = chooseRes.tempFiles[0].tempFilePath;
                this.setData({ 'publishBannerForm.imageUrl': tempPath });
                wx.showToast({ title: '已选定封面照片', icon: 'success' });
              }
            }
          });
        } else if (res.tapIndex === 1) {
          this.setData({ 'publishBannerForm.imageUrl': 'http://172.20.10.4:8080/image/banner1.jpg' });
          wx.showToast({ title: '已应用剧照 1', icon: 'success' });
        } else if (res.tapIndex === 2) {
          this.setData({ 'publishBannerForm.imageUrl': 'http://172.20.10.4:8080/image/banner2.jpg' });
          wx.showToast({ title: '已应用剧照 2', icon: 'success' });
        }
      }
    });
  },

  // 📝 提交发布新的演出/风采展示 Banner
  submitPublishBanner() {
    const form = this.data.publishBannerForm;
    if (!form.title || !form.title.trim()) {
      wx.showToast({ title: '请输入活动名称/剧目标题', icon: 'none' });
      return;
    }
    if (!form.content || !form.content.trim()) {
      wx.showToast({ title: '请输入活动精彩回顾与报道文字', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在发布中...' });

    api.publishBanner(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 成功发布演出风采展播！', icon: 'success' });
      this.setData({ showPublishBannerModal: false });
      this.loadBanners();
    }).catch(err => {
      wx.hideLoading();
    });
  },

  openSchoolIntroModal() {
    this.setData({ showSchoolIntroModal: true });
  },

  closeSchoolIntroModal() {
    this.setData({ showSchoolIntroModal: false });
  },

  openTeacherModal(e) {
    const teacher = e.currentTarget.dataset.teacher;
    if (teacher) {
      this.setData({
        selectedTeacher: teacher,
        showHomeTeacherModal: true
      });
    }
  },

  closeHomeTeacherModal() {
    this.setData({ showHomeTeacherModal: false });
  },

  handleNoticeClick(e) {
    const notice = e.currentTarget.dataset.notice;
    if (notice) {
      this.setData({
        selectedNotice: notice,
        showNoticeDetailModal: true
      });
    }
  },

  closeNoticeDetailModal() {
    this.setData({ showNoticeDetailModal: false });
  },

  getTodayDate() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
});
