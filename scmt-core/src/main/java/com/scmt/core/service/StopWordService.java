package com.scmt.core.service;

import com.scmt.core.base.ScmtBaseService;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.StopWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 禁用词管理接口
 * @author Exrick
 */
public interface StopWordService extends ScmtBaseService<StopWord, String> {

    /**
    * 多条件分页获取
    * @param stopWord
    * @param searchVo
    * @param pageable
    * @return
    */
    Page<StopWord> findByCondition(StopWord stopWord, SearchVo searchVo, Pageable pageable);

}
