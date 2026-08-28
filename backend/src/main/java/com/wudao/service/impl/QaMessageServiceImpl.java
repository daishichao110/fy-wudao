package com.wudao.service.impl;

import com.wudao.entity.QaMessage;
import com.wudao.entity.User;
import com.wudao.mapper.QaMessageMapper;
import com.wudao.mapper.UserMapper;
import com.wudao.service.QaMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class QaMessageServiceImpl implements QaMessageService {

    private static final Logger log = LoggerFactory.getLogger(QaMessageServiceImpl.class);

    @Autowired
    private QaMessageMapper qaMessageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<QaMessage> getMyMessages(Long userId) {
        log.info("[QaMessageService] Executing getMyMessages() for userId: {}", userId);
        if (userId == null || userId <= 0) {
            log.error("[QaMessageService] Invalid userId: {}", userId);
            throw new IllegalArgumentException("查询的用户ID不合法");
        }
        List<QaMessage> list = qaMessageMapper.selectByUserId(userId);
        log.info("[QaMessageService] Fetched {} Q&A messages for user {}", list != null ? list.size() : 0, userId);
        return list;
    }

    @Override
    @Transactional
    public QaMessage askQuestion(QaMessage message) {
        log.info("[QaMessageService] Executing askQuestion() (Strict 1-on-1 Q&A)...");

        // 1. 参数与基础非空校验
        if (message == null) {
            throw new IllegalArgumentException("提问参数不可为空");
        }
        if (message.getStudentId() == null || message.getStudentId() <= 0) {
            throw new IllegalArgumentException("提问学员ID不合法");
        }
        if (message.getTeacherId() == null || message.getTeacherId() <= 0) {
            throw new IllegalArgumentException("接收导师ID不合法");
        }
        if (!StringUtils.hasText(message.getQuestionContent()) || message.getQuestionContent().trim().length() < 4) {
            throw new IllegalArgumentException("提问内容不可少于4个字符");
        }

        // 2. 学员与导师存在性校验
        User student = userMapper.selectById(message.getStudentId());
        if (student == null) {
            log.error("[QaMessageService] Ask failed: Student ID {} not found", message.getStudentId());
            throw new IllegalArgumentException("提问学员账号不存在");
        }
        User teacher = userMapper.selectById(message.getTeacherId());
        if (teacher == null) {
            log.error("[QaMessageService] Ask failed: Teacher ID {} not found", message.getTeacherId());
            throw new IllegalArgumentException("接收提问的导师不存在");
        }

        // 3. 严格 1 对 1 赋名
        message.setStudentName(student.getRealName());
        message.setTeacherName(teacher.getRealName());
        message.setIsFeatured(0);

        if (message.getMsgId() == null || message.getMsgId() <= 0) {
            message.setMsgId(com.wudao.common.SnowflakeIdWorker.generateId());
        }

        qaMessageMapper.insert(message);
        log.info("[QaMessageService] Private Q&A message created successfully with ID: {}", message.getMsgId());
        return message;
    }

    @Override
    @Transactional
    public QaMessage replyQuestion(Long msgId, String replyContent) {
        log.info("[QaMessageService] Executing replyQuestion() for msgId={}", msgId);

        // 1. 参数校验
        if (msgId == null || msgId <= 0) {
            throw new IllegalArgumentException("回复的消息ID不合法");
        }
        if (!StringUtils.hasText(replyContent)) {
            throw new IllegalArgumentException("回复解答内容不可为空");
        }

        // 2. 消息存在性校验
        QaMessage existing = qaMessageMapper.selectById(msgId);
        if (existing == null) {
            log.error("[QaMessageService] Reply failed: QaMessage ID {} not found", msgId);
            throw new IllegalArgumentException("回复的问答记录不存在(ID: " + msgId + ")");
        }

        qaMessageMapper.updateReply(msgId, replyContent);
        existing.setReplyContent(replyContent);

        log.info("[QaMessageService] Question ID {} successfully replied by teacher {}", msgId, existing.getTeacherName());
        return existing;
    }

    @Override
    @Transactional
    public QaMessage featureQuestion(Long msgId, String featuredTitle) {
        log.info("[QaMessageService] Executing featureQuestion() for msgId={}", msgId);

        // 1. 参数校验
        if (msgId == null || msgId <= 0) {
            throw new IllegalArgumentException("精选消息ID不合法");
        }
        if (!StringUtils.hasText(featuredTitle)) {
            throw new IllegalArgumentException("精选知识点标题不可为空");
        }

        // 2. 消息与回复内容存在性校验
        QaMessage existing = qaMessageMapper.selectById(msgId);
        if (existing == null) {
            throw new IllegalArgumentException("该问答记录不存在");
        }
        if (!StringUtils.hasText(existing.getReplyContent())) {
            log.warn("[QaMessageService] Feature failed: Message ID {} has not been replied yet", msgId);
            throw new IllegalStateException("未经过导师专业回复的问答不能直接设为公开精选知识");
        }

        qaMessageMapper.updateFeatured(msgId, featuredTitle);
        existing.setIsFeatured(1);
        existing.setFeaturedTitle(featuredTitle);

        log.info("[QaMessageService] Message ID {} is now promoted to Knowledge Base! Title: {}", msgId, featuredTitle);
        return existing;
    }

    @Override
    public List<QaMessage> getFeaturedMessages() {
        log.info("[QaMessageService] Executing getFeaturedMessages()...");
        List<QaMessage> featured = qaMessageMapper.selectFeatured();
        log.info("[QaMessageService] Fetched {} featured Q&A knowledge items", featured != null ? featured.size() : 0);
        return featured;
    }
}
