package com.scmt.healthy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.User;
import com.scmt.core.service.UserService;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.DocUtil;
import com.scmt.healthy.utils.PdfUtil;
import com.scmt.healthy.utils.UploadFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = " 团检订单数据接口")
@RequestMapping("/scmt/tGroupOrder")
public class TGroupOrderController {
    @Autowired
    private ITGroupOrderService tGroupOrderService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITOrderGroupService itOrderGroupService;
    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;
    @Autowired
    private ITBaseProjectService itBaseProjectService;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;
    @Autowired
    private ITOrderGroupItemProjectService itOrderGroupItemProjectService;
    @Autowired
    private ITPortfolioProjectService portProjectService;
    @Autowired
    private ITOrderFlowService tOrderFlowService;
    @Autowired
    private ITGroupPersonService itGroupPersonService;
    @Autowired
    private ITReviewPersonService itReviewPersonService;
    @Autowired
    private ITGroupPersonService tGroupPersonService;

    @Autowired
    private ITDepartResultService itDepartResultService;

    @Autowired
    private ITGroupUnitService tGroupUnitService;

    @Autowired
    private ITComboService tComboService;

    @Autowired
    private UserService userService;

    @Autowired
    private ITComboItemService tComboItemService;

    @Autowired
    private ITPortfolioProjectService tPortfolioProjectService;

    @Autowired
    private ITOrderGroupService tOrderGroupService;

    @Autowired
    public SocketConfig socketConfig;

