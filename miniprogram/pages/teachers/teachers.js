const api = require('../../utils/api.js');

Page({
  data: {
    categoryList: ['全部分类', '芭蕾舞', '中国舞', '现代舞'],
    currentCategory: '全部分类',
    teacherList: [],
    filteredTeachers: [],
    showDetailModal: false,
    selectedTeacher: null
  },

  onShow() {
    this.loadTeachers();
  },

  onPullDownRefresh() {
    this.loadTeachers(() => {
      wx.stopPullDownRefresh();
    });
  },

  loadTeachers(cb) {
    api.getTeacherList().then(res => {
      const rawList = (res && res.data) ? res.data : [];
      const uniqueList = this.deduplicateTeachers(rawList);
      this.setData({ teacherList: uniqueList });
      this.filterByCategory(this.data.currentCategory, uniqueList);
      if (typeof cb === 'function') cb();
    }).catch(err => {
      console.log('读取后端教师列表 API 异常:', err);
      this.setData({ teacherList: [], filteredTeachers: [] });
      if (typeof cb === 'function') cb();
    });
  },

  // 严格按姓名去重：防止同一教师因本地缓存与数据库ID不一致出现重复
  deduplicateTeachers(list) {
    const result = [];
    const nameSet = new Set();
    list.forEach(item => {
      if (!item || !item.name) return;
      const nameKey = item.name.trim();
      if (!nameSet.has(nameKey)) {
        nameSet.add(nameKey);
        result.push(item);
      }
    });
    return result;
  },

  selectCategory(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({ currentCategory: category });
    this.filterByCategory(category, this.data.teacherList);
  },

  filterByCategory(category, list) {
    if (category === '全部分类') {
      this.setData({ filteredTeachers: list });
    } else {
      const filtered = list.filter(t => t.danceType && t.danceType.includes(category));
      this.setData({ filteredTeachers: filtered });
    }
  },

  openTeacherDetail(e) {
    const teacher = e.currentTarget.dataset.teacher;
    if (teacher) {
      this.setData({
        selectedTeacher: teacher,
        showDetailModal: true
      });
    }
  },

  closeDetailModal() {
    this.setData({ showDetailModal: false });
  }
});
