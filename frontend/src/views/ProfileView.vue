<template>
  <div class="profile-view">
    <!-- 1. 微信原生极简头部卡片 -->
    <div class="profile-header-card">
      <div class="user-main-row">
        <div class="u-avatar">💃</div>
        <div class="u-info-col">
          <div class="u-name-line">
            <span class="u-name">{{ store.currentUser.name || '学员/家长' }}</span>
            <span class="role-badge">{{ store.currentUser.roleName || '学员' }}</span>
          </div>
          <span class="u-sub-text">班级: {{ store.currentUser.danceClass || '芭蕾高级班' }}</span>
        </div>
      </div>
    </div>

    <!-- 2. 👑 管理员教务指挥中心 -->
    <div v-if="isAdmin" class="section-header-row">
      <span class="menu-group-title">👑 管理员教务指挥中心</span>
      <span class="group-tip">超级管理员端</span>
    </div>
    <div v-if="isAdmin" class="action-tools-grid">
      <div @click="showApprovalModal = true" class="action-item">
        <div class="action-icon-box purple-bg">
          <span class="a-icon">👑</span>
          <span v-if="pendingList.length > 0" class="badge-dot">{{ pendingList.length }}</span>
        </div>
        <span class="action-label">角色审批</span>
      </div>

      <div @click="showScheduleModal = true" class="action-item">
        <div class="action-icon-box blue-bg">
          <span class="a-icon">📅</span>
        </div>
        <span class="action-label">发布排课</span>
      </div>

      <div @click="showMentorshipModal = true" class="action-item">
        <div class="action-icon-box amber-bg">
          <span class="a-icon">🌟</span>
        </div>
        <span class="action-label">设置结对</span>
      </div>

      <div @click="showMetricModal = true" class="action-item">
        <div class="action-icon-box green-bg">
          <span class="a-icon">📏</span>
        </div>
        <span class="action-label">录入量体</span>
      </div>

      <div @click="downloadStudentInfo" class="action-item">
        <div class="action-icon-box indigo-bg">
          <span class="a-icon">📥</span>
        </div>
        <span class="action-label">学员信息下载</span>
      </div>

      <div @click="downloadItemDemand" class="action-item">
        <div class="action-icon-box sky-bg">
          <span class="a-icon">📦</span>
        </div>
        <span class="action-label">物品需求下载</span>
      </div>
    </div>

    <!-- 3. 👩‍🏫 教师端教务工作台 (已严格去掉角色审批、录入量体；仅包含发布排课、设置结对、学员信息下载、物品需求下载) -->
    <div v-if="isTeacher && !isAdmin" class="section-header-row">
      <span class="menu-group-title">🛠️ 教师教务管理工作台</span>
      <span class="group-tip">专业教师端 (精简专业项)</span>
    </div>
    <div v-if="isTeacher && !isAdmin" class="action-tools-grid">
      <div @click="showScheduleModal = true" class="action-item">
        <div class="action-icon-box blue-bg">
          <span class="a-icon">📅</span>
        </div>
        <span class="action-label">发布排课</span>
      </div>

      <div @click="showMentorshipModal = true" class="action-item">
        <div class="action-icon-box amber-bg">
          <span class="a-icon">🌟</span>
        </div>
        <span class="action-label">设置结对</span>
      </div>

      <div @click="downloadStudentInfo" class="action-item">
        <div class="action-icon-box indigo-bg">
          <span class="a-icon">📥</span>
        </div>
        <span class="action-label">学员信息下载</span>
      </div>

      <div @click="downloadItemDemand" class="action-item">
        <div class="action-icon-box sky-bg">
          <span class="a-icon">📦</span>
        </div>
        <span class="action-label">物品需求下载</span>
      </div>
    </div>

    <!-- 4. 📜 家委会端工作台 (增加发布采购需求-支持手动添加表格行；增加物品需求下载) -->
    <div v-if="isCommittee && !isAdmin" class="section-header-row">
      <span class="menu-group-title">📜 家委会管理工作台</span>
      <span class="group-tip">家委干部端</span>
    </div>
    <div v-if="isCommittee && !isAdmin" class="action-tools-grid">
      <div @click="showDynamicPurchaseModal = true" class="action-item">
        <div class="action-icon-box fuchsia-bg">
          <span class="a-icon">📝</span>
        </div>
        <span class="action-label">发布采购需求</span>
      </div>

      <div @click="downloadItemDemand" class="action-item">
        <div class="action-icon-box sky-bg">
          <span class="a-icon">📦</span>
        </div>
        <span class="action-label">物品需求下载</span>
      </div>

      <router-link to="/purchases" class="action-item text-decoration-none">
        <div class="action-icon-box indigo-bg">
          <span class="a-icon">📜</span>
        </div>
        <span class="action-label">采购公示</span>
      </router-link>
    </div>

    <!-- 5. 📱 个人服务中心 -->
    <div class="section-header-row margin-top-12">
      <span class="menu-group-title">📱 个人服务中心</span>
      <span class="group-tip">{{ isTeacher && !isAdmin ? '教师专用' : '学员/家长专属' }}</span>
    </div>

    <div class="action-tools-grid">
      <!-- 教师端个人服务中心：已严格全部去掉其余项，只保留【有感而发】与【说说心里话】 -->
      <template v-if="isTeacher && !isAdmin">
        <div @click="openThoughtModal('THOUGHT')" class="action-item">
          <div class="action-icon-box violet-bg">
            <span class="a-icon">💭</span>
          </div>
          <span class="action-label">有感而发</span>
        </div>

        <div @click="openThoughtModal('HEART')" class="action-item">
          <div class="action-icon-box rose-bg">
            <span class="a-icon">💖</span>
          </div>
          <span class="action-label">说说心里话</span>
        </div>
      </template>

      <!-- 普通家长端/管理员/家委端个人服务中心 -->
      <template v-else>
        <div @click="showStudentProfileModal = true" class="action-item">
          <div class="action-icon-box violet-bg">
            <span class="a-icon">👤</span>
          </div>
          <span class="action-label">学员维护</span>
        </div>

        <div @click="showItemDemandModal = true" class="action-item">
          <div class="action-icon-box cyan-bg">
            <span class="a-icon">📦</span>
          </div>
          <span class="action-label">物品需求</span>
        </div>

        <div @click="openThoughtModal('THOUGHT')" class="action-item">
          <div class="action-icon-box pink-bg">
            <span class="a-icon">💭</span>
          </div>
          <span class="action-label">有感而发</span>
        </div>

        <div @click="openThoughtModal('HEART')" class="action-item">
          <div class="action-icon-box rose-bg">
            <span class="a-icon">💖</span>
          </div>
          <span class="action-label">说说心里话</span>
        </div>
      </template>
    </div>

    <!-- 弹窗 1：发布采购需求 (动态表格) Modal -->
    <div v-if="showDynamicPurchaseModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">📝 发布采购需求 (动态表格)</h3>
          <button @click="showDynamicPurchaseModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">采购主题/批次名称</label>
            <input v-model="purchaseTitle" class="form-input" placeholder="例如：2026年秋季演出服与头饰集采" />
          </div>

          <div class="section-title-row margin-top-12">
            <h4 class="s-title">📋 采购物品明细表格 ({{ dynamicRows.length }}行)</h4>
            <button @click="addDynamicRow" class="add-row-btn">＋ 添加表格行</button>
          </div>

          <div v-for="(row, idx) in dynamicRows" :key="idx" class="table-row-card">
            <div class="t-row-head">
              <span class="t-row-num">第 {{ idx + 1 }} 项物品</span>
              <button @click="removeDynamicRow(idx)" class="del-row-btn">删除行 ✕</button>
            </div>
            <div class="form-group margin-top-4">
              <label class="form-label">物品名称</label>
              <input v-model="row.name" class="form-input" placeholder="例如：芭蕾舞立裙 / 练习缎鞋" />
            </div>
            <div class="form-row-2 margin-top-4">
              <div class="form-group flex-1">
                <label class="form-label">预估单价(元)</label>
                <input v-model="row.price" type="number" class="form-input" placeholder="120.00" />
              </div>
              <div class="form-group flex-1">
                <label class="form-label">预算数量(件)</label>
                <input v-model="row.count" type="number" class="form-input" placeholder="25" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showDynamicPurchaseModal = false" class="btn-outline">取消</button>
          <button @click="submitDynamicPurchase" class="btn-primary">确认发布采购表格</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 2：查看物品需求 Modal -->
    <div v-if="showItemDemandModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">📦 查看与确认物品需求列表</h3>
          <button @click="showItemDemandModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div v-for="item in itemDemandList" :key="item.itemId" class="demand-item-card">
            <div class="d-item-main">
              <span class="d-name">{{ item.itemName }}</span>
              <span class="d-spec">规格: {{ item.spec }} | 参考单价: <strong class="d-price">{{ item.unitPrice }}</strong></span>
            </div>
            <div class="d-control-row">
              <div @click="toggleNeedIt(item.itemId)" class="need-switch-box">
                <span class="checkbox-icon">{{ item.needIt ? '☑' : '☐' }}</span>
                <span :class="['need-label', item.needIt ? 'text-green' : 'text-gray']">{{ item.needIt ? '确定需要' : '暂不需要' }}</span>
              </div>
              <div v-if="item.needIt" class="qty-counter-box">
                <button @click="changeQty(item.itemId, -1)" class="qty-btn">-</button>
                <span class="qty-num">{{ item.quantity }}</span>
                <button @click="changeQty(item.itemId, 1)" class="qty-btn">+</button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showItemDemandModal = false" class="btn-primary">保存并关闭</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 3：【有感而发】与【说说心里话】 Modal -->
    <div v-if="showThoughtModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">{{ activeThoughtType === 'THOUGHT' ? '💭 有感而发 · 舞蹈心路历程' : '💖 说说心里话 · 倾诉与反馈' }}</h3>
          <button @click="showThoughtModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">{{ activeThoughtType === 'THOUGHT' ? '发布随感与心得' : '向学校/导师倾诉心里话' }}</label>
            <textarea v-model="thoughtContent" class="form-textarea" :placeholder="activeThoughtType === 'THOUGHT' ? '分享舞蹈练习中的收获、喜悦与点滴成就...' : '写下您对教务管理、课程设置或孩子学习的心里话...'"></textarea>
            <button @click="submitThought" class="btn-primary submit-ask-btn">发布文字</button>
          </div>

          <div class="section-title-row margin-top-12">
            <h4 class="s-title">📖 共享心得与心里话 ({{ thoughtList.length }}条)</h4>
          </div>

          <div v-for="item in thoughtList" :key="item.id" class="msg-card">
            <div class="msg-head">
              <span :class="['tag-badge', item.type === 'THOUGHT' ? 'tag-purple' : 'tag-rose']">{{ item.title }}</span>
              <span class="msg-date">{{ item.createdAt }}</span>
            </div>
            <div class="q-row">
              <span class="q-label">[{{ item.studentName }}]:</span>
              <span class="q-text">{{ item.content }}</span>
            </div>
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showThoughtModal = false" class="btn-outline">关闭</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 -1：学员维护 Modal -->
    <div v-if="showStudentProfileModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">👤 学员基本信息与综合成绩维护</h3>
          <button @click="showStudentProfileModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">学员姓名</label>
            <input v-model="studentProfileForm.studentName" class="form-input" placeholder="例如：李小桐" />
          </div>

          <div class="form-group">
            <label class="form-label">年纪 / 年级</label>
            <input v-model="studentProfileForm.gradeLevel" class="form-input" placeholder="例如：小学三年级 / 9岁" />
          </div>

          <div class="form-row-3">
            <div class="form-group flex-1">
              <label class="form-label">语文成绩</label>
              <input v-model="studentProfileForm.chineseScore" type="number" class="form-input" placeholder="95.5" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">数学成绩</label>
              <input v-model="studentProfileForm.mathScore" type="number" class="form-input" placeholder="98.0" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">英语成绩</label>
              <input v-model="studentProfileForm.englishScore" type="number" class="form-input" placeholder="94.0" />
            </div>
          </div>

          <div class="form-row-2 margin-top-6">
            <div class="form-group flex-1">
              <label class="form-label">身高 (cm)</label>
              <input v-model="studentProfileForm.heightCm" type="number" class="form-input" placeholder="138.5" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">体重 (kg)</label>
              <input v-model="studentProfileForm.weightKg" type="number" class="form-input" placeholder="31.2" />
            </div>
          </div>

          <div class="form-group margin-top-6">
            <label class="form-label">家长姓名</label>
            <input v-model="studentProfileForm.parentName" class="form-input" placeholder="例如：李妈妈 / 李先生" />
          </div>

          <div class="form-group">
            <label class="form-label">家长联系方式 (手机号)</label>
            <input v-model="studentProfileForm.parentPhone" class="form-input" placeholder="例如：13900000006" />
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showStudentProfileModal = false" class="btn-outline">取消</button>
          <button @click="submitStudentProfile" class="btn-primary">保存学员信息</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 0：专业教室风采 Modal -->
    <div v-if="showHallModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">🏆 舞蹈学校专业教室与设施风采</h3>
          <button @click="showHallModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="hall-card">
            <h4 class="hall-title">📍 1号芭蕾专业剧目排练厅 (200㎡)</h4>
            <div class="hall-specs">
              <span class="spec-tag">德国进口双皮层防滑地胶</span>
              <span class="spec-tag">双层松木把杆</span>
              <span class="spec-tag">珠江大钢琴</span>
            </div>
            <p class="hall-desc">专为芭蕾舞剧目变奏、足尖课及考级设计，具备弹力减震龙骨，有效保护学员关节。</p>
          </div>
          <div class="hall-card">
            <h4 class="hall-title">📍 2号中国舞身韵多功能厅 (160㎡)</h4>
            <div class="hall-specs">
              <span class="spec-tag">高密度 EVA 防摔软垫</span>
              <span class="spec-tag">气压调节把杆</span>
              <span class="spec-tag">新风系统</span>
            </div>
            <p class="hall-desc">适用于中国舞软开度训练、身韵技巧与剧目排练，配备把杆砖与弹力带设备。</p>
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showHallModal = false" class="btn-outline">关闭窗口</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 2：角色资格审批 Modal (仅管理员) -->
    <div v-if="showApprovalModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">👑 管理员全量角色资格审批</h3>
          <button @click="showApprovalModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div v-if="pendingList.length > 0">
            <div v-for="item in pendingList" :key="item.userId" class="pending-user-item">
              <div class="p-user-info">
                <div class="p-name-row">
                  <span class="p-name">{{ item.realName }}</span>
                  <span :class="['tag-badge', item.roleType === 'TEACHER' ? 'tag-purple' : (item.roleType === 'COMMITTEE' ? 'tag-amber' : 'tag-rose')]">
                    {{ item.roleType === 'TEACHER' ? '申请专业老师' : (item.roleType === 'COMMITTEE' ? '申请家委干部' : '申请学员/家长') }}
                  </span>
                </div>
                <span v-if="item.studentName" class="p-sub">👨‍👩‍👧 学生: {{ item.studentName }} ({{ item.relationship }})</span>
                <span class="p-sub">📱 手机号: {{ item.phone }}</span>
              </div>
              <div class="p-actions">
                <button @click="handleApprove(item.userId, 2)" class="btn-outline mini-btn reject-btn">驳回</button>
                <button @click="handleApprove(item.userId, 1)" class="btn-primary mini-btn pass-btn">通过</button>
              </div>
            </div>
          </div>
          <div v-else class="empty-pending">暂无待处理的审核申请，所有账号处于正常状态。</div>
        </div>
        <div class="modal-foot">
          <button @click="showApprovalModal = false" class="btn-outline">关闭窗口</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 3：发布新课程与指南 Modal -->
    <div v-if="showScheduleModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">📅 发布新排课与着装指南</h3>
          <button @click="showScheduleModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">课程名称</label>
            <input v-model="scheduleForm.courseName" class="form-input" placeholder="例如：芭蕾舞基训与剧目排练" />
          </div>
          <div class="form-group">
            <label class="form-label">舞种类型</label>
            <input v-model="scheduleForm.danceType" class="form-input" placeholder="芭蕾舞/中国舞/拉丁舞" />
          </div>
          <div class="form-group">
            <label class="form-label">上课教室</label>
            <input v-model="scheduleForm.classroomName" class="form-input" placeholder="1号芭蕾专业排练厅" />
          </div>
          <div class="form-group">
            <label class="form-label">上课日期与时间</label>
            <input v-model="scheduleForm.classDate" class="form-input" placeholder="YYYY-MM-DD" />
            <input v-model="scheduleForm.timeRange" class="form-input margin-top-6" placeholder="14:00-16:00" />
          </div>
          <div class="form-group">
            <label class="form-label">👗 上身着装要求</label>
            <input v-model="scheduleForm.topsReq" class="form-input" placeholder="粉色吊带连体服(无蕾丝)" />
          </div>
          <div class="form-group">
            <label class="form-label">👖 下身着装要求</label>
            <input v-model="scheduleForm.bottomsReq" class="form-input" placeholder="白色九分芭蕾大袜" />
          </div>
          <div class="form-group">
            <label class="form-label">🌸 裙子要求</label>
            <input v-model="scheduleForm.skirtReq" class="form-input" placeholder="粉色雪纺一片绑带短裙" />
          </div>
          <div class="form-group">
            <label class="form-label">👟 鞋履与发型</label>
            <input v-model="scheduleForm.shoesReq" class="form-input" placeholder="双皮头粉色软底鞋" />
            <input v-model="scheduleForm.hairReq" class="form-input margin-top-6" placeholder="高盘头丸子头(配发网)" />
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showScheduleModal = false" class="btn-outline">取消</button>
          <button @click="submitSchedule" class="btn-primary">确认发布课程</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 4：设置高低年级结对子 Modal -->
    <div v-if="showMentorshipModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">🌟 设置高低年级结对子</h3>
          <button @click="showMentorshipModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">高年级学姐姓名</label>
            <input v-model="mentorshipForm.seniorStudentName" class="form-input" placeholder="例如：张悦悦(高年级学姐)" />
          </div>
          <div class="form-group">
            <label class="form-label">低年级学妹姓名</label>
            <input v-model="mentorshipForm.juniorStudentName" class="form-input" placeholder="例如：李小桐(新学员)" />
          </div>
          <div class="form-group">
            <label class="form-label">关联班级与初始姐妹星</label>
            <input v-model="mentorshipForm.termName" class="form-input" placeholder="芭蕾与中国舞联训班" />
            <input v-model="mentorshipForm.starPoints" type="number" class="form-input margin-top-6" placeholder="初始积分 (默认 50)" />
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showMentorshipModal = false" class="btn-outline">取消</button>
          <button @click="submitMentorship" class="btn-primary">确认绑定结对</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 5：录入学员身材量体 Modal -->
    <div v-if="showMetricModal" class="modal-overlay">
      <div class="modal-card">
        <div class="modal-head">
          <h3 class="m-title">📏 录入学员 10 维身材量体档案</h3>
          <button @click="showMetricModal = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">学员姓名与ID</label>
            <input v-model="metricForm.studentName" class="form-input" placeholder="例如：张悦悦(高年级学姐)" />
          </div>
          <div class="form-row-2">
            <div class="form-group flex-1">
              <label class="form-label">身高(cm)</label>
              <input v-model="metricForm.heightCm" type="number" class="form-input" placeholder="152.5" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">体重(kg)</label>
              <input v-model="metricForm.weightKg" type="number" class="form-input" placeholder="38.0" />
            </div>
          </div>
          <div class="form-row-2">
            <div class="form-group flex-1">
              <label class="form-label">胸围(cm)</label>
              <input v-model="metricForm.bustCm" type="number" class="form-input" placeholder="72.0" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">腰围(cm)</label>
              <input v-model="metricForm.waistCm" type="number" class="form-input" placeholder="58.0" />
            </div>
          </div>
          <div class="form-row-2">
            <div class="form-group flex-1">
              <label class="form-label">臀围(cm)</label>
              <input v-model="metricForm.hipCm" type="number" class="form-input" placeholder="76.0" />
            </div>
            <div class="form-group flex-1">
              <label class="form-label">舞鞋码数(码)</label>
              <input v-model="metricForm.shoeSize" type="number" class="form-input" placeholder="35.0" />
            </div>
          </div>
        </div>
        <div class="modal-foot">
          <button @click="showMetricModal = false" class="btn-outline">取消</button>
          <button @click="submitMetric" class="btn-primary">保存量体档案</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '../stores/appStore'
