package com.scmt.healthy.miniapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.entity.TComboItem;
import com.scmt.healthy.service.ITComboItemService;
import com.scmt.healthy.service.ITComboService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *@author
 **/
@RestController
@Api(tags =" 小程序套餐相关数据接口")
@RequestMapping("/miniapp/tCombo")
public class MiniComboController {

    @Autowired
    private ITComboService tComboService;

    @Autowired
    private ITComboItemService tComboItemService;

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取体检套餐数据")
    @GetMapping("getTComboPriceById")
    public Result<Object> getTComboById(@RequestParam(value = "id" ,required = false) String id, @RequestParam(value = "isMiniApps",required = false) Boolean isMiniApps) {
        System.out.println(id);
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TCombo res = tComboService.getTCombo(id);
            if(isMiniApps != null && isMiniApps){
                //判断是否为小程序端，是的话去除图片地址的/tempFileUrl前缀
                String url = res.getUrl().replaceAll("/tempFileUrl", "");
                res.setUrl(url);
            }
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
     * 功能描述：根据套餐id查询套餐项目
     *
     * @param comboId 请求参数
     * @return
     */
    @SystemLog(description = "根据套餐id查询套餐项目", type = LogType.OPERATION)
    @ApiOperation("根据套餐id查询套餐项目")
    @GetMapping("getComboItemByComboId")
    public Result<Object> getComboItemByComboId(String comboId) {
        try {
            if (StringUtils.isBlank(comboId)) {
                return ResultUtil.data(new ArrayList<>());
            }
            String[] split = comboId.split(",");
            QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("combo_id", Arrays.asList(split));
            queryWrapper.groupBy("portfolio_project_id");
            queryWrapper.orderByAsc("name");
            List<TComboItem> list = tComboItemService.listByComboIds(queryWrapper);
            return ResultUtil.data(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询失败！");
        }
    }

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("分页查询体检套餐数据")
    @GetMapping("queryTComboList")
    public Result<Object> queryTComboList(TCombo tCombo, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TCombo> result = tComboService.queryTComboListByPage(tCombo, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

}
