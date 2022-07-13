package com.scmt.base.controller.manage;

import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.entity.Log;
import com.scmt.core.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(description = "日志管理接口")
@RequestMapping("/scmt/log")
@Transactional
public class LogController {

    @Value("${scmt.logRecord.es:false}")
    private Boolean esRecord;

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/getAllByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取全部")
    public Result<Object> getAllByPage(@RequestParam(required = false) Integer type,
                                       @RequestParam String key,
                                       SearchVo searchVo,
                                       PageVo pageVo) {

        if (esRecord) {
            // 支持排序的字段
            if (!"costTime".equals(pageVo.getSort())) {
                pageVo.setSort("timeMillis");
            }
            return ResultUtil.data(null);
        } else {
            Page<Log> log = logService.findByConfition(type, key, searchVo, PageUtil.initPage(pageVo));
            return ResultUtil.data(log);
        }
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量删除")
    public Result<Object> delByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            if (esRecord) {
            } else {
                logService.delete(id);
            }
        }
        return ResultUtil.success("删除成功");
    }

    @RequestMapping(value = "/delAll", method = RequestMethod.POST)
    @ApiOperation(value = "全部删除")
    public Result<Object> delAll() {

        if (esRecord) {
        } else {
            logService.deleteAll();
        }
        return ResultUtil.success("删除成功");
    }
}
