package com.scmt.healthy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = " 分检结果数据接口")
@RequestMapping("/scmt/tDepartResult")
public class TDepartResultController {
    @Autowired
    private ITDepartResultService tDepartResultService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITOrderGroupService orderGroupService;
    @Autowired
    private ITOrderGroupItemService itemService;
    @Autowired
    private ITPortfolioProjectService portfolioProjectService;
    @Autowired
    private IRelationBasePortfolioService relationBasePortService;
    @Autowired
    private ITBaseProjectService baseProjectService;
    @Autowired
    private IRelationProjectReferenceService referenceService;
    @Autowired
    private ITDepartResultService departResultService;
    @Autowired
    private ITDepartItemResultService itemResultService;
    @Autowired
    private ITGroupPersonService personService;
    @Autowired
    private ITOrderGroupItemProjectService itemProjectService;
    @Autowired
    private ITReviewProjectService reviewProjectService;
    @Autowired
    private IRelationPersonProjectCheckService iRelationPersonProjectCheckService;

    @Autowired
    private ITReviewPersonService itReviewPersonService;

    /**
     * 功能描述：添加组合项目检查结果以及添加基础项目结果
     *
     * @param form 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "添加组合项目检查结果以及添加基础项目结果", type = LogType.OPERATION)
    @ApiOperation("添加组合项目检查结果以及添加基础项目结果")
    @PostMapping("addTDepartResult")
    @Transactional(rollbackOn = { Exception.class })
    public Result<Object> addTDepartResult(@RequestBody String form) {
        try {
            if (StringUtils.isBlank(form)) {
                return ResultUtil.error("参数为空，请联系管理员！");
            }
            String currendId = securityUtil.getCurrUser().getId();

            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            JSONObject jsonObjectGroup = JSON.parseObject(formStr);

            //组合项目检查结果
            String departResultStr = jsonObjectGroup.getString("groupResult");
            TOrderGroupItem groupItem = JSON.parseObject(departResultStr, TOrderGroupItem.class);
            //基础项目检查结果
            String departItemResultStr = jsonObjectGroup.getString("groupItemResult");
            List<TOrderGroupItemProject> tOrderGroupItemProjects = JSON.parseArray(departItemResultStr, TOrderGroupItemProject.class);

            String officeId = groupItem.getOfficeId();
            String officeName = groupItem.getOfficeName();
            if (groupItem == null) {
                return ResultUtil.error("组合项结果参数为空，请联系管理员！");
            }
            if (tOrderGroupItemProjects == null) {
                return ResultUtil.error("基础项结果参数为空，请联系管理员！");
            }

            TDepartResult dr = groupItem.getDepartResult();
            if (StringUtils.isNotBlank( dr.getGroupItemName()) && dr.getGroupItemName().indexOf("(复)") > -1) {
                dr.setIsRecheck(1);
            }
            dr.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            dr.setIsFile(groupItem.getIsFile());
            dr.setUrl(groupItem.getDepartResult().getUrl());
            String personId = dr.getPersonId();

            //检查人员信息
            TReviewPerson tReviewPerson = itReviewPersonService.getById(personId);
            String personIdNow = "";
            Boolean isUpdate = false;
            if(tReviewPerson!=null && tReviewPerson.getFirstPersonId()!=null && StringUtils.isNotBlank(tReviewPerson.getFirstPersonId())){
                personIdNow = tReviewPerson.getFirstPersonId();
                isUpdate = true;
            }else{
                personIdNow = personId;
                isUpdate = false;
            }
            TGroupPerson byId = personService.getById(personIdNow);
            byId.setAvatar(null);
            TDepartResult one  = null;
            String oldgroup_item_id = null;
            boolean isAddRes = false;//是否是新增
            if (dr != null) {
                //界面未刷新 可能提交多条。
                //组合项结果查询
                QueryWrapper<TDepartResult> queryWrapperD = new QueryWrapper<>();
                queryWrapperD.eq("person_id", personId);
                queryWrapperD.eq("del_flag", 0);
                queryWrapperD.and(wrapper -> wrapper.eq("group_item_id", dr.getGroupItemId()).or().eq("group_item_name", dr.getGroupItemName()));
                List<TDepartResult> oneListD = departResultService.list(queryWrapperD);
                //基础项结果查询
                QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", personId);
                queryWrapper.eq("del_flag", 0);
                queryWrapper.and(wrapper -> wrapper.eq("order_group_item_id", dr.getGroupItemId()));
                List<TDepartItemResult> oneList = itemResultService.list(queryWrapper);

                if (oneListD != null && oneListD.size()>0 && oneList != null && oneList.size()>0) { //已经有
                    one = oneListD.get(0);
                    oldgroup_item_id = one.getGroupItemId();
                    //修改
                    one.setUpdateId(currendId);
                    one.setGroupItemId( dr.getGroupItemId());
                    one.setUpdateDate(new Date(System.currentTimeMillis()));
                    one.setCheckDoc(securityUtil.getCurrUser().getNickname());
                    if(StringUtils.isBlank(dr.getCreateId())){
                        one.setCreateId(currendId);
                    }
                    else{
                        one.setCreateId(dr.getCreateId());
                    }
                    if(dr.getCheckDate()==null){
                        one.setCheckDate(new Date(System.currentTimeMillis()));
                    }else{
                        one.setCheckDate(dr.getCheckDate());
                    }
                    if(StringUtils.isBlank(dr.getCheckDoc())){
                        one.setCheckDoc(securityUtil.getCurrUser().getNickname());
                    }else{
                        one.setCheckDoc(dr.getCheckDoc());
                    }
                    if(StringUtils.isNotBlank(dr.getCheckSignPath())){
                        one.setCheckSign(dr.getCheckSignPath().getBytes());
                    }
                    one.setDiagnoseSum(dr.getDiagnoseSum());
                    one.setDiagnoseTip(dr.getDiagnoseTip());
                    one.setUrl(dr.getUrl());
                    boolean resU = tDepartResultService.updateById(one);
                    if(!resU){
                        //手工回滚异常
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error( "保存失败");
                    }
                } else {
                    isAddRes = true;
                    //新增
                    dr.setDelFlag(0);
                    if(StringUtils.isBlank(dr.getCreateId())){
                        dr.setCreateId(currendId);
                    }

//                    dr.setCreateDate(new Date());
                    dr.setCheckNum(1);
                    dr.setState(0);
                    if(dr.getCheckDate()==null){
                        dr.setCheckDate(new Date(System.currentTimeMillis()));
                    }
                    if(StringUtils.isBlank(dr.getCheckDoc())){
                        dr.setCheckDoc(securityUtil.getCurrUser().getNickname());
                    }
                    if(StringUtils.isNotBlank(dr.getCheckSignPath())){
                        dr.setCheckSign(dr.getCheckSignPath().getBytes());
                    }
                    if(dr.getCheckSign()==null){
                        dr.setCheckSign(securityUtil.getCurrUser().getAutograph());
                    }

                    dr.setOfficeId(officeId);
                    dr.setOfficeName(officeName);

                    boolean save = tDepartResultService.saveOrUpdate(dr);
                    //已检查修改组合项目检查状态
                    if (save) {
                        String id = groupItem.getId();
                        TOrderGroupItem byId1 = itemService.getById(id);
                        if(byId1 != null){
                            byId1.setStatus(1);
                            byId1.setUpdateId(securityUtil.getCurrUser().getId());
                            byId1.setUpdateTime(new Date(System.currentTimeMillis()));
                            boolean resb = itemService.updateById(byId1);
                            if(!resb){
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error( "保存失败");
                            }
                        }
                    }else{
                        //手工回滚异常
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error( "保存失败");
                    }
                }

            }
            //基础项目检查结果 保存
            if (tOrderGroupItemProjects.size() > 0) {
                String orderGroupItemId = tOrderGroupItemProjects.get(0).getDepartItemResults().getOrderGroupItemId();
                List<TDepartItemResult> addList = new ArrayList<>();
                QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", personId);
                if(oldgroup_item_id==null){
                    queryWrapper.eq("order_group_item_id", orderGroupItemId);
                }
                else {
                    queryWrapper.eq("order_group_item_id",oldgroup_item_id);
                }

                boolean resDir = itemResultService.remove(queryWrapper);
                if(!resDir && !isAddRes){//删除失败 并且 不是新增
                    //手工回滚异常
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error( "保存失败");
                }
                for (TOrderGroupItemProject itemProject : tOrderGroupItemProjects) {
                    TDepartItemResult departItemResults = itemProject.getDepartItemResults();
//                    departItemResults.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    departItemResults.setDelFlag(0);
                    departItemResults.setCreateDate(new Date());
                    departItemResults.setCreateId(currendId);
                    departItemResults.setOrderGroupItemProjectId(itemProject.getId());
                    departItemResults.setOfficeId(officeId);
                    departItemResults.setOfficeName(officeName);
                    departItemResults.setCheckDoc(securityUtil.getCurrUser().getNickname());
                    departItemResults.setCheckDate(new Date(System.currentTimeMillis()));
                    if(dr!=null){
                        if(dr.getCheckDoc()!=null){
                            departItemResults.setCheckDoc(dr.getCheckDoc());
                        }
                        if(dr.getCheckDate()!=null){
                            departItemResults.setCheckDate(dr.getCheckDate());
                        }
                    };
                    departItemResults.setIgnoreStatus(1);
                    departItemResults.setOrderNum(itemProject.getOrderNum());
                    departItemResults.setDepartResultId(dr.getId());
                    if (dr.getGroupItemName().indexOf("(复)") > -1) {
                        departItemResults.setIsRecheck(1);
                    }
                    addList.add(departItemResults);
                }
                boolean flag = false;
                boolean b = itemResultService.saveBatch(addList);
                if(!b){
                    //手工回滚异常
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error( "保存失败");
                }
//                boolean b = itemResultService.saveOrUpdateBatch(addList);
                if (b && byId.getIsWzCheck() == 1 && byId.getIsPass() < 3 && !isUpdate) {
                    //所有检查项       //查询复查检查项和已检项
                    Integer count = itemService.getAllCheckCount(personId, byId.getGroupId());
                    Integer count1 = itemService.getDepartResultCount(personId, byId.getGroupId());
                    //弃检记录
                    QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                    checkQueryWrapper.eq("state", 2);
                    checkQueryWrapper.eq("person_id", byId.getId());
                    int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                   /* int count2 = 0;*/

