const api = require('../../utils/api.js');

Page({
  data: {
    userInfo: null,
    userRoleName: '学员',
    roleTitle: '🎒 学员专属成绩档案',
    permissionTip: '只能查看与更新个人成绩及简历信息',
    
    // 权限标志位
    isAdminOrTeacher: false,
    isCommitteeRole: false,
    isStudentRole: false,
    
    canSelectGrade: false,
    canAddScore: true,
    lockedGradeName: '全部年级',

    // 下拉选项
    gradeOptions: ['全校全部', '二年级', '三年级', '四年级', '五年级', '六年级'],
    gradeIndex: 0,

    formGradeOptions: ['二年级', '三年级', '四年级', '五年级', '六年级'],
    formGradeIndex: 0,

    // 全量成绩与档案数据集 (来自后端 MySQL 表 student_profile)
    allStudentScores: [],

    // 过滤后的前端展示成绩
    displayScores: [],

    // Form Modal 控制与数据
    showScoreModal: false,
    modalPermissionHint: '',
    scoreForm: {
      profileId: null,
      studentId: '',
      studentName: '',
      gradeLevel: '二年级',
      chineseScore: '',
      mathScore: '',
      englishScore: '',
      heightCm: '',
      weightKg: '',
      bustCm: '',
      waistCm: '',
      hipCm: '',
      shoeSize: '',
      resumeBio: ''
    }
  },

  onShow() {
    this.initUserPermissions();
    this.loadStudentProfiles();
  },

  // 从真实后端接口拉取 MySQL 中的全量学员成绩与档案数据
  loadStudentProfiles() {
    api.getStudentProfiles().then(res => {
      const list = (res && res.data) ? res.data : [];
      const mapped = list.map(item => {
        const chinese = parseFloat(item.chineseScore) || 0;
        const math = parseFloat(item.mathScore) || 0;
        const english = parseFloat(item.englishScore) || 0;
        const total = Math.round((chinese + math + english) * 10) / 10;
        return {
          id: item.profileId || item.studentId,
          profileId: item.profileId,
          studentId: item.studentId,
          studentName: item.studentName || '',
          gradeLevel: item.gradeLevel || '二年级',
          chineseScore: chinese,
          mathScore: math,
          englishScore: english,
          totalScore: total,
          heightCm: item.heightCm || '',
          weightKg: item.weightKg || '',
          bustCm: item.bustCm || '',
          waistCm: item.waistCm || '',
          hipCm: item.hipCm || '',
          shoeSize: item.shoeSize || '',
          resumeBio: item.resumeBio || ''
        };
      });

      this.setData({ allStudentScores: mapped });
      this.filterDisplayScores();
    }).catch(err => {
      console.log('读取后端学员档案接口异常:', err);
    });
  },

  // 核心：基于当前登录用户的角色判定查看与修改权限
  initUserPermissions() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    const role = userInfo.roleType || 'STUDENT';
    const rawClass = userInfo.danceClassName || 'GRADE_2';
    
    let targetGradeName = '二年级';
    if (rawClass.indexOf('GRADE_3') !== -1 || rawClass.indexOf('三年级') !== -1) targetGradeName = '三年级';
    else if (rawClass.indexOf('GRADE_4') !== -1 || rawClass.indexOf('四年级') !== -1) targetGradeName = '四年级';
    else if (rawClass.indexOf('GRADE_5') !== -1 || rawClass.indexOf('五年级') !== -1) targetGradeName = '五年级';
    else if (rawClass.indexOf('GRADE_6') !== -1 || rawClass.indexOf('六年级') !== -1) targetGradeName = '六年级';

    let userRoleName = '学员/家长';
    let roleTitle = '🎒 学员专属成绩与综合档案';
    let permissionTip = '已锁定为仅查看与维护个人专属唯一档案';
    let isAdminOrTeacher = false;
    let isCommitteeRole = false;
    let isStudentRole = false;
    let canSelectGrade = false;
    let lockedGradeName = '全校全部';

    if (role === 'SUPER_ADMIN' || role === 'TEACHER') {
      userRoleName = role === 'SUPER_ADMIN' ? '👑 超级管理员' : '👩‍🏫 专业老师';
      roleTitle = '👑 全校成绩与档案教务指挥中心';
      permissionTip = '管理员与老师可查看全校所有年级数据，并可通过下拉切换年级或录入任意成绩与身材档案';
      isAdminOrTeacher = true;
      canSelectGrade = true;
    } else if (role === 'COMMITTEE') {
      userRoleName = '🤝 家委会';
      roleTitle = '🤝 家委会年级专属数据中心';
      permissionTip = `家委会仅可查看与修改对应【${targetGradeName}】的学员成绩与档案数据`;
      isCommitteeRole = true;
      canSelectGrade = false;
      lockedGradeName = targetGradeName;
    } else {
      userRoleName = '🎒 学员/家长';
      roleTitle = '👤 个人专属成绩与身材体态档案';
      permissionTip = '家长仅可维护并覆盖自己小孩的唯一一条专属综合档案';
      isStudentRole = true;
      canSelectGrade = false;
      lockedGradeName = userInfo.studentName || userInfo.realName || '个人专属';
    }

    this.setData({
      userInfo,
      userRoleName,
      roleTitle,
      permissionTip,
      isAdminOrTeacher,
      isCommitteeRole,
      isStudentRole,
      canSelectGrade,
      lockedGradeName
    });
  },

  // 根据当前选择的年级/角色筛选出 displayScores，并赋予每一项 canEdit
  filterDisplayScores() {
    const { allStudentScores, isAdminOrTeacher, isCommitteeRole, isStudentRole, lockedGradeName, gradeOptions, gradeIndex, userInfo } = this.data;
    const selectedOption = gradeOptions[gradeIndex];
    const currentStudentName = (userInfo && (userInfo.studentName || userInfo.realName)) ? (userInfo.studentName || userInfo.realName) : '';

    let filtered = [];

    if (isAdminOrTeacher) {
      if (selectedOption && selectedOption !== '全校全部') {
        filtered = allStudentScores.filter(item => item.gradeLevel === selectedOption);
      } else {
        filtered = allStudentScores.slice();
      }
    } else if (isCommitteeRole) {
      filtered = allStudentScores.filter(item => item.gradeLevel === lockedGradeName);
    } else {
      // 家长端：精准匹配且仅展示自己小孩的唯一一条数据
      filtered = allStudentScores.filter(item => {
        return currentStudentName && (item.studentName.indexOf(currentStudentName) !== -1 || currentStudentName.indexOf(item.studentName) !== -1 || item.studentId === userInfo.userId);
      });
    }

    // 为每条数据绑定 canEdit 标记
    const processed = filtered.map(item => {
      let canEdit = false;
      if (isAdminOrTeacher) {
        canEdit = true;
      } else if (isCommitteeRole) {
        canEdit = (item.gradeLevel === lockedGradeName);
      } else if (isStudentRole) {
        canEdit = currentStudentName && (item.studentName.indexOf(currentStudentName) !== -1 || currentStudentName.indexOf(item.studentName) !== -1 || item.studentId === userInfo.userId);
      }
      return {
        ...item,
        canEdit
      };
    });

    // 默认按总分高到低降序
    processed.sort((a, b) => b.totalScore - a.totalScore);

    this.setData({ displayScores: processed });
  },

  onGradeChange(e) {
    const idx = Number(e.detail.value);
    this.setData({ gradeIndex: idx });
    this.filterDisplayScores();
  },

  // 打开录入/编辑弹窗 (对于普通家长，强校验并加载个人已有唯一记录)
  openAddScoreModal() {
    const { isAdminOrTeacher, isCommitteeRole, isStudentRole, lockedGradeName, userInfo, allStudentScores } = this.data;
    const currentStudentName = (userInfo && (userInfo.studentName || userInfo.realName)) ? (userInfo.studentName || userInfo.realName) : '';

    let hint = '';
    let defaultGrade = '二年级';
    let existingItem = null;

    if (isStudentRole) {
      hint = '家长仅维护小孩的唯一专属综合档案，多次保存将自动覆盖更新';
      defaultGrade = userInfo.danceClassName || '二年级';
      // 检查当前家长小孩是否已有档案
      existingItem = allStudentScores.find(item => item.studentId === userInfo.userId || (currentStudentName && item.studentName.indexOf(currentStudentName) !== -1));
    } else if (isCommitteeRole) {
      hint = `家委会限定录入/修改【${lockedGradeName}】学员成绩与档案`;
      defaultGrade = lockedGradeName;
    } else {
      hint = '管理员/老师可录入任意学员的全量成绩与身材体态档案';
    }

    if (existingItem) {
      // 已经存在档案：自动填充现有数据，保存时执行覆写 UPDATE
      const formGradeOptions = this.data.formGradeOptions;
      let formGradeIndex = formGradeOptions.indexOf(existingItem.gradeLevel);
      if (formGradeIndex === -1) formGradeIndex = 0;

      this.setData({
        showScoreModal: true,
        modalPermissionHint: hint,
        formGradeIndex,
        scoreForm: {
          profileId: existingItem.profileId,
          studentId: existingItem.studentId || userInfo.userId,
          studentName: existingItem.studentName,
          gradeLevel: existingItem.gradeLevel,
          chineseScore: existingItem.chineseScore,
          mathScore: existingItem.mathScore,
          englishScore: existingItem.englishScore,
          heightCm: existingItem.heightCm,
          weightKg: existingItem.weightKg,
          bustCm: existingItem.bustCm,
          waistCm: existingItem.waistCm,
          hipCm: existingItem.hipCm,
          shoeSize: existingItem.shoeSize,
          resumeBio: existingItem.resumeBio || ''
        }
      });
    } else {
      // 尚未建档：新建
      const formGradeOptions = this.data.formGradeOptions;
      let formGradeIndex = formGradeOptions.indexOf(defaultGrade);
      if (formGradeIndex === -1) formGradeIndex = 0;

      this.setData({
        showScoreModal: true,
        modalPermissionHint: hint,
        formGradeIndex,
        scoreForm: {
          profileId: null,
          studentId: userInfo.userId || '',
          studentName: isStudentRole ? currentStudentName : '',
          gradeLevel: formGradeOptions[formGradeIndex],
          chineseScore: '',
          mathScore: '',
          englishScore: '',
          heightCm: '',
          weightKg: '',
          bustCm: '',
          waistCm: '',
          hipCm: '',
          shoeSize: '',
          resumeBio: ''
        }
      });
    }
  },

  // 编辑指定记录
  editStudentScore(e) {
    const item = e.currentTarget.dataset.item;
    if (!item || !item.canEdit) {
      wx.showToast({ title: '没有权限修改该条记录', icon: 'none' });
      return;
    }

    const formGradeOptions = this.data.formGradeOptions;
    let formGradeIndex = formGradeOptions.indexOf(item.gradeLevel);
    if (formGradeIndex === -1) formGradeIndex = 0;

    this.setData({
      showScoreModal: true,
      modalPermissionHint: `您正在维护【${item.studentName}】的全量成绩与身材体态档案`,
      formGradeIndex,
      scoreForm: {
        profileId: item.profileId,
        studentId: item.studentId,
        studentName: item.studentName,
        gradeLevel: item.gradeLevel,
        chineseScore: item.chineseScore,
        mathScore: item.mathScore,
        englishScore: item.englishScore,
        heightCm: item.heightCm,
        weightKg: item.weightKg,
        bustCm: item.bustCm,
        waistCm: item.waistCm,
        hipCm: item.hipCm,
        shoeSize: item.shoeSize,
        resumeBio: item.resumeBio || ''
      }
    });
  },

  closeScoreModal() {
    this.setData({ showScoreModal: false });
  },

  onFormInput(e) {
    const field = e.currentTarget.dataset.field;
    const val = e.detail.value;
    this.setData({
      [`scoreForm.${field}`]: val
    });
  },

  onFormGradeChange(e) {
    const idx = Number(e.detail.value);
    const selectedGrade = this.data.formGradeOptions[idx];
    this.setData({
      formGradeIndex: idx,
      'scoreForm.gradeLevel': selectedGrade
    });
  },

  // 保存提交全量成绩与身材档案到后端 MySQL 数据库
  saveScoreSubmit() {
    const { scoreForm, userInfo } = this.data;
    if (!scoreForm.studentName || !scoreForm.studentName.trim()) {
      wx.showToast({ title: '请填写学员姓名', icon: 'none' });
      return;
    }

    const chinese = parseFloat(scoreForm.chineseScore) || 0;
    const math = parseFloat(scoreForm.mathScore) || 0;
    const english = parseFloat(scoreForm.englishScore) || 0;

    const payload = {
      profileId: scoreForm.profileId,
      studentId: scoreForm.studentId || userInfo.userId,
      studentName: scoreForm.studentName.trim(),
      gradeLevel: scoreForm.gradeLevel,
      chineseScore: chinese,
      mathScore: math,
      englishScore: english,
      heightCm: parseFloat(scoreForm.heightCm) || 0,
      weightKg: parseFloat(scoreForm.weightKg) || 0,
      bustCm: parseFloat(scoreForm.bustCm) || 0,
      waistCm: parseFloat(scoreForm.waistCm) || 0,
      hipCm: parseFloat(scoreForm.hipCm) || 0,
      shoeSize: parseFloat(scoreForm.shoeSize) || 0,
      parentName: userInfo.realName || '',
      parentPhone: userInfo.phone || '',
      resumeBio: scoreForm.resumeBio ? scoreForm.resumeBio.trim() : ''
    };

    wx.showLoading({ title: '提交保存中...' });

    // 核心：调用 Spring Boot 真实 API 写入/更新 MySQL 数据库
    api.submitStudentProfile(payload).then(res => {
      wx.hideLoading();
      wx.showToast({ title: '成绩与档案保存成功！', icon: 'success' });
      this.setData({ showScoreModal: false });
      // 重新拉取最新数据，保障数据 100% 真实落库与同步
      this.loadStudentProfiles();
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: (err && err.message) || '保存失败，请检查网络', icon: 'none' });
    });
  }
});
