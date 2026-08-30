package com.wudao.mapper;

import com.wudao.entity.Mentorship;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MentorshipMapper {
    List<Mentorship> selectAll();
    Mentorship selectById(@Param("pairId") String pairId);
    int incrementStarAndCheckin(@Param("pairId") String pairId, @Param("addStars") Integer addStars);
    int insert(Mentorship mentorship);
}
