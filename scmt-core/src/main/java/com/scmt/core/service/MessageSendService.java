package com.scmt.core.service;

import com.scmt.core.base.ScmtBaseService;
import com.scmt.core.entity.MessageSend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 消息发送接口
 * @author Exrick
 */
public interface MessageSendService extends ScmtBaseService<MessageSend, String> {

    /**
     * 发送消息 带websock推送
     * @param messageSend
     * @return
     */
    MessageSend send(MessageSend messageSend);

    /**
     * 通过消息id删除
     * @param messageId
     */
    void deleteByMessageId(String messageId);

    /**
     * 多条件分页获取
     * @param messageSend
     * @param pageable
     * @return
     */
    Page<MessageSend> findByCondition(MessageSend messageSend, Pageable pageable);

    /**
     * 批量更新消息状态
     * @param userId
     * @param status
     */
    void updateStatusByUserId(String userId, Integer status);

    /**
     * 通过userId删除
     * @param userId
     */
    void deleteByUserId(String userId);
}