    /**
     * 功能描述：新增团检订单数据
     *
     * @param tGroupOrder 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增团检订单数据", type = LogType.OPERATION)
    @ApiOperation("新增团检订单数据")
    @PostMapping("addTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> addTGroupOrder(@RequestBody TGroupOrder tGroupOrder) {
        try {

//            if (tGroupOrder.getGroupData() == null || tGroupOrder.getGroupData().size() < 1) {
//                return ResultUtil.error("分组信息不能为空！");
//            }
//            for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
//                if (groupDatum.getProjectData() == null || groupDatum.getProjectData().size() < 1) {
//                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检项目不能为空！");
//                }
//                if (groupDatum.getPersonCount() == null || groupDatum.getPersonCount() < 1) {
//                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检人数不能为空！");
//                }
//            }
            tGroupOrder.setPayStatus(0);
            tGroupOrder.setDelFlag(0);
            tGroupOrder.setCreateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setCreateTime(new Date());
            tGroupOrder.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            TGroupOrder one = tGroupOrderService.getOneByWhere(securityUtil.getCurrUser().getDepartmentId());
            String orderCode = "";
            if (one == null) {
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += "0001";
            } else {
                String substring = one.getOrderCode().substring(one.getOrderCode().length() - 4);
                int i = Integer.parseInt(substring);
                i += 1;
                String code = String.valueOf(i);
                if (code.length() == 1) {
                    code = "000" + code;
                } else if (code.length() == 2) {
                    code = "00" + code;
                } else if (code.length() == 3) {
                    code = "0" + code;
                }
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += code;
            }
            tGroupOrder.setOrderCode(orderCode);
            boolean res = tGroupOrderService.save(tGroupOrder);
            if (res) {

                if (tGroupOrder.getGroupData() != null && tGroupOrder.getGroupData().size() > 0) {
                    //保存订单分组信息
                    List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                    List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                    for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                        groupDatum.setGroupOrderId(tGroupOrder.getId());
                        groupDatum.setDelFlag(0);
                        groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                        groupDatum.setCreateTime(new Date());
                        boolean save = itOrderGroupService.save(groupDatum);
                        if (save) {
                            if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                                for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                    projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                    projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                    projectDatum.setCreateTime(new Date());
                                    projectDatum.setDelFlag(0);
                                    projectDatum.setGroupId(groupDatum.getId());
                                    projectDatum.setGroupOrderId(tGroupOrder.getId());
                                    projectDatum.setStatus(0);
                                    //保存分组项目
                                    boolean save1 = itOrderGroupItemService.save(projectDatum);
                                    if (save1) {
                                        //保存分组项目的子项目
                                        List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                                .filter(item -> item.getPortfolioProjectId().equals(projectDatum.getPortfolioProjectId()))
                                                .collect(Collectors.toList());
                                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                        for (RelationBasePortfolio relationBasePortfolio : collect) {
                                            TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                            if(tBaseProject != null) {
                                                List<TOrderGroupItemProject> collect1 =
                                                        projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(projectDatum.getId())
                                                                && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                                if (collect1.size() > 0) {
                                                    continue;
                                                }
                                                TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                tOrderGroupItemProject.setName(tBaseProject.getName());
                                                tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                                tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                projectArrayList.add(tOrderGroupItemProject);
                                            }
                                        }
                                        itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                    }
                                }
                            }
                        }
                    }
                }
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：新增团检订单数据
     *
     * @param tGroupOrder 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增团检订单数据(app端)", type = LogType.OPERATION)
    @ApiOperation("新增团检订单数据(app端)")
    @PostMapping("addTGroupOrderApp")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> addTGroupOrderApp(@RequestBody TGroupOrder tGroupOrder) {
        try {
            //获取当前用户信息
            User usernNow = userService.findByMobile(tGroupOrder.getMobile());
            //判断是否新增企业信息
            QueryWrapper<TGroupUnit> tGroupUnitQueryWrapper = new QueryWrapper<>();
            tGroupUnitQueryWrapper.eq("del_flag",0);
            tGroupUnitQueryWrapper.eq("name",tGroupOrder.getUnitName());
            tGroupUnitQueryWrapper.eq("uscc",tGroupOrder.getUsccCode());
            tGroupUnitQueryWrapper.eq("physical_type",tGroupOrder.getPhysicalType());
            TGroupUnit tGroupUnitOne = tGroupUnitService.getOne(tGroupUnitQueryWrapper);
            if(tGroupUnitOne == null){//未查询到该企业信息，则直接新增
                TGroupUnit tGroupUnitNew = new TGroupUnit();
                tGroupUnitNew.setDelFlag(0);
                tGroupUnitNew.setCreateId(usernNow.getId());
                tGroupUnitNew.setCreateTime(new Date());
                tGroupUnitNew.setName(tGroupOrder.getUnitName());
                tGroupUnitNew.setUscc(tGroupOrder.getUsccCode());
                tGroupUnitNew.setPhysicalType(tGroupOrder.getPhysicalType());
                tGroupUnitNew.setLinkMan2(usernNow.getUsername());
                tGroupUnitNew.setLinkPhone2(usernNow.getMobile());
                tGroupUnitService.save(tGroupUnitNew);
                tGroupUnitOne = tGroupUnitService.getOne(tGroupUnitQueryWrapper);
            }
            //新增订单信息
            tGroupOrder.setGroupUnitId(tGroupUnitOne.getId());
            tGroupOrder.setGroupUnitName(tGroupUnitOne.getName());
            tGroupOrder.setAuditState(0);
            tGroupOrder.setSporadicPhysical(0);
            tGroupOrder.setPayStatus(0);
            tGroupOrder.setDelFlag(0);
            tGroupOrder.setCreateId(usernNow.getId());
            tGroupOrder.setCreateTime(new Date());
            tGroupOrder.setDepartmentId(usernNow.getDepartmentId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            TGroupOrder one = tGroupOrderService.getOneByWhere(usernNow.getDepartmentId());
            String orderCode = "";
            if (one == null) {
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += "0001";
            } else {
                String substring = one.getOrderCode().substring(one.getOrderCode().length() - 4);
                int i = Integer.parseInt(substring);
                i += 1;
                String code = String.valueOf(i);
                if (code.length() == 1) {
                    code = "000" + code;
                } else if (code.length() == 2) {
                    code = "00" + code;
                } else if (code.length() == 3) {
                    code = "0" + code;
                }
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += code;
            }
            tGroupOrder.setOrderCode(orderCode);
            boolean res = tGroupOrderService.save(tGroupOrder);
            if (res) {

                QueryWrapper<TGroupOrder> tGroupOrderQueryWrapper = new QueryWrapper<>();
                tGroupOrderQueryWrapper.eq("del_flag",0);
                tGroupOrderQueryWrapper.eq("order_code",orderCode);
                tGroupOrderQueryWrapper.eq("group_unit_name",tGroupOrder.getUnitName());
                TGroupOrder tGroupOrderNew = tGroupOrderService.getOne(tGroupOrderQueryWrapper);

                if(tGroupOrder.getSetMealItems() != null && tGroupOrder.getSetMealItems().size() > 0){
                    //保存订单分组信息
                    List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                    List<TBaseProject> tBaseProjectList = itBaseProjectService.list();

                    TOrderGroup tOrderGroup = new TOrderGroup();
                    tOrderGroup.setGroupOrderId(tGroupOrderNew.getId());//订单id
                    tOrderGroup.setDelFlag(0);//是否删除
                    tOrderGroup.setAddDiscount(100);//选检项折扣
                    tOrderGroup.setDiscount(100);//必检项折扣
                    tOrderGroup.setPersonCount(tGroupOrder.getPersonCount());//分组人数
                    String comboIds  = "";
                    String name  = "";
                    List<String> comboIdLists = new ArrayList<>();
                    for(TCombo tCombo : tGroupOrder.getSetMealItems()){
                        if(comboIds.trim().length() > 0){
                            comboIds += "," + tCombo.getId();
                        }else{
                            comboIds += tCombo.getId();
                        }
                        if(name.trim().length() > 0){
                            name += "、" + tCombo.getName();
                        }else{
                            name += tCombo.getName();
                        }
                        comboIdLists.add(tCombo.getId());
                    }
                    tOrderGroup.setComboId(comboIds);//套餐id
                    tOrderGroup.setName(name);//分组名
                    //保存分组
                    tOrderGroupService.save(tOrderGroup);

                    QueryWrapper<TOrderGroup> tOrderGroupQueryWrapper = new QueryWrapper<>();
                    tOrderGroupQueryWrapper.eq("del_flag",0);
                    tOrderGroupQueryWrapper.eq("group_order_id",tGroupOrderNew.getId());
                    tOrderGroupQueryWrapper.eq("name",name);
                    TOrderGroup tOrderGroupNew = tOrderGroupService.getOne(tOrderGroupQueryWrapper);

                    //保存订单分组项目信息
                    QueryWrapper<TComboItem> tComboItemQueryWrapper = new QueryWrapper<>();
                    tComboItemQueryWrapper.in("combo_id",comboIdLists);
                    tComboItemQueryWrapper.groupBy("portfolio_project_id");
                    List<TComboItem> tComboItems = tComboItemService.list(tComboItemQueryWrapper);
                    List<String> projects = new ArrayList<>();
                    for(TComboItem tComboItem : tComboItems){
                        projects.add(tComboItem.getPortfolioProjectId());
                    }
                    QueryWrapper<TPortfolioProject> tPortfolioProjectQueryWrapper = new QueryWrapper<>();
                    tPortfolioProjectQueryWrapper.eq("del_flag",0);
                    tPortfolioProjectQueryWrapper.in("id",projects);
                    List<TPortfolioProject> tPortfolioProjects = tPortfolioProjectService.list(tPortfolioProjectQueryWrapper);
                    for(TPortfolioProject tPortfolioProject : tPortfolioProjects){
                        TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                        tOrderGroupItem.setAddress(tPortfolioProject.getAddress());
                        tOrderGroupItem.setDiscount(100);
                        tOrderGroupItem.setDiscountPrice(tPortfolioProject.getSalePrice());
                        tOrderGroupItem.setGroupOrderId(tGroupOrderNew.getId());
                        tOrderGroupItem.setGroupId(tOrderGroupNew.getId());
                        tOrderGroupItem.setPortfolioProjectId(tPortfolioProject.getId());
                        tOrderGroupItem.setIsFile(tPortfolioProject.getIsFile());
                        tOrderGroupItem.setName(tPortfolioProject.getName());
                        tOrderGroupItem.setOfficeId(tPortfolioProject.getOfficeId());
                        tOrderGroupItem.setOfficeName(tPortfolioProject.getOfficeName());
                        tOrderGroupItem.setOrderNum(tPortfolioProject.getOrderNum());
                        tOrderGroupItem.setProjectType(1);
                        tOrderGroupItem.setSalePrice(tPortfolioProject.getSalePrice());
                        tOrderGroupItem.setServiceType(tPortfolioProject.getServiceType());
                        tOrderGroupItem.setShortName(tPortfolioProject.getShortName());
                        tOrderGroupItem.setTemplate(tPortfolioProject.getTemplate());
                        tOrderGroupItem.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        tOrderGroupItem.setCreateId(usernNow.getId());
                        tOrderGroupItem.setCreateTime(new Date());
                        tOrderGroupItem.setDelFlag(0);
                        tOrderGroupItem.setStatus(0);
                        //保存分组项目
                        boolean save = itOrderGroupItemService.save(tOrderGroupItem);
                        if (save) {
                            //保存分组项目的子项目
                            List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                    .filter(item -> item.getPortfolioProjectId().equals(tOrderGroupItem.getPortfolioProjectId()))
                                    .collect(Collectors.toList());
                            ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                            for (RelationBasePortfolio relationBasePortfolio : collect) {
                                TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                if(tBaseProject != null) {
                                    List<TOrderGroupItemProject> collect1 =
                                            projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(tOrderGroupItem.getId())
                                                    && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                    if (collect1.size() > 0) {
                                        continue;
                                    }
                                    TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                    tOrderGroupItemProject.setTOrderGroupItemId(tOrderGroupItem.getId());
                                    tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                    tOrderGroupItemProject.setName(tBaseProject.getName());
                                    tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                    tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                    tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                    tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                    tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                    tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                    tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                    tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                    tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                    tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                    tOrderGroupItemProject.setGroupOrderId(tGroupOrderNew.getId());
                                    tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                    tOrderGroupItemProject.setDelFlag(0);;
                                    projectArrayList.add(tOrderGroupItemProject);
                                }
                            }
                            itOrderGroupItemProjectService.saveBatch(projectArrayList);
                        }
                    }
                }
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：新增从业体检订单数据
     *
     * @param tGroupOrder 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增从业体检订单数据", type = LogType.OPERATION)
    @ApiOperation("新增从业体检订单数据")
    @PostMapping("addPracticeTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> addPracticeTGroupOrder(@RequestBody TGroupOrder tGroupOrder) {
        try {
            if (tGroupOrder.getGroupData() == null || tGroupOrder.getGroupData().size() < 1) {
                return ResultUtil.error("分组信息不能为空！");
            }
            for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                if (groupDatum.getProjectData() == null || groupDatum.getProjectData().size() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检项目不能为空！");
                }
                if (groupDatum.getPersonCount() == null || groupDatum.getPersonCount() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检人数不能为空！");
                }
            }
            tGroupOrder.setDelFlag(0);
            tGroupOrder.setCreateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setCreateTime(new Date());
            tGroupOrder.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            TGroupOrder one = tGroupOrderService.getOneByWhere(securityUtil.getCurrUser().getDepartmentId());
            String orderCode = "";
            if (one == null) {
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += "0001";
            } else {
                String substring = one.getOrderCode().substring(one.getOrderCode().length() - 4);
                int i = Integer.parseInt(substring);
                i += 1;
                String code = String.valueOf(i);
                if (code.length() == 1) {
                    code = "000" + code;
                } else if (code.length() == 2) {
                    code = "00" + code;
                } else if (code.length() == 3) {
                    code = "0" + code;
                }
                if(socketConfig.getUpdateCreateMethd()){
                    orderCode = "6" + dateFormat.format(new Date());
                }else{
                    orderCode = dateFormat.format(new Date());
                }
                orderCode += code;
            }
            tGroupOrder.setOrderCode(orderCode);
            boolean res = tGroupOrderService.save(tGroupOrder);
            if (res) {
                //保存订单分组信息
                if (tGroupOrder.getGroupData() != null && tGroupOrder.getGroupData().size() > 0) {
                    for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                        groupDatum.setGroupOrderId(tGroupOrder.getId());
                        groupDatum.setDelFlag(0);
                        groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                        groupDatum.setCreateTime(new Date());
                        boolean save = itOrderGroupService.save(groupDatum);
                        if (save) {
                            TGroupPerson groupPerson ;
                            groupPerson = tGroupOrder.getGroupPerson();
                            if (tGroupOrder.getGroupPerson().getAvatar() != null && StringUtils.isNotBlank(tGroupOrder.getGroupPerson().getAvatar().toString())&&tGroupOrder.getGroupPerson().getAvatar().toString().indexOf("data:image")>-1 ) {
                                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupOrder.getGroupPerson().getAvatar().toString());
                                String classPath = DocUtil.getClassPath().split(":")[0];
                                //时间戳
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                String DataStr = format.format(new Date());
                                if(tGroupOrder.getGroupPerson().getTestNum()!=null && tGroupOrder.getGroupPerson().getTestNum().trim().length() > 0){
                                    DataStr = tGroupOrder.getGroupPerson().getTestNum();
                                }
                                String name = imgFile.getOriginalFilename();
                                File file1 = new File(classPath+":" + UploadFileUtils.basePath +"dcm/avatar/" + DataStr + "/" + name);
                                //存在则删除
                                if(file1.isFile() && file1.exists()){
                                    file1.delete();
                                    file1 = new File(classPath+":" + UploadFileUtils.basePath +"dcm/avatar/" + DataStr + "/" + name);
                                }
                                FileUtils.writeByteArrayToFile(file1,imgFile.getBytes());
                                String url = "/tempFileUrl/tempfile/dcm/avatar/" + DataStr + "/" + name;
                                groupPerson.setAvatar(url);
                            }
                            else{
                                groupPerson.setAvatar(null);
                            }
                            groupPerson.setOrderId(tGroupOrder.getId());
                            groupPerson.setGroupId(groupDatum.getId());
                            groupPerson.setIsPass(1);
                            groupPerson.setDelFlag(0);
                            groupPerson.setIsCheck(0);
                            groupPerson.setStatu(0);
                            groupPerson.setReportPrintingNum(0);
                            groupPerson.setTestNum(generatorNum(tGroupOrder.getPhysicalType()));
                            groupPerson.setCreateId(securityUtil.getCurrUser().getId());
                            groupPerson.setCreateTime(new Date());
                            boolean personSave = tGroupPersonService.save(groupPerson);
                            if (personSave) {
                                if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                                    for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                        projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                        projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                        projectDatum.setCreateTime(new Date());
                                        projectDatum.setDelFlag(0);
                                        projectDatum.setGroupId(groupDatum.getId());
                                        projectDatum.setGroupOrderId(tGroupOrder.getId());

                                        //保存分组项目
                                        boolean save1 = itOrderGroupItemService.save(projectDatum);
                                        if (save1) {
                                            //保存分组项目的子项目
                                            ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(projectDatum.getPortfolioProjectId());
                                            if (list != null && list.size() > 0) {
                                                List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                                                ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                                for (TBaseProject tBaseProject : tBaseProjects) {
                                                    TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                    tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                    tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                    tOrderGroupItemProject.setName(tBaseProject.getName());
                                                    tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                    tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                    tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                    tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                    tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                    tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                    tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                    tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                    tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                    tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                    tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                                    tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                    projectArrayList.add(tOrderGroupItemProject);
                                                }
                                                itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tGroupOrder 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检订单数据", type = LogType.OPERATION)
    @ApiOperation("更新团检订单数据")
    @PostMapping("updateTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> updateTGroupOrder(@RequestBody TGroupOrder tGroupOrder) {
        if (StringUtils.isBlank(tGroupOrder.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            if (tGroupOrder.getGroupData() == null || tGroupOrder.getGroupData().size() < 1) {
                return ResultUtil.error("分组信息不能为空！");
            }
            for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                if (groupDatum.getProjectData() == null || groupDatum.getProjectData().size() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检项目不能为空！");
                }
                if (!tGroupOrder.getTolerable()){
                    if (groupDatum.getPersonCount() == null || groupDatum.getPersonCount() < 1) {
                        return ResultUtil.error("“" + groupDatum.getName() + "”分组体检人数不能为空！");
                    }
                }
            }
            tGroupOrder.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setUpdateTime(new Date());
            boolean res = tGroupOrderService.updateById(tGroupOrder);
            if (res) {
                //判断订单下是否有人且已登记，是就不删除订单分组及项目
                QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
                personQueryWrapper.eq("order_id",tGroupOrder.getId());
                personQueryWrapper.ge("is_pass",2);
                Integer countPerson =  tGroupPersonService.count(personQueryWrapper);
                if(countPerson==0){//先删除再创建
                    //删除订单分组
                    QueryWrapper<TOrderGroup> groupQueryWrapper = new QueryWrapper<>();
                    groupQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                    groupQueryWrapper.select("id");
                    List<TOrderGroup> tOrderGroupList =   itOrderGroupService.list(groupQueryWrapper);
                    List<String> courseIds=  tOrderGroupList.stream().map(TOrderGroup::getId).collect(Collectors.toList());
                    List<String> Ids=  tGroupOrder.getGroupData().stream().map(TOrderGroup::getId).collect(Collectors.toList());
                    courseIds.removeAll(Ids);
                    itOrderGroupService.removeByIds(courseIds);
                    //删除分组项目
                    QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                    groupItemQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                    itOrderGroupItemService.remove(groupItemQueryWrapper);
                    //删除分组项目子项目
                    QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
                    groupItemProjectQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                    itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);
                    //保存订单分组信息
                    List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                    List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                    for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                        if(StringUtils.isBlank(groupDatum.getId())){
                            groupDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        }
                        groupDatum.setGroupOrderId(tGroupOrder.getId());
                        groupDatum.setDelFlag(0);
                        groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                        groupDatum.setCreateTime(new Date());
                        boolean flag = itOrderGroupService.saveOrUpdate(groupDatum);
                        if (flag) {
                            if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                                for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                    //保存分组项目
                                    projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                    projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                    projectDatum.setCreateTime(new Date());
                                    projectDatum.setDelFlag(0);
                                    projectDatum.setGroupId(groupDatum.getId());
                                    projectDatum.setGroupOrderId(tGroupOrder.getId());
                                    boolean save1 = itOrderGroupItemService.save(projectDatum);
                                    if (save1) {
                                        //保存分组项目的子项目
                                        List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                                .filter(item -> item.getPortfolioProjectId().equals(projectDatum.getPortfolioProjectId()))
                                                .collect(Collectors.toList());
                                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                        for (RelationBasePortfolio relationBasePortfolio : collect) {
                                            TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                            if(tBaseProject != null) {
                                                List<TOrderGroupItemProject> collect1 =
                                                        projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(projectDatum.getId())
                                                                && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                                if (collect1.size() > 0) {
                                                    continue;
                                                }
                                                TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                tOrderGroupItemProject.setName(tBaseProject.getName());
                                                tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                                tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                projectArrayList.add(tOrderGroupItemProject);
                                            }
                                        }
                                        itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                    }
                                }
                            }
                        }
                    }
                    return ResultUtil.data(res, "修改成功");
                }else{//保存订单分组信息
                    List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                    List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                    //保存订单分组信息
                    for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                        //分组是否是新增
                        Boolean isAdd = false;
                        if(StringUtils.isNotBlank(groupDatum.getId())){
                            groupDatum.setUpdateId(securityUtil.getCurrUser().getId());
                            groupDatum.setUpdateTime(new Date());
                        }
                        else{
                            groupDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                            groupDatum.setGroupOrderId(tGroupOrder.getId());
                            groupDatum.setDelFlag(0);
                            groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                            groupDatum.setCreateTime(new Date());
                            isAdd = true;
                        }
                        Boolean flag = itOrderGroupService.saveOrUpdate(groupDatum);
                        if (flag) {
                            //删除选检的分组项目
                            QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                            groupItemQueryWrapper.eq("group_id", groupDatum.getId());
                            groupItemQueryWrapper.eq("project_type", 2);
                            groupItemQueryWrapper.eq("del_flag", 0);
                            List<TOrderGroupItem> groupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
                            List<String> groupItemProjetIds = new ArrayList<>();
                            if(groupItems.size()>0){
                                groupItemProjetIds = groupItems.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
                                List<TOrderGroupItem> projectList =  groupDatum.getProjectData();
                                List<String> groupItemIds =projectList.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
                                if(groupItemIds!=null && groupItemIds.size()>0){
                                    List<String> groupItemIdsDelete = groupItemProjetIds.stream() .filter(currPrivilege ->!groupItemIds.contains(currPrivilege)).collect(Collectors.toList());
                                    if(groupItemIdsDelete!=null&& groupItemIdsDelete.size()>0){
                                        List<TOrderGroupItem> projectListDelete = groupItems.stream().filter(ii ->groupItemIdsDelete.contains(ii.getPortfolioProjectId())).collect(Collectors.toList());
                                        for(TOrderGroupItem groupItemDelete:projectListDelete ){
                                            QueryWrapper<TDepartResult> tDepartItemResultQueryWrapper = new QueryWrapper<>();
                                            tDepartItemResultQueryWrapper.eq("del_flag", 0);
                                            tDepartItemResultQueryWrapper.lambda().in(TDepartResult::getGroupItemId,groupItemDelete.getId());
                                            int count =  itDepartResultService.count(tDepartItemResultQueryWrapper);
                                            if(count>0){
                                                new Exception("分组："+ groupDatum.getName() +" 下的项目 "+ groupItemDelete.getName() +" 已经有人检查了，不能被删除！");
                                            }
                                            //选检的分组项目
                                            QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
                                            groupItemProjectQueryWrapper.eq("group_order_id", tGroupOrder.getId());
//                                        groupItemProjectQueryWrapper.lambda().in(TOrderGroupItemProject::getTOrderGroupItemId,groupItemIdsDelete);
                                            groupItemProjectQueryWrapper.eq("t_order_group_item_id",groupItemDelete.getId());
                                            /*itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);*/
                                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                            tOrderGroupItemProject.setDelFlag(1);
                                            //逻辑删除选检项目的订单基础项信息
                                            itOrderGroupItemProjectService.update(tOrderGroupItemProject,groupItemProjectQueryWrapper);

