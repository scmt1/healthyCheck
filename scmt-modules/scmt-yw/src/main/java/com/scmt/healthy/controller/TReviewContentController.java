package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.entity.TGroupOrder;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TOrderFlow;
import com.scmt.healthy.entity.TReviewContent;
import com.scmt.healthy.service.ITGroupOrderService;
import com.scmt.healthy.service.TReviewContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @author
 **/
@RestController
@Api(tags = " 术语数据接口")
@RequestMapping("/scmt/tReviewContent")
public class TReviewContentController {
    @Autowired
    private TReviewContentService tReviewContentService;
    @Autowired
    private ITGroupOrderService tGroupOrderService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：合同评审
     *
     * @param tReviewContent 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "合同评审", type = LogType.OPERATION)
    @ApiOperation("合同评审")
    @PostMapping("approve")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> approve(@RequestBody TReviewContent tReviewContent) {
        try {
            boolean res = false;
            if (StringUtils.isBlank(tReviewContent.getId()) || tReviewContent.getId().trim().length()==0) {
                tReviewContent.setDelFlag(0);
                tReviewContent.setCreateId(securityUtil.getCurrUser().getId());
                tReviewContent.setCreateTime(new Date());
                res = tReviewContentService.save(tReviewContent);
            }else{
                tReviewContent.setUpdateId(securityUtil.getCurrUser().getId());
                tReviewContent.setUpdateTime(new Date());
                res = tReviewContentService.updateById(tReviewContent);
                /*if(res && tReviewContent!=null && tReviewContent.getAuditLevel()!=null && StringUtils.isNotBlank(tReviewContent.getOrderId())){
                    //更新订单状态
//                    TGroupOrder tGroupOrder = tGroupOrderService.getById(tReviewContent.getOrderId());
                    TGroupOrder tGroupOrder = new TGroupOrder();
                    tGroupOrder.setId(tReviewContent.getOrderId());
                    tGroupOrder.setAuditState(tReviewContent.getAuditLevel());
                    res = tGroupOrderService.updateById(tGroupOrder);
                }*/
            }
            if (res) {
                return ResultUtil.data(res, "保存成功");
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据订单id来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据订单id来数据", type = LogType.OPERATION)
    @ApiOperation("根据订单id来获取数据")
    @GetMapping("getTReviewContent")
    public Result<Object> getTReviewContent(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TReviewContent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag",0);
            queryWrapper.eq("order_id",id);
            TReviewContent res = tReviewContentService.getOne(queryWrapper);
            if (res != null) {
                return ResultUtil.data(res, "查询成功");
            } else {
                return ResultUtil.data(res, "查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
