package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Notice;
import com.wudao.mapper.NoticeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 获取数据库真正的全量公告通知列表 (无预制 Mock 数据，零数据时返回空数组)
     */
    @GetMapping("/list")
    public Result<List<Notice>> getNotices() {
        log.info("[REST API GET /api/notice/list] Querying real notices from MySQL table sys_notice...");
        List<Notice> list = noticeMapper.selectAllNotices();
        log.info("[REST API GET /api/notice/list] Fetched {} notices from database.", list.size());
        return Result.success("获取成功", list);
    }

    /**
     * 超级管理员/教师发布并保存新公告通知至 MySQL 数据库
     */
    @PostMapping("/create")
    public Result<Notice> createNotice(@RequestBody Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return Result.error("请输入公告标题！");
        }

        if (notice.getTag() == null || notice.getTag().trim().isEmpty()) {
            notice.setTag("【通知】");
        }

        if (notice.getPublisher() == null || notice.getPublisher().trim().isEmpty()) {
            notice.setPublisher("舞蹈学校教务处");
        }

        log.info("[REST API POST /api/notice/create] Inserting real notice: Title={}, Tag={}", notice.getTitle(), notice.getTag());
        if (!org.springframework.util.StringUtils.hasText(notice.getNoticeId())) {
            notice.setNoticeId(com.wudao.common.SnowflakeIdWorker.generateIdStr());
        }
        noticeMapper.insertNotice(notice);
        log.info("[REST API POST /api/notice/create] Saved notice ID: {}", notice.getNoticeId());

        return Result.success("公告广播发布成功！", notice);
    }
}
