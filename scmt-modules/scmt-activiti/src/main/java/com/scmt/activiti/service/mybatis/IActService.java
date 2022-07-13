package com.scmt.activiti.service.mybatis;

/**
 * @author Exrickx
 */
public interface IActService {

    /**
     * 删除关联业务表
     * @param table
     * @param id
     * @return
     */
    Integer deleteBusiness(String table, String id);
}
