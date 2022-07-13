package com.scmt.base.controller.manage;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.word.DocUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.base.async.AddMessage;
import com.scmt.base.utils.BASE64DecodedMultipartFile;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.redis.RedisTemplateHelper;
import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.utils.StopWordsUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.config.security.SecurityUserDetails;
import com.scmt.core.dao.mapper.DeleteMapper;
import com.scmt.core.entity.Department;
import com.scmt.core.entity.Role;
import com.scmt.core.entity.User;
import com.scmt.core.entity.UserRole;
import com.scmt.core.service.*;
import com.scmt.core.service.mybatis.IUserRoleService;
import com.scmt.core.vo.RoleDTO;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(description = "用户接口")
@RequestMapping("/scmt/user")
@CacheConfig(cacheNames = "user")
@Transactional
public class UserController {

    public static final String USER = "user::";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentHeaderService departmentHeaderService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AddMessage addMessage;

    @Autowired
    private DeleteMapper deleteMapper;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/smsLogin", method = RequestMethod.POST)
    @SystemLog(description = "短信登录", type = LogType.LOGIN)
    @ApiOperation(value = "短信登录接口")
    public Result<Object> smsLogin(@RequestParam String mobile,
                                   @RequestParam(required = false) Boolean saveLogin) {

        User u = userService.findByMobile(mobile);
        if (u == null) {
            throw new ScmtException("手机号不存在");
        }
        String accessToken = securityUtil.getToken(u.getUsername(), saveLogin);
        // 记录日志使用
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new SecurityUserDetails(u), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResultUtil.data(accessToken);
    }

