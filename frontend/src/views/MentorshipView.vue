<template>
  <div class="mentorship-view">
    <div class="view-header">
      <h2>传帮带“结对子”成长港</h2>
      <p class="subtitle">高年级学姐学长引导新学员，共同压腿、盘头打卡积累姐妹星勋章</p>
    </div>

    <div v-for="pair in mentorshipList" :key="pair.pairId" class="dance-card">
      <div class="pair-header">
        <span class="tag-badge tag-amber"><Award :size="12" /> {{ pair.termName }}</span>
        <span class="stars-cnt"><Star :size="14" class="star-icon" /> 姐妹星: {{ pair.starPoints }} 点</span>
      </div>

      <div class="pair-box">
        <div class="person senior">
          <div class="role-title">高年级领航员</div>
          <div class="name">{{ pair.seniorStudentName }}</div>
        </div>

        <div class="link-icon">
          <Zap :size="18" />
          <div class="cnt">已打卡 {{ pair.checkinCount }} 次</div>
        </div>

        <div class="person junior">
          <div class="role-title">低年级新学员</div>
          <div class="name">{{ pair.juniorStudentName }}</div>
        </div>
      </div>

      <div class="pair-actions">
        <button @click="handleCheckin(pair)" class="btn-primary" style="width: 100%;">
          <CheckCircle2 :size="14" /> 提交结对互动打卡 (指导盘头/共同压腿 +5星)
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Award, Star, Zap, CheckCircle2 } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const mentorshipList = ref([])

const loadMentorships = async () => {
  try {
    const res = await api.getMentorships()
    if (res.data) mentorshipList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleCheckin = async (pair) => {
  try {
    const res = await api.checkinMentorship({ pairId: pair.pairId })
    if (res.code === 200) {
      store.showToast('结对子打卡成功！双方获得 5 颗姐妹星勋章。')
      loadMentorships()
    }
  } catch (e) {
    store.showToast('打卡失败', 'error')
  }
}

onMounted(() => {
  loadMentorships()
})
</script>

<style scoped>
.view-header { margin-bottom: 16px; }
.view-header h2 { font-size: 18px; font-weight: 700; color: var(--text-main); }
.subtitle { font-size: 12px; color: var(--text-muted); }

.pair-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.stars-cnt { font-size: 13px; font-weight: 700; color: #d97706; display: flex; align-items: center; gap: 4px; }
.star-icon { fill: #f59e0b; color: #f59e0b; }

.pair-box {
  background: var(--bg-app);
  border-radius: var(--radius-sm);
  padding: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.person { text-align: center; width: 38%; }
.person .role-title { font-size: 11px; color: var(--text-muted); margin-bottom: 2px; }
.person .name { font-size: 15px; font-weight: 700; color: var(--text-main); }
.person.senior .name { color: var(--primary-purple); }
.person.junior .name { color: var(--primary-rose); }

.link-icon { text-align: center; color: var(--primary-rose); }
.link-icon .cnt { font-size: 10px; color: var(--text-muted); margin-top: 2px; }
</style>
