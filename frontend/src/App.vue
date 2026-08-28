<template>
  <div class="miniprogram-phone-shell">
    <!-- 微信小程序顶端状态栏 (Status Bar) -->
    <div class="wx-status-bar">
      <span>20:28</span>
      <div style="display: flex; gap: 6px; align-items: center;">
        <Signal :size="14" />
        <Wifi :size="14" />
        <Battery :size="16" />
      </div>
    </div>

    <!-- 微信小程序原生 Header 导航栏 (带右上角微信胶囊按钮) -->
    <div class="wx-nav-bar">
      <div class="wx-title">劲松金帆舞团</div>

      <!-- 右上角微信标志性胶囊按钮 -->
      <div class="wx-capsule">
        <span class="wx-capsule-btn" title="微信小程序菜单"><MoreHorizontal :size="16" /></span>
        <div class="wx-capsule-line"></div>
        <span class="wx-capsule-btn" title="关闭小程序"><CircleDot :size="14" /></span>
      </div>
    </div>

    <!-- 角色/身份模拟切换快捷栏 (方便演示 5 种 RBAC 角色) -->
    <div class="role-bar">
      <span class="role-label"><UserCheck :size="12" /> 体验角色:</span>
      <select :value="store.currentUser.id" @change="handleRoleChange" class="role-select">
        <option v-for="user in store.availableRoles" :key="user.id" :value="user.id">
          {{ user.name }} ({{ user.roleName }})
        </option>
      </select>
    </div>

    <!-- 小程序 Page 视图容器 -->
    <main class="wx-body">
      <router-view />
    </main>

    <!-- 微信小程序底部 4 栏 TabBar 导航 -->
    <nav class="wx-tabbar">
      <router-link to="/" class="wx-tab-item">
        <Home class="wx-tab-icon" />
        <span>首页指南</span>
      </router-link>
      <router-link to="/schedule" class="wx-tab-item">
        <Calendar class="wx-tab-icon" />
        <span>教务课表</span>
      </router-link>
      <router-link to="/volunteers" class="wx-tab-item">
        <HeartHandshake class="wx-tab-icon" />
        <span>家委协同</span>
      </router-link>
      <router-link to="/profile" class="wx-tab-item">
        <User class="wx-tab-icon" />
        <span>我的</span>
      </router-link>
    </nav>

    <!-- 微信小程序 Message Toast 消息通知 -->
    <div v-if="store.toast.show" :class="['toast-notification', `toast-${store.toast.type}`]">
      <span>{{ store.toast.message }}</span>
    </div>
  </div>
</template>

<script setup>
import { Signal, Wifi, Battery, MoreHorizontal, CircleDot, UserCheck, Home, Calendar, HeartHandshake, User } from 'lucide-vue-next'
import { useAppStore } from './stores/appStore'

const store = useAppStore()

const handleRoleChange = (e) => {
  const selectedId = Number(e.target.value)
  const targetUser = store.availableRoles.find(u => u.id === selectedId)
  if (targetUser) {
    store.switchRole(targetUser)
  }
}
</script>

<style scoped>
.role-bar {
  background: #f1f5f9;
  padding: 6px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-color);
  font-size: 11px;
}
.role-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  color: var(--primary-purple);
}
.role-select {
  border: 1px solid var(--border-color);
  background: #fff;
  border-radius: 12px;
  font-size: 11px;
  padding: 2px 6px;
  outline: none;
  font-weight: 600;
}

.toast-notification {
  position: absolute;
  top: 96px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 200;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.2s ease;
  white-space: nowrap;
}
.toast-success { background: var(--wx-green); }
.toast-info { background: var(--primary-purple); }
.toast-error { background: #ef4444; }

@keyframes fadeIn {
  from { opacity: 0; transform: translate(-50%, -10px); }
  to { opacity: 1; transform: translate(-50%, 0); }
}
</style>
