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
    scheduleList: [],
    displayScheduleList: [],
    calendarDays: [],
    selectedDateStr: '',
    
    isTeacherOrAdmin: false,
    currentClassCode: 'GRADE_2',
    currentClassName: '二年级',
    currentClass: '二年级',
    classRange: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerOptions: ['全校/公共', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerIndex: 1
  },

  onLoad() {
    this.initUserClassScope();
    this.loadSchedules();
  },

  onShow() {
    this.initUserClassScope();
    this.loadSchedules();
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
      currentClass: chosenObj.name,
      selectedDateStr: ''
    });
    this.loadSchedules();
  },

  onPullDownRefresh() {
    this.loadSchedules(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 📅 生成最近 14 天滚动日历 (有课有颜色 #fff7ed/橙点，无课白底)
  buildCalendarDays(scheduleList) {
    const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const days = [];
    const now = new Date();

    for (let i = 0; i < 14; i++) {
      const targetDate = new Date(now.getTime() + i * 86400000);
      const yyyy = targetDate.getFullYear();
      const mm = String(targetDate.getMonth() + 1).padStart(2, '0');
      const dd = String(targetDate.getDate()).padStart(2, '0');
      const dateStr = `${yyyy}-${mm}-${dd}`;
      const weekDayStr = weekMap[targetDate.getDay()];

      const hasCourse = scheduleList.some(item => item.classDate === dateStr);

      days.push({
        dateStr,
        dayNum: dd,
        monthNum: targetDate.getMonth() + 1,
        weekDayStr: i === 0 ? '今天' : (i === 1 ? '明天' : weekDayStr),
        hasCourse,
        isSelected: this.data.selectedDateStr === dateStr
      });
    }

    this.setData({ calendarDays: days });
  },

  onSelectCalendarDate(e) {
    const dateStr = e.currentTarget.dataset.date;
    if (this.data.selectedDateStr === dateStr) {
      // 取消勾选，恢复展示全量排课
      this.setData({ selectedDateStr: '' });
    } else {
      this.setData({ selectedDateStr: dateStr });
    }

    this.updateDisplaySchedules();
    this.buildCalendarDays(this.data.scheduleList);
  },

  clearDateFilter() {
    this.setData({ selectedDateStr: '' });
    this.updateDisplaySchedules();
    this.buildCalendarDays(this.data.scheduleList);
  },

  updateDisplaySchedules() {
    const { scheduleList, selectedDateStr } = this.data;
    if (!selectedDateStr) {
      this.setData({ displayScheduleList: scheduleList });
    } else {
      const filtered = scheduleList.filter(item => item.classDate === selectedDateStr);
      this.setData({ displayScheduleList: filtered });
    }
  },

  toggleCollapse(e) {
    const id = e.currentTarget.dataset.id;
    const list = this.data.scheduleList.map(item => {
      if (item.scheduleId === id) {
        item.isCollapsed = !item.isCollapsed;
      }
      return item;
    });
    this.setData({ scheduleList: list });
    this.updateDisplaySchedules();
  },

  getTodayDate() {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  },

  loadSchedules(cb) {
    const today = this.getTodayDate();
    const collapsedMap = {};
    (this.data.scheduleList || []).forEach(item => {
      if (item.isCollapsed) collapsedMap[item.scheduleId] = true;
    });

    const queryClassCode = this.data.currentClassCode === 'GRADE_ALL' ? '' : this.data.currentClassCode;

    api.getSchedules(queryClassCode).then(res => {
      const remoteData = (res && res.data) ? res.data : [];
      const validRemote = remoteData.filter(item => !item.classDate || item.classDate >= today);
      const mappedRemote = validRemote.map(item => ({
        ...item,
        isCollapsed: !!collapsedMap[item.scheduleId]
      }));

      this.setData({ scheduleList: mappedRemote });
      this.updateDisplaySchedules();
      this.buildCalendarDays(mappedRemote);

      if (cb) cb();
    }).catch(err => {
      console.log('读取排课后端 API 异常:', err);
      this.setData({ scheduleList: [], displayScheduleList: [] });
      if (cb) cb();
    });
  }
});
