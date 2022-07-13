package com.scmt.base.controller.manage;

import com.scmt.base.utils.VoUtil;
import com.scmt.base.vo.MenuVo;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.core.config.security.permission.MySecurityMetadataSource;
import com.scmt.core.entity.Permission;
import com.scmt.core.entity.RolePermission;
import com.scmt.core.entity.User;
import com.scmt.core.service.PermissionService;
import com.scmt.core.service.RolePermissionService;
import com.scmt.core.service.mybatis.IPermissionService;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scmt.core.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(description = "菜单/权限管理接口")
@RequestMapping("/scmt/permission")
@CacheConfig(cacheNames = "permission")
@Transactional
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private IPermissionService iPermissionService;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    @RequestMapping(value = "/getMenuList", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户页面菜单数据")
    @SystemLog(description = "获取用户页面菜单数据", type = LogType.OPERATION)
    public Result<List<MenuVo>> getAllMenuList() {

        List<MenuVo> menuList;
        // 读取缓存
        User u = securityUtil.getCurrUser();
        String key = "permission::userMenuList:" + u.getId();
        String v = redisTemplate.get(key);
        if (StrUtil.isNotBlank(v) && !v.equals("[]")) {
            menuList = new Gson().fromJson(v, new TypeToken<List<MenuVo>>() {
            }.getType());
            return new ResultUtil<List<MenuVo>>().setData(menuList);
        }

        // 用户所有权限 已排序去重
        List<Permission> list = iPermissionService.findByUserId(u.getId());

        // 筛选0级页面
        menuList = list.stream().filter(p -> CommonConstant.PERMISSION_NAV.equals(p.getType()))
                .sorted(Comparator.comparing(Permission::getSortOrder))
                .map(VoUtil::permissionToMenuVo).collect(Collectors.toList());
        getMenuByRecursion(menuList, list);

        // 缓存
        redisTemplate.set(key, new Gson().toJson(menuList), 15L, TimeUnit.DAYS);
        return new ResultUtil<List<MenuVo>>().setData(menuList);
    }

    private void getMenuByRecursion(List<MenuVo> curr, List<Permission> list) {

        curr.forEach(e -> {
            if (CommonConstant.LEVEL_TWO.equals(e.getLevel())) {
                List<String> buttonPermissions = list.stream()
                        .filter(p -> (e.getId()).equals(p.getParentId()) && CommonConstant.PERMISSION_OPERATION.equals(p.getType()))
                        .sorted(Comparator.comparing(Permission::getSortOrder))
                        .map(Permission::getButtonType).collect(Collectors.toList());
                e.setPermTypes(buttonPermissions);
            } else {
                List<MenuVo> children = list.stream()
                        .filter(p -> (e.getId()).equals(p.getParentId()) && CommonConstant.PERMISSION_PAGE.equals(p.getType()))
                        .sorted(Comparator.comparing(Permission::getSortOrder))
                        .map(VoUtil::permissionToMenuVo).collect(Collectors.toList());
                e.setChildren(children);
                if (e.getLevel() < 3) {
                    getMenuByRecursion(children, list);
                }
            }
        });
    }

    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    @ApiOperation(value = "获取权限菜单树")
    //@Cacheable(key = "'allList'")
    @SystemLog(description = "获取权限菜单树", type = LogType.OPERATION)
    public Result<List<Permission>> getAllList() {

        List<Permission> list = permissionService.getAll();
        // 0级
        List<Permission> list0 = list.stream().filter(e -> (CommonConstant.LEVEL_ZERO).equals(e.getLevel()))
                .sorted(Comparator.comparing(Permission::getSortOrder)).collect(Collectors.toList());
        getAllByRecursion(list0, list);
        return new ResultUtil<List<Permission>>().setData(list0);
    }

    private void getAllByRecursion(List<Permission> curr, List<Permission> list) {

        curr.forEach(e -> {
            List<Permission> children = list.stream().filter(p -> (e.getId()).equals(p.getParentId()))
                    .sorted(Comparator.comparing(Permission::getSortOrder)).collect(Collectors.toList());
            e.setChildren(children);
            if (e.getLevel() < 3) {
                getAllByRecursion(children, list);
            }
        });
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    @SystemLog(description = "添加", type = LogType.OPERATION)
    //@CacheEvict(key = "'menuList'")
    public Result<Permission> add(Permission permission) {

        // 判断拦截请求的操作权限按钮名是否已存在
        if (CommonConstant.PERMISSION_OPERATION.equals(permission.getType())) {
            List<Permission> list = permissionService.findByTitle(permission.getTitle());
            if (list != null && list.size() > 0) {
                return new ResultUtil<Permission>().setErrorMsg("名称已存在");
            }
        }
        Permission u = permissionService.save(permission);
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动删除缓存
        redisTemplate.delete("permission::allList");
        redisTemplate.delete("permission::userMenuList:" + securityUtil.getCurrUser().getId());
        return new ResultUtil<Permission>().setData(u);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    @SystemLog(description = "编辑", type = LogType.OPERATION)
    public Result<Permission> edit(Permission permission) {

        // 判断拦截请求的操作权限按钮名是否已存在
        if (CommonConstant.PERMISSION_OPERATION.equals(permission.getType())) {
            // 若名称修改
            Permission p = permissionService.get(permission.getId());
            if (!p.getTitle().equals(permission.getTitle())) {
                List<Permission> list = permissionService.findByTitle(permission.getTitle());
                if (list != null && list.size() > 0) {
                    return new ResultUtil<Permission>().setErrorMsg("名称已存在");
                }
            }
        }
        Permission u = permissionService.update(permission);
        // 重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        // 手动批量删除缓存
        redisTemplate.deleteByPattern("user:" + "*");
        redisTemplate.deleteByPattern("permission::userMenuList:*");
        redisTemplate.delete("permission::allList");
        redisTemplate.delete("permission::userMenuList:" + securityUtil.getCurrUser().getId());
        return new ResultUtil<Permission>().setData(u);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    @CacheEvict(key = "'menuList'")
    @SystemLog(description = "批量通过id删除", type = LogType.OPERATION)
    public Result<Object> delByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            List<RolePermission> list = rolePermissionService.findByPermissionId(id);
            if (list != null && list.size() > 0) {
                return ResultUtil.error("删除失败，包含正被角色使用关联的菜单或权限");
            }
        }
        for (String id : ids) {
            permissionService.delete(id);
        }
        // 重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        // 手动删除缓存
        redisTemplate.delete("permission::allList");
        redisTemplate.delete("permission::userMenuList:" + securityUtil.getCurrUser().getId());
        return ResultUtil.success("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "搜索菜单")
    @SystemLog(description = "搜索菜单", type = LogType.OPERATION)
    public Result<List<Permission>> searchPermissionList(@RequestParam String title) {

        List<Permission> list = permissionService.findByTitleLikeOrderBySortOrder("%" + title + "%");
        return new ResultUtil<List<Permission>>().setData(list);
    }

    @RequestMapping(value = "/queryFirstParentData", method = RequestMethod.GET)
    @ApiOperation(value = "查找最顶级菜单")
    @SystemLog(description = "查找最顶级菜单", type = LogType.OPERATION)
    public Result<Object> queryFirstParentData(String id) {
        Permission permission = permissionService.queryFirstParentData(id);
        return ResultUtil.data(permission);
    }
}
