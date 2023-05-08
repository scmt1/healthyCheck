package com.scmt.healthy.controller;

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
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author
 **/
@RestController
@Api(tags = " 人员科室确认表数据接口")
@RequestMapping("/scmt/relationPersonProjectCheck")
public class RelationPersonProjectCheckController {
    @Autowired
    private IRelationPersonProjectCheckService relationPersonProjectCheckService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITGroupPersonService itGroupPersonService;
    @Autowired
    private ITOrderGroupItemService itemService;
    @Autowired
    private IRelationPersonProjectCheckService iRelationPersonProjectCheckService;
    @Autowired
    private ITGroupPersonService personService;
    @Autowired
    private ITDepartResultService itDepartResultService;
    @Autowired
    private ITDepartItemResultService itDepartItemResultService;

    /**
     * 功能描述：新增人员科室确认表数据
     *
     * @param relationPersonProjectCheck 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("新增人员科室确认表数据")
    @PostMapping("addRelationPersonProjectCheck")
    public Result<Object> addRelationPersonProjectCheck(@RequestBody RelationPersonProjectCheck relationPersonProjectCheck) {
        if (StringUtils.isBlank(relationPersonProjectCheck.getPersonId()) || relationPersonProjectCheck.getState() == null) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<String> deparmentIds = securityUtil.getDeparmentIds();
            List<TOrderGroupItem> list = new ArrayList<>();
            list = relationPersonProjectCheckService.getNoRegistProjectData(relationPersonProjectCheck.getPersonId(), deparmentIds);
            Boolean isUpdate = true;
            if (list == null || list.size() == 0) {
                list = relationPersonProjectCheckService.getNoRegistProjectDataReview(relationPersonProjectCheck.getPersonId(), deparmentIds);
                if (list != null && list.size() > 0) {
                    isUpdate = false;
                }
            }
            ArrayList<RelationPersonProjectCheck> objects = new ArrayList<>();
            for (TOrderGroupItem item : list) {
                RelationPersonProjectCheck rpj = new RelationPersonProjectCheck();
                rpj.setPersonId(relationPersonProjectCheck.getPersonId());
                rpj.setState(relationPersonProjectCheck.getState());
                rpj.setOfficeId(item.getOfficeId());
                rpj.setOrderGroupItemId(item.getId());
                objects.add(rpj);
            }
            if (objects.size() > 0) {
                boolean b = relationPersonProjectCheckService.saveBatch(objects);
                if (b && isUpdate) {
                    TGroupPerson byId = itGroupPersonService.getById(relationPersonProjectCheck.getPersonId());
                    if (byId.getIsWzCheck() == 1 && byId.getIsPass() < 3) {
                        //所有检查项       //查询复查检查项和已检项
                        Integer count = itemService.getAllCheckCount(relationPersonProjectCheck.getPersonId(), byId.getGroupId());
                        Integer count1 = itemService.getDepartResultCount(relationPersonProjectCheck.getPersonId(), byId.getGroupId());

                        //弃检记录
                        QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                        checkQueryWrapper.eq("state", 2);
                        checkQueryWrapper.eq("person_id", byId.getId());
                        int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                        /*int count2 = 0;*/

