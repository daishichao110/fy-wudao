# Host Onboard Smoke Guide

- Generated At: 2026-08-21T12:04:05.002041+00:00
- Project: /Users/daishichao110/Documents/my_study/workspace/super-fy/wudao
- Install Scope: project surfaces only
- Status: ok

## Gemini CLI

- Status: ready
- Standard Flow First Prompt: `/super-dev 你的需求`
- Competition Flow First Prompt: `/super-dev-seeai 比赛需求`
- Install Scope: project surfaces only

### Start Playbook
- 起手建议: 优先在当前 Gemini CLI 项目会话里使用注入出来的 /super-dev，并确认 GEMINI.md、settings 与 custom commands 已加载。
- 避免动作: 不要把 /super-dev 误当成宿主原生命令，也不要先把需求丢回普通聊天，再手工追流程状态。

### Post-Onboard Self-Check
- Gemini CLI 接入后先确认入口可用: /super-dev 你的需求 / /super-dev-seeai 比赛需求
- Gemini CLI 接入后再确认 SEEAI 项目补充面已写入: .gemini/commands/super-dev-seeai.toml
- Gemini CLI 接入后再确认 SEEAI 用户级补充面已写入: ~/.gemini/skills/super-dev-seeai/SKILL.md

### Official Workflow Checks
- 确认 Gemini CLI 按 official-context 官方协议面真实加载 Super Dev，而不是只检测到文件存在。
- 确认官方接入面真实生效: 项目侧 GEMINI.md / .gemini/commands/super-dev.toml
- 如启用当前增强接入面，再确认: 项目侧 .gemini/settings.json；用户侧 ~/.gemini/GEMINI.md / ~/.gemini/settings.json
- 确认 SEEAI 项目补充面真实生效: .gemini/commands/super-dev-seeai.toml
- 确认 SEEAI 用户级补充面真实生效: ~/.gemini/skills/super-dev-seeai/SKILL.md
- 确认当前 Gemini CLI 会话真实加载 GEMINI.md、可选 .gemini/settings.json 与 .gemini/commands，并把 /super-dev 视为注入出来的 custom command；skills 仅作增强层核对。

### Official Pass Criteria
- Gemini CLI 官方工作流面、入口链、恢复链与 SEEAI 补充面均已真人验收通过。
- 确认 Gemini CLI 按 official-context 官方协议面真实加载 Super Dev，而不是只检测到文件存在。
- 确认官方接入面真实生效: 项目侧 GEMINI.md / .gemini/commands/super-dev.toml
- 如启用当前增强接入面，再确认: 项目侧 .gemini/settings.json；用户侧 ~/.gemini/GEMINI.md / ~/.gemini/settings.json

### Resume Guidance
- 优先入口: /super-dev 你的需求 / /super-dev-seeai 比赛需求
- 原生恢复: /super-dev 继续当前流程 / 回当前 Gemini CLI 会话继续
- 优先沿用当前宿主会话恢复，不要先走新的普通聊天入口。

### Repair Playbook
-

### SEEAI Project Supplements
- `.gemini/commands/super-dev-seeai.toml`

### SEEAI User Supplements
- `~/.gemini/skills/super-dev-seeai/SKILL.md`

### Written Surfaces
- `/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/.gemini/commands/super-dev-seeai.toml`
- `/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/.gemini/commands/super-dev.toml`
- `/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/GEMINI.md`

## Antigravity

- Status: ready
- Standard Flow First Prompt: `/super-dev 你的需求`
- Competition Flow First Prompt: `/super-dev-seeai 比赛需求`
- Install Scope: project surfaces only

### Start Playbook
- 起手建议: 优先在当前 Antigravity Agent Chat 里直接用 /super-dev，保持同一条工作流连续性。
- 避免动作: 不要先切回普通聊天再补一大段背景。

### Post-Onboard Self-Check
- Antigravity 接入后先确认入口可用: /super-dev 你的需求 / /super-dev-seeai 比赛需求
- Antigravity 接入后再确认 SEEAI 项目补充面已写入: .gemini/commands/super-dev-seeai.toml
- Antigravity 接入后再确认 SEEAI 用户级补充面已写入: ~/.gemini/skills/super-dev-seeai/SKILL.md

