package com.scmt.core.serviceimpl;

import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.dao.StopWordDao;
import com.scmt.core.entity.StopWord;
import com.scmt.core.service.StopWordService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
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
 * 禁用词管理接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class StopWordServiceImpl implements StopWordService {

    @Autowired
    private StopWordDao stopWordDao;

    @Override
    public StopWordDao getRepository() {
        return stopWordDao;
    }

    private static WordTree wordTree;

    public WordTree getInstance() {

        if (wordTree == null) {
            // 初始加载数据
            wordTree = new WordTree();
            stopWordDao.findAll().forEach(e -> wordTree.addWord(e.getTitle()));
        }
        return wordTree;
    }

    @Override
    public Page<StopWord> findByCondition(StopWord stopWord, SearchVo searchVo, Pageable pageable) {

        return stopWordDao.findAll(new Specification<StopWord>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<StopWord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> titleField = root.get("title");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<>();

                if (StrUtil.isNotBlank(stopWord.getTitle())) {
                    list.add(cb.like(titleField, "%" + stopWord.getTitle() + "%"));
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

}
