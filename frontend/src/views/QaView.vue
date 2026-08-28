<template>
  <div class="qa-view">
    <!-- Tab 切换 -->
    <div class="tab-header">
      <button
        @click="activeTab = 'my'"
        :class="['tab-btn', activeTab === 'my' ? 'active' : '']"
      >
        <MessageCircle :size="15" /> 师生1对1私信提问
      </button>
      <button
        @click="activeTab = 'featured'"
        :class="['tab-btn', activeTab === 'featured' ? 'active' : '']"
      >
        <Sparkles :size="15" /> 舞蹈知识百宝箱 (精选)
      </button>
    </div>

    <!-- Tab 1: 1对1私信 -->
    <div v-if="activeTab === 'my'">
      <!-- 学员提问框 -->
      <div v-if="store.currentUser.role === 'STUDENT'" class="dance-card ask-box">
        <h4 class="ask-title"><Send :size="15" /> 向任课老师发起专业提问</h4>
        <div class="form-group">
          <textarea
            v-model="newQuestion"
            rows="3"
            placeholder="请输入您在基训、剧目排练、拉伸肌肉或动作姿态方面的疑问..."
            class="form-textarea"
          ></textarea>
        </div>
        <button @click="handleAsk" class="btn-primary" style="float: right;">发送私信给老师</button>
        <div style="clear: both;"></div>
      </div>

      <!-- 消息列表 -->
      <div v-for="msg in myMessages" :key="msg.msgId" class="dance-card msg-card">
        <div class="msg-meta">
          <span class="student-name">{{ msg.studentName }} 提问给 {{ msg.teacherName }}</span>
          <span class="msg-date">{{ msg.createdAt }}</span>
        </div>
        <div class="q-content">
          <strong>Q:</strong> {{ msg.questionContent }}
        </div>

        <div v-if="msg.replyContent" class="a-content">
          <strong>A ({{ msg.teacherName }}):</strong> {{ msg.replyContent }}
        </div>
        <div v-else class="pending-reply">
          <Clock :size="13" /> 老师正在思考回复中...
        </div>

        <!-- 老师端专属：一键设为精选公开 -->
        <div v-if="store.currentUser.role === 'TEACHER' || store.currentUser.role === 'SUPER_ADMIN'" class="teacher-actions">
          <div v-if="!msg.replyContent" class="reply-form">
            <input v-model="replyTextMap[msg.msgId]" placeholder="输入您的专业建议与纠错回复..." class="form-input" />
            <button @click="handleReply(msg.msgId)" class="btn-primary">回复</button>
          </div>
          <button
            v-if="msg.replyContent && !msg.isFeatured"
            @click="openFeatureModal(msg)"
            class="btn-outline"
            style="margin-top: 8px; font-size: 12px;"
          >
            <Star :size="13" /> ★ 设为精选公开 (沉淀至百宝箱)
          </button>
          <span v-if="msg.isFeatured" class="tag-badge tag-green" style="margin-top: 8px;">
            ✓ 已精选入库：{{ msg.featuredTitle }}
          </span>
        </div>
      </div>
    </div>

    <!-- Tab 2: 舞蹈知识精选库 -->
    <div v-if="activeTab === 'featured'">
      <div class="search-bar">
        <input v-model="searchKeyword" placeholder="搜索动作要领、肌肉拉伸、擦地、跳跃..." class="form-input" />
      </div>

      <div v-for="item in filteredFeatured" :key="item.msgId" class="dance-card">
        <div class="featured-badge-row">
          <span class="tag-badge tag-rose"><BookOpen :size="12" /> 官方解答</span>
          <span class="teacher-name">解答导师: {{ item.teacherName }}</span>
        </div>
        <h3 class="f-title">{{ item.featuredTitle || item.questionContent }}</h3>
        <p class="f-q"><strong>问：</strong>{{ item.questionContent }}</p>
        <div class="f-a"><strong>答：</strong>{{ item.replyContent }}</div>
      </div>
    </div>

    <!-- 精选弹窗 -->
    <div v-if="featureModal.show" class="modal-overlay">
      <div class="modal-content">
        <h3>设为精选问答 (脱敏沉淀至知识库)</h3>
        <div class="form-group" style="margin-top: 12px;">
          <label class="form-label">精选展示标题</label>
          <input v-model="featureModal.title" placeholder="如: 【芭蕾基训】擦地如何规避脚趾扣地" class="form-input" />
        </div>
        <div class="modal-btns">
          <button @click="featureModal.show = false" class="btn-outline">取消</button>
          <button @click="submitFeature" class="btn-primary">公开至百宝箱</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { MessageCircle, Sparkles, Send, Clock, Star, BookOpen } from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const activeTab = ref('my')
