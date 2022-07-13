package com.scmt.healthy.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TProType;
import com.scmt.healthy.service.ITProTypeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author
 **/
@RestController
@Api(tags = "职业类型数据接口")
@RequestMapping("/scmt/tProType")
public class TProTypeController {
    @Autowired
    private ITProTypeService tProTypeService;
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 功能描述：新增职业类型数据
     *
     * @param tProType 实体
     * @return 返回新增结果
     */
    @SystemLog(description = "新增职业类型数据", type = LogType.OPERATION)
    @ApiOperation("新增职业类型数据")
    @PostMapping("addTProType")
    public Result<Object> addTProType(@RequestBody TProType tProType) {
        try {
            QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
            //判断是否第一级
            if (StringUtils.isBlank(tProType.getParentId())) {
                queryWrapper.eq("type_name", tProType.getTypeName().trim());
                queryWrapper.eq("parent_id", "0");
                queryWrapper.eq("del_flag", 0);
                TProType one = tProTypeService.getOne(queryWrapper);
                if (one != null && !one.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型名称重复！");
                }
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_code", tProType.getTypeCode().trim());
                queryWrapper.eq("parent_id", "0");
                queryWrapper.eq("del_flag", 0);
                TProType one1 = tProTypeService.getOne(queryWrapper);
                if (one1 != null && !one1.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型编码重复！");
                }
                tProType.setParentId("0");
            } else {
                //不是第一级，带有parentId
                String parentId = tProType.getParentId();
                if (StringUtils.isBlank(parentId)) {
                    return ResultUtil.error("父级ID为空，请重新操作！");
                }
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_name", tProType.getTypeName().trim());
                queryWrapper.eq("parent_id", parentId);
                queryWrapper.eq("del_flag", 0);
                TProType one = tProTypeService.getOne(queryWrapper);
                if (one != null && !one.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型名称重复！");
                }

                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_code", tProType.getTypeCode().trim());
                queryWrapper.eq("parent_id", parentId);
                queryWrapper.eq("del_flag", 0);

                TProType one1 = tProTypeService.getOne(queryWrapper);
                if (one1 != null && !one1.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型编码重复！");
                }
            }

            tProType.setDelFlag(0);
            tProType.setCreateTime(new Date());
            tProType.setCreateId(securityUtil.getCurrUser().getId());

            boolean res = tProTypeService.save(tProType);
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
     * @param tProType 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新职业类型数据", type = LogType.OPERATION)
    @ApiOperation("更新职业类型数据")
    @PostMapping("updateTProType")
    public Result<Object> updateTProType(@RequestBody TProType tProType) {
        if (StringUtils.isBlank(tProType.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
            //判断是否第一级
            if (StringUtils.isBlank(tProType.getParentId())) {
                queryWrapper.eq("type_name", tProType.getTypeName().trim());
                queryWrapper.eq("parent_id", "0");
                queryWrapper.eq("del_flag", 0);
                TProType one = tProTypeService.getOne(queryWrapper);
                if (one != null && !one.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型名称重复！");
                }
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_code", tProType.getTypeCode().trim());
                queryWrapper.eq("parent_id", "0");
                queryWrapper.eq("del_flag", 0);
                TProType one1 = tProTypeService.getOne(queryWrapper);
                if (one1 != null && !one1.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型编码重复！");
                }
                tProType.setParentId("0");
            } else {
                //不是第一级，带有parentId
                String parentId = tProType.getParentId();
                if (StringUtils.isBlank(parentId)) {
                    return ResultUtil.error("父级ID为空，请重新操作！");
                }
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_name", tProType.getTypeName().trim());
                queryWrapper.eq("parent_id", parentId);
                queryWrapper.eq("del_flag", 0);
                TProType one = tProTypeService.getOne(queryWrapper);
                if (one != null && !one.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型名称重复！");
                }

                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("type_code", tProType.getTypeCode().trim());
                queryWrapper.eq("parent_id", parentId);
                queryWrapper.eq("del_flag", 0);

                TProType one1 = tProTypeService.getOne(queryWrapper);
                if (one1 != null && !one1.getId().equals(tProType.getId())) {
                    return ResultUtil.error("类型编码重复！");
                }
            }

            tProType.setUpdateTime(new Date());
            tProType.setUpdateId(securityUtil.getCurrUser().getId());
            boolean res = tProTypeService.updateById(tProType);
            if (res) {
                return ResultUtil.data(res, "修改成功");
            } else {
                return ResultUtil.error("修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("修改异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：根据主键来删除数据
     *
     * @param ids 主键集合
     * @return 返回删除结果
     */
    @ApiOperation("根据主键来删除职业类型数据")
    @SystemLog(description = "根据主键来删除职业类型数据", type = LogType.OPERATION)
    @PostMapping("deleteTProType")
    public Result<Object> deleteTProType(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tProTypeService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取职业类型数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取职业类型数据")
    @GetMapping("getTProType")
    public Result<Object> getTProType(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TProType res = tProTypeService.getById(id);
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
    @SystemLog(description = "分页查询职业类型数据", type = LogType.OPERATION)
    @ApiOperation("分页查询职业类型数据")
    @GetMapping("queryTProTypeList")
    public Result<Object> queryTProTypeList(TProType tProType, SearchVo searchVo, PageVo pageVo) {
        try {
            List<TProType> result = tProTypeService.queryTProTypeListByPage(tProType, searchVo, pageVo);
            //如果有条件，则不需要树
            if (StringUtils.isBlank(tProType.getTypeName()) && StringUtils.isBlank(tProType.getTypeCode())) {
                //所有
                QueryWrapper<TProType> queryWrapperAll = new QueryWrapper<>();
                queryWrapperAll.eq("del_flag", 0);
                List<TProType> list = tProTypeService.list(queryWrapperAll);
                //递归找子集
                recursion(result,list);
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 递归寻找子集
     */
    public void recursion(List<TProType> proTypes, List<TProType> list) {
        for (TProType proType : proTypes) {
            //找子集
            List<TProType> children = list.stream().filter(ii->ii.getParentId().equals(proType.getId())).collect(Collectors.toList());
            if (children.size() > 0) {
                proType.setChildren(children);
                recursion(children,list);
            }else{
                proType.set_loading(true);
                proType.setChildren(null);
            }
        }
    }

    /**
     * 查询所有分类类型
     *
     * @return
     */
    @SystemLog(description = "查询所有分类类型", type = LogType.OPERATION)
    @ApiOperation("查询所有分类类型")
    @GetMapping("getAllProTypeByTree")
    public Result<Object> getAllProTypeByTree(String parentId, String typeName) {
        QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);

        if (StringUtils.isNotBlank(typeName)) {
            queryWrapper.like("type_name", typeName).or().like("type_code", typeName);
        } else {
            queryWrapper.eq("parent_id", StringUtils.isNotBlank(parentId) ? parentId : "0");
        }

        List<TProType> proTypes = tProTypeService.list(queryWrapper);

        if (StringUtils.isBlank(typeName)) {
            //所有
            QueryWrapper<TProType> queryWrapperAll = new QueryWrapper<>();
            queryWrapperAll.eq("del_flag", 0);
            List<TProType> list = tProTypeService.list(queryWrapperAll);
            //递归找子集
            recursion(proTypes,list);
        }
        return ResultUtil.data(proTypes);
    }


    /**
     * 功能描述：导出数据
     *
     * @param response 请求参数
     * @param tProType 查询参数
     * @return
     */
    @SystemLog(description = "导出职业类型数据", type = LogType.OPERATION)
    @ApiOperation("导出职业类型数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TProType tProType) {
        try {
            tProTypeService.download(tProType, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SystemLog(description = "根据类型编码查询数据", type = LogType.OPERATION)
    @ApiOperation("根据类型编码查询数据")
    @GetMapping("/queryProTypeByTypeCode")
    public Result<Object> queryProTypeByTypeCode(String typeCode) {
        QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_code", typeCode);
        queryWrapper.eq("del_flag", 0);
        TProType tProType = tProTypeService.getOne(queryWrapper);
        if (tProType == null) {
            return ResultUtil.error("类型不存在！");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.eq("parent_id", tProType.getId());
        List<TProType> list = tProTypeService.list(queryWrapper);
        //所有
        QueryWrapper<TProType> queryWrapperAll = new QueryWrapper<>();
        queryWrapperAll.eq("del_flag", 0);
        List<TProType> listAll = tProTypeService.list(queryWrapperAll);
        //递归找子集
        recursion(list,listAll);
        return ResultUtil.data(list);
    }


    @SystemLog(description = "插入职业危害数据", type = LogType.OPERATION)
    @ApiOperation("插入职业危害数据")
    @GetMapping("/insertTProType")
    public Result<Object> insertTProType() {
        String url = "https://www.zhongjingzh.com:8899/zj-peis/api/typeInfo/findAllTypes";
        Map<String, Object> param = new HashMap<>();

        HttpRequest httpRequest = HttpUtil.createGet(url);
        httpRequest.header("user_token", "eyJhbGciOiJIUzI1NiJ9.eyJkZXB0SWQiOjEsImV4cCI6MTk5MzY1NTQyMSwidXNlcmlkIjoxLCJ1c2VybmFtZSI6IueuoeeQhuWRmCJ9.ndv7sVMRnjeW_58NQz2H4frD8F11btojFCH2YD2XcX0");
        httpRequest.form(param);
        HttpResponse execute = httpRequest.execute();
        String body = execute.body();

        JSONObject jsonObject = JSON.parseObject(body);
        JSONArray result = jsonObject.getJSONArray("result");


        List<TProType> list = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            JSONObject json = (JSONObject) result.get(i);

            TProType tProType = new TProType();
            tProType.setId(json.getString("id"));
            tProType.setTypeName(json.getString("typeName"));
            tProType.setTypeCode(json.getString("typeCode"));
            tProType.setRemark(json.getString("remark"));
            tProType.setParentId(json.getString("parentId"));
            tProType.setCreateTime(new Date());
            tProType.setCreateId(securityUtil.getCurrUser().getId());
            if (!StringUtils.isBlank(json.getString("sortNum"))) {
                tProType.setOrderNum(Integer.valueOf(json.getString("sortNum")));
            }
            list.add(tProType);
            //如果有children
            JSONArray children = json.getJSONArray("children");
            if (children.size() > 0) {
                List<TProType> proTypes = setRecursion(children);
                list.addAll(proTypes);
            }
        }
        tProTypeService.saveBatch(list);
        return ResultUtil.data(list);
    }

    public List<TProType> setRecursion(JSONArray children) {
        List<TProType> proTypes = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            JSONObject json = (JSONObject) children.get(i);
            TProType tProType = new TProType();
            tProType.setId(json.getString("id"));
            tProType.setTypeName(json.getString("typeName"));
            tProType.setTypeCode(json.getString("typeCode"));
            tProType.setRemark(json.getString("remark"));
            tProType.setParentId(json.getString("parentId"));
            tProType.setCreateTime(new Date());
            tProType.setCreateId(securityUtil.getCurrUser().getId());
            if (!StringUtils.isBlank(json.getString("sortNum"))) {
                tProType.setOrderNum(Integer.valueOf(json.getString("sortNum")));
            }
            proTypes.add(tProType);

            //如果有children
            JSONArray children1 = json.getJSONArray("children");
            if (children1.size() > 0) {
                List<TProType> proTypes1 = setRecursion(children1);
                proTypes.addAll(proTypes1);
            }
        }
        return proTypes;
    }
}
