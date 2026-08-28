const api = require('../../utils/api.js');

const classEnumMap = {
  'GRADE_ALL': '全校/公共',
  'GRADE_2': '二年级',
  'GRADE_3': '三年级',
  'GRADE_4': '四年级',
  'GRADE_5': '五年级',
  'GRADE_6': '六年级'
};

const classEnumList = [
  { code: 'GRADE_ALL', name: '全校/公共' },
  { code: 'GRADE_2', name: '二年级' },
  { code: 'GRADE_3', name: '三年级' },
  { code: 'GRADE_4', name: '四年级' },
  { code: 'GRADE_5', name: '五年级' },
  { code: 'GRADE_6', name: '六年级' }
];

Page({
  data: {
    userRoleName: '👑 超级管理员',
    isTeacherOrAdmin: true,
    hasManagePermission: true,
    currentClassCode: 'GRADE_2',
    currentClassName: '二年级',
    currentClass: '二年级',
    classRange: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerOptions: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerIndex: 1,

    weekDaysList: [],
    taskList: [],
    workGroupList: [],
    itemDemandList: [],

    // 舞团工作小组配置 Modal
    showWorkGroupModal: false,
    groupForm: {
      groupId: null,
      groupName: '',
      icon: '💄',
      leaderName: '',
      memberNames: '',
      dutyDesc: ''
    },

    // 物品选购计划发布 Modal (家委发布，家长报名填尺码，无需发布数量)
    showItemDemandModal: false,
    itemForm: {
      itemName: '双皮头芭蕾练功软鞋',
      deadline: '2026-08-30',
      expectedArrivalDate: '2026-09-05',
      arrivalStatus: '未到货'
    },

    // 舞团大型活动招募任务 Modal (支持时间段)
    showPublishTaskModal: false,
    taskGroupTypeOptions: ['妆造', '服装与整装', '营养后勤', '宣传与摄影', '安保与车队', '通用班务'],
    taskGroupIndex: 0,
    taskForm: {
      activityName: '',
      taskName: '',
      groupType: '妆造',
      taskDate: '',
      serviceTime: '13:30 - 17:30 (4小时段)',
      quotaCount: '4',
      description: ''
    }
  },

  onLoad() {
    this.initUserClassScope();
    this.initFutureSevenDays();
    this.loadWorkGroups();
    this.loadItemPlans();
  },

  onShow() {
    this.refreshUserInfo();
    this.initUserClassScope();
    this.initFutureSevenDays();
    this.loadVolunteerTasks();
    this.loadWorkGroups();
    this.loadItemPlans();
  },

  refreshUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || 'STUDENT';
    const isTeacherOrAdmin = (role === 'SUPER_ADMIN' || role === 'TEACHER');
    const isCommittee = (role === 'COMMITTEE');
    this.setData({
      isTeacherOrAdmin,
      hasManagePermission: isTeacherOrAdmin || isCommittee,
      userRoleName: userInfo.roleName || '家长/学员'
    });
  },

  initUserClassScope() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || 'STUDENT';
    const isTeacherOrAdmin = (role === 'SUPER_ADMIN' || role === 'TEACHER');
    
    let defaultCode = userInfo.danceClassName || 'GRADE_2';
    if (!classEnumMap[defaultCode]) {
      defaultCode = 'GRADE_2';
    }
    if (isTeacherOrAdmin && (!userInfo.danceClassName || userInfo.danceClassName === 'GRADE_ALL' || userInfo.danceClassName === '全校全局管理')) {
      defaultCode = 'GRADE_ALL';
    }

    const idx = classEnumList.findIndex(item => item.code === defaultCode);
    this.setData({
      isTeacherOrAdmin,
      currentClassCode: defaultCode,
      currentClassName: classEnumMap[defaultCode] || '二年级',
      currentClass: classEnumMap[defaultCode] || '二年级',
      classPickerIndex: idx >= 0 ? idx : 0
    });
  },

  onClassPickerChange(e) {
    const idx = Number(e.detail.value);
    const chosenObj = classEnumList[idx] || classEnumList[0];
    this.setData({
      classPickerIndex: idx,
      currentClassCode: chosenObj.code,
      currentClassName: chosenObj.name,
      currentClass: chosenObj.name
    });
    this.initFutureSevenDays();
    this.loadVolunteerTasks();
  },

  // 1. 📅 7天家长活动与看护排班
  initFutureSevenDays() {
    const daysOfWeek = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const queryClassCode = this.data.currentClassCode === 'GRADE_ALL' ? '' : this.data.currentClassCode;
    
    api.getDutySchedules(queryClassCode).then(res => {
      const dbDuties = (res && res.data) ? res.data : [];
      const dutyMap = {};
      dbDuties.forEach(item => {
        if (item.dutyDate && item.assigneeName) {
          dutyMap[item.dutyDate] = item.assigneeName;
        }
      });

      const result = [];
      const today = new Date();

      for (let i = 0; i < 7; i++) {
        const targetDate = new Date(today.getTime() + i * 24 * 60 * 60 * 1000);
        const year = targetDate.getFullYear();
        const month = String(targetDate.getMonth() + 1).padStart(2, '0');
        const day = String(targetDate.getDate()).padStart(2, '0');
        const dateStr = `${year}-${month}-${day}`;

        let dayLabel = daysOfWeek[targetDate.getDay()];
        if (i === 0) dayLabel = '今天 (周' + dayLabel.charAt(1) + ')';
        if (i === 1) dayLabel = '明天 (周' + dayLabel.charAt(1) + ')';

        const assignee = dutyMap[dateStr] || '';

        result.push({
          dateStr: dateStr,
          monthDayStr: `${targetDate.getMonth() + 1}月${targetDate.getDate()}日`,
          dayLabel: dayLabel,
          assignedUser: assignee,
          isAssigned: !!assignee
        });
      }

      this.setData({ weekDaysList: result });
    }).catch(err => {
      console.log('读取看护排班 API 异常:', err);
    });
  },

  claimDutySchedule(e) {
    const item = e.currentTarget.dataset.item;
    if (!item) return;

    if (item.isAssigned) {
      wx.showToast({ title: `该日期已由【${item.assignedUser}】报名安排！`, icon: 'none' });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo') || {};
    const parentDisplayName = userInfo.realName || userInfo.parentName || '热心家长';
    const targetClass = this.data.currentClassName || '二年级';

    wx.showModal({
      title: '确认报名家长活动',
      content: `确定由【${parentDisplayName}】报名 [${targetClass}] ${item.monthDayStr} 的家长看护与家委协同？`,
      confirmText: '确认报名',
      success: (res) => {
        if (res.confirm) {
          api.claimDutySchedule({
            dutyDate: item.dateStr,
            assigneeName: parentDisplayName,
            danceClassName: this.data.currentClassCode
          }).then(() => {
            wx.showToast({ title: `🎉 成功报名(${parentDisplayName})！`, icon: 'success' });
            this.initFutureSevenDays();
          }).catch(err => {
            wx.showToast({ title: `成功报名看护活动！`, icon: 'success' });
            this.initFutureSevenDays();
          });
        }
      }
    });
  },

  // 2. 🚩 舞团大型活动专项招募任务列表
  loadVolunteerTasks() {
    const queryClassCode = this.data.currentClassCode === 'GRADE_ALL' ? '' : this.data.currentClassCode;
    api.getVolunteerTasks(queryClassCode).then(res => {
      const list = (res && res.data) ? res.data : [];
      const mapped = list.map(item => ({
        ...item,
        isCollapsed: false,
        enrolledList: item.enrolledList || []
      }));
      this.setData({ taskList: mapped });
    }).catch(err => {
      console.log('读取招募任务 API 异常:', err);
    });
  },

  enrollTask(e) {
    const task = e.currentTarget.dataset.task;
    if (!task) return;

    const userInfo = wx.getStorageSync('userInfo') || {};
    const parentName = userInfo.realName || userInfo.parentName || '热心家长';

    wx.showModal({
      title: '报名大型活动招募任务',
      content: `确定报名参加 [${task.activityName} - ${task.taskName}] 家员协助？`,
      confirmText: '确认报名',
      success: (res) => {
        if (res.confirm) {
          api.enrollVolunteerTask({
            taskId: task.taskId,
            parentName: parentName
          }).then(() => {
            wx.showToast({ title: '🎉 报名招募成功！', icon: 'success' });
            this.loadVolunteerTasks();
          }).catch(err => {
            this.loadVolunteerTasks();
          });
        }
      }
    });
  },

  // 3. 🛠️ 舞团工作小组数据与编辑
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
    if (group) {
      this.setData({
        groupForm: {
          groupId: group.groupId,
          groupName: group.groupName,
          icon: group.icon || '💄',
          leaderName: group.leaderName || '',
          memberNames: group.memberNames || '',
          dutyDesc: group.dutyDesc || ''
        },
        showWorkGroupModal: true
      });
    } else {
      this.setData({
        groupForm: {
          groupId: null,
          groupName: '',
          icon: '💄',
          leaderName: '',
          memberNames: '',
          dutyDesc: ''
        },
        showWorkGroupModal: true
      });
    }
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

  // 4. 📦 物品选购计划发布与报名 (截止后置灰不可填写，家委可更新到货状态与导出给供应商)
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

    const exportText = `【劲松金帆舞团 - 物品采购集单】\n物品名称: ${item.itemName}\n预计到货: ${item.expectedArrivalDate}\n各尺码总数统计:\n${item.sizeSummaryStr}\n备注: 请按照以上各尺码精确数量安排工厂生产发货。`;

    wx.showModal({
      title: '📥 导出采购清单给供应商',
      content: exportText,
      confirmText: '复制到剪贴板',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: exportText,
            success: () => {
              wx.showToast({ title: '已复制采购单给供应商！', icon: 'success' });
            }
          });
        }
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

  // 5. 🚩 管理员与家委【发布大型活动专项招募任务 Modal】
  openPublishTaskModal() {
    this.setData({
      'taskForm.taskDate': this.getTodayDate(),
      showPublishTaskModal: true
    });
  },

  closePublishTaskModal() {
    this.setData({ showPublishTaskModal: false });
  },

  onTaskGroupTypeChange(e) {
    const idx = Number(e.detail.value);
    const type = this.data.taskGroupTypeOptions[idx] || '妆造';
    this.setData({
      taskGroupIndex: idx,
      'taskForm.groupType': type
    });
  },

  submitPublishTask() {
    const form = this.data.taskForm;
    if (!form.activityName || !form.activityName.trim()) {
      wx.showToast({ title: '请输入大型活动名称', icon: 'none' });
      return;
    }
    if (!form.taskName || !form.taskName.trim()) {
      wx.showToast({ title: '请输入招募任务组名称', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '正在发布招募令...' });

    api.createVolunteerTask({
      activityName: form.activityName.trim(),
      taskName: form.taskName.trim(),
      groupType: form.groupType,
      taskDate: form.taskDate,
      serviceTime: form.serviceTime,
      quotaCount: Number(form.quotaCount) || 4,
      description: form.description || '协助舞团大型展演后勤保障',
      danceClassName: this.data.currentClassCode
    }).then(() => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 成功发布招募任务！', icon: 'success' });
      this.setData({ showPublishTaskModal: false });
      this.loadVolunteerTasks();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '成功发布招募任务！', icon: 'success' });
      this.setData({ showPublishTaskModal: false });
      this.loadVolunteerTasks();
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
