package com.scmt.activiti.controller;

import com.scmt.activiti.controller.modeler.DefaultProcessDiagramGenerator;
import com.scmt.activiti.entity.*;
import com.scmt.activiti.properties.ActivitiExtendProperties;
import com.scmt.activiti.service.*;
import com.scmt.activiti.vo.ProcessNodeVo;
import com.scmt.core.common.constant.ActivitiConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.core.entity.UserRole;
import com.scmt.core.service.UserRoleService;
import com.scmt.core.service.UserService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "流程定义管理接口")
@RequestMapping("/scmt/actProcess")
@Transactional
public class ActProcessController {

    @Autowired
    private ActivitiExtendProperties properties;

    @Autowired
    private ActModelService actModelService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ActNodeService actNodeService;

    @Autowired
    private ActCategoryService actCategoryService;

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private ActStarterService actStarterService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取流程列表")
    public Result<Object> getByCondition(@ApiParam("是否显示所有版本") @RequestParam(required = false) Boolean showLatest,
                                         @ApiParam("是否开启发起人过滤") @RequestParam(required = false, defaultValue = "false") Boolean filter,
                                         ActProcess actProcess,
                                         SearchVo searchVo,
                                         PageVo pageVo) {

        Page<ActProcess> page = actProcessService.findByCondition(showLatest, actProcess, searchVo, PageUtil.initPage(pageVo));
        User user = securityUtil.getCurrUser();
        Set<String> processes = actStarterService.findByUserId(user.getId());
        List<ActProcess> content = new ArrayList<>();

        for (ActProcess ap : page.getContent()) {
            // 是否开启过滤 若开启：判断是否所有人可见 如果不是：则过滤当前用户拥有的
            if (filter) {
                if (ap.getAllUser() != null && !ap.getAllUser()) {
                    // 过滤
                    if (processes.contains(ap.getId())) {
                        content.add(ap);
                    }
                } else {
                    content.add(ap);
                }
            }
        }
        if (filter) {
            Map<Object, Object> result = new HashMap<>(16);
            result.put("content", content);
            result.put("totalPages", page.getTotalPages());
            return ResultUtil.data(result);
        }
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/getByKey/{key}", method = RequestMethod.GET)
    @ApiOperation(value = "通过key获取最新流程")
    public Result<ActProcess> getByCondition(@PathVariable String key) {

        ActProcess actProcess = actProcessService.findByProcessKeyAndLatest(key, true);
        return new ResultUtil<ActProcess>().setData(actProcess);
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    @ApiOperation(value = "修改关联路由表单分类或备注")
    public Result<Object> updateInfo(ActProcess actProcess) {

        ActProcess old = actProcessService.get(actProcess.getId());
        if (old == null) {
            return ResultUtil.error("流程信息不存在");
        }
        ProcessDefinition pd = repositoryService.getProcessDefinition(actProcess.getId());
        if (pd == null) {
            return ResultUtil.error("流程定义不存在");
        }
        PageUtil.SQLInject(actProcess.getBusinessTable());
        repositoryService.setProcessDefinitionCategory(actProcess.getId(), actProcess.getCategoryId());
        repositoryService.setDeploymentCategory(pd.getDeploymentId(), actProcess.getCategoryId());
        old.setRouteName(actProcess.getRouteName());
        old.setBusinessTable(actProcess.getBusinessTable());
        old.setDescription(actProcess.getDescription());
        if (StrUtil.isNotBlank(actProcess.getCategoryId())) {
            ActCategory c = actCategoryService.get(actProcess.getCategoryId());
            if (c != null) {
                old.setCategoryId(actProcess.getCategoryId());
                old.setCategoryTitle(c.getTitle());
            }
        } else {
            old.setCategoryId(null);
            old.setCategoryTitle("");
        }
        actProcessService.update(old);
        return ResultUtil.data("修改成功");
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "激活或挂起流程定义")
    public Result<Object> updateStatus(@ApiParam("流程定义id") @RequestParam String id,
                                       @RequestParam Integer status) {

        if (ActivitiConstant.PROCESS_STATUS_ACTIVE.equals(status)) {
            repositoryService.activateProcessDefinitionById(id, true, new Date());
        } else if (ActivitiConstant.PROCESS_STATUS_SUSPEND.equals(status)) {
            repositoryService.suspendProcessDefinitionById(id, true, new Date());
        }

        ActProcess actProcess = actProcessService.get(id);
        actProcess.setStatus(status);
        actProcessService.update(actProcess);
        return ResultUtil.data("修改成功");
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出部署流程资源")
    public void exportResource(@ApiParam("流程定义id") @RequestParam String id,
                               @ApiParam("0XML 1图片") @RequestParam Integer type,
                               HttpServletResponse response) {

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(id).singleResult();

        String resourceName;
        InputStream inputStream;
        if (ActivitiConstant.RESOURCE_TYPE_XML.equals(type)) {
            resourceName = pd.getResourceName();
            inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);
        } else if (ActivitiConstant.RESOURCE_TYPE_IMAGE.equals(type)) {
            resourceName = pd.getDiagramResourceName();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
            ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            inputStream = diagramGenerator.generateDiagram(bpmnModel, "png",
                    properties.getActivityFontName(), properties.getLabelFontName(), properties.getLabelFontName(), null, 1.0);
        } else {
            throw new ScmtException("类型type不正确");
        }

        try {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(resourceName, "UTF-8"));
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.toString());
            throw new ScmtException("导出部署流程资源失败");
        }
    }