import api from '../api'

const store = useAppStore()
const pendingList = ref([])

// 弹窗控制
const showStudentProfileModal = ref(false)
const showApprovalModal = ref(false)
const showScheduleModal = ref(false)
const showMentorshipModal = ref(false)
const showMetricModal = ref(false)
const showHallModal = ref(false)
const showThoughtModal = ref(false)
const showItemDemandModal = ref(false)
const showDynamicPurchaseModal = ref(false)

// 有感而发与心里话
const activeThoughtType = ref('THOUGHT')
const thoughtContent = ref('')
const thoughtList = ref([])

// 物品需求
const itemDemandList = ref([])

// 动态采购明细表格
const purchaseTitle = ref('2026年秋季演出服与头饰集采')
const dynamicRows = ref([
  { name: '芭蕾舞短款立裙', price: '120.00', count: 25 },
  { name: '羽毛演出头饰配饰', price: '35.00', count: 25 }
])

// 学员维护表单数据
const studentProfileForm = ref({
  studentId: 6,
  studentName: '李小桐(新学员)',
  gradeLevel: '小学三年级',
  chineseScore: 95.5,
  mathScore: 98.0,
  englishScore: 94.0,
  heightCm: 138.5,
  weightKg: 31.2,
  parentName: '李妈妈',
  parentPhone: '13900000006'
})

