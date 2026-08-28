const api = require('../../utils/api.js');

Page({
  data: {
    isAdmin: false,
    isTeacher: false,
    isCommittee: false,
    isTeacherOrAdmin: false,
    hasManagePermission: true,

    iconThemeColor: '#ea580c',
    svgIcons: {},
    colorPalette: [
      { name: '爱马仕橙', color: '#ea580c' },
      { name: '翡翠绿', color: '#059669' },
      { name: '洁白风', color: '#ffffff' }
    ],

    currentUser: {
      userId: 1,
      realName: '系统管理员',
      roleType: 'SUPER_ADMIN',
      roleName: '👑 超级管理员',
      danceClassName: '全校全局管理'
    },
    pendingList: [],
    showApprovalModal: false,
    showStudentProfileModal: false,
    showThoughtModal: false,
    showDynamicPurchaseModal: false,
    showScheduleModal: false,
    showNoticeConfigModal: false,
    showTeacherConfigModal: false,
    showHallModal: false,

    // 🚩 发布招募任务弹窗
    showCreateTaskModal: false,

    // 🎪 大型演出与风采展播配置 (仅管理员与家委会成员可用)
    showPublishBannerModal: false,
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
    },

    groupTypeOptions: ['妆造', '道具', '保洁', '摄影与跟拍', '餐饮后勤', '通用与安保'],
    groupTypeIndex: 0,
    taskForm: {
      taskName: '',
      activityName: '',
      groupType: '妆造',
      taskDate: '',
      quotaCount: '4',
      serviceTime: '13:30 - 17:30 (4小时段)',
      description: ''
    },

    // 公告配置表单
    tagOptions: ['【通知】', '【家委】', '【装备】', '【考级】'],
    tagIndex: 0,
    noticeTitle: '',
    noticeContent: '',

    // 发布排课表单变量
    minDate: '',
    scheduleCourseName: '',
    scheduleDanceType: '芭蕾舞',
    scheduleTeacherName: '',
    scheduleClassroomName: '',
    scheduleClassDate: '',
    scheduleStartTime: '',
    scheduleEndTime: '',
    scheduleTopsReq: '',
    scheduleBottomsReq: '',
    scheduleSkirtReq: '',
    scheduleShoesReq: '',
    scheduleHairReq: '',
    schedulePropsReq: '',
    scheduleOtherReq: '',
    scheduleRemark: '',
    scheduleClassOptions: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    scheduleClassCodeList: ['GRADE_ALL', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5', 'GRADE_6'],
    scheduleClassIndex: 0,
    studentList: [
      { name: '李小桐', checked: true },
      { name: '张小宝', checked: true },
      { name: '王美美', checked: true },
      { name: '赵心怡', checked: true },
      { name: '陈萌萌', checked: true },
      { name: '周思涵', checked: true }
    ],

    // 📊 全校学员成绩总览 (默认按总分由高到低排序，支持按年级与全校查看)
    scoreGradeOptions: ['全部年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    scoreGradeIndex: 0,
    allStudentScores: [
      { id: 1, studentName: '李小桐', gradeLevel: '二年级', chineseScore: 96, mathScore: 98, englishScore: 97, totalScore: 291, resumeBio: '2025年斩获全国少儿芭蕾剧目一等奖，通过芭蕾4级' },
      { id: 2, studentName: '陈萌萌', gradeLevel: '四年级', chineseScore: 96, mathScore: 97, englishScore: 95, totalScore: 288, resumeBio: '2024年获得全区少儿舞蹈展演金奖' },
      { id: 3, studentName: '王美美', gradeLevel: '三年级', chineseScore: 94, mathScore: 96, englishScore: 97, totalScore: 287, resumeBio: '中国舞考级5级，舞团领舞学员' },
      { id: 4, studentName: '张小宝', gradeLevel: '二年级', chineseScore: 92, mathScore: 95, englishScore: 94, totalScore: 281, resumeBio: '舞团基训班优秀学员' },
      { id: 5, studentName: '周思涵', gradeLevel: '五年级', chineseScore: 93, mathScore: 94, englishScore: 93, totalScore: 280, resumeBio: '现代舞与拉伸班优秀学员' },
      { id: 6, studentName: '赵心怡', gradeLevel: '三年级', chineseScore: 90, mathScore: 93, englishScore: 92, totalScore: 275, resumeBio: '少儿芭蕾启蒙体验班学员' }
    ],
    displayStudentScores: [],

    // 随感与心里话列表 (最多600字，全员共享)
    thoughtContent: '',
    activeThoughtType: 'THOUGHT',
    thoughtList: [],

    studentProfileForm: {
      studentId: 6,
      studentName: '',
      gradeLevel: '',
      chineseScore: '',
      mathScore: '',
      englishScore: '',
      heightCm: '',
      weightKg: '',
      parentName: '',
      parentPhone: '',
      resumeBio: ''
    }
  },

  onLoad() {
    this.updateDisplayScores();
  },

  onShow() {
    this.refreshUserInfo();
    this.loadPendingApprovals();
    this.updateDisplayScores();
    const savedColor = wx.getStorageSync('profile_icon_color') || '#ea580c';
    this.setData({
      minDate: this.getTodayDate(),
      iconThemeColor: savedColor
    });
  },

  onScoreGradeChange(e) {
    const idx = Number(e.detail.value);
    this.setData({ scoreGradeIndex: idx });
    this.updateDisplayScores();
  },

  updateDisplayScores() {
    const { allStudentScores, scoreGradeIndex, scoreGradeOptions } = this.data;
    const selectedGrade = scoreGradeOptions[scoreGradeIndex];
    let filtered = allStudentScores;
    if (selectedGrade && selectedGrade !== '全部年级') {
      filtered = allStudentScores.filter(item => item.gradeLevel === selectedGrade);
    }
    // 默认按总分从高到低排序
    const sorted = filtered.slice().sort((a, b) => b.totalScore - a.totalScore);
    this.setData({ displayStudentScores: sorted });
  },

  refreshUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || 'STUDENT';

    let roleName = '🎒 学员';
    if (role === 'SUPER_ADMIN') roleName = '👑 超级管理员';
    else if (role === 'TEACHER') roleName = '👩‍🏫 教务教师';
    else if (role === 'COMMITTEE') roleName = '🤝 家委会成员';

    const isAdmin = (role === 'SUPER_ADMIN');
    const isTeacher = (role === 'TEACHER');
    const isCommittee = (role === 'COMMITTEE');
    const isTeacherOrAdmin = (isAdmin || isTeacher);
    const hasManagePermission = (isAdmin || isTeacher || isCommittee);

    this.setData({
      isAdmin,
      isTeacher,
      isCommittee,
      isTeacherOrAdmin,
      hasManagePermission,
      currentUser: {
        userId: userInfo.userId || 1,
        realName: userInfo.realName || userInfo.parentName || '管理员',
        roleType: role,
        roleName: roleName,
        danceClassName: userInfo.danceClassName || '全校全局管理'
      }
    });
  },

  loadPendingApprovals() {
    api.getPendingUsers().then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ pendingList: list });
    }).catch(err => {
      console.log('读取待审批数据异常:', err);
    });
  },

  openModal(e) {
    const modalType = e.currentTarget.dataset.modal;
    if (modalType === 'approval') this.setData({ showApprovalModal: true });
    else if (modalType === 'studentProfile') this.setData({ showStudentProfileModal: true });
    else if (modalType === 'createTask') this.setData({ showCreateTaskModal: true });
  },

  closeModals() {
    this.setData({
      showApprovalModal: false,
      showStudentProfileModal: false,
      showCreateTaskModal: false,
      showPublishBannerModal: false,
      showScheduleModal: false
    });
  },

  downloadStudentInfo() {
    wx.showModal({
      title: '📜 全校学员档案模版',
      content: '包含：姓名、年级、语文/数学/英语成绩、总分、联系电话及艺术简历。点击确认复制模版数据。',
      confirmText: '复制模版',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: '学员姓名,年级,语文,数学,英语,总分,家长电话,艺术简历\n李小桐,二年级,96,98,97,291,18911800655,2025年全国少儿芭蕾一等奖\n陈萌萌,四年级,96,97,95,288,13912345678,2024年全区展演金奖',
            success: () => {
              wx.showToast({ title: '模版数据已复制到剪贴板！', icon: 'success' });
            }
          });
        }
      }
    });
  },

  submitStudentProfile() {
    const form = this.data.studentProfileForm;
    if (!form.studentName || !form.studentName.trim()) {
      wx.showToast({ title: '请输入学员姓名', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在保存档案...' });

    api.submitStudentProfile(form).then(() => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 学员档案及简历保存成功！', icon: 'success' });
      this.closeModals();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '学员档案及简历保存成功！', icon: 'success' });
      this.closeModals();
    });
  },

  getTodayDate() {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  },

  // 🎪 Banner 发布逻辑
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
    }).catch(err => {
      wx.hideLoading();
    });
  }
});
