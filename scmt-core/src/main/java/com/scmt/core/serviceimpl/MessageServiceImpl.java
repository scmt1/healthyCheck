package com.scmt.core.serviceimpl;

import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.dao.MessageDao;
import com.scmt.core.entity.Message;
import com.scmt.core.service.MessageService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消息内容接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Override
    public MessageDao getRepository() {
        return messageDao;
    }

    @Override
    public Page<Message> findByCondition(Message message, SearchVo searchVo, Pageable pageable) {

        return messageDao.findAll(new Specification<Message>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> titleField = root.get("title");
                Path<String> contentField = root.get("content");
                Path<String> typeField = root.get("type");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                // 模糊搜素
                if (StrUtil.isNotBlank(message.getTitle())) {
                    list.add(cb.like(titleField, '%' + message.getTitle() + '%'));
                }
                if (StrUtil.isNotBlank(message.getContent())) {
                    list.add(cb.like(contentField, '%' + message.getContent() + '%'));
                }

                if (message.getType() != null) {
                    list.add(cb.equal(typeField, message.getType()));
                }

                // 创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public List<Message> findByCreateSend(Boolean createSend) {

        return messageDao.findByCreateSend(createSend);
    }
}