// 表单数据
const scheduleForm = ref({
  courseName: '芭蕾舞基训与剧目排练',
  danceType: '芭蕾舞',
  classroomName: '1号芭蕾专业排练厅',
  classDate: '2026-08-25',
  timeRange: '14:00-16:00',
  topsReq: '粉色吊带连体服(无蕾丝)',
  bottomsReq: '白色九分芭蕾大袜',
  skirtReq: '粉色雪纺一片绑带短裙',
  shoesReq: '双皮头粉色软底鞋',
  hairReq: '高盘头丸子头(配黑色发网)',
  propsReq: '把杆砖、弹力带(2米中弹)'
})

const mentorshipForm = ref({
  seniorStudentName: '张悦悦(高年级学姐)',
  juniorStudentName: '李小桐(新学员)',
  termName: '芭蕾与中国舞联训班',
  starPoints: 50
})

const metricForm = ref({
  studentName: '张悦悦(高年级学姐)',
  heightCm: 152.5,
  weightKg: 38.0,
  bustCm: 72.0,
  waistCm: 58.0,
  hipCm: 76.0,
  shoeSize: 35.0
})

const isAdmin = computed(() => store.currentUser.role === 'SUPER_ADMIN')
const isTeacher = computed(() => store.currentUser.role === 'TEACHER')
const isCommittee = computed(() => store.currentUser.role === 'COMMITTEE')
const isTeacherOrAdmin = computed(() => isAdmin.value || isTeacher.value)

