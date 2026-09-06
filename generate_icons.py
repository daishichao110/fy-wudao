import os
from PIL import Image, ImageDraw

def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

tabbar_dir = "/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/miniprogram/images/tabbar"
icons_dir = "/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/miniprogram/images/icons"
ensure_dir(tabbar_dir)
ensure_dir(icons_dir)

# 1. 默认头像
def make_default_avatar(path):
    size = 160
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # 圆形浅橙背景
    draw.ellipse([4, 4, size-4, size-4], fill=(255, 237, 213, 255), outline=(254, 215, 170, 255), width=2)
    # 头部
    draw.ellipse([54, 32, 106, 84], fill=(234, 88, 12, 255))
    # 身体半圆
    draw.chord([28, 92, 132, 160], start=180, end=360, fill=(234, 88, 12, 255))
    img.save(path, "PNG")

make_default_avatar("/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/miniprogram/images/default-avatar.png")

# 2. 生成 TabBar 图标 (80x80px)
def draw_home(draw, color):
    # 屋顶
    draw.polygon([(40, 16), (16, 38), (24, 38), (24, 64), (56, 64), (56, 38), (64, 38)], fill=color)
    # 门
    draw.rectangle([34, 46, 46, 64], fill=(255, 255, 255, 255))

def draw_schedule(draw, color):
    # 日历轮廓
    draw.rounded_rectangle([18, 22, 62, 64], radius=6, outline=color, width=5)
    # 顶部挂钩
    draw.rectangle([28, 16, 32, 24], fill=color)
    draw.rectangle([48, 16, 52, 24], fill=color)
    # 横线
    draw.line([(18, 34), (62, 34)], fill=color, width=4)
    # 点阵
    draw.rectangle([26, 42, 32, 48], fill=color)
    draw.rectangle([37, 42, 43, 48], fill=color)
    draw.rectangle([48, 42, 54, 48], fill=color)

def draw_volunteers(draw, color):
    # 两个小人
    # 主人
    draw.ellipse([26, 20, 42, 36], fill=color)
    draw.chord([16, 42, 52, 68], start=180, end=360, fill=color)
    # 协同人
    draw.ellipse([46, 26, 58, 38], fill=color)
    draw.chord([40, 44, 64, 66], start=180, end=360, fill=color)

def draw_profile(draw, color):
    # 个人
    draw.ellipse([27, 18, 53, 44], fill=color)
    draw.chord([17, 46, 63, 68], start=180, end=360, fill=color)

tab_drawers = {
    "home": draw_home,
    "schedule": draw_schedule,
    "volunteers": draw_volunteers,
    "profile": draw_profile
}

for name, drawer in tab_drawers.items():
    # 常规态 (#94a3b8)
    img_normal = Image.new("RGBA", (80, 80), (0, 0, 0, 0))
    d_norm = ImageDraw.Draw(img_normal)
    drawer(d_norm, (148, 163, 184, 255))
    img_normal.save(os.path.join(tabbar_dir, f"{name}.png"), "PNG")

    # 选中态 (#ea580c)
    img_active = Image.new("RGBA", (80, 80), (0, 0, 0, 0))
    d_act = ImageDraw.Draw(img_active)
    drawer(d_act, (234, 88, 12, 255))
    img_active.save(os.path.join(tabbar_dir, f"{name}_active.png"), "PNG")

# 3. 生成【我的】页面 10 个功能按钮高清图标 (96x96px)
color_orange = (234, 88, 12, 255)

def draw_btn_approval(draw):
    # 盾牌
    draw.polygon([(48, 16), (76, 26), (76, 54), (48, 80), (20, 54), (20, 26)], fill=color_orange)
    # 对勾
    draw.line([(32, 48), (42, 58), (62, 34)], fill=(255, 255, 255, 255), width=6)

def draw_btn_task(draw):
    # 喇叭
    draw.polygon([(22, 38), (38, 38), (58, 22), (58, 74), (38, 58), (22, 58)], fill=color_orange)
    draw.rectangle([66, 36, 72, 60], fill=color_orange)
    draw.rectangle([76, 42, 80, 54], fill=color_orange)

def draw_btn_group(draw):
    # 3人团队
    draw.ellipse([38, 18, 58, 38], fill=color_orange)
    draw.chord([26, 44, 70, 78], start=180, end=360, fill=color_orange)
    draw.ellipse([18, 28, 32, 42], fill=color_orange)
    draw.ellipse([64, 28, 78, 42], fill=color_orange)

def draw_btn_banner(draw):
    # 画框/展播
    draw.rounded_rectangle([18, 22, 78, 74], radius=8, outline=color_orange, width=6)
    # 播放三角
    draw.polygon([(40, 36), (62, 48), (40, 60)], fill=color_orange)

def draw_btn_notice(draw):
    # 铃铛
    draw.chord([24, 24, 72, 72], start=180, end=360, fill=color_orange)
    draw.rectangle([20, 60, 76, 68], fill=color_orange)
    draw.ellipse([40, 68, 56, 82], fill=color_orange)

def draw_btn_schedule(draw):
    # 课表日历
    draw.rounded_rectangle([18, 20, 78, 76], radius=8, fill=color_orange)
    draw.line([(18, 38), (78, 38)], fill=(255, 255, 255, 255), width=5)
    draw.rectangle([30, 48, 42, 60], fill=(255, 255, 255, 255))
    draw.rectangle([54, 48, 66, 60], fill=(255, 255, 255, 255))

def draw_btn_export(draw):
    # 导出文件
    draw.rounded_rectangle([24, 18, 72, 78], radius=6, outline=color_orange, width=6)
    # 向上箭头
    draw.line([(48, 36), (48, 62)], fill=color_orange, width=6)
    draw.polygon([(36, 44), (48, 30), (60, 44)], fill=color_orange)

def draw_btn_teacher(draw):
    # 名师/讲台
    draw.ellipse([34, 16, 62, 44], fill=color_orange)
    draw.polygon([(20, 78), (30, 52), (66, 52), (76, 78)], fill=color_orange)

def draw_btn_thought(draw):
    # 气泡
    draw.rounded_rectangle([18, 20, 78, 66], radius=12, fill=color_orange)
    draw.polygon([(32, 66), (24, 80), (48, 66)], fill=color_orange)
    draw.ellipse([32, 38, 40, 46], fill=(255, 255, 255, 255))
    draw.ellipse([44, 38, 52, 46], fill=(255, 255, 255, 255))
    draw.ellipse([56, 38, 64, 46], fill=(255, 255, 255, 255))

def draw_btn_heart(draw):
    # 心形
    draw.ellipse([22, 24, 52, 54], fill=color_orange)
    draw.ellipse([44, 24, 74, 54], fill=color_orange)
    draw.polygon([(23, 44), (73, 44), (48, 78)], fill=color_orange)

btn_drawers = {
    "approval": draw_btn_approval,
    "task": draw_btn_task,
    "group": draw_btn_group,
    "banner": draw_btn_banner,
    "notice": draw_btn_notice,
    "schedule": draw_btn_schedule,
    "export": draw_btn_export,
    "teacher": draw_btn_teacher,
    "thought": draw_btn_thought,
    "heart": draw_btn_heart
}

for name, drawer in btn_drawers.items():
    img = Image.new("RGBA", (96, 96), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    drawer(d)
    img.save(os.path.join(icons_dir, f"{name}.png"), "PNG")

print("所有图标生成成功！")
