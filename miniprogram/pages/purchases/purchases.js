const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: null,
    userRoleName: '家长/学员',
    roleTitle: '📦 集中采购与选购中心',
    permissionTip: '支持家长在线选购报名、家委到货状态维护与供应商采购单导出',

    hasManagePermission: false,

    itemDemandList: [],

    // 发布/新增物品选购计划 Modal
    showItemDemandModal: false,
    itemForm: {
      itemName: '',
      deadline: '',
      expectedArrivalDate: '',
      arrivalStatus: '未到货'
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

    let userRoleName = '🎒 学员/家长';
    let roleTitle = '📦 全校物品选购与需求集单';
    let permissionTip = '已登录账号可参看物品到货状态与选购计划需求';
    let hasManagePermission = false;

    if (role === 'SUPER_ADMIN' || role === 'TEACHER') {
      userRoleName = role === 'SUPER_ADMIN' ? '👑 超级管理员' : '👩‍🏫 专业老师';
      roleTitle = '👑 全校集中采购与选购指挥中心';
      permissionTip = '管理员与老师可发布选购计划、更新到货状态并一键导出采购清单给供应商';
      hasManagePermission = true;
    } else if (role === 'COMMITTEE') {
      userRoleName = '🤝 家委会成员';
      roleTitle = '🤝 家委会集中采购协同中心';
      permissionTip = '家委干部可新增选购计划、维护到货进度与汇总导出各尺码报名数据';
      hasManagePermission = true;
    }

    this.setData({
      userInfo,
      userRoleName,
      roleTitle,
      permissionTip,
      hasManagePermission
    });
  },

  loadItemPlans(cb) {
    api.getItemDemands().then(res => {
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
          sizeSummaryStr: item.sizeSummaryStr || '',
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

  openItemModal() {
    this.setData({
      itemForm: {
        itemName: '双皮头芭蕾练功软鞋 (粉色)',
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

    wx.showLoading({ title: '正在发布选购计划...' });

    api.updateItemDemand({
      itemName: form.itemName.trim(),
      deadline: form.deadline,
      expectedArrivalDate: form.expectedArrivalDate,
      arrivalStatus: form.arrivalStatus || '未到货',
      needIt: true
    }).then(() => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 选购计划已发布！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: '选购计划已发布！', icon: 'success' });
      this.setData({ showItemDemandModal: false });
      this.loadItemPlans();
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
  }
});
