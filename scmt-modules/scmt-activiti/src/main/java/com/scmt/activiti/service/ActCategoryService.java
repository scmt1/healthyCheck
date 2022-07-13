package com.scmt.activiti.service;

import com.scmt.activiti.entity.ActCategory;
import com.scmt.core.base.ScmtBaseService;

import java.util.List;

/**
 * 流程分类接口
 * @author Exrick
 */
public interface ActCategoryService extends ScmtBaseService<ActCategory, String> {

    /**
     * 通过父id获取
     * @param parentId
     * @return
     */
    List<ActCategory> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过名称模糊搜索
     * @param title
     * @return
     */
    List<ActCategory> findByTitleLikeOrderBySortOrder(String title);
}
