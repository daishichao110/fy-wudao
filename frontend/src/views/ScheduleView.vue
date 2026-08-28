<template>
  <div class="schedule-view">
    <div class="view-header">
      <h2>教务课表与着装指南</h2>
      <p class="subtitle">零审批机制：一键请假返还额度，一键核销预约补课</p>
    </div>

    <!-- 教师/管理员操作：发布排课 -->
    <div v-if="store.currentUser.role === 'TEACHER' || store.currentUser.role === 'SUPER_ADMIN'" class="dance-card publish-card">
      <div class="card-title">
        <PlusCircle :size="16" />
        <span>发布新排课与装备要求</span>
      </div>
      <form @submit.prevent="handleCreateSchedule" class="form-grid">
        <input v-model="newSchedule.courseName" placeholder="课程名称(如: 芭蕾技巧进阶)" class="form-input" required />
        <input v-model="newSchedule.danceType" placeholder="舞种(中国舞/芭蕾/拉丁)" class="form-input" required />
        <input v-model="newSchedule.classroomName" placeholder="教室名称(如: 1号芭蕾厅)" class="form-input" required />
        <input type="date" v-model="newSchedule.classDate" class="form-input" required />
        <input v-model="newSchedule.startTime" placeholder="开始 14:00" class="form-input" required />
        <input v-model="newSchedule.endTime" placeholder="结束 16:00" class="form-input" required />
        <input v-model="newSchedule.topsReq" placeholder="上身着装规范" class="form-input" required />
        <input v-model="newSchedule.bottomsReq" placeholder="下身着装规范" class="form-input" required />
        <input v-model="newSchedule.skirtReq" placeholder="裙子要求(如: 粉色一片短裙)" class="form-input" required />
        <input v-model="newSchedule.shoesReq" placeholder="鞋履规范" class="form-input" required />
        <input v-model="newSchedule.hairReq" placeholder="发型要求" class="form-input" required />
        <input v-model="newSchedule.propsReq" placeholder="携带教具" class="form-input" required />
        <button type="submit" class="btn-primary" style="grid-column: span 2;">发布课程规范</button>
      </form>
    </div>

    <!-- 排课卡片列表 -->
    <div v-for="item in scheduleList" :key="item.scheduleId" class="dance-card">
      <div class="card-header">
        <div class="course-title-group">
          <span class="tag-badge tag-purple">{{ item.danceType }}</span>
          <h3 class="title">{{ item.courseName }}</h3>
        </div>
        <span class="capacity-tag">已约 {{ item.bookedCount }}/{{ item.capacity }} 人</span>
      </div>

      <div class="meta-row">
        <span><Clock :size="13" /> {{ item.classDate }} {{ item.startTime }}-{{ item.endTime }}</span>
        <span><MapPin :size="13" /> {{ item.classroomName }}</span>
        <span><User :size="13" /> 任课老师: {{ item.teacherName }}</span>
      </div>

      <div class="dress-spec">
        <div class="spec-label"><Shirt :size="13" /> 装备指南:</div>
        <div class="spec-tags">
          <span class="tag-badge tag-rose">上身: {{ item.topsReq }}</span>
          <span class="tag-badge tag-purple">下身: {{ item.bottomsReq }}</span>
          <span class="tag-badge tag-amber">裙子: {{ item.skirtReq || '粉色雪纺一片裙' }}</span>
          <span class="tag-badge tag-blue">鞋履: {{ item.shoesReq }}</span>
          <span class="tag-badge tag-green">发型: {{ item.hairReq }}</span>
          <span class="tag-badge tag-purple">教具: {{ item.propsReq }}</span>
        </div>
      </div>

      <!-- 操作按钮 (极简零审批) -->
      <div class="action-row">
        <button @click="openLeaveModal(item)" class="btn-outline">
          <AlertCircle :size="14" /> 一键请假 (无需审批)
        </button>
        <button @click="openMakeupModal(item)" class="btn-primary">
          <CheckCircle :size="14" /> 预约补课 (立即核销)
        </button>
      </div>
    </div>

    <!-- 请假极简弹窗 -->
    <div v-if="leaveModal.show" class="modal-overlay">
      <div class="modal-content">
        <h3>一键请假 (无审批，直接生效)</h3>
        <p class="modal-tip">课程：{{ leaveModal.item?.courseName }} ({{ leaveModal.item?.classDate }})</p>
        <div class="form-group">
          <label class="form-label">请假原因 (选填)</label>
          <input v-model="leaveModal.reason" placeholder="如: 事假/病假" class="form-input" />
        </div>
        <div class="modal-btns">
          <button @click="leaveModal.show = false" class="btn-outline">取消</button>
          <button @click="submitLeave" class="btn-primary">确定请假并返还额度</button>
        </div>
      </div>
    </div>

    <!-- 补课极简弹窗 -->
    <div v-if="makeupModal.show" class="modal-overlay">
      <div class="modal-content">
        <h3>预约补课 (无需审批，立即核销)</h3>
        <p class="modal-tip">课程：{{ makeupModal.item?.courseName }} ({{ makeupModal.item?.classDate }})</p>
        <p class="modal-tip" style="color: var(--accent-green);">当前学员：{{ store.currentUser.name }}（可直接核销 1 次补课额度）</p>
        <div class="modal-btns">
          <button @click="makeupModal.show = false" class="btn-outline">取消</button>
          <button @click="submitMakeup" class="btn-success">立即核销并生成凭证</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { PlusCircle, Clock, MapPin, User, Shirt, AlertCircle, CheckCircle } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const scheduleList = ref([])