    @RequestMapping(value = "/convertToModel/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "转化流程为模型")
    public Result<Object> convertToModel(@ApiParam("流程定义id") @PathVariable String id) {

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
        ActProcess actProcess = actProcessService.get(id);

        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            // 避免XXE攻击
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            BpmnJsonConverter converter = new BpmnJsonConverter();

            ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model modelData = repositoryService.newModel();
            modelData.setKey(pd.getKey());
            modelData.setName(pd.getResourceName());

            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, actProcess.getName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, actProcess.getDescription());
            modelData.setMetaInfo(modelObjectNode.toString());

            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

            // 保存扩展模型至数据库
            ActModel actModel = new ActModel();
            actModel.setId(modelData.getId());
            actModel.setName(modelData.getName());
            actModel.setModelKey(modelData.getKey());
            actModel.setDescription(actProcess.getDescription());
            actModel.setVersion(modelData.getVersion());
            actModelService.save(actModel);
        } catch (Exception e) {
            log.error(e.toString());
            return ResultUtil.error("转化流程为模型失败");
        }
        return ResultUtil.data("修改成功");
    }

    @RequestMapping(value = "/getProcessNode/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程定义id获取流程节点")
    public Result<List<ProcessNodeVo>> getProcessNode(@ApiParam("流程定义id") @PathVariable String id) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);

        List<ProcessNodeVo> list = new ArrayList<>();

        List<Process> processes = bpmnModel.getProcesses();
        if (processes == null || processes.isEmpty()) {
            return new ResultUtil<List<ProcessNodeVo>>().setData(null);
        }
        for (Process process : processes) {
            Collection<FlowElement> elements = process.getFlowElements();
            for (FlowElement element : elements) {
                ProcessNodeVo node = new ProcessNodeVo();
                node.setId(element.getId());
                node.setTitle(element.getName());
                if (element instanceof StartEvent) {
                    // 开始节点
                    node.setType(ActivitiConstant.NODE_TYPE_START);
                    // 设置关联用户
                    node.setUsers(actNodeService.findUserByNodeId(id));
                    // 设置关联角色
                    node.setRoles(actNodeService.findRoleByNodeId(id));
                    // 设置关联部门
                    node.setDepartments(actNodeService.findDepartmentByNodeId(id));
                } else if (element instanceof UserTask) {
                    // 用户任务
                    node.setType(ActivitiConstant.NODE_TYPE_TASK);
                    // 设置关联用户
                    node.setUsers(actNodeService.findUserByNodeId(element.getId()));
                    // 设置关联角色
                    node.setRoles(actNodeService.findRoleByNodeId(element.getId()));
                    // 设置关联部门
                    node.setDepartments(actNodeService.findDepartmentByNodeId(element.getId()));
                    // 是否设置操作人负责人
                    node.setChooseDepHeader(actNodeService.hasChooseDepHeader(element.getId()));
                    // 是否设置操作人负责人
                    node.setCustomUser(actNodeService.hasCustomUser(element.getId()));
                } else if (element instanceof EndEvent) {
                    // 结束
                    node.setType(ActivitiConstant.NODE_TYPE_END);
                } else {
                    // 排除其他连线或节点
                    continue;
                }
                list.add(node);
            }
        }
        return new ResultUtil<List<ProcessNodeVo>>().setData(list);
    }

    @RequestMapping(value = "/editStartUser", method = RequestMethod.POST)
    @ApiOperation(value = "编辑流程可发起用户")
    public Result<Object> editStartUser(@ApiParam("流程定义id") @RequestParam String nodeId,
                                        @ApiParam("是否所有用户可见") @RequestParam Boolean allUser,
                                        @RequestParam(required = false) String[] userIds,
                                        @RequestParam(required = false) String[] roleIds,
                                        @RequestParam(required = false) String[] departmentIds) {

        // 删除其关联权限
        actNodeService.deleteByNodeId(nodeId);
        actStarterService.deleteByProcessDefId(nodeId);
        ActProcess actProcess = actProcessService.get(nodeId);
        if (actProcess == null) {
            return ResultUtil.error("流程不存在");
        }
        actProcess.setAllUser(allUser);
        actProcessService.update(actProcess);
        if (!allUser) {
            HashSet<String> starters = new HashSet<>();
            List<ActStarter> listStarter = new ArrayList<>();
            List<ActNode> listNode = new ArrayList<>();
            // 分配新用户
            if (userIds != null) {
                for (String userId : userIds) {
                    // 保存用户
                    if (!starters.contains(userId)) {
                        ActStarter actStarter = new ActStarter().setProcessDefId(nodeId).setUserId(userId);
                        listStarter.add(actStarter);
                        starters.add(userId);
                    }
                    // 保存配置
                    ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(userId).setType(ActivitiConstant.NODE_USER);
                    listNode.add(actNode);
                }
            }
            // 分配新角色的用户
            if (roleIds != null) {
                for (String roleId : roleIds) {
                    // 通过角色获取用户
                    List<UserRole> userRoles = userRoleService.findByRoleId(roleId);
                    for (UserRole ur : userRoles) {
                        // 保存用户
                        if (!starters.contains(ur.getUserId())) {
                            ActStarter actStarter = new ActStarter().setProcessDefId(nodeId).setUserId(ur.getUserId());
                            listStarter.add(actStarter);
                            starters.add(ur.getUserId());
                        }
                    }
                    ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(roleId).setType(ActivitiConstant.NODE_ROLE);
                    listNode.add(actNode);
                }
            }
            // 分配新部门
            if (departmentIds != null) {
                for (String departmentId : departmentIds) {
                    // 通过部门获取用户
                    List<User> users = userService.findByDepartmentId(departmentId);
                    for (User u : users) {
                        // 保存用户
                        if (!starters.contains(u.getId())) {
                            ActStarter actStarter = new ActStarter().setProcessDefId(nodeId).setUserId(u.getId());
                            listStarter.add(actStarter);
                            starters.add(u.getId());
                        }
                    }
                    ;
                    ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(departmentId).setType(ActivitiConstant.NODE_DEPARTMENT);
                    listNode.add(actNode);
                }
            }
            // 批量保存
            actStarterService.saveOrUpdateAll(listStarter);
            actNodeService.saveOrUpdateAll(listNode);
            // GC
            starters = null;
        }
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/editNodeUser", method = RequestMethod.POST)
    @ApiOperation(value = "编辑节点分配用户")
    public Result<Object> editNodeUser(@RequestParam String nodeId,
                                       @RequestParam(required = false) String[] userIds,
                                       @RequestParam(required = false) String[] roleIds,
                                       @RequestParam(required = false) String[] departmentIds,
                                       @ApiParam("是否勾选操连续多级部门负责人") @RequestParam(required = false) Boolean chooseDepHeader,
                                       @ApiParam("是否勾选用户自选") @RequestParam(required = false) Boolean customUser) {

        // 删除其关联数据
        actNodeService.deleteByNodeId(nodeId);
        if (customUser != null && customUser) {
            ActNode actNode = new ActNode().setNodeId(nodeId).setType(ActivitiConstant.NODE_CUSTOM);
            actNodeService.save(actNode);
            return ResultUtil.success("操作成功");
        }
        List<ActNode> list = new ArrayList<>();
        // 分配新用户
        if (userIds != null) {
            for (String userId : userIds) {
                ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(userId).setType(ActivitiConstant.NODE_USER);
                list.add(actNode);
            }
        }
        // 分配新角色
        if (roleIds != null) {
            for (String roleId : roleIds) {
                ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(roleId).setType(ActivitiConstant.NODE_ROLE);
                list.add(actNode);
            }
        }
        // 分配新部门
        if (departmentIds != null) {
            for (String departmentId : departmentIds) {
                ActNode actNode = new ActNode().setNodeId(nodeId).setRelateId(departmentId).setType(ActivitiConstant.NODE_DEPARTMENT);
                list.add(actNode);
            }
        }
        if (chooseDepHeader != null && chooseDepHeader) {
            ActNode actNode = new ActNode().setNodeId(nodeId).setType(ActivitiConstant.NODE_DEP_HEADER);
            list.add(actNode);
        }
        // 批量保存
        actNodeService.saveOrUpdateAll(list);
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "通过id删除流程")
    public Result<Object> delByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            if (CollectionUtil.isNotEmpty(actBusinessService.findByProcDefId(id))) {
                return ResultUtil.error("包含已发起申请的流程，无法删除");
            }
            ActProcess actProcess = actProcessService.get(id);
            // 当删除最后一个版本时 删除关联数据
            if (actProcess.getVersion() == 1) {
                deleteNodeUsers(id);
            }
            // 级联删除
            repositoryService.deleteDeployment(actProcess.getDeploymentId(), true);
            actProcessService.delete(id);
            // 更新最新版本
            actProcessService.setLatestByProcessKey(actProcess.getProcessKey());
        }
        return ResultUtil.data("删除成功");
    }

    public void deleteNodeUsers(String processId) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processId);
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            Collection<FlowElement> elements = process.getFlowElements();
            for (FlowElement element : elements) {
                actNodeService.deleteByNodeId(element.getId());
            }
        }
    }
}
