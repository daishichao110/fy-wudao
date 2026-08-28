const api = require('../../utils/api.js');

Page({
  data: {
    featuredList: []
  },

  onShow() {
    api.getFeaturedQA().then(res => {
      if (res.data) this.setData({ featuredList: res.data });
    });
  }
});