                    //全部检查完  修改状态为3，到总检
                    if (count1.intValue() >= (count.intValue() - count2)) {
                        byId.setIsPass(3);
                        byId.setUpdateId(securityUtil.getCurrUser().getId());
                        byId.setUpdateTime(new Date(System.currentTimeMillis()));
                        flag = personService.updateById(byId);
                        if(!flag){
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error( "保存失败");
                        }
                    }
                    return ResultUtil.data(flag, "保存成功");
                }else if(isUpdate){
                    //所有检查项       //查询复查检查项和已检项
                    Integer count = itemService.getAllCheckCountReview(tReviewPerson.getId(), tReviewPerson.getGroupId());
                    Integer count1 = itemService.getDepartResultCount(tReviewPerson.getId(), tReviewPerson.getGroupId());
                    //弃检记录
                    QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                    checkQueryWrapper.eq("state", 2);
                    checkQueryWrapper.eq("person_id", tReviewPerson.getId());
                    int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                    /*int count2 = 0;*/

                    //全部检查完  修改状态为3，到总检
                    if (count1.intValue() >= (count.intValue() - count2) && tReviewPerson.getIsPass()<3) {
                        QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper = new QueryWrapper<>();
                        tReviewPersonQueryWrapper.eq("del_flag",0);
                        tReviewPersonQueryWrapper.eq("id",tReviewPerson.getId());
                        TReviewPerson tReviewPerson1 = new TReviewPerson();
                        tReviewPerson1.setIsPass(3);
                        boolean resRe = itReviewPersonService.update(tReviewPerson1,tReviewPersonQueryWrapper);
                        if(!resRe){
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error( "保存失败");
                        }
                    }
                }
                return ResultUtil.data(true, "保存成功");
            } else {
                //手工回滚异常
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error("基础项参数为空,保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //手工回滚异常
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新组合项目检查结果以及更新基础项目结果
     *
     * @param form 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新组合项目检查结果以及更新基础项目结果", type = LogType.OPERATION)
    @ApiOperation("更新组合项目检查结果以及更新基础项目结果")
    @PostMapping("updateResultAndItemResult")
    public Result<Object> updateResultAndItemResult(@RequestBody String form) {
        try {
            if (StringUtils.isBlank(form)) {
                return ResultUtil.error("参数为空，请联系管理员！");
            }
            String currendId = securityUtil.getCurrUser().getId();

            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            JSONObject jsonObjectGroup = JSON.parseObject(formStr);

            //组合项目检查结果
            String departResultStr = jsonObjectGroup.getString("groupResult");
            List<TDepartResult> groupItem = JSON.parseArray(departResultStr, TDepartResult.class);
            if (groupItem.size() == 0) {
                return ResultUtil.error("参数为空，请联系管理员！");
            }

            TDepartResult dr = groupItem.get(0);
            String personId = dr.getPersonId();

            if (dr != null) {
                //界面未刷新 可能提交多条。
                QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("person_id", personId);
                queryWrapper.eq("del_flag", 0);
                queryWrapper.eq("group_item_id", dr.getGroupItemId());
                TDepartResult one = departResultService.getOne(queryWrapper);
                if (one != null) { //已经有
                    //修改
                    one.setUpdateId(currendId);
                    one.setUpdateDate(new Date());
                    one.setDiagnoseSum(dr.getDiagnoseSum());
                    one.setDiagnoseTip(dr.getDiagnoseTip());
                    tDepartResultService.updateById(one);
                }
            }

            //基础项目检查结果
            String departItemResultStr = jsonObjectGroup.getString("groupItemResult");
            List<TOrderGroupItemProject> tOrderGroupItemProjects = JSON.parseArray(departItemResultStr, TOrderGroupItemProject.class);
            if (tOrderGroupItemProjects.size() > 0) {
                List<TDepartItemResult> addList = new ArrayList<>();
                for (TOrderGroupItemProject itemProject : tOrderGroupItemProjects) {
                    TDepartItemResult departItemResults = itemProject.getDepartItemResults();

                    QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("person_id", personId);
                    queryWrapper.eq("order_group_item_project_id", departItemResults.getOrderGroupItemProjectId());
                    TDepartItemResult one = itemResultService.getOne(queryWrapper);
                    if (one != null) {
                        one.setUnitCode(departItemResults.getUnitCode());
                        one.setUnitName(departItemResults.getUnitName());
                        one.setResult(departItemResults.getResult());
                        one.setScope(departItemResults.getScope());
                        one.setCrisisDegree(departItemResults.getCrisisDegree());
                        one.setArrow(departItemResults.getArrow());
                        one.setImgUrl(departItemResults.getImgUrl());
                        one.setUpdateId(securityUtil.getCurrUser().getId());
                        one.setUpdateDate(new Date());
                        addList.add(one);
                    }
                }

                boolean b = itemResultService.saveOrUpdateBatch(addList);
                if (b) {
                    return ResultUtil.data(b, "保存成功");
                } else {
                    return ResultUtil.data(b, "保存失败");
                }
            } else {
                return ResultUtil.error("参数为空，请联系管理员！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tDepartResult 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新分检结果数据", type = LogType.OPERATION)
    @ApiOperation("更新分检结果数据")
    @PostMapping("updateTDepartResult")
    public Result<Object> updateTDepartResult(@RequestBody TDepartResult tDepartResult) {
        if (StringUtils.isBlank(tDepartResult.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tDepartResult.setUpdateId(securityUtil.getCurrUser().getId());
            tDepartResult.setUpdateDate(new Date());
            boolean res = tDepartResultService.updateById(tDepartResult);
            if (res) {
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
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除分检结果数据")
    @SystemLog(description = "根据主键来删除分检结果数据", type = LogType.OPERATION)
    @PostMapping("deleteTDepartResult")
    public Result<Object> deleteTDepartResult(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tDepartResultService.removeByIds(Arrays.asList(ids));
            if (res) {
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
    @SystemLog(description = "根据主键来获取分检结果数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取分检结果数据")
    @GetMapping("getTDepartResult")
    public Result<Object> getTDepartResult(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TDepartResult res = tDepartResultService.getById(id);
            if (res != null) {
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
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询分检结果数据", type = LogType.OPERATION)
    @ApiOperation("分页查询分检结果数据")
    @GetMapping("queryTDepartResultList")
    public Result<Object> queryTDepartResultList(TDepartResult tDepartResult, SearchVo searchVo) {
        try {
            List<TDepartResult> result = tDepartResultService.queryTDepartResultList(tDepartResult, searchVo);
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
    @SystemLog(description = "分页查询科室及分检结果数据", type = LogType.OPERATION)
    @ApiOperation("分页查询科室及分检结果数据")
    @GetMapping("queryTDepartResultListAndOfficeName")
    public Result<Object> queryTDepartResultListAndOfficeName(TDepartResult tDepartResult, SearchVo
            searchVo, PageVo pageVo) {
        try {
            IPage<TDepartResult> result = tDepartResultService.queryTDepartResultListAndOfficeNameByPage(tDepartResult, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询人员检查科室
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询人员检查科室", type = LogType.OPERATION)
    @ApiOperation("查询人员检查科室")
    @GetMapping("queryPersonCheckOffice")
    public Result<Object> queryPersonCheckOffice(TDepartResult tDepartResult) {
        try {
            List<TDepartResult> result = tDepartResultService.queryPersonCheckOffice(tDepartResult);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response      请求参数
     * @param tDepartResult 查询参数
     * @return
     */
    @SystemLog(description = "导出分检结果数据", type = LogType.OPERATION)
    @ApiOperation("导出分检结果数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TDepartResult tDepartResult) {
        try {
            tDepartResultService.download(tDepartResult, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param groupId 请求参数(分组id)
     * @return
     */
    @SystemLog(description = "导出分检结果数据", type = LogType.OPERATION)
    @ApiOperation("导出分检结果数据")
    @PostMapping("/getBaseProjectByGroupId")
    public Result<Object> getBaseProjectByGroupId(String groupId) {
        try {
            TOrderGroup orderGroup = orderGroupService.getById(groupId);
            if (orderGroup != null) {
                QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("group_id", groupId);
                queryWrapper.eq("del_flag", 0);
                List<TOrderGroupItem> itemList = itemService.list(queryWrapper);

                for (TOrderGroupItem item : itemList) {
                    List<TBaseProject> list = portfolioProjectService.getBaseProjectByPortfolioProject(item.getPortfolioProjectId());
                    item.setBaseProjects(list);
                }
                orderGroup.setProjectData(itemList);
                return ResultUtil.data(orderGroup);
            } else {
                return ResultUtil.error("未找到分组，查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询分组
     *
     * @param groupId 请求参数(分组id)
     * @return
     */
    @SystemLog(description = "根据选择人员中的分组id，查询分组项目", type = LogType.OPERATION)
    @ApiOperation("根据选择人员中的分组id，查询分组项目")
    @GetMapping("/getItemByGroupId")
    public Result<Object> getPortfolioProjectByGroupId(String groupId, String personId, String type,boolean isReview) {
        if (StringUtils.isBlank(groupId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
            //是否需要设置权限
            if (!"isAll".equals(type)) {
                if (securityUtil.getDeparmentIds() != null) {
                    tOrderGroupItem.setOfficeList(securityUtil.getDeparmentIds());
                }
            }
            tOrderGroupItem.setGroupId(groupId);
            tOrderGroupItem.setPersonId(personId);
            //分组项目
            List<TOrderGroupItem> items = new ArrayList<>();
            if(!isReview){
                items = itemService.listByQueryWrapper(tOrderGroupItem);
            }

            //复查
            TReviewProject tReviewProject = new TReviewProject();
            tReviewProject.setPersonId(personId);
            tReviewProject.setGroupId(groupId);
            tReviewProject.setIsPass(2);
            tReviewProject.setDelFlag(0);
            if (securityUtil.getDeparmentIds() != null) {
                tReviewProject.setOfficeList(securityUtil.getDeparmentIds());
            }
            List<TReviewProject> list = reviewProjectService.listByWhere(tReviewProject);
            if (list.size() > 0) {
                for (TReviewProject t : list) {
                    TOrderGroupItem orderGroupItem = new TOrderGroupItem();
                    orderGroupItem.setId(t.getId());
                    orderGroupItem.setName(t.getPortfolioProjectName());
                    orderGroupItem.setPortfolioProjectId(t.getPortfolioProjectId());
                    orderGroupItem.setGroupId(t.getGroupId());
                    orderGroupItem.setGroupOrderId(t.getGroupOrderId());
                    orderGroupItem.setOfficeId(t.getOfficeId());
                    orderGroupItem.setOfficeName(t.getOfficeName());
                    orderGroupItem.setTestNum(t.getTestNum());
                    orderGroupItem.setStatus(t.getStatus());
                    orderGroupItem.setSpecimen(t.getSpecimen());
                    orderGroupItem.setIsFile(t.getIsFile());
                    items.add(orderGroupItem);
                }
            }
            QueryWrapper<TDepartResult> resultQueryWrapper = new QueryWrapper<>();
            resultQueryWrapper.eq("person_id", personId);
            resultQueryWrapper.eq("del_flag", 0);
            resultQueryWrapper.isNotNull("group_item_id");
            List<TDepartResult> departResults = departResultService.list(resultQueryWrapper);
            for (TOrderGroupItem item : items) {
                //是否有检查结果
                TDepartResult result = departResults.stream().filter(i -> i.getGroupItemId().equals(item.getId())).findFirst().orElse(null);
                if(result!=null && result.getCheckSign()!=null){
                    //字节转字符串
                    byte[] blob = (byte[]) result.getCheckSign();
                    if(blob!=null){
                        String avatarNow = new String(blob);
                        if(avatarNow.indexOf("/dcm") > -1){
                            result.setCheckSignPath(avatarNow);
                        }
                    }
                }
                if (result != null) {
                    item.setDepartResult(result);
                }
            }
            return ResultUtil.data(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询分组
     *
     * @param officeId 请求参数(科室id)
     * @return
     */
    @SystemLog(description = "根据选择人员中的科室id，查询分组项目", type = LogType.OPERATION)
    @ApiOperation("根据选择人员中的科室id，查询分组项目")
    @GetMapping("/getGroupByOfficeId")
    public Result<Object> getGroupByOfficeId(String officeId, String personId,String groupId, SearchVo searchVo) {
        if (StringUtils.isBlank(officeId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        if (StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
//            QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("office_id", officeId);
//            queryWrapper.eq("person_id", personId);
//            queryWrapper.eq("del_flag", 0);
//            if (searchVo != null) {
//                if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
//                    queryWrapper.between("check_date", searchVo.getStartDate(), searchVo.getEndDate());
//                }
//            }
            //分组项目
//            List<TDepartResult> items = departResultService.list(queryWrapper);
            TDepartResult tDepartResult = new TDepartResult();
            tDepartResult.setOfficeId(officeId);
            tDepartResult.setPersonId(personId);
            tDepartResult.setDelFlag(0);
            List<TDepartResult> items = departResultService.queryTDepartResultAndProjectId(tDepartResult,searchVo);
            //复查
            TReviewProject tReviewProject = new TReviewProject();
            tReviewProject.setPersonId(personId);
            tReviewProject.setGroupId(groupId);
            /*if (securityUtil.getDeparmentIds() != null) {
                List<String> officeList = new ArrayList<>();
                officeList.add(officeId);
                tReviewProject.setOfficeList(officeList);
            }*/
            tReviewProject.setOfficeId(officeId);
            tReviewProject.setDelFlag(0);
            List<TReviewProject> list = reviewProjectService.listByWhere(tReviewProject);
            if (list.size() > 0 && items.size() > 0) {
                for (TDepartResult t : items) {
                    List<TReviewProject> listNow = list.stream().filter(p -> t.getGroupItemId().equals(p.getId())).collect(Collectors.toList());
                    if(listNow.size() > 0){
                        String portfolioProjectIdNow = listNow.get(0).getPortfolioProjectId();
                        String groupIdNow = listNow.get(0).getGroupId();
                        t.setPortfolioProjectId(portfolioProjectIdNow);
                        t.setGroupId(groupIdNow);
                    }
                    if(t.getCheckSign()!=null){
                        //字节转字符串
                        byte[] blob = (byte[]) t.getCheckSign();
                        if(blob!=null){
                            String avatarNow = new String(blob);
                            if(avatarNow.indexOf("/dcm") > -1){
                                t.setCheckSignPath(avatarNow);
                            }
                        }
                    }
                }
            }
            for (TDepartResult t : items) {
                if(t.getCheckSign()!=null){
                    //字节转字符串
                    byte[] blob = (byte[]) t.getCheckSign();
                    if(blob!=null){
                        String avatarNow = new String(blob);
                        if(avatarNow.indexOf("/dcm") > -1){
                            t.setCheckSignPath(avatarNow);
                        }
                    }
                }
            }
            return ResultUtil.data(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据组合项目id 查询基础项目
     *
     * @param portfolioProjectId
     * @return
     */
    @SystemLog(description = "根据组合项目id 查询基础项目", type = LogType.OPERATION)
    @ApiOperation("根据组合项目id 查询基础项目")
    @GetMapping("/getBaseProjectByPortfolioProjectId")
    public Result<Object> getBaseProjectByPortfolioProjectId(String portfolioProjectId) {
        if (StringUtils.isBlank(portfolioProjectId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("portfolio_project_id", portfolioProjectId);
            List<RelationBasePortfolio> list = relationBasePortService.list(queryWrapper);
            List<TBaseProject> baseProjects = new ArrayList<>();
            if (list.size() > 0) {
                for (RelationBasePortfolio basePortfolio : list) {
                    String baseProjectId = basePortfolio.getBaseProjectId();
                    TBaseProject byId = baseProjectService.getById(baseProjectId);
                    //关联配置范围
                    QueryWrapper<RelationProjectReference> relationWrapper = new QueryWrapper<>();
                    relationWrapper.eq("base_project_id", byId.getId());
                    relationWrapper.orderByDesc();
                    relationWrapper.last("limit 1");
                    List<RelationProjectReference> one = referenceService.list(relationWrapper);
                    byId.setProjectReferenceList(one);
                    baseProjects.add(byId);
                }
                return ResultUtil.data(baseProjects);
            } else {
                return ResultUtil.error("未查询到对应的体检项目");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据人员和组合项目查询初检结果
     *
     * @param groupId            分组id
     * @param personId           人员id
     * @param portfolioProjectId 组合项目id
     * @return
     */
    @SystemLog(description = "根据人员和组合项目查询初检结果", type = LogType.OPERATION)
    @ApiOperation("根据人员和组合项目查询初检结果")
    @GetMapping("/getDepartResultDataByPersonIdAndPortfolioProId")
    public Result<Object> getDepartResultDataByPersonIdAndPortfolioProId(String personId, String groupId, String
            portfolioProjectId) {
        if (StringUtils.isBlank(personId) && StringUtils.isBlank(portfolioProjectId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("state", 1);//最优结果
            queryWrapper.eq("person_id", personId);
            queryWrapper.eq("group_id", groupId);
            queryWrapper.eq("portfolio_project_id", portfolioProjectId);
            List<TDepartResult> list = departResultService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /***
     * 根据人员 分组项目 找组合项目检查结果
     * @param personId
     * @param groupItemId
     * @return
     */
    @SystemLog(description = "根据人员和组合项目查询初检结果", type = LogType.OPERATION)
    @ApiOperation("根据人员和组合项目查询初检结果")
    @GetMapping("/getDepartResultByPerIdAndItemId")
    public Result<Object> getDepartResultByPerIdAndItemId(String personId, String groupItemId) {
        if (StringUtils.isBlank(personId) && StringUtils.isBlank(groupItemId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("person_id", personId);
            queryWrapper.eq("group_item_id", groupItemId);
            List<TDepartResult> list = departResultService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：从业体检 丙氨酸基转移酶(转氨酶) 如果有问题，固定增加两项检查
     *
     * @param orderGroupItemId 实体
     * @param groupOrderId     实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新分检结果数据", type = LogType.OPERATION)
    @ApiOperation("更新分检结果数据")
    @PostMapping("addOrderGroupItemPorjectByExtra")
    public Result<Object> addOrderGroupItemPorjectByExtra(String orderGroupItemId, String groupOrderId) {
        try {
            List<String> list = new ArrayList<>();
            list.add("戊肝Igm(转氨酶异常的增查)");
            list.add("甲肝Igm(转氨酶异常的增查)");

            List<TOrderGroupItemProject> projectList = new ArrayList<>();

            for (String str : list) {
                QueryWrapper<TBaseProject> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", str);
                queryWrapper.eq("del_flag", 0);
                TBaseProject one = baseProjectService.getOne(queryWrapper);
                //选中
                TOrderGroupItemProject baseProject = new TOrderGroupItemProject();

                baseProject.setTOrderGroupItemId(orderGroupItemId);
                baseProject.setCode(one.getShortName());
                baseProject.setShortName(one.getShortName());
                baseProject.setBaseProjectId(one.getId());
                baseProject.setName(one.getName());
                baseProject.setOrderNum(one.getOrderNum());
                baseProject.setOfficeId(one.getOfficeId());
                baseProject.setOfficeName(one.getOfficeName());
                baseProject.setUnitName(one.getUnitName());
                baseProject.setUnitCode(one.getUnitCode());
                baseProject.setDefaultValue(one.getDefaultValue());
                baseProject.setResultType(one.getResultType());
                baseProject.setInConclusion(one.getInConclusion());
                baseProject.setGroupOrderId(groupOrderId);
                baseProject.setDelFlag(0);
                projectList.add(baseProject);
            }
            boolean save = itemProjectService.saveBatch(projectList);
            if (save) {
                return ResultUtil.data(true);
            } else {
                return ResultUtil.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("添加异常" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @return 返回获取结果
     */
    @ApiOperation("查询当前科室已检查项目数量")
    @PostMapping("queryTDepartResultByPersonId")
    public Result<Object> queryTDepartResultByPersonId(@RequestBody String data) {
        try {
            JSONObject data1 = JSON.parseObject(data);
            JSONObject jsonObject = data1.getJSONObject("data");
            JSONArray itemIdList = jsonObject.getJSONArray("groupItemIdList");
            String[] groupItemIdList = itemIdList.toArray(new String[itemIdList.size()]);
            String personId = jsonObject.getString("personId");
            Integer result = tDepartResultService.queryTDepartResultByPersonId(Arrays.asList(groupItemIdList), personId);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @return 返回获取结果
     */
    @ApiOperation("查询科室已检查项目数量")
    @PostMapping("queryTDepartResultStatistics")
    public Result<Object> queryTDepartResultStatistics(@RequestBody Map<String,Object> data) {
        try {
            String startDate = data.get("startDate").toString();
            String endDate = data.get("endDate").toString();
            String dept =data.get("dept")!=null? data.get("dept").toString():"";
            List<String> officeIds=data.get("officeIds")!=null? (List<String>) data.get("officeIds"):new ArrayList<>();
            List<TDepartResult>  result = tDepartResultService.queryTDepartResultStatistics(startDate, endDate,officeIds,dept);
            //查询已登记
            QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.ge("is_pass", 2);
            queryWrapper.between("regist_date",startDate,endDate);
            //查询已检查
            QueryWrapper<TGroupPerson> queryWrapperTwo = new QueryWrapper<>();
            queryWrapperTwo.eq("del_flag", 0);
            queryWrapperTwo.ge("is_pass", 3);
            queryWrapperTwo.between("regist_date",startDate,endDate);

            if(StringUtils.isNotBlank(dept)){
                queryWrapper.eq("unit_id", dept);
                queryWrapperTwo.eq("unit_id", dept);
            }
            int countRegist = personService.count(queryWrapper);

            int countHealthy = personService.count(queryWrapperTwo);
            //分装返回
            Map<String,Object> map = new HashMap<>();
            map.put("countRegist",countRegist);
            map.put("countHealthy",countHealthy);
            map.put("result",result);
            return ResultUtil.data(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
