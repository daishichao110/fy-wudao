package com.wudao.service;

import com.wudao.entity.WorkGroup;
import java.util.List;

public interface WorkGroupService {
    List<WorkGroup> getAllWorkGroups();
    WorkGroup saveOrUpdateGroup(WorkGroup group);
    boolean deleteGroup(Long groupId);
}
