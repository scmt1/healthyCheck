package com.scmt.healthy.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.annotation.SystemLog;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 复查项目数据接口")
@RequestMapping("/scmt/tReviewProject")
public class TReviewProjectController {
    @Autowired
    private ITReviewProjectService tReviewProjectService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITOrderGroupItemProjectService orderGroupItemProjectService;
    @Autowired
    private IRelationBasePortfolioService relationBasePortfolioService;

    @Autowired
    private ITBaseProjectService baseProjectService;
    @Autowired
    private ITPortfolioProjectService portfolioProjectService;
    @Autowired
    private ITGroupPersonService itGroupPersonService;

    @Autowired
    private ITBaseProjectService itBaseProjectService;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;
    @Autowired
    private ITOrderGroupItemProjectService itOrderGroupItemProjectService;

    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;

    @Autowired
    private ITDepartResultService departResultService;

    @Autowired
    private ITReviewPersonService itReviewPersonService;

    /**
     * 功能描述：新增复查项目数据
     *
     * @param tReviewProject 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增复查项目数据", type = LogType.OPERATION)
    @ApiOperation("新增复查项目数据")
    @PostMapping("addTReviewProject")
    public Result<Object> addTReviewProject(@RequestBody TReviewProject tReviewProject) {
        try {
            tReviewProject.setDelFlag(0);
            boolean res = tReviewProjectService.save(tReviewProject);
            if (res) {
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tReviewProject 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新复查项目数据", type = LogType.OPERATION)
    @ApiOperation("更新复查项目数据")
    @PostMapping("updateTReviewProject")
    public Result<Object> updateTReviewProject(@RequestBody TReviewProject tReviewProject) {
        if (StringUtils.isBlank(tReviewProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tReviewProjectService.updateById(tReviewProject);
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
    @ApiOperation("根据主键来删除复查项目数据")
    @SystemLog(description = "根据主键来删除复查项目数据", type = LogType.OPERATION)
    @PostMapping("deleteTReviewProject")
    @Transactional(rollbackOn = { Exception.class })
    public Result<Object> deleteTReviewProject(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            String currendId = securityUtil.getCurrUser().getId();

            QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("group_item_id", Arrays.asList(ids));
            List<TDepartResult> departResults = departResultService.list(queryWrapper);
            if(departResults!=null && departResults.size()>0){
                TDepartResult tDepartResult = new TDepartResult();
                tDepartResult.setDelFlag(1);
                tDepartResult.setDeleteDate(new Date());
                tDepartResult.setDeleteId(currendId);
                boolean resDep = departResultService.update(tDepartResult,queryWrapper);
                if(!resDep){
                    //手工回滚异常
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error("组合项结果删除失败");
                }
            }

            String personId = "";
            String id0 = ids[0];
            if(id0!=null && id0.trim().length()>0){
                QueryWrapper<TReviewProject> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.in("id", id0);
                TReviewProject tReviewProject = tReviewProjectService.getOne(queryWrapper1);
                personId = tReviewProject.getPersonId();//获取人员id
            }

            boolean res = tReviewProjectService.removeByIds(Arrays.asList(ids));//删除项目
            if(!res){
                //手工回滚异常
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error("复查项目删除失败");
            }

            if(personId!=null && personId.trim().length()>0 && res){
                QueryWrapper<TReviewProject> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.in("person_id", personId);
                queryWrapper1.in("del_flag", 0);
                List<TReviewProject> tReviewProjects = tReviewProjectService.list(queryWrapper1);
                if(tReviewProjects==null || tReviewProjects.size()==0){
                    TReviewPerson tReviewPersons = itReviewPersonService.getById(personId);
                    //更新人员复查状态
                    if(tReviewPersons!=null && StringUtils.isNotBlank(tReviewPersons.getFirstPersonId())){
                        TGroupPerson tGroupPerson = new TGroupPerson();
                        tGroupPerson.setId(tReviewPersons.getFirstPersonId());
                        tGroupPerson.setIsRecheck(0);
                        boolean resGrp = itGroupPersonService.updateById(tGroupPerson);
                        if(!resGrp){
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("人员复查状态修改时失败");
                        }
                        //删除复查人员
                        boolean resRevp = itReviewPersonService.removeById(personId);
                        if(!resRevp){
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("复查人员删除失败");
                        }
                    }
                }
            }
            return ResultUtil.data(res, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            //手工回滚异常
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("删除异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取复查项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取复查项目数据")
    @GetMapping("getTReviewProject")
    public Result<Object> getTReviewProject(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TReviewProject res = tReviewProjectService.getById(id);
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
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询复查项目数据", type = LogType.OPERATION)
    @ApiOperation("分页查询复查项目数据")
    @GetMapping("queryTReviewProjectList")
    public Result<Object> queryTReviewProjectList(TReviewProject tReviewProject, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TReviewProject> result = tReviewProjectService.queryTReviewProjectListByPage(tReviewProject, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response       请求参数
     * @param tReviewProject 查询参数
     * @return
     */
    @SystemLog(description = "导出复查项目数据", type = LogType.OPERATION)
    @ApiOperation("导出复查项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TReviewProject tReviewProject) {
        try {
            tReviewProjectService.download(tReviewProject, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：保存复检项目
     *
     * @param groupPerson 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新分检结果数据", type = LogType.OPERATION)
    @ApiOperation("更新分检结果数据")
    @PostMapping("reCheckBaseProject")
    @Transactional(rollbackOn = { Exception.class })
    public Result<Object> reCheckBaseProject(TGroupPerson groupPerson) {
        if (groupPerson == null) {
            return ResultUtil.error("参数为空");
        }
        try {
            //判断是否已创建复查人员信息，是就修改
            Boolean addReviewPerson = false;
            QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper = new QueryWrapper<>();
            tReviewPersonQueryWrapper.eq("del_flag",0);
            tReviewPersonQueryWrapper.eq("old_person_id",groupPerson.getId());
            List<TReviewPerson> tReviewPersonList = itReviewPersonService.list(tReviewPersonQueryWrapper);
            String reviewPersonId = "";//本次复查人员id
            if(tReviewPersonList.size() > 0){
                reviewPersonId = tReviewPersonList.get(0).getId();
                addReviewPerson = false;
            }else{
                reviewPersonId = UUID.randomUUID().toString().replaceAll("-", "");
                addReviewPerson = true;
            }

            //是否存有当前人的复检记录
            QueryWrapper<TReviewProject> reviewProjectQueryWrapper = new QueryWrapper<>();
            reviewProjectQueryWrapper.eq("group_id", groupPerson.getGroupId());
//            reviewProjectQueryWrapper.eq("person_id", groupPerson.getId());
            reviewProjectQueryWrapper.eq("person_id", reviewPersonId);
            reviewProjectQueryWrapper.eq("del_flag", 0);
            reviewProjectQueryWrapper.orderByDesc("create_time");
            reviewProjectQueryWrapper.last("limit 1");
            TReviewProject one = tReviewProjectService.getOne(reviewProjectQueryWrapper);
            //编号
            String testNum = "";
            if (one != null) {
                testNum = one.getTestNum();
            } else {
                testNum = generatorNum();
            }
            //保存人员信息
            TGroupPerson byId1 = itGroupPersonService.getById(groupPerson.getId());
            //新增复查人员表信息
            if(addReviewPerson){
                if(byId1==null){
                    TReviewPerson tReviewPerson1 = itReviewPersonService.getById(groupPerson.getId());
                    TReviewPerson tReviewPerson = new TReviewPerson();
                    tReviewPerson.setDelFlag(0);
                    tReviewPerson.setIsPass(1);
                    tReviewPerson.setTestNum(testNum);
                    tReviewPerson.setId(reviewPersonId);
                    tReviewPerson.setOldPersonId(tReviewPerson1.getId());
                    tReviewPerson.setFirstPersonId(tReviewPerson1.getFirstPersonId());
                    tReviewPerson.setGroupId(tReviewPerson1.getGroupId());
                    tReviewPerson.setUnitId(tReviewPerson1.getUnitId());
                    tReviewPerson.setOrderId(tReviewPerson1.getOrderId());
                    tReviewPerson.setPersonName(tReviewPerson1.getPersonName());
                    tReviewPerson.setIdCard(tReviewPerson1.getIdCard());
                    tReviewPerson.setDept(tReviewPerson1.getDept());
                    tReviewPerson.setPhysicalType(tReviewPerson1.getPhysicalType());
                    tReviewPerson.setCreateId(securityUtil.getCurrUser().getId());
                    String code = "";
                    if (groupPerson.getHazardFactorCode().length > 0 && groupPerson.getHazardFactorCode()!=null){
                        for (int i = 0; i < groupPerson.getHazardFactorCode().length; i++) {
                            code += groupPerson.getHazardFactorCode()[i]+"|";
                        }
                    }
                    if (code.length()>0){
                        code = code.substring(0,code.length()-1);
                    }
                    tReviewPerson.setHazardFactorCode(code);
                    tReviewPerson.setCreateTime(new Date());
                    Boolean flag = itReviewPersonService.save(tReviewPerson);
                    if(!flag){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存失败：" + "保存复查人员信息失败，请重新保存");
                    }
                }else{
                    TReviewPerson tReviewPerson = new TReviewPerson();
                    tReviewPerson.setDelFlag(0);
                    tReviewPerson.setIsPass(1);
                    tReviewPerson.setTestNum(testNum);
                    tReviewPerson.setId(reviewPersonId);
                    tReviewPerson.setOldPersonId(byId1.getId());
                    tReviewPerson.setFirstPersonId(byId1.getId());
                    tReviewPerson.setGroupId(byId1.getGroupId());
                    tReviewPerson.setUnitId(byId1.getUnitId());
                    tReviewPerson.setOrderId(byId1.getOrderId());
                    tReviewPerson.setPersonName(byId1.getPersonName());
                    tReviewPerson.setIdCard(byId1.getIdCard());
                    tReviewPerson.setDept(byId1.getDept());
                    tReviewPerson.setPhysicalType(byId1.getPhysicalType());
                    tReviewPerson.setCreateId(securityUtil.getCurrUser().getId());
                    tReviewPerson.setCreateTime(new Date());
                    String code = "";
                    if (groupPerson.getHazardFactorCode().length > 0 && groupPerson.getHazardFactorCode() != null) {
                        for (int i = 0; i < groupPerson.getHazardFactorCode().length; i++) {
                            code += groupPerson.getHazardFactorCode()[i] + "|";
                        }
                    }
                    if (code.length()>0){
                        code = code.substring(0,code.length()-1);
                    }
                    tReviewPerson.setHazardFactorCode(code);
                    Boolean flag = itReviewPersonService.save(tReviewPerson);
                    if(!flag){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存失败：" + "保存复查人员信息失败，请重新保存");
                    }
                }
            }else{//更新复查人员表状态 体检状态重置为未登记
                TReviewPerson tReviewPerson = new TReviewPerson();
                tReviewPerson.setIsPass(1);
                QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper1 = new QueryWrapper<>();
                tReviewPersonQueryWrapper1.eq("del_flag",0);
                tReviewPersonQueryWrapper1.eq("id",reviewPersonId);
                Boolean flag = itReviewPersonService.update(tReviewPerson,tReviewPersonQueryWrapper1);
                if(!flag){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error("保存失败：" + "保存复查人员信息失败，请重新保存");
                }
            }

            //更新人员复查状态
            if (byId1 != null) {
                byId1.setIsRecheck(1);
                byId1.setReviewStatu(0);//新增复查项目后 重置复查状态
                byId1.setUpdateTime(new Date());
                byId1.setUpdateId(securityUtil.getCurrUser().getId());
                Boolean flag = itGroupPersonService.updateById(byId1);
                if(!flag){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error("保存失败：" + "保存复查人员信息失败，请重新保存");
                }
            }


            //选中组合项目id
            String[] ids = groupPerson.getIds();
            List<String> strings = Arrays.asList(ids);
            if(strings!=null && strings.size()>0){
                List<RelationBasePortfolio> relationBasePortfolioList = iRelationBasePortfolioService.list();
                List<TBaseProject> tBaseProjectList = itBaseProjectService.list();
                for (String portfolioId : strings)
                {
                    TPortfolioProject byId = portfolioProjectService.getById(portfolioId);
                    TReviewProject reviewProject = new TReviewProject();
                    reviewProject.setId(UUID.randomUUID().toString().replaceAll("-", ""));
//                    reviewProject.setPersonId(groupPerson.getId());
                    reviewProject.setPersonId(reviewPersonId);
                    reviewProject.setPersonName(groupPerson.getPersonName());
                    reviewProject.setName(byId.getName() + "(复)");
                    reviewProject.setShortName(byId.getShortName());
                    reviewProject.setOrderNum(byId.getOrderNum());
                    reviewProject.setOfficeId(byId.getOfficeId());
                    reviewProject.setOfficeName(byId.getOfficeName());
                    reviewProject.setSalePrice(byId.getSalePrice());
                    reviewProject.setGroupId(groupPerson.getGroupId());
                    reviewProject.setPortfolioProjectId(byId.getId());
                    reviewProject.setPortfolioProjectName(byId.getName() + "(复)");
                    reviewProject.setAddress(byId.getAddress());
                    reviewProject.setGroupOrderId(groupPerson.getOrderId());
                    reviewProject.setPhysicalType(groupPerson.getPhysicalType());
                    reviewProject.setServiceType(byId.getServiceType());
                    reviewProject.setSpecimen(byId.getSpecimen());
                    reviewProject.setReason(groupPerson.getReason());
                    reviewProject.setIsFile(byId.getIsFile());
                    reviewProject.setDelFlag(0);
                    reviewProject.setIsPass(1);
                    reviewProject.setProjectType(1);
                    String code = "";
                    if (groupPerson.getHazardFactorCode().length > 0 && groupPerson.getHazardFactorCode()!=null){
                        for (int i = 0; i < groupPerson.getHazardFactorCode().length; i++) {
                            code += groupPerson.getHazardFactorCode()[i]+"|";
                        }
                    }
                    if (code.length()>0){
                        code = code.substring(0,code.length()-1);
                    }
                    reviewProject.setHazardFactorCode(code);
                    reviewProject.setCreateId(securityUtil.getCurrUser().getId());
                    reviewProject.setCreateTime(new Date());
                    //生成编号
                    reviewProject.setTestNum(testNum);
                    //保存分组复查信息
                    boolean save =  tReviewProjectService.save(reviewProject);
                    if(save)
                    {
                        //查询当前分组是否有此组合项目的基础项目，
                        QueryWrapper<TOrderGroupItem> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("portfolio_project_id", portfolioId);
                        queryWrapper.eq("group_id", groupPerson.getGroupId());
                        int count = itOrderGroupItemService.count(queryWrapper);
                        //没有就添加
                        //分组项目没有
                        if(count==0)
                        {
                            QueryWrapper<TReviewProject> queryWrapperReviewProject = new QueryWrapper<>();
                            queryWrapperReviewProject.eq("portfolio_project_id", portfolioId);
                            queryWrapperReviewProject.eq("group_id", groupPerson.getGroupId());
                            queryWrapperReviewProject.eq("del_flag", 0);
                            queryWrapperReviewProject.orderByAsc("create_time");
                            queryWrapperReviewProject.select("id");
                            List<TReviewProject> reviewProjects = tReviewProjectService.list(queryWrapperReviewProject);
                            if(reviewProjects != null && reviewProjects.size() > 0) {
                                List<String> idList = reviewProjects.stream().map(TReviewProject::getId).collect(Collectors.toList());
                                if(idList != null && idList.size() > 0){
                                    QueryWrapper<TOrderGroupItemProject> tOrderGroupItemProjectQueryWrapper = new QueryWrapper<>();
                                    tOrderGroupItemProjectQueryWrapper.in("t_order_group_item_id",idList);
                                    tOrderGroupItemProjectQueryWrapper.eq("del_flag",0);
                                    count = itOrderGroupItemProjectService.count(tOrderGroupItemProjectQueryWrapper);
                                }
                            }
                            //复查项目也没有
                            if(count==0)
                            {
                                //保存分组项目的子项目
                                List<RelationBasePortfolio> collect = relationBasePortfolioList.stream()
                                        .filter(item -> item.getPortfolioProjectId().equals(reviewProject.getPortfolioProjectId()))
                                        .collect(Collectors.toList());
                                ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                                for (RelationBasePortfolio relationBasePortfolio : collect) {
                                    TBaseProject tBaseProject = tBaseProjectList.stream().filter(item -> item.getId().equals(relationBasePortfolio.getBaseProjectId())).findFirst().orElse(null);
                                    if(tBaseProject != null)
                                    {
                                        List<TOrderGroupItemProject> collect1 =
                                                projectArrayList.stream().filter(item -> item.getTOrderGroupItemId().equals(reviewProject.getId())
                                                        && item.getBaseProjectId().equals(tBaseProject.getId())).collect(Collectors.toList());
                                        if (collect1.size() > 0) {
                                            continue;
                                        }
                                        TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                                        tOrderGroupItemProject.setTOrderGroupItemId(reviewProject.getId());
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
//                                        tOrderGroupItemProject.setGroupOrderId(groupPerson.getId());
                                        tOrderGroupItemProject.setGroupOrderId(reviewPersonId);
                                        tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                                        projectArrayList.add(tOrderGroupItemProject);
                                    }
                                }
                                Boolean flag = itOrderGroupItemProjectService.saveBatch(projectArrayList);
                                if(!flag){
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return ResultUtil.error("保存失败：" + "保存复检项目失败，请重新保存");
                                }
                            }

                        }
                    }
                    else{
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("保存失败：" + "保存复检项目失败，请联系管理员");
                    }


                }
            }


            return ResultUtil.data("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param portfolioId 请求参数
     * @return
     */
    @ApiOperation("根据组合id获取基础项目")
    @GetMapping("/getBaseProjectByPortfolioId")
    public Result<Object> getBaseProjectByPortfolioId(String portfolioId) {
        if (StringUtils.isBlank(portfolioId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<RelationBasePortfolio> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("portfolio_project_id", portfolioId);
            List<RelationBasePortfolio> list = relationBasePortfolioService.list(queryWrapper);
            List<TBaseProject> projects = new ArrayList<>();

            if (list.size() > 0) {
                for (RelationBasePortfolio base : list) {
                    TBaseProject byId = baseProjectService.getById(base.getBaseProjectId());
                    projects.add(byId);
                }
            }
            return ResultUtil.data(projects);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常！");
        }
    }

    /**
     * 生成体检编号
     *
     * @return
     */
    public String generatorNum() {
        QueryWrapper<TReviewProject> queryWrapper = new QueryWrapper<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = format.format(new Date());
        queryWrapper.apply(StringUtils.isNotBlank(currentDay), "Date(create_time)=STR_TO_Date('" + currentDay + "','%Y-%m-%d')");
        queryWrapper.orderByDesc("test_num");
        queryWrapper.last("limit 1");
        TReviewProject one = tReviewProjectService.getOne(queryWrapper);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String testNum = "";
        if (one == null) {
            testNum = df.format(new Date());
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
            testNum = df.format(new Date());
            testNum += code;
        }
        return testNum;
    }
}
