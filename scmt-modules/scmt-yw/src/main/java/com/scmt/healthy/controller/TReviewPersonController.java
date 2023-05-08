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
import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author
 **/
@RestController
@Api(tags = "复查人员数据接口")
@RequestMapping("/scmt/tReviewPerson")
public class TReviewPersonController {
    @Autowired
    private ITReviewPersonService itReviewPersonService;

    @Autowired
    private ITGroupPersonService tGroupPersonService;

    @Autowired
    private ITGroupOrderService tGroupOrderService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询复查人员数据", type = LogType.OPERATION)
    @ApiOperation("分页查询复查人员数据")
    @GetMapping("queryTReviewPersonList")
    public Result<Object> queryTReviewPersonList(TReviewPerson tReviewPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            //科室id
            List<String> officeId = securityUtil.getDeparmentIds();
            IPage<TReviewPerson> result = itReviewPersonService.queryTReviewPersonListByPage(tReviewPerson, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：分页查询复查人员信息及检查项目
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询复查人员信息及检查项目", type = LogType.OPERATION)
    @ApiOperation("分页查询复查人员信息及检查项目")
    @PostMapping("getReviewProjectPerson")
    public Result<Object> getReviewProjectPerson(TReviewPerson tGroupPerson, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TReviewPerson> result = itReviewPersonService.getReviewProjectPerson(tGroupPerson, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
}
