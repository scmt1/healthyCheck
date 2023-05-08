package com.scmt.healthy.miniapp.controller;

import com.scmt.base.entity.Dict;
import com.scmt.base.entity.DictData;
import com.scmt.base.service.DictDataService;
import com.scmt.base.service.DictService;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "字典数据管理接口")
@RequestMapping("/miniapp/dictData")
@Transactional
public class MiniDictDataController {

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @RequestMapping(value = "/getByType/{type}", method = RequestMethod.GET)
    @ApiOperation(value = "通过类型获取")
    @SystemLog(description = "通过类型获取", type = LogType.OPERATION)
    public Result<Object> getByType(@PathVariable String type) {

        Dict dict = dictService.findByType(type);
        if (dict == null) {
            return ResultUtil.error("字典类型 " + type + " 不存在");
        }
        List<DictData> list = dictDataService.findByDictId(dict.getId());
        return ResultUtil.data(list);
    }
}
