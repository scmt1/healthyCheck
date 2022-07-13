package com.scmt.activiti.service;

import com.scmt.activiti.entity.ActBusiness;
import com.scmt.activiti.entity.ActProcess;
import com.scmt.activiti.vo.ProcessNodeVo;
import com.scmt.core.base.ScmtBaseService;
import com.scmt.core.common.vo.SearchVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 流程管理接口
 * @author Exrick
 */
public interface ActProcessService extends ScmtBaseService<ActProcess, String> {

    /**
     * 多条件分页获取
     * @param showLatest
     * @param actProcess
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<ActProcess> findByCondition(Boolean showLatest, ActProcess actProcess, SearchVo searchVo, Pageable pageable);

    /**
     * 通过key和latest获取
     * @param processKey
     * @return
     */
    ActProcess findByProcessKeyAndLatest(String processKey, Boolean latest);

    /**
     * 通过key设置所有版本为旧
     * @param processKey
     */
    void setAllOldByProcessKey(String processKey);

    /**
     * 通过key设置最新版本
     * @param processKey
     */
    void setLatestByProcessKey(String processKey);

    /**
     * 通过分类获取
     * @param categoryId
     * @return
     */
    List<ActProcess> findByCategoryId(String categoryId);

    /**
     * 通过流程定义id启动流程 返回流程实例id
     * @param actBusiness
     * @return
     */
    String startProcess(ActBusiness actBusiness);

    /**
     * 通过流程定义id获取第一个任务节点
     * @param procDefId
     * @return
     */
    ProcessNodeVo getFirstNode(String procDefId);

    /**
     * 获取下一节点
     * @param procDefId
     * @param procInstId
     * @return
     */
    ProcessNodeVo getNextNode(String procDefId, String procInstId);

    /**
     * 获取节点审批人
     * @param nodeId
     * @return
     */
    ProcessNodeVo getNode(String nodeId);

    /**
     * 获取节点审批人
     * @param nodeId
     * @param taskId
     * @return
     */
    ProcessNodeVo getNode(String nodeId, String taskId);

    /**
     * 更新分类名称
     * @param categoryId
     * @param categoryTitle
     */
    void updateCategoryTitle(String categoryId, String categoryTitle);
}