                        //全部检查完  修改状态为3，到总检
                        if (count1.intValue() >= (count.intValue() - count2)) {
                            byId.setIsPass(3);
                            byId.setUpdateId(securityUtil.getCurrUser().getId());
                            byId.setUpdateTime(new Date());
                            itGroupPersonService.updateById(byId);
                        }
                    }
                }
            }
            return ResultUtil.data(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param relationPersonProjectCheck 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("更新人员科室确认表数据")
    @PostMapping("updateRelationPersonProjectCheck")
    public Result<Object> updateRelationPersonProjectCheck(@RequestBody RelationPersonProjectCheck relationPersonProjectCheck) {
        if (StringUtils.isBlank(relationPersonProjectCheck.getPersonId()) && StringUtils.isBlank(relationPersonProjectCheck.getOfficeId()) && StringUtils.isBlank(relationPersonProjectCheck.getOrderGroupItemId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<RelationPersonProjectCheck> wrapper = new QueryWrapper<>();
            wrapper.eq("person_id", relationPersonProjectCheck.getPersonId());
            wrapper.eq("office_id", relationPersonProjectCheck.getOfficeId());
            wrapper.eq("state", 1);
            wrapper.eq("order_group_item_id", relationPersonProjectCheck.getOrderGroupItemId());
            RelationPersonProjectCheck one = relationPersonProjectCheckService.getOne(wrapper);
            one.setAbandonRenson(relationPersonProjectCheck.getAbandonRenson());
            if (one != null) {
                QueryWrapper<TDepartResult> queryWrapper = new QueryWrapper();
                queryWrapper.eq("person_id", one.getPersonId());
                queryWrapper.eq("group_item_id", one.getOrderGroupItemId());
                List<TDepartResult> list1 = itDepartResultService.list(queryWrapper);
                if (list1.size() > 0) {
                    for (TDepartResult departResult : list1) {
                        QueryWrapper<TDepartItemResult> queryWrapper1 = new QueryWrapper<>();
                        queryWrapper1.eq("person_id", departResult.getPersonId());
                        queryWrapper1.eq("order_group_item_id", departResult.getGroupItemId());
                        List<TDepartItemResult> list = itDepartItemResultService.list(queryWrapper1);
                        if (list.size() > 0) {
                            itDepartItemResultService.remove(queryWrapper1);
                        }

                    }
                    itDepartResultService.remove(queryWrapper);
                }
            }
            //检查人员信息
            TGroupPerson byId = personService.getById(relationPersonProjectCheck.getPersonId());
            byId.setAvatar(null);
            //如果到科室登记过了的，直接修改状态为弃检，否则直接新增一条弃检记录
            if (one != null) {
                one.setState(2);
                boolean res = relationPersonProjectCheckService.updateById(one);
                boolean flag = false;
                if (res && byId.getIsWzCheck() == 1 && byId.getIsPass() < 3) {
                    //所有检查项       //查询复查检查项和已检项
                    Integer count = itemService.getAllCheckCount(byId.getId(), byId.getGroupId());
                    Integer count1 = itemService.getDepartResultCount(byId.getId(), byId.getGroupId());
                    //弃检记录
                    QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                    checkQueryWrapper.eq("state", 2);
                    checkQueryWrapper.eq("person_id", byId.getId());
                    int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);

                    /*int count2 = 0;*/
                    //全部检查完  修改状态为3，到总检
                    if (count1.intValue() >= (count.intValue() - count2)) {
                        byId.setIsPass(3);
                        byId.setUpdateId(securityUtil.getCurrUser().getId());
                        byId.setUpdateTime(new Date());
                        personService.updateById(byId);
                        flag = true;
                    }
                    return ResultUtil.data(flag, "修改成功");
                }
                if (res) {
                    return ResultUtil.data(res, "修改成功");
                } else {
                    return ResultUtil.error("修改失败");
                }
            } else {
                RelationPersonProjectCheck personProjectCheck = new RelationPersonProjectCheck();
                personProjectCheck.setPersonId(relationPersonProjectCheck.getPersonId());
                personProjectCheck.setOfficeId(relationPersonProjectCheck.getOfficeId());
                personProjectCheck.setState(2);
                personProjectCheck.setOrderGroupItemId(relationPersonProjectCheck.getOrderGroupItemId());
                boolean save = relationPersonProjectCheckService.save(personProjectCheck);
                boolean flag = false;
                if (save && byId.getIsWzCheck() == 1 && byId.getIsPass() < 3) {
                    //所有检查项       //查询复查检查项和已检项
                    Integer count = itemService.getAllCheckCount(byId.getId(), byId.getGroupId());
                    Integer count1 = itemService.getDepartResultCount(byId.getId(), byId.getGroupId());
                    //弃检记录
                    QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
                    checkQueryWrapper.eq("state", 2);
                    checkQueryWrapper.eq("person_id", byId.getId());
                    int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
                 /*   Integer count2 = 0;*/

                    //全部检查完  修改状态为3，到总检
                    if (count1.intValue() >= (count.intValue() - count2)) {
                        byId.setIsPass(3);
                        byId.setUpdateId(securityUtil.getCurrUser().getId());
                        byId.setUpdateTime(new Date());
                        personService.updateById(byId);
                        flag = true;
                    }
                    return ResultUtil.data(flag, "修改成功");
                }
                if (save) {
                    return ResultUtil.data(save, "修改成功");
                } else {
                    return ResultUtil.error("修改失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    @SystemLog(description = "更新人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("更新人员科室确认表数据")
    @PostMapping("updateCancelPersonProjectCheck")
    @Transactional(rollbackOn = {Exception.class})
    public Result<Object> updateCancelPersonProjectCheck(@RequestBody RelationPersonProjectCheck relationPersonProjectCheck) {
        if (StringUtils.isBlank(relationPersonProjectCheck.getPersonId()) && StringUtils.isBlank(relationPersonProjectCheck.getOfficeId()) && StringUtils.isBlank(relationPersonProjectCheck.getOrderGroupItemId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<RelationPersonProjectCheck> wrapper = new QueryWrapper<>();
            wrapper.eq("person_id", relationPersonProjectCheck.getPersonId());
            wrapper.eq("office_id", relationPersonProjectCheck.getOfficeId());
            wrapper.eq("state", 2);
            wrapper.eq("order_group_item_id", relationPersonProjectCheck.getOrderGroupItemId());
            RelationPersonProjectCheck one = relationPersonProjectCheckService.getOne(wrapper);
            one.setAbandonRenson(relationPersonProjectCheck.getAbandonRenson());
            //检查人员信息
            TGroupPerson byId = personService.getById(relationPersonProjectCheck.getPersonId());
            byId.setAvatar(null);
            if (byId != null && byId.getIsPass() != null) {
                //如果到科室登记过了的，直接修改状态为取消弃检，否则直接新增一条弃检记录
                if (one != null) {
                    if (byId.getIsPass() > 2) {
                        byId.setIsPass(2);
                        boolean resP = personService.updateById(byId);
                        if (resP) {
                            //清除弃检结果
                            boolean removeRes = this.removeResult(one);
                            if (!removeRes) {
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("修改失败：清除弃检结果失败");
                            }
                            //修改到检状态
                            one.setState(1);
                            boolean res = relationPersonProjectCheckService.updateById(one);
                            if (res) {
                                return ResultUtil.data(res, "修改成功");
                            } else {
                                //手工回滚异常
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return ResultUtil.error("修改到检状态失败");
                            }
                        } else {
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("修改人员体检状态失败");
                        }
                    } else {
                        //清除弃检结果
                        boolean removeRes = this.removeResult(one);
                        if (!removeRes) {
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("修改失败：清除弃检结果失败");
                        }
                        //修改到检状态
                        one.setState(1);
                        boolean res = relationPersonProjectCheckService.updateById(one);
                        if (res) {
                            return ResultUtil.data(res, "修改成功");
                        } else {
                            //手工回滚异常
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResultUtil.error("修改到检状态失败");
                        }
                    }
                } else {
                    //清除弃检结果
                    boolean removeRes = this.removeResult(one);
                    if (!removeRes) {
                        //手工回滚异常
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("修改失败：清除弃检结果失败");
                    }
                    RelationPersonProjectCheck personProjectCheck = new RelationPersonProjectCheck();
                    personProjectCheck.setPersonId(relationPersonProjectCheck.getPersonId());
                    personProjectCheck.setOfficeId(relationPersonProjectCheck.getOfficeId());
                    personProjectCheck.setState(1);
                    personProjectCheck.setOrderGroupItemId(relationPersonProjectCheck.getOrderGroupItemId());
                    boolean save = relationPersonProjectCheckService.save(personProjectCheck);
                    if (save) {
                        return ResultUtil.data(save, "修改成功");
                    } else {
                        //手工回滚异常
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.error("修改到检状态失败");
                    }
                }
            } else {
                //手工回滚异常
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error("修改失败：体检人员不存在或体检状态为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //手工回滚异常
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 清除结果(取消弃检 需先清除弃检结果)
     */
    public boolean removeResult(RelationPersonProjectCheck one) {
        //清除已保存弃检项目 的结果(组合项)
        QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
        tDepartResultQueryWrapper.eq("del_flag", 0);
        tDepartResultQueryWrapper.eq("person_id", one.getPersonId());
        tDepartResultQueryWrapper.eq("group_item_id", one.getOrderGroupItemId());
        boolean resD = itDepartResultService.remove(tDepartResultQueryWrapper);
        //清除已保存弃检项目 的结果(基础项)
        QueryWrapper<TDepartItemResult> tDepartItemResultQueryWrapper = new QueryWrapper<>();
        tDepartItemResultQueryWrapper.eq("del_flag", 0);
        tDepartItemResultQueryWrapper.eq("person_id", one.getPersonId());
        tDepartItemResultQueryWrapper.eq("order_group_item_id", one.getOrderGroupItemId());
        boolean resDr = itDepartItemResultService.remove(tDepartItemResultQueryWrapper);
        //返回清除结果
        if (resD && resDr) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除人员科室确认表数据")
    @SystemLog(description = "根据主键来删除人员科室确认表数据", type = LogType.OPERATION)
    @PostMapping("deleteRelationPersonProjectCheck")
    public Result<Object> deleteRelationPersonProjectCheck(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = relationPersonProjectCheckService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取人员科室确认表数据")
    @GetMapping("getRelationPersonProjectCheck")
    public Result<Object> getRelationPersonProjectCheck(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            RelationPersonProjectCheck res = relationPersonProjectCheckService.getById(id);
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
    @SystemLog(description = "分页查询人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("分页查询人员科室确认表数据")
    @GetMapping("queryRelationPersonProjectCheckList")
    public Result<Object> queryRelationPersonProjectCheckList(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<RelationPersonProjectCheck> result = relationPersonProjectCheckService.queryRelationPersonProjectCheckListByPage(relationPersonProjectCheck, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现查询全部
     *
     * @param searchVo 需要模糊查询的信息
     * @return 返回获取结果
     */
    @SystemLog(description = "查询人员科室确认表全部数据", type = LogType.OPERATION)
    @ApiOperation("查询人员科室确认表全部数据")
    @GetMapping("queryRelationPersonProjectCheckAll")
    public Result<Object> queryRelationPersonProjectCheckAll(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo) {
        try {
            List<RelationPersonProjectCheck> result = relationPersonProjectCheckService.queryRelationPersonProjectCheckListAll(relationPersonProjectCheck, searchVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response                   请求参数
     * @param relationPersonProjectCheck 查询参数
     * @return
     */
    @SystemLog(description = "导出人员科室确认表数据", type = LogType.OPERATION)
    @ApiOperation("导出人员科室确认表数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, RelationPersonProjectCheck relationPersonProjectCheck) {
        try {
            relationPersonProjectCheckService.download(relationPersonProjectCheck, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
