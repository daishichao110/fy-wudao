package com.wudao.mapper;

import com.wudao.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NoticeMapper {
    List<Notice> selectAllNotices();
    int insertNotice(Notice notice);
}
