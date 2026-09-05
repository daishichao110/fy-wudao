const api = require('../../utils/api.js');

const classEnumMap = {
  'GRADE_ALL': '全校公共',
  'GRADE_1': '一年级',
  'GRADE_2': '二年级',
  'GRADE_3': '三年级',
  'GRADE_4': '四年级',
  'GRADE_5': '五年级',
  'GRADE_6': '六年级'
};

const classEnumList = [
  { code: 'GRADE_ALL', name: '全校公共' },
  { code: 'GRADE_1', name: '一年级' },
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
    hasManagePermission: false,

    currentClassCode: 'GRADE_2',
    currentClassName: '二年级',
    currentClass: '二年级',
    classRange: ['全校公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerOptions: ['全校公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    classPickerIndex: 2,

    // 发布排课 Modal 控制
    showScheduleModal: false,
    scheduleClassOptions: ['全校公共', '一年级', '二年级', '三年级', '四年级', '五年级', '六年级'],
    scheduleClassIndex: 2,
    scheduleForm: {
      danceClassName: '二年级',
      classDate: '',
      courseName: '',
      startTime: '09:30',
      endTime: '11:30',
      classroomName: '101舞蹈大教室',
      teacherName: '',
      danceType: '芭蕾舞/中国舞',
      topsReq: '',
      bottomsReq: '',
      skirtReq: '',
      shoesReq: '',
      hairReq: '',
      propsReq: '',
      otherReq: '',
      remark: ''
    }
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
    const hasManagePermission = (role === 'SUPER_ADMIN' || role === 'TEACHER' || role === 'COMMITTEE');
    
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
      hasManagePermission,
      currentClassCode: defaultCode,
      currentClassName: classEnumMap[defaultCode] || '二年级',
      currentClass: classEnumMap[defaultCode] || '二年级',
      classPickerIndex: idx >= 0 ? idx : 2
    });
  },

  onClassPickerChange(e) {
    if (!this.data.isTeacherOrAdmin) {
      wx.showToast({ title: '仅管理员与老师可切换年级视图', icon: 'none' });
      return;
    }
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

  // 📅 生成最近 14 天滚动日历
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

  normalizeDateStr(dStr) {
    if (!dStr) return '';
    const parts = String(dStr).split('-');
    if (parts.length === 3) {
      const y = parts[0];
      const m = String(parts[1]).padStart(2, '0');
      const d = String(parts[2]).padStart(2, '0');
      return `${y}-${m}-${d}`;
    }
    return dStr;
  },

  updateDisplaySchedules() {
    const { scheduleList, selectedDateStr } = this.data;
    if (!selectedDateStr) {
      this.setData({ displayScheduleList: scheduleList });
    } else {
      const normSelected = this.normalizeDateStr(selectedDateStr);
      const filtered = scheduleList.filter(item => this.normalizeDateStr(item.classDate) === normSelected);
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

  // 📅 排课发布 Modal 操作
  openScheduleModal() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const defaultTeacher = (userInfo.roleType === 'TEACHER' && userInfo.realName) ? userInfo.realName : '';
    const chosenClass = this.data.currentClassName || '二年级';

    let idx = this.data.scheduleClassOptions.indexOf(chosenClass);
    if (idx < 0) idx = 2;

    this.setData({
      showScheduleModal: true,
      scheduleClassIndex: idx,
      scheduleForm: {
        danceClassName: chosenClass,
        classDate: this.getTodayDate(),
        courseName: '',
        startTime: '09:30',
        endTime: '11:30',
        classroomName: '',
        teacherName: defaultTeacher,
        danceType: '',
        topsReq: '',
        bottomsReq: '',
        skirtReq: '',
        shoesReq: '',
        hairReq: '',
        propsReq: '',
        otherReq: '',
        remark: ''
      }
    });
  },

  closeScheduleModal() {
    this.setData({ showScheduleModal: false });
  },

  onScheduleClassChange(e) {
    const idx = Number(e.detail.value);
    const chosen = this.data.scheduleClassOptions[idx] || '二年级';
    this.setData({
      scheduleClassIndex: idx,
      'scheduleForm.danceClassName': chosen
    });
  },

  onDateChange(e) {
    this.setData({ 'scheduleForm.classDate': e.detail.value });
  },

  onStartTimeChange(e) {
    this.setData({ 'scheduleForm.startTime': e.detail.value });
  },

  onEndTimeChange(e) {
    this.setData({ 'scheduleForm.endTime': e.detail.value });
  },

  submitScheduleForm() {
    const form = { ...this.data.scheduleForm };
    if (!form.courseName || !form.courseName.trim()) {
      wx.showToast({ title: '请输入课程名称', icon: 'none' });
      return;
    }
    if (!form.classDate || !form.classDate.trim()) {
      wx.showToast({ title: '请选择上课日期', icon: 'none' });
      return;
    }
    if (!form.startTime || !form.startTime.trim()) {
      wx.showToast({ title: '请选择开始时间', icon: 'none' });
      return;
    }
    if (!form.endTime || !form.endTime.trim()) {
      wx.showToast({ title: '请选择结束时间', icon: 'none' });
      return;
    }
    if (!form.classroomName || !form.classroomName.trim()) {
      wx.showToast({ title: '请输入教室房号', icon: 'none' });
      return;
    }
    if (!form.teacherName || !form.teacherName.trim()) {
      wx.showToast({ title: '请输入任课导师', icon: 'none' });
      return;
    }

    form.danceClassName = this.data.scheduleClassOptions[this.data.scheduleClassIndex] || '二年级';

    wx.showLoading({ title: '正在发布排课...' });

    api.createSchedule(form).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '🎉 排课发布成功！', icon: 'success' });
      this.setData({ showScheduleModal: false });
      this.loadSchedules();
    }).catch(err => {
      wx.hideLoading();
      console.error('发布排课后端接口异常:', err);
      const errMsg = (err && (err.message || err.errMsg)) || '排课发布失败，请重新尝试';
      wx.showToast({ title: errMsg, icon: 'none', duration: 3000 });
    });
  },

  loadSchedules(cb) {
    const today = this.getTodayDate();
    const collapsedMap = {};
    (this.data.scheduleList || []).forEach(item => {
      if (item.scheduleId) {
        collapsedMap[item.scheduleId] = item.isCollapsed;
      }
    });

    const queryClassCode = this.data.currentClassCode === 'GRADE_ALL' ? '' : this.data.currentClassCode;

    api.getSchedules(queryClassCode).then(res => {
      const remoteData = (res && res.data) ? res.data : [];
      const mappedRemote = remoteData.map(item => {
        let displayClassName = '全校/公共';
        if (item.danceClassName === 'GRADE_1' || item.danceClassName === '一年级') displayClassName = '一年级';
        else if (item.danceClassName === 'GRADE_2' || item.danceClassName === '二年级') displayClassName = '二年级';
        else if (item.danceClassName === 'GRADE_3' || item.danceClassName === '三年级') displayClassName = '三年级';
        else if (item.danceClassName === 'GRADE_4' || item.danceClassName === '四年级') displayClassName = '四年级';
        else if (item.danceClassName === 'GRADE_5' || item.danceClassName === '五年级') displayClassName = '五年级';
        else if (item.danceClassName === 'GRADE_6' || item.danceClassName === '六年级') displayClassName = '六年级';

        return {
          ...item,
          danceClassName: displayClassName,
          isCollapsed: collapsedMap[item.scheduleId] !== undefined ? !!collapsedMap[item.scheduleId] : true
        };
      });

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
