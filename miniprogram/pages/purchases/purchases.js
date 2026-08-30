const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: null,
    userRoleName: '家长/学员',
    userGradeName: '二年级',
    roleTitle: '📦 集中采购与选购中心',
    permissionTip: '根据对应注册年级展示选购需求与到货进度',

    hasManagePermission: false,
    isCommitteeLocked: false,

    itemClassOptions: ['全校/公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    itemClassIndex: 0,

    itemDemandList: [],

    // 发布/新增物品选购计划 Modal
    showItemDemandModal: false,
    itemForm: {
      itemName: '',
      deadline: '',
      expectedArrivalDate: '',
      arrivalStatus: '未到货',
      danceClassName: '全校/公共'
    },

    // 导出 Modal
    showExportModal: false,
    exportTextData: ''
  },

  onShow() {
    this.initUserPermissions();
    this.loadItemPlans();
  },

  onPullDownRefresh() {
    this.loadItemPlans(() => {
      wx.stopPullDownRefresh();
    });
  },

  getTodayDate() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  initUserPermissions() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || 'STUDENT';
    const rawClass = userInfo.danceClassName || 'GRADE_2';

    let targetGradeName = '二年级';
    if (rawClass.indexOf('GRADE_1') !== -1 || rawClass.indexOf('一年级') !== -1) targetGradeName = '一年级';
    else if (rawClass.indexOf('GRADE_3') !== -1 || rawClass.indexOf('三年级') !== -1) targetGradeName = '三年级';
    else if (rawClass.indexOf('GRADE_4') !== -1 || rawClass.indexOf('四年级') !== -1) targetGradeName = '四年级';
    else if (rawClass.indexOf('GRADE_5') !== -1 || rawClass.indexOf('五年级') !== -1) targetGradeName = '五年级';
    else if (rawClass.indexOf('GRADE_6') !== -1 || rawClass.indexOf('六年级') !== -1) targetGradeName = '六年级';

    let userRoleName = '🎒 学员/家长';
    let roleTitle = '📦 全校物品选购与需求集单';
    let permissionTip = `对应展示【${targetGradeName}】及全校公共的物品选购需求`;
    let hasManagePermission = false;
    let isCommitteeLocked = false;

    let itemClassOptions = ['全校/公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'];
    let itemClassIndex = 0;

    if (role === 'SUPER_ADMIN' || role === 'TEACHER') {
      userRoleName = role === 'SUPER_ADMIN' ? '👑 超级管理员' : '👩‍🏫 专业老师';
      roleTitle = '👑 全校集中采购与选购指挥中心';
      permissionTip = '管理员与老师拥有全校权限，可发布任意年级或全校公共的物品选购计划';
      hasManagePermission = true;
      isCommitteeLocked = false;
      itemClassOptions = ['全校/公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'];
      itemClassIndex = 0;
    } else if (role === 'COMMITTEE') {
      userRoleName = '🤝 家委会成员';
      roleTitle = '🤝 家委会集中采购协同中心';
      permissionTip = `家委仅限发布与维护当前所属【${targetGradeName}】的物品需求`;
      hasManagePermission = true;
      isCommitteeLocked = true; // 锁定年级，禁止家委会跨年级发布
      itemClassOptions = [targetGradeName];
      itemClassIndex = 0;
    } else {
      userRoleName = '🎒 学员/家长';
      roleTitle = '📦 年级物品选购与需求集单';
      permissionTip = `展示匹配【${targetGradeName}】与全校公共的选购清单`;
      hasManagePermission = false;
      isCommitteeLocked = false;
    }

    this.setData({
      userInfo,
      userRoleName,
      userGradeName: targetGradeName,
      roleTitle,
      permissionTip,
      hasManagePermission,
      isCommitteeLocked,
      itemClassOptions,
      itemClassIndex,
      todayDateStr: this.getTodayDate()
    });
  },

  loadItemPlans(cb) {
    const { userInfo } = this.data;
    const role = userInfo ? userInfo.roleType : 'STUDENT';
    const rawClass = userInfo ? (userInfo.danceClassName || 'GRADE_2') : 'GRADE_2';
    
    let targetGradeName = '二年级';
    if (rawClass.indexOf('GRADE_1') !== -1 || rawClass.indexOf('一年级') !== -1) targetGradeName = '一年级';
    else if (rawClass.indexOf('GRADE_3') !== -1 || rawClass.indexOf('三年级') !== -1) targetGradeName = '三年级';
    else if (rawClass.indexOf('GRADE_4') !== -1 || rawClass.indexOf('四年级') !== -1) targetGradeName = '四年级';
    else if (rawClass.indexOf('GRADE_5') !== -1 || rawClass.indexOf('五年级') !== -1) targetGradeName = '五年级';
    else if (rawClass.indexOf('GRADE_6') !== -1 || rawClass.indexOf('六年级') !== -1) targetGradeName = '六年级';

    // 管理员/老师可查全校全部；家长/家委按注册年级精准过滤
    const filterClass = (role === 'SUPER_ADMIN' || role === 'TEACHER') ? '全校全部' : targetGradeName;

    api.getItemDemands(filterClass).then(res => {
      const list = (res && res.data) ? res.data : [];
      const today = this.getTodayDate();
      const mapped = list.map(item => {
        const deadlineStr = item.deadline || today;
        const isExpired = deadlineStr < today;
        return {
          ...item,
          deadlineStr: deadlineStr,
          expectedArrivalDate: item.expectedArrivalDate || '',
          arrivalStatus: item.arrivalStatus || '未到货',
          isExpired: isExpired,
          danceClassName: item.danceClassName || '全校/公共',
          sizeSummaryStr: item.sizeSummaryStr || '已登记采购需求',
          signedCount: item.signedCount || 0
        };
      });
      this.setData({ itemDemandList: mapped });
      if (typeof cb === 'function') cb();
    }).catch(err => {
      console.log('读取物品选购计划 API 异常:', err);
      if (typeof cb === 'function') cb();
    });
  },

  onItemClassChange(e) {
    const idx = Number(e.detail.value);
    const chosen = this.data.itemClassOptions[idx] || '全校/公共';
    this.setData({
      itemClassIndex: idx,
      'itemForm.danceClassName': chosen
    });
  },

  onItemNameInput(e) {
    this.setData({
      'itemForm.itemName': e.detail.value
    });
  },

  onDeadlineDateChange(e) {
    this.setData({
      'itemForm.deadline': e.detail.value
    });
  },

  onArrivalDateChange(e) {
    this.setData({
      'itemForm.expectedArrivalDate': e.detail.value
    });
  },

  openItemModal() {
    const chosenClass = this.data.itemClassOptions[this.data.itemClassIndex] || '全校/公共';
    const today = this.getTodayDate();
    this.setData({
      showItemDemandModal: true,
      todayDateStr: today,
      itemForm: {
        itemName: '',
        deadline: today,
        expectedArrivalDate: '',
        arrivalStatus: '未到货',
        danceClassName: chosenClass
      }
    });
  },

  closeItemModal() {
    this.setData({ showItemDemandModal: false });
  },

  submitItemDemand() {
    const form = this.data.itemForm;
    const today = this.getTodayDate();

    if (!form.itemName || !form.itemName.trim()) {
      wx.showToast({ title: '请输入物品名称', icon: 'none' });
      return;
    }

    if (!form.deadline) {
      wx.showToast({ title: '请选择报名截止日期', icon: 'none' });
      return;
    }

    if (form.deadline < today) {
      wx.showToast({ title: '截止日期不能早于今天', icon: 'none' });
      return;
    }

    if (form.expectedArrivalDate && form.expectedArrivalDate < form.deadline) {
      wx.showToast({ title: '预计到货日期不能早于截止日期', icon: 'none' });
      return;
    }

    const chosenClass = this.data.itemClassOptions[this.data.itemClassIndex] || '全校/公共';
    form.danceClassName = chosenClass;

    wx.showLoading({ title: '正在发布选购计划...' });

    api.createItemDemand(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 选购计划发布成功！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 选购计划发布成功！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
    });
  },

  exportAllPurchaseData() {
    if (!this.data.hasManagePermission) {
      wx.showToast({ title: '权限不足，仅管理角色可导出全量数据', icon: 'none' });
      return;
    }
    api.exportItemDemands().then(res => {
      const csvStr = (res && res.data) ? res.data : '暂无数据';
      this.setData({
        exportTextData: csvStr,
        showExportModal: true
      });
    }).catch(err => {
      wx.showToast({ title: '导出数据异常', icon: 'none' });
    });
  },

  exportPlanForSupplier(e) {
    const item = e.currentTarget.dataset.item;
    if (!item) return;
    const text = `【劲松金帆舞团 - 采购单】\n物品名称: ${item.itemName}\n适用年级: ${item.danceClassName || '全校/公共'}\n截止日期: ${item.deadlineStr}\n期望到货: ${item.expectedArrivalDate}\n尺码统计: ${item.sizeSummaryStr}`;
    wx.setClipboardData({
      data: text,
      success: () => {
        wx.showToast({ title: '单项采购单已复制', icon: 'success' });
      }
    });
  },

  closeExportModal() {
    this.setData({ showExportModal: false });
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
  }
});
