<template>
  <div class="metric-view">
    <!-- 教师 / 管理员视角：查看全校学员档案 -->
    <div v-if="store.isTeacherOrAdmin">
      <div class="dance-card teacher-header">
        <div class="header-left">
          <span class="tag-badge tag-purple">教师专权视角</span>
          <h3 class="t-title">全校学员身材量体档案总览</h3>
        </div>
        <button @click="exportSheet" class="btn-primary mini-btn"><Download :size="12" /> 批量导出演出服订购单(CSV)</button>
      </div>

      <div v-for="item in allMetrics" :key="item.metricId" class="dance-card student-card">
        <div class="s-head">
          <span class="s-name"><User :size="14" /> {{ item.studentName }} (ID: {{ item.studentId }})</span>
          <span class="tag-badge tag-rose">舞鞋: {{ item.shoeSize }}码</span>
        </div>
        <div class="metric-grid">
          <div class="m-item"><span class="m-label">身高</span><span class="m-val">{{ item.heightCm }} cm</span></div>
          <div class="m-item"><span class="m-label">体重</span><span class="m-val">{{ item.weightKg }} kg</span></div>
          <div class="m-item"><span class="m-label">胸围</span><span class="m-val">{{ item.bustCm }} cm</span></div>
          <div class="m-item"><span class="m-label">腰围</span><span class="m-val">{{ item.waistCm }} cm</span></div>
          <div class="m-item"><span class="m-label">臀围</span><span class="m-val">{{ item.hipCm }} cm</span></div>
          <div class="m-item"><span class="m-label">胴长</span><span class="m-val">{{ item.torsoLengthCm }} cm</span></div>
        </div>
        <div class="m-date">更新日期：{{ item.measuredDate }}</div>
      </div>
    </div>

    <!-- 普通学员视角 -->
    <div v-else>
      <div class="dance-card flex-between">
        <div><span class="label">学员：</span><span class="name">{{ store.currentUser.name }}</span></div>
        <button @click="exportSheet" class="btn-outline">导出个人尺码(CSV)</button>
      </div>

      <div v-if="metric" class="dance-card">
        <h4 class="card-title">📏 我的最新身材档案 ({{ metric.measuredDate }})</h4>
        <div class="metric-grid">
          <div class="m-item"><span class="m-label">身高</span><span class="m-val">{{ metric.heightCm }} cm</span></div>
          <div class="m-item"><span class="m-label">体重</span><span class="m-val">{{ metric.weightKg }} kg</span></div>
          <div class="m-item"><span class="m-label">胸围</span><span class="m-val">{{ metric.bustCm }} cm</span></div>
          <div class="m-item"><span class="m-label">腰围</span><span class="m-val">{{ metric.waistCm }} cm</span></div>
          <div class="m-item"><span class="m-label">臀围</span><span class="m-val">{{ metric.hipCm }} cm</span></div>
          <div class="m-item"><span class="m-label">胴长</span><span class="m-val">{{ metric.torsoLengthCm }} cm</span></div>
          <div class="m-item highlight"><span class="m-label">舞鞋码</span><span class="m-val">{{ metric.shoeSize }} 码</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { User, Download } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const metric = ref(null)
const allMetrics = ref([])

const loadData = async () => {
  try {
    if (store.isTeacherOrAdmin) {
      const res = await api.getAllMetrics()
      if (res.code === 200) allMetrics.value = res.data
    } else {
      const res = await api.getStudentMetrics(store.currentUser.id || 6)
      if (res.code === 200) metric.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const exportSheet = () => {
  // 直接下载后端生成的 UTF-8 BOM CSV 文件
  window.open('http://localhost:8080/api/metric/export-csv', '_blank')
  store.showToast('演出服订购单下载中！', 'success')
}

onMounted(loadData)
watch(() => store.currentUser.id, loadData)
</script>

<style scoped>
.teacher-header {
  background: linear-gradient(135deg, #f3e8ff 0%, #ffffff 100%);
  border-left: 4px solid var(--primary-purple);
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;
}
.t-title { font-size: 15px; font-weight: 800; color: #7e22ce; margin-top: 4px; }
.student-card { margin-bottom: 10px; }
.s-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; border-bottom: 1px dashed var(--border-color); padding-bottom: 6px; }
.s-name { font-size: 14px; font-weight: 700; color: var(--text-main); display: flex; align-items: center; gap: 4px; }

.metric-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.m-item { background: #f8fafc; padding: 8px; border-radius: 8px; display: flex; flex-direction: column; align-items: center; }
.m-label { font-size: 11px; color: var(--text-muted); }
.m-val { font-size: 14px; font-weight: 700; color: var(--text-main); }
.m-date { font-size: 11px; color: #94a3b8; text-align: right; margin-top: 6px; }
.mini-btn { font-size: 11px; padding: 4px 10px; border-radius: 14px; display: flex; align-items: center; gap: 4px; }
</style>
