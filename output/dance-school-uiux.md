# 舞蹈学校数字化综合管理小程序 —— UI/UX 设计规范文档 (UI/UX Redesign V2.0)

| 文档版本 | V2.0 (全端视觉大升级) | 编制日期 | 2026-08-21 |
| :--- | :--- | :--- | :--- |
| 设计风格 | 典雅艺术舞韵风 + 微信小程序高保真拟态微渐变 (Vibrant Ballet Rose & Stage Gold) | 图标规范 | 严格采用 Lucide Icons (杜绝任何 Emoji 作为功能图标) |
| 前端框架 | Vue 3 + TailwindCSS / WXML + WXSS Mobile First | 核心体验 | 顶级美感、金刚区 8 宫格、轮播海报、实时走马灯公告、名师风采 |

---

## 1. 首页 V2.0 视觉重构架构 (Homepage Redesign Specification)

### 1.1 品牌视觉 Token 升级 (Design Tokens)
```css
:root {
  /* 优雅芭蕾紫与舞台玫红 */
  --primary-purple: #7e22ce;
  --primary-rose: #be123c;
  --primary-pink: #ec4899;
  --hero-gradient: linear-gradient(135deg, #6b21a8 0%, #a21caf 50%, #be123c 100%);
  --gold-gradient: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  --bg-main: #f8fafc;
  --card-shadow: 0 8px 24px rgba(149, 157, 165, 0.08);
}
```

### 1.2 小程序首页 7 大吸引力模块 (7 Core Modules for Maximum Engagement)

1. **Top Banner Swiper (动态艺术轮播海报)**
   - 高保真艺术海报轮播：舞剧展演宣导、上课规范着装指南、极简零审批教务通知。
2. **Personalized User Hero Card (个性化欢迎与身份卡片)**
   - 展现学员/教师头像、真实姓名、当前微信授权身份、剩余课时与志愿积分勋章，带微倾斜高光。
3. **8-Grid Golden Matrix (金刚区 8 宫格高质感导航矩阵)**
   - 【教务排课】`Calendar` (紫底)
   - 【量体档案】`Ruler` (粉底)
   - 【家委协同】`HeartHandshake` (金底)
   - 【结对助学】`Award` (蓝底)
   - 【私信答疑】`MessageSquare` (绿底)
   - 【采购公示】`ShoppingBag` (橙底)
   - 【一键请假】`Zap` (红底)
   - 【CSV导出】`FileSpreadsheet` (靛底)
4. **Live Bulletin Ticker (实时消息走马灯播报)**
   - 动态横向滚动条：“📢 【家委广播】六一演出服装集采已发布，请点击确认”、“⚡ 【教务通知】请假已升级为即时生效模式”。
5. **Interactive "Today Schedule & Dress Code" Showcase (今日排课与着装指南深度展示卡片)**
   - 包含上身、下身、裙子、鞋履、发型、教具 6 大彩色药丸 Tag，以及老师快速发布按钮。
6. **Star Mentorship Spotlight (高低年级结对与名师风采推荐)**
   - 展示张悦悦学姐 & 李小桐学妹的【姐妹星积分 85 分】打卡卡片，激发参与感。
7. **Floating Quick Action Dock (底部悬浮快速操作栏)**
   - 一键请假 / 一键补课快捷浮动 Pill。
