package com.scmt.healthy.miniapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCheckOrg;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.service.ITCheckOrgService;
import com.scmt.healthy.service.ITComboService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *@author
 **/
@RestController
@Api(tags =" 小程序体检机构相关数据接口")
@RequestMapping("/miniapp/tCheckOrg")
public class MiniCheckOrgController {

    @Autowired
    private ITCheckOrgService tCheckOrgService;

    @Autowired
    private ITComboService tComboService;

    @ApiOperation("分页查询体检机构及对应套餐信息")
    @GetMapping("findOrgAndCombo")
    public Result<Object> getOrgAndCombo(TCheckOrg tCheckOrg, SearchVo searchVo, PageVo pageVo){
        try {
            IPage<TCheckOrg> list = tCheckOrgService.getOrgAndComboInfoByPage(tCheckOrg,searchVo,pageVo);
            //去除图片地址的/tempFileUrl前缀
            if(tCheckOrg.getIsMiniApps() != null && tCheckOrg.getIsMiniApps()){
                List<TCheckOrg> records = list.getRecords();
                for (TCheckOrg checkOrg:records) {
                    String avatar = checkOrg.getAvatar().replaceAll("/tempFileUrl", "");
                    String images = checkOrg.getImages().replaceAll("/tempFileUrl", "");
                    checkOrg.setAvatar(avatar);
                    checkOrg.setImages(images);
                    List<TCombo> combos = checkOrg.getTCombos();
                    for (TCombo combo:combos) {
                        String url = combo.getUrl().replaceAll("/tempFileUrl", "");
                        combo.setUrl(url);
                    }
                }
            }
            return ResultUtil.data(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
        }
    }

    @ApiOperation("根据机构id查询体检机构及对应套餐信息")
    @GetMapping("getOrgAndComboData")
    public Result<Object> getOrgAndComboData(TCheckOrg tCheckOrg){
        if(tCheckOrg.getId() == null){
            return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
        }
        try {
            List<TCombo> orgAndComboData = tComboService.getOrgAndComboData(tCheckOrg);
            //判断是否为小程序端，是的话去除图片地址的/tempFileUrl前缀
            if(tCheckOrg.getIsMiniApps() != null && tCheckOrg.getIsMiniApps()){
                for (TCombo tCombo:orgAndComboData) {
                    String url = tCombo.getUrl().replaceAll("/tempFileUrl", "");
                    tCombo.setUrl(url);
                }
            }
            return ResultUtil.data(orgAndComboData);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
        }
    }

    @ApiOperation("根据id查询体检机构及对应套餐信息")
    @GetMapping("findOrgAndComboById")
    public Result<Object> getOrgAndCombo(TCheckOrg tCheckOrg){
        if(tCheckOrg.getId() == null){
            return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
        }
        try {
            TCheckOrg list = tCheckOrgService.getOrgAndComboInfo(tCheckOrg);
            return ResultUtil.data(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
        }
    }

    /**
     * 动态模糊查询所有机构
     * @param tCheckOrg
     * @param searchVo
     * @return
     */
    @ApiOperation("查询所有的体检机构数据")
    @GetMapping("getAllCheckOrg")
    public Result<Object> getCheckOrg(TCheckOrg tCheckOrg,SearchVo searchVo){
        try {
            List<TCheckOrg> checkOrg = tCheckOrgService.getAllCheckOrg(tCheckOrg,searchVo);
            if(checkOrg != null && checkOrg.size() > 0){
                return ResultUtil.data(checkOrg, MessageConstant.QUERY_DATA_SUCCESS);
            }
            return ResultUtil.data(checkOrg,MessageConstant.QUERY_DATA_FAIL);
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
        }
    }

}
