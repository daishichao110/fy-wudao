package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.QaMessage;
import com.wudao.service.QaMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
public class QaMessageController {

    private static final Logger log = LoggerFactory.getLogger(QaMessageController.class);

    @Autowired
    private QaMessageService qaMessageService;

    @GetMapping("/my-messages")
    public Result<List<QaMessage>> getMyMessages(@RequestParam("userId") Long userId) {
        log.info("[REST API GET /api/qa/my-messages] Querying messages for userId: {}", userId);
        List<QaMessage> list = qaMessageService.getMyMessages(userId);
        return Result.success(list);
    }

    @GetMapping("/featured-list")
    public Result<List<QaMessage>> getFeaturedList() {
        log.info("[REST API GET /api/qa/featured-list] Querying featured Q&A knowledge base");
        List<QaMessage> list = qaMessageService.getFeaturedMessages();
        return Result.success(list);
    }

    @PostMapping("/ask")
    public Result<QaMessage> askQuestion(@RequestBody QaMessage msg) {
        log.info("[REST API POST /api/qa/ask] Student asking teacher: studentId={}, teacherId={}", msg.getStudentId(), msg.getTeacherId());
        QaMessage res = qaMessageService.askQuestion(msg);
        return Result.success("提问发送成功", res);
    }

    @PostMapping("/reply")
    public Result<QaMessage> replyQuestion(@RequestBody Map<String, Object> params) {
        Long msgId = Long.valueOf(params.get("msgId").toString());
        String replyContent = params.get("replyContent").toString();
        log.info("[REST API POST /api/qa/reply] Teacher replying for msgId: {}", msgId);
        QaMessage res = qaMessageService.replyQuestion(msgId, replyContent);
        return Result.success("专业回复成功", res);
    }

    @PostMapping("/feature")
    public Result<QaMessage> featureQuestion(@RequestBody Map<String, Object> params) {
        Long msgId = Long.valueOf(params.get("msgId").toString());
        String featuredTitle = params.get("featuredTitle").toString();
        log.info("[REST API POST /api/qa/feature] Teacher one-click publishing to knowledge base: msgId={}", msgId);
        QaMessage res = qaMessageService.featureQuestion(msgId, featuredTitle);
        return Result.success("已精选公开至舞蹈知识百宝箱", res);
    }
}
