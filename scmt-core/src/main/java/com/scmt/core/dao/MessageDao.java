package com.scmt.core.dao;

import com.scmt.core.base.ScmtBaseDao;
import com.scmt.core.entity.Message;

import java.util.List;

/**
 * 消息内容数据处理层
 * @author Exrick
 */
public interface MessageDao extends ScmtBaseDao<Message, String> {

    /**
     * 通过创建发送标识获取
     * @param createSend
     * @return
     */
    List<Message> findByCreateSend(Boolean createSend);
}
