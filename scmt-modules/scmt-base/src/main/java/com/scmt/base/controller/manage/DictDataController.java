package com.scmt.base.controller.manage;

import com.scmt.base.entity.Dict;
import com.scmt.base.entity.DictData;
import com.scmt.base.service.DictDataService;
import com.scmt.base.service.DictService;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "字典数据管理接口")
@RequestMapping("/scmt/dictData")
@Transactional
public class DictDataController {

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    @SystemLog(description = "多条件分页获取用户列表", type = LogType.OPERATION)
    public Result<Page<DictData>> getByCondition(DictData dictData, PageVo pageVo) {
        Page<DictData> page = dictDataService.findByCondition(dictData, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<DictData>>().setData(page);
    }

    @RequestMapping(value = "/getAllByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典列表")
    @SystemLog(description = "获取字典列表", type = LogType.OPERATION)
    public Result<Object> getAllByCondition(String dictId) {
        List<DictData> list = dictDataService.findByDictId(dictId);
        return ResultUtil.data(list);
    }


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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    @SystemLog(description = "添加", type = LogType.OPERATION)
    public Result<Object> add(DictData dictData) {

        Dict dict = dictService.get(dictData.getDictId());
        if (dict == null) {
            return ResultUtil.error("字典类型id不存在");
        }
        dictDataService.save(dictData);
        // 删除缓存
        redisTemplate.delete("dictData::" + dict.getType());
        return ResultUtil.success("添加成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    @SystemLog(description = "编辑", type = LogType.OPERATION)
    public Result<Object> edit(DictData dictData) {

        dictDataService.update(dictData);
        // 删除缓存
        Dict dict = dictService.get(dictData.getDictId());
        redisTemplate.delete("dictData::" + dict.getType());
        return ResultUtil.success("编辑成功");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    @SystemLog(description = "批量通过id删除", type = LogType.OPERATION)
    public Result<Object> delByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            DictData dictData = dictDataService.get(id);
            if (dictData == null) {
                return ResultUtil.error("数据不存在");
            }
            Dict dict = dictService.get(dictData.getDictId());
            dictDataService.delete(id);
            // 删除缓存
            redisTemplate.delete("dictData::" + dict.getType());
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
