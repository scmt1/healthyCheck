package com.scmt.activiti.controller;

import com.scmt.activiti.controller.modeler.DefaultProcessDiagramGenerator;
import com.scmt.activiti.entity.ActBusiness;
import com.scmt.activiti.entity.ActProcess;
import com.scmt.activiti.properties.ActivitiExtendProperties;
import com.scmt.activiti.service.ActBusinessService;
import com.scmt.activiti.service.ActProcessService;
import com.scmt.activiti.vo.ActPage;
import com.scmt.activiti.vo.HistoricProcessInsVo;
import com.scmt.activiti.vo.ProcessInsVo;
import com.scmt.activiti.vo.ProcessNodeVo;
import com.scmt.core.common.constant.ActivitiConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.core.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "流程实例管理接口")
@RequestMapping("/scmt/actProcess")
@Transactional
public class ActProcessInsController {

    @Autowired
    private ActivitiExtendProperties properties;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @RequestMapping(value = "/getRunningProcess", method = RequestMethod.GET)
    @ApiOperation(value = "获取运行中的流程实例")
    public Result<Object> getRunningProcess(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String categoryId,
                                            @RequestParam(required = false) String key,
                                            PageVo pageVo) {

        ActPage<ProcessInsVo> page = new ActPage<ProcessInsVo>();
        List<ProcessInsVo> list = new ArrayList<>();

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId().desc();

        if (StrUtil.isNotBlank(name)) {
            query.processInstanceNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(categoryId)) {
            query.processDefinitionCategory(categoryId);
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKey(key);
        }

        page.setTotalElements(query.count());
        int first = (pageVo.getPageNumber() - 1) * pageVo.getPageSize();
        List<ProcessInstance> processInstanceList = query.listPage(first, pageVo.getPageSize());
        processInstanceList.forEach(e -> {
            list.add(new ProcessInsVo(e));
        });
        list.forEach(e -> {
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForProcessInstance(e.getId());
            for (HistoricIdentityLink hik : identityLinks) {
                // 关联发起人
                if (IdentityLinkType.STARTER.equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    User s = userService.get(hik.getUserId());
                    e.setApplyer(s.getNickname()).setApplyerUsername(s.getUsername());
                }
            }
            // 关联当前任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(e.getProcInstId()).list();
            if (taskList != null && taskList.size() == 1) {
                e.setCurrTaskName(taskList.get(0).getName());
            } else if (taskList != null && taskList.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < taskList.size() - 1; i++) {
                    sb.append(taskList.get(i).getName() + "、");
                }
                sb.append(taskList.get(taskList.size() - 1).getName());
                e.setCurrTaskName(sb.toString());
            }
            // 关联流程表单路由
            ActProcess actProcess = actProcessService.get(e.getProcDefId());
            if (actProcess != null) {
                e.setRouteName(actProcess.getRouteName());
            }
            // 关联业务表id
            ActBusiness actBusiness = actBusinessService.get(e.getBusinessKey());
            if (actBusiness != null) {
                e.setTableId(actBusiness.getTableId());
            }
        });
        page.setContent(list);
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/getFinishedProcess", method = RequestMethod.GET)
    @ApiOperation(value = "获取结束的的流程实例")
    public Result<Object> getFinishedProcess(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String categoryId,
                                             @RequestParam(required = false) String key,
                                             SearchVo searchVo,
                                             PageVo pageVo) {

        ActPage<HistoricProcessInsVo> page = new ActPage<HistoricProcessInsVo>();
        List<HistoricProcessInsVo> list = new ArrayList<>();

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().finished().
                orderByProcessInstanceEndTime().desc();

        if (StrUtil.isNotBlank(name)) {
            query.processInstanceNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(categoryId)) {
            query.processDefinitionCategory(categoryId);
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKey(key);
        }
        if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
            Date start = DateUtil.parse(searchVo.getStartDate());
            Date end = DateUtil.parse(searchVo.getEndDate());
            query.finishedAfter(start);
            query.finishedBefore(DateUtil.endOfDay(end));
        }

