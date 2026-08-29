package com.wudao.service.impl;

import com.wudao.common.SnowflakeIdWorker;
import com.wudao.entity.User;
import com.wudao.entity.WorkGroup;
import com.wudao.mapper.UserMapper;
import com.wudao.mapper.WorkGroupMapper;
import com.wudao.service.WorkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkGroupServiceImpl implements WorkGroupService {

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<WorkGroup> getAllWorkGroups() {
        List<WorkGroup> list = workGroupMapper.selectAll();
        for (WorkGroup group : list) {
            // 组长姓名拼接：根据 leader_user_id 检索 sys_user 拼接 "学生姓名+的+家长称呼" (例如：张小宝的妈妈)
            if (group.getLeaderUserId() != null) {
                User leaderUser = userMapper.selectById(group.getLeaderUserId());
                if (leaderUser != null && leaderUser.getStudentName() != null) {
                    String rel = leaderUser.getRelationship() != null ? leaderUser.getRelationship() : "家长";
                    group.setLeaderName(leaderUser.getStudentName() + "的" + rel);
                }
            }

            // 组员姓名拼接：根据 member_user_ids 检索 sys_user 动态拼接 (例如：李小桐的爸爸, 张小宝的妈妈, 王美美的妈妈)
            if (group.getMemberUserIds() != null && !group.getMemberUserIds().trim().isEmpty()) {
                String[] idStrs = group.getMemberUserIds().split(",");
                List<String> names = new ArrayList<>();
                for (String idStr : idStrs) {
                    try {
                        Long uid = Long.parseLong(idStr.trim());
                        User memberUser = userMapper.selectById(uid);
                        if (memberUser != null && memberUser.getStudentName() != null) {
                            String rel = memberUser.getRelationship() != null ? memberUser.getRelationship() : "家长";
                            names.add(memberUser.getStudentName() + "的" + rel);
                        }
                    } catch (Exception e) {
                        // ignore malformed id
                    }
                }
                if (!names.isEmpty()) {
                    group.setMemberNames(String.join(", ", names));
                }
            }
        }
        return list;
    }

    @Override
    public WorkGroup saveOrUpdateGroup(WorkGroup group) {
        if (group.getGroupId() == null || group.getGroupId() <= 0) {
            group.setGroupId(SnowflakeIdWorker.generateId());
            if (group.getIcon() == null || group.getIcon().trim().isEmpty()) {
                group.setIcon("👥");
            }
            if (group.getDanceClassName() == null || group.getDanceClassName().trim().isEmpty()) {
                group.setDanceClassName("全校/公共");
            }
            if (group.getSortOrder() == null) {
                group.setSortOrder(99);
            }
            workGroupMapper.insert(group);
        } else {
            workGroupMapper.update(group);
        }
        return group;
    }

    @Override
    public boolean deleteGroup(Long groupId) {
        return workGroupMapper.delete(groupId) > 0;
    }
}
