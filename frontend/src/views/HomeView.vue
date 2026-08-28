<template>
  <div class="home-view">
    <!-- 1. 动态艺术顶部海报 (可点击跳转超链接、教室风采首位) -->
    <div class="banner-section">
      <div class="banner-card slide-1" @click="handleBannerClick(bannerList[0])">
        <div class="slide-badge-row">
          <span class="slide-badge">{{ bannerList[0].badge }}</span>
          <span class="click-hint">点击查看详情 ➔</span>
        </div>
        <h2 class="slide-title">{{ bannerList[0].title }}</h2>
        <p class="slide-sub">{{ bannerList[0].subTitle }}</p>
      </div>

      <!-- 仅老师/管理员可见的【上传发布 Banner】快捷按钮 -->
      <div v-if="store.isTeacherOrAdmin" class="banner-admin-bar">
        <button @click="showBannerUploadModal = true" class="upload-banner-btn">
          📸 老师/管理员发布 Banner 轮播海报
        </button>
      </div>
    </div>

    <!-- 2. 个性化身份与积分卡片 (User Hero Card) -->
    <div class="user-hero-card">
      <div class="hero-left">
        <div class="avatar-circle">💃</div>
        <div class="user-meta">
          <div class="name-row">
            <span class="u-name">{{ store.currentUser.name || '学员/家长' }}</span>
            <span class="role-pill">{{ store.currentUser.roleName || '学员' }}</span>
          </div>
          <span class="class-sub">所属班级: {{ store.currentUser.danceClass || '芭蕾高级班' }}</span>
        </div>
      </div>
      <router-link to="/login" class="switch-role-btn">切换角色 ➔</router-link>
    </div>

    <!-- 3. 实时播报走马灯 (Notice Ticker) -->
    <div class="notice-ticker">
      <Volume2 :size="16" class="ticker-icon" />
      <div class="ticker-content">
        <span class="ticker-text">【家委广播】六一演出服装与道具集采已公示，请各位家长及时查验进度！</span>
      </div>
    </div>

    <!-- 4. 金刚区 8 宫格导航矩阵 (8-Grid Matrix) -->
    <div class="matrix-title-row">
      <h3 class="section-title">✨ 数字化教务服务中心</h3>
      <span class="section-more">全流程通达 ➔</span>
    </div>

    <div class="grid-matrix">
      <router-link to="/schedule" class="matrix-item bg-purple">
        <div class="item-icon-box"><Calendar :size="20" /></div>
        <span class="item-name">教务排课</span>
        <span class="item-desc">课表与指南</span>
      </router-link>

      <router-link to="/metrics" class="matrix-item bg-pink">
        <div class="item-icon-box"><Ruler :size="20" /></div>
        <span class="item-name">量体档案</span>
        <span class="item-desc">10维数据表</span>
      </router-link>

      <router-link to="/volunteers" class="matrix-item bg-amber">
        <div class="item-icon-box"><HeartHandshake :size="20" /></div>
        <span class="item-name">家委协同</span>
        <span class="item-desc">志愿认领</span>
      </router-link>

      <router-link to="/profile" class="matrix-item bg-blue">
        <div class="item-icon-box"><MessageSquare :size="20" /></div>
        <span class="item-name">私信答疑</span>
        <span class="item-desc">导师1对1</span>
      </router-link>

      <router-link to="/schedule" class="matrix-item bg-rose">
        <div class="item-icon-box"><Zap :size="20" /></div>
        <span class="item-name">一键请假</span>
        <span class="item-desc">零审批退额</span>
      </router-link>

      <router-link to="/schedule" class="matrix-item bg-emerald">
        <div class="item-icon-box"><Ticket :size="20" /></div>
        <span class="item-name">预约补课</span>
        <span class="item-desc">即时核销</span>
      </router-link>

      <router-link to="/metrics" class="matrix-item bg-indigo">
        <div class="item-icon-box"><FileSpreadsheet :size="20" /></div>
        <span class="item-name">演出服导出</span>
        <span class="item-desc">Excel CSV</span>
      </router-link>

      <router-link to="/profile" class="matrix-item bg-cyan">
        <div class="item-icon-box"><BookOpen :size="20" /></div>
        <span class="item-name">知识宝箱</span>
        <span class="item-desc">精选解答</span>
      </router-link>
    </div>

    <!-- 5. 点击 Banner 触发的【教室风采详情】Modal 弹窗 -->
    <div v-if="showClassroomModal" class="modal-mask">
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">🏆 舞蹈学校专业教室与设施风采</h3>
          <span @click="showClassroomModal = false" class="close-btn">✖</span>
        </div>

        <div class="modal-scroll-body">
          <div class="hall-card">
            <h4 class="hall-title">📍 1号芭蕾专业剧目排练厅 (200㎡)</h4>
            <div class="hall-specs">
              <span class="spec-tag">德国进口双皮层防滑地胶</span>
              <span class="spec-tag">离地85-110cm双层松木把杆</span>
              <span class="spec-tag">全侧墙高透双层落地镜</span>
              <span class="spec-tag">珠江立式大钢琴现场伴奏</span>
            </div>
            <p class="hall-desc">专为芭蕾舞剧目变奏、足尖课及考级设计，具备弹力减震龙骨，有效保护学员踝关节与膝关节。</p>
          </div>

          <div class="hall-card">
            <h4 class="hall-title">📍 2号中国舞身韵多功能厅 (160㎡)</h4>
            <div class="hall-specs">
              <span class="spec-tag">高密度 EVA 防摔软垫</span>
              <span class="spec-tag">独立气压调节把杆</span>
              <span class="spec-tag">专业声学吸音顶棚</span>
              <span class="spec-tag">新风恒温空气净化系统</span>
            </div>
            <p class="hall-desc">适用于中国舞软开度训练、身韵技巧与剧目排练，配备高品质瑜伽垫、把杆砖与弹力带设备。</p>
          </div>

          <div class="hall-card">
            <h4 class="hall-title">📍 3号少儿拉丁/现代舞镜面厅 (120㎡)</h4>
            <div class="hall-specs">
              <span class="spec-tag">环绕立体声音响系统</span>
              <span class="spec-tag">HD高清跟拍机位</span>
              <span class="spec-tag">家委观摩等候区</span>
            </div>
            <p class="hall-desc">专为少儿拉丁舞节奏训练与舞步练习设计，支持多角度跟拍与家委观摩。</p>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="showClassroomModal = false" class="btn-primary">关闭风采展示</button>
        </div>
      </div>
    </div>

    <!-- 6. 老师/管理员发布 Banner 弹窗 -->
    <div v-if="showBannerUploadModal" class="modal-mask">
      <div class="modal-box">
        <div class="modal-header">
          <h3 class="modal-title">📸 老师/管理员发布 Banner 轮播海报</h3>
          <span @click="showBannerUploadModal = false" class="close-btn">✖</span>
        </div>

        <div class="modal-scroll-body">
          <div class="form-group-card">
            <div class="form-item">
              <label class="form-label">海报标题</label>
              <input v-model="bannerForm.title" class="form-input" placeholder="请输入海报标题" />
            </div>
            <div class="form-item">
              <label class="form-label">副标题</label>
              <input v-model="bannerForm.subTitle" class="form-input" placeholder="请输入副标题" />
            </div>
            <div class="form-item">
              <label class="form-label">徽章标签</label>
              <input v-model="bannerForm.badge" class="form-input" placeholder="如: 🌟 顶级设施" />
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="showBannerUploadModal = false" class="btn-outline">取消</button>
          <button @click="submitBanner" class="btn-primary">✨ 立即发布海报</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar, Ruler, HeartHandshake, MessageSquare, Zap, Ticket,
  FileSpreadsheet, BookOpen, Volume2
} from 'lucide-vue-next'
import { useAppStore } from '../stores/appStore'

