package com.wudao.mapper;

import com.wudao.entity.Mentorship;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MentorshipMapper {
    List<Mentorship> selectAll();
    Mentorship selectById(@Param("pairId") Long pairId);
    int incrementStarAndCheckin(@Param("pairId") Long pairId, @Param("addStars") Integer addStars);
    int insert(Mentorship mentorship);
}
