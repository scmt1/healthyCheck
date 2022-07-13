package com.scmt.activiti.service;

import com.scmt.activiti.entity.ActModel;
import com.scmt.core.base.ScmtBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 模型管理接口
 * @author Exrick
 */
public interface ActModelService extends ScmtBaseService<ActModel, String> {

    /**
     * 多条件分页获取
     * @param actModel
     * @param pageable
     * @return
     */
    Page<ActModel> findByCondition(ActModel actModel, Pageable pageable);
}