const router = useRouter()
const store = useAppStore()
const showClassroomModal = ref(false)
const showBannerUploadModal = ref(false)

const bannerForm = ref({
  title: '🌟 1号专业芭蕾与国舞排练厅风采展示',
  subTitle: '点击查看 200㎡ 双地胶、立式大钢琴与落地镜排练厅设施',
  badge: '🏆 顶级设施环境'
})

const bannerList = ref([
  {
    id: 1,
    title: '🌟 1号专业芭蕾与国舞排练厅风采展示',
    subTitle: '点击查看 200㎡ 双地胶、大钢琴与落地镜排练厅设施 ➔',
    badge: '🏆 顶级设施',
    targetType: 'SHOWCASE'
  },
  {
    id: 2,
    title: '👑 六一少儿舞剧展演排练中',
    subTitle: '点击查看教务课表与各班级排练安排 ➔',
    badge: '👑 2026盛典',
    targetType: 'SCHEDULE'
  }
])

const handleBannerClick = (banner) => {
  if (!banner) return
  if (banner.targetType === 'SHOWCASE') {
    showClassroomModal.value = true
  } else if (banner.targetType === 'SCHEDULE') {
    router.push('/schedule')
  } else {
    showClassroomModal.value = true
  }
}

const submitBanner = () => {
  if (!bannerForm.value.title) return
  bannerList.value.unshift({
    id: Date.now(),
    title: bannerForm.value.title,
    subTitle: bannerForm.value.subTitle || '点击查看详细规范 ➔',
    badge: bannerForm.value.badge || '🌟 动态风采',
    targetType: 'SHOWCASE'
  })
  showBannerUploadModal.value = false
  store.showToast('Banner 海报发布成功！', 'success')
}
</script>

