package com.scmt.healthy.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.utils.NameUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.entity.User;
import com.scmt.core.service.UserService;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.constraints.Pattern;

import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.UploadFileUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.common.annotation.SystemLog;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.enums.LogType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 **/
@RestController
@Api(tags = " 体检套餐数据接口")
@RequestMapping("/scmt/tCombo")
public class TComboController {
    @Autowired
    private ITOrderGroupItemProjectService itOrderGroupItemProjectService;

    @Autowired
    private  ITBaseProjectService  itBaseProjectService;

    @Autowired
    private ITGroupPersonService tGroupPersonService;

    @Autowired
    private ITOrderGroupItemService itOrderGroupItemService;

    @Autowired
    private ITComboItemService tComboItemService;

    @Autowired
    private ITComboService tComboService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITComboItemService comboItemService;
    @Autowired
    private IRelationBasePortfolioService iRelationBasePortfolioService;

    @Autowired
    private ITProTypeService itProTypeService;

    @Autowired
    private UserService userService;
    /**
     * 功能描述：新增体检套餐数据
     *
     * @param form 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("新增体检套餐数据")
    @PostMapping("addTCombo")
    public Result<Object> addTCombo(@RequestBody String form) {
        try {
            if (!StringUtils.isNotBlank(form)) {
                return ResultUtil.data("参数为空，请联系管理员！");
            }
            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            TCombo tCombo = JSON.parseObject(formStr, TCombo.class);

            //套餐名称重复校验
            QueryWrapper<TCombo> tComboQueryWrapper = new QueryWrapper<>();
            tComboQueryWrapper.eq("name", tCombo.getName());
            tComboQueryWrapper.eq("del_flag", 0);
            tComboQueryWrapper.orderByDesc("create_time");
            tComboQueryWrapper.last("LIMIT 1");
            TCombo combo = tComboService.getOne(tComboQueryWrapper);
            if (combo != null) {
                return ResultUtil.error("套餐名称重复，保存失败");
            }

            //保存套餐
            tCombo.setDelFlag(0);
            tCombo.setCreateId(securityUtil.getCurrUser().getId());
            tCombo.setCreateTime(new Date());
            tCombo.setSimpleSpell(tCombo.getSimpleSpell().toUpperCase());
            if (StringUtils.isNotBlank(tCombo.getUrl())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tCombo.getUrl());
                String fileUrl = UploadFileUtils.uploadFile(imgFile);
                tCombo.setUrl(fileUrl);
            }
            boolean res = tComboService.save(tCombo);
            if (res) {
                //保存套餐项目
                List<TComboItem> comboItemList = tCombo.getComboItemList();
                QueryWrapper<RelationBasePortfolio> queryWrapper1 = new QueryWrapper<>();
                List<RelationBasePortfolio> list = iRelationBasePortfolioService.list(queryWrapper1);
                if (comboItemList.size() > 0) {
                    for (TComboItem comboItem : comboItemList) {
                        long count = list.stream().filter(i -> i.getPortfolioProjectId().equals(comboItem.getPortfolioProjectId())).count();
                        if(count == 0) {
                            return ResultUtil.error(comboItem.getName()+"未绑定基础项目，保存失败！");
                        }
                        comboItem.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        comboItem.setComboId(tCombo.getId());
                        comboItem.setProjectType(1);
                    }
                    comboItemService.saveBatch(comboItemList);
                }
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
     * @param form 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("更新体检套餐数据")
    @PostMapping("updateTCombo")
    public Result<Object> updateTCombo(@RequestBody String form) {
        try {
            if (!StringUtils.isNotBlank(form)) {
                return ResultUtil.data("参数为空，请联系管理员！");
            }

            JSONObject jsonObject = JSON.parseObject(form);
            String formStr = jsonObject.getString("form");
            TCombo tCombo = JSON.parseObject(formStr, TCombo.class);

            if (StringUtils.isBlank(tCombo.getId())) {
                return ResultUtil.error("参数为空，请联系管理员！！");
            }

            //套餐名称重复校验
            QueryWrapper<TCombo> tComboQueryWrapper = new QueryWrapper<>();
            tComboQueryWrapper.eq("name", tCombo.getName());
            tComboQueryWrapper.eq("del_flag", 0);
            tComboQueryWrapper.orderByDesc("create_time");
            tComboQueryWrapper.last("LIMIT 1");
            TCombo combo = tComboService.getOne(tComboQueryWrapper);
            if (combo != null && !tCombo.getId().equals(combo.getId())) {
                return ResultUtil.error("套餐名称重复，保存失败");
            }
            tCombo.setUpdateId(securityUtil.getCurrUser().getId());
            tCombo.setUpdateTime(new Date());
            tCombo.setSimpleSpell(tCombo.getSimpleSpell().toUpperCase());
            if (StringUtils.isNotBlank(tCombo.getUrl()) && tCombo.getUrl().indexOf("tempfile") == -1) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(tCombo.getUrl());
                String fileUrl = UploadFileUtils.uploadFile(imgFile);
                tCombo.setUrl(fileUrl);
            }
            boolean res = tComboService.updateById(tCombo);
            if (res) {
                //清除套餐项目
                QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("combo_id", tCombo.getId());
                comboItemService.remove(queryWrapper);
                if (tCombo.getComboItemList() != null && tCombo.getComboItemList().size() > 0) {
                    List<TComboItem> comboItemList = tCombo.getComboItemList();
                    //组合项目是否关联有基础项目校验
                    QueryWrapper<RelationBasePortfolio> queryWrapper1 = new QueryWrapper<>();
                    List<RelationBasePortfolio> list = iRelationBasePortfolioService.list(queryWrapper1);

                    for (TComboItem comboItem : comboItemList) {
                        long count = list.stream().filter(i -> i.getPortfolioProjectId().equals(comboItem.getPortfolioProjectId())).count();
                        if(count == 0) {
                            return ResultUtil.error(comboItem.getName()+"未绑定基础项目，保存失败！");
                        }
                        comboItem.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        comboItem.setComboId(tCombo.getId());
                        comboItem.setCreateTime(new Date());
                        comboItem.setProjectType(1);
                        comboItem.setCreateId(securityUtil.getCurrUser().getId());
                    }
                    //添加套餐项目
                    comboItemService.saveBatch(tCombo.getComboItemList());
                }
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
    @ApiOperation("根据主键来删除体检套餐数据")
    @SystemLog(description = "根据主键来删除体检套餐数据", type = LogType.OPERATION)
    @PostMapping("deleteTCombo")
    public Result<Object> deleteTCombo(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tComboService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取体检套餐数据")
    @GetMapping("getTCombo")
    public Result<Object> getTCombo(@RequestParam (value = "id" ,required = false) String id) {
        System.out.println(id);
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TCombo res = tComboService.getById(id);
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
     * 功能描述：根据人员id来获取体检套餐数据
     *
     * @param personId 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据人员id来获取体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取体检套餐数据")
    @GetMapping("getTComboByPersonId")
    public Result<Object> getTComboByPersonId(@Param("personId") String personId, @Param("hazardFactors") String hazardFactors, @Param("content") String content) {
        if (StringUtils.isBlank(personId)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TCombo res = tComboService.getTComboByPersonId(personId, hazardFactors, content);
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

    /**
     * 功能描述：实现查询
     *
     * @return 返回获取结果
     */
    @SystemLog(description = "查询体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("查询体检套餐数据")
    @GetMapping("queryTComboAppList")
    public Result<Object> queryTComboAppList(TCombo tCombo, SearchVo searchVo, PageVo pageVo) {
        try {
            QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag",0);
            queryWrapper.eq("type",tCombo.getType());
            List<TCombo> result = tComboService.list(queryWrapper);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response 请求参数
     * @param tCombo   查询参数
     * @return
     */
    @SystemLog(description = "导出体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("导出体检套餐数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TCombo tCombo) {
        try {
            tComboService.download(tCombo, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：通过分组id查询体检套餐危害因素
     *
     * @param groupId 分组id
     * @return 返回获取结果
     */
    @SystemLog(description = "通过分组id查询体检套餐危害因素", type = LogType.OPERATION)
    @ApiOperation("通过分组id查询体检套餐危害因素")
    @GetMapping("gethazardFactorsByGroupId")
    public Result<Object> gethazardFactorsByGroupId(@Param("groupId") String groupId) {
        try {
            List<TCombo> result = tComboService.gethazardFactorsByGroupId(groupId);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：通过分组id查询体检套餐危害因素
     *
     * @param name 分组id
     * @param id   分组id
     * @return 返回获取结果
     */
    @SystemLog(description = "通过分组id查询体检套餐危害因素", type = LogType.OPERATION)
    @ApiOperation("通过分组id查询体检套餐危害因素")
    @GetMapping("getTcomboByName")
    public Result<Object> getTcomboByName(String name, String id) {
        try {
            QueryWrapper<TCombo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", name);
            queryWrapper.eq("del_flag", 0);
            TCombo one = tComboService.getOne(queryWrapper);
            if (one != null && !one.getId().equals(id)) {
                return ResultUtil.data(true);
            } else {
                return ResultUtil.data(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }
    /**
     * 功能描述：根据危害因素更新排序
     *
     * @return 返回获取结果
     */
    @ApiOperation("根据危害因素更新排序")
    @GetMapping("updateOrderNum")
    public Result<Object> updateOrderNum() {
        try {
            QueryWrapper<TProType > queryWrapper = new QueryWrapper<>();

            queryWrapper.eq("del_flag", 0);
            queryWrapper.orderByAsc("type_code");
            List<TProType> proTypes = itProTypeService.list(queryWrapper);
            for (int i = 0; i < proTypes.size() ; i++) {

                QueryWrapper<TCombo> queryWrapperCombo = new QueryWrapper<>();
                queryWrapperCombo.eq("hazard_factors", proTypes.get(i).getTypeCode());
                queryWrapperCombo.eq("del_flag", 0);
                TCombo combo = new TCombo();
                //combo.setUpdateId(securityUtil.getCurrUser().getId());
                combo.setUpdateTime(new Date());
                combo.setOrderNum(i);
                tComboService.update(combo, queryWrapperCombo);
            }

            return ResultUtil.data(true);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：查询用户的体检报告(人员信息、检查项目)
     *
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    @SystemLog(description = "查询用户的体检报告(人员信息、检查项目)", type = LogType.OPERATION)
    @ApiOperation("查询套餐列表(套餐信息、套餐项目)")
    @GetMapping("queryTComboAndItemList")
    public Result<Object> queryTComboAndItemList(TCombo tCombo, PageVo pageVo) {
        try {
            IPage<TCombo> result = tComboService.queryTComboAndItemList(tCombo, pageVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }


    /**
     * 功能描述：根据用户的体检报告id查询对应的体检项目
     */
    @SystemLog(description = "根据用户的体检报告id查询对应的体检项目", type = LogType.OPERATION)
    @ApiOperation("根据用户的体检报告id查询对应的体检项目")
    @GetMapping("getItemById")
    public Result<Object> getItemById(@RequestParam (value = "id" ,required = false) String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            List<TCombo> res = tComboService.getItemById(id);
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
     * 功能描述：根据套餐id来获取数据
     *
     * @param id 主键
     * @return 返回获取结果
     */
    @SystemLog(description = "根据主键来获取体检套餐数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取体检套餐数据")
    @GetMapping("getTComboById")
    public Result<Object> getTComboById(@RequestParam (value = "id" ,required = false) String[] id) {
        System.out.println(id);
        /*if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }*/
        try {
            List<TCombo> res = tComboService.getTComboById(id);
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

    @ApiOperation("通过app端保存或更新健康体检人员信息和体检项目信息")
    @PostMapping("/saveOrUpdatePersonInfoByApp")
    @Transactional(rollbackOn = Exception.class)
    public Result<Object> saveOrUpdatePersonInfoByApp(@RequestBody TGroupPerson tGroupPerson) {
        //获取当前用户信息
       //userService.findByUsername(tGroupPerson.getMobile());
        User byUsername = userService.findByUsername(tGroupPerson.getUsername());
        @Pattern(regexp = NameUtil.regUsername, message = "登录账号不能包含特殊字符且长度不能>16") String username = byUsername.getUsername();
        List<TOrderGroupItem> tOrderGroupItems = null;
        if (StringUtils.isBlank(tGroupPerson.getId())) {
            tGroupPerson.setIsWzCheck(1);//默认设置问诊科已检
            tGroupPerson.setIsPass(1);
            tGroupPerson.setDelFlag(0);
            tGroupPerson.setCreateTime(new Date());
           //tGroupPerson.setCreateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setCreateId(username);
            tGroupPerson.setTestNum(generatorNum(tGroupPerson.getPhysicalType()));
        } else {
            tGroupPerson.setUpdateTime(new Date());
           // tGroupPerson.setUpdateId(securityUtil.getCurrUser().getId());
            tGroupPerson.setUpdateId(username);
            QueryWrapper<TOrderGroupItem> groupItemQueryWrapper = new QueryWrapper<>();
            groupItemQueryWrapper.eq("group_order_id", tGroupPerson.getOrderId());
            groupItemQueryWrapper.eq("group_id", tGroupPerson.getGroupId());
            tOrderGroupItems = itOrderGroupItemService.list(groupItemQueryWrapper);
            //返回每个list的第一个值，
            List<String> itemIds = itOrderGroupItemService.listObjs(groupItemQueryWrapper, new Function<Object, String>() {
                @Override
                public String apply(Object o) {
                    return o.toString();
                }
            });

            //如果当前的分组id和原分组id不一样，则可以删除，否则不能删除
//            if (StringUtils.isNotBlank(tGroupPerson.getGroupId()) && !tGroupPerson.getGroupId().equals(tGroupPerson.getOldGroupId())) {
//                if (itemIds != null && itemIds.size() > 0) {
//                    //删除分组项目子项目
//                    QueryWrapper<TOrderGroupItemProject> groupItemProjectQueryWrapper = new QueryWrapper<>();
//                    groupItemProjectQueryWrapper.in("t_order_group_item_id", itemIds);
//                    itOrderGroupItemProjectService.remove(groupItemProjectQueryWrapper);
//                }
//                //删除分组项目
//                itOrderGroupItemService.remove(groupItemQueryWrapper);
//            }
        }
        List<String> groupItemProjetIds = new ArrayList<>();
        //拿出所有组合项目id
        if(tOrderGroupItems!=null && tOrderGroupItems.size() > 0){
            groupItemProjetIds = tOrderGroupItems.stream().map(TOrderGroupItem:: getPortfolioProjectId).collect(Collectors.toList());
        }
        tGroupPerson.setAvatar(null);
        if(StringUtils.isBlank(tGroupPerson.getOrderId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
            tGroupPerson.setOrderId(groupId);
        }
        if(StringUtils.isBlank(tGroupPerson.getGroupId()) ){
            String groupId = UUID.randomUUID().toString().replaceAll("-", "");
            tGroupPerson.setGroupId(groupId);
        }
        tGroupPersonService.saveOrUpdate(tGroupPerson);
        //添加项目
        // List<TOrderGroupItem> projectData = tGroupPerson.getProjectData();
        Set<TOrderGroupItem> projectData2= (tGroupPerson.getProjectData2());
        for (TOrderGroupItem projectDatum : projectData2) {
            System.out.println(projectDatum);
        }
        //所有组合项id
        List<String> finalGroupItemProjetIds = groupItemProjetIds;
        projectData2.forEach(i -> {
            if(!finalGroupItemProjetIds.contains(i.getPortfolioProjectId())){
                i.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                i.setCreateTime(new Date());
               // i.setCreateId(securityUtil.getCurrUser().getId());
                i.setCreateId(username);
                i.setDelFlag(0);
                i.setGroupId(tGroupPerson.getGroupId());
                i.setGroupOrderId(tGroupPerson.getOrderId());
                boolean save1 = itOrderGroupItemService.save(i);
                if (save1) {
                    //保存分组项目的子项目
                    ArrayList<String> list = iRelationBasePortfolioService.queryBaseProjectIdList(i.getPortfolioProjectId());
                    if (list != null && list.size() > 0) {
                        List<TBaseProject> tBaseProjects = itBaseProjectService.listByIds(list);
                        ArrayList<TOrderGroupItemProject> projectArrayList = new ArrayList<>();
                        for (TBaseProject tBaseProject : tBaseProjects) {
                            TOrderGroupItemProject tOrderGroupItemProject = new TOrderGroupItemProject();
                            tOrderGroupItemProject.setTOrderGroupItemId(i.getId());
                            tOrderGroupItemProject.setCode(tBaseProject.getCode());
                            tOrderGroupItemProject.setName(tBaseProject.getName());
                            tOrderGroupItemProject.setShortName(tBaseProject.getShortName());
                            tOrderGroupItemProject.setOrderNum(tBaseProject.getOrderNum());
                            tOrderGroupItemProject.setOfficeId(tBaseProject.getOfficeId());
                            tOrderGroupItemProject.setUnitCode(tBaseProject.getUnitCode());
                            tOrderGroupItemProject.setUnitName(tBaseProject.getUnitName());
                            tOrderGroupItemProject.setDefaultValue(tBaseProject.getDefaultValue());
                            tOrderGroupItemProject.setResultType(tBaseProject.getResultType());
                            tOrderGroupItemProject.setInConclusion(tBaseProject.getInConclusion());
                            tOrderGroupItemProject.setInReport(tBaseProject.getInReport());
                            tOrderGroupItemProject.setRelationCode(tBaseProject.getRelationCode());
                            tOrderGroupItemProject.setGroupOrderId(i.getGroupOrderId());
                            tOrderGroupItemProject.setBaseProjectId(tBaseProject.getId());
                            projectArrayList.add(tOrderGroupItemProject);
                        }
                        itOrderGroupItemProjectService.saveBatch(projectArrayList);
                    }
                }
            }
        });
        return ResultUtil.data("保存成功");
    }

    public String generatorNum(String type) {
        QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = format.format(new Date());
        queryWrapper.apply(StringUtils.isNotBlank(currentDay), "Date(create_time)=STR_TO_Date('" + currentDay + "','%Y-%m-%d')");
        queryWrapper.eq("physical_type", type);
        queryWrapper.orderByDesc("create_time").orderByDesc("test_num");
        queryWrapper.last("limit 1");
        queryWrapper.select("id,test_num");
        TGroupPerson one = tGroupPersonService.getOne(queryWrapper);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        String testNum = "";
        if ("职业体检".equals(type)) {
            testNum = "1";
        } else if ("健康体检".equals(type)) {
            testNum = "2";
        } else if ("从业体检".equals(type)) {
            testNum = "3";
        } else if ("放射体检".equals(type)) {
            testNum = "4";
        } else {
            testNum = "5";
        }
        if (one == null) {
            testNum += df.format(new Date());
            testNum += "0001";
        } else {
            String substring = one.getTestNum().substring(one.getTestNum().length() - 4);
            int i = Integer.valueOf(substring);
            i += 1;
            String code = String.valueOf(i);
            if (code.length() == 1) {
                code = "000" + code;
            } else if (code.length() == 2) {
                code = "00" + code;
            } else if (code.length() == 3) {
                code = "0" + code;
            }
            testNum += df.format(new Date());
            testNum += code;
        }
        return testNum;
    }

}
