package com.wudao.mapper;

import com.wudao.entity.QaMessage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QaMessageMapper {
    List<QaMessage> selectByUserId(@Param("userId") Long userId);
    List<QaMessage> selectFeatured();
    QaMessage selectById(@Param("msgId") Long msgId);
    int insert(QaMessage msg);
    int updateReply(@Param("msgId") Long msgId, @Param("replyContent") String replyContent);
    int updateFeatured(@Param("msgId") Long msgId, @Param("featuredTitle") String featuredTitle);
}
