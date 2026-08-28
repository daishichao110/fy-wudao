package com.wudao.service;

import com.wudao.entity.QaMessage;
import java.util.List;

public interface QaMessageService {
    List<QaMessage> getMyMessages(Long userId);
    List<QaMessage> getFeaturedMessages();
    QaMessage askQuestion(QaMessage msg);
    QaMessage replyQuestion(Long msgId, String replyContent);
    QaMessage featureQuestion(Long msgId, String featuredTitle);
}
