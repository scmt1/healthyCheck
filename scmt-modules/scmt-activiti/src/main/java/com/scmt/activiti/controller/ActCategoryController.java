package com.scmt.activiti.controller;

import com.scmt.activiti.entity.ActCategory;
import com.scmt.activiti.entity.ActProcess;
import com.scmt.activiti.service.ActCategoryService;
import com.scmt.activiti.service.ActProcessService;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.CommonUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "流程分类管理接口")
@RequestMapping("/scmt/actCategory")
@CacheConfig(cacheNames = "actCategory")
@Transactional
public class ActCategoryController {

    @Autowired
    private ActCategoryService actCategoryService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @RequestMapping(value = "/getByParentId/{parentId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    @Cacheable(key = "#parentId")
    public Result<List<ActCategory>> getByParentId(@PathVariable String parentId) {

        List<ActCategory> list = actCategoryService.findByParentIdOrderBySortOrder(parentId);
        setInfo(list);
        return new ResultUtil<List<ActCategory>>().setData(list);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Object> add(ActCategory actCategory) {

        actCategoryService.save(actCategory);
        // 如果不是添加的一级 判断设置上级为父节点标识
        if (!CommonConstant.PARENT_ID.equals(actCategory.getParentId())) {
            ActCategory parent = actCategoryService.get(actCategory.getParentId());
            if (parent.getIsParent() == null || !parent.getIsParent()) {
                parent.setIsParent(true);
                actCategoryService.update(parent);
            }
        }
        // 更新缓存
        redisTemplate.deleteByPattern("actCategory::*");
        return ResultUtil.success("添加成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    @SystemLog(description = "编辑", type = LogType.OPERATION)
    public Result<Object> edit(ActCategory actCategory) {

        ActCategory old = actCategoryService.get(actCategory.getId());
        actCategoryService.update(actCategory);
        // 若修改了分类名称
        if (!old.getTitle().equals(actCategory.getTitle())) {
            actProcessService.updateCategoryTitle(actCategory.getId(), actCategory.getTitle());
        }
        // 手动删除所有分类缓存
        redisTemplate.deleteByPattern("actCategory:" + "*");
        return ResultUtil.success("编辑成功");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result<Object> delByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            deleteRecursion(id, ids);
        }
        // 手动删除所有缓存
        redisTemplate.deleteByPattern("actCategory:" + "*");
        return ResultUtil.success("批量通过id删除数据成功");
    }

    public void deleteRecursion(String id, String[] ids) {

        List<ActProcess> list = actProcessService.findByCategoryId(id);
        if (list != null && list.size() > 0) {
            throw new ScmtException("删除失败，包含正被流程使用关联的分类");
        }
        // 获得其父节点
        ActCategory cat = actCategoryService.get(id);
        ActCategory parent = null;
        if (cat != null && StrUtil.isNotBlank(cat.getParentId())) {
            parent = actCategoryService.get(cat.getParentId());
        }
        actCategoryService.delete(id);
        // 判断父节点是否还有子节点
        if (parent != null) {
            List<ActCategory> childrenDeps = actCategoryService.findByParentIdOrderBySortOrder(parent.getId());
            if (childrenDeps == null || childrenDeps.isEmpty()) {
                parent.setIsParent(false);
                actCategoryService.update(parent);
            }
        }
        // 递归删除
        List<ActCategory> categories = actCategoryService.findByParentIdOrderBySortOrder(id);
        for (ActCategory c : categories) {
            if (!CommonUtil.judgeIds(c.getId(), ids)) {
                deleteRecursion(c.getId(), ids);
            }
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "名称模糊搜索")
    public Result<List<ActCategory>> searchByTitle(@RequestParam String title) {

        List<ActCategory> list = actCategoryService.findByTitleLikeOrderBySortOrder("%" + title + "%");
        setInfo(list);
        return new ResultUtil<List<ActCategory>>().setData(list);
    }

    public void setInfo(List<ActCategory> list) {

        // lambda表达式
        list.forEach(item -> {
            if (!CommonConstant.PARENT_ID.equals(item.getParentId())) {
                ActCategory parent = actCategoryService.get(item.getParentId());
                item.setParentTitle(parent.getTitle());
            } else {
                item.setParentTitle("一级节点");
            }
        });
    }
}
