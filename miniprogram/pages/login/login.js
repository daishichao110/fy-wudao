const api = require('../../utils/api.js');

Page({
  data: {
    showRegisterForm: false, // 默认隐藏申请表单；当微信登录校验为“未批准”时自动跳转开启
    selectedRole: 'STUDENT', // 默认选中身份：普通学员/家长
    studentName: '',
    parentName: '',
    teacherName: '',
    danceClassName: '',
    phone: '', // 微信授权解析出的手机号
    inputPhone: '', // 手动输入的手机号 (与 phone 分离，避免输入过程中视图重新渲染切换)
    relationOptions: ['爸爸', '妈妈', '爷爷/奶奶/其他监护人'],
    relationIndex: 0,
    classEnumList: [
      { code: 'GRADE_2', name: '二年级' },
      { code: 'GRADE_3', name: '三年级' },
      { code: 'GRADE_4', name: '四年级' },
      { code: 'GRADE_5', name: '五年级' },
      { code: 'GRADE_6', name: '六年级' }
    ],
    classRange: ['二年级', '三年级', '四年级', '五年级', '六年级'],
    classIndex: 0,
    applying: false,
    loading: false
  },

  onLoad() {
    console.log('登录页加载，重置测试登录态');
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  onClassChange(e) {
    this.setData({ classIndex: Number(e.detail.value) });
  },

  onRelationChange(e) {
    this.setData({ relationIndex: Number(e.detail.value) });
  },

  onRoleChange(e) {
    this.setData({ selectedRole: e.detail.value });
  },

  selectRole(e) {
    const role = e.currentTarget.dataset.role;
    if (role) {
      this.setData({ selectedRole: role });
    }
  },

  // 取消申请，返回主登录界面
  cancelRegisterForm() {
    this.setData({ showRegisterForm: false });
  },

  // 🔑 获取或自动生成客户端唯一持久化的 OpenID (彻底解决每次 wx.login 生成不同 Code 导致找不到 OpenID 的问题)
  getOrCreateOpenId() {
    let openId = wx.getStorageSync('wx_openid');
    if (!openId) {
      openId = 'wx_openid_' + Date.now() + '_' + Math.floor(Math.random() * 10000);
      wx.setStorageSync('wx_openid', openId);
    }
    return openId;
  },

  // 💬 微信一键授权登录 (基于标准 wx.login 凭证 code + 持久化 OpenID 识别账号)
  handleWxLogin() {
    if (this.data.loading) return;
    this.setData({ loading: true });

    const openId = this.getOrCreateOpenId();

    wx.login({
      success: (loginRes) => {
        api.wxLogin({
          code: loginRes.code || '',
          openId: openId
        }).then(res => {
          this.setData({ loading: false });
          if (res.data && res.data.userInfo) {
            const user = res.data.userInfo;
            wx.setStorageSync('token', res.data.token);
            wx.setStorageSync('userInfo', user);

            const roleNameMap = {
              'SUPER_ADMIN': '👑 超级管理员',
              'TEACHER': '👩‍🏫 专业老师',
              'COMMITTEE': '📜 家委干部',
              'STUDENT': '👨‍👩‍👧 学员/家长'
            };
            const roleTitle = roleNameMap[user.roleType] || '用户';

            wx.showToast({ title: `识别为[${roleTitle}]，登录成功！`, icon: 'success' });

            setTimeout(() => {
              wx.switchTab({ url: '/pages/home/home' });
            }, 600);
          }
        }).catch(err => {
          this.setData({ loading: false });
          
          // 未批准或未注册：提示并进入资料申请卡片
          wx.showModal({
            title: '🔒 暂未开通登录权限',
            content: (err && err.message) || '您的微信账号尚未开通权限。点击【去申请】填写资料提交审批！',
            showCancel: true,
            confirmText: '去申请',
            success: (modalRes) => {
              if (modalRes.confirm) {
                this.setData({
                  wxCode: loginRes.code || '',
                  showRegisterForm: true
                });
              }
            }
          });
        });
      },
      fail: () => {
        this.setData({ loading: false });
        wx.showToast({ title: '微信 wx.login 调用失败', icon: 'none' });
      }
    });
  },

  // 📝 提交申请 (提交持久化 OpenID 到数据库，审核通过后即可直接解封无感秒登)
  handleApplyLoginPermission() {
    const { selectedRole, teacherName, danceClassName, studentName, parentName, phone, relationOptions, relationIndex, classIndex, classEnumList, wxCode } = this.data;
    const openId = this.getOrCreateOpenId();

    let payload = {};

    if (selectedRole === 'TEACHER') {
      if (!teacherName || !teacherName.trim()) {
        wx.showToast({ title: '请输入教师姓名', icon: 'none' });
        return;
      }
      payload = {
        code: wxCode || '',
        openId: openId,
        parentName: teacherName.trim(),
        studentName: '教务教师',
        relationship: '教师',
        phone: phone ? phone.trim() : '',
        roleType: 'TEACHER',
        danceClassName: danceClassName ? danceClassName.trim() : '全校芭蕾/中国舞教务'
      };
    } else {
      if (!studentName || !studentName.trim()) {
        wx.showToast({ title: '请输入学生姓名', icon: 'none' });
        return;
      }
      const relationship = (relationOptions && relationOptions[relationIndex]) ? relationOptions[relationIndex] : '爸爸';
      const chosenClassObj = (classEnumList && classEnumList[classIndex]) ? classEnumList[classIndex] : { code: 'GRADE_2', name: '二年级' };
      payload = {
        code: wxCode || '',
        openId: openId,
        studentName: studentName.trim(),
        parentName: parentName ? parentName.trim() : (studentName.trim() + relationship),
        relationship: relationship,
        phone: phone ? phone.trim() : '',
        roleType: selectedRole,
        danceClassName: chosenClassObj.code || 'GRADE_2'
      };
    }

    this.setData({ applying: true });

    api.applyLoginPermission(payload).then(res => {
      this.setData({ applying: false, showRegisterForm: false });
      wx.showModal({
        title: '✅ 申请提交成功',
        content: `您的身份开通申请已成功提交！等待超级管理员审批同意后，点击【微信一键授权登录】即可直接进入对应权限主页。`,
        showCancel: false
      });
    }).catch(err => {
      this.setData({ applying: false });
    });
  }
});