### Official Workflow Checks
- 确认 Antigravity 按 recommended-gemini-workflow 当前推荐接入模型真实加载 Super Dev，而不是只检测到文件存在。
- 确认官方接入面真实生效: 项目侧 GEMINI.md / .gemini/commands/super-dev.toml
- 如启用当前增强接入面，再确认: 项目侧 .agent/workflows/super-dev.md；用户侧 ~/.gemini/GEMINI.md / ~/.gemini/commands/super-dev.toml
- 确认 SEEAI 项目补充面真实生效: .gemini/commands/super-dev-seeai.toml
- 确认 SEEAI 用户级补充面真实生效: ~/.gemini/skills/super-dev-seeai/SKILL.md
- 确认当前 Antigravity Agent Chat 真实加载 GEMINI.md、.gemini/commands 与当前推荐 `.agent/workflows/`；skills 只按兼容增强层核对。

### Official Pass Criteria
- Antigravity 当前推荐接入模型、入口链、恢复链与 SEEAI 补充面均已真人验收通过。
- 确认 Antigravity 按 recommended-gemini-workflow 当前推荐接入模型真实加载 Super Dev，而不是只检测到文件存在。
- 确认官方接入面真实生效: 项目侧 GEMINI.md / .gemini/commands/super-dev.toml
- 如启用当前增强接入面，再确认: 项目侧 .agent/workflows/super-dev.md；用户侧 ~/.gemini/GEMINI.md / ~/.gemini/commands/super-dev.toml

### Resume Guidance
- 优先入口: /super-dev 你的需求 / /super-dev-seeai 比赛需求
- 原生恢复: /super-dev 继续当前流程 / 回当前 Agent Chat 会话继续
- 优先沿用当前 Agent Chat 连续性，不要切到新的聊天线程。

### Repair Playbook
-

### SEEAI Project Supplements
- `.gemini/commands/super-dev-seeai.toml`

### SEEAI User Supplements
- `~/.gemini/skills/super-dev-seeai/SKILL.md`

### Written Surfaces
- `/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/.agent/workflows/super-dev.md`

## WorkBuddy

- Status: ready
- Standard Flow First Prompt: `super-dev: 你的需求`
- Competition Flow First Prompt: `super-dev-seeai: 比赛需求`
- Install Scope: project surfaces only

### Start Playbook
- 起手建议: 优先在当前任务线程里触发 super-dev:，保持任务上下文完整。
- 避免动作: 不要在新线程里重新开一个平行任务流。

### Post-Onboard Self-Check
- WorkBuddy 接入后先确认入口可用: super-dev: 你的需求 / super-dev-seeai: 比赛需求
- WorkBuddy 接入后再确认 SEEAI 用户级补充面已写入: ~/.workbuddy/skills/super-dev-seeai/SKILL.md
- 确认 WorkBuddy 按 manual-task-workbench-mcp 当前推荐接入模型真实加载 Super Dev，而不是只检测到文件存在。

### Official Workflow Checks
- 确认 WorkBuddy 按 manual-task-workbench-mcp 当前推荐接入模型真实加载 Super Dev，而不是只检测到文件存在。
- 如启用当前增强接入面，再确认: 用户侧 ~/.workbuddy/skills/super-dev/SKILL.md
- 确认 SEEAI 用户级补充面真实生效: ~/.workbuddy/skills/super-dev-seeai/SKILL.md
- 确认当前 WorkBuddy 任务工作台已启用 Skills、MCP 与项目目录授权；若使用文件侧 Skill 导入面，应把它视为当前接入模型的补充面而不是唯一官方合同。

### Official Pass Criteria
- WorkBuddy 当前推荐接入模型、入口链、恢复链与 SEEAI 补充面均已真人验收通过。
- 确认 WorkBuddy 按 manual-task-workbench-mcp 当前推荐接入模型真实加载 Super Dev，而不是只检测到文件存在。
- 如启用当前增强接入面，再确认: 用户侧 ~/.workbuddy/skills/super-dev/SKILL.md
- 确认 SEEAI 用户级补充面真实生效: ~/.workbuddy/skills/super-dev-seeai/SKILL.md

### Resume Guidance
- 优先入口: super-dev: 你的需求 / super-dev-seeai: 比赛需求
- 原生恢复: 回当前任务线程继续 / super-dev: 继续当前流程
- 优先沿用当前任务线程，不要重新开一个新的任务流。

### Repair Playbook
-

### SEEAI User Supplements
- `~/.workbuddy/skills/super-dev-seeai/SKILL.md`

### Written Surfaces
- `/Users/daishichao110/.workbuddy/skills/super-dev-seeai/SKILL.md`
- `/Users/daishichao110/.workbuddy/skills/super-dev/SKILL.md`
