package com.scmt.healthy.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.reporting.EmploymentUpload;
import com.scmt.healthy.reporting.Reporting;
import com.scmt.healthy.service.ITCertificateManageService;

import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.service.ITComboService;
import com.scmt.healthy.service.ITGroupPersonService;
import com.scmt.healthy.service.ITOrderGroupService;
import com.scmt.healthy.utils.BASE64DecodedMultipartFile;
import com.scmt.healthy.utils.DocUtil;
import com.scmt.healthy.utils.UploadFileUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import static com.scmt.healthy.utils.HttpClient.sendPostRequest;
import static com.scmt.healthy.utils.HttpClient.sendPostRequestMh;

/**
 * @author
 **/
@RestController
@Api(tags = " com.scmt.healthy数据接口")
@RequestMapping("/scmt/tCertificateManage")
public class TCertificateManageController {
    @Autowired
    private ITCertificateManageService tCertificateManageService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ITGroupPersonService tGroupPersonService;
    @Autowired
    private ITOrderGroupService tOrderGroupService;
    @Autowired
    private ITComboService tComboService;
    @Autowired
    private EmploymentUpload employmentUpload;

    /**
     * 功能描述：新增com.scmt.healthy数据
     * @param tCertificateManage 实体
     * @return 返回新增结果
     */
  @SystemLog(description = "新增com.scmt.healthy数据", type = LogType.OPERATION)
  @ApiOperation("新增com.scmt.healthy数据")
  @PostMapping("addTCertificateManage")
  public Result<Object> addTCertificateManage(@RequestBody TCertificateManage tCertificateManage){
    try {
      TGroupPerson tGroupPerson = tGroupPersonService.getById(tCertificateManage.getPersonId());
      tCertificateManage.setDelFlag(0);
      tCertificateManage.setCreateId(securityUtil.getCurrUser().getId());
      tCertificateManage.setCreateTime(new Date());
      tCertificateManage.setHeadImg(tGroupPerson.getAvatar());
      String oldNum ="000001";
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      String maxOldNum = tCertificateManageService.findMaxOldNum();
      if(maxOldNum!=null){
        int i = Integer.parseInt(maxOldNum)+1;
        oldNum = String.format("%06d", i);
      }
      String healthCcertificate = "川〔"+year+"〕"+ employmentUpload.getRegistration()+"-"+oldNum;//健康证编码
      String registrationNumber = year+ employmentUpload.getRegistration()+oldNum;//登记号码
      tCertificateManage.setHealthCcertificate(healthCcertificate);
      tCertificateManage.setRegistrationNumber(registrationNumber);
      boolean res = tCertificateManageService.save(tCertificateManage);
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
     * 功能描述：新增健康证数据
     *
     * @param ids
     * @return 返回新增结果
     */
    @SystemLog(description = "新增健康证数据", type = LogType.OPERATION)
    @ApiOperation("新增健康证数据")
    @PostMapping("increaseTCertificateManage")
    public Result<Object> increaseTCertificateManage(String[] ids) {
        if ((ids == null || ids.length == 0)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            Calendar calendar = Calendar.getInstance();
            // 获取当前年
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, 1);
            QueryWrapper<TGroupPerson> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", Arrays.asList(ids));
            List<TGroupPerson> personList = tGroupPersonService.list(queryWrapper);
            Boolean res = false;
            if (personList != null && personList.size() > 0) {
                for (int i = 0; i < personList.size(); i++) {
                    TCertificateManage tCertificateManage = new TCertificateManage();
                    TGroupPerson tGroupPerson = personList.get(i);
                    tCertificateManage.setPersonId(tGroupPerson.getId());
                    tCertificateManage.setResults("合格");
                    tCertificateManage.setDateOfIssue(new Date());
                    tCertificateManage.setOrderId(tGroupPerson.getOrderId());
                    tCertificateManage.setCode(tGroupPerson.getTestNum());
                    tCertificateManage.setName(tGroupPerson.getPersonName());
                    tCertificateManage.setSex(tGroupPerson.getSex());
                    tCertificateManage.setAge(tGroupPerson.getAge());
                    tCertificateManage.setTermOfValidity(calendar.getTime());
                    tCertificateManage.setUnitOfIssue(employmentUpload.getName());
                    tCertificateManage.setDelFlag(0);
                    tCertificateManage.setIsUpload(0);
                    tCertificateManage.setCreateId(securityUtil.getCurrUser().getId());
                    tCertificateManage.setCreateTime(new Date());
                    tCertificateManage.setHeadImg(tGroupPerson.getAvatar());
                    String oldNum = "000001";
                    String maxOldNum = tCertificateManageService.findMaxOldNum();
                    if (maxOldNum != null) {
                        int x = Integer.parseInt(maxOldNum) + 1;
                        oldNum = String.format("%06d", x);
                    }
                    String healthCcertificate = "川〔" + year + "〕" + employmentUpload.getRegistration() + "-" + oldNum;//健康证编码
                    String registrationNumber = year + employmentUpload.getRegistration() + oldNum;//登记号码
                    tCertificateManage.setHealthCcertificate(healthCcertificate);
                    tCertificateManage.setRegistrationNumber(registrationNumber);
                    res = tCertificateManageService.save(tCertificateManage);
                    if (!res) {
                      return ResultUtil.error("保存失败");
                    }
                }
            }
            if (res){
                return ResultUtil.data(res,"保存成功");
            }else {
                return ResultUtil.error("保存失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("保存异常:" + e.getMessage());
        }
    }


    /**
     * 功能描述：更新数据
     *
     * @param tCertificateManage 实体
     * @return 返回更新结果
     */
    @SystemLog(description = "更新com.scmt.healthy数据", type = LogType.OPERATION)
    @ApiOperation("更新com.scmt.healthy数据")
    @PostMapping("updateTCertificateManage")
    public Result<Object> updateTCertificateManage(@RequestBody TCertificateManage tCertificateManage) {
        if (StringUtils.isBlank(tCertificateManage.getId())) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            tCertificateManage.setUpdateId(securityUtil.getCurrUser().getId());
            tCertificateManage.setUpdateTime(new Date());
            boolean res = tCertificateManageService.updateById(tCertificateManage);
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
    @ApiOperation("根据主键来删除com.scmt.healthy数据")
    @SystemLog(description = "根据主键来删除com.scmt.healthy数据", type = LogType.OPERATION)
    @PostMapping("deleteTCertificateManage")
    public Result<Object> deleteTCertificateManage(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            boolean res = tCertificateManageService.removeByIds(Arrays.asList(ids));
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
    @SystemLog(description = "根据主键来获取com.scmt.healthy数据", type = LogType.OPERATION)
    @ApiOperation("根据主键来获取com.scmt.healthy数据")
    @GetMapping("getTCertificateManage")
    public Result<Object> getTCertificateManage(@RequestParam(name = "id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        try {
            TCertificateManage res = tCertificateManageService.getById(id);
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
    @SystemLog(description = "分页查询com.scmt.healthy数据", type = LogType.OPERATION)
    @ApiOperation("分页查询com.scmt.healthy数据")
    @GetMapping("queryTCertificateManageList")
    public Result<Object> queryTCertificateManageList(TCertificateManage tCertificateManage, SearchVo searchVo, PageVo pageVo) {
        try {
            IPage<TCertificateManage> result = tCertificateManageService.queryTCertificateManageListByPage(tCertificateManage, searchVo, pageVo);
            for (TCertificateManage tCertificateManageOne : result.getRecords()) {
                if (tCertificateManageOne != null && tCertificateManageOne.getHeadImg() != null) {
                    //字节转字符串
                    Boolean flag = false;
                    byte[] blob = (byte[]) tCertificateManageOne.getHeadImg();
                    if (blob != null) {
                        String avatarNow = new String(blob);
                        if (avatarNow.indexOf("/dcm") > -1) {
                            flag = true;
                            tCertificateManageOne.setHeadImgPath(avatarNow);
                        }
                    }
                    //若存的不是路径，则转换为路径形式并更新数据
                    if (!flag) {
                        String base64Str = Base64.getEncoder().encodeToString(blob);
                        String autograph = "data:image/png;base64," + base64Str;
                        MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(autograph);
                        String classPath = DocUtil.getClassPath().split(":")[0];
                        //时间戳
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        String DataStr = format.format(new Date());
                        if (tCertificateManageOne.getCode() != null && tCertificateManageOne.getCode().trim().length() > 0) {
                            DataStr = tCertificateManageOne.getCode();
                        }
                        String name = imgFile.getOriginalFilename();
                        File file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                        //存在则删除
                        if (file1.isFile() && file1.exists()) {
                            file1.delete();
                            file1 = new File(classPath + ":" + UploadFileUtils.basePath + "dcm/avatar/" + DataStr + "/" + name);
                        }
                        FileUtils.writeByteArrayToFile(file1, imgFile.getBytes());
                        String url = "/tempFileUrl/tempfile/dcm/avatar/" + DataStr + "/" + name;
                        tCertificateManageOne.setHeadImgPath(url);
                    }
                }
                if (tCertificateManageOne != null && StringUtils.isNotBlank(tCertificateManageOne.getGroupId())) {
                    //根据分组id获取套餐
                    TOrderGroup tOrderGroup = tOrderGroupService.getById(tCertificateManageOne.getGroupId());
                    if (tOrderGroup != null && StringUtils.isNotBlank(tOrderGroup.getComboId())) {
                        TCombo tCombo = tComboService.getById(tOrderGroup.getComboId());
                        if (tCombo != null && StringUtils.isNotBlank(tCombo.getName())) {
                            tCertificateManageOne.setComboName(tCombo.getName());
                        }
                    }
                }
            }
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：实现查询全部
     *
     * @param tCertificateManage 查询参数
     * @param searchVo           需要模糊查询的信息
     * @return 返回获取结果
     */
    @SystemLog(description = "查询tCertificateManage数据", type = LogType.OPERATION)
    @ApiOperation("查询tCertificateManage数据")
    @GetMapping("queryTCertificateManageAll")
    public Result<Object> queryTCertificateManageAll(TCertificateManage tCertificateManage, SearchVo searchVo) {
        try {
            List<TCertificateManage> result = tCertificateManageService.queryTCertificateManageByNotPage(tCertificateManage, searchVo);
            return ResultUtil.data(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("查询异常:" + e.getMessage());
        }
    }

    /**
     * 功能描述：导出数据
     *
     * @param response           请求参数
     * @param tCertificateManage 查询参数
     * @return
     */
    @SystemLog(description = "导出com.scmt.healthy数据", type = LogType.OPERATION)
    @ApiOperation("导出com.scmt.healthy数据")
    @PostMapping("/download")
    public void download(HttpServletResponse response, TCertificateManage tCertificateManage) {
        try {
            tCertificateManageService.download(tCertificateManage, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}