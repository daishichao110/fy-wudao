package com.wudao.mapper;

import com.wudao.entity.QaMessage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QaMessageMapper {
    List<QaMessage> selectByUserId(@Param("userId") String userId);
    List<QaMessage> selectFeatured();
    QaMessage selectById(@Param("msgId") String msgId);
    int insert(QaMessage msg);
    int updateReply(@Param("msgId") String msgId, @Param("replyContent") String replyContent);
    int updateFeatured(@Param("msgId") String msgId, @Param("featuredTitle") String featuredTitle);
}
