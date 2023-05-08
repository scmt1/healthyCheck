package com.scmt.healthy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 **/
@RestController
@Api(tags = " 条形码数据接口")
@RequestMapping("/scmt/tBarcode")
public class TBarcodeController {
    @Autowired
    private ITBarcodeService tBarcodeService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITLisDataService itLisDataService;
    @Autowired
    private ITPacsDataService itPacsDataService;
    @Autowired
    private ITConclusionService itConclusionService;
    @Autowired
    private ITGroupPersonService itGroupPersonService;
    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;
    @Autowired
    private ITDepartResultService tDepartResultService;
    @Autowired
    private ITReviewProjectService itReviewProjectService;
    @Autowired
    private ITReviewPersonService itReviewPersonService;
    /**
     * socket配置
     */
    @Autowired
    public SocketConfig socketConfig;

    /**
     * 功能描述：新增条形码数据
     *
     * @param tBarcode 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增条形码数据", type = LogType.OPERATION)
    @ApiOperation("新增条形码数据")
    @PostMapping("addTBarcode")
    public Result<Object> addTBarcode(@RequestBody TBarcode tBarcode) {
        try {
            tBarcode.setCreateTime(new Date());
            boolean res = tBarcodeService.save(tBarcode);
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
     * @param tBarcode 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新条形码数据", type = LogType.OPERATION)
    @ApiOperation("更新条形码数据")
    @PostMapping("updateTBarcode")
    public Result<Object> updateTBarcode(@RequestBody TBarcode tBarcode) {
        if (StringUtils.isBlank(tBarcode.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tBarcodeService.updateById(tBarcode);
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
    @ApiOperation("根据主键来删除条形码数据")
    @SystemLog(description = "根据主键来删除条形码数据", type = LogType.OPERATION)
    @PostMapping("deleteTBarcode")
    public Result<Object> deleteTBarcode(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tBarcodeService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取条形码数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取条形码数据")
    @GetMapping("getTBarcode")
    public Result<Object> getTBarcode(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TBarcode res = tBarcodeService.getById(id);
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
    @SystemLog(description = "分页查询条形码数据", type = LogType.OPERATION)
    @ApiOperation("分页查询条形码数据")
    @GetMapping("queryTBarcodeList")
    public Result<Object> queryTBarcodeList(TBarcode tBarcode, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TBarcode> result = tBarcodeService.queryTBarcodeListByPage(tBarcode, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response 请求参数
     * @param tBarcode 查询参数
     * @return
     */
    @SystemLog(description = "导出条形码数据", type = LogType.OPERATION)
    @ApiOperation("导出条形码数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TBarcode tBarcode) {
        try {
            tBarcodeService.download(tBarcode, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SystemLog(description = "根据条形码查询当前人员信息和项目信息", type = LogType.OPERATION)
    @ApiOperation("根据条形码查询当前人员信息和项目信息")
    @GetMapping("getPersonAndProjectInfoByBarcode")
    public Result<Object> getPersonAndProjectInfoByBarcode(String barcode) {
        QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
        wrapper.eq("barcode", barcode);
        TBarcode one = tBarcodeService.getOne(wrapper);
        if (one != null) {
            return ResultUtil.data(one);
        } else {
            return ResultUtil.error("人员信息不存在！");
        }
    }

    @ApiOperation("根据人员id和检查项目id查询对一个的条形码")
    @GetMapping("getBarcodeByPersonIdAndGroupItemId")
    public Result<Object> getBarcodeByPersonIdAndGroupItemId(String personId, String groupItemId, String testNum, String isFile, String type, String deptName) {
        if(StringUtils.isNotBlank(deptName)){
            deptName = deptName.trim();
        }
        TOrderGroupItem orderGroupItem = itOrderGroupItemService.getById(groupItemId);
        if(orderGroupItem==null || StringUtils.isBlank(orderGroupItem.getId())){
            //查询是否有复查项目
            TReviewProject tReviewProject = itReviewProjectService.getById(groupItemId);
            if(tReviewProject!=null && StringUtils.isNotBlank(tReviewProject.getId())){
                orderGroupItem = new TOrderGroupItem();
                BeanUtils.copyProperties(tReviewProject,orderGroupItem);
            }
        }
        if(orderGroupItem==null || StringUtils.isBlank(orderGroupItem.getId())){
            return ResultUtil.error("查询失败，没有查到对应项目数据!");
        }
        QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
        wrapper.eq("person_id", personId);
        wrapper.eq("test_num", testNum);
        wrapper.orderByDesc("create_time");
        wrapper.last("limit 1");
        if ("是".equals(isFile)) {
            wrapper.eq("type", 2);
        } else {
            if (socketConfig.getIsbiochemistryMerge() && (orderGroupItem != null && (orderGroupItem.getName().contains("血脂") || orderGroupItem.getName().contains("血糖")
                  || orderGroupItem.getName().contains("肝功") || orderGroupItem.getName().contains("肾功")|| orderGroupItem.getName().contains("ALT") || orderGroupItem.getName().contains("生化") || "cancer".equals(orderGroupItem.getSpecimen())))) {
                if(orderGroupItem.getName().contains("复")){
                    wrapper.eq("group_item_id", "99999999999999999999999999999998");
                }else{
                    wrapper.eq("group_item_id", "99999999999999999999999999999999");
                }
            } else {
                wrapper.eq("group_item_id", groupItemId);
            }
            wrapper.and(i -> i.eq("type", 1).or().eq("type", 3));
        }
        TBarcode one = tBarcodeService.getOne(wrapper);
        if (one != null) {
            if ("是".equals(isFile)) {
                QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
                String barCode = one.getBarcode();
                if(socketConfig.getIsCodeLike()){
                    queryWrapper.like("code", barCode);
                }else{
                    queryWrapper.eq("code", barCode);
                }
                queryWrapper.eq("type", type);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("limit 1");
                TPacsData pacsData = itPacsDataService.getOne(queryWrapper);
                if (pacsData == null && socketConfig.getIsPersonName()) {
                    pacsData = getPacsData(personId, type);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("pacsData", pacsData);

                //根据barcode 查询同步回来的体检小结和映像所见
                QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
                if(socketConfig.getIsCodeLike()){
                    conclusionWrapper.like("code", one.getBarcode());
                }else{
                    conclusionWrapper.eq("code", one.getBarcode());
                }
                if(StringUtils.isNotBlank(deptName)){
                    conclusionWrapper.like("viewpos", deptName);
                }
                conclusionWrapper.eq("type", type);
                conclusionWrapper.orderByDesc("create_time");
                conclusionWrapper.last("limit 1");
                TConclusion one1 = itConclusionService.getOne(conclusionWrapper);
                if (one1 == null) {
                    TGroupPerson byId = itGroupPersonService.getById(personId);
                    if(byId!=null && !byId.getPhysicalType().contains("职业体检")){
                        conclusionWrapper = new QueryWrapper<>();
                        conclusionWrapper.eq("person_name", byId.getPersonName());
                        conclusionWrapper.eq("type", type);
                        if(StringUtils.isNotBlank(deptName)){
                            conclusionWrapper.like("viewpos", deptName);
                        }
                        if(byId!=null && byId.getDiagnosisDate()!=null){//若总检日期存在，则属于复查同步 取总检日期条件判断
                            conclusionWrapper.gt("create_time",byId.getDiagnosisDate());
                        }else if(byId!=null && byId.getRegistDate()!=null){//属于第一次同步 取登记日期条件判断
                            conclusionWrapper.gt("create_time",byId.getRegistDate());
                        }
                        conclusionWrapper.orderByDesc("create_time");
                        conclusionWrapper.last("limit 1");
                        one1 = itConclusionService.getOne(conclusionWrapper);
                    }else{
                        one1 = null;
                    }
                }
                map.put("conclusion", one1);
                return ResultUtil.data(map);
            } else {
                //根据barcode 去读取结果
                QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
                String barCode = one.getBarcode();
//                if(StringUtils.isNotBlank(barCode) && socketConfig.getLisCode()){
//                    barCode = barCode.substring(3);
//                }
                if(socketConfig.getIsCodeLike()){
                    queryWrapper.like("code", barCode);
                }else{
                    queryWrapper.eq("code", barCode);
                }
                queryWrapper.eq("type", type);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("limit 1");
                TLisData tLisData = itLisDataService.getOne(queryWrapper);

                //额外判断复查同步逻辑(复查条码与体检编号不一样) type:xhlis
                if(tLisData==null){
                    //根据barcode 去读取结果
                    queryWrapper = new QueryWrapper<>();
                    if(socketConfig.getIsCodeLike()){
                        queryWrapper.like("code", barCode);
                    }else{
                        queryWrapper.eq("code", barCode);
                    }
                    queryWrapper.eq("type", "xhlis");
                    queryWrapper.orderByDesc("create_time");
                    queryWrapper.last("LIMIT 1");
                    tLisData = itLisDataService.getOne(queryWrapper);
                }

                //额外判断是否有体检编号
                if(tLisData==null){
                    //根据barcode 去读取结果
                    queryWrapper = new QueryWrapper<>();
                    String testNumNew = testNum;
//                    if(StringUtils.isNotBlank(""+testNumNew) && socketConfig.getLisCode()){
//                        testNumNew = testNumNew.substring(3);
//                    }
                    if(socketConfig.getIsCodeLike()){
                        queryWrapper.like("code", testNumNew);
                    }else{
                        queryWrapper.eq("code", testNumNew);
                    }
                    queryWrapper.eq("type", type);
                    queryWrapper.orderByDesc("create_time");
                    queryWrapper.last("LIMIT 1");
                    tLisData = itLisDataService.getOne(queryWrapper);
                    if(tLisData==null){
                        //根据barcode 去读取结果
                        queryWrapper = new QueryWrapper<>();
                        if(socketConfig.getIsCodeLike()){
                            queryWrapper.like("code", testNum.substring(3));
                        }else{
                            queryWrapper.eq("code", testNum.substring(3));
                        }
                        queryWrapper.eq("type", "xhlis");
                        queryWrapper.orderByDesc("create_time");
                        queryWrapper.last("LIMIT 1");
                        tLisData = itLisDataService.getOne(queryWrapper);
                    }

                }
                if (tLisData == null && socketConfig.getIsPersonName()) {
                    tLisData = getLisData(personId, type);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("pacsData", tLisData);
                //根据barcode 查询同步回来的体检小结和映像所见
                QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
                if(socketConfig.getIsCodeLike()){
                    conclusionWrapper.like("code", one.getBarcode());
                }else{
                    conclusionWrapper.eq("code", one.getBarcode());
                }
                conclusionWrapper.eq("type", type);
                conclusionWrapper.orderByDesc("create_time");
                conclusionWrapper.last("limit 1");
                TConclusion one1 = itConclusionService.getOne(conclusionWrapper);
                if (one1 == null) {
                    TGroupPerson byId = itGroupPersonService.getById(personId);
                    if(byId!=null && !byId.getPhysicalType().contains("职业体检")){
                        conclusionWrapper = new QueryWrapper<>();
                        conclusionWrapper.eq("person_name", byId.getPersonName());
                        conclusionWrapper.eq("type", type);
                        if(byId!=null && byId.getDiagnosisDate()!=null){//若总检日期存在，则属于复查同步 取总检日期条件判断
                            conclusionWrapper.gt("create_time",byId.getDiagnosisDate());
                        }else if(byId!=null && byId.getRegistDate()!=null){//属于第一次同步 取登记日期条件判断
                            conclusionWrapper.gt("create_time",byId.getRegistDate());
                        }
                        conclusionWrapper.orderByDesc("create_time");
                        conclusionWrapper.last("limit 1");
                        one1 = itConclusionService.getOne(conclusionWrapper);
                    }else{
                        one1 = null;
                    }
                }
                map.put("conclusion", one1);
                return ResultUtil.data(map);
            }
        } else {
            if ("是".equals(isFile)) {
                QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
                if(socketConfig.getIsCodeLike()){
                    queryWrapper.like("code", testNum);
                }else{
                    queryWrapper.eq("code", testNum);
                }
                queryWrapper.eq("type", type);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("limit 1");
                TPacsData pacsData = itPacsDataService.getOne(queryWrapper);
                if(pacsData==null && socketConfig.getIsPersonName()){
                    //根据名称查询
                    pacsData = getPacsData(personId, type);
                }
                TConclusion one1 = null;
                if (pacsData != null) {
                    //查询同步回来的体检小结和映像所见
                    QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
                    if(socketConfig.getIsCodeLike()){
                        conclusionWrapper.like("code", pacsData.getCode());
                    }else{
                        conclusionWrapper.eq("code", pacsData.getCode());
                    }
                    if(StringUtils.isNotBlank(deptName)){
                        conclusionWrapper.like("viewpos", deptName);
                    }
                    conclusionWrapper.eq("type", type);
                    conclusionWrapper.orderByDesc("create_time");
                    conclusionWrapper.last("limit 1");
                    one1 = itConclusionService.getOne(conclusionWrapper);
                } else {
                    TGroupPerson byId = itGroupPersonService.getById(personId);
                    if(byId!=null && !byId.getPhysicalType().contains("职业体检")){
                        QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
                        conclusionWrapper.eq("person_name", byId.getPersonName());
                        conclusionWrapper.eq("type", type);
                        if(StringUtils.isNotBlank(deptName)){
                            conclusionWrapper.like("viewpos", deptName);
                        }
                        if(byId!=null && byId.getDiagnosisDate()!=null){//若总检日期存在，则属于复查同步 取总检日期条件判断
                            conclusionWrapper.gt("create_time",byId.getDiagnosisDate());
                        }else if(byId!=null && byId.getRegistDate()!=null){//属于第一次同步 取登记日期条件判断
                            conclusionWrapper.gt("create_time",byId.getRegistDate());
                        }
                        conclusionWrapper.orderByDesc("create_time");
                        conclusionWrapper.last("limit 1");
                        one1 = itConclusionService.getOne(conclusionWrapper);
                    }else{
                        one1 = null;
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("pacsData", pacsData);
                map.put("conclusion", one1);
                return ResultUtil.data(map);
            } else {
                Map<String, Object> map = new HashMap<>();
                //根据testNumber 去读取结果
                QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
                if(socketConfig.getIsCodeLike()){
                    queryWrapper.like("code", testNum);
                }else{
                    queryWrapper.eq("code", testNum);
                }
                queryWrapper.eq("type", type);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("LIMIT 1");
                TLisData lisData = itLisDataService.getOne(queryWrapper);

                if(lisData==null){
                    //根据barcode 去读取结果
                    queryWrapper = new QueryWrapper<>();
                    if(socketConfig.getIsCodeLike()){
                        queryWrapper.like("code", testNum.substring(3));
                    }else{
                        queryWrapper.eq("code", testNum);
                    }
                    queryWrapper.eq("type", "xhlis");
                    queryWrapper.orderByDesc("create_time");
                    queryWrapper.last("LIMIT 1");
                    lisData = itLisDataService.getOne(queryWrapper);
                }
                if(lisData == null && socketConfig.getIsPersonName()){
                    lisData = getLisData(personId, type);
                }

                map.put("pacsData", lisData);
                map.put("conclusion", null);
                return ResultUtil.data(map);
            }
        }
    }

    /**
     * 根据姓名获取 pacsData
     *
     * @param personId
     * @param type
     * @return
     */
    private TPacsData getPacsData(String personId, String type) {
        TPacsData tPacsData = new TPacsData();
        TGroupPerson byId = itGroupPersonService.getById(personId);
        TReviewPerson byIdR = new TReviewPerson();
        if(byId==null){
            byIdR = itReviewPersonService.getById(personId);
        }
        if(byId!=null && byId.getPhysicalType()!=null && !byId.getPhysicalType().contains("职业体检")){//第一次体检
            QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("person_name", byId.getPersonName());
            queryWrapper.eq("type", type);
            if(byId!=null && byId.getRegistDate()!=null){
                queryWrapper.gt("create_time",byId.getRegistDate());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("LIMIT 1");
            tPacsData = itPacsDataService.getOne(queryWrapper);
        }else if(byIdR!=null && byIdR.getPhysicalType()!=null && !byIdR.getPhysicalType().contains("职业体检")) {//复查
            QueryWrapper<TPacsData> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("person_name", byIdR.getPersonName());
            queryWrapper.eq("type", type);
            if(byIdR!=null && byIdR.getRegistDate()!=null){
                queryWrapper.gt("create_time",byIdR.getRegistDate());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("LIMIT 1");
            tPacsData = itPacsDataService.getOne(queryWrapper);
        }else{
            tPacsData = null;
        }
        return tPacsData;
    }


    @ApiOperation("根据人员id和体检编号查询对一个的条形码")
    @GetMapping("getBarcodeByPersonIdAndTestNum")
    public Result<Object> getBarcodeByPersonIdAndTestNum(String personId, String groupItemId, String testNum, String type) {
        TBarcode tBarcode = tBarcodeService.getTBarcodeByPersonIdAndItemId(personId, groupItemId, testNum);
        if(tBarcode == null){
            tBarcode = tBarcodeService.getTBarcodeByPersonId(personId, testNum);
        }
        if (tBarcode != null) {
            //根据barcode 去读取结果
            QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
            if(socketConfig.getIsCodeLike()){
                queryWrapper.like("code", tBarcode.getBarcode());
            }else{
                queryWrapper.eq("code", tBarcode.getBarcode());
            }
            queryWrapper.eq("type", type);
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("LIMIT 1");
            TLisData tLisData = itLisDataService.getOne(queryWrapper);

            Map<String, Object> map = new HashMap<>();

            //查询lis
            if (tLisData == null && socketConfig.getIsPersonName()) {
                //根据人名去匹配最新一条
                tLisData = getLisData(personId, type);
            }
            map.put("lisData", tLisData);
            //查询体检结论
            QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
            if(socketConfig.getIsCodeLike()){
                conclusionWrapper.like("code", tBarcode.getBarcode());
            }else{
                conclusionWrapper.eq("code", tBarcode.getBarcode());
            }
            conclusionWrapper.eq("type", type);
            conclusionWrapper.orderByDesc("create_time");
            conclusionWrapper.last("limit 1");
            TConclusion one = itConclusionService.getOne(conclusionWrapper);
            /*if (one == null) {
                conclusionWrapper = new QueryWrapper<>();
                TGroupPerson byId = itGroupPersonService.getById(personId);
                conclusionWrapper.eq("person_name", byId.getPersonName());
                conclusionWrapper.eq("type", type);
                conclusionWrapper.orderByDesc("create_time");
                conclusionWrapper.last("limit 1");
                one = itConclusionService.getOne(conclusionWrapper);
            }*/
            map.put("conclusion", one);
            return ResultUtil.data(map);
        } else {
            TLisData lisData = null;
            if(socketConfig.getIsPersonName()){
                lisData = getLisData(personId, type);
            }
            Map<String, Object> map = new HashMap<>();
            //额外判断是否有体检编号
            if(lisData==null){
                //根据barcode 去读取结果
                QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
                if(socketConfig.getIsCodeLike()){
                    queryWrapper.like("code", testNum);
                }else{
                    queryWrapper.eq("code", testNum);
                }
                queryWrapper.eq("type", type);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("LIMIT 1");
                lisData = itLisDataService.getOne(queryWrapper);
                /*if(lisData==null){
                    QueryWrapper<TLisData> queryWrapperNow = new QueryWrapper<>();
                    String testNumNow = testNum.substring(3);
                    if(socketConfig.getIsCodeLike()){
                        queryWrapperNow.like("code", testNumNow);
                    }else{
                        queryWrapperNow.eq("code", testNumNow);
                    }
                    queryWrapperNow.eq("type", type);
                    queryWrapperNow.orderByDesc("create_time");
                    queryWrapperNow.last("LIMIT 1");
                    lisData = itLisDataService.getOne(queryWrapperNow);
                }*/
            }
            map.put("lisData", lisData);

            //影像结论
            QueryWrapper<TConclusion> conclusionWrapper = new QueryWrapper<>();
            if(socketConfig.getIsCodeLike()){
                conclusionWrapper.like("code", testNum);
            }else{
                conclusionWrapper.eq("code", testNum);
            }
            conclusionWrapper.eq("type", type);
            conclusionWrapper.orderByDesc("create_time");
            conclusionWrapper.last("limit 1");
            TConclusion one = itConclusionService.getOne(conclusionWrapper);
            /*if (one == null) {
                conclusionWrapper = new QueryWrapper<>();
                TGroupPerson byId = itGroupPersonService.getById(personId);
                conclusionWrapper.eq("person_name", byId.getPersonName());
                conclusionWrapper.eq("type", type);
                conclusionWrapper.orderByDesc("create_time");
                conclusionWrapper.last("limit 1");
                one = itConclusionService.getOne(conclusionWrapper);
            }*/
            /*if(one==null){
                QueryWrapper<TConclusion> conclusionWrapperNow = new QueryWrapper<>();
                String testNumNow = testNum.substring(3);
                if(socketConfig.getIsCodeLike()){
                    conclusionWrapperNow.like("code", testNumNow);
                }else{
                    conclusionWrapperNow.eq("code", testNumNow);
                }
                conclusionWrapperNow.eq("type", type);
                conclusionWrapperNow.orderByDesc("create_time");
                conclusionWrapperNow.last("limit 1");
                one = itConclusionService.getOne(conclusionWrapperNow);
            }*/
            map.put("conclusion", one);
            return ResultUtil.data(map);
        }
    }

    @ApiOperation("批量修改组合项结果表(血常规)")
    @GetMapping("batchUpdateDepartResult")
    public Result<Object> batchUpdateDepartResult() {
        QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
        tDepartResultQueryWrapper.isNull("url");
        tDepartResultQueryWrapper.like("group_item_name","血常规");
        List<TDepartResult> tDepartResults = tDepartResultService.list(tDepartResultQueryWrapper);
        //根据barcode 去读取结果
        QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "ZN-HA");
        List<TLisData> tLisDatas = itLisDataService.list(queryWrapper);
        for (TDepartResult tDepartResult : tDepartResults){
            QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
            wrapper.eq("person_id", tDepartResult.getPersonId());
            wrapper.eq("group_item_id", tDepartResult.getGroupItemId());
            TBarcode one = tBarcodeService.getOne(wrapper);
            if(one != null && StringUtils.isNotBlank(one.getBarcode())){
                List<TLisData> tLisDataNew = tLisDatas.stream().filter(ii -> ii.getCode().equals(one.getBarcode())).collect(Collectors.toList());

                if(tLisDataNew != null && tLisDataNew.size() > 0){

                    JSONArray jsonData = JSON.parseArray(tLisDataNew.get(0).getData());
                    String urls = "";
                    for(int i=0;i<jsonData.size();i++) {
                        if(jsonData.getJSONObject(i) != null && jsonData.getJSONObject(i).containsKey("type") && jsonData.getJSONObject(i).get("type").equals("image")){
                            if(urls == ""){
                                urls += jsonData.getJSONObject(i).get("base64");
                            }else{
                                urls += "," + jsonData.getJSONObject(i).get("base64");
                            }
                        }
                    }

                    QueryWrapper<TDepartResult> tDepartResultUpdateWrapper = new QueryWrapper<>();
                    tDepartResultUpdateWrapper.eq("id", tDepartResult.getId());
                    tDepartResultUpdateWrapper.eq("person_id", tDepartResult.getPersonId());
                    tDepartResultUpdateWrapper.eq("group_item_name", "血常规");
                    TDepartResult tDepartResultNew = new TDepartResult();
                    tDepartResultNew.setUrl(urls);
                    tDepartResultService.update(tDepartResultNew,tDepartResultUpdateWrapper);
                }
            }
        }
        return ResultUtil.data(true,"修改完成");
    }

    /**
     * 根据姓名获取 lisData
     *
     * @param personId
     * @param type
     * @return
     */
    private TLisData getLisData(String personId, String type) {
        TLisData tLisData = new TLisData();
        TGroupPerson byId = itGroupPersonService.getById(personId);
        TReviewPerson byIdR = new TReviewPerson();
        if(byId==null){
           byIdR = itReviewPersonService.getById(personId);
        }
        if(byId!=null && byId.getPhysicalType()!=null && !byId.getPhysicalType().contains("职业体检")){//第一次体检
            QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("person_name", byId.getPersonName());
            queryWrapper.eq("type", type);
            if(byId!=null && byId.getRegistDate()!=null){
                queryWrapper.gt("create_time",byId.getRegistDate());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("limit 1");
            tLisData = itLisDataService.getOne(queryWrapper);
        }else if(byIdR!=null && byIdR.getPhysicalType()!=null && !byIdR.getPhysicalType().contains("职业体检")){//复查
            QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("person_name", byIdR.getPersonName());
            queryWrapper.eq("type", type);
            if(byIdR!=null && byIdR.getRegistDate()!=null){
                queryWrapper.gt("create_time",byIdR.getRegistDate());
            }
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("limit 1");
            tLisData = itLisDataService.getOne(queryWrapper);
        }else{
            tLisData = null;
        }
        return tLisData;
    }
}
