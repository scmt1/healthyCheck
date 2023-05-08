package com.scmt.healthy.miniapp.controller;

import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.service.ITOrderRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *@author
 **/
@RestController
@Api(tags =" 小程序预约记录数据接口")
@RequestMapping("/miniapp/tOrderRecord")
public class MiniOrderRecordController {

    @Autowired
    private ITOrderRecordService tOrderRecordService;

    @ApiOperation("根据手机号来查看用户的预约记录")
    @GetMapping("getOrderRecordInfoListByMobile")
    public Result<Object> queryTOrderRecordListByMobile(String mobile, String isPass){
        try {
            if(mobile  == null){
                return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
            }
            List<Map<String,Object>> result = tOrderRecordService.getOrderRecordInfoListByMobile(mobile,isPass);
            if(result != null && result.size() > 0){
                return ResultUtil.data(result, MessageConstant.QUERY_DATA_SUCCESS);
            }
            return ResultUtil.data(result,"暂无该体检预约记录!");
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
        }
    }

    @ApiOperation("根据订单id来获取对应的预约记录")
    @GetMapping("getOrderRecordInfoByOrderId")
    public Result<Object> queryTOrderRecordInfoByOrderId(@RequestParam("orderId") String orderId){
        try {
            if(orderId == null){
                return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
            }
            Map<String, Object> result = tOrderRecordService.getOrderRecordInfoByOrderId(orderId);
            if(result != null){
                return ResultUtil.data(result, MessageConstant.QUERY_DATA_SUCCESS);
            }
            return ResultUtil.data(result,"暂无该体检预约记录!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
        }
    }
}
