package com.scmt.healthy.controller;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.ITDocumentFileService;
import com.scmt.healthy.service.ITGroupOrderService;
import com.scmt.healthy.service.ITGroupUnitService;
import com.scmt.healthy.service.ITOrderFlowService;

import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.UploadFileUtils;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 **/
@RestController
@Api(tags = " 订单审核流程数据接口")
@RequestMapping("/scmt/tOrderFlow")
public class TOrderFlowController {
    @Autowired
    private ITOrderFlowService tOrderFlowService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITGroupOrderService itGroupOrderService;
    @Autowired
    private ITDocumentFileService itDocumentFileService;
    @Autowired
    private ITGroupUnitService tGroupUnitService;

    /**
     * 功能描述：新增订单审核流程数据
     *
     * @param tOrderFlow 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("新增订单审核流程数据")
    @PostMapping("addTOrderFlow")
    public Result<Object> addTOrderFlow(@RequestBody TOrderFlow tOrderFlow) {
        try {
            TGroupOrder byId = itGroupOrderService.getById(tOrderFlow.getGroupOrderId());
            if(byId !=  null && byId.getAuditState() != 0 && byId.getAuditState() != 5) {
                return ResultUtil.error("当前订单已在审核流程中！无需重复提交");
            }
            tOrderFlow.setCreateTime(new Date());
            tOrderFlow.setCreateUserId(securityUtil.getCurrUser().getId());
            tOrderFlow.setCreateUserName(securityUtil.getCurrUser().getNickname());
            tOrderFlow.setAuditUserId(securityUtil.getCurrUser().getId());
            tOrderFlow.setAuditUserName(securityUtil.getCurrUser().getNickname());
            tOrderFlow.setAuditState(0);
            tOrderFlow.setAuditTime(new Date());
            tOrderFlow.setAuditContent("提交审核");
            boolean res = tOrderFlowService.save(tOrderFlow);
            if (res) {
                TGroupOrder tGroupOrder = new TGroupOrder();
                tGroupOrder.setId(tOrderFlow.getGroupOrderId());
                tGroupOrder.setAuditState(1);
                itGroupOrderService.updateById(tGroupOrder);
                if (tOrderFlow.getDocumentList() != null && tOrderFlow.getDocumentList().size() > 0) {
                    ArrayList<TDocumentFile> fileArrayList = new ArrayList<>();
                    for (String s : tOrderFlow.getDocumentList()) {
                        if(StringUtils.isNotBlank(s) && s.indexOf("tempFileUrl") == -1 && s.indexOf("tempfile") == -1) {
                            MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(s);
                            String fileUrl = UploadFileUtils.uploadFile(imgFile);
                            TDocumentFile tDocumentFile = new TDocumentFile();
                            tDocumentFile.setForeignKey(tOrderFlow.getGroupOrderId());
                            tDocumentFile.setName(imgFile.getOriginalFilename());
                            tDocumentFile.setUrl(fileUrl);
                            tDocumentFile.setSize(imgFile.getSize());
                            tDocumentFile.setType(imgFile.getContentType());
                            fileArrayList.add(tDocumentFile);
                        }
                    }
                    itDocumentFileService.saveBatch(fileArrayList);
                }
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
     * @param tOrderFlow 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("更新订单审核流程数据")
    @PostMapping("updateTOrderFlow")
    public Result<Object> updateTOrderFlow(@RequestBody TOrderFlow tOrderFlow) {
        if (StringUtils.isBlank(tOrderFlow.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderFlowService.updateById(tOrderFlow);
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
    @ApiOperation("根据主键来删除订单审核流程数据")
    @SystemLog(description = "根据主键来删除订单审核流程数据", type = LogType.OPERATION)
    @PostMapping("deleteTOrderFlow")
    public Result<Object> deleteTOrderFlow(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderFlowService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取订单审核流程数据")
    @GetMapping("getTOrderFlow")
    public Result<Object> getTOrderFlow(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderFlow res = tOrderFlowService.getById(id);
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
    @SystemLog(description = "分页查询订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("分页查询订单审核流程数据")
    @GetMapping("queryTOrderFlowList")
    public Result<Object> queryTOrderFlowList(TOrderFlow tOrderFlow, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TOrderFlow> result = tOrderFlowService.queryTOrderFlowListByPage(tOrderFlow, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    @SystemLog(description = "查询全部订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("查询全部订单审核流程数据")
    @GetMapping("queryAllTOrderFlowList")
    public Result<Object> queryAllTOrderFlowList(TOrderFlow tOrderFlow) {
        try {
            List<TOrderFlow> result = tOrderFlowService.queryAllTOrderFlowList(tOrderFlow);
            QueryWrapper<TDocumentFile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("foreign_key", tOrderFlow.getGroupOrderId());
            List<TDocumentFile> list = itDocumentFileService.list(queryWrapper);
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("data", result);
            stringObjectHashMap.put("uploadList", list);
            return ResultUtil.data(stringObjectHashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    @SystemLog(description = "查询订单审核数据", type = LogType.OPERATION)
    @ApiOperation("查询订单审核数据")
    @GetMapping("getTOrderAndFlowData")
    public Result<Object> getTOrderAndFlowData(TGroupOrder tGroupOrder) {
        try {
            //审批流程数据查询
            TOrderFlow tOrderFlow = new TOrderFlow();
            tOrderFlow.setGroupOrderId(tGroupOrder.getId());
            List<TOrderFlow> result = tOrderFlowService.queryAllTOrderFlowList(tOrderFlow);
            QueryWrapper<TDocumentFile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("foreign_key", tGroupOrder.getId());
            List<TDocumentFile> list = itDocumentFileService.list(queryWrapper);
            //团检订单数据查询
            QueryWrapper<TGroupOrder> tGroupOrderQueryWrapper = new QueryWrapper<>();
            tGroupOrderQueryWrapper.eq("id",tGroupOrder.getId());
            tGroupOrderQueryWrapper.eq("del_flag",0);
            TGroupOrder tGroupOrderNew = itGroupOrderService.getOne(tGroupOrderQueryWrapper);
            //团检单位数据查询
            TGroupUnit tGroupUnit = tGroupUnitService.getById(tGroupOrderNew.getGroupUnitId());
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("flowData", result);
            stringObjectHashMap.put("uploadList", list);
            stringObjectHashMap.put("tGroupOrder", tGroupOrderNew);
            stringObjectHashMap.put("tGroupUnit", tGroupUnit);
            return ResultUtil.data(stringObjectHashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    /**
     * 功能描述：导出数据
     *
     * @param response   请求参数
     * @param tOrderFlow 查询参数
     * @return
     */
    @SystemLog(description = "导出订单审核流程数据", type = LogType.OPERATION)
    @ApiOperation("导出订单审核流程数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TOrderFlow tOrderFlow) {
        try {
            tOrderFlowService.download(tOrderFlow, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
