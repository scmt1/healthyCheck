package com.scmt.activiti.controller;

import com.scmt.activiti.entity.ActBusiness;
import com.scmt.activiti.entity.ActProcess;
import com.scmt.activiti.service.ActBusinessService;
import com.scmt.activiti.service.ActProcessService;
import com.scmt.activiti.service.mybatis.IHistoryIdentityService;
import com.scmt.activiti.service.mybatis.IRunIdentityService;
import com.scmt.activiti.utils.MessageUtil;
import com.scmt.activiti.vo.*;
import com.scmt.core.common.constant.ActivitiConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.utils.SnowFlakeUtil;
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
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "任务管理接口")
@RequestMapping("/scmt/actTask")
@Transactional
public class ActTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private UserService userService;

    @Autowired
    private IHistoryIdentityService iHistoryIdentityService;

    @Autowired
    private IRunIdentityService iRunIdentityService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private MessageUtil messageUtil;

    @RequestMapping(value = "/todoList", method = RequestMethod.GET)
    @ApiOperation(value = "代办列表")
    public Result<Object> todoList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String categoryId,
                                   @RequestParam(required = false) Integer priority,
                                   SearchVo searchVo,
                                   PageVo pageVo) {

        ActPage<TaskVo> page = new ActPage<TaskVo>();
        List<TaskVo> list = new ArrayList<>();

        String userId = securityUtil.getCurrUser().getId();
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId);

        // 多条件搜索
        if ("createTime".equals(pageVo.getSort()) && "asc".equals(pageVo.getOrder())) {
            query.orderByTaskCreateTime().asc();
        } else if ("priority".equals(pageVo.getSort()) && "asc".equals(pageVo.getOrder())) {
            query.orderByTaskPriority().asc();
        } else if ("priority".equals(pageVo.getSort()) && "desc".equals(pageVo.getOrder())) {
            query.orderByTaskPriority().desc();
        } else {
            query.orderByTaskCreateTime().desc();
        }
        if (StrUtil.isNotBlank(name)) {
            query.taskNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(categoryId)) {
            query.taskCategory(categoryId);
        }
        if (priority != null) {
            query.taskPriority(priority);
        }
        if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
            Date start = DateUtil.parse(searchVo.getStartDate());
            Date end = DateUtil.parse(searchVo.getEndDate());
            query.taskCreatedAfter(start);
            query.taskCreatedBefore(DateUtil.endOfDay(end));
        }

        page.setTotalElements(query.count());
        int first = (pageVo.getPageNumber() - 1) * pageVo.getPageSize();
        List<Task> taskList = query.listPage(first, pageVo.getPageSize());

        // 转换vo
        taskList.forEach(e -> {
            TaskVo tv = new TaskVo(e);

            // 关联委托人
            if (StrUtil.isNotBlank(tv.getOwner())) {
                User o = userService.get(tv.getOwner());
                tv.setOwner(o.getNickname()).setOwnerUsername(o.getUsername());
            }
            List<IdentityLink> identityLinks = runtimeService.getIdentityLinksForProcessInstance(tv.getProcInstId());
            for (IdentityLink ik : identityLinks) {
                // 关联发起人
                if (IdentityLinkType.STARTER.equals(ik.getType()) && StrUtil.isNotBlank(ik.getUserId())) {
                    User s = userService.get(ik.getUserId());
                    tv.setApplyer(s.getNickname()).setApplyerUsername(s.getUsername());
                }
            }
            // 关联流程信息
            ActProcess actProcess = actProcessService.get(tv.getProcDefId());
            if (actProcess != null) {
                tv.setProcessName(actProcess.getName());
                tv.setRouteName(actProcess.getRouteName());
                tv.setVersion(actProcess.getVersion());
            }
            // 关联业务key
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(tv.getProcInstId()).singleResult();
            tv.setBusinessKey(pi.getBusinessKey());
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            if (actBusiness != null) {
                tv.setTableId(actBusiness.getTableId());
            }

            list.add(tv);
        });
        page.setContent(list);
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/doneList", method = RequestMethod.GET)
    @ApiOperation(value = "已办列表")
    public Result<Object> doneList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String categoryId,
                                   @RequestParam(required = false) Integer priority,
                                   SearchVo searchVo,
                                   PageVo pageVo) {

        ActPage<HistoricTaskVo> page = new ActPage<HistoricTaskVo>();
        List<HistoricTaskVo> list = new ArrayList<>();

        String userId = securityUtil.getCurrUser().getId();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().or().taskCandidateUser(userId).
                taskAssignee(userId).endOr().finished();

        // 多条件搜索
        if ("createTime".equals(pageVo.getSort()) && "asc".equals(pageVo.getOrder())) {
            query.orderByHistoricTaskInstanceEndTime().asc();
        } else if ("priority".equals(pageVo.getSort()) && "asc".equals(pageVo.getOrder())) {
            query.orderByTaskPriority().asc();
        } else if ("priority".equals(pageVo.getSort()) && "desc".equals(pageVo.getOrder())) {
            query.orderByTaskPriority().desc();
        } else if ("duration".equals(pageVo.getSort()) && "asc".equals(pageVo.getOrder())) {
            query.orderByHistoricTaskInstanceDuration().asc();
        } else if ("duration".equals(pageVo.getSort()) && "desc".equals(pageVo.getOrder())) {
            query.orderByHistoricTaskInstanceDuration().desc();
        } else {
            query.orderByHistoricTaskInstanceEndTime().desc();
        }
        if (StrUtil.isNotBlank(name)) {
            query.taskNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(categoryId)) {
            query.taskCategory(categoryId);
        }
        if (priority != null) {
            query.taskPriority(priority);
        }
        if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
            Date start = DateUtil.parse(searchVo.getStartDate());
            Date end = DateUtil.parse(searchVo.getEndDate());
            query.taskCompletedAfter(start);
            query.taskCompletedBefore(DateUtil.endOfDay(end));
        }

        page.setTotalElements((long) query.list().size());
        int first = (pageVo.getPageNumber() - 1) * pageVo.getPageSize();
        List<HistoricTaskInstance> taskList = query.listPage(first, pageVo.getPageSize());
        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            // 关联委托人
            if (StrUtil.isNotBlank(htv.getOwner())) {
                User o = userService.get(htv.getOwner());
                htv.setOwner(o.getNickname()).setOwnerUsername(o.getUsername());
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForProcessInstance(htv.getProcInstId());
            for (HistoricIdentityLink hik : identityLinks) {
                // 关联发起人
                if (IdentityLinkType.STARTER.equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    User s = userService.get(hik.getUserId());
                    htv.setApplyer(s.getNickname()).setApplyerUsername(s.getUsername());
                }
            }
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if (comments != null && comments.size() > 0) {
                htv.setComment(comments.get(0).getFullMessage());
            }
            // 关联流程信息
            ActProcess actProcess = actProcessService.get(htv.getProcDefId());
            if (actProcess != null) {
                htv.setProcessName(actProcess.getName());
                htv.setRouteName(actProcess.getRouteName());
                htv.setVersion(actProcess.getVersion());
            }
            // 关联业务key
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(htv.getProcInstId()).singleResult();
            htv.setBusinessKey(hpi.getBusinessKey());
            ActBusiness actBusiness = actBusinessService.get(hpi.getBusinessKey());
            if (actBusiness != null) {
                htv.setTableId(actBusiness.getTableId());
            }

            list.add(htv);
        });
        page.setContent(list);
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/historicFlow/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "流程流转历史")
    public Result<Object> historicFlow(@ApiParam("流程实例id") @PathVariable String id) {

        List<HistoricTaskVo> list = new ArrayList<>();

        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(id).orderByHistoricTaskInstanceEndTime().asc().list();

        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            List<Assignee> assignees = new ArrayList<>();
            // 关联分配人（委托用户时显示该人）
            if (StrUtil.isNotBlank(htv.getAssignee())) {
                User assignee = userService.get(htv.getAssignee());
                User owner = userService.get(htv.getOwner());
                assignees.add(new Assignee(assignee.getNickname(),
                        assignee.getUsername() + "【受 " + owner.getNickname() + "(" + owner.getUsername() + ") 委托】", true));
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(e.getId());
            // 获取实际审批用户id
            String userId = iHistoryIdentityService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE, e.getId());

            for (HistoricIdentityLink hik : identityLinks) {
                // 关联候选用户（分配的候选用户审批人）
                if (IdentityLinkType.CANDIDATE.equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    User u = userService.get(hik.getUserId());
                    Assignee assignee = new Assignee(u.getNickname(), u.getUsername(), false);
                    if (StrUtil.isNotBlank(userId) && userId.equals(hik.getUserId())) {
                        assignee.setIsExecutor(true);
                    }
                    assignees.add(assignee);
                }
            }
            htv.setAssignees(assignees);
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if (comments != null && comments.size() > 0) {
                htv.setComment(comments.get(0).getFullMessage());
            }
            list.add(htv);
        });
        return ResultUtil.data(list);
    }

    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批通过")
    public Result<Object> pass(@ApiParam("任务id") @RequestParam String id,
                               @ApiParam("流程实例id") @RequestParam String procInstId,
                               @ApiParam("下个节点审批人") @RequestParam(required = false) String[] assignees,
                               @ApiParam("优先级") @RequestParam(required = false) Integer priority,
                               @ApiParam("意见评论") @RequestParam(required = false) String comment,
                               @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                               @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                               @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {

        taskService.addComment(id, procInstId, StrUtil.isBlank(comment) ? "" : comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        if (StrUtil.isNotBlank(task.getOwner()) && !DelegationState.RESOLVED.equals(task.getDelegationState())) {
            // 未解决的委托任务 先resolve
            String oldAssignee = task.getAssignee();
            taskService.resolveTask(id);
            taskService.setAssignee(id, oldAssignee);
        }
        taskService.complete(id);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        // 判断下一个节点
        if (tasks != null && tasks.size() > 0) {
            for (Task t : tasks) {
                if (assignees == null || assignees.length < 1) {
                    // 如果下个节点未分配审批人为空 取消结束流程
                    List<User> users = actProcessService.getNode(t.getTaskDefinitionKey()).getUsers();
                    if (users == null || users.isEmpty()) {
                        runtimeService.deleteProcessInstance(procInstId, "canceled-审批节点未分配审批人，流程自动中断取消");
                        ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
                        actBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
                        actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
                        actBusinessService.update(actBusiness);
                        break;
                    } else {
                        // 避免重复添加
                        List<String> list = iRunIdentityService.selectByConditions(t.getId(), IdentityLinkType.CANDIDATE);
                        if (list == null || list.isEmpty()) {
                            // 分配了节点负责人分发给全部
                            for (User user : users) {
                                taskService.addCandidateUser(t.getId(), user.getId());
                                // 异步发消息
                                messageUtil.sendActMessage(user.getId(), ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                            }
                            taskService.setPriority(t.getId(), task.getPriority());
                        }
                    }
                } else {
                    // 避免重复添加
                    List<String> list = iRunIdentityService.selectByConditions(t.getId(), IdentityLinkType.CANDIDATE);
                    if (list == null || list.isEmpty()) {
                        for (String assignee : assignees) {
                            taskService.addCandidateUser(t.getId(), assignee);
                            // 异步发消息
                            messageUtil.sendActMessage(assignee, ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                            taskService.setPriority(t.getId(), priority);
                        }
                    }
                }
            }
        } else {
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_PASS);
            actBusinessService.update(actBusiness);
            // 异步发消息
            messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_PASS_CONTENT, sendMessage, sendSms, sendEmail);
        }
        // 记录实际审批人员
        iHistoryIdentityService.insert(SnowFlakeUtil.nextId().toString(),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/passAll", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过")
    public Result<Object> passAll(@ApiParam("任务ids") @RequestParam String[] ids,
                                  @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                  @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                  @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                  @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {

        int count = 0;
        for (String id : ids) {
            Task task = taskService.createTaskQuery().taskId(id).singleResult();
            taskService.addComment(id, task.getProcessInstanceId(), StrUtil.isBlank(comment) ? "" : comment);
            ProcessNodeVo next = actProcessService.getNextNode(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (ActivitiConstant.NODE_TYPE_CUSTOM.equals(next.getType())) {
                count++;
                continue;
            }
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if (StrUtil.isNotBlank(task.getOwner()) && !DelegationState.RESOLVED.equals(task.getDelegationState())) {
                String oldAssignee = task.getAssignee();
                taskService.resolveTask(id);
                taskService.setAssignee(id, oldAssignee);
            }
            taskService.complete(id);
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            // 判断下一个节点
            if (tasks != null && tasks.size() > 0) {
                for (Task t : tasks) {
                    List<User> users = actProcessService.getNode(t.getTaskDefinitionKey()).getUsers();
                    // 如果下个节点未分配审批人为空 取消结束流程
                    if (users == null || users.isEmpty()) {
                        runtimeService.deleteProcessInstance(pi.getId(), "canceled-审批节点未分配审批人，流程自动中断取消");
                        ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
                        actBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
                        actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
                        actBusinessService.update(actBusiness);
                        break;
                    } else {
                        // 避免重复添加
                        List<String> list = iRunIdentityService.selectByConditions(t.getId(), IdentityLinkType.CANDIDATE);
                        if (list == null || list.isEmpty()) {
                            // 分配了节点负责人分发给全部
                            for (User user : users) {
                                taskService.addCandidateUser(t.getId(), user.getId());
                                // 异步发消息
                                messageUtil.sendActMessage(user.getId(), ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                                taskService.setPriority(t.getId(), task.getPriority());
                            }
                        }
                    }
                }
            } else {
                ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
                actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
                actBusiness.setResult(ActivitiConstant.RESULT_PASS);
                actBusinessService.update(actBusiness);
                // 异步发消息
                messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_PASS_CONTENT, sendMessage, sendSms, sendEmail);
            }
            // 记录实际审批人员
            iHistoryIdentityService.insert(SnowFlakeUtil.nextId().toString(),
                    ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, pi.getId());
        }
        String customCount = "";
        if (count > 0) {
            customCount = "，跳过了" + count + "个自选审批人节点";
        }
        return ResultUtil.success("成功批量通过了" + (ids.length - count) + "条数据" + customCount);
    }

    @RequestMapping(value = "/getBackList/{procInstId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取可返回的节点")
    public Result<Object> getBackList(@PathVariable String procInstId) {

        List<HistoricTaskVo> list = new ArrayList<>();
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId)
                .finished().list();

        taskInstanceList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            list.add(htv);
        });

        // 根据任务key去重
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        List<HistoricTaskVo> newList = new ArrayList<>();
        list.forEach(e -> {
            if (set.add(e.getKey())) {
                newList.add(e);
            }
        });

        return ResultUtil.data(newList);
    }

    @RequestMapping(value = "/backToTask", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批驳回至指定历史节点")
    public Result<Object> backToTask(@ApiParam("任务id") @RequestParam String id,
                                     @ApiParam("驳回指定节点key") @RequestParam String backTaskKey,
                                     @ApiParam("流程实例id") @RequestParam String procInstId,
                                     @ApiParam("流程定义id") @RequestParam String procDefId,
                                     @ApiParam("原节点审批人") @RequestParam(required = false) String[] assignees,
                                     @ApiParam("优先级") @RequestParam(required = false) Integer priority,
                                     @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                     @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                     @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                     @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {


        judgeParallelGateway(procDefId);
        taskService.addComment(id, procInstId, StrUtil.isBlank(comment) ? "" : comment);
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
        // 获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(backTaskKey);
        // 实现跳转
        managementService.executeCommand(new JumpTask(procInstId, hisActivity.getId()));
        // 重新分配原节点审批人
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (tasks != null && tasks.size() > 0) {
            tasks.forEach(e -> {
                for (String assignee : assignees) {
                    taskService.addCandidateUser(e.getId(), assignee);
                    // 异步发消息
                    messageUtil.sendActMessage(assignee, ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                }
                if (priority != null) {
                    taskService.setPriority(e.getId(), priority);
                }
            });
        }
        // 记录实际审批人员
        iHistoryIdentityService.insert(SnowFlakeUtil.nextId().toString(),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return ResultUtil.success("操作成功");
    }

    public void judgeParallelGateway(String procDefId) {

        ProcessDefinitionEntity dfe = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(procDefId);
        // 判断流程所有节点是否包含平行网关
        for (ActivityImpl activityImpl : dfe.getActivities()) {
            String type = activityImpl.getProperty("type").toString();
            if ("parallelGateway".equals(type)) {
                throw new ScmtException("流程设计中包含平行网关，暂不支持驳回（仅支持驳回至发起人）");
            }
        }
    }

    @RequestMapping(value = "/back", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批驳回至发起人")
    public Result<Object> back(@ApiParam("任务id") @RequestParam String id,
                               @ApiParam("流程实例id") @RequestParam String procInstId,
                               @ApiParam("意见评论") @RequestParam(required = false) String comment,
                               @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                               @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                               @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {


        taskService.addComment(id, procInstId, StrUtil.isBlank(comment) ? "" : comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        // 删除流程实例
        runtimeService.deleteProcessInstance(procInstId, ActivitiConstant.BACKED_FLAG);
        ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
        actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
        actBusiness.setResult(ActivitiConstant.RESULT_FAIL);
        actBusinessService.update(actBusiness);
        // 异步发消息
        messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_BACK_CONTENT, sendMessage, sendSms, sendEmail);
        // 记录实际审批人员
        iHistoryIdentityService.insert(SnowFlakeUtil.nextId().toString(),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/backAll", method = RequestMethod.POST)
    @ApiOperation(value = "批量驳回至发起人")
    public Result<Object> backAll(@ApiParam("流程实例ids") @RequestParam String[] procInstIds,
                                  @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                  @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                  @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                  @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {

        for (String procInstId : procInstIds) {
            // 记录实际审批人员
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
            tasks.forEach(t -> {
                taskService.addComment(t.getId(), procInstId, StrUtil.isBlank(comment) ? "" : comment);
                iHistoryIdentityService.insert(SnowFlakeUtil.nextId().toString(),
                        ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), t.getId(), procInstId);
            });
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
            // 删除流程实例
            try {
                runtimeService.deleteProcessInstance(procInstId, ActivitiConstant.BACKED_FLAG);
            } catch (Exception e) {
                throw new ScmtException("请确保无重复所属的流程，或尝试对单条数据进行驳回");
            }
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_FAIL);
            actBusinessService.update(actBusiness);
            // 异步发消息
            messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_BACK_CONTENT, sendMessage, sendSms, sendEmail);
        }
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/delegate", method = RequestMethod.POST)
    @ApiOperation(value = "委托他人代办")
    public Result<Object> delegate(@ApiParam("任务id") @RequestParam String id,
                                   @ApiParam("委托用户id") @RequestParam String userId,
                                   @ApiParam("流程实例id") @RequestParam String procInstId,
                                   @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                   @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                   @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                   @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail) {

        taskService.addComment(id, procInstId, StrUtil.isBlank(comment) ? "" : comment);
        taskService.delegateTask(id, userId);
        taskService.setOwner(id, securityUtil.getCurrUser().getId());
        // 异步发消息
        messageUtil.sendActMessage(userId, ActivitiConstant.MESSAGE_DELEGATE_CONTENT, sendMessage, sendSms, sendEmail);
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/deleteHistoric", method = RequestMethod.POST)
    @ApiOperation(value = "删除任务历史")
    public Result<Object> deleteHistoric(@ApiParam("任务id") @RequestParam String[] ids) {

        for (String id : ids) {
            historyService.deleteHistoricTaskInstance(id);
        }
        return ResultUtil.success("操作成功");
    }

    public class JumpTask implements Command<ExecutionEntity> {

        private String procInstId;
        private String activityId;

        public JumpTask(String procInstId, String activityId) {
            this.procInstId = procInstId;
            this.activityId = activityId;
        }

        @Override
        public ExecutionEntity execute(CommandContext commandContext) {

            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(procInstId);
            executionEntity.destroyScope(ActivitiConstant.BACKED_FLAG);
            ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
            ActivityImpl activity = processDefinition.findActivity(activityId);
            executionEntity.executeActivity(activity);

            return executionEntity;
        }
    }
}
