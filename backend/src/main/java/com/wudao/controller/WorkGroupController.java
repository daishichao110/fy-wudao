package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.WorkGroup;
import com.wudao.service.WorkGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-group")
public class WorkGroupController {

    private static final Logger log = LoggerFactory.getLogger(WorkGroupController.class);

    @Autowired
    private WorkGroupService workGroupService;

    @GetMapping("/list")
    public Result<List<WorkGroup>> getWorkGroups() {
        log.info("[REST API GET /api/work-group/list] Fetching work groups roster");
        List<WorkGroup> list = workGroupService.getAllWorkGroups();
        return Result.success("获取工作小组列表成功", list);
    }

    @PostMapping("/save")
    public Result<WorkGroup> saveWorkGroup(@RequestBody WorkGroup group) {
        log.info("[REST API POST /api/work-group/save] Saving work group: name={}, leader={}", group.getGroupName(), group.getLeaderName());
        if (group.getGroupName() == null || group.getGroupName().trim().isEmpty()) {
            return Result.error("小组名称不能为空");
        }
        WorkGroup saved = workGroupService.saveOrUpdateGroup(group);
        return Result.success("保存工作小组信息成功", saved);
    }

    @PostMapping("/delete")
    public Result<String> deleteWorkGroup(@RequestParam String groupId) {
        log.info("[REST API POST /api/work-group/delete] Deleting work group: groupId={}", groupId);
        workGroupService.deleteGroup(groupId);
        return Result.success("删除工作小组成功");
    }
}