const myMessages = ref([])
const featuredList = ref([])
const newQuestion = ref('')
const replyTextMap = reactive({})
const searchKeyword = ref('')

const featureModal = reactive({ show: false, msgId: null, title: '' })

const loadMyMessages = async () => {
  try {
    const res = await api.getMyMessages(store.currentUser.id)
    if (res.data) myMessages.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadFeatured = async () => {
  try {
    const res = await api.getFeaturedQA()
    if (res.data) featuredList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleAsk = async () => {
  if (!newQuestion.value.trim()) return
  try {
    const payload = {
      studentId: store.currentUser.id,
      studentName: store.currentUser.name,
      teacherId: 2, // 林依依老师
      teacherName: '林依依老师',
      questionContent: newQuestion.value
    }
    const res = await api.askQuestion(payload)
    if (res.code === 200) {
      store.showToast('提问成功发送给老师！')
      newQuestion.value = ''
      loadMyMessages()
    }
  } catch (e) {
    store.showToast('发送失败', 'error')
  }
}

const handleReply = async (msgId) => {
  const replyContent = replyTextMap[msgId]
  if (!replyContent) return
  try {
    const res = await api.replyQuestion({ msgId, replyContent })
    if (res.code === 200) {
      store.showToast('回复成功！')
      loadMyMessages()
    }
  } catch (e) {
    store.showToast('回复失败', 'error')
  }
}

const openFeatureModal = (msg) => {
  featureModal.msgId = msg.msgId
  featureModal.title = `【专业解析】${msg.questionContent.slice(0, 15)}...`
  featureModal.show = true
}

const submitFeature = async () => {
  try {
    const res = await api.featureQuestion({
      msgId: featureModal.msgId,
      featuredTitle: featureModal.title
    })
    if (res.code === 200) {
      store.showToast('已精选公开至舞蹈知识百宝箱！')
      featureModal.show = false
      loadMyMessages()
      loadFeatured()
    }
  } catch (e) {
    store.showToast('设置失败', 'error')
  }
}

const filteredFeatured = computed(() => {
  if (!searchKeyword.value) return featuredList.value
  return featuredList.value.filter(item =>
    (item.featuredTitle && item.featuredTitle.includes(searchKeyword.value)) ||
    (item.questionContent && item.questionContent.includes(searchKeyword.value)) ||
    (item.replyContent && item.replyContent.includes(searchKeyword.value))
  )
})

onMounted(() => {
  loadMyMessages()
  loadFeatured()
})
</script>

<style scoped>
.tab-header { display: flex; gap: 8px; margin-bottom: 14px; }
.tab-btn {
  flex: 1; padding: 10px; border: 1px solid var(--border-color); border-radius: var(--radius-sm);
  background: #fff; font-weight: 600; font-size: 13px; color: var(--text-muted); cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.tab-btn.active { background: var(--primary-gradient); color: #fff; border: none; }

.ask-box { background: #faf5ff; border: 1px solid #e9d5ff; }
.ask-title { font-size: 14px; font-weight: 700; color: var(--primary-purple); margin-bottom: 8px; display: flex; align-items: center; gap: 6px; }

.msg-card { border-left: 3px solid var(--primary-purple); }
.msg-meta { display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted); margin-bottom: 6px; }
.student-name { font-weight: 700; color: var(--text-main); }
.q-content { font-size: 14px; color: var(--text-main); margin-bottom: 8px; }
.a-content { background: var(--bg-app); padding: 8px 12px; border-radius: var(--radius-sm); font-size: 13px; color: #334155; margin-bottom: 6px; }
.pending-reply { font-size: 12px; color: var(--accent-gold); display: flex; align-items: center; gap: 4px; }

.reply-form { display: flex; gap: 6px; margin-top: 8px; }

.featured-badge-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.teacher-name { font-size: 12px; color: var(--text-muted); font-weight: 600; }
.f-title { font-size: 15px; font-weight: 700; color: var(--primary-purple); margin-bottom: 6px; }
.f-q { font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
.f-a { background: #f8fafc; padding: 10px; border-radius: var(--radius-sm); font-size: 13px; color: #1e293b; border-left: 3px solid var(--accent-green); }

.search-bar { margin-bottom: 12px; }
.modal-btns { display: flex; justify-content: flex-end; gap: 8px; margin-top: 14px; }
</style>