// 0. 快速体验角色切换
const quickSwitchRole = (roleType) => {
  const target = store.availableRoles.find(u => u.role === roleType)
  if (target) {
    store.switchRole(target)
  }
}

// 1. 学员信息下载
const downloadStudentInfo = async () => {
  try {
    const res = await api.exportStudents()
    if (res.data) {
      alert('📥 学员信息导出数据表:\n\n' + res.data)
    }
  } catch (e) {
    store.showToast('导出失败', 'error')
  }
}

// 2. 物品需求下载
const downloadItemDemand = async () => {
  try {
    const res = await api.exportItemDemands()
    if (res.data) {
      alert('📦 物品需求汇总导出表:\n\n' + res.data)
    }
  } catch (e) {
    store.showToast('导出失败', 'error')
  }
}

// 3. 有感而发 & 心里话
const openThoughtModal = (type) => {
  activeThoughtType.value = type
  showThoughtModal.value = true
  loadThoughts()
}

const loadThoughts = async () => {
  try {
    const res = await api.getThoughts(activeThoughtType.value)
    if (res.data) thoughtList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const submitThought = async () => {
  if (!thoughtContent.value.trim()) {
    store.showToast('请输入内容', 'error')
    return
  }
  try {
    const payload = {
      studentName: store.currentUser.name || '匿名用户',
      roleType: store.currentUser.role || 'STUDENT',
      type: activeThoughtType.value,
      content: thoughtContent.value
    }
    const res = await api.publishThought(payload)
    if (res.code === 200) {
      store.showToast('发布成功！', 'success')
      thoughtContent.value = ''
      await loadThoughts()
    }
  } catch (e) {
    store.showToast('发布失败', 'error')
  }
}

// 4. 物品需求勾选及数量
const loadItemDemands = async () => {
  try {
    const res = await api.getItemDemands()
    if (res.data) itemDemandList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const toggleNeedIt = (id) => {
  itemDemandList.value = itemDemandList.value.map(item => {
    if (item.itemId === id) {
      const next = !item.needIt
      api.updateItemDemand({ itemId: id, needIt: next, quantity: next ? (item.quantity || 1) : 0 })
      return { ...item, needIt: next, quantity: next ? (item.quantity || 1) : 0 }
    }
    return item
  })
}

const changeQty = (id, delta) => {
  itemDemandList.value = itemDemandList.value.map(item => {
    if (item.itemId === id) {
      const nextQty = Math.max(1, (item.quantity || 1) + delta)
      api.updateItemDemand({ itemId: id, needIt: true, quantity: nextQty })
      return { ...item, quantity: nextQty, needIt: true }
    }
    return item
  })
}

// 5. 家委动态采购明细表格
const addDynamicRow = () => {
  dynamicRows.value.push({ name: '', price: '', count: 1 })
}

const removeDynamicRow = (idx) => {
  if (dynamicRows.value.length <= 1) {
    store.showToast('至少需保留 1 项物品明细', 'error')
    return
  }
  dynamicRows.value.splice(idx, 1)
}

const submitDynamicPurchase = async () => {
  if (!purchaseTitle.value.trim()) {
    store.showToast('请输入采购主题', 'error')
    return
  }
  try {
    const payload = {
      title: purchaseTitle.value,
      items: dynamicRows.value
    }
    const res = await api.publishDynamicPurchase(payload)
    if (res.code === 200) {
      store.showToast('采购需求表格成功发布公示！', 'success')
      showDynamicPurchaseModal.value = false
    }
  } catch (e) {
    store.showToast('发布失败', 'error')
  }
}

// 其它通用逻辑
const loadStudentProfile = async () => {
  try {
    const res = await api.getStudentProfile(store.currentUser.id || 6)
    if (res.data) studentProfileForm.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const submitStudentProfile = async () => {
  try {
    const payload = {
      ...studentProfileForm.value,
      studentId: store.currentUser.id || 6,
      chineseScore: Number(studentProfileForm.value.chineseScore) || 0,
      mathScore: Number(studentProfileForm.value.mathScore) || 0,
      englishScore: Number(studentProfileForm.value.englishScore) || 0,
      heightCm: Number(studentProfileForm.value.heightCm) || 0,
      weightKg: Number(studentProfileForm.value.weightKg) || 0
    }
    const res = await api.saveStudentProfile(payload)
    if (res.code === 200) {
      store.showToast('学员档案保存成功！', 'success')
      showStudentProfileModal.value = false
    }
  } catch (e) {
    store.showToast('保存失败', 'error')
  }
}

const submitSchedule = async () => {
  const times = (scheduleForm.value.timeRange || '14:00-16:00').split('-')
  try {
    const payload = {
      courseName: scheduleForm.value.courseName,
      danceType: scheduleForm.value.danceType,
      teacherId: store.currentUser.id || 2,
      teacherName: store.currentUser.name || '林依依老师',
      classroomName: scheduleForm.value.classroomName,
      classDate: scheduleForm.value.classDate,
      startTime: times[0] || '14:00',
      endTime: times[1] || '16:00',
      topsReq: scheduleForm.value.topsReq,
      bottomsReq: scheduleForm.value.bottomsReq,
      skirtReq: scheduleForm.value.skirtReq,
      shoesReq: scheduleForm.value.shoesReq,
      hairReq: scheduleForm.value.hairReq,
      propsReq: scheduleForm.value.propsReq,
      capacity: 15
    }
    const res = await api.createSchedule(payload)
    if (res.code === 200) {
      store.showToast('新排课与着装指南发布成功！', 'success')
      showScheduleModal.value = false
    }
  } catch (e) {
    store.showToast('发布失败', 'error')
  }
}

const submitMentorship = async () => {
  try {
    const payload = {
      seniorStudentId: 5,
      seniorStudentName: mentorshipForm.value.seniorStudentName,
      juniorStudentId: 6,
      juniorStudentName: mentorshipForm.value.juniorStudentName,
      termName: mentorshipForm.value.termName,
      starPoints: Number(mentorshipForm.value.starPoints) || 50
    }
    const res = await api.createMentorship(payload)
    if (res.code === 200) {
      store.showToast('高低年级结对子设置成功！', 'success')
      showMentorshipModal.value = false
    }
  } catch (e) {
    store.showToast('设置失败', 'error')
  }
}

const submitMetric = async () => {
  try {
    const payload = {
      studentId: 5,
      studentName: metricForm.value.studentName,
      heightCm: Number(metricForm.value.heightCm),
      weightKg: Number(metricForm.value.weightKg),
      bustCm: Number(metricForm.value.bustCm),
      waistCm: Number(metricForm.value.waistCm),
      hipCm: Number(metricForm.value.hipCm),
      torsoLengthCm: 56.0,
      shoeSize: Number(metricForm.value.shoeSize),
      measuredDate: '2026-08-21'
    }
    const res = await api.saveMetric(payload)
    if (res.code === 200) {
      store.showToast('学员量体档案需求保存成功！', 'success')
      showMetricModal.value = false
    }
  } catch (e) {
    store.showToast('保存失败', 'error')
  }
}

const loadPendingApprovals = async () => {
  if (!isAdmin.value) return
  try {
    const res = await api.getPendingApprovals('SUPER_ADMIN')
    if (res.data) pendingList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleApprove = async (userId, status) => {
  const actionName = status === 1 ? '通过审批' : '驳回'
  if (confirm(`确定要 ${actionName} 该用户的角色申请吗？`)) {
    try {
      const res = await api.approveUser(userId, status)
      if (res.code === 200) {
        store.showToast(`已成功${actionName}`, 'success')
        await loadPendingApprovals()
      }
    } catch (e) {
      store.showToast('操作失败', 'error')
    }
  }
}

onMounted(() => {
  loadStudentProfile()
  loadItemDemands()
  loadPendingApprovals()
})
</script>

<style scoped>
.role-switch-pills-bar {
  background: #ffffff; border-radius: 12px; padding: 8px 10px; margin-bottom: 12px;
  border: 1px solid #e2e8f0; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  display: flex; flex-direction: column; gap: 6px; text-align: left;
}
.switch-tip-label { font-size: 11px; font-weight: 800; color: #7e22ce; }
.pills-scroll-row { display: flex; align-items: center; gap: 6px; overflow-x: auto; }
.role-pill {
  font-size: 11px; font-weight: 700; color: #64748b; background: #f1f5f9;
  padding: 4px 10px; border-radius: 14px; white-space: nowrap; border: none; cursor: pointer;
}
.active-pill { color: #ffffff !important; font-weight: 800; }
.purple-pill { background: #7e22ce !important; }
.blue-pill { background: #2563eb !important; }
.amber-pill { background: #d97706 !important; }
.green-pill { background: #059669 !important; }

.profile-header-card {
  background: linear-gradient(135deg, #7e22ce 0%, #a21caf 50%, #be123c 100%);
  border-radius: 16px; padding: 18px 16px; color: #ffffff;
  margin-bottom: 16px; box-shadow: 0 6px 20px rgba(126, 34, 206, 0.15); text-align: left;
}
.user-main-row { display: flex; align-items: center; gap: 12px; }
.u-avatar {
  width: 52px; height: 52px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.2); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  font-size: 26px; border: 2px solid rgba(255, 255, 255, 0.5);
}
.u-info-col { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.u-name-line { display: flex; align-items: center; gap: 8px; }
.u-name { font-size: 18px; font-weight: 800; }
.role-badge { font-size: 10px; font-weight: 700; background: #fef08a; color: #854d0e; padding: 2px 8px; border-radius: 10px; }
.u-sub-text { font-size: 11px; opacity: 0.9; }
.switch-btn { font-size: 11px; font-weight: 700; background: rgba(255, 255, 255, 0.2); backdrop-filter: blur(4px); padding: 6px 12px; border-radius: 16px; border: 1px solid rgba(255, 255, 255, 0.3); color: #ffffff; text-decoration: none; }

.section-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; padding: 0 4px; }
.menu-group-title { font-size: 13px; font-weight: 800; color: #1e293b; text-align: left; }
.group-tip { font-size: 10px; color: #94a3b8; }
.margin-top-12 { margin-top: 14px; }
.margin-top-4 { margin-top: 4px; }

.action-tools-grid {
  background: #ffffff; border-radius: 16px; padding: 16px 10px;
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px;
  margin-bottom: 16px; box-shadow: 0 4px 14px rgba(0, 0, 0, 0.03); border: 1px solid #f1f5f9;
}
.action-item { display: flex; flex-direction: column; align-items: center; gap: 6px; cursor: pointer; }
.text-decoration-none { text-decoration: none; }
.action-icon-box {
  width: 46px; height: 46px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center; position: relative;
}
.purple-bg { background: #faf5ff; }
.blue-bg { background: #eff6ff; }
.amber-bg { background: #fffbe6; }
.green-bg { background: #ecfdf5; }

.cyan-bg { background: #e0f2fe; }
.pink-bg { background: #fce7f3; }
.rose-bg { background: #ffe4e6; }
.emerald-bg { background: #d1fae5; }

.indigo-bg { background: #e0e7ff; }
.violet-bg { background: #ddd6fe; }
.fuchsia-bg { background: #fae8ff; }
.sky-bg { background: #e0f2fe; }

.a-icon { font-size: 22px; }
.badge-dot {
  position: absolute; top: -4px; right: -4px; background: #ef4444; color: #ffffff;
  font-size: 10px; font-weight: 800; width: 18px; height: 18px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; border: 2px solid #ffffff;
}
.action-label { font-size: 12px; font-weight: 700; color: #334155; text-align: center; }

/* 动态采购明细表格 */
.section-title-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.s-title { font-size: 13px; font-weight: 800; color: #1e293b; margin: 0; }
.add-row-btn { font-size: 11px; font-weight: 700; background: #7e22ce; color: #ffffff; padding: 4px 10px; border-radius: 12px; border: none; cursor: pointer; }

.table-row-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 12px; margin-bottom: 10px; text-align: left; }
.t-row-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.t-row-num { font-size: 12px; font-weight: 800; color: #6b21a8; }
.del-row-btn { font-size: 10px; color: #ef4444; font-weight: 700; border: none; background: none; cursor: pointer; }

/* 物品需求列表 */
.demand-item-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 12px; margin-bottom: 10px; display: flex; flex-direction: column; gap: 8px; text-align: left; }
.d-item-main { display: flex; flex-direction: column; gap: 2px; }
.d-name { font-size: 14px; font-weight: 800; color: #1e293b; }
.d-spec { font-size: 11px; color: #64748b; }
.d-price { color: #e11d48; font-weight: 700; }
.d-control-row { display: flex; justify-content: space-between; align-items: center; padding-top: 6px; border-top: 1px dashed #f1f5f9; }
.need-switch-box { display: flex; align-items: center; gap: 6px; cursor: pointer; }
.checkbox-icon { font-size: 16px; color: #7e22ce; font-weight: 800; }
.need-label { font-size: 12px; font-weight: 700; }
.text-green { color: #059669; }
.text-gray { color: #94a3b8; }
.qty-counter-box { display: flex; align-items: center; border: 1px solid #cbd5e1; border-radius: 6px; overflow: hidden; }
.qty-btn { width: 28px; height: 28px; line-height: 28px; text-align: center; background: #f1f5f9; font-size: 14px; font-weight: 800; color: #334155; border: none; cursor: pointer; }
.qty-num { width: 32px; height: 28px; line-height: 28px; text-align: center; font-size: 13px; font-weight: 800; color: #0f172a; background: #ffffff; }

.form-row-3 { display: flex; gap: 6px; }
.form-row-2 { display: flex; gap: 10px; }
.flex-1 { flex: 1; }

.tag-badge { font-size: 10px; font-weight: 700; padding: 2px 6px; border-radius: 4px; }
.tag-purple { background: #faf5ff; color: #7e22ce; }
.tag-amber { background: #fffbe6; color: #d97706; }
.tag-rose { background: #ffe4e6; color: #e11d48; }

.hall-card { background: #ffffff; border-radius: 12px; padding: 12px; margin-bottom: 10px; border: 1px solid #e2e8f0; text-align: left; }
.hall-title { font-size: 13px; font-weight: 800; color: #7e22ce; margin-bottom: 6px; }
.hall-specs { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 6px; }
.spec-tag { font-size: 9px; font-weight: 700; background: #faf5ff; color: #8b5cf6; padding: 2px 6px; border-radius: 4px; }
.hall-desc { font-size: 11px; color: #475569; line-height: 1.4; }

.pending-user-item { background: #ffffff; border-radius: 10px; padding: 10px 12px; margin-bottom: 8px; border: 1px solid #fed7aa; display: flex; justify-content: space-between; align-items: center; }
.p-user-info { display: flex; flex-direction: column; gap: 2px; }
.p-name-row { display: flex; align-items: center; gap: 6px; }
.p-name { font-size: 13px; font-weight: 800; color: #1e293b; }
.p-sub { font-size: 10px; color: #64748b; }
.p-actions { display: flex; gap: 6px; }
.pass-btn { background: #059669; color: #ffffff; border: none; padding: 4px 10px; border-radius: 6px; cursor: pointer; font-size: 11px; }
.reject-btn { border: 1px solid #ef4444; color: #ef4444; background: none; padding: 4px 10px; border-radius: 6px; cursor: pointer; font-size: 11px; }
.empty-pending { font-size: 12px; color: #94a3b8; text-align: center; padding: 20px 0; font-style: italic; }

.form-textarea { width: 100%; height: 70px; background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 8px; padding: 10px; font-size: 12px; box-sizing: border-box; margin-bottom: 10px; outline: none; }
.submit-ask-btn { font-size: 12px; padding: 6px 16px; border-radius: 18px; }

.msg-card { margin-bottom: 10px; border-left: 3px solid #cbd5e1; background: #f8fafc; padding: 10px; border-radius: 8px; text-align: left; }
.msg-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.msg-date { font-size: 10px; color: #94a3b8; }
.q-row { margin-bottom: 4px; }
.q-label { font-size: 12px; font-weight: 700; color: #be123c; margin-right: 4px; }
.q-text { font-size: 12px; color: #0f172a; line-height: 1.4; }

.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(4px);
  z-index: 999; display: flex; align-items: center; justify-content: center; padding: 16px;
}
.modal-card {
  width: 100%; max-width: 420px; background: #ffffff; border-radius: 18px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2); display: flex; flex-direction: column;
  max-height: 85vh; overflow: hidden; text-align: left;
}
.modal-head { padding: 16px 18px; background: #faf5ff; border-bottom: 1px solid #f3e8ff; display: flex; justify-content: space-between; align-items: center; }
.m-title { font-size: 15px; font-weight: 800; color: #7e22ce; margin: 0; }
.close-btn { font-size: 16px; color: #94a3b8; font-weight: 700; border: none; background: none; cursor: pointer; }
.modal-body { padding: 16px 18px; overflow-y: auto; box-sizing: border-box; }
.form-group { margin-bottom: 12px; display: flex; flex-direction: column; gap: 4px; }
.form-label { font-size: 12px; font-weight: 700; color: #334155; }
.form-input {
  height: 42px; line-height: 42px; background: #f8fafc; border: 1px solid #cbd5e1;
  border-radius: 8px; padding: 0 12px; font-size: 13px; box-sizing: border-box; outline: none;
}
.modal-foot { padding: 12px 18px; border-top: 1px solid #f1f5f9; display: flex; justify-content: flex-end; gap: 10px; }
</style>
