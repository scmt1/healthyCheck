package com.scmt.activiti.dao.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @author Exrickx
 */
public interface HistoryIdentityMapper {

    /**
     * 添加历史任务关联用户信息
     * @param id
     * @param type
     * @param userId
     * @param taskId
     * @param procInstId
     * @return
     */
    Integer insert(@Param("id") String id, @Param("type") String type, @Param("userId") String userId,
                   @Param("taskId") String taskId, @Param("procInstId") String procInstId);

    /**
     * 通过类型和任务id查找用户id
     * @param type
     * @param taskId
     * @return
     */
    String findUserIdByTypeAndTaskId(@Param("type") String type, @Param("taskId") String taskId);
}
