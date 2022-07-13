package com.scmt.healthy.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.service.ITDepartItemResultService;
import org.apache.ibatis.annotations.Param;
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
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 *@author
 **/
@RestController
@Api(tags =" 基础项目检查结果数据接口")
@RequestMapping("/scmt/tDepartItemResult")
public class TDepartItemResultController{
    @Autowired
    private ITDepartItemResultService tDepartItemResultService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增基础项目检查结果数据
     * @param tDepartItemResult 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("新增基础项目检查结果数据")
    @PostMapping("addTDepartItemResult")
    public Result<Object> addTDepartItemResult(@RequestBody TDepartItemResult tDepartItemResult){
        try {
            tDepartItemResult.setDelFlag(0);
            tDepartItemResult.setCreateId(securityUtil.getCurrUser().getId());
            tDepartItemResult.setCreateDate(new Date());
            boolean res = tDepartItemResultService.save(tDepartItemResult);
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
     * @param tDepartItemResult 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("更新基础项目检查结果数据")
    @PostMapping("updateTDepartItemResult")
    public Result<Object> updateTDepartItemResult(@RequestBody TDepartItemResult tDepartItemResult){
        if (StringUtils.isBlank(tDepartItemResult.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tDepartItemResult.setUpdateId(securityUtil.getCurrUser().getId());
            tDepartItemResult.setUpdateDate(new Date());
            boolean res = tDepartItemResultService.updateById(tDepartItemResult);
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
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除基础项目检查结果数据")
    @SystemLog(description = "根据主键来删除基础项目检查结果数据", type = LogType.OPERATION)
    @PostMapping("deleteTDepartItemResult")
    public Result<Object> deleteTDepartItemResult(@RequestParam String[] ids){
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tDepartItemResultService.removeByIds(Arrays.asList(ids));
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
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取基础项目检查结果数据")
    @GetMapping("getTDepartItemResult")
    public Result<Object> getTDepartItemResult(@RequestParam(name = "id")String id){
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TDepartItemResult res = tDepartItemResultService.getById(id);
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
     * 功能描述：根据人员id来获取异常数据
     * @param personId 人员id
     * @return 返回获取结果
     */
    @SystemLog(description = "根据人员id来获取异常数据", type = LogType.OPERATION)
    @ApiOperation("根据人员id来获取异常数据")
    @GetMapping("getAbnormalResultList")
    public Result<Object> getAbnormalResultList(@RequestParam(name = "personId")String personId){
        if (StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TDepartItemResult> list = tDepartItemResultService.getAbnormalResultList(personId);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现分页查询
     * @param searchVo 需要模糊查询的信息
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("分页查询基础项目检查结果数据")
    @GetMapping("queryTDepartItemResultList")
    public Result<Object> queryTDepartItemResultList(TDepartItemResult  tDepartItemResult, SearchVo searchVo, PageVo pageVo){
        try {
            IPage<TDepartItemResult> result =tDepartItemResultService.queryTDepartItemResultListByPage(tDepartItemResult, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：分页查询阳性结果结果数据
     * @param searchVo 需要模糊查询的信息
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询阳性结果结果数据", type = LogType.OPERATION)
    @ApiOperation("分页查询阳性结果结果数据")
    @GetMapping("querySummaryResultList")
    public Result<Object> querySummaryResultList(TDepartItemResult  tDepartItemResult, SearchVo searchVo, PageVo pageVo){
        try {
            IPage<TDepartItemResult> result =tDepartItemResultService.querySummaryResultListByPage(tDepartItemResult, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询检查项目及结果
     * @param personId 人员id
     * @param officeId 科室id
     * @param departResultId 分检结果id
     * @return 返回获取结果
     */
    @SystemLog(description = "查询基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("查询基础项目检查结果数据")
    @GetMapping("queryAllTDepartItemResultList")
    public Result<Object> queryAllTDepartItemResultList(@Param("personId") String personId, @Param("officeId") String officeId, @Param("checkDate") String checkDate, @Param("departResultId") String departResultId){
        try {
            List<TDepartItemResult> result =tDepartItemResultService.queryAllTDepartItemResultList(personId,officeId,checkDate,departResultId);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询基础项目检查结果异常数据
     * @param tDepartItemResult
     * @return 返回获取结果
     */
    @SystemLog(description = "查询基础项目检查结果异常数据", type = LogType.OPERATION)
    @ApiOperation("查询基础项目检查结果异常数据")
    @GetMapping("queryAllAbnormalItemResultList")
    public Result<Object> queryAllAbnormalItemResultList(TDepartItemResult  tDepartItemResult){
        try {
            List<TDepartItemResult> result =tDepartItemResultService.queryAllAbnormalItemResultList(tDepartItemResult);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     * @param response 请求参数
     * @param tDepartItemResult 查询参数
     * @return
     */
    @SystemLog(description = "导出基础项目检查结果数据", type = LogType.OPERATION)
    @ApiOperation("导出基础项目检查结果数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response,TDepartItemResult  tDepartItemResult){
        try {
            tDepartItemResultService.download( tDepartItemResult,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("电测听左右耳值对调")
    @GetMapping("dataHandTcErrorData")
    public Result<Object> dataHandTcErrorData(){
        try {
            QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.inSql("person_id", "SELECT id FROM t_group_person WHERE unit_id in  (SELECT id FROM t_group_unit WHERE id like '%W%' and create_time >=  '2022-03-07 00:00:00' AND  create_time <= '2022-03-11 09:01:40') and del_flag = 0");
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("office_id", 202);
            List<TDepartItemResult> result =tDepartItemResultService.list(queryWrapper);
            Map<String, List<TDepartItemResult>> map = result.stream().collect(Collectors.groupingBy(t -> t.getPersonId()));
            if(map!=null && map.size()>0){
                /*然后再对map处理，这样就方便取出自己要的数据*/
                for(Map.Entry<String, List<TDepartItemResult>> entry : map.entrySet()){
                    List<TDepartItemResult> personResult = entry.getValue();
                    if(personResult!=null && personResult.size()>0){
                        for (int i = 0; i < personResult.size(); i++) {
                            TDepartItemResult tDepartItemResult = personResult.get(i);
                            if (tDepartItemResult != null && StringUtils.isNotBlank(tDepartItemResult.getId())){
                                if (StringUtils.isNotBlank(tDepartItemResult.getResult()) && StringUtils.isNotBlank(tDepartItemResult.getOrderGroupItemProjectName()) && tDepartItemResult.getOrderGroupItemProjectName().contains("左耳")) {
                                    String result1 =tDepartItemResult.getResult();

                                    //查找右耳的值
                                    String name = tDepartItemResult.getOrderGroupItemProjectName().replaceAll("左耳","右耳");
                                    List<TDepartItemResult> collect = personResult.stream().filter(ii -> ii.getOrderGroupItemProjectName().equals(name)).collect(Collectors.toList());
                                    if(collect!=null && collect.size()>0){
                                        tDepartItemResult.setResult(collect.get(0).getResult());
                                        collect.get(0).setResult(result1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            tDepartItemResultService.updateBatchById(result);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
