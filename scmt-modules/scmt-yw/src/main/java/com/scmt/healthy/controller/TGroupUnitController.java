package com.scmt.healthy.controller;

import java.sql.Blob;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;

import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.UploadFileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 **/
@RestController
@Api(tags = "团检单位数据接口")
@RequestMapping("/scmt/tGroupUnit")
public class TGroupUnitController {
    @Autowired
    private ITGroupUnitService tGroupUnitService;

    @Autowired
    private ITGroupPersonService tGroupPersonService;

    @Autowired
    private ITGroupOrderService tGroupOrderService;
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TTestRecordService tTestRecordService;

    @Autowired
    private ITReviewPersonService itReviewPersonService;

    /**
     * 功能描述：新增团检单位数据
     *
     * @param tGroupUnit 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增团检单位数据", type = LogType.OPERATION)
    @ApiOperation("新增团检单位数据")
    @PostMapping("addTGroupUnit")
    public Result<Object> addTGroupUnit(@RequestBody TGroupUnit tGroupUnit) {
        try {
            QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            /*if("职业体检".equals(tGroupUnit.getPhysicalType()) || "放射体检".equals(tGroupUnit.getPhysicalType())){
                queryWrapper.eq("uscc", tGroupUnit.getUscc());
            }else{
                queryWrapper.eq("name", tGroupUnit.getName());
            }*/
            queryWrapper.eq("uscc", tGroupUnit.getUscc());
            queryWrapper.eq("name", tGroupUnit.getName());
            queryWrapper.eq("physical_type", tGroupUnit.getPhysicalType());
            Integer one = tGroupUnitService.count(queryWrapper);
            if(one > 0) {
                return ResultUtil.error("该公司已存在，无需新增！");
            }
            tGroupUnit.setDelFlag(0);
            tGroupUnit.setCreateId(securityUtil.getCurrUser().getId());
            tGroupUnit.setCreateTime(new Date());
            if (tGroupUnit.getAttachment() != null && StringUtils.isNotBlank(tGroupUnit.getAttachment().toString())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupUnit.getAttachment().toString());
                Blob blob = new SerialBlob(imgFile.getBytes());
                tGroupUnit.setAttachment(blob);
            }
            boolean res = tGroupUnitService.save(tGroupUnit);
            if (res) {
                TGroupUnit tGroupUnit1 = tGroupUnitService.getOne(queryWrapper);
                //更新检测信息
                QueryWrapper<TTestRecord> tTestRecordQueryWrapper = new QueryWrapper<>();
                tTestRecordQueryWrapper.eq("unit_id",tGroupUnit1.getId());
                tTestRecordService.remove(tTestRecordQueryWrapper);//清除旧数据
                for(TTestRecord tTestRecord : tGroupUnit.getTestRecordData()){
                    tTestRecord.setUnitId(tGroupUnit1.getId());
                    tTestRecord.setCreateTime(new Date());
                    tTestRecord.setCreateId(securityUtil.getCurrUser().getId());
                }
                boolean res1 = tTestRecordService.saveBatch(tGroupUnit.getTestRecordData());//添加
                if(res1){
                    return ResultUtil.data(res1, "保存成功");
                }else{
                    return ResultUtil.data(res1, "保存失败");
                }
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
     * @param tGroupUnit 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新团检单位数据", type = LogType.OPERATION)
    @ApiOperation("更新团检单位数据")
    @PostMapping("updateTGroupUnit")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> updateTGroupUnit(@RequestBody TGroupUnit tGroupUnit) {
        if (StringUtils.isBlank(tGroupUnit.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            /*if("职业体检".equals(tGroupUnit.getPhysicalType()) || "放射体检".equals(tGroupUnit.getPhysicalType())){
                queryWrapper.eq("uscc", tGroupUnit.getUscc());
            }else{
                queryWrapper.eq("name", tGroupUnit.getName());
            }*/
            /*queryWrapper.eq("uscc", tGroupUnit.getUscc());
            queryWrapper.eq("name", tGroupUnit.getName());
            queryWrapper.ne("id", tGroupUnit.getId());
            queryWrapper.eq("physical_type", tGroupUnit.getPhysicalType());
            Integer one = tGroupUnitService.count(queryWrapper);
            if(one > 0) {
                return ResultUtil.error("该公司已存在，不能修改为此公司！");
            }*/
            tGroupUnit.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupUnit.setUpdateTime(new Date());
            if (tGroupUnit.getAttachment() != null && StringUtils.isNotBlank(tGroupUnit.getAttachment().toString())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tGroupUnit.getAttachment().toString());
                Blob blob = new SerialBlob(imgFile.getBytes());
                tGroupUnit.setAttachment(blob);
            }
            boolean res = false;
            //查询原本的单位信息
            TGroupUnit tGroupUnitOld = tGroupUnitService.getById(tGroupUnit.getId());
            if(tGroupUnit.getName()!=null && StringUtils.isNotBlank(tGroupUnit.getName())){
                /*//查询原本的单位信息
                TGroupUnit tGroupUnitOld = tGroupUnitService.getById(tGroupUnit.getId());*/
                //若名称一样则批量修改
                QueryWrapper<TGroupUnit> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("del_flag",0);
                queryWrapper1.eq("name",tGroupUnitOld.getName());
                queryWrapper1.eq("physical_type",tGroupUnit.getPhysicalType());
                tGroupUnit.setId(null);
                res = tGroupUnitService.update(tGroupUnit,queryWrapper1);
            }else{
                //根据id修改
                res = tGroupUnitService.updateById(tGroupUnit);
            }
            if (res) {
                //更新订单 dept字段
                QueryWrapper<TGroupOrder> groupOrderQueryWrapper = new QueryWrapper<>();
                if(tGroupUnit.getName()!=null && StringUtils.isNotBlank(tGroupUnit.getName())){
                    groupOrderQueryWrapper.eq("group_unit_name",tGroupUnitOld.getName());
                }else{
                    groupOrderQueryWrapper.eq("group_unit_id",tGroupUnit.getId());
                }
                groupOrderQueryWrapper.eq("physical_type",tGroupUnit.getPhysicalType());
                groupOrderQueryWrapper.eq("del_flag",0);
                /*List<TGroupOrder> tGroupOrderList = tGroupOrderService.list(groupOrderQueryWrapper);
                for(TGroupOrder tGroupOrder : tGroupOrderList){
                    tGroupOrder.setGroupUnitName(tGroupUnit.getName());
                }
                tGroupOrderService.updateBatchById(tGroupOrderList);*/
                TGroupOrder groupOrder= new TGroupOrder();
                groupOrder.setGroupUnitName(tGroupUnit.getName());
                tGroupOrderService.update(groupOrder,groupOrderQueryWrapper);
                //更新人员 dept 字段
                QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
                if(tGroupUnit.getName()!=null && StringUtils.isNotBlank(tGroupUnit.getName())){
                    personQueryWrapper.eq("dept",tGroupUnitOld.getName());
                }else{
                    personQueryWrapper.eq("unit_id",tGroupUnit.getId());
                }
                personQueryWrapper.eq("physical_type",tGroupUnit.getPhysicalType());
                personQueryWrapper.eq("del_flag",0);
                /*List<TGroupPerson> tGroupPersonList = tGroupPersonService.list(personQueryWrapper);
                for(TGroupPerson tGroupPerson : tGroupPersonList){
                    tGroupPerson.setDept(tGroupUnit.getName());
                }
                tGroupPersonService.updateBatchById(tGroupPersonList);*/
                TGroupPerson tGroupPerson= new TGroupPerson();
                tGroupPerson.setDept(tGroupUnit.getName());
                tGroupPersonService.update(tGroupPerson,personQueryWrapper);

                //更新复查人员 dept 字段
                QueryWrapper<TReviewPerson> tReviewPersonQueryWrapper = new QueryWrapper<>();
                if(tGroupUnit.getName()!=null && StringUtils.isNotBlank(tGroupUnit.getName())){
                    tReviewPersonQueryWrapper.eq("dept",tGroupUnitOld.getName());
                }else{
                    tReviewPersonQueryWrapper.eq("unit_id",tGroupUnit.getId());
                }
                tReviewPersonQueryWrapper.eq("physical_type",tGroupUnit.getPhysicalType());
                tReviewPersonQueryWrapper.eq("del_flag",0);
                TReviewPerson tReviewPerson= new TReviewPerson();
                tReviewPerson.setDept(tGroupUnit.getName());
                itReviewPersonService.update(tReviewPerson,tReviewPersonQueryWrapper);

                //更新检测信息
                QueryWrapper<TTestRecord> tTestRecordQueryWrapper = new QueryWrapper<>();
                tTestRecordQueryWrapper.eq("unit_id",tGroupUnit.getId());
                tTestRecordService.remove(tTestRecordQueryWrapper);//清除旧数据
                for(TTestRecord tTestRecord : tGroupUnit.getTestRecordData()){
                    tTestRecord.setCreateTime(new Date());
                    tTestRecord.setCreateId(securityUtil.getCurrUser().getId());
                }
                tTestRecordService.saveBatch(tGroupUnit.getTestRecordData());//添加
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
    @ApiOperation("根据主键来删除团检单位数据")
    @SystemLog(description = "根据主键来删除团检单位数据", type = LogType.OPERATION)
    @PostMapping("deleteTGroupUnit")
    public Result<Object> deleteTGroupUnit(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TGroupOrder> tGroupOrderQueryWrapper = new QueryWrapper<>();
            tGroupOrderQueryWrapper.eq("del_flag",0);
            tGroupOrderQueryWrapper.in("group_unit_id",Arrays.asList(ids));
            Integer countOrder = tGroupOrderService.count(tGroupOrderQueryWrapper);
            if(countOrder != null && countOrder > 0){
                return ResultUtil.error("删除失败,当前单位已存在体检订单!");
            }
            QueryWrapper<TGroupPerson> tGroupPersonQueryWrapper = new QueryWrapper<>();
            tGroupPersonQueryWrapper.eq("del_flag",0);
            tGroupPersonQueryWrapper.in("unit_id",Arrays.asList(ids));
            Integer count = tGroupPersonService.count(tGroupPersonQueryWrapper);
            if(count != null && count > 0){
                return ResultUtil.error("删除失败,当前单位已存在体检人员!");
            }
            boolean res = tGroupUnitService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取团检单位数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取团检单位数据")
    @GetMapping("getTGroupUnit")
    public Result<Object> getTGroupUnit(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TGroupUnit res = tGroupUnitService.getById(id);
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
    @SystemLog(description = "分页查询团检单位数据", type = LogType.OPERATION)
    @ApiOperation("分页查询团检单位数据")
    @GetMapping("queryTGroupUnitList")
    public Result<Object> queryTGroupUnitList(TGroupUnit tGroupUnit, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TGroupUnit> result = tGroupUnitService.queryTGroupUnitListByPage(tGroupUnit, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }



    @SystemLog(description = "查询全部团检单位数据", type = LogType.OPERATION)
    @ApiOperation("查询全部团检单位数据")
    @GetMapping("queryAllTGroupUnitList")
    public Result<Object> queryAllTGroupUnitList(TGroupUnit tGroupUnit) {
        try {
            QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("physical_type", tGroupUnit.getPhysicalType());
            List<TGroupUnit> list = tGroupUnitService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     *  查询全部团检单位数据（根据名字去重）
     * @return
     */
    @SystemLog(description = "查询全部团检单位数据（根据名字去重）", type = LogType.OPERATION)
    @ApiOperation("查询全部团检单位数据（根据名字去重）")
    @GetMapping("queryAllTGroupUnitListGroupName")
    public Result<Object> queryAllTGroupUnitListGroupName() {
        try {
            QueryWrapper<TGroupUnit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.groupBy("name");
            List<TGroupUnit> list = tGroupUnitService.list(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response   请求参数
     * @param tGroupUnit 查询参数
     * @return
     */
    @SystemLog(description = "导出团检单位数据", type = LogType.OPERATION)
    @ApiOperation("导出团检单位数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TGroupUnit tGroupUnit) {
        try {
            tGroupUnitService.download(tGroupUnit, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
