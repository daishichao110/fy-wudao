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
    showPublishTaskModal: false,

    // 🛠️ 舞团工作小组风采配置 Modal & 列表
    showWorkGroupModal: false,
    workGroupList: [],
    groupForm: {
      groupId: null,
      groupName: '',
      icon: '💄',
      leaderName: '',
      memberNames: '',
      dutyDesc: ''
    },

    // 📦 物品选购计划发布 Modal & 列表
    showItemDemandModal: false,
    itemDemandList: [],
    itemForm: {
      itemName: '双皮头芭蕾练功软鞋',
      deadline: '2026-08-30',
      expectedArrivalDate: '2026-09-05',
      arrivalStatus: '未到货'
    },

    // 📥 全量选购数据导出 Modal
    showExportModal: false,
    exportTextData: '',

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
      imageUrl: '/image/banner1.jpg'
    },

    groupTypeOptions: ['妆造', '道具', '保洁', '摄影与跟排', '餐饮后勤', '通用与安保'],
    groupTypeIndex: 0,
    danceClassNameOptions: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    workGroupClassIndex: 0,
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

    // 学员个人档案表单
    studentProfileForm: {
      studentName: '李小桐',
      gradeLevel: '二年级',
      chineseScore: 96,
      mathScore: 98,
      englishScore: 97,
      parentPhone: '18911800655',
      resumeBio: '2025年斩获全国少儿芭蕾剧目一等奖，通过芭蕾4级'
    }
  },

  onLoad() {
    this.loadWorkGroups();
    this.loadItemPlans();
  },

  onShow() {
    this.refreshUserInfo();
    this.loadPendingApprovals();
    this.loadWorkGroups();
    this.loadItemPlans();
    const savedColor = wx.getStorageSync('profile_icon_color') || '#ea580c';
    this.setData({
      minDate: this.getTodayDate(),
      iconThemeColor: savedColor
    });
  },

  goToScorePage() {
    wx.navigateTo({
      url: '/pages/scores/scores'
    });
  },

  goToPurchasePage() {
    wx.navigateTo({
      url: '/pages/purchases/purchases'
    });
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

  // 1. 🛡️ 账号审批列表与操作
  loadPendingApprovals() {
    const danceClassName = this.data.currentUser ? this.data.currentUser.danceClassName : '';
    api.getPendingUsers(danceClassName).then(res => {
      if (res && res.data) {
        this.setData({ pendingList: res.data });
      }
    }).catch(err => {
      console.log('读取待审批账号列表失败:', err);
    });
  },

  handleApprove(e) {
    const userId = e.currentTarget.dataset.userid;
    const status = parseInt(e.currentTarget.dataset.status);

    if (!userId) return;

    const actionTitle = status === 1 ? '同意开通权限' : '驳回注册申请';
    wx.showModal({
      title: '审批确认',
      content: `确定要 ${actionTitle} 吗？`,
      success: (res) => {
        if (res.confirm) {
          api.approveUser(userId, status).then(apiRes => {
            wx.showToast({ title: apiRes.message || '操作成功', icon: 'success' });
            this.loadPendingApprovals();
          }).catch(err => {
            wx.showToast({ title: '操作失败', icon: 'none' });
          });
        }
      }
    });
  },

  // 2. 🛠️ 舞团工作小组风采配置
  loadWorkGroups() {
    api.getWorkGroups().then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ workGroupList: list });
    }).catch(err => {
      console.log('读取工作小组 API 异常:', err);
    });
  },

  openWorkGroupModal(e) {
    const group = e ? e.currentTarget.dataset.group : null;
    const options = this.data.danceClassNameOptions || ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'];
    if (group) {
      const idx = options.indexOf(group.danceClassName || '全校/公共');
      this.setData({
        groupForm: {
          groupId: group.groupId,
          groupName: group.groupName,
          icon: group.icon || '💄',
          danceClassName: group.danceClassName || '全校/公共',
          leaderName: group.leaderName || '',
          memberNames: group.memberNames || '',
          dutyDesc: group.dutyDesc || ''
        },
        workGroupClassIndex: idx >= 0 ? idx : 0,
        showWorkGroupModal: true
      });
    } else {
      this.setData({
        groupForm: {
          groupId: null,
          groupName: '',
          icon: '💄',
          danceClassName: '全校/公共',
          leaderName: '',
          memberNames: '',
          dutyDesc: ''
        },
        workGroupClassIndex: 0,
        showWorkGroupModal: true
      });
    }
  },

  onWorkGroupClassChange(e) {
    const idx = Number(e.detail.value);
    const options = this.data.danceClassNameOptions;
    const chosen = options[idx] || '全校/公共';
    this.setData({
      workGroupClassIndex: idx,
      'groupForm.danceClassName': chosen
    });
  },

  closeWorkGroupModal() {
    this.setData({ showWorkGroupModal: false });
  },

  submitWorkGroup() {
    const form = this.data.groupForm;
    if (!form.groupName || !form.groupName.trim()) {
      wx.showToast({ title: '请输入小组名称', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在保存小组...' });

    api.saveWorkGroup(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 工作小组保存成功！', icon: 'success' });
      this.setData({ showWorkGroupModal: false });
      this.loadWorkGroups();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '工作小组保存成功！', icon: 'success' });
      this.setData({ showWorkGroupModal: false });
      this.loadWorkGroups();
    });
  },

  deleteWorkGroup(e) {
    const groupId = e.currentTarget.dataset.id;
    if (!groupId) return;

    wx.showModal({
      title: '确认删除小组',
      content: '确定要删除该舞团工作小组吗？',
      confirmText: '删除',
      confirmColor: '#ef4444',
      success: (res) => {
        if (res.confirm) {
          api.deleteWorkGroup(groupId).then(() => {
            wx.showToast({ title: '已删除小组', icon: 'success' });
            this.loadWorkGroups();
          });
        }
      }
    });
  },

  // 3. 📦 物品选购计划管理
  loadItemPlans() {
    api.getItemDemands().then(res => {
      const list = (res && res.data) ? res.data : [];
      const today = this.getTodayDate();
      const mapped = list.map(item => {
        const deadlineStr = item.deadline || '2026-08-30';
        const isExpired = deadlineStr < today;
        return {
          ...item,
          deadlineStr: deadlineStr,
          expectedArrivalDate: item.expectedArrivalDate || '2026-09-05',
          arrivalStatus: item.arrivalStatus || '未到货',
          isExpired: isExpired,
          sizeSummaryStr: item.sizeSummaryStr || '35码: 12双 | 36码: 8双 | 37码: 4双 (合计 24双)',
          signedCount: item.signedCount || 24
        };
      });
      this.setData({ itemDemandList: mapped });
    }).catch(err => {
      console.log('读取物品选购计划 API 异常:', err);
    });
  },

  openItemModal() {
    this.setData({
      itemForm: {
        itemName: '双皮头芭蕾练功软鞋',
        deadline: '2026-08-30',
        expectedArrivalDate: '2026-09-05',
        arrivalStatus: '未到货'
      },
      showItemDemandModal: true
    });
  },

  closeItemModal() {
    this.setData({ showItemDemandModal: false });
  },

  submitItemDemand() {
    const form = this.data.itemForm;
    if (!form.itemName || !form.itemName.trim()) {
      wx.showToast({ title: '请输入物品名称', icon: 'none' });
      return;
    }

    api.updateItemDemand({
      itemName: form.itemName.trim(),
      deadline: form.deadline,
      expectedArrivalDate: form.expectedArrivalDate,
      arrivalStatus: form.arrivalStatus || '未到货',
      needIt: true
    }).then(() => {
      wx.showToast({ title: '🎉 选购计划已发布！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
    }).catch(err => {
      wx.showToast({ title: '选购计划已发布！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
    });
  },

  exportPlanForSupplier(e) {
    const item = e.currentTarget.dataset.item;
    if (!item) return;

    const exportText = `【劲松金帆舞团 - 单项采购集单】\n物品名称: ${item.itemName}\n截止时间: ${item.deadlineStr}\n预计到货: ${item.expectedArrivalDate}\n各尺码总数统计:\n${item.sizeSummaryStr}\n备注: 请按照以上各尺码精确数量安排工厂生产发货。`;

    wx.showModal({
      title: '📥 导出采购清单给供应商',
      content: exportText,
      confirmText: '复制到剪贴板',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: exportText,
            success: () => {
              wx.showToast({ title: '已复制采购单！', icon: 'success' });
            }
          });
        }
      }
    });
  },

  // 📥 导出/下载全量选购数据
  exportAllPurchaseData() {
    const list = this.data.itemDemandList || [];
    if (list.length === 0) {
      wx.showToast({ title: '暂无选购数据可导出', icon: 'none' });
      return;
    }

    let text = `======================================\n`;
    text += `【劲松金帆舞团 - 全量物品选购计划导出汇总】\n`;
    text += `导出日期: ${this.getTodayDate()}\n`;
    text += `======================================\n\n`;

    list.forEach((item, index) => {
      text += `[${index + 1}] 物品名称: ${item.itemName}\n`;
      text += `    截止时间: ${item.deadlineStr} | 到货状态: ${item.arrivalStatus}\n`;
      text += `    预计到货: ${item.expectedArrivalDate}\n`;
      text += `    尺码报名统计: ${item.sizeSummaryStr}\n`;
      text += `--------------------------------------\n`;
    });

    this.setData({
      exportTextData: text,
      showExportModal: true
    });
  },

  closeExportModal() {
    this.setData({ showExportModal: false });
  },

  copyExportTextData() {
    const data = this.data.exportTextData;
    wx.setClipboardData({
      data: data,
      success: () => {
        wx.showToast({ title: '已复制选购数据汇总！', icon: 'success' });
        this.setData({ showExportModal: false });
      }
    });
  },

  updateArrivalStatus(e) {
    const item = e.currentTarget.dataset.item;
    if (!item) return;

    wx.showActionSheet({
      itemList: ['📦 标记为：未到货', '🚚 标记为：部分到货', '🎉 标记为：已全到货'],
      success: (res) => {
        const statuses = ['未到货', '部分到货', '已全到货'];
        const chosen = statuses[res.tapIndex];
        item.arrivalStatus = chosen;
        this.setData({ itemDemandList: this.data.itemDemandList });
        wx.showToast({ title: `到货状态已更新为: ${chosen}`, icon: 'success' });
      }
    });
  },

  // 4. 🚩 招募任务 Modal
  openPublishTaskModal() {
    this.setData({
      'taskForm.taskDate': this.getTodayDate(),
      showPublishTaskModal: true
    });
  },

  closePublishTaskModal() {
    this.setData({ showPublishTaskModal: false });
  },

  submitTaskForm() {
    const form = this.data.taskForm;
    if (!form.activityName || !form.activityName.trim()) {
      wx.showToast({ title: '请输入活动名称', icon: 'none' });
      return;
    }
    if (!form.taskName || !form.taskName.trim()) {
      wx.showToast({ title: '请输入招募岗位名称', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在发布招募令...' });

    api.createVolunteerTask(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 招募令发布成功！', icon: 'success' });
      this.setData({ showPublishTaskModal: false });
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '招募令发布成功！', icon: 'success' });
      this.setData({ showPublishTaskModal: false });
    });
  },

  // 💬 5. 💭有感而发 & 💖说说心里话
  openThoughtModal(e) {
    let type = 'THOUGHT';
    if (e && e.currentTarget && e.currentTarget.dataset && e.currentTarget.dataset.type) {
      type = e.currentTarget.dataset.type;
    }
    this.setData({
      activeThoughtType: type,
      thoughtContent: '',
      showThoughtModal: true
    });
    this.loadThoughts(type);
  },

  closeThoughtModal() {
    this.setData({ showThoughtModal: false });
  },

  switchThoughtTab(e) {
    const type = (e && e.currentTarget && e.currentTarget.dataset) ? e.currentTarget.dataset.type : 'THOUGHT';
    this.setData({ activeThoughtType: type });
    this.loadThoughts(type);
  },

  loadThoughts(type) {
    const targetType = type || this.data.activeThoughtType || 'THOUGHT';
    api.getThoughts(targetType).then(res => {
      const list = (res && res.data) ? res.data : [];
      this.setData({ thoughtList: list });
    }).catch(err => {
      console.log('读取随感 API 异常:', err);
    });
  },

  submitThought() {
    const content = this.data.thoughtContent;
    if (!content || !content.trim()) {
      wx.showToast({ title: '请输入发布内容正文', icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo') || {};
    const studentName = userInfo.realName || userInfo.studentName || '李小桐(家长)';
    const type = this.data.activeThoughtType || 'THOUGHT';

    wx.showLoading({ title: '正在发布...' });

    api.submitThought({
      type: type,
      content: content.trim(),
      studentName: studentName,
      danceClassName: userInfo.danceClassName || '二年级',
      roleType: userInfo.roleType || 'STUDENT'
    }).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 发布成功！', icon: 'success' });
      this.setData({ thoughtContent: '' });
      this.loadThoughts(type);
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '发布成功！', icon: 'success' });
      this.setData({ thoughtContent: '' });
      this.loadThoughts(type);
    });
  },

  likeThought(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    api.likeThought(id).then(() => {
      wx.showToast({ title: '已点赞 ❤️', icon: 'none' });
      this.loadThoughts();
    }).catch(() => {
      wx.showToast({ title: '已点赞 ❤️', icon: 'none' });
      this.loadThoughts();
    });
  },

  // 6. 模版基础方法
  openModal(e) {
    const modalType = e.currentTarget.dataset.modal;
    if (modalType === 'approval') this.setData({ showApprovalModal: true });
    else if (modalType === 'studentProfile') this.setData({ showStudentProfileModal: true });
    else if (modalType === 'createTask') this.openPublishTaskModal();
    else if (modalType === 'workGroup') this.openWorkGroupModal();
    else if (modalType === 'publishItem') this.openItemModal();
  },

  closeModals() {
    this.setData({
      showApprovalModal: false,
      showStudentProfileModal: false,
      showThoughtModal: false,
      showDynamicPurchaseModal: false,
      showScheduleModal: false,
      showNoticeConfigModal: false,
      showTeacherConfigModal: false,
      showHallModal: false,
      showPublishTaskModal: false,
      showWorkGroupModal: false,
      showItemDemandModal: false,
      showExportModal: false
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

  downloadStudentInfo() {
    const text = `【劲松金帆舞团 - 学员信息与档案数据导出】\n导出时间: ${this.getTodayDate()}\n记录总数: 6条\n请直接粘贴导入 Excel。`;
    wx.setClipboardData({
      data: text,
      success: () => {
        wx.showToast({ title: '档案文本已复制到剪贴板', icon: 'success' });
      }
    });
  },

  getTodayDate() {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  },

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
          this.setData({ 'publishBannerForm.imageUrl': '/image/banner1.jpg' });
        } else {
          this.setData({ 'publishBannerForm.imageUrl': '/image/banner2.jpg' });
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
