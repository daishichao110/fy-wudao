const api = require('../../utils/api.js');

Page({
  data: {
    purchaseList: []
  },

  onShow() {
    api.getPurchases().then(res => {
      if (res.data) this.setData({ purchaseList: res.data });
    });
  },

  showProof(e) {
    const url = e.currentTarget.dataset.url;
    wx.showModal({
      title: '采购电子发票收据凭证',
      content: `关联凭证URL: ${url}`,
      showCancel: false,
      confirmText: '关闭'
    });
  }
});
