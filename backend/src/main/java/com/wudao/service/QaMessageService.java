package com.wudao.service;

import com.wudao.entity.QaMessage;
import java.util.List;

public interface QaMessageService {
    List<QaMessage> getMyMessages(String userId);
    List<QaMessage> getFeaturedMessages();
    QaMessage askQuestion(QaMessage msg);
    QaMessage replyQuestion(String msgId, String replyContent);
    QaMessage featureQuestion(String msgId, String featuredTitle);
}