                                            /*itOrderGroupItemService.remove(groupItemQueryWrapper);*/
                                            //逻辑删除选检项目的订单组合项信息
                                            groupItemDelete.setDelFlag(1);
                                            itOrderGroupItemService.updateById(groupItemDelete);
                                        }
                                    }

                                }


                            }
                            if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                                for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                    //是新添加的或者是选检项目
                                    if((2 == projectDatum.getProjectType() && !groupItemProjetIds.contains(projectDatum.getPortfolioProjectId()))  || isAdd){
                                        QueryWrapper<TOrderGroupItem> tOrderGroupItemQueryWrapper = new QueryWrapper<>();
                                        tOrderGroupItemQueryWrapper.eq("id",projectDatum.getId());
                                        tOrderGroupItemQueryWrapper.eq("group_id",groupDatum.getId());
                                        TOrderGroupItem tOrderGroupItem = itOrderGroupItemService.getOne(tOrderGroupItemQueryWrapper);
                                        if(tOrderGroupItem == null || (tOrderGroupItem!=null && tOrderGroupItem.getGroupId()!=null && groupDatum.getId()!=null && !tOrderGroupItem.getGroupId().equals(groupDatum.getId())) || StringUtils.isBlank(projectDatum.getId())){
                                            //保存分组项目
                                            projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                            projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                            projectDatum.setCreateTime(new Date());
                                            projectDatum.setDelFlag(0);
                                            projectDatum.setGroupId(groupDatum.getId());
                                            projectDatum.setGroupOrderId(tGroupOrder.getId());
                                            boolean save1 = itOrderGroupItemService.save(projectDatum);
                                            if (save1) {
                                                //保存分组项目的子项目
                                                List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                                        .filter(item -> item.getPortfolioProjectId().equals(projectDatum.getPortfolioProjectId()))
                                                        .collect(Collectors.toList());
                                                ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                                for (RelationBasePortfolio relationBasePortfolio : collect) {
                                                    TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                                    if(tBaseProject != null) {
                                                        List<TOrderGroupItemProject> collect1 =
                                                                projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(projectDatum.getId())
                                                                        && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            continue;
                                                        }
                                                        TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                        tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                        tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                        tOrderGroupItemProject.setName(tBaseProject.getName());
                                                        tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                        tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                        tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                        tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                        tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                        tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                        tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                        tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                        tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                        tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                        tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                                        tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                        projectArrayList.add(tOrderGroupItemProject);
                                                    }
                                                }
                                                itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return ResultUtil.data(res, "修改成功");
                }
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tGroupOrder 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检订单数据", type = LogType.OPERATION)
    @ApiOperation("更新团检订单数据")
    @PostMapping("updateTGroupOrderInfo")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> updateTGroupOrderInfo(@RequestBody TGroupOrder tGroupOrder) {
        if (StringUtils.isBlank(tGroupOrder.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            if (tGroupOrder.getGroupData() == null || tGroupOrder.getGroupData().size() < 1) {
                return ResultUtil.error("分组信息不能为空！");
            }
            for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                if (groupDatum.getProjectData() == null || groupDatum.getProjectData().size() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检项目不能为空！");
                }
                if (!tGroupOrder.getTolerable()){
                    if (groupDatum.getPersonCount() == null || groupDatum.getPersonCount() < 1) {
                        return ResultUtil.error("“" + groupDatum.getName() + "”分组体检人数不能为空！");
                    }
                }
            }
            tGroupOrder.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setUpdateTime(new Date());
            boolean res = tGroupOrderService.updateById(tGroupOrder);
            if (res) {
                List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                //保存订单分组信息
                for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                    //分组是否是新增
                    Boolean isAdd = false;
                    if(StringUtils.isNotBlank(groupDatum.getId())){
                        groupDatum.setUpdateId(securityUtil.getCurrUser().getId());
                        groupDatum.setUpdateTime(new Date());
                    }
                    else{
                        groupDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        groupDatum.setGroupOrderId(tGroupOrder.getId());
                        groupDatum.setDelFlag(0);
                        groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                        groupDatum.setCreateTime(new Date());
                        if(tGroupOrder!=null && StringUtils.isNotBlank(tGroupOrder.getId())){
                            QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("del_flag",0);
                            queryWrapper.eq("group_order_id",tGroupOrder.getId());
                            queryWrapper.orderByDesc("order_num");
                            queryWrapper.last("limit 1");
                            TOrderGroup tOrderGroup = itOrderGroupService.getOne(queryWrapper);
                            if(tOrderGroup!=null && tOrderGroup.getOrderNum()!=null){
                                groupDatum.setOrderNum(tOrderGroup.getOrderNum()+1);
                            }else{
                                groupDatum.setOrderNum(0);
                            }
                        }
                        isAdd = true;
                    }
                    Boolean flag = itOrderGroupService.saveOrUpdate(groupDatum);
                    if (flag) {
                        //删除选检的分组项目
                        QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                        groupItemQueryWrapper.eq("group_id", groupDatum.getId());
                        groupItemQueryWrapper.eq("project_type", 2);
                        groupItemQueryWrapper.eq("del_flag", 0);
                        List<TOrderGroupItem> groupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
                        List<String> groupItemProjetIds = new ArrayList<>();
                        if(groupItems.size()>0){
                            groupItemProjetIds = groupItems.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
                            List<TOrderGroupItem> projectList =  groupDatum.getProjectData();
                            List<String> groupItemIds =projectList.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
                            if(groupItemIds!=null && groupItemIds.size()>0){
                                List<String> groupItemIdsDelete = groupItemProjetIds.stream() .filter(currPrivilege ->!groupItemIds.contains(currPrivilege)).collect(Collectors.toList());
                                if(groupItemIdsDelete!=null&& groupItemIdsDelete.size()>0){
                                    List<TOrderGroupItem> projectListDelete = groupItems.stream().filter(ii ->groupItemIdsDelete.contains(ii.getPortfolioProjectId())).collect(Collectors.toList());
                                    for(TOrderGroupItem groupItemDelete:projectListDelete ){
                                        QueryWrapper<TDepartResult> tDepartItemResultQueryWrapper = new QueryWrapper<>();
                                        tDepartItemResultQueryWrapper.eq("del_flag", 0);
                                        tDepartItemResultQueryWrapper.lambda().in(TDepartResult::getGroupItemId,groupItemDelete.getId());
                                        int count =  itDepartResultService.count(tDepartItemResultQueryWrapper);
                                        if(count>0){
                                            new Exception("分组："+ groupDatum.getName() +" 下的项目 "+ groupItemDelete.getName() +" 已经有人检查了，不能被删除！");
                                        }
                                        //选检的分组项目
                                        QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
                                        groupItemProjectQueryWrapper.eq("group_order_id", tGroupOrder.getId());
//                                        groupItemProjectQueryWrapper.lambda().in(TOrderGroupItemProject::getTOrderGroupItemId,groupItemIdsDelete);
                                        groupItemProjectQueryWrapper.eq("t_order_group_item_id",groupItemDelete.getId());
                                        /*itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);*/
                                        TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                        tOrderGroupItemProject.setDelFlag(1);
                                        //逻辑删除选检项目的订单基础项信息
                                        itOrderGroupItemProjectService.update(tOrderGroupItemProject,groupItemProjectQueryWrapper);

                                        /*itOrderGroupItemService.remove(groupItemQueryWrapper);*/
                                        //逻辑删除选检项目的订单组合项信息
                                        groupItemDelete.setDelFlag(1);
                                        itOrderGroupItemService.updateById(groupItemDelete);
                                    }
                                }

                            }


                        }
                        if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                            for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                //是新添加的或者是选检项目
                                if((2 == projectDatum.getProjectType() && !groupItemProjetIds.contains(projectDatum.getPortfolioProjectId()))  || isAdd){
                                    QueryWrapper<TOrderGroupItem> tOrderGroupItemQueryWrapper = new QueryWrapper<>();
                                    tOrderGroupItemQueryWrapper.eq("id",projectDatum.getId());
                                    tOrderGroupItemQueryWrapper.eq("group_id",groupDatum.getId());
                                    TOrderGroupItem tOrderGroupItem = itOrderGroupItemService.getOne(tOrderGroupItemQueryWrapper);
                                    if(tOrderGroupItem == null || (tOrderGroupItem!=null && tOrderGroupItem.getGroupId()!=null && groupDatum.getId()!=null && !tOrderGroupItem.getGroupId().equals(groupDatum.getId())) || StringUtils.isBlank(projectDatum.getId())){
                                        //保存分组项目
                                        projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                        projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                        projectDatum.setCreateTime(new Date());
                                        projectDatum.setDelFlag(0);
                                        projectDatum.setGroupId(groupDatum.getId());
                                        projectDatum.setGroupOrderId(tGroupOrder.getId());
                                        boolean save1 = itOrderGroupItemService.save(projectDatum);
                                        if (save1) {
                                            //保存分组项目的子项目
                                            List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                                    .filter(item -> item.getPortfolioProjectId().equals(projectDatum.getPortfolioProjectId()))
                                                    .collect(Collectors.toList());
                                            ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                            for (RelationBasePortfolio relationBasePortfolio : collect) {
                                                TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                                if(tBaseProject != null) {
                                                    List<TOrderGroupItemProject> collect1 =
                                                            projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(projectDatum.getId())
                                                                    && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                                    if (collect1.size() > 0) {
                                                        continue;
                                                    }
                                                    TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                    tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                                    tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                                    tOrderGroupItemProject.setName(tBaseProject.getName());
                                                    tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                                    tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                                    tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                                    tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                                    tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                                    tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                                    tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                                    tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                                    tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                                    tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                                    tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                                    tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                    projectArrayList.add(tOrderGroupItemProject);
                                                }
                                            }
                                            itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新从业体检订单数据
     *
     * @param tGroupOrder 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新从业体检订单数据", type = LogType.OPERATION)
    @ApiOperation("更新从业体检订单数据")
    @PostMapping("updatePracticeTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> updatePracticeTGroupOrder(@RequestBody TGroupOrder tGroupOrder) {
        if (StringUtils.isBlank(tGroupOrder.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            if (tGroupOrder.getGroupData() == null || tGroupOrder.getGroupData().size() < 1) {
                return ResultUtil.error("分组信息不能为空！");
            }
            for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                if (groupDatum.getProjectData() == null || groupDatum.getProjectData().size() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检项目不能为空！");
                }
                if (groupDatum.getPersonCount() == null || groupDatum.getPersonCount() < 1) {
                    return ResultUtil.error("“" + groupDatum.getName() + "”分组体检人数不能为空！");
                }
            }
            tGroupOrder.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setUpdateTime(new Date());
            boolean res = tGroupOrderService.updateById(tGroupOrder);
            if (res) {
                //删除订单分组
                QueryWrapper<TOrderGroup> groupQueryWrapper = new QueryWrapper<>();
                groupQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                itOrderGroupService.remove(groupQueryWrapper);
                //删除分组项目
                QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                groupItemQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                itOrderGroupItemService.remove(groupItemQueryWrapper);
                //删除分组项目子项目
                QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
                groupItemProjectQueryWrapper.eq("group_order_id", tGroupOrder.getId());
                itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);

                //保存订单分组信息
                for (TOrderGroup groupDatum : tGroupOrder.getGroupData()) {
                    groupDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    groupDatum.setGroupOrderId(tGroupOrder.getId());
                    groupDatum.setDelFlag(0);
                    groupDatum.setCreateId(securityUtil.getCurrUser().getId());
                    groupDatum.setCreateTime(new Date());
                    boolean flag = itOrderGroupService.save(groupDatum);
                    if (flag) {
                        if (groupDatum.getProjectData() != null && groupDatum.getProjectData().size() > 0) {
                            for (TOrderGroupItem projectDatum : groupDatum.getProjectData()) {
                                //保存分组项目
                                projectDatum.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                projectDatum.setCreateId(securityUtil.getCurrUser().getId());
                                projectDatum.setCreateTime(new Date());
                                projectDatum.setDelFlag(0);
                                projectDatum.setGroupId(groupDatum.getId());
                                projectDatum.setGroupOrderId(tGroupOrder.getId());
                                boolean save1 = itOrderGroupItemService.save(projectDatum);
                                if (save1) {
                                    //保存分组项目的子项目
                                    ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(projectDatum.getPortfolioProjectId());
                                    if (list != null && list.size() > 0) {
                                        List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                        for (TBaseProject tBaseProject : tBaseProjects) {
                                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                            tOrderGroupItemProject.setTOrderGroupItemId(projectDatum.getId());
                                            tOrderGroupItemProject.setCode(tBaseProject.getCode());
                                            tOrderGroupItemProject.setName(tBaseProject.getName());
                                            tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                                            tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                                            tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                                            tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                                            tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                                            tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                                            tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                                            tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                                            tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                                            tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                                            tOrderGroupItemProject.setGroupOrderId(tGroupOrder.getId());
                                            tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                            projectArrayList.add(tOrderGroupItemProject);
                                        }
                                        boolean itemSave = itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                        if (itemSave) {
                                            TGroupPerson groupPerson;
                                            groupPerson = tGroupOrder.getGroupPerson();
                                            groupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                                            groupPerson.setGroupId(groupDatum.getId());
                                            groupPerson.setUpdateTime(new Date());
                                            tGroupPersonService.updateById(groupPerson);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 修改订单支付状态为已支付
     *
     * @param id
     * @return
     */
    @ApiOperation("修改订单支付状态为已支付")
    @SystemLog(description = "修改订单支付状态为已支付", type = LogType.OPERATION)
    @PostMapping("updatePayStatus")
    public Result<Object> updatePayStatus(@RequestParam String id) {
        try {
            TGroupOrder tGroupOrder = new TGroupOrder();
            tGroupOrder.setId(id);
            tGroupOrder.setPayStatus(1);
            boolean update = tGroupOrderService.updateById(tGroupOrder);
            if (update) {
                return ResultUtil.data(update);
            } else {
                return ResultUtil.error("修改订单支付状态失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改订单支付状态异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除团检订单数据")
    @SystemLog(description = "根据主键来删除团检订单数据", type = LogType.OPERATION)
    @PostMapping("deleteTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> deleteTGroupOrder(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tGroupOrderService.removeByIds(Arrays.asList(ids));
            if (res) {
                for (String id : ids) {
                    //删除订单对应的人员信息
                    QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_id", id);
                    TGroupPerson tGroupPerson = new TGroupPerson();
                    tGroupPerson.setDeleteId(securityUtil.getCurrUser().getId());
                    tGroupPerson.setDeleteTime(new Date());
                    tGroupPerson.setDelFlag(1);
                    itGroupPersonService.update(tGroupPerson, queryWrapper);

                    //删除订单对应的分组和分组项目
                    QueryWrapper<TOrderGroup> orderGroupQueryWrapper = new QueryWrapper<>();
                    orderGroupQueryWrapper.eq("group_order_id", id);
                    TOrderGroup tOrderGroup = new TOrderGroup();
                    tOrderGroup.setDeleteId(securityUtil.getCurrUser().getId());
                    tOrderGroup.setDeleteTime(new Date());
                    tOrderGroup.setDelFlag(1);
                    itOrderGroupService.update(tOrderGroup, orderGroupQueryWrapper);

                    QueryWrapper<TOrderGroupItem> itemQueryWrapper = new QueryWrapper<>();
                    itemQueryWrapper.eq("group_order_id", id);
                    TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                    tOrderGroupItem.setDeleteId(securityUtil.getCurrUser().getId());
                    tOrderGroupItem.setDeleteTime(new Date());
                    tOrderGroupItem.setDelFlag(1);
                    itOrderGroupItemService.update(tOrderGroupItem, itemQueryWrapper);

                    QueryWrapper<TOrderGroupItemProject> itemProjectQueryWrapper = new QueryWrapper<>();
                    itemProjectQueryWrapper.eq("group_order_id", id);
                    TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                    tOrderGroupItemProject.setDelFlag(1);
                    itOrderGroupItemProjectService.update(tOrderGroupItemProject, itemProjectQueryWrapper);
                }
                return ResultUtil.data(res, "删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取团检订单数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取团检订单数据")
    @GetMapping("getTGroupOrder")
    public Result<Object> getTGroupOrder(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TGroupOrder res = tGroupOrderService.getById(id);
            QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
            personQueryWrapper.eq("del_flag", 0);
            personQueryWrapper.gt("is_pass", 1);
            personQueryWrapper.eq("order_id", id);
            Integer personCount = tGroupPersonService.count(personQueryWrapper);
            if (res != null) {
                res.setPersonCount(personCount);
                return ResultUtil.data(res, "查询成功");
            } else {
                return ResultUtil.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询团检订单数据", type = LogType.OPERATION)
    @ApiOperation("分页查询团检订单数据")
    @GetMapping("queryTGroupOrderList")
    public Result<Object> queryTGroupOrderList(TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupOrder> result = tGroupOrderService.queryTGroupOrderListByPage(tGroupOrder, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询用户的团检预约信息", type = LogType.OPERATION)
    @ApiOperation("分页查询用户的团检预约信息")
    @GetMapping("queryTGroupOrderAppList")
    public Result<Object> queryTGroupOrderAppList(TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo) {
        try {
            //获取当前app用户手机号 作为订单公司联系人
            String mobile = tGroupOrder.getMobile();
            //获取公司(当前用户手机号下的) group_unit_id
            List<String> unitIds = new ArrayList<>();
            QueryWrapper<TGroupUnit> tGroupUnitQueryWrapper = new QueryWrapper<>();
            tGroupUnitQueryWrapper.eq("del_flag",0);
            tGroupUnitQueryWrapper.eq("link_phone2",mobile);
            List<TGroupUnit> TGroupUnits = tGroupUnitService.list(tGroupUnitQueryWrapper);
            for(TGroupUnit tGroupUnit : TGroupUnits){
                if(tGroupUnit!=null && tGroupUnit.getId()!=null && tGroupUnit.getId().trim().length() > 0){
                    unitIds.add(tGroupUnit.getId());
                }
            }
            //根据公司id查询对应的团检订单
            IPage<TGroupOrder> result = tGroupOrderService.queryTGroupOrderAppList(tGroupOrder, searchVo, pageVo, unitIds);
            //获取订单下的套餐
            for(TGroupOrder tGroupOrderNow : result.getRecords()){
                if(tGroupOrderNow!=null & tGroupOrderNow.getComboIds()!=null && tGroupOrderNow.getComboIds().trim().length() > 0){
                    //获取套餐id
                    List<String> tComboIds = new ArrayList<>();
                    if(tGroupOrderNow.getComboIds().indexOf(",") > -1){
                        String[] strings = tGroupOrderNow.getComboIds().split(",");
                        for(String s : strings){
                            tComboIds.add(s);
                        }
                    }else{
                        tComboIds.add(tGroupOrderNow.getComboIds());
                    }
                    //根据套餐id查询对应套餐
                    QueryWrapper<TCombo> tComboQueryWrapper = new QueryWrapper<>();
                    tComboQueryWrapper.eq("del_flag",0);
                    tComboQueryWrapper.in("id",tComboIds);
                    List<TCombo> tCombos = tComboService.list(tComboQueryWrapper);
                    //将套餐名称存入订单
                    String comboNames = "";
                    for(TCombo tCombo : tCombos){
                        if(tCombo!=null && tCombo.getName()!=null && tCombo.getName().trim().length() > 0){
                            String comboName = tCombo.getName();
                            if(comboNames == "" || comboNames.trim().length()==0){
                                comboNames += comboName;
                            }else{
                                comboNames += "," + comboName;
                            }
                        }
                    }
                    tGroupOrderNow.setComboNames(comboNames);
                }
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }



    /**
     * 功能描述：实现数据查询
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询团检预约详情", type = LogType.OPERATION)
    @ApiOperation("查询团检预约详情")
    @GetMapping("getGroupOrderApp")
    public Result<Object> getGroupOrderApp(TGroupOrder tGroupOrder) {
        try {
            TGroupOrder result = tGroupOrderService.getById(tGroupOrder.getId());
            QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag",0);
            queryWrapper.eq("order_id",tGroupOrder.getId());
            Integer personCount = tGroupPersonService.count(queryWrapper);
            result.setPersonCount(personCount);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询审批团检订单数据", type = LogType.OPERATION)
    @ApiOperation("分页查询审批团检订单数据")
    @GetMapping("queryApproveTGroupOrderList")
    public Result<Object> queryApproveTGroupOrderList(TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo) {
        try {
            //登录人员id
            String auditUserId = securityUtil.getCurrUser().getId();
            IPage<TGroupOrder> result = tGroupOrderService.queryApproveTGroupOrderList(auditUserId, tGroupOrder, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    @SystemLog(description = "查询全部团检订单数据", type = LogType.OPERATION)
    @ApiOperation("查询全部团检订单数据")
    @GetMapping("queryAllTGroupOrderList")
    public Result<Object> queryAllTGroupOrderList(TGroupOrder tGroupOrder) {
        try {
            List<TGroupOrder> result = tGroupOrderService.queryAllTGroupOrderList(tGroupOrder);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response    请求参数
     * @param tGroupOrder 查询参数
     * @return
     */
    @SystemLog(description = "导出团检订单数据", type = LogType.OPERATION)
    @ApiOperation("导出团检订单数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TGroupOrder tGroupOrder) {
        try {
            tGroupOrderService.download(tGroupOrder, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：查询待审批与已审批订单数量
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询待审批与已审批订单数量", type = LogType.OPERATION)
    @ApiOperation("查询待审批与已审批订单数量")
    @GetMapping("getTGroupOrderNumByCreateId")
    public Result<Object> getTGroupOrderNumByCreateId(String physicalType) {
        try {
            TGroupOrder res = tGroupOrderService.getTGroupOrderNumByCreateId(securityUtil.getCurrUser().getRoles().get(0).getId(),physicalType);
            return ResultUtil.data(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询待审批与已审批订单数量
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询待审批与已审批订单数量", type = LogType.OPERATION)
    @ApiOperation("查询待审批与已审批订单数量")
    @GetMapping("getTGroupOrderByIdWithLink")
    public Result<Object> getTGroupOrderByIdWithLink(String id) {
        try {
            Map<String, Object> map = tGroupOrderService.getTGroupOrderByIdWithLink(id);
            QueryWrapper<TOrderGroup> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_order_id", id);
            queryWrapper.eq("del_flag", 0);
            List<TOrderGroup> list = itOrderGroupService.list(queryWrapper);
            int personCount = 0;
            /*if (list.size() > 0) {
                for (TOrderGroup orderGroup : list) {
                    if (orderGroup.getPersonCount() != null) {
                        personCount += Integer.valueOf(orderGroup.getPersonCount());
                    }
                }
            }*/
            QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
            personQueryWrapper.eq("del_flag", 0);
            personQueryWrapper.eq("order_id", id);
            personCount = tGroupPersonService.count(personQueryWrapper);
            map.put("personCount", personCount);
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tGroupOrder 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新订单审批状态", type = LogType.OPERATION)
    @ApiOperation("更新订单审批状态")
    @PostMapping("updateTGroupOrderState")
    public Result<Object> updateTGroupOrderState(@RequestBody TGroupOrder tGroupOrder) {
        if (StringUtils.isBlank(tGroupOrder.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tGroupOrderService.updateById(tGroupOrder);
            if (res) {
                //保存新的审批意见
                TOrderFlow tOrderFlow = new TOrderFlow();
                tOrderFlow.setAuditState(tGroupOrder.getAuditState());
                tOrderFlow.setAuditContent(tGroupOrder.getAuditContent());
                tOrderFlow.setGroupOrderId(tGroupOrder.getId());
                tOrderFlow.setCreateUserId(securityUtil.getCurrUser().getId());
                tOrderFlow.setCreateUserName(securityUtil.getCurrUser().getNickname());
                tOrderFlow.setCreateTime(new Date());
                tOrderFlow.setAuditUserId(securityUtil.getCurrUser().getId());
                tOrderFlow.setAuditUserName(securityUtil.getCurrUser().getNickname());
                tOrderFlow.setAuditTime(new Date());

                tOrderFlowService.save(tOrderFlow);
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }
    /**
     * 功能描述：订单合并
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("订单合并")
    @PostMapping("consolidatedOrder")
    @Transactional(rollbackOn = { Exception.class })
    public Result<Object> consolidatedOrder(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        if(ids.length<=1){
            return ResultUtil.error("参数不正确，请联系管理员！！");
        }
        try {
            //合并后的订单Id
            String  idConsolidated =  ids[0];
            if(idConsolidated.indexOf("W")==0){
                for (String id : ids){
                    if(!id.contains("W")){
                        idConsolidated = id;
                        break;
                    }
                }
            }
            for (String id : ids) {
                if(!id.equals(idConsolidated)){
                    //删除订单
                    boolean res = tGroupOrderService.removeById(id);
                    if(res){
                        if(id.indexOf("W")==0){
                            //删除订单重复的人员信息
                            QueryWrapper<TGroupPerson> queryWrapperSelect = new QueryWrapper<>();
                            queryWrapperSelect.eq("order_id", id);
                            queryWrapperSelect.select("id_card");
                            queryWrapperSelect.ne("is_pass",1);
                            List<TGroupPerson> personList = itGroupPersonService.list(queryWrapperSelect);
                            if(personList!=null && personList.size()>0){
                                List<String> idCards = personList.stream().map(TGroupPerson:: getIdCard).collect(Collectors.toList());
                                TGroupPerson tGroupPerson = new TGroupPerson();
                                tGroupPerson.setDeleteId(securityUtil.getCurrUser().getId());
                                tGroupPerson.setDeleteTime(new Date());
                                tGroupPerson.setDelFlag(1);
                                //删除未登记且在体检车上的体检了的
                                QueryWrapper<TGroupPerson> queryWrapperDelete = new QueryWrapper<>();
                                queryWrapperDelete.eq("order_id", id);
                                queryWrapperDelete.in("id_card",idCards);
                                queryWrapperDelete.eq("is_pass",1);
                                List<TGroupPerson> tGroupPersonLists = itGroupPersonService.list(queryWrapperDelete);
                                if(tGroupPersonLists!=null && tGroupPersonLists.size()>0){
                                    boolean resU = itGroupPersonService.update(tGroupPerson, queryWrapperDelete);
                                    if(!resU){
                                        //手工回滚异常
                                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                        return ResultUtil.error("合并失败");
                                    }
                                }
                            }

                        }

                        //人员表相关更改
                        //更改订单对应的人员信息
                        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("order_id", id);
                        List<TGroupPerson> tGroupPersonList = itGroupPersonService.list(queryWrapper);
                        if(tGroupPersonList!=null && tGroupPersonList.size()>0){
                            TGroupPerson tGroupPerson = new TGroupPerson();
                            tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                            tGroupPerson.setUpdateTime(new Date());
                            tGroupPerson.setOrderId(idConsolidated);
                            boolean resG = itGroupPersonService.update(tGroupPerson, queryWrapper);
                            if(!resG){
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("合并失败");
                            }

                            //更改订单对应的分组和分组项目
                            QueryWrapper<TOrderGroup> orderGroupQueryWrapper = new QueryWrapper<>();
                            orderGroupQueryWrapper.eq("group_order_id", id);
                            TOrderGroup tOrderGroup = new TOrderGroup();
                            tOrderGroup.setUpdateId(securityUtil.getCurrUser().getId());
                            tOrderGroup.setUpdateTime(new Date());
                            tOrderGroup.setGroupOrderId(idConsolidated);
                            boolean resO = itOrderGroupService.update(tOrderGroup, orderGroupQueryWrapper);
                            if(!resO){
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("合并失败");
                            }
                            //更改订单对应的分组的组合项目
                            QueryWrapper<TOrderGroupItem> itemQueryWrapper = new QueryWrapper<>();
                            itemQueryWrapper.eq("group_order_id", id);
                            TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                            tOrderGroupItem.setUpdateId(securityUtil.getCurrUser().getId());
                            tOrderGroupItem.setUpdateTime(new Date());
                            tOrderGroupItem.setGroupOrderId(idConsolidated);
                            boolean resOR = itOrderGroupItemService.update(tOrderGroupItem, itemQueryWrapper);
                            if(!resOR){
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("合并失败");
                            }
                            //更改订单对应的分组的基础项目
                            QueryWrapper<TOrderGroupItemProject> itemProjectQueryWrapper = new QueryWrapper<>();
                            itemProjectQueryWrapper.eq("group_order_id", id);
                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                            tOrderGroupItemProject.setGroupOrderId(idConsolidated);
                            boolean resORi = itOrderGroupItemProjectService.update(tOrderGroupItemProject, itemProjectQueryWrapper);
                            if(!resORi){
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("合并失败");
                            }


                            //复查人员表相关更改
                            //更改订单对应的复查人员信息
                            QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper = new QueryWrapper<>();
                            tReviewPersonQueryWrapper.eq("order_id", id);
                            List<TReviewPerson> tReviewPersonList = itReviewPersonService.list(tReviewPersonQueryWrapper);
                            if(tReviewPersonList!=null && tReviewPersonList.size()>0){
                                TReviewPerson tReviewPerson = new TReviewPerson();
                                tReviewPerson.setUpdateId(securityUtil.getCurrUser().getId());
                                tReviewPerson.setUpdateTime(new Date());
                                tReviewPerson.setOrderId(idConsolidated);
                                boolean resTR = itReviewPersonService.update(tReviewPerson, tReviewPersonQueryWrapper);
                                if(!resTR){
                                    //手工回滚异常
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return ResultUtil.error("合并失败");
                                }
                            }
                        }
                    }else {
                        //手工回滚异常
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("合并失败");
                    }
                }
            }
            return ResultUtil.data(true, "合并成功");

        } catch (Exception e) {
            e.printStackTrace();
            //手工回滚异常
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("合并异常:" + e.getMessage());
        }
    }

    /**
     * 合并分组（未完成，考虑不合并）
     * @param orderId
     */
    public void consolidatedGroup(String orderId){
        if(StringUtils.isBlank(orderId)){
            return;
        }

        //查询订单下的分组
        QueryWrapper<TOrderGroup> queryWrapperSelectGroup = new QueryWrapper<>();
        queryWrapperSelectGroup.eq("group_order_id", orderId);
        queryWrapperSelectGroup.eq("del_flag", 0);
        List<TOrderGroup> list = itOrderGroupService.list(queryWrapperSelectGroup);
        if(list!=null && list.size()>0){
            //通过名称分组
            Map<String, List<TOrderGroup>> detailsMap = list.stream()
                    .collect(Collectors.groupingBy(TOrderGroup::getName));
            for (String key : detailsMap.keySet()) {
                List<TOrderGroup> groupList = (List<TOrderGroup>) detailsMap.get(key);
                if(groupList.size()>1){
                    TOrderGroup groupItem = groupList.get(0);
                    for (int i = 1; i < groupList.size(); i++) {
                        TOrderGroup item = groupList.get(i);
                        //删除订单对应的人员信息
                        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("group_id", item.getId());
                        TGroupPerson tGroupPerson = new TGroupPerson();
                        tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
                        tGroupPerson.setUpdateTime(new Date());
                        tGroupPerson.setGroupId(groupItem.getId());
                        itGroupPersonService.update(tGroupPerson, queryWrapper);

                        //分组的分组项目


//                        QueryWrapper<TOrderGroupItem> itemQueryWrapper = new QueryWrapper<>();
//                        itemQueryWrapper.eq("group_order_id", id);
//                        TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
//                        tOrderGroupItem.setDeleteId(securityUtil.getCurrUser().getId());
//                        tOrderGroupItem.setDeleteTime(new Date());
//                        tOrderGroupItem.setDelFlag(1);
//                        itOrderGroupItemService.update(tOrderGroupItem, itemQueryWrapper);
//
//                        QueryWrapper<TOrderGroupItemProject> itemProjectQueryWrapper = new QueryWrapper<>();
//                        itemProjectQueryWrapper.eq("group_order_id", id);
//                        TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
//                        tOrderGroupItemProject.setDelFlag(1);
//                        itOrderGroupItemProjectService.update(tOrderGroupItemProject, itemProjectQueryWrapper);

                    }
                }
            }
        }
    }
    /**
     * 功能描述：合同上传

     * @return
     */
    @ApiOperation("合同上传")
    @PostMapping("/uploadContracts")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> uploadContracts(@RequestParam(value = "file") MultipartFile multipartFile,String orderId,String name){
        try{
            String fileName = multipartFile.getOriginalFilename();
            if(StringUtils.isBlank(orderId)){
                return ResultUtil.error("合同上传失败:" + "订单Id为空");
            }
            if((fileName.indexOf(".pdf")==-1 && StringUtils.isNotBlank(name) && !name.contains("执照")) || (fileName.indexOf(".pdf")==-1 && fileName.indexOf(".png") == -1 && fileName.indexOf(".jpg") == -1 && fileName.indexOf(".jpeg") == -1 && StringUtils.isNotBlank(name) && name.contains("执照"))){
                return ResultUtil.error("合同上传异常:" + "上传的文件类型不对");
            }
            String classPath = DocUtil.getClassPath();

            TGroupOrder tGroupOrder = tGroupOrderService.getById(orderId);
            String nameNow = "";
            if(name!=null && name.trim().length() > 0){
                nameNow = name;
            }
            if(tGroupOrder!=null){
                String orderPathNow = "";
                if(nameNow.contains("执照")){
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderLicensePath())){
                        orderPathNow = tGroupOrder.getOrderLicensePath();
                    }
                }else if(nameNow.contains("评价")){
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderEvaluatePath())) {
                        orderPathNow = tGroupOrder.getOrderEvaluatePath();
                    }
                }else if(nameNow.contains("基本信息")){
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderInfoPath())) {
                        orderPathNow = tGroupOrder.getOrderInfoPath();
                    }
                }else if(nameNow.contains("人员名单")){
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderPersonDataPath())) {
                        orderPathNow = tGroupOrder.getOrderPersonDataPath();
                    }
                }else{
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderPath())) {
                        orderPathNow = tGroupOrder.getOrderPath();
                    }
                }
                if(StringUtils.isNotBlank(orderPathNow)){
                    String deletePath =  classPath.split(":")[0] + ":" + UploadFileUtils.deletePath+"/"+orderPathNow;
                    PdfUtil.deleteDocx(deletePath);
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String DataStr = format.format(new Date());

            String path = "";
            String orderPath = "";

            if(fileName.indexOf(".pdf") > -1 || fileName.indexOf(".Pdf") > -1){//pdf文件上传
                path = classPath.split(":")[0] + ":" + DocUtil.basePath + "order/" + orderId+nameNow + "/" + DataStr+".pdf";
                orderPath = "tempfile/wordTemplate/order/"+ orderId+nameNow + "/" + DataStr+".pdf";
                //获取文件地址
                //先创建输出文件路径
                File dest = (new File(path));
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                multipartFile.transferTo(dest);
            }else if(fileName.indexOf(".png") > -1 || fileName.indexOf(".jpg") > -1 || fileName.indexOf(".jpeg") > -1){//图片上传
                path = classPath.split(":")[0] + ":" + DocUtil.basePath + "order/" + orderId+nameNow + "/" + DataStr+"/" + fileName;
                orderPath = "tempfile/wordTemplate/order/"+ orderId+nameNow + "/" + DataStr+"/" + fileName;
                //获取文件地址
                //先创建输出文件路径
                File dest = (new File(path));
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                FileUtils.writeByteArrayToFile(dest,multipartFile.getBytes());
            }
            if(nameNow.contains("执照")){
                tGroupOrder.setOrderLicensePath(orderPath);
            }else if(nameNow.contains("评价")){
                tGroupOrder.setOrderEvaluatePath(orderPath);
            }else if(nameNow.contains("基本信息")){
                tGroupOrder.setOrderInfoPath(orderPath);
            }else if(nameNow.contains("人员名单")){
                tGroupOrder.setOrderPersonDataPath(orderPath);
            }else{
                tGroupOrder.setOrderPath(orderPath);
            }
            tGroupOrder.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupOrder.setUpdateTime(new Date());
            tGroupOrderService.updateById(tGroupOrder);
            return ResultUtil.data(true, orderPath);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error("合同上传异常:" + e.getMessage());
        }

    }
    /**
     * 功能描述：上传订单附件
     *
     * @param file 实体
     * @param fileLicense 实体
     * @param fileEvaluate 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "上传订单附件", type = LogType.OPERATION)
    @ApiOperation("上传订单附件")
    @PostMapping("uploadorderFile")
    public Result<Object> uploadorderFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "fileLicense", required = false) MultipartFile fileLicense, @RequestParam(value = "fileEvaluate", required = false) MultipartFile fileEvaluate, @RequestParam String groupOrderInfo) {
        if (StringUtils.isBlank(groupOrderInfo)) {
            return ResultUtil.error("保存失败:参数为空");
        }
        try {
            String classPath = DocUtil.getClassPath();
            TGroupOrder tGroupOrder = JSONObject.parseObject(groupOrderInfo, TGroupOrder.class);
            String orderId = tGroupOrder.getId();
            //查询是否重复
            if (tGroupOrder != null) {
                if(file!=null){//委托协议
                    //删除旧附件
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderPath())){
                        String orderPathNow = "";
                        orderPathNow = tGroupOrder.getOrderPath();
                        if(StringUtils.isNotBlank(orderPathNow)){
                            String deletePath =  classPath.split(":")[0] + ":" + UploadFileUtils.deletePath+"/"+orderPathNow;
                            PdfUtil.deleteDocx(deletePath);
                        }
                    }
                    //添加新附件
                    if(file.getOriginalFilename().indexOf(".pdf") >= 0 || file.getOriginalFilename().indexOf(".Pdf") >= 0){
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String DataStr = format.format(new Date());

                        String path = classPath.split(":")[0] + ":" + DocUtil.basePath + "order/" + orderId+"协议" + "/" + DataStr+".pdf";
                        String orderPath = "tempfile/wordTemplate/order/"+ orderId+"协议" + "/" + DataStr+".pdf";
                        //获取文件地址
                        //先创建输出文件路径
                        File dest = (new File(path));
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        file.transferTo(dest);
                        tGroupOrder.setOrderPath(orderPath);
                    }
                }
                if(fileLicense!=null){//营业执照
                    //删除旧附件
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderLicensePath())){
                        String orderPathNow = "";
                        orderPathNow = tGroupOrder.getOrderLicensePath();
                        if(StringUtils.isNotBlank(orderPathNow)){
                            String deletePath =  classPath.split(":")[0] + ":" + UploadFileUtils.deletePath+"/"+orderPathNow;
                            PdfUtil.deleteDocx(deletePath);
                        }
                    }
                    //添加新附件
                    if(fileLicense.getOriginalFilename().indexOf(".pdf") >= 0 || fileLicense.getOriginalFilename().indexOf(".Pdf") >= 0){
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String DataStr = format.format(new Date());

                        String path = classPath.split(":")[0] + ":" + DocUtil.basePath + "order/" + orderId+"执照" + "/" + DataStr+".pdf";
                        String orderPath = "tempfile/wordTemplate/order/"+ orderId+"执照" + "/" + DataStr+".pdf";
                        //获取文件地址
                        //先创建输出文件路径
                        File dest = (new File(path));
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        fileLicense.transferTo(dest);
                        tGroupOrder.setOrderLicensePath(orderPath);
                    }
                }
                if(fileEvaluate!=null){//评价报告
                    //删除旧附件
                    if(StringUtils.isNotBlank(tGroupOrder.getOrderEvaluatePath())){
                        String orderPathNow = "";
                        orderPathNow = tGroupOrder.getOrderEvaluatePath();
                        if(StringUtils.isNotBlank(orderPathNow)){
                            String deletePath =  classPath.split(":")[0] + ":" + UploadFileUtils.deletePath+"/"+orderPathNow;
                            PdfUtil.deleteDocx(deletePath);
                        }
                    }
                    //添加新附件
                    if(fileEvaluate.getOriginalFilename().indexOf(".pdf") >= 0 || fileEvaluate.getOriginalFilename().indexOf(".Pdf") >= 0){
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String DataStr = format.format(new Date());

                        String path = classPath.split(":")[0] + ":" + DocUtil.basePath + "order/" + orderId+"评价" + "/" + DataStr+".pdf";
                        String orderPath = "tempfile/wordTemplate/order/"+ orderId+"评价" + "/" + DataStr+".pdf";
                        //获取文件地址
                        //先创建输出文件路径
                        File dest = (new File(path));
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        fileEvaluate.transferTo(dest);
                        tGroupOrder.setOrderEvaluatePath(orderPath);
                    }
                }

                boolean res = tGroupOrderService.saveOrUpdate(tGroupOrder);
                if (res) {
                    return ResultUtil.data(res, "保存成功");
                } else {
                    return ResultUtil.data(res, "保存失败");
                }
            } else {
                return ResultUtil.data(false, "保存失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }
    /**
     * 生成体检编号
     *
     * @return
     */
    public String generatorNum(String type) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = format.format(new Date());
        queryWrapper.apply(StringUtils.isNotBlank(currentDay), "Date(create_time)=STR_TO_Date('" + currentDay + "','%Y-%m-%d')");
        queryWrapper.eq("physical_type", type);
        queryWrapper.orderByDesc("test_num");
        queryWrapper.last("limit 1");
        TGroupPerson one = tGroupPersonService.getOne(queryWrapper);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        String testNum = "";
        if ("职业体检".equals(type)) {
            testNum = "1";
        } else if ("健康体检".equals(type)) {
            testNum = "2";
        } else if ("从业体检".equals(type)) {
            testNum = "3";
        } else if ("放射体检".equals(type)) {
            testNum = "4";
        } else {
            testNum = "5";
        }
        if (one == null) {
            testNum += df.format(new Date());
            testNum += "0001";
        } else {
            String substring = one.getTestNum().substring(one.getTestNum().length() - 4);
            int i = Integer.valueOf(substring);
            i += 1;
            String code = String.valueOf(i);
            if (code.length() == 1) {
                code = "000" + code;
            } else if (code.length() == 2) {
                code = "00" + code;
            } else if (code.length() == 3) {
                code = "0" + code;
            }
            testNum += df.format(new Date());
            testNum += code;
        }
        return testNum;
    }

    /**
     * 功能描述：网报确认
     *
     * @param ids 主键集合
     * @return 返回结果
     */
    @ApiOperation("根据主键来修改团检订单网报确认状态")
    @SystemLog(description = "根据主键来修改团检订单网报确认状态", type = LogType.OPERATION)
    @PostMapping("updateTOrderHistoryStateById")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> updateTOrderHistoryStateById(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag",0);
            queryWrapper.in("id",ids);
            TGroupOrder tGroupOrder = new TGroupOrder();
            tGroupOrder.setDeleteId("1");
            boolean res = tGroupOrderService.update(tGroupOrder,queryWrapper);
            if (res) {
                return ResultUtil.data(res, "确认成功");
            } else {
                return ResultUtil.error("确认失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("确认异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现团检订单查询
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询团检订单数据", type = LogType.OPERATION)
    @ApiOperation("查询团检订单数据")
    @GetMapping("getTGroupOrderList")
    public Result<Object> getTGroupOrderList(TGroupOrder tGroupOrder) {
        try {
            List<TGroupOrder> result = tGroupOrderService.queryAllTGroupOrderList(tGroupOrder);
            result = result.stream().filter(aa->StringUtils.isNotBlank(aa.getGroupUnitName()) ).collect(Collectors.toList());
            Map<String, List<TGroupOrder>> map = result.stream().collect(Collectors.groupingBy(TGroupOrder::getGroupUnitName));
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
