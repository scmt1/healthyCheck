package com.scmt.activiti.dao;

import com.scmt.activiti.entity.ActBusiness;
import com.scmt.core.base.ScmtBaseDao;

import java.util.List;

/**
 * 申请业务数据处理层
 * @author Exrick
 */
public interface ActBusinessDao extends ScmtBaseDao<ActBusiness, String> {

    /**
     * 通过流程定义id获取
     * @param procDefId
     * @return
     */
    List<ActBusiness> findByProcDefId(String procDefId);
}
