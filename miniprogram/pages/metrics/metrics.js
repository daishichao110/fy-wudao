const api = require('../../utils/api.js');

Page({
  data: {
    currentUser: {},
    isTeacherOrAdmin: false,
    metric: null,
    allMetrics: [],
    showExportModal: false,
    csvData: ''
  },

  onShow() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const isTeacher = userInfo.roleType === 'TEACHER' || userInfo.roleType === 'SUPER_ADMIN';
    this.setData({
      currentUser: userInfo,
      isTeacherOrAdmin: isTeacher
    });

    if (isTeacher) {
      this.loadAllMetrics();
    } else {
      this.loadStudentMetric(userInfo.userId || 6);
    }
  },

  loadStudentMetric(studentId) {
    api.getStudentMetrics(studentId).then(res => {
      if (res.data) this.setData({ metric: res.data });
    });
  },

  loadAllMetrics() {
    api.getAllMetrics().then(res => {
      if (res.data) this.setData({ allMetrics: res.data });
    }).catch(() => {
      api.getStudentMetrics(6).then(res => {
        if (res.data) this.setData({ allMetrics: [res.data] });
      });
    });
  },

  exportSheet() {
    wx.showLoading({ title: '生成导出报表中...' });
    wx.request({
      url: `${api.BASE_URL}/metric/export-csv`,
      method: 'GET',
      success: (res) => {
        wx.hideLoading();
        if (res.data) {
          this.setData({
            csvData: res.data,
            showExportModal: true
          });
        }
      },
      fail: () => {
        wx.hideLoading();
        // 本地前端降级生成 CSV
        const list = this.data.allMetrics.length > 0 ? this.data.allMetrics : [this.data.metric];
        let csvStr = "学员ID,学员姓名,身高(cm),体重(kg),胸围(cm),腰围(cm),臀围(cm),胴长(cm),舞鞋码(欧码),测量日期\n";
        list.forEach(m => {
          if (m) {
            csvStr += `${m.studentId || 6},${m.studentName || '李小桐'},${m.heightCm || 132},${m.weightKg || 27.5},${m.bustCm || 62},${m.waistCm || 52},${m.hipCm || 65},${m.torsoLengthCm || 48},${m.shoeSize || 32},${m.measuredDate || '2026-08-21'}\n`;
          }
        });
        this.setData({
          csvData: csvStr,
          showExportModal: true
        });
      }
    });
  },

  closeExportModal() {
    this.setData({ showExportModal: false });
  },

  copyCsvContent() {
    wx.setClipboardData({
      data: this.data.csvData,
      success: () => {
        wx.showToast({ title: '已复制 CSV 数据，可在 Excel 粘贴', icon: 'success' });
        this.setData({ showExportModal: false });
      }
    });
  }
});
