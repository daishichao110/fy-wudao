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
      imageUrl: '/image/banner1.jpg'
    },

    // 📢 系统广播公告专有状态
    showPublishNoticeModal: false,
    noticeTagOptions: ['【通知】', '【重要通知】', '【演出排练】', '【考级通知】', '【教务提示】'],
    noticeTagIndex: 0,
    publishNoticeForm: {
      title: '',
      tag: '【通知】',
      publisher: '舞蹈学校教务处',
      content: ''
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
      const mapped = list.map(item => ({
        ...item,
        imageUrl: api.getImageUrl(item.imageUrl)
      }));
      this.setData({ bannerList: mapped });
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
      const mapped = list.map(t => ({
        ...t,
        avatarUrl: api.getImageUrl(t.avatarUrl)
      }));
      this.setData({ previewTeachers: mapped.slice(0, 2) });
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

  // 📷 上传或选定活动照片 (直接从相册选取)
  chooseBannerImage() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album'],
      success: (chooseRes) => {
        if (chooseRes.tempFiles && chooseRes.tempFiles.length > 0) {
          const tempPath = chooseRes.tempFiles[0].tempFilePath;
          this.setData({ 'publishBannerForm.imageUrl': tempPath });
          wx.showToast({ title: '已从相册选取照片', icon: 'success' });
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

  openPublishNoticeModal() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const defaultPublisher = userInfo.roleType === 'SUPER_ADMIN' ? '👑 舞蹈学校教务处' : (userInfo.roleType === 'COMMITTEE' ? '🤝 家委会' : '👩‍🏫 专业老师');
    this.setData({
      showPublishNoticeModal: true,
      publishNoticeForm: {
        title: '',
        tag: this.data.noticeTagOptions[this.data.noticeTagIndex || 0],
        publisher: defaultPublisher,
        content: ''
      }
    });
  },

  onNoticeInput(e) {
    const field = e.currentTarget.dataset.field;
    const val = e.detail.value;
    if (field) {
      this.setData({
        [`publishNoticeForm.${field}`]: val
      });
    }
  },

  closePublishNoticeModal() {
    this.setData({ showPublishNoticeModal: false });
  },

  onNoticeTagChange(e) {
    const idx = Number(e.detail.value);
    const tag = this.data.noticeTagOptions[idx] || '【通知】';
    this.setData({
      noticeTagIndex: idx,
      'publishNoticeForm.tag': tag
    });
  },

  submitPublishNotice() {
    const form = this.data.publishNoticeForm;
    if (!form.title || !form.title.trim()) {
      wx.showToast({ title: '请输入公告标题', icon: 'none' });
      return;
    }
    if (!form.content || !form.content.trim()) {
      wx.showToast({ title: '请输入公告正文内容', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在发布全校广播...' });

    api.createNotice(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 全校公告发布成功！', icon: 'success' });
      this.setData({ showPublishNoticeModal: false });
      this.loadNotices();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '全校公告发布成功！', icon: 'success' });
      this.setData({ showPublishNoticeModal: false });
      this.loadNotices();
    });
  },

  getTodayDate() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
});