        page.setTotalElements(query.count());
        int first = (pageVo.getPageNumber() - 1) * pageVo.getPageSize();
        List<HistoricProcessInstance> processInstanceList = query.listPage(first, pageVo.getPageSize());
        processInstanceList.forEach(e -> {
            list.add(new HistoricProcessInsVo(e));
        });
        list.forEach(e -> {
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForProcessInstance(e.getId());
            for (HistoricIdentityLink hik : identityLinks) {
                // 关联发起人
                if (IdentityLinkType.STARTER.equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    User s = userService.get(hik.getUserId());
                    e.setApplyer(s.getNickname()).setApplyerUsername(s.getUsername());
                }
            }
            // 关联流程表单路由
            ActProcess actProcess = actProcessService.get(e.getProcDefId());
            if (actProcess != null) {
                e.setRouteName(actProcess.getRouteName());
            }
            // 关联业务表id和结果
            ActBusiness actBusiness = actBusinessService.get(e.getBusinessKey());
            if (actBusiness != null) {
                e.setTableId(actBusiness.getTableId());
                String reason = e.getDeleteReason();
                if (reason == null) {
                    e.setResult(ActivitiConstant.RESULT_PASS);
                } else if (reason.contains(ActivitiConstant.CANCEL_PRE)) {
                    e.setResult(ActivitiConstant.RESULT_CANCEL);
                    if (reason.length() > 9) {
                        e.setDeleteReason(reason.substring(9));
                    } else {
                        e.setDeleteReason("");
                    }
                } else if (ActivitiConstant.BACKED_FLAG.equals(reason)) {
                    e.setResult(ActivitiConstant.RESULT_FAIL);
                    e.setDeleteReason("");
                } else if (reason.contains(ActivitiConstant.DELETE_PRE)) {
                    e.setResult(ActivitiConstant.RESULT_DELETED);
                    if (reason.length() > 8) {
                        e.setDeleteReason(reason.substring(8));
                    } else {
                        e.setDeleteReason("");
                    }
                } else {
                    e.setResult(ActivitiConstant.RESULT_PASS);
                }
            }
        });
        page.setContent(list);
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/getFirstNode/{procDefId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程定义id获取第一个任务节点")
    public Result<ProcessNodeVo> getFirstNode(@ApiParam("流程定义id") @PathVariable String procDefId) {

        ProcessNodeVo node = actProcessService.getFirstNode(procDefId);
        return new ResultUtil<ProcessNodeVo>().setData(node);
    }

    @RequestMapping(value = "/getNextNode/{procDefId}/{currActId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过当前节点定义id获取下一个节点")
    public Result<ProcessNodeVo> getNextNode(@ApiParam("当前节点定义id") @PathVariable String procDefId,
                                             @ApiParam("当前节点定义id") @PathVariable String currActId) {

        ProcessNodeVo node = actProcessService.getNextNode(procDefId, currActId);
        return new ResultUtil<ProcessNodeVo>().setData(node);
    }

    @RequestMapping(value = "/getNode/{nodeId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过节点nodeId获取审批人")
    public Result<ProcessNodeVo> getNode(@ApiParam("节点nodeId") @PathVariable String nodeId,
                                         @ApiParam("任务taskId") @RequestParam(required = false) String taskId) {

        ProcessNodeVo node = actProcessService.getNode(nodeId, taskId);
        return new ResultUtil<ProcessNodeVo>().setData(node);
    }

    @RequestMapping(value = "/getHighlightImg/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取高亮实时流程图")
    public void getHighlightImg(@ApiParam("流程实例id") @PathVariable String id,
                                HttpServletResponse response) {

        InputStream inputStream;
        ProcessInstance pi;
        String picName;
        // 查询历史
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
        if (hpi.getEndTime() != null) {
            // 已经结束流程获取原图
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(hpi.getProcessDefinitionId()).singleResult();
            picName = pd.getDiagramResourceName();

            BpmnModel bpmnModel = repositoryService.getBpmnModel(hpi.getProcessDefinitionId());
            ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            inputStream = diagramGenerator.generateDiagram(bpmnModel, "png",
                    properties.getActivityFontName(), properties.getLabelFontName(), properties.getLabelFontName(), null, 1.0);
        } else {
            pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());

            List<String> highLightedActivities = new ArrayList<String>();
            // 高亮任务节点
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(id).list();
            for (Task task : tasks) {
                highLightedActivities.add(task.getTaskDefinitionKey());
            }

            List<String> highLightedFlows = new ArrayList<String>();
            ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows,
                    properties.getActivityFontName(), properties.getLabelFontName(), properties.getLabelFontName(), null, 1.0);
            picName = pi.getName() + ".png";
        }
        try {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(picName, "UTF-8"));
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.toString());
            throw new ScmtException("读取流程图片失败");
        }
    }

    @RequestMapping(value = "/updateInsStatus", method = RequestMethod.POST)
    @ApiOperation(value = "激活或挂起流程实例")
    public Result<Object> updateStatus(@ApiParam("流程实例id") @RequestParam String id,
                                       @RequestParam Integer status) {

        if (ActivitiConstant.PROCESS_STATUS_ACTIVE.equals(status)) {
            runtimeService.activateProcessInstanceById(id);
        } else if (ActivitiConstant.PROCESS_STATUS_SUSPEND.equals(status)) {
            runtimeService.suspendProcessInstanceById(id);
        }

        return ResultUtil.data("修改成功");
    }

    @RequestMapping(value = "/delInsByIds", method = RequestMethod.POST)
    @ApiOperation(value = "通过id删除运行中的实例")
    public Result<Object> delInsByIds(@RequestParam String[] ids,
                                      @RequestParam(required = false) String reason) {

        if (StrUtil.isBlank(reason)) {
            reason = "";
        }
        for (String id : ids) {
            // 关联业务状态结束
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            actBusiness.setStatus(ActivitiConstant.STATUS_TO_APPLY);
            actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
            actBusinessService.update(actBusiness);
            runtimeService.deleteProcessInstance(id, ActivitiConstant.DELETE_PRE + reason);
        }
        return ResultUtil.data("删除成功");
    }

    @RequestMapping(value = "/delHistoricInsByIds", method = RequestMethod.POST)
    @ApiOperation(value = "通过id删除已结束的实例")
    public Result<Object> delHistoricInsByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            historyService.deleteHistoricProcessInstance(id);
        }
        return ResultUtil.data("删除成功");
    }

}
