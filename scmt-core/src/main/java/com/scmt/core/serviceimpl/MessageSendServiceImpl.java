package com.scmt.core.serviceimpl;

import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.dao.MessageSendDao;
import com.scmt.core.entity.MessageSend;
import com.scmt.core.service.MessageSendService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息发送接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class MessageSendServiceImpl implements MessageSendService {

    @Autowired
    private MessageSendDao messageSendDao;

    //@Autowired
    //private SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageSendDao getRepository() {
        return messageSendDao;
    }

    @Override
    public MessageSend send(MessageSend messageSend) {
        MessageSend ms = messageSendDao.save(messageSend);
        //messagingTemplate.convertAndSendToUser(messageSend.getUserId(), "/queue/subscribe", "您收到了新的消息");
        return ms;
    }

    @Override
    public void deleteByMessageId(String messageId) {

        messageSendDao.deleteByMessageId(messageId);
    }

    @Override
    public Page<MessageSend> findByCondition(MessageSend messageSend, Pageable pageable) {

        return messageSendDao.findAll(new Specification<MessageSend>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<MessageSend> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> messageIdField = root.get("messageId");
                Path<String> userIdField = root.get("userId");
                Path<Integer> statusField = root.get("status");

                List<Predicate> list = new ArrayList<>();

                if (StrUtil.isNotBlank(messageSend.getMessageId())) {
                    list.add(cb.equal(messageIdField, messageSend.getMessageId()));
                }
                if (StrUtil.isNotBlank(messageSend.getUserId())) {
                    list.add(cb.equal(userIdField, messageSend.getUserId()));
                }
                if (messageSend.getStatus() != null) {
                    list.add(cb.equal(statusField, messageSend.getStatus()));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public void updateStatusByUserId(String userId, Integer status) {

        messageSendDao.updateStatusByUserId(userId, status);
    }

    @Override
    public void deleteByUserId(String userId) {

        messageSendDao.deleteByUserId(userId, CommonConstant.MESSAGE_STATUS_READ);
    }

}
