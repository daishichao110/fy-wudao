const api = require('../../utils/api.js');

Page({
  data: {
    showRegisterForm: false, // 默认隐藏申请表单；当微信登录校验为“未批准”时自动跳转开启
    selectedRole: 'STUDENT', // 默认选中身份：普通学员/家长
    studentName: '',
    parentName: '',
    teacherName: '',
    danceClassName: '',
    phone: '', // 彻底移除任何默认手机号，纯净绑定真实授权手机号
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

  // 💬 微信官方原生授权手机号回调处理 (获取当前微信绑定的真实手机号)
  onGetPhoneNumber(e) {
    console.log('微信授权手机号回调:', e.detail);

    // 1. 如果用户拒绝授权手机号
    if (e.detail && e.detail.errMsg && e.detail.errMsg.indexOf('deny') !== -1) {
      wx.showToast({ title: '未授权手机号，无法进行身份验证', icon: 'none' });
      return;
    }

    // 2. 从微信原生授权回调中提取真实的手机号 (DevTools / 微信真机环境)
    let realPhone = '';
    if (e.detail && e.detail.phoneNumber) {
      realPhone = e.detail.phoneNumber;
    } else if (e.detail && e.detail.phone) {
      realPhone = e.detail.phone;
    }

    // 3. 将真实授权手机号同步至 data 并发起后端身份识别
    if (realPhone) {
      this.setData({ phone: realPhone });
    }
    this.executeWxLoginCheck(realPhone);
  },

  // 核心校验逻辑：微信登录 -> 匹配授权手机号 -> 已批准直接进入主页 / 未批准提示并携带该手机号进入申请页
  executeWxLoginCheck(authorizedPhone) {
    if (this.data.loading) return;
    this.setData({ loading: true });

    const targetPhone = authorizedPhone || this.data.phone || '';

    wx.login({
      success: (loginRes) => {
        api.wxLogin({
          code: loginRes.code || 'wx_code',
          phone: targetPhone
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
          
          // 未批准或未注册：跳转到填写申请卡片！
          wx.showModal({
            title: '🔒 暂未开通登录权限',
            content: (err && err.message) || '您的微信账号尚未开通权限。点击【去申请】填写资料提交审批！',
            showCancel: true,
            confirmText: '去申请',
            success: (modalRes) => {
              if (modalRes.confirm) {
                this.setData({
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

  // 📝 提交申请 (智能区分专业老师 vs 学员/家长)
  handleApplyLoginPermission() {
    const { selectedRole, teacherName, danceClassName, studentName, parentName, phone, relationOptions, relationIndex, classIndex, classEnumList } = this.data;

    if (!phone || !phone.trim() || phone.trim().length !== 11) {
      wx.showToast({ title: '请输入正确的11位手机号', icon: 'none' });
      return;
    }

    let payload = {};

    if (selectedRole === 'TEACHER') {
      if (!teacherName || !teacherName.trim()) {
        wx.showToast({ title: '请输入教师姓名', icon: 'none' });
        return;
      }
      payload = {
        parentName: teacherName.trim(),
        studentName: '教务教师',
        relationship: '教师',
        phone: phone.trim(),
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
        studentName: studentName.trim(),
        parentName: parentName ? parentName.trim() : (studentName.trim() + relationship),
        relationship: relationship,
        phone: phone.trim(),
        roleType: selectedRole,
        danceClassName: chosenClassObj.code || 'GRADE_2'
      };
    }

    this.setData({ applying: true });

    api.applyLoginPermission(payload).then(res => {
      this.setData({ applying: false, showRegisterForm: false });
      wx.showModal({
        title: '✅ 申请提交成功',
        content: `您的手机号 (${phone}) 开通申请已提交。必须等待超级管理员审批同意后，再次点击微信授权登录即可直接进入对应权限主页！`,
        showCancel: false
      });
    }).catch(err => {
      this.setData({ applying: false });
    });
  }
});