<style scoped>
.banner-section { margin-bottom: 14px; }
.banner-card {
  border-radius: 16px; padding: 20px; color: #ffffff;
  box-shadow: 0 8px 20px rgba(126, 34, 206, 0.15); cursor: pointer;
}
.slide-1 {
  background: linear-gradient(135deg, #6b21a8 0%, #a21caf 50%, #be123c 100%);
}
.slide-badge-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.slide-badge {
  font-size: 11px; font-weight: 700; background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4px); padding: 3px 10px; border-radius: 12px;
}
.click-hint { font-size: 10px; font-weight: 700; color: #fef08a; background: rgba(0, 0, 0, 0.2); padding: 2px 8px; border-radius: 10px; }

.slide-title { font-size: 20px; font-weight: 800; margin-bottom: 4px; text-align: left; }
.slide-sub { font-size: 12px; opacity: 0.95; text-align: left; }

.banner-admin-bar { margin-top: 6px; display: flex; justify-content: flex-end; }
.upload-banner-btn {
  font-size: 11px; font-weight: 700; background: #faf5ff; color: #7e22ce;
  border: 1px dashed #c084fc; border-radius: 14px; padding: 4px 12px; cursor: pointer;
}

.user-hero-card {
  background: #ffffff; border-radius: 14px; padding: 12px 16px;
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 12px; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}
.hero-left { display: flex; align-items: center; gap: 12px; }
.avatar-circle {
  width: 42px; height: 42px; border-radius: 50%;
  background: linear-gradient(135deg, #f3e8ff 0%, #fce7f3 100%);
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; border: 1px solid #e9d5ff;
}
.user-meta { display: flex; flex-direction: column; gap: 2px; text-align: left; }
.name-row { display: flex; align-items: center; gap: 8px; }
.u-name { font-size: 15px; font-weight: 800; color: #0f172a; }
.role-pill { font-size: 10px; font-weight: 700; background: #f3e8ff; color: #7e22ce; padding: 2px 8px; border-radius: 10px; }
.class-sub { font-size: 11px; color: #64748b; }
.switch-role-btn { font-size: 11px; color: #8b5cf6; font-weight: 700; background: #faf5ff; padding: 6px 12px; border-radius: 16px; border: 1px solid #e9d5ff; text-decoration: none; }

.notice-ticker {
  background: #faf5ff; border: 1px solid #f3e8ff; border-radius: 10px;
  padding: 8px 12px; display: flex; align-items: center; gap: 8px; margin-bottom: 14px;
}
.ticker-icon { color: #7e22ce; }
.ticker-content { flex: 1; overflow: hidden; white-space: nowrap; text-align: left; }
.ticker-text { font-size: 12px; color: #6b21a8; font-weight: 600; }

.matrix-title-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.section-title { font-size: 15px; font-weight: 800; color: #1e293b; }
.section-more { font-size: 11px; color: #94a3b8; }

.grid-matrix {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-bottom: 16px;
}
.matrix-item {
  background: #ffffff; border-radius: 12px; padding: 10px 4px;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03); border: 1px solid #f1f5f9;
  text-decoration: none; transition: transform 0.2s ease;
}
.matrix-item:hover { transform: translateY(-2px); }
.item-icon-box {
  width: 38px; height: 38px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center; margin-bottom: 6px;
}

.bg-purple .item-icon-box { background: #f3e8ff; color: #7e22ce; }
.bg-pink .item-icon-box { background: #fce7f3; color: #be123c; }
.bg-amber .item-icon-box { background: #fef3c7; color: #d97706; }
.bg-blue .item-icon-box { background: #e0f2fe; color: #0284c7; }
.bg-rose .item-icon-box { background: #ffe4e6; color: #e11d48; }
.bg-emerald .item-icon-box { background: #d1fae5; color: #059669; }
.bg-indigo .item-icon-box { background: #e0e7ff; color: #4338ca; }
.bg-cyan .item-icon-box { background: #cffafe; color: #0891b2; }

.item-name { font-size: 12px; font-weight: 700; color: #1e293b; margin-bottom: 2px; }
.item-desc { font-size: 9px; color: #94a3b8; }

.hall-card {
  background: #ffffff; border-radius: 12px; padding: 14px; margin-bottom: 12px;
  border: 1px solid #e2e8f0; text-align: left;
}
.hall-title { font-size: 14px; font-weight: 800; color: #7e22ce; margin-bottom: 8px; }
.hall-specs { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.spec-tag { font-size: 10px; font-weight: 700; background: #faf5ff; color: #8b5cf6; padding: 2px 8px; border-radius: 6px; border: 1px solid #f3e8ff; }
.hall-desc { font-size: 12px; color: #475569; line-height: 1.5; }

.modal-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.65); z-index: 999;
  display: flex; align-items: center; justify-content: center; padding: 16px;
}
.modal-box {
  background: #ffffff; border-radius: 18px; width: 100%; max-width: 440px; max-height: 88vh;
  display: flex; flex-direction: column; overflow: hidden; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
.modal-header {
  padding: 16px 20px; border-bottom: 1px solid #f1f5f9; background: #faf5ff;
  display: flex; justify-content: space-between; align-items: center;
}
.modal-title { font-size: 16px; font-weight: 800; color: #7e22ce; line-height: 1.2; }
.close-btn { cursor: pointer; color: #94a3b8; font-size: 16px; }

.modal-scroll-body { padding: 16px; max-height: 62vh; overflow-y: auto; background: #f8fafc; }
.form-group-card { background: #ffffff; border-radius: 12px; padding: 14px; margin-bottom: 14px; border: 1px solid #e2e8f0; }

.form-item { margin-bottom: 12px; display: flex; flex-direction: column; gap: 6px; }
.form-label { font-size: 12px; font-weight: 700; color: #334155; text-align: left; line-height: 1.4; }

.form-input {
  background: #ffffff; border: 1px solid #cbd5e1; border-radius: 8px;
  padding: 0 12px; font-size: 13px; color: #0f172a; height: 42px;
  min-height: 42px; line-height: 42px; box-sizing: border-box; outline: none;
  display: flex; align-items: center;
}

.modal-footer { padding: 12px 16px; background: #ffffff; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; }
</style>
