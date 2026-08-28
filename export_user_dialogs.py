import json
import re

transcript_path = '/Users/daishichao110/.gemini/antigravity-ide/brain/8db10ed9-6c92-43d0-9ff9-bb996c13345b/.system_generated/logs/transcript.jsonl'
output_path = '/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao/user_dialog_history.md'

user_requests = []

with open(transcript_path, 'r', encoding='utf-8') as f:
    for line in f:
        try:
            data = json.loads(line)
            content = data.get('content', '')
            if '<USER_REQUEST>' in content:
                match = re.search(r'<USER_REQUEST>\s*(.*?)\s*</USER_REQUEST>', content, re.DOTALL)
                if match:
                    req_text = match.group(1).strip()
                    if req_text and not req_text.startswith('{{ CHECKPOINT'):
                        if not user_requests or user_requests[-1] != req_text:
                            user_requests.append(req_text)
        except Exception:
            pass

lines = [
    '# 劲松金帆舞团项目 - 用户需求对话历史汇总\n\n',
    '> 导出时间：2026-08-23 | 对话 ID：8db10ed9-6c92-43d0-9ff9-bb996c13345b\n',
    '> 工程目录：/Users/daishichao110/Documents/my_study/workspace/super-fy/wudao\n\n',
    '本文档记录了在 **劲松金帆舞团** 开发过程中，您提出的所有需求、反馈与指令按时间顺序的完整导出列表：\n\n',
    '---\n\n'
]

for idx, req in enumerate(user_requests, 1):
    lines.append(f'### {idx}. 需求反馈 #{idx}\n')
    lines.append(f'```text\n{req}\n```\n\n')

with open(output_path, 'w', encoding='utf-8') as f:
    f.writelines(lines)

print(f'Done! Exported {len(user_requests)} user dialog requests.')
