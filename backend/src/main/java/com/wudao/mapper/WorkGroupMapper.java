package com.wudao.mapper;

import com.wudao.entity.WorkGroup;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WorkGroupMapper {
    List<WorkGroup> selectAll();
    int insert(WorkGroup group);
    int update(WorkGroup group);
    int delete(Long groupId);
}
