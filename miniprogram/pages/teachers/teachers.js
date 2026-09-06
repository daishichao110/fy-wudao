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
      console.log('[DEBUG TEACHER LIST FE] 后端返回教师数据共 ' + rawList.length + ' 条:', rawList);
      const uniqueList = this.deduplicateTeachers(rawList);
      const mapped = uniqueList.map(item => {
        const fullPic = api.getImageUrl(item.avatarUrl);
        console.log('[DEBUG TEACHER LIST FE] 教师 Item:', item.name, '-> 处理后的头像 URL:', fullPic);
        return {
          ...item,
          avatarUrl: fullPic
        };
      });
      this.setData({ teacherList: mapped });
      this.filterByCategory(this.data.currentCategory, mapped);
      if (typeof cb === 'function') cb();
    }).catch(err => {
      console.error('[DEBUG TEACHER LIST FE] ❌ 读取后端教师列表 API 异常:', err);
      this.setData({ teacherList: [], filteredTeachers: [] });
      if (typeof cb === 'function') cb();
    });
  },

  // 严格按姓名去重：若遇到同名教师且已有记录缺乏有效图片，优先保留带 OSS 图片的新记录
  deduplicateTeachers(list) {
    const map = new Map();
    list.forEach(item => {
      if (!item || !item.name) return;
      const nameKey = item.name.trim();
      if (!map.has(nameKey)) {
        map.set(nameKey, item);
      } else {
        const existing = map.get(nameKey);
        const existingHasAvatar = existing.avatarUrl && (existing.avatarUrl.includes('wudao/') || existing.avatarUrl.startsWith('http'));
        const currentHasAvatar = item.avatarUrl && (item.avatarUrl.includes('wudao/') || item.avatarUrl.startsWith('http'));
        // 如果现有记录没有合法图片而新记录有，或者新记录是最新添加的，则替换
        if (!existingHasAvatar && currentHasAvatar) {
          map.set(nameKey, item);
        }
      }
    });
    return Array.from(map.values());
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
