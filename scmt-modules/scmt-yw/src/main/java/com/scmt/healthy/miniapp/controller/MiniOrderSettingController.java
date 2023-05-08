package com.scmt.healthy.miniapp.controller;

import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.entity.TOrderSetting;
import com.scmt.healthy.service.ITOrderSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *@author
 **/
@RestController
@Api(tags =" 小程序预约设置数据接口")
@RequestMapping("/miniapp/orderSetting")
public class MiniOrderSettingController {

    @Autowired
    private ITOrderSettingService tOrderSettingService;

    @ApiOperation("根据体检机构id和当前时间获取对应时间区间的可预约设置信息")
    @RequestMapping(value = "getOrderSettingInfoByOrderDate",method = RequestMethod.GET)
    public Result<Object> getOrderSettingByOrg(TOrderSetting tOrderSetting){
        if(tOrderSetting == null){
            return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
        }
        try {
            List<Map<String,Object>> list = tOrderSettingService.findOrderSettingInfoByOrgId(tOrderSetting);
            if(list != null && list.size() > 0){
                return ResultUtil.data(list,MessageConstant.QUERY_DATA_SUCCESS);
            }
            return ResultUtil.data(list,MessageConstant.QUERY_DATA_FAIL);
        }catch (Exception e){
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
        }
    }
}