const newSchedule = reactive({
  courseName: '',
  danceType: '中国舞',
  classroomName: '1号排练厅',
  classDate: new Date().toISOString().split('T')[0],
  startTime: '14:00',
  endTime: '16:00',
  topsReq: '紧身练功服',
  bottomsReq: '连裤袜',
  skirtReq: '粉色雪纺一片短裙',
  shoesReq: '软底舞蹈鞋',
  hairReq: '盘头丸子头',
  propsReq: '瑜伽垫'
})

const leaveModal = reactive({ show: false, item: null, reason: '个人请假' })
const makeupModal = reactive({ show: false, item: null })

const loadSchedules = async () => {
  try {
    const res = await api.getSchedules()
    if (res.data) scheduleList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleCreateSchedule = async () => {
  try {
    const payload = {
      ...newSchedule,
      teacherId: store.currentUser.id,
      teacherName: store.currentUser.name
    }
    const res = await api.createSchedule(payload)
    if (res.code === 200) {
      store.showToast('新课程及装备规范发布成功！')
      loadSchedules()
    }
  } catch (e) {
    store.showToast('发布失败', 'error')
  }
}

const openLeaveModal = (item) => {
  leaveModal.item = item
  leaveModal.show = true
}

const submitLeave = async () => {
  try {
    const res = await api.applyLeave({
      studentId: store.currentUser.id,
      studentName: store.currentUser.name,
      scheduleId: leaveModal.item.scheduleId,
      courseName: leaveModal.item.courseName,
      reason: leaveModal.reason
    })
    if (res.code === 200) {
      store.showToast('一键请假成功！额度已退回。', 'success')
      leaveModal.show = false
      loadSchedules()
    }
  } catch (e) {
    store.showToast('请假失败', 'error')
  }
}

const openMakeupModal = (item) => {
  makeupModal.item = item
  makeupModal.show = true
}

const submitMakeup = async () => {
  try {
    const res = await api.applyMakeup({
      studentId: store.currentUser.id,
      studentName: store.currentUser.name,
      scheduleId: makeupModal.item.scheduleId,
      courseName: makeupModal.item.courseName
    })
    if (res.code === 200) {
      store.showToast('预约补课成功！凭证已核销生成。', 'success')
      makeupModal.show = false
      loadSchedules()
    }
  } catch (e) {
    store.showToast('补课预约失败', 'error')
  }
}

onMounted(() => {
  loadSchedules()
})
</script>

<style scoped>
.view-header {
  margin-bottom: 16px;
}
.view-header h2 { font-size: 18px; font-weight: 700; color: var(--text-main); }
.subtitle { font-size: 12px; color: var(--text-muted); }

.publish-card { background: #faf5ff; border: 1px solid #e9d5ff; }
.card-title { display: flex; align-items: center; gap: 6px; font-weight: 700; color: var(--primary-purple); margin-bottom: 10px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.course-title-group { display: flex; align-items: center; gap: 8px; }
.title { font-size: 16px; font-weight: 700; color: var(--text-main); }
.capacity-tag { font-size: 12px; color: var(--text-muted); font-weight: 500; }

.meta-row { display: flex; flex-wrap: wrap; gap: 12px; font-size: 12px; color: var(--text-muted); margin-bottom: 10px; }
.meta-row span { display: flex; align-items: center; gap: 4px; }

.dress-spec { background: var(--bg-app); border-radius: var(--radius-sm); padding: 8px 12px; margin-bottom: 12px; }
.spec-label { font-size: 12px; font-weight: 600; color: var(--primary-rose); margin-bottom: 4px; display: flex; align-items: center; gap: 4px; }
.spec-tags { display: flex; flex-wrap: wrap; gap: 4px; }

.action-row { display: flex; justify-content: flex-end; gap: 8px; }

.modal-tip { font-size: 13px; color: var(--text-muted); margin-bottom: 14px; }
.modal-btns { display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px; }
</style>