    @RequestMapping(value = "/appLogin", method = RequestMethod.POST)
    @SystemLog(description = "用户登录(账号/手机号)", type = LogType.LOGIN)
    @ApiOperation(value = "用户登录接口(账号/手机号)")
    public Result<Object> appLogin(@RequestParam(value = "username") String username,
                                   @RequestParam(value = "password") String password,
                                   @RequestParam(required = false) Boolean saveLogin) {

        User u = userService.findByMobile(username);//手机号查询
        if (u == null) {
            u = userService.findByUsername(username);//账号查询
            if (u == null) {
                throw new ScmtException("账号或手机号不存在");
            }
        }
        if(!new BCryptPasswordEncoder().matches(password, u.getPassword())){
            throw new ScmtException("密码错误");
        }
        String accessToken = securityUtil.getToken(u.getUsername(), saveLogin);
        // 记录日志使用
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new SecurityUserDetails(u), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = new User();
        user.setUsername(u.getUsername());
        user.setNickname(u.getNickname());
        user.setMobile(u.getMobile());
        user.setEmail(u.getEmail());
        user.setSex(u.getSex());
        return ResultUtil.data(user,accessToken);
    }
    @RequestMapping(value = "/appWxLogin", method = RequestMethod.POST)
    @SystemLog(description = "用户登录(账号/手机号)", type = LogType.LOGIN)
    @ApiOperation(value = "用户登录接口(账号/手机号)")
    public Result<Object> appWxLogin(@RequestParam(value = "username") String username,//用户名称
                                   @RequestParam(value = "password") String password,//密码
                                   @RequestParam(value = "mobile") String mobile,//手机号
                                   @RequestParam(value = "avatar") String avatar,//用户头像
                                   @RequestParam(required = false) Boolean saveLogin) {

        User u = userService.findByMobile(mobile);//手机号查询
        if (u!=null) {//已存在 直接登录
            String accessToken = securityUtil.getToken(u.getUsername(), saveLogin);
            // 记录日志使用
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new SecurityUserDetails(u), null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = new User();
            user.setUsername(u.getUsername());
            user.setNickname(u.getNickname());
            user.setMobile(u.getMobile());
            user.setEmail(u.getEmail());
            user.setSex(u.getSex());
            return ResultUtil.data(user,accessToken);
        }else{//注册并登录
            String encryptPass = new BCryptPasswordEncoder().encode(password);
            u.setPassword(encryptPass).setType(CommonConstant.USER_TYPE_NORMAL);
            if(u.getNickname()!= null && StringUtils.isBlank(u.getNickname())){
                u.setNickname(username);
            }
            u.setUsername(username);
            u.setMobile(mobile);
            u.setAvatar(null);
            User user = userService.save(u);

            // 默认角色
            List<Role> roleList = roleService.findByDefaultRole(true);
            if (roleList != null && roleList.size() > 0) {
                for (Role role : roleList) {
                    UserRole ur = new UserRole().setUserId(user.getId()).setRoleId(role.getId());
                    userRoleService.save(ur);
                }
            }
            // 异步发送创建账号消息
            //addMessage.addSendMessage(user.getId());

            String accessToken = securityUtil.getToken(u.getUsername(), true);
            // 记录日志使用
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new SecurityUserDetails(u), null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User userNew = new User();
            userNew.setUsername(user.getUsername());
            userNew.setNickname(user.getNickname());
            userNew.setMobile(user.getMobile());
            userNew.setEmail(user.getEmail());
            userNew.setSex(user.getSex());

            return ResultUtil.data(userNew,accessToken);
        }
    }
    @RequestMapping(value = "/appEdit", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户个人资料(昵称、用户名、性别、邮箱)")
    @CacheEvict(key = "#u.username")
    @SystemLog(description = "修改用户自己资料", type = LogType.OPERATION)
    public Result<Object> appEdit(User u) {
        User old = userService.findByMobile(u.getMobile());
        old.setNickname(u.getNickname());
        old.setUsername(u.getUsername());
        old.setSex(u.getSex());
        old.setEmail(u.getEmail());
        userService.update(old);
        return ResultUtil.data(u,"userService.update(old);");
    }
    /**
     * 线上demo不允许测试账号改密码
     *
     * @param mobile
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/appModifyPass", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    @SystemLog(description = "修改密码", type = LogType.OPERATION)
    public Result<Object> appModifyPass(@ApiParam("手机号") @RequestParam String mobile,
                                        @ApiParam("旧密码") @RequestParam String password,
                                        @ApiParam("新密码") @RequestParam String newPass,
                                        @ApiParam("密码强度") @RequestParam String passStrength) {

//        User user = userService.findByMobile("15897885125");
        User user = userService.findByMobile(mobile);
        // 在线DEMO所需
        if ("test".equals(user.getUsername()) || "test2".equals(user.getUsername())) {
            return ResultUtil.error("演示账号不支持修改密码");
        }

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return ResultUtil.error("旧密码不正确");
        }

        String newEncryptPass = new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        user.setPassStrength(passStrength);
        userService.update(user);

        // 手动更新缓存
        redisTemplate.delete(USER + user.getUsername());

        return ResultUtil.success("修改密码成功");
    }
    @RequestMapping(value = "/appRegist", method = RequestMethod.POST)
    @ApiOperation(value = "注册并登陆")
    @SystemLog(description = "注册并登陆", type = LogType.OPERATION)
    public Result<Object> appRegist(@Valid User u) {

        // 校验是否已存在
        if (StrUtil.isNotBlank(u.getMobile()) && userService.findByMobile(u.getMobile()) != null) {
            throw new ScmtException("该手机号已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass).setType(CommonConstant.USER_TYPE_NORMAL);
        if(u.getNickname()!= null && StringUtils.isBlank(u.getNickname())){
            u.setNickname(u.getUsername());
        }
        User user = userService.save(u);

        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                UserRole ur = new UserRole().setUserId(user.getId()).setRoleId(role.getId());
                userRoleService.save(ur);
            }
        }
        // 异步发送创建账号消息
        //addMessage.addSendMessage(user.getId());

        String accessToken = securityUtil.getToken(u.getUsername(), true);
        // 记录日志使用
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(new SecurityUserDetails(u), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userNew = new User();
        userNew.setUsername(user.getUsername());
        userNew.setNickname(user.getNickname());
        userNew.setMobile(user.getMobile());
        userNew.setEmail(user.getEmail());
        userNew.setSex(user.getSex());

        return ResultUtil.data(userNew,accessToken);
    }

    @RequestMapping(value = "/resetByMobile", method = RequestMethod.POST)
    @ApiOperation(value = "通过短信重置密码")
    @SystemLog(description = "通过短信重置密码", type = LogType.OPERATION)
    public Result<Object> resetByMobile(@RequestParam String mobile,
                                        @RequestParam String password,
                                        @RequestParam String passStrength) {

        User u = userService.findByMobile(mobile);
        String encryptPass = new BCryptPasswordEncoder().encode(password);
        u.setPassword(encryptPass).setPassStrength(passStrength);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete(USER + u.getUsername());
        return ResultUtil.success("重置密码成功");
    }

    /*
    *
    * 微信公众号
    * */
    @GetMapping(value = "/test/wx")
    @ApiOperation(value = "微信", httpMethod = "GET")
    public String wxFirm(@RequestParam(value = "signature", required = false) String signature,
                         @RequestParam(value = "timestamp", required = false) String timestamp,
                         @RequestParam(value = "nonce", required = false) String nonce,
                         @RequestParam(value = "echostr", required = false) String echostr) {
        String str= null;
        try {
            str = wxFirm1(signature, timestamp, nonce, echostr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;

    }
    public String wxFirm1(String signature, String timestamp, String nonce,String echostr) throws Exception {
        String checktext = null;
        System.out.println("signature"+signature);
        System.out.println("timestamp"+timestamp);
        System.out.println("nonce"+nonce);
        System.out.println("echostr"+echostr);
        if (null != signature) {
            // 对ToKen,timestamp,nonce 按字典排序,这里需要注意的是”abc123“和我们在微信公众号内配置的一样。
            String[] paramArr = new String[] { "abc123", timestamp, nonce };
            Arrays.sort(paramArr);
            // 将排序后的结果拼成一个字符串
            String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);

            try {
                /*MessageDigest md = MessageDigest.getInstance("SHA-1");
                // 对接后的字符串进行sha1加密
                byte[] digest = md.digest(content.toString().getBytes());
                checktext = byteToStr(digest);*/

                // 对接后的字符串进行sha1加密
                checktext = getSha1(content);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        System.out.println(checktext);
        // 将加密后的字符串与signature进行对比
        if(checktext.equals(signature.toUpperCase())) {
            return echostr;
        }else {
            return "false";
        }

    }
    /**
     * 进⾏sha1加密
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String getSha1(@RequestParam String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (null == str || str.length() == 0){
            return null;
        }
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    @SystemLog(description = "注册用户", type = LogType.OPERATION)
    public Result<Object> regist(@Valid User u) {

        // 校验是否已存在
        checkUserInfo(u.getUsername(), u.getMobile(), u.getEmail());

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass).setType(CommonConstant.USER_TYPE_NORMAL);
        User user = userService.save(u);

        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                UserRole ur = new UserRole().setUserId(user.getId()).setRoleId(role.getId());
                userRoleService.save(ur);
            }
        }
        // 异步发送创建账号消息
        //addMessage.addSendMessage(user.getId());

        return ResultUtil.data(user);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前登录用户接口")
    @SystemLog(description = "获取当前登录用户接口", type = LogType.OPERATION)
    public Result<User> getUserInfo() {

        User u = securityUtil.getCurrUser();
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        u.setPassword(null);
        return new ResultUtil<User>().setData(u);
    }

    @RequestMapping(value = "/checkPassword", method = RequestMethod.GET)
    @ApiOperation(value = "判断当前用户的密码是否过于简单")
    @SystemLog(description = "判断当前用户的密码是否过于简单", type = LogType.OPERATION)
    public Result<Object> checkPassword() {
        User u = securityUtil.getCurrUser();
        if("弱".equals(u.getPassStrength())) {
            return ResultUtil.data(true);
        }else {
            return ResultUtil.data(false);
        }
    }


    @RequestMapping(value = "/changeMobile", method = RequestMethod.POST)
    @ApiOperation(value = "修改绑定手机")
    @SystemLog(description = "修改绑定手机", type = LogType.OPERATION)
    public Result<Object> changeMobile(@RequestParam String mobile) {

        User u = securityUtil.getCurrUser();
        u.setMobile(mobile);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete(USER + u.getUsername());
        return ResultUtil.success("修改手机号成功");
    }

    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    @SystemLog(description = "解锁验证密码", type = LogType.OPERATION)
    public Result<Object> unLock(@RequestParam String password) {

        User u = securityUtil.getCurrUser();
        if (!new BCryptPasswordEncoder().matches(password, u.getPassword())) {
            return ResultUtil.error("密码不正确");
        }
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/resetPass", method = RequestMethod.POST)
    @ApiOperation(value = "重置密码")
    @SystemLog(description = "重置密码", type = LogType.OPERATION)
    public Result<Object> resetPass(@RequestParam String[] ids) {

        for (String id : ids) {
            User u = userService.get(id);
            // 在线DEMO所需
            if ("test".equals(u.getUsername()) || "test2".equals(u.getUsername()) || "admin".equals(u.getUsername())) {
                throw new ScmtException("测试账号及管理员账号不得重置");
            }
            u.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userService.update(u);
            redisTemplate.delete(USER + u.getUsername());
        }
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料", notes = "用户名密码等不会修改 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    @SystemLog(description = "修改用户自己资料", type = LogType.OPERATION)
    public Result<Object> editOwn(User u) {

        String urlPath = "";
        User old = securityUtil.getCurrUser();
        // 不能修改的字段
        u.setUsername(old.getUsername()).setPassword(old.getPassword()).setType(old.getType()).setStatus(old.getStatus());

        try {
            if (u.getAvatarFile() != null && StringUtils.isNotBlank(u.getAvatarFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAvatarFile());
                u.setAvatar(imgFile.getBytes());
            }
        }catch (Exception e){}

        try {
            if (u.getAutographFile() != null && StringUtils.isNotBlank(u.getAutographFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAutographFile());
                u.setAutograph(imgFile.getBytes());
            }
        }catch (Exception e){}

        userService.update(u);
        return ResultUtil.success("修改成功");
    }

    /**
     * 线上demo不允许测试账号改密码
     *
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    @SystemLog(description = "修改密码", type = LogType.OPERATION)
    public Result<Object> modifyPass(@ApiParam("旧密码") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass,
                                     @ApiParam("密码强度") @RequestParam String passStrength) {

        User user = securityUtil.getCurrUser();
        // 在线DEMO所需
        if ("test".equals(user.getUsername()) || "test2".equals(user.getUsername())) {
            return ResultUtil.error("演示账号不支持修改密码");
        }

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return ResultUtil.error("旧密码不正确");
        }

        String newEncryptPass = new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        user.setPassStrength(passStrength);
        userService.update(user);

        // 手动更新缓存
        redisTemplate.delete(USER + user.getUsername());

        return ResultUtil.success("修改密码成功");
    }

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    @SystemLog(description = "多条件分页获取用户列表", type = LogType.OPERATION)
    public Result<Page<User>> getByCondition(User user,
                                             SearchVo searchVo,
                                             PageVo pageVo) {

        Page<User> page = userService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for (User u : page.getContent()) {
            // 关联角色
            List<Role> list = iUserRoleService.findByUserId(u.getId());
            List<RoleDTO> roleDTOList = list.stream().map(e -> {
                return new RoleDTO().setId(e.getId()).setName(e.getName()).setDescription(e.getDescription());
            }).collect(Collectors.toList());
            u.setRoles(roleDTOList);
            // 游离态 避免后面语句导致持久化
            entityManager.detach(u);
            u.setPassword(null);
        }
        return new ResultUtil<Page<User>>().setData(page);
    }

    @RequestMapping(value = "/getByDepartmentId/{departmentId}", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    @SystemLog(description = "多条件分页获取用户列表", type = LogType.OPERATION)
    public Result<List<User>> getByCondition(@PathVariable String departmentId) {

        List<User> list = userService.findByDepartmentId(departmentId);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/searchByName/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "通过用户名搜索用户")
    @SystemLog(description = "通过用户名搜索用户", type = LogType.OPERATION)
    public Result<List<User>> searchByName(@PathVariable String username) throws UnsupportedEncodingException {

        List<User> list = userService.findByUsernameLikeAndStatus(URLDecoder.decode(username, "utf-8"), CommonConstant.STATUS_NORMAL);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    @SystemLog(description = "获取全部用户数据", type = LogType.OPERATION)
    public Result<List<User>> getAll() {

        List<User> list = userService.getAll();
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        for (User u : list) {
            u.setPassword(null);
        }
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    @SystemLog(description = "添加用户", type = LogType.OPERATION)
    public Result<Object> add(@Valid User u,
                              @RequestParam(required = false) String[] roleIds) throws IOException, SQLException {

        // 校验是否已存在
        checkUserInfo(u.getUsername(), u.getMobile(), u.getEmail());

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        if (StrUtil.isNotBlank(u.getDepartmentId())) {
            Department d = departmentService.get(u.getDepartmentId());
            if (d != null) {
                u.setDepartmentTitle(d.getTitle());
            }
        } else {
            u.setDepartmentId(null);
            u.setDepartmentTitle("");
        }
        try {
            if (u.getAvatarFile() != null && StringUtils.isNotBlank(u.getAvatarFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAvatarFile());
                u.setAvatar(imgFile.getBytes());
            }
        }catch (Exception e){}

        try {
            if (u.getAutographFile() != null && StringUtils.isNotBlank(u.getAutographFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAutographFile());
                u.setAutograph(imgFile.getBytes());
            }
        }catch (Exception e){}
        User user = userService.save(u);
        if (roleIds != null) {
            // 添加角色
            List<UserRole> userRoles = Arrays.asList(roleIds).stream().map(e -> {
                return new UserRole().setUserId(u.getId()).setRoleId(e);
            }).collect(Collectors.toList());
            userRoleService.saveOrUpdateAll(userRoles);
        }
        // 发送创建账号消息
        //addMessage.addSendMessage(user.getId());

        return ResultUtil.success("添加成功");
    }

    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ApiOperation(value = "管理员修改资料", notes = "需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    @SystemLog(description = "管理员修改资料", type = LogType.OPERATION)
    public Result<Object> edit(User u, @RequestParam(required = false) String[] roleIds) throws IOException, SQLException {

        User old = userService.get(u.getId());

        u.setUsername(old.getUsername());
        // 若修改了手机和邮箱判断是否唯一
        if (!old.getMobile().equals(u.getMobile()) && userService.findByMobile(u.getMobile()) != null) {
            return ResultUtil.error("该手机号已绑定其他账户");
        }
//        if (!old.getEmail().equals(u.getEmail()) && userService.findByEmail(u.getEmail()) != null) {
//            return ResultUtil.error("该邮箱已绑定其他账户");
//        }
        if (StrUtil.isNotBlank(u.getDepartmentId())) {
            Department d = departmentService.get(u.getDepartmentId());
            if (d != null) {
                u.setDepartmentTitle(d.getTitle());
            }
        } else {
            u.setDepartmentId(null);
            u.setDepartmentTitle("");
        }
        try {
            if (u.getAvatarFile() != null && StringUtils.isNotBlank(u.getAvatarFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAvatarFile());
                u.setAvatar(imgFile.getBytes());
            }
        }catch (Exception e){}
        try {
            if (u.getAutographFile() != null && StringUtils.isNotBlank(u.getAutographFile())) {
                MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(u.getAutographFile());
                u.setAutograph(imgFile.getBytes());
            }
        }catch (Exception e){}
        u.setPassword(old.getPassword());
        userService.update(u);
        // 删除该用户角色
        userRoleService.deleteByUserId(u.getId());
        if (roleIds != null) {
            // 新角色
            List<UserRole> userRoles = Arrays.asList(roleIds).stream().map(e -> {
                return new UserRole().setRoleId(e).setUserId(u.getId());
            }).collect(Collectors.toList());
            userRoleService.saveOrUpdateAll(userRoles);
        }
        // 手动删除缓存
        redisTemplate.delete("userRole::" + u.getId());
        redisTemplate.delete("userRole::depIds:" + u.getId());
        redisTemplate.delete("permission::userMenuList:" + u.getId());
        return ResultUtil.success("修改成功");
    }

    @RequestMapping(value = "/admin/disable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    @SystemLog(description = "后台禁用用户", type = LogType.OPERATION)
    public Result<Object> disable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        User user = userService.get(userId);
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        userService.update(user);
        // 手动更新缓存
        redisTemplate.delete(USER + user.getUsername());
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/admin/enable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    @SystemLog(description = "后台启用用户", type = LogType.OPERATION)
    public Result<Object> enable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        User user = userService.get(userId);
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        userService.update(user);
        // 手动更新缓存
        redisTemplate.delete(USER + user.getUsername());
        return ResultUtil.success("操作成功");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过ids删除")
    @SystemLog(description = "批量通过ids删除", type = LogType.OPERATION)
    public Result<Object> delAllByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            User u = userService.get(id);
            // 删除相关缓存
            redisTemplate.delete(USER + u.getUsername());
            redisTemplate.delete("userRole::" + u.getId());
            redisTemplate.delete("userRole::depIds:" + u.getId());
            redisTemplate.delete("permission::userMenuList:" + u.getId());
            redisTemplate.deleteByPattern("department::*");

            userService.delete(id);

            // 删除关联角色
            userRoleService.deleteByUserId(id);
            // 删除关联部门负责人
            departmentHeaderService.deleteByUserId(id);

            // 删除关联流程、社交账号数据
            try {
                deleteMapper.deleteActNode(u.getId());
                deleteMapper.deleteActStarter(u.getId());
                deleteMapper.deleteSocial(u.getUsername());
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ApiOperation(value = "导入用户数据")
    @SystemLog(description = "导入用户数据", type = LogType.OPERATION)
    public Result<Object> importData(@RequestBody List<User> users) {

        List<Integer> errors = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        int count = 0;
        for (User u : users) {
            count++;
            // 验证用户名密码不为空
            if (StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())) {
                errors.add(count);
                reasons.add("用户名或密码为空");
                continue;
            }
            // 验证用户名唯一
            if (userService.findByUsername(u.getUsername()) != null) {
                errors.add(count);
                reasons.add("用户名已存在");
                continue;
            }
            // 加密密码
            u.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
            // 验证部门id正确性
            if (StrUtil.isNotBlank(u.getDepartmentId())) {
                try {
                    Department d = departmentService.get(u.getDepartmentId());
                    log.info(d.toString());
                } catch (Exception e) {
                    errors.add(count);
                    reasons.add("部门id不存在");
                    continue;
                }
            }
            if (u.getStatus() == null) {
                u.setStatus(CommonConstant.USER_STATUS_NORMAL);
            }
            userService.save(u);
            // 分配默认角色
            if (u.getDefaultRole() != null && u.getDefaultRole() == 1) {
                List<Role> roleList = roleService.findByDefaultRole(true);
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        UserRole ur = new UserRole().setUserId(u.getId()).setRoleId(role.getId());
                        userRoleService.save(ur);
                    }
                }
            }
        }
        // 批量保存数据
        int successCount = users.size() - errors.size();
        String successMessage = "全部导入成功，共计 " + successCount + " 条数据";
        String failMessage = "导入成功 " + successCount + " 条，失败 " + errors.size() + " 条数据。<br>" +
                "第 " + errors.toString() + " 行数据导入出错，错误原因分别为：<br>" + reasons.toString();
        String message = "";
        if (errors.isEmpty()) {
            message = successMessage;
        } else {
            message = failMessage;
        }
        return ResultUtil.success(message);
    }

    /**
     * 校验
     *
     * @param username 用户名 不校验传空字符或null 下同
     * @param mobile   手机号
     * @param email    邮箱
     */
    public void checkUserInfo(String username, String mobile, String email) {

        // 禁用词
        StopWordsUtil.matchWord(username);

        if (StrUtil.isNotBlank(username) && userService.findByUsername(username) != null) {
            throw new ScmtException("该登录账号已被注册");
        }
//        if (StrUtil.isNotBlank(email) && userService.findByEmail(email) != null) {
//            throw new ScmtException("该邮箱已被注册");
//        }
        if (StrUtil.isNotBlank(mobile) && userService.findByMobile(mobile) != null) {
            throw new ScmtException("该手机号已被注册");
        }
    }

    //根据手机号发送短信验证码
    @SystemLog(description = "根据手机号发送短信验证码", type = LogType.OPERATION)
    @ApiOperation("根据手机号发送短信验证码")
    @GetMapping("sendMsm")
    public Result<Object> sendMsm(String phone) {
        String code = "";
        code = RandomUtil.randomNumbers(6);//使用工具类生成随机六位数验证码
        Map<String,Object> param = new HashMap<>();
        param.put("code",code);//定义一个Map用于存储验证码
        //调用service发送短信
        boolean isSend = send(param,phone);
        if (isSend) {
            //发送成功，把验证码放到redis里面,设置有效时间10分钟
//            redisTemplate.opsForValue().set(phone,code,10, TimeUnit.MINUTES);
            return ResultUtil.success("发送成功");
        }else {
            return ResultUtil.error("短信发送失败");
        }

    }

    public boolean send(Map<String, Object> param, String phone) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential("AKIDc3FApDLDo7XhaDa5KIzRcW9WbQxFGzW3", "OXQ3QWLtAAtMZWBZgW4LL5XibDYcOt74");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"86"+phone};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId("1400679196");
            req.setSignName("兄弟们的掌上生活公众号");
            req.setTemplateId("1405129");

            String[] templateParamSet1 = {""+param.get("code"), "10"};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public boolean send(Map<String, Object> param, String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        DefaultProfile profile =
                DefaultProfile.getProfile("default", "阿里云账号ID", "阿里云账号秘钥");
        IAcsClient client = new DefaultAcsClient(profile);

        //设置相关参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        //固定写法
        request.setMethod(MethodType.POST);//请求方法
        request.setDomain("dysmsapi.aliyuncs.com");//阿里云发送
        request.setVersion("2017-05-25");//版本
        request.setAction("SendSms");//发送方法

        //设置发送相关的参数
        request.putQueryParameter("PhoneNumbers", phone);//设置发送的手机号码
        request.putQueryParameter("SignName", "*****");//*****为阿里云申请的签名名称
        request.putQueryParameter("TemplateCode", "*****");//*****为阿里云申请的模板ID
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));//设置验证码 参数需要是json格式字符串

        try {
            CommonResponse response = client.getCommonResponse(request);
//            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();//是否发送成功
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/
}
