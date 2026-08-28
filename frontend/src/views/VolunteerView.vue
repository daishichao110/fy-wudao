<template>
  <div class="volunteer-view">
    <div class="view-header">
      <h2>家委志愿分工与协同大厅</h2>
      <p class="subtitle">极简认领：名额实时扣减，无需审批，积累爱心志愿积分</p>
    </div>

    <!-- 家长爱心积分卡片 -->
    <div class="dance-card points-banner">
      <div class="banner-left">
        <HeartHandshake :size="24" class="icon" />
        <div>
          <div class="user font-bold">{{ store.currentUser.name }}</div>
          <div class="sub">当前爱心志愿服务积分</div>
        </div>
      </div>
      <div class="pts">{{ store.currentUser.points }} <small>分</small></div>
    </div>

    <!-- 任务组列表 -->
    <div v-for="task in taskList" :key="task.taskId" class="dance-card">
      <div class="card-top">
        <span :class="['tag-badge', getGroupTagClass(task.groupType)]">
          {{ getGroupLabel(task.groupType) }}
        </span>
        <span :class="['status-badge', task.status === 'FULL' ? 'status-full' : 'status-open']">
          {{ task.status === 'FULL' ? '名额已满' : '招募中' }}
        </span>
      </div>

      <h3 class="task-name">{{ task.taskName }}</h3>
      <p class="activity-sub">关联赛事活动：{{ task.activityName }}</p>

      <div class="progress-bar">
        <div class="fill" :style="{ width: (task.enrolledCount / task.quotaCount * 100) + '%' }"></div>
      </div>

      <div class="task-foot">
        <span class="quota">已认领 {{ task.enrolledCount }} / {{ task.quotaCount }} 人</span>
        <button
          @click="handleEnroll(task)"
          :disabled="task.status === 'FULL'"
          :class="['btn-primary', task.status === 'FULL' ? 'btn-disabled' : '']"
        >
          <UserPlus :size="14" /> {{ task.status === 'FULL' ? '已被招满' : '一键认领任务 (免审)' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { HeartHandshake, UserPlus } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const taskList = ref([])

const loadTasks = async () => {
  try {
    const res = await api.getVolunteerTasks()
    if (res.data) taskList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const getGroupLabel = (type) => {
  const map = {
    COSTUME: '服装清点组',
    MAKEUP: '化妆造型组',
    CATERING: '后勤餐饮组',
    PHOTO: '摄影摄像组'
  }
  return map[type] || '志愿组'
}

const getGroupTagClass = (type) => {
  const map = {
    COSTUME: 'tag-rose',
    MAKEUP: 'tag-purple',
    CATERING: 'tag-amber',
    PHOTO: 'tag-blue'
  }
  return map[type] || 'tag-purple'
}

const handleEnroll = async (task) => {
  try {
    const payload = {
      taskId: task.taskId,
      userId: store.currentUser.id,
      userName: store.currentUser.name
    }
    const res = await api.enrollVolunteer(payload)
    if (res.code === 200) {
      store.showToast('任务认领成功！无需审核，爱心积分+15分。')
      store.currentUser.points += 15
      loadTasks()
    }
  } catch (e) {
    store.showToast('认领失败或您已认领', 'error')
  }
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.view-header { margin-bottom: 16px; }
.view-header h2 { font-size: 18px; font-weight: 700; color: var(--text-main); }
.subtitle { font-size: 12px; color: var(--text-muted); }

.points-banner {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.banner-left { display: flex; align-items: center; gap: 10px; }
.user { font-size: 15px; font-weight: 700; }
.sub { font-size: 11px; opacity: 0.9; }
.pts { font-size: 24px; font-weight: 800; }
.pts small { font-size: 12px; font-weight: 400; }

.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.status-badge { font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 12px; }
.status-open { background: #d1fae5; color: #047857; }
.status-full { background: #f1f5f9; color: #94a3b8; }

.task-name { font-size: 15px; font-weight: 700; color: var(--text-main); margin-bottom: 2px; }
.activity-sub { font-size: 12px; color: var(--text-muted); margin-bottom: 12px; }

.progress-bar { height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; margin-bottom: 12px; }
.progress-bar .fill { height: 100%; background: var(--primary-gradient); transition: width 0.3s ease; }

.task-foot { display: flex; justify-content: space-between; align-items: center; }
.quota { font-size: 12px; color: var(--text-muted); font-weight: 500; }

.btn-disabled { background: #cbd5e1 !important; cursor: not-allowed; }
</style>
