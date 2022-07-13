package com.scmt.healthy.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scmt.base.vo.MenuVo;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.entity.User;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.annotation.SystemLog;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = " 体检项目数据接口")
@RequestMapping("/scmt/tOrderGroupItemProject")
public class TOrderGroupItemProjectController {
    @Autowired
    private ITOrderGroupItemProjectService tOrderGroupItemProjectService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITDepartItemResultService itemResultService;
    @Autowired
    private ITGroupPersonService personService;
    @Autowired
    private IRelationProjectReferenceService referenceService;
    @Autowired
    private IRelationProjectCriticalService criticalService;
    @Autowired
    private ITReviewProjectService tReviewProjectService;
    @Autowired
    private RedisTemplateHelper redisTemplate;
    @Autowired
    private ITTemplateService tTemplateService;
    @Autowired
    private ITGroupPersonService tGroupPersonService;

    /**
     * 功能描述：新增体检项目数据
     *
     * @param tOrderGroupItemProject 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增体检项目数据", type = LogType.OPERATION)
    @ApiOperation("新增体检项目数据")
    @PostMapping("addTOrderGroupItemProject")
    public Result<Object> addTOrderGroupItemProject(@RequestBody TOrderGroupItemProject tOrderGroupItemProject) {
        try {
            boolean res = tOrderGroupItemProjectService.save(tOrderGroupItemProject);
            if (res) {
                return ResultUtil.data(res, "保存成功");
            } else {
                return ResultUtil.data(res, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param tOrderGroupItemProject 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新体检项目数据", type = LogType.OPERATION)
    @ApiOperation("更新体检项目数据")
    @PostMapping("updateTOrderGroupItemProject")
    public Result<Object> updateTOrderGroupItemProject(@RequestBody TOrderGroupItemProject tOrderGroupItemProject) {
        if (StringUtils.isBlank(tOrderGroupItemProject.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderGroupItemProjectService.updateById(tOrderGroupItemProject);
            if (res) {
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除体检项目数据")
    @SystemLog(description = "根据主键来删除体检项目数据", type = LogType.OPERATION)
    @PostMapping("deleteTOrderGroupItemProject")
    public Result<Object> deleteTOrderGroupItemProject(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tOrderGroupItemProjectService.removeByIds(Arrays.asList(ids));
            if (res) {
                return ResultUtil.data(res, "删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取体检项目数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取体检项目数据")
    @GetMapping("getTOrderGroupItemProject")
    public Result<Object> getTOrderGroupItemProject(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TOrderGroupItemProject res = tOrderGroupItemProjectService.getById(id);
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
     * 功能描述：实现分页查询
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "分页查询体检项目数据", type = LogType.OPERATION)
    @ApiOperation("分页查询体检项目数据")
    @GetMapping("queryTOrderGroupItemProjectList")
    public Result<Object> queryTOrderGroupItemProjectList(TOrderGroupItemProject tOrderGroupItemProject, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TOrderGroupItemProject> result = tOrderGroupItemProjectService.queryTOrderGroupItemProjectListByPage(tOrderGroupItemProject, searchVo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询未体检项目数据
     *
     * @param personId 人员id
     * @return 返回获取结果
     */
    @SystemLog(description = "查询未体检项目数据", type = LogType.OPERATION)
    @ApiOperation("查询未体检项目数据")
    @GetMapping("queryNoCheckTOrderGroupItemProjectList")
    public Result<Object> queryNoCheckTOrderGroupItemProjectList(@RequestParam(name = "personId") String personId, @RequestParam(name = "groupId") String groupId) {
        try {
            List<TOrderGroupItem> result = tOrderGroupItemProjectService.queryNoCheckTOrderGroupItemProjectList(personId, groupId);
            List<TReviewProject> tReviewProjects = tReviewProjectService.queryNoCheckReviewProject(personId);
            TGroupPerson tGroupPerson = tGroupPersonService.getById(personId);

            for (TReviewProject tReviewProject : tReviewProjects) {
                TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                tOrderGroupItem.setOfficeId(tReviewProject.getOfficeId());
                tOrderGroupItem.setOfficeName(tReviewProject.getOfficeName());
                tOrderGroupItem.setName(tReviewProject.getName());
                result.add(tOrderGroupItem);
            }
            if(tGroupPerson != null && ("职业体检".equals(tGroupPerson.getPhysicalType()) || "放射体检".equals(tGroupPerson.getPhysicalType())) && tGroupPerson.getIsWzCheck() != null && tGroupPerson.getIsWzCheck() == 0){
                TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                tOrderGroupItem.setOfficeId("1454369800754171904");
                tOrderGroupItem.setOfficeName("问诊科");
                tOrderGroupItem.setName("问诊信息录入");
                result.add(tOrderGroupItem);
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询弃检项目数据
     *
     * @param personId 人员id
     * @return 返回获取结果
     */
    @SystemLog(description = "查询弃检项目数据", type = LogType.OPERATION)
    @ApiOperation("查询弃检项目数据")
    @GetMapping("queryAbandonTOrderGroupItemProjectList")
    public Result<Object> queryAbandonTOrderGroupItemProjectList(@RequestParam(name = "personId") String personId, @RequestParam(name = "groupId") String groupId) {
        try {
            List<TOrderGroupItem> result = tOrderGroupItemProjectService.queryAbandonTOrderGroupItemProjectList(personId, groupId);
            List<TReviewProject> reviewData = tReviewProjectService.queryAbandonTReviewProjectList(personId, groupId);
            for(TReviewProject tReviewProject : reviewData){
                TOrderGroupItem tOrderGroupItem = new TOrderGroupItem();
                tOrderGroupItem.setOfficeId(tReviewProject.getOfficeId());
                tOrderGroupItem.setName(tReviewProject.getName());
                tOrderGroupItem.setOfficeName(tReviewProject.getOfficeName());
                tOrderGroupItem.setAbandonRenson(tReviewProject.getAbandonRenson());
                result.add(tOrderGroupItem);
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response               请求参数
     * @param tOrderGroupItemProject 查询参数
     * @return
     */
    @SystemLog(description = "导出体检项目数据", type = LogType.OPERATION)
    @ApiOperation("导出体检项目数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TOrderGroupItemProject tOrderGroupItemProject) {
        try {
            tOrderGroupItemProjectService.download(tOrderGroupItemProject, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：获取选中的基础项目
     *
     * @param itemId
     * @return
     */
    @SystemLog(description = "获取选中的基础项目", type = LogType.OPERATION)
    @ApiOperation("获取选中的基础项目")
    @GetMapping("/getSelectedBaseItemByItemId")
    public Result<Object> getSelectedBaseItemByItemId(String itemId, String personId, String portfolioId, String groupId) {
        if (StringUtils.isBlank(itemId) && StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TOrderGroupItemProject> list = new ArrayList<>();

            //复检表 关联基础项目
            if (StringUtils.isNotBlank(portfolioId) && StringUtils.isNotBlank(groupId)) {
                list = tOrderGroupItemProjectService.getOrderGroupITemProjectByReview(portfolioId, groupId, securityUtil.getDeparmentIds());
            } else {
                QueryWrapper<TOrderGroupItemProject> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("t_order_group_item_id", itemId);
                queryWrapper.groupBy("base_project_id");
                queryWrapper.orderByAsc("order_num");
                //原始数据
                list = tOrderGroupItemProjectService.list(queryWrapper);
            }

            if (list.size() > 0) {
                //获取规则
                TGroupPerson byId = personService.getById(personId);
                String sex = byId.getSex();
                Integer age = byId.getAge();

                QueryWrapper<TDepartItemResult> itemResultQueryWrapper = new QueryWrapper<>();
                itemResultQueryWrapper.eq("person_id", personId);
                itemResultQueryWrapper.eq("del_flag", 0);
                itemResultQueryWrapper.isNotNull("order_group_item_project_id");
                itemResultQueryWrapper.isNotNull("order_group_item_id");
                List<TDepartItemResult> departItemResults = itemResultService.list(itemResultQueryWrapper);

                //复查项目结果
                QueryWrapper<TReviewProject> queryWrapperReviewRecord = new QueryWrapper<>();
                queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getPersonId, personId));
                queryWrapperReviewRecord.lambda().and(i -> i.eq(TReviewProject::getDelFlag, 0));
                List<TReviewProject> tReviewProjects =tReviewProjectService.list(queryWrapperReviewRecord);

                List<RelationProjectReference> referenceList = null;
                List<RelationProjectCritical> criticalList = null;

                // 读取缓存
                User u = securityUtil.getCurrUser();
                String key1 = "permission::relationProjectReference:" + u.getId();
                String v1 = redisTemplate.get(key1);
                if (StrUtil.isNotBlank(v1) && !v1.equals("[]")) {
                    referenceList = new Gson().fromJson(v1, new TypeToken<List<RelationProjectReference>>() {
                    }.getType());
                } else {
                    referenceList = referenceService.list();
                    redisTemplate.set(key1, new Gson().toJson(referenceList), 15L, TimeUnit.DAYS);
                }

                String key = "permission::relationProjectCritical:" + u.getId();
                String v = redisTemplate.get(key);
                if (StrUtil.isNotBlank(v) && !v.equals("[]")) {
                    criticalList = new Gson().fromJson(v, new TypeToken<List<RelationProjectCritical>>() {
                    }.getType());
                } else {
                    criticalList = criticalService.list();
                    redisTemplate.set(key, new Gson().toJson(criticalList), 15L, TimeUnit.DAYS);
                }

                for (TOrderGroupItemProject project : list) {
                    TDepartItemResult itemResult = departItemResults.stream().filter(i -> i.getOrderGroupItemProjectId().equals(project.getId()) && itemId.equals(i.getOrderGroupItemId())).findFirst().orElse(null);
                    String baseProjectId = project.getBaseProjectId();

                    //参考值
                    RelationProjectReference reference = referenceList.stream().filter(i ->
                          i.getBaseProjectId().equals(baseProjectId) && i.getMinAge() <= age && age <= i.getMaxAge()
                                && (i.getAllowSex().equals(sex) || i.getAllowSex().equals("全部"))
                    ).findFirst().orElse(null);

                    //危机值
                    List<RelationProjectCritical> tempList = criticalList.stream().filter(i -> i.getBaseProjectId().trim().equals(baseProjectId.trim())).collect(Collectors.toList());

                    project.setCriticals(tempList);
                    project.setRelationProjectReference(reference);
                    project.setDepartItemResults(itemResult);//单项结果
                    project.setTReviewProjects(tReviewProjects);//复查项目表
                }
                return ResultUtil.data(list);
            } else {
                return ResultUtil.error("未找到对应的基础项目");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常" + e.getMessage());
        }
    }
}
