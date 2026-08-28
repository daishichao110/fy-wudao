import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    currentUser: {
      id: 6,
      name: '李小桐(新学员)',
      role: 'STUDENT',
      roleName: '学员/家长',
      class: '中国舞基础班',
      hours: 22,
      points: 10
    },
    availableRoles: [
      { id: 1, name: '管理员', role: 'SUPER_ADMIN', roleName: '管理员', class: '全校全局', hours: 999, points: 100 },
      { id: 2, name: '林依依老师', role: 'TEACHER', roleName: '专业教师', class: '芭蕾高级班', hours: 500, points: 50 },
      { id: 4, name: '王妈妈(家委组长)', role: 'COMMITTEE', roleName: '班委/家委', class: '芭蕾高级班', hours: 24, points: 85 },
      { id: 5, name: '张悦悦(高年级学姐)', role: 'STUDENT', roleName: '高年级学员', class: '芭蕾高级班', hours: 18, points: 120 },
      { id: 6, name: '李小桐(新学员)', role: 'STUDENT', roleName: '新学员/家长', class: '中国舞基础班', hours: 22, points: 10 }
    ],
    toast: {
      show: false,
      message: '',
      type: 'success'
    }
  }),
  actions: {
    switchRole(user) {
      this.currentUser = { ...user }
      localStorage.setItem('currentUserId', user.id)
      this.showToast(`已成功切换身份为: ${user.name} (${user.roleName})`, 'info')
    },
    showToast(msg, type = 'success') {
      this.toast.message = msg
      this.toast.type = type
      this.toast.show = true
      setTimeout(() => {
        this.toast.show = false
      }, 3000)
    }
  }
})
