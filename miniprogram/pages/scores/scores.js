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

    // 全量成绩数据集
    allStudentScores: [],

    // 过滤后的前端展示成绩
    displayScores: [],

    // Form Modal 控制与数据
    showScoreModal: false,
    modalPermissionHint: '',
    scoreForm: {
      id: null,
      studentName: '',
      gradeLevel: '二年级',
      chineseScore: '',
      mathScore: '',
      englishScore: '',
      resumeBio: ''
    }
  },

  onShow() {
    this.initUserPermissions();
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
    let roleTitle = '🎒 学员专属成绩档案';
    let permissionTip = '已锁定为仅查看与管理个人成绩履历';
    let isAdminOrTeacher = false;
    let isCommitteeRole = false;
    let isStudentRole = false;
    let canSelectGrade = false;
    let lockedGradeName = '全校全部';

    if (role === 'SUPER_ADMIN' || role === 'TEACHER') {
      userRoleName = role === 'SUPER_ADMIN' ? '👑 超级管理员' : '👩‍🏫 专业老师';
      roleTitle = '👑 全校成绩教务指挥中心';
      permissionTip = '管理员与老师可查看全校所有年级数据，并可通过下拉切换年级或录入任意成绩';
      isAdminOrTeacher = true;
      canSelectGrade = true;
    } else if (role === 'COMMITTEE') {
      userRoleName = '🤝 家委会';
      roleTitle = '🤝 家委会年级专属数据中心';
      permissionTip = `家委会仅可查看与修改对应【${targetGradeName}】的学员成绩数据`;
      isCommitteeRole = true;
      canSelectGrade = false;
      lockedGradeName = targetGradeName;
    } else {
      userRoleName = '🎒 学员/家长';
      roleTitle = '👤 个人专属成绩档案';
      permissionTip = '家长仅可查看与录入修改自己孩子的成绩及简历信息';
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

    this.filterDisplayScores();
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
      // 家委会只能看到对应年级数据
      filtered = allStudentScores.filter(item => item.gradeLevel === lockedGradeName);
    } else {
      // 家长仅能看到自己的数据
      filtered = allStudentScores.filter(item => {
        return currentStudentName && (item.studentName.indexOf(currentStudentName) !== -1 || currentStudentName.indexOf(item.studentName) !== -1);
      });
    }

    // 为每条数据绑定 canEdit 标记
    const processed = filtered.map(item => {
      let canEdit = false;
      if (isAdminOrTeacher) {
        canEdit = true; // 管理员和老师能改所有年级数据
      } else if (isCommitteeRole) {
        canEdit = (item.gradeLevel === lockedGradeName); // 家委会能改对应年级数据
      } else if (isStudentRole) {
        canEdit = currentStudentName && (item.studentName.indexOf(currentStudentName) !== -1 || currentStudentName.indexOf(item.studentName) !== -1);
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

  // 打开录入/编辑弹窗
  openAddScoreModal() {
    const { isAdminOrTeacher, isCommitteeRole, isStudentRole, lockedGradeName, userInfo } = this.data;
    const currentStudentName = (userInfo && (userInfo.studentName || userInfo.realName)) ? (userInfo.studentName || userInfo.realName) : '';

    let hint = '';
    let defaultGrade = '二年级';
    let defaultName = '';

    if (isAdminOrTeacher) {
      hint = '管理员/老师可录入任意年级与学员成绩';
    } else if (isCommitteeRole) {
      hint = `家委会限定录入/修改【${lockedGradeName}】学员成绩`;
      defaultGrade = lockedGradeName;
    } else {
      hint = '家长仅可修改本人学员的成绩档案';
      defaultGrade = '二年级';
      defaultName = currentStudentName;
    }

    const formGradeOptions = this.data.formGradeOptions;
    let formGradeIndex = formGradeOptions.indexOf(defaultGrade);
    if (formGradeIndex === -1) formGradeIndex = 0;

    this.setData({
      showScoreModal: true,
      modalPermissionHint: hint,
      formGradeIndex,
      scoreForm: {
        id: null,
        studentName: defaultName,
        gradeLevel: formGradeOptions[formGradeIndex],
        chineseScore: '',
        mathScore: '',
        englishScore: '',
        resumeBio: ''
      }
    });
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
      modalPermissionHint: `您正在编辑【${item.studentName}】的成绩记录`,
      formGradeIndex,
      scoreForm: {
        id: item.id,
        studentName: item.studentName,
        gradeLevel: item.gradeLevel,
        chineseScore: item.chineseScore,
        mathScore: item.mathScore,
        englishScore: item.englishScore,
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

  // 保存提交成绩
  saveScoreSubmit() {
    const { scoreForm, allStudentScores } = this.data;
    if (!scoreForm.studentName || !scoreForm.studentName.trim()) {
      wx.showToast({ title: '请填写学员姓名', icon: 'none' });
      return;
    }

    const chinese = parseFloat(scoreForm.chineseScore) || 0;
    const math = parseFloat(scoreForm.mathScore) || 0;
    const english = parseFloat(scoreForm.englishScore) || 0;
    const totalScore = Math.round((chinese + math + english) * 10) / 10;

    let updatedList = allStudentScores.slice();

    if (scoreForm.id) {
      // 更新现有记录
      updatedList = updatedList.map(item => {
        if (item.id === scoreForm.id) {
          return {
            ...item,
            studentName: scoreForm.studentName.trim(),
            gradeLevel: scoreForm.gradeLevel,
            chineseScore: chinese,
            mathScore: math,
            englishScore: english,
            totalScore: totalScore,
            resumeBio: scoreForm.resumeBio ? scoreForm.resumeBio.trim() : ''
          };
        }
        return item;
      });
    } else {
      // 新增记录
      const newId = Date.now();
      updatedList.unshift({
        id: newId,
        studentName: scoreForm.studentName.trim(),
        gradeLevel: scoreForm.gradeLevel,
        chineseScore: chinese,
        mathScore: math,
        englishScore: english,
        totalScore: totalScore,
        resumeBio: scoreForm.resumeBio ? scoreForm.resumeBio.trim() : ''
      });
    }

    this.setData({
      allStudentScores: updatedList,
      showScoreModal: false
    });

    this.filterDisplayScores();

    // 亦调后台服务 API 进行持久化备份
    api.submitStudentProfile({
      studentName: scoreForm.studentName,
      gradeLevel: scoreForm.gradeLevel,
      chineseScore: chinese,
      mathScore: math,
      englishScore: english,
      resumeBio: scoreForm.resumeBio
    });

    wx.showToast({ title: '成绩录入/保存成功！', icon: 'success' });
  }
});
