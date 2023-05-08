package com.scmt.healthy.miniapp.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.DocUtil;
import com.scmt.healthy.utils.UploadFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = "小程序端健康/职业/从业体检表单提交的相关数据接口")
@Slf4j
@RequestMapping("/miniapp/tCheckOrg")
public class MiniReservationpersonController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ITGroupOrderService tGroupOrderService;

    @Autowired
    public SocketConfig socketConfig;

    @Autowired
    private ITGroupPersonService tGroupPersonService;

    @Autowired
    private ITOrderGroupService itOrderGroupService;

    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;

    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;

    @Autowired
    private ITBaseProjectService itBaseProjectService;

    @Autowired
    private ITOrderGroupItemProjectService itOrderGroupItemProjectService;

    @Autowired
    private ITOrderGroupService tOrderGroupService;

    @Autowired
    private ITPortfolioProjectService tPortfolioProjectService;

    @Autowired
    private ITComboService tComboService;

    @Autowired
    private ITOrderRecordService tOrderRecordService;

    @Autowired
    private ITOrderSettingService tOrderSettingService;

    @Autowired
    private ITCheckOrgService itCheckOrgService;


    /**
     * 功能描述：新增从业体检订单数据
     *
     * @param map 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增从业体检预约订单数据", type = LogType.OPERATION)
    @ApiOperation("新增从业体检预约订单数据")
    @PostMapping("/addPracticeTGroupOrder")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> addPracticeTGroupOrder(@RequestBody Map<String, Object> map) {
        try {
            TGroupOrder tGroupOrder = new TGroupOrder();
            List<Object> tGroupPersonData = (List<Object>) map.get("tGroupPersonData");
            Object checkDate = map.get("checkDate");
            Object groupUnitId = map.get("groupUnitId");
            Object groupUnitName = map.get("groupUnitName");
            Map<String,Map<String,Object>> mapInfo = new HashMap();
            Map<String,Object> orderInfo = new HashMap<>();
            Map<String,Object> comboInfo = new HashMap();
            Object comboId = map.get("comboId");
            Object checkOrgId = map.get("checkOrgId");
            if (tGroupPersonData == null || tGroupPersonData.size() < 1) {
                return ResultUtil.error("体检人员不能为空！");
            }
            if (groupUnitId == null) {
                return ResultUtil.error("单位id不能为空！");
            }
            if (groupUnitName == null) {
                return ResultUtil.error("单位名称不能为空！");
            }
            if (comboId == null) {
                return ResultUtil.error("套餐id不能为空！");
            }
            if (checkDate == null){
                return ResultUtil.error("预约时间不能为空！");
            }
            //包含多个体检人员提交表单,校验身份证和手机号是否重复
            if(tGroupPersonData != null && tGroupPersonData.size() > 1){
                List<TGroupPerson> personList = new ArrayList<>();
                for (Object groupPersonData:tGroupPersonData) {
                    Object object = groupPersonData;
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(object);
                    TGroupPerson groupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                    personList.add(groupPerson);
                }
                List<String> idCardList = personList.stream().map(TGroupPerson::getIdCard).distinct().collect(Collectors.toList());
                if(personList.size() != idCardList.size()){
                    return ResultUtil.error("体检人员名单中含有重复的身份证!");
                }
                List<String> mobileList = personList.stream().map(TGroupPerson::getMobile).distinct().collect(Collectors.toList());
                if(personList.size() != mobileList.size()){
                    return ResultUtil.error("体检人员名单中含有重复的手机号码!");
                }
            }
            //校验体检人员是否重复提交
            if(tGroupPersonData.size() > 0 && tGroupPersonData != null){
                for (int i = 0; i < tGroupPersonData.size(); i++) {
                    Object object = tGroupPersonData.get(i);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(object);
                    TGroupPerson groupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                    Boolean repeatCommit = tOrderRecordService.getGroupPersonRepeatCommit(groupPerson.getIdCard(), checkDate.toString(), (String) checkOrgId);
                    if(repeatCommit){
                        return ResultUtil.error("添加失败!"+groupPerson.getPersonName()+"该日已有体检预约!");
                    }
                }
            }
            //预约设置容量校验
            TOrderSetting orderSettingInfo = tOrderSettingService.findOrderSettingByCheckOrgAndCheckDate((String) checkOrgId, checkDate.toString());
            if(orderSettingInfo == null){
                return ResultUtil.error("添加失败!查询不到对应的预约设置信息");
            }
            if(orderSettingInfo.getReservations() + tGroupPersonData.size() > orderSettingInfo.getNumber()){
                return ResultUtil.error("添加失败!该日容量还剩"+ (orderSettingInfo.getNumber() - orderSettingInfo.getReservations())+",不满足当前预约总人数");
            }
            /**/
            if (tGroupPersonData.size() > 0 && tGroupPersonData != null) {
                for (int x = 0; x < tGroupPersonData.size(); x++) {
                    String tGroupOrderId = UUID.randomUUID().toString().replaceAll("-", "");

                    tGroupOrder.setGroupUnitId((String) groupUnitId);
                    tGroupOrder.setGroupUnitName((String) groupUnitName);
                    tGroupOrder.setId(tGroupOrderId);
                    tGroupOrder.setDelFlag(0);
                    /* tGroupOrder.setCreateId(securityUtil.getCurrUser().getId());*/
                    tGroupOrder.setCreateTime(new Date());
                    /*tGroupOrder.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());*/
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    /* TGroupOrder one = tGroupOrderService.getOneByWhere(securityUtil.getCurrUser().getDepartmentId());*/
                    //获取当天最新的订单信息
                    TGroupOrder groupOrder = tGroupOrderService.getLastGroupOrderInfo();
                    String orderCode = "";
                    if (groupOrder == null) {
                        if(socketConfig.getUpdateCreateMethd()){
                            orderCode = "6" + dateFormat.format(new Date());
                        }else{
                            orderCode = dateFormat.format(new Date());
                        }
                        orderCode += "0001";
                    } else {
                        String substring = groupOrder.getOrderCode().substring(groupOrder.getOrderCode().length() - 4);
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
                    tGroupOrder.setSigningTime(new Date());
                    tGroupOrder.setPhysicalType("从业体检");
                    tGroupOrder.setOrderCode(orderCode);
                    boolean res = tGroupOrderService.save(tGroupOrder);
                    if (res) {
                        String tOrderGroupId = UUID.randomUUID().toString().replaceAll("-", "");
                        //保存订单分组信息
                        TOrderGroup groupDatum = new TOrderGroup();
                        groupDatum.setGroupOrderId(tGroupOrderId);
                        groupDatum.setComboId((String)comboId);
                        groupDatum.setName("从业体检");
                        groupDatum.setDelFlag(0);
                        groupDatum.setId(tOrderGroupId);
                        /*   groupDatum.setCreateId(securityUtil.getCurrUser().getId());*/
                        groupDatum.setCreateTime(new Date());
                        boolean save = itOrderGroupService.save(groupDatum);
                        if (save) {
                            Object object = tGroupPersonData.get(x);
                            ObjectMapper objectMapper = new ObjectMapper();
                            String json = objectMapper.writeValueAsString(object);
                            TGroupPerson groupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                            if (groupPerson.getAvatar() != null && StringUtils.isNotBlank(groupPerson.getAvatar().toString()) && groupPerson.getAvatar().toString().indexOf("data:image") > -1) {
                                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupOrder.getGroupPerson().getAvatar().toString());
                                String classPath = DocUtil.getClassPath().split(":")[0];
                                //时间戳
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                                String DataStr = format.format(new Date());
                                if (tGroupOrder.getGroupPerson().getTestNum() != null && tGroupOrder.getGroupPerson().getTestNum().trim().length() > 0) {
                                    DataStr = tGroupOrder.getGroupPerson().getTestNum();
                                }
                                String name = imgFile.getOriginalFilename();
                                File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                                //存在则删除
                                if (file1.isFile() && file1.exists()) {
                                    file1.delete();
                                    file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                                }
                                FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
                                String url = "/tempFileUrl/tempfile/dcm/avatar/" + DataStr + "/" + name;
                                groupPerson.setAvatar(url);
                            } else {
                                groupPerson.setAvatar(null);
                            }
                            groupPerson.setOrderId(tGroupOrderId);
                            groupPerson.setGroupId(tOrderGroupId);
                            groupPerson.setIsPass(0);
                            groupPerson.setDelFlag(0);
                            groupPerson.setIsCheck(0);
                            groupPerson.setStatu(0);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            groupPerson.setCheckDate(simpleDateFormat.parse(checkDate.toString()));
                            groupPerson.setUnitId((String) groupUnitId);
                            groupPerson.setDept((String) groupUnitName);
                            groupPerson.setReportPrintingNum(0);
                            groupPerson.setTestNum(generatorNum(tGroupOrder.getPhysicalType()));
                            /*groupPerson.setCreateId(securityUtil.getCurrUser().getId());*/
                            groupPerson.setCreateTime(new Date());
                            boolean personSave = tGroupPersonService.save(groupPerson);
                            if (personSave) {
                                List<TPortfolioProject> projectData = tPortfolioProjectService.getProjectData((String) comboId);
                                if (projectData != null && projectData.size() > 0) {
                                    for (TPortfolioProject i : projectData) {
                                        TOrderGroupItem projectDatum = new TOrderGroupItem();
                                        String tOrderGroupItemId = UUID.randomUUID().toString().replaceAll("-", "");
                                        projectDatum.setId(tOrderGroupItemId);
                                        /*   projectDatum.setCreateId(securityUtil.getCurrUser().getId());*/
                                        projectDatum.setName(i.getName());
                                        projectDatum.setAddress(i.getAddress());
                                        projectDatum.setShortName(i.getShortName());
                                        projectDatum.setSpecimen(i.getSpecimen());
                                        projectDatum.setGroupId(tOrderGroupId);
                                        projectDatum.setGroupOrderId(tGroupOrderId);
                                        projectDatum.setPortfolioProjectId(i.getId());
                                        projectDatum.setOfficeName(i.getOfficeName());
                                        projectDatum.setOfficeId(i.getOfficeId());
                                        projectDatum.setIsFile(i.getIsFile());
                                        projectDatum.setSalePrice(i.getSalePrice());
                                        projectDatum.setDiscount(100);
                                        projectDatum.setDiscountPrice(i.getSalePrice());
                                        projectDatum.setCreateTime(new Date());
                                        projectDatum.setDelFlag(0);
                                        projectDatum.setProjectType(1);

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
                                                    tOrderGroupItemProject.setTOrderGroupItemId(tOrderGroupItemId);
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
                                                    tOrderGroupItemProject.setGroupOrderId(tGroupOrderId);
                                                    tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                    tOrderGroupItemProject.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                                    projectArrayList.add(tOrderGroupItemProject);
                                                }
                                                itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                            }
                                        }
                                    }
                                }
                                //将体检人员添加到预约记录表中
                                if(comboId != null && checkOrgId != null){
                                    TOrderRecord orderRecord = new TOrderRecord();
                                    orderRecord.setPersonId(groupPerson.getId());
                                    TCombo combo = tComboService.getById((String)comboId);
                                    orderRecord.setComboId(combo.getId());
                                    orderRecord.setComboName(combo.getName());
                                    orderRecord.setCheckOrgId((String) checkOrgId);
                                    orderRecord.setGroupOrderId(tGroupOrderId);
                                    orderRecord.setOrderDate(groupPerson.getCheckDate());
                                    orderRecord.setType(groupPerson.getPhysicalType());
                                    orderRecord.setOrderStatus(0);
                                    orderRecord.setCheckStatus(0);
                                    orderRecord.setDelFlag(0);
                                    orderRecord.setCreateTime(new Date());
                                    boolean saveFlag = tOrderRecordService.save(orderRecord);
                                    if(!saveFlag){
                                        return ResultUtil.error( "添加预约记录失败!");
                                    }
                                }
                            }
                        }
                    }
                }
                //更新预约设置信息
                orderSettingInfo.setReservations(orderSettingInfo.getReservations() + tGroupPersonData.size());
                boolean orderSettingSave = tOrderSettingService.updateById(orderSettingInfo);
                if(!orderSettingSave){
                    return ResultUtil.error( "更新预约设置失败!");
                }
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                orderInfo.put("checkDate",checkDate.toString());
                orderInfo.put("unitId",groupUnitId);
                orderInfo.put("unitName",groupUnitName);
                orderInfo.put("createTime",simpleDateFormat1.format(new Date()));

                TCombo tCombo = tComboService.getTCombo((String)comboId);
                comboInfo.put("checkDate",checkDate.toString());
                comboInfo.put("comboId",comboId);
                comboInfo.put("comboName",tCombo.getName());

                mapInfo.put("comboInfo",comboInfo);
                mapInfo.put("orderInfo",orderInfo);
                return ResultUtil.data( mapInfo,"保存成功");
            } else {
                return ResultUtil.error( "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }


    @ApiOperation("新增健康预约体检人员信息和体检项目信息")
    @PostMapping("/saveOrUpdatePersonInfo")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> saveOrUpdatePersonInfo(@RequestBody TGroupPerson tGroupPerson) {
        try {
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            String checkDate = formatDate.format(tGroupPerson.getCheckDate());

            //校验体检人员是否重复提交
            Boolean repeatCommit = tOrderRecordService.getGroupPersonRepeatCommit(tGroupPerson.getIdCard(), checkDate, tGroupPerson.getCheckOrgId());
            if(repeatCommit){
                return ResultUtil.error( "添加失败!"+tGroupPerson.getPersonName()+"该日已有体检预约!");
            }
            //预约设置容量校验
            TOrderSetting orderSettingInfo = tOrderSettingService.findOrderSettingByCheckOrgAndCheckDate(tGroupPerson.getCheckOrgId(), formatDate.format(tGroupPerson.getCheckDate()));
            if(orderSettingInfo == null){
                return ResultUtil.error("添加失败!查询不到对应的预约设置信息");
            }
            if(orderSettingInfo.getReservations() + 1 > orderSettingInfo.getNumber()){
                return ResultUtil.error("添加失败!该日预约人数已达上限!");
            }
            List<TOrderGroupItem> tOrderGroupItems = null;
            String orderId = UUID.randomUUID().toString().replaceAll("-", "");
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            if (tGroupPerson.getAvatar() != null && StringUtils.isNotBlank(tGroupPerson.getAvatar().toString()) && tGroupPerson.getAvatar().toString().indexOf("data:image/") > -1) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupPerson.getAvatar().toString());
                String classPath = DocUtil.getClassPath().split(":")[0];
                //时间戳
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String DataStr = format.format(new Date());
                if (tGroupPerson.getTestNum() != null && tGroupPerson.getTestNum().trim().length() > 0) {
                    DataStr = tGroupPerson.getTestNum();
                }
                String name = imgFile.getOriginalFilename();
                File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                //存在则删除
                if (file1.isFile() && file1.exists()) {
                    file1.delete();
                    file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                }
                FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
                String url = "/tempFileUrl/tempfile/dcm/avatar/" + DataStr + "/" + name;
                tGroupPerson.setAvatar(url);
            } else {
                tGroupPerson.setAvatar(null);
            }
            if (StringUtils.isBlank(tGroupPerson.getId())) {
                if (tGroupPerson != null && tGroupPerson.getPhysicalType() != null && tGroupPerson.getPhysicalType().equals("健康体检")) {
                    tGroupPerson.setIsWzCheck(1);//默认设置问诊科已检
                } else {
                    tGroupPerson.setIsWzCheck(0);
                }
                tGroupPerson.setIsPass(0);
                tGroupPerson.setDelFlag(0);
                tGroupPerson.setCreateTime(new Date());
                tGroupPerson.setOrderId(orderId);
                tGroupPerson.setGroupId(groupId);
                /*tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());*/
                tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
            } else {
                tGroupPerson.setUpdateTime(new Date());
                /*tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());*/
                QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                groupItemQueryWrapper.eq("group_order_id", tGroupPerson.getOrderId());
                groupItemQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
                tOrderGroupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
                //返回每个list的第一个值，
                List<String> itemIds = itOrderGroupItemService.listObjs(groupItemQueryWrapper, new Function<Object, String>() {
                    @Override
                    public String apply(Object o) {
                        return o.toString();
                    }
                });
            }
            List<String> groupItemProjetIds = new ArrayList<>();
            //拿出所有组合项目id
            if (tOrderGroupItems != null && tOrderGroupItems.size() > 0) {
                groupItemProjetIds = tOrderGroupItems.stream().map(TOrderGroupItem::getPortfolioProjectId).collect(Collectors.toList());
            }

            //判断是否重新保存组合项
            boolean addNew = false;
            //订单id为空，且属于零星体检 不创建订单
//            if (StringUtils.isNotBlank(tGroupPerson.getOrderId())) {
//                addNew = true;
                tGroupPerson.setOrderId(orderId);
//                if (tGroupPerson.getUnitId() != null && StringUtils.isNotBlank(tGroupPerson.getUnitId())) {
                    TGroupOrder tGroupOrder = new TGroupOrder();
                    tGroupOrder.setId(orderId);//ID
                    tGroupOrder.setPayStatus(1);//订单确认状态
                    tGroupOrder.setDelFlag(0);//是否删除
                    tGroupOrder.setCreateTime(new Date());//创建时间
                   /* tGroupOrder.setCreateId(securityUtil.getCurrUser().getId());//创建人
                    tGroupOrder.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());//所属部门*/
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    /* TGroupOrder one = tGroupOrderService.getOneByWhere(securityUtil.getCurrUser().getDepartmentId());*/
                    //获取当天最新的订单信息
                    TGroupOrder groupOrder = tGroupOrderService.getLastGroupOrderInfo();
                    String orderCode = "";
                    if (groupOrder == null) {
                        if (socketConfig.getUpdateCreateMethd()) {
                            orderCode = "6" + dateFormat.format(new Date());
                        } else {
                            orderCode = dateFormat.format(new Date());
                        }
                        orderCode += "0001";
                    } else {
                        String substring = groupOrder.getOrderCode().substring(groupOrder.getOrderCode().length() - 4);
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
                        if (socketConfig.getUpdateCreateMethd()) {
                            orderCode = "6" + dateFormat.format(new Date());
                        } else {
                            orderCode = dateFormat.format(new Date());
                        }
                        orderCode += code;
                    }
                    tGroupOrder.setOrderCode(orderCode);//编号
//                    tGroupOrder.setGroupUnitId(tGroupPerson.getUnitId());//团检单位id
                    tGroupOrder.setGroupUnitId("");
                    tGroupOrder.setGroupUnitName("");
//                    tGroupOrder.setGroupUnitName(tGroupPerson.getDept());//团检单位名称
                    tGroupOrder.setPhysicalType(tGroupPerson.getPhysicalType());//团检类型
                    /*  tGroupOrder.setSalesDirector(securityUtil.getCurrUser().getId());//销售负责人*/
                    /*       tGroupOrder.setSalesDirectorName(securityUtil.getCurrUser().getNickname());//销售负责人姓名*/
                    tGroupOrder.setSigningTime(new Date());//签订日期
                    tGroupOrder.setDeliveryTime(new Date());//交付日期
                    tGroupOrder.setAuditState(-1);//审核状态
                    tGroupOrder.setSporadicPhysical(1);//是否零星
                    //添加订单
                    boolean resOrder = tGroupOrderService.saveOrUpdate(tGroupOrder);
                    if (!resOrder) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常：" + "订单添加失败");
                    }
//                } else {//若没有单位，则只创建分组 分组和订单id用同一个
                    groupId = orderId;
//                }
//            } else if (StringUtils.isBlank(tGroupPerson.getOrderId())) {
//                tGroupPerson.setOrderId(orderId);
//            }
            TCombo tCombo = null;
            if (StringUtils.isBlank(null)) {
                addNew = true;
                tGroupPerson.setGroupId(groupId);
                tCombo = tComboService.getTCombo(tGroupPerson.getComboId());
                if (tCombo != null) {
                    /* TOrderGroup tOrderGroup = tGroupPerson.getGroupData();*/
                    TOrderGroup tOrderGroup = new TOrderGroup();
                    tOrderGroup.setId(groupId);//ID
                    tOrderGroup.setName(tCombo.getName());
                    tOrderGroup.setComboId(tCombo.getId());
                    if (StringUtils.isBlank(tOrderGroup.getName())) {
                        String name = "";
                        if (StringUtils.isBlank(name)) {
                            if (tGroupPerson.getPhysicalType().equals("职业体检") || tGroupPerson.getPhysicalType().equals("放射体检")) {
                                if (tGroupPerson.getHazardFactorsText() != null && StringUtils.isNotBlank(tGroupPerson.getHazardFactorsText()) && tGroupPerson.getWorkStateText() != null && StringUtils.isNotBlank(tGroupPerson.getWorkStateText())) {
                                    name = tGroupPerson.getHazardFactorsText().replaceAll(" ", "") + "[" + tGroupPerson.getWorkStateText().replaceAll(" ", "") + "]";
                                }
                            } else {
                                if (StringUtils.isNotBlank(tGroupPerson.getGroupName())) {
                                    name = tGroupPerson.getGroupName();
                                } else if (StringUtils.isNotBlank(tGroupPerson.getDept())) {
                                    name = tGroupPerson.getDept();
                                } else {
                                    name = tGroupPerson.getPersonName();
                                }
                            }
                        }
                        tOrderGroup.setName(name);//分组名称
                    }
                    tOrderGroup.setDelFlag(0);//是否删除
                    tOrderGroup.setCreateTime(new Date());//创建时间
                    /*  tOrderGroup.setCreateId(securityUtil.getCurrUser().getId());//创建人*/
                    tOrderGroup.setGroupOrderId(orderId);//订单id
                    //添加分组
                    boolean resGroup = tOrderGroupService.saveOrUpdate(tOrderGroup);
                    if (!resGroup) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常：" + "分组添加失败");
                    }
                }
            }
            //监测类型
            if (tGroupPerson != null && (tGroupPerson.getJcType() == null || StringUtils.isBlank(tGroupPerson.getJcType()))) {
                tGroupPerson.setJcType("1");
            }
            boolean resp = tGroupPersonService.saveOrUpdate(tGroupPerson);
            if (!resp) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error("保存异常：" + "人员信息保存失败");
            }
            //添加项目
            if (addNew) {
                /*List<TOrderGroupItem> projectData = tGroupPerson.getProjectData();*/

                List<TPortfolioProject> projectData = tPortfolioProjectService.getProjectData(tGroupPerson.getComboId());
                List<String> finalGroupItemProjetIds = groupItemProjetIds;
                for (TPortfolioProject i : projectData) {
                    String orderGroupItemID = UUID.randomUUID().toString().replaceAll("-", "");
                    TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                    if (!finalGroupItemProjetIds.contains(i.getId())) {
                        tOrderGroupItem.setId(orderGroupItemID);
                        tOrderGroupItem.setName(i.getName());
                        tOrderGroupItem.setShortName(i.getShortName());
                        tOrderGroupItem.setAddress(i.getAddress());
                        tOrderGroupItem.setSpecimen(i.getSpecimen());
                        tOrderGroupItem.setDiagnostic(i.getDiagnostic());
                        tOrderGroupItem.setOfficeName(i.getOfficeName());
                        tOrderGroupItem.setOfficeId(i.getOfficeId());
                        tOrderGroupItem.setPortfolioProjectId(i.getId());
                        tOrderGroupItem.setIsFile(i.getIsFile());
                        tOrderGroupItem.setUrl(i.getUrl());
                        tOrderGroupItem.setSalePrice(i.getSalePrice());
                        tOrderGroupItem.setDiscount(100);
                        tOrderGroupItem.setDiscountPrice(i.getSalePrice());
                        tOrderGroupItem.setCreateTime(new Date());
                        /* tOrderGroupItem.setCreateId(securityUtil.getCurrUser().getId());*/
                        tOrderGroupItem.setDelFlag(0);
                        tOrderGroupItem.setGroupId(groupId);
                        tOrderGroupItem.setGroupOrderId(orderId);
                        tOrderGroupItem.setProjectType(1);
                        boolean save1 = itOrderGroupItemService.save(tOrderGroupItem);
                        if (save1) {
                            //保存分组项目的子项目
                            ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(i.getId());
                            if (list != null && list.size() > 0) {
                                List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                                ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                for (TBaseProject tBaseProject : tBaseProjects) {
                                    TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                    tOrderGroupItemProject.setTOrderGroupItemId(orderGroupItemID);
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
                                    tOrderGroupItemProject.setGroupOrderId(orderId);
                                    tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                    projectArrayList.add(tOrderGroupItemProject);
                                }
                                boolean resO = itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                if (!resO) {
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return ResultUtil.error("保存异常：" + "保存分组项目‘" + i.getName() + "’ 的基础项目保存失败");
                                }
                            } else {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("保存异常：" + "分组项目‘" + i.getName() + "’未绑定基础项目");
                            }
                        } else {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("保存异常：" + "分组项目保存失败");
                        }
                    }
                }
            }
            //将预约数据添加到预约记录表中
            if(tGroupPerson.getId() != null && tGroupPerson.getCheckOrgId() != null){
                TOrderRecord orderRecord = new TOrderRecord();
                orderRecord.setCheckOrgId(tGroupPerson.getCheckOrgId());
                orderRecord.setPersonId(tGroupPerson.getId());
                orderRecord.setComboId(tCombo.getId());
                orderRecord.setComboName(tCombo.getName());
                orderRecord.setOrderDate(tGroupPerson.getCheckDate());
                orderRecord.setType(tGroupPerson.getPhysicalType());
                orderRecord.setGroupOrderId(orderId);
                orderRecord.setOrderStatus(0);
                orderRecord.setCheckStatus(0);
                orderRecord.setDelFlag(0);
                orderRecord.setCreateTime(new Date());
                boolean record = tOrderRecordService.save(orderRecord);
                if(!record){
                    return ResultUtil.error("添加预约记录异常!");
                }
            }
            //更新对应的预约设置信息
            orderSettingInfo.setReservations(orderSettingInfo.getReservations() + 1);
            boolean saveSetting = tOrderSettingService.updateById(orderSettingInfo);
            if(!saveSetting){
                return ResultUtil.error("更新预约设置信息失败!");
            }
            //返回数据
            Map<String,Map<String,Object>> map = new HashMap();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //预约信息
            Map<String,Object> orderInfo = new HashMap();
            orderInfo.put("personId",tGroupPerson.getId());
            orderInfo.put("personName",tGroupPerson.getPersonName());
            orderInfo.put("mobile",tGroupPerson.getMobile());
            if(tGroupPerson.getCheckDate() != null){
                orderInfo.put("checkDate",simpleDateFormat.format(tGroupPerson.getCheckDate()));
            }
            orderInfo.put("testNum",tGroupPerson.getTestNum());
            orderInfo.put("orderCode",tGroupOrder.getOrderCode());
            orderInfo.put("createTime",simpleDateFormat1.format(tGroupOrder.getCreateTime()));
            //套餐信息
            Map<String,Object> comboInfo = new HashMap();
            comboInfo.put("comboId",tCombo.getId());
            comboInfo.put("comboName",tCombo.getName());
            if(tGroupPerson.getCheckDate() != null){
                comboInfo.put("checkDate",simpleDateFormat.format(tGroupPerson.getCheckDate()));
            }
            map.put("orderInfo",orderInfo);
            map.put("comboInfo",comboInfo);
            return ResultUtil.data(map,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }


    @ApiOperation("新增职业体检预约体检人员信息和体检项目信息")
    @PostMapping("/saveOrUpdateCareerPersonInfo")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> saveOrUpdateCareerPersonInfo(@RequestBody Map<String, Object> map) {
        try {
            List<TOrderGroupItem> tOrderGroupItems = null;
            List<Object> tGroupPersonData = (List<Object>) map.get("tGroupPersonData");
            Object groupUnitId = map.get("groupUnitId");
            Object checkOrgId = map.get("checkOrgId");
            Object checkDate = map.get("checkDate");
            Object groupUnitName = map.get("groupUnitName");
            Object comboId = map.get("comboId");
            List<Object> comboIds = (List<Object>) map.get("comboIds");
            String tComboName = "";
            String tComboId = "";

            Map<String,Map<String,Object>> mapInfo = new HashMap();
            Map<String,Object> orderInfo = new HashMap<>();
            Map<String,Object> comboInfo = new HashMap();
            TGroupOrder temp = new TGroupOrder();
            Object sporadicPhysical = map.get("sporadicPhysical");
            if (tGroupPersonData == null || tGroupPersonData.size() < 1) {
                return ResultUtil.error("体检人员不能为空！");
            }
            if (groupUnitId == null) {
                return ResultUtil.error("单位id不能为空！");
            }
            if (groupUnitName == null) {
                return ResultUtil.error("单位名称不能为空！");
            }
            if (comboIds == null && comboIds.size() > 0) {
                return ResultUtil.error("套餐id不能为空！");
            }
            if(checkDate == null && checkDate == ""){
                return ResultUtil.error("体检时间不能为空!！");
            }
            if(checkOrgId == null && checkOrgId == ""){
                return ResultUtil.error("体检机构id不能为空!！");
            }

            //包含多个体检人员提交表单,校验身份证和手机号是否重复
            if(tGroupPersonData.size() > 1 && tGroupPersonData != null){
                List<TGroupPerson> personList = new ArrayList<>();
                for (Object groupPersonData:tGroupPersonData) {
                    Object object = groupPersonData;
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(object);
                    TGroupPerson groupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                    personList.add(groupPerson);
                }
                List<String> idCardList = personList.stream().map(TGroupPerson::getIdCard).distinct().collect(Collectors.toList());
                if(personList.size() != idCardList.size()){
                    return ResultUtil.error("体检人员名单中含有重复的身份证!");
                }
                List<String> mobileList = personList.stream().map(TGroupPerson::getMobile).distinct().collect(Collectors.toList());
                if(personList.size() != mobileList.size()){
                    return ResultUtil.error("体检人员名单中含有重复的手机号码!");
                }
            }
            //校验体检人员是否重复提交
            if(tGroupPersonData.size() > 0 && tGroupPersonData != null){
                for (int i = 0; i < tGroupPersonData.size(); i++) {
                    Object object = tGroupPersonData.get(i);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(object);
                    TGroupPerson groupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                    Boolean repeatCommit = tOrderRecordService.getGroupPersonRepeatCommit(groupPerson.getIdCard(), checkDate.toString(), (String) checkOrgId);
                    if(repeatCommit){
                        return ResultUtil.error("添加失败!"+groupPerson.getPersonName()+"该日已有体检预约!");
                    }
                }
            }
            //预约设置容量校验
            TOrderSetting orderSettingInfo = tOrderSettingService.findOrderSettingByCheckOrgAndCheckDate((String) checkOrgId, checkDate.toString());
            if(orderSettingInfo == null){
                return ResultUtil.error("添加失败!查询不到对应的预约设置信息!");
            }
            if(orderSettingInfo.getReservations() + tGroupPersonData.size() > orderSettingInfo.getNumber()){
                return ResultUtil.error("添加失败!该日预约容量还剩"+(orderSettingInfo.getNumber() - orderSettingInfo.getReservations())+",不满足当前预约总人数");
            }
            if (tGroupPersonData.size() > 0 && tGroupPersonData!=null){
                String orderId = UUID.randomUUID().toString().replaceAll("-", "");
                String groupId = UUID.randomUUID().toString().replaceAll("-", "");
                TCombo tCombo = new TCombo();
                for (int y = 0; y < comboIds.size(); y++) {
                    Object objects = comboIds.get(y);
                    tCombo = tComboService.getTCombo((String) objects);

                    tComboName += tCombo.getName()+"|";
                    tComboId += tCombo.getId()+",";
                }
                tComboName = tComboName.substring(0,tComboName.length()-1);
                tComboId = tComboId.substring(0,tComboId.length()-1);
                for (int x = 0; x <tGroupPersonData.size() ; x++) {
                    Object object = tGroupPersonData.get(x);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(object);
                    TGroupPerson tGroupPerson = JSONUtil.toBean(json, TGroupPerson.class);
                    if (tGroupPerson.getAvatar() != null && StringUtils.isNotBlank(tGroupPerson.getAvatar().toString()) && tGroupPerson.getAvatar().toString().indexOf("data:image/") > -1) {
                        MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupPerson.getAvatar().toString());
                        String classPath = DocUtil.getClassPath().split(":")[0];
                        //时间戳
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String DataStr = format.format(new Date());
                        if (tGroupPerson.getTestNum() != null && tGroupPerson.getTestNum().trim().length() > 0) {
                            DataStr = tGroupPerson.getTestNum();
                        }
                        String name = imgFile.getOriginalFilename();
                        File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                        //存在则删除
                        if (file1.isFile() && file1.exists()) {
                            file1.delete();
                            file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                        }
                        FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
                        String url = "/tempFileUrl/tempfile/dcm/avatar/" + DataStr + "/" + name;
                        tGroupPerson.setAvatar(url);
                    } else {
                        tGroupPerson.setAvatar(null);
                    }
                    if (StringUtils.isBlank(tGroupPerson.getId())) {
                        if (tGroupPerson != null && tGroupPerson.getPhysicalType() != null && tGroupPerson.getPhysicalType().equals("健康体检")) {
                            tGroupPerson.setIsWzCheck(1);//默认设置问诊科已检
                        } else {
                            tGroupPerson.setIsWzCheck(0);
                        }
                        tGroupPerson.setIsPass(0);
                        tGroupPerson.setDelFlag(0);
                        tGroupPerson.setCreateTime(new Date());
                        tGroupPerson.setOrderId(groupId);
                        tGroupPerson.setGroupId(orderId);
                        tGroupPerson.setUnitName((String) groupUnitName);
                        tGroupPerson.setUnitId((String) groupUnitId);
                        tGroupPerson.setDept((String) groupUnitName);
                        /*tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());*/
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        tGroupPerson.setCheckDate(simpleDateFormat.parse(checkDate.toString()));
                        tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
                    } else {
                        tGroupPerson.setUpdateTime(new Date());
                        /*tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());*/
                        QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
                        groupItemQueryWrapper.eq("group_order_id", tGroupPerson.getOrderId());
                        groupItemQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
                        tOrderGroupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
                        //返回每个list的第一个值，
                        List<String> itemIds = itOrderGroupItemService.listObjs(groupItemQueryWrapper, new Function<Object, String>() {
                            @Override
                            public String apply(Object o) {
                                return o.toString();
                            }
                        });
                    }
                    List<String> groupItemProjetIds = new ArrayList<>();
                    //拿出所有组合项目id
                    if (tOrderGroupItems != null && tOrderGroupItems.size() > 0) {
                        groupItemProjetIds = tOrderGroupItems.stream().map(TOrderGroupItem::getPortfolioProjectId).collect(Collectors.toList());
                    }

                    //判断是否重新保存组合项
                    boolean addNew = false;

                    //订单id为空，且属于零星体检 不创建订单
//                    if (sporadicPhysical != null && !sporadicPhysical.equals(1)) {
//                        addNew = true;
//                        tGroupPerson.setOrderId(groupId);
//                        if (groupUnitId != null && StringUtils.isNotBlank((String)groupUnitId)) {
                            TGroupOrder tGroupOrder = new TGroupOrder();
                            tGroupOrder.setId(groupId);//ID
                            tGroupOrder.setPayStatus(1);//订单确认状态
                            tGroupOrder.setDelFlag(0);//是否删除
                            tGroupOrder.setCreateTime(new Date());//创建时间
                   /* tGroupOrder.setCreateId(securityUtil.getCurrUser().getId());//创建人
                    tGroupOrder.setDepartmentId(securityUtil.getCurrUser().getDepartmentId());//所属部门*/
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                            /* TGroupOrder one = tGroupOrderService.getOneByWhere(securityUtil.getCurrUser().getDepartmentId());*/
                            //获取当天最新的订单信息
                    TGroupOrder groupOrder = tGroupOrderService.getLastGroupOrderInfo();
                    String orderCode = "";
                    if (groupOrder == null) {
                        if (socketConfig.getUpdateCreateMethd()) {
                            orderCode = "6" + dateFormat.format(new Date());
                        } else {
                            orderCode = dateFormat.format(new Date());
                        }
                        orderCode += "0001";
                    } else {
                        String substring = groupOrder.getOrderCode().substring(groupOrder.getOrderCode().length() - 4);
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
                        if (socketConfig.getUpdateCreateMethd()) {
                            orderCode = "6" + dateFormat.format(new Date());
                        } else {
                            orderCode = dateFormat.format(new Date());
                        }
                        orderCode += code;
                    }
                            tGroupOrder.setOrderCode(orderCode);//编号
                            tGroupOrder.setGroupUnitId((String)groupUnitId);//团检单位id
                            tGroupOrder.setGroupUnitName((String)groupUnitName);//团检单位名称
                            tGroupOrder.setPhysicalType(tGroupPerson.getPhysicalType());//团检类型
                            /*  tGroupOrder.setSalesDirector(securityUtil.getCurrUser().getId());//销售负责人*/
                            /*       tGroupOrder.setSalesDirectorName(securityUtil.getCurrUser().getNickname());//销售负责人姓名*/
                            tGroupOrder.setSigningTime(new Date());//签订日期
                            tGroupOrder.setDeliveryTime(new Date());//交付日期
                            tGroupOrder.setAuditState(-1);//审核状态
                            tGroupOrder.setSporadicPhysical(1);//是否零星
//                            tGroupOrder.setPackagePrice();
                            //订单总人数
                            tGroupOrder.setPersonCount(tGroupPersonData.size());
                            //添加订单
                            boolean resOrder = tGroupOrderService.saveOrUpdate(tGroupOrder);
                            if (!resOrder) {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("保存异常：" + "订单添加失败");
                            }
                            temp = tGroupOrder;
//                        } else {//若没有单位，则只创建分组 分组和订单id用同一个
//                            groupId = orderId;
//                        }
//                    } else if (StringUtils.isBlank(tGroupPerson.getOrderId())) {
//                        tGroupPerson.setOrderId(orderId);
//                    }
                    if (StringUtils.isBlank(null)) {
                        addNew = true;
                        /* TCombo tCombo = tComboService.getTCombo((String)comboId);*/

                        tGroupPerson.setGroupId(orderId);


                        if (tCombo != null) {
                            /* TOrderGroup tOrderGroup = tGroupPerson.getGroupData();*/
                            TOrderGroup tOrderGroup = new TOrderGroup();
                            tOrderGroup.setId(orderId);//ID
                            tOrderGroup.setName(tComboName);
                            tOrderGroup.setComboId(tComboId);
                            if (StringUtils.isBlank(tOrderGroup.getName())) {
                                String name = "";
                                if (StringUtils.isBlank(name)) {
                                    if (tGroupPerson.getPhysicalType().equals("职业体检") || tGroupPerson.getPhysicalType().equals("放射体检")) {
                                        if (tGroupPerson.getHazardFactorsText() != null && StringUtils.isNotBlank(tGroupPerson.getHazardFactorsText()) && tGroupPerson.getWorkStateText() != null && StringUtils.isNotBlank(tGroupPerson.getWorkStateText())) {
                                            name = tGroupPerson.getHazardFactorsText().replaceAll(" ", "") + "[" + tGroupPerson.getWorkStateText().replaceAll(" ", "") + "]";
                                        }
                                    } else {
                                        if (StringUtils.isNotBlank(tGroupPerson.getGroupName())) {
                                            name = tGroupPerson.getGroupName();
                                        } else if (StringUtils.isNotBlank(tGroupPerson.getDept())) {
                                            name = tGroupPerson.getDept();
                                        } else {
                                            name = tGroupPerson.getPersonName();
                                        }
                                    }
                                }
                                tOrderGroup.setName(name);//分组名称
                            }
                            tOrderGroup.setDelFlag(0);//是否删除
                            tOrderGroup.setCreateTime(new Date());//创建时间
                            /*  tOrderGroup.setCreateId(securityUtil.getCurrUser().getId());//创建人*/
                            tOrderGroup.setGroupOrderId(groupId);//订单id
                            //添加分组
                            boolean resGroup = tOrderGroupService.saveOrUpdate(tOrderGroup);
                            if (!resGroup) {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("保存异常：" + "分组添加失败");
                            }
                        }
                    }
                    //监测类型
                    if (tGroupPerson != null && (tGroupPerson.getJcType() == null || StringUtils.isBlank(tGroupPerson.getJcType()))) {
                        tGroupPerson.setJcType("1");
                    }
                    boolean resp = tGroupPersonService.saveOrUpdate(tGroupPerson);
                    if (!resp) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存异常：" + "人员信息保存失败");
                    }
                    //添加项目
                    if (addNew) {
                        /*List<TOrderGroupItem> projectData = tGroupPerson.getProjectData();*/

                        /*List<TPortfolioProject> projectData = tPortfolioProjectService.getProjectData((String)comboId);*/
                        if(x == 0){
                            List<TPortfolioProject> projectData = tPortfolioProjectService.getProjectList(comboIds);
                            List<TPortfolioProject> collect = projectData.stream().distinct().collect(Collectors.toList());
                            List<String> finalGroupItemProjetIds = groupItemProjetIds;
                            for (TPortfolioProject i : collect) {
                                String orderGroupItemID = UUID.randomUUID().toString().replaceAll("-", "");
                                TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                                if (!finalGroupItemProjetIds.contains(i.getId())) {
                                    tOrderGroupItem.setId(orderGroupItemID);
                                    tOrderGroupItem.setName(i.getName());
                                    tOrderGroupItem.setShortName(i.getShortName());
                                    tOrderGroupItem.setAddress(i.getAddress());
                                    tOrderGroupItem.setSpecimen(i.getSpecimen());
                                    tOrderGroupItem.setDiagnostic(i.getDiagnostic());
                                    tOrderGroupItem.setOfficeName(i.getOfficeName());
                                    tOrderGroupItem.setOfficeId(i.getOfficeId());
                                    tOrderGroupItem.setPortfolioProjectId(i.getId());
                                    tOrderGroupItem.setIsFile(i.getIsFile());
                                    tOrderGroupItem.setUrl(i.getUrl());
                                    tOrderGroupItem.setSalePrice(i.getSalePrice());
                                    tOrderGroupItem.setDiscount(100);
                                    tOrderGroupItem.setDiscountPrice(i.getSalePrice());
                                    tOrderGroupItem.setCreateTime(new Date());
                                    /* tOrderGroupItem.setCreateId(securityUtil.getCurrUser().getId());*/
                                    tOrderGroupItem.setDelFlag(0);
                                    tOrderGroupItem.setGroupId(orderId);
                                    tOrderGroupItem.setGroupOrderId(groupId);
                                    tOrderGroupItem.setProjectType(1);
                                    boolean save1 = itOrderGroupItemService.save(tOrderGroupItem);
                                    if (save1) {
                                        //保存分组项目的子项目
                                        ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(i.getId());
                                        if (list != null && list.size() > 0) {
                                            List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                                            ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                            for (TBaseProject tBaseProject : tBaseProjects) {
                                                TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                                tOrderGroupItemProject.setTOrderGroupItemId(orderGroupItemID);
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
                                                tOrderGroupItemProject.setGroupOrderId(orderId);
                                                tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                                projectArrayList.add(tOrderGroupItemProject);
                                            }
                                            boolean resO = itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                            if (!resO) {
                                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                                return ResultUtil.error("保存异常：" + "保存分组项目‘" + i.getName() + "’ 的基础项目保存失败");
                                            }
                                        } else {
                                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                            return ResultUtil.error("保存异常：" + "分组项目‘" + i.getName() + "’未绑定基础项目");
                                        }
                                    } else {
                                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                        return ResultUtil.error("保存异常：" + "分组项目保存失败");
                                    }
                                }
                            }
                        }

                    }
                    //将体检人员添加到预约记录表中
                    if(comboIds != null && checkOrgId != null){
                        TOrderRecord orderRecord = new TOrderRecord();
                        orderRecord.setPersonId(tGroupPerson.getId());
                        orderRecord.setCheckOrgId((String)checkOrgId);
                        String tComboIds = "";
                        String tComboNames = "";
                        for (Object ids:comboIds) {
                            TCombo combo = tComboService.getById((String) ids);
                            tComboIds = tComboIds + combo.getId() + ";";
                            tComboNames = tComboNames + combo.getName() + ";";
                        }
                        orderRecord.setComboId(tComboIds);
                        orderRecord.setComboName(tComboNames);
                        orderRecord.setGroupOrderId(groupId);
                        orderRecord.setOrderDate(tGroupPerson.getCheckDate());
                        orderRecord.setType(tGroupPerson.getPhysicalType());
                        orderRecord.setOrderStatus(0);
                        orderRecord.setCheckStatus(0);
                        orderRecord.setDelFlag(0);
                        orderRecord.setCreateTime(new Date());
                        boolean saveFlag = tOrderRecordService.save(orderRecord);
                        if(!saveFlag){
                            return ResultUtil.error("添加预约记录失败!");
                        }
                    }
                }
            }
            //更新预约设置信息
            orderSettingInfo.setReservations(orderSettingInfo.getReservations() + tGroupPersonData.size());
            boolean orderSettingSave = tOrderSettingService.updateById(orderSettingInfo);
            if(!orderSettingSave){
                return ResultUtil.error("更新预约设置信息失败!");
            }
            //预约信息和订单信息
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orderInfo.put("checkDate",checkDate.toString());
            orderInfo.put("unitId",groupUnitId);
            orderInfo.put("unitName",groupUnitName);
            orderInfo.put("orderCode",temp.getOrderCode());
            orderInfo.put("createTime",simpleDateFormat1.format(temp.getCreateTime()));
            //套餐信息
            comboInfo.put("checkDate",checkDate.toString());
            comboInfo.put("comboId",tComboId);
            comboInfo.put("comboName",tComboName);

            mapInfo.put("comboInfo",comboInfo);
            mapInfo.put("orderInfo",orderInfo);
            return ResultUtil.data(mapInfo,"登记成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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

}
