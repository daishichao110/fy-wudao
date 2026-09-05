package com.wudao.mapper;

import com.wudao.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    int insertLog(OperationLog log);

    List<OperationLog> selectLogs(@Param("userId") String userId, @Param("opType") String opType, @Param("limit") Integer limit);
}
