package com.scmt.core.common.utils;

import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.common.constant.SecurityConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.vo.TokenMember;
import com.scmt.core.common.vo.TokenUser;
import com.scmt.core.config.properties.ScmtAppTokenProperties;
import com.scmt.core.config.properties.ScmtTokenProperties;
import com.scmt.core.entity.Department;
import com.scmt.core.entity.Member;
import com.scmt.core.entity.Role;
import com.scmt.core.entity.User;
import com.scmt.core.service.DepartmentService;
import com.scmt.core.service.MemberService;
import com.scmt.core.service.UserService;
import com.scmt.core.service.mybatis.IUserRoleService;
import com.scmt.core.vo.PermissionDTO;
import com.scmt.core.vo.RoleDTO;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Component
public class SecurityUtil {

    @Autowired
    private ScmtTokenProperties tokenProperties;

    @Autowired
    private ScmtAppTokenProperties appTokenProperties;

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    public String getToken(String username, Boolean saveLogin) {

        if (StrUtil.isBlank(username)) {
            throw new ScmtException("username不能为空");
        }
        Boolean saved = false;
        if (saveLogin == null || saveLogin) {
            saved = true;
            if (!tokenProperties.getRedis()) {
                tokenProperties.setTokenExpireTime(tokenProperties.getSaveLoginTime() * 60 * 24);
            }
        }
        // 生成token
        User u = userService.findByUsername(username);
        List<String> list = new ArrayList<>();
        // 缓存权限
        if (tokenProperties.getStorePerms()) {
            for (PermissionDTO p : u.getPermissions()) {
                if (StrUtil.isNotBlank(p.getTitle()) && StrUtil.isNotBlank(p.getPath())) {
                    list.add(p.getTitle());
                }
            }
            for (RoleDTO r : u.getRoles()) {
                list.add(r.getName());
            }
        }
        // 登陆成功生成token
        String token;
        if (tokenProperties.getRedis()) {
            // redis
            token = IdUtil.simpleUUID();
            TokenUser user = new TokenUser(u.getUsername(), list, saved);
            // 单设备登录 之前的token失效
            if (tokenProperties.getSdl()) {
                String oldToken = redisTemplate.get(SecurityConstant.USER_TOKEN + u.getUsername());
                if (StrUtil.isNotBlank(oldToken)) {
                    redisTemplate.delete(SecurityConstant.TOKEN_PRE + oldToken);
                }
            }
            if (saved) {
                redisTemplate.set(SecurityConstant.USER_TOKEN + u.getUsername(), token, tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
                redisTemplate.set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(user), tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
            } else {
                redisTemplate.set(SecurityConstant.USER_TOKEN + u.getUsername(), token, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
                redisTemplate.set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(user), tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
            }
        } else {
            // JWT不缓存权限 避免JWT长度过长
            list = null;
            // JWT
            token = SecurityConstant.TOKEN_SPLIT + Jwts.builder()
                    //主题 放入用户名
                    .setSubject(u.getUsername())
                    //自定义属性 放入用户拥有请求权限
                    .claim(SecurityConstant.AUTHORITIES, new Gson().toJson(list))
                    //失效时间
                    .setExpiration(new Date(System.currentTimeMillis() + tokenProperties.getTokenExpireTime() * 60 * 1000))
                    //签名算法和密钥
                    .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                    .compact();
        }
        return token;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public User getCurrUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ScmtException("未检测到登录用户");
        }
        return userService.findByUsername(authentication.getName());
    }

    /**
     * 获取当前用户数据权限 null代表具有所有权限 包含值为-1的数据代表无任何权限
     */
    public List<String> getDeparmentIds() {

        List<String> deparmentIds = new ArrayList<>();
        User u = getCurrUser();
        // 读取缓存
        String key = "userRole::depIds:" + u.getId();
        String v = redisTemplate.get(key);
        if (StrUtil.isNotBlank(v)) {
            deparmentIds = new Gson().fromJson(v, new TypeToken<List<String>>() {
            }.getType());
            return deparmentIds;
        }
        // 当前用户拥有角色
        List<Role> roles = iUserRoleService.findByUserId(u.getId());
        // 判断有无全部数据的角色
        Boolean flagAll = false;
        for (Role r : roles) {
            if (r.getDataType() == null || r.getDataType().equals(CommonConstant.DATA_TYPE_ALL)) {
                flagAll = true;
                break;
            }
        }
        // 包含全部权限返回null
        if (flagAll) {
            return null;
        }
        // 每个角色判断 求并集
        for (Role r : roles) {
            if (r.getDataType().equals(CommonConstant.DATA_TYPE_UNDER)) {
                // 本部门及以下
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    // 递归获取自己与子级
                    List<String> ids = new ArrayList<>();
                    getRecursion(u.getDepartmentId(), ids);
                    deparmentIds.addAll(ids);
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_SAME)) {
                // 本部门
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.add(u.getDepartmentId());
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_CUSTOM)) {
                // 自定义
                List<String> depIds = iUserRoleService.findDepIdsByUserId(u.getId());
                if (depIds == null || depIds.size() == 0) {
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.addAll(depIds);
                }
            }
        }
        // 去重
        LinkedHashSet<String> set = new LinkedHashSet<>(deparmentIds.size());
        set.addAll(deparmentIds);
        deparmentIds.clear();
        deparmentIds.addAll(set);
        // 缓存
        redisTemplate.set(key, new Gson().toJson(deparmentIds), 15L, TimeUnit.DAYS);
        return deparmentIds;
    }

    private void getRecursion(String departmentId, List<String> ids) {

        Department department = departmentService.get(departmentId);
        ids.add(department.getId());
        if (department.getIsParent() != null && department.getIsParent()) {
            // 获取其下级
            List<Department> departments = departmentService.findByParentIdAndStatusOrderBySortOrder(departmentId, CommonConstant.STATUS_NORMAL);
            departments.forEach(d -> {
                getRecursion(d.getId(), ids);
            });
        }
    }

    /**
     * 通过用户名获取用户拥有权限
     * @param username
     */
    public List<GrantedAuthority> getCurrUserPerms(String username) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        User user = userService.findByUsername(username);
        if (user == null || user.getPermissions() == null || user.getPermissions().isEmpty()) {
            return authorities;
        }
        for (PermissionDTO p : user.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(p.getTitle()));
        }
        return authorities;
    }

    public String getAppToken(String username, Integer platform) {

        if (StrUtil.isBlank(username)) {
            throw new ScmtException("username不能为空");
        }
        // 生成token
        String token = IdUtil.simpleUUID();
        TokenMember member = new TokenMember(username, platform);
        String key = SecurityConstant.MEMBER_TOKEN + member.getUsername() + ":" + platform;
        // 单平台登录 之前的token失效
        if (appTokenProperties.getSpl()) {
            String oldToken = redisTemplate.get(key);
            if (StrUtil.isNotBlank(oldToken)) {
                redisTemplate.delete(SecurityConstant.TOKEN_MEMBER_PRE + oldToken);
            }
        }
        redisTemplate.set(key, token, appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
        redisTemplate.set(SecurityConstant.TOKEN_MEMBER_PRE + token, new Gson().toJson(member), appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
        return token;
    }

    /**
     * 获取当前登录会员
     * @return
     */
    public Member getCurrMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ScmtException("未检测到登录会员");
        }
        return memberService.findByUsername(authentication.getName());
    }

    /**
     * 通过会员名获取其拥有权限
     * @param username
     */
    public List<GrantedAuthority> getCurrMemberPerms(String username) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        Member member = memberService.findByUsername(username);
        if (member == null || StrUtil.isBlank(member.getPermissions())) {
            return authorities;
        }
        String[] as = member.getPermissions().split(",");
        for (String a : as) {
            authorities.add(new SimpleGrantedAuthority(a));
        }
        return authorities;
    }
}
