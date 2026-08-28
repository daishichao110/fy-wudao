<template>
  <div class="login-page">
    <div class="logo-box">
      <div class="logo-circle"><Sparkles :size="40" /></div>
      <h2 class="title">劲松金帆舞团</h2>
      <p class="subtitle">数字化教务与家委协同平台</p>
    </div>

    <div class="dance-card role-card">
      <div class="card-label">选择登录体验身份：</div>
      <div class="role-list">
        <div
          v-for="user in store.availableRoles"
          :key="user.id"
          @click="selectedRole = user.role"
          :class="['role-option', selectedRole === user.role ? 'selected' : '']"
        >
          <div class="r-name">{{ user.name }}</div>
          <div class="r-desc">{{ user.roleName }} - {{ user.class }}</div>
        </div>
      </div>
    </div>

    <button @click="handleLogin" class="wx-login-btn">
      <MessageCircle :size="20" /> 微信一键授权快捷登录
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Sparkles, MessageCircle } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const router = useRouter()
const selectedRole = ref('STUDENT')

const handleLogin = async () => {
  try {
    const res = await api.wxLogin({
      code: 'mock_wx_code_888',
      roleType: selectedRole.value
    })
    if (res.code === 200) {
      const dbUser = res.data?.userInfo
      let roleUser = null
      if (dbUser) {
        roleUser = {
          id: dbUser.userId,
          name: dbUser.realName || dbUser.studentName || dbUser.username,
          role: dbUser.roleType,
          roleName: dbUser.roleType === 'SUPER_ADMIN' ? '管理员' : (dbUser.roleType === 'TEACHER' ? '专业教师' : (dbUser.roleType === 'COMMITTEE' ? '班委/家委' : '学员/家长')),
          class: dbUser.danceClassName || '中国舞基础班',
          hours: dbUser.remainingHours || 0,
          points: dbUser.volunteerPoints || 0
        }
      } else {
        roleUser = store.availableRoles.find(u => u.role === selectedRole.value) || store.availableRoles[0]
      }
      store.switchRole(roleUser)
      store.showToast(`微信授权登录成功！当前身份: ${roleUser.name} (${roleUser.roleName})`, 'success')
      router.push('/')
    }
  } catch (e) {
    store.showToast('登录失败', 'error')
  }
}
</script>

<style scoped>
.login-page {
  padding: 30px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo-box { text-align: center; margin-bottom: 24px; }
.logo-circle {
  width: 72px; height: 72px; border-radius: 50%;
  background: var(--primary-gradient); color: #fff;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12px; box-shadow: var(--shadow-md);
}
.title { font-size: 20px; font-weight: 800; color: var(--text-main); }
.subtitle { font-size: 12px; color: var(--text-muted); }

.role-card { width: 100%; margin-bottom: 24px; }
.card-label { font-size: 13px; font-weight: 700; color: var(--primary-purple); margin-bottom: 10px; }
.role-list { display: flex; flex-direction: column; gap: 8px; }
.role-option {
  padding: 10px 12px; border-radius: 10px; border: 1px solid var(--border-color);
  background: #f8fafc; cursor: pointer; transition: all 0.2s ease;
}
.role-option.selected { border-color: var(--primary-purple); background: #f3e8ff; }
.r-name { font-weight: 700; font-size: 14px; color: var(--text-main); }
.r-desc { font-size: 11px; color: var(--text-muted); }

.wx-login-btn {
  width: 100%; background: var(--wx-green); color: #fff; border: none;
  padding: 12px; border-radius: 24px; font-size: 15px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  box-shadow: 0 4px 15px rgba(7, 193, 96, 0.3); cursor: pointer;
}
</style>
