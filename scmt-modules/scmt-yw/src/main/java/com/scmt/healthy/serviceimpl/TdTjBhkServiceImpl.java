package com.scmt.healthy.serviceimpl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TdTjBhk;
import com.scmt.healthy.mapper.TdTjBhkMapper;
import com.scmt.healthy.service.ITdTjBhkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 **/
@Service
@DS("sub")
public class TdTjBhkServiceImpl extends ServiceImpl<TdTjBhkMapper, TdTjBhk> implements ITdTjBhkService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TdTjBhkMapper tdTjBhkMapper;

    @Override
    public IPage<TdTjBhk> queryTdTjBhkListByPage(TdTjBhk tdTjBhk, SearchVo searchVo, PageVo pageVo) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TdTjBhk> pageData = new Page<>(page, limit);
        QueryWrapper<TdTjBhk> queryWrapper = new QueryWrapper<>();
        if (tdTjBhk != null) {
            queryWrapper = LikeAllField(tdTjBhk, searchVo);
        }
        IPage<TdTjBhk> result = tdTjBhkMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TdTjBhk tdTjBhk, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TdTjBhk> queryWrapper = new QueryWrapper<>();
        if (tdTjBhk != null) {
            queryWrapper = LikeAllField(tdTjBhk, null);
        }
        List<TdTjBhk> list = tdTjBhkMapper.selectList(queryWrapper);
        for (TdTjBhk re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("自增主键id", re.getId());
            map.put("业务系统主键", re.getRid());
            map.put("体检机构编号，由职业卫生平台提供的机构编码", re.getBhkorganCode());
            map.put("体检编号，机构内需唯一", re.getBhkCode());
            map.put("社会信用代码", re.getInstitutionCode());
            map.put("企业名称", re.getCrptName());
            map.put("企业注册地址", re.getCrptAddr());
            map.put("人员姓名", re.getPersonName());
            map.put("性别", re.getSex());
            map.put("证件号码", re.getIdc());
            map.put("出生日期", re.getBrth());
            map.put("年龄", re.getAge());
            map.put("婚否", re.getIsxmrd());
            map.put("人员联系电话", re.getLnktel());
            map.put("体检人员工作部门", re.getDpt());
            map.put("人员工号", re.getWrknum());
            map.put("总工龄年数", re.getWrklnt());
            map.put("总工龄月数,不能超过12,当总工龄年数不为空时，该字段必填", re.getWrklntmonth());
            map.put("接害工龄年数,不能超过12", re.getTchbadrsntim());
            map.put("接害工龄月数", re.getTchbadrsnmonth());
            map.put("工种其他名称", re.getWorkName());
            map.put("在岗状态编码", re.getOnguardState());
            map.put("体检日期", re.getBhkDate());
            map.put("体检结果", re.getBhkrst());
            map.put("主检建议", re.getMhkadv());
            map.put("体检结论", re.getVerdict());
            map.put("主检医师", re.getMhkdct());
            map.put("体检类型编码", re.getBhkType());
            map.put("主检判定日期", re.getJdgdat());
            map.put("接害因素", re.getBadrsn());
            map.put("是否为复检", re.getIfRhk());
            map.put("复检对应的上次体检编号", re.getLastBhkCode());
            map.put("身份证件类型代码", re.getIdCardTypeCode());
            map.put("工种代码", re.getWorkTypeCode());
            map.put("开始接害日期", re.getHarmStartDate());
            map.put("监测类型", re.getJcType());
            map.put("报告打印日期", re.getRptPrintDate());
            map.put("用工单位社会信用代码", re.getCreditCodeEmp());
            map.put("用工单位名称", re.getCrptNameEmp());
            map.put("用工单位行业类别编码", re.getIndusTypeCodeEmp());
            map.put("用工单位经济类型编码", re.getEconomyCodeEmp());
            map.put("用工单位企业规模编码", re.getCrptSizeCodeEmp());
            map.put("用工单位所属地区编码", re.getZoneCodeEmp());
            map.put("是否上传标志 是否上传标识--0：未上传 1：上传成功 2：上传失败", re.getFlag());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<Map<String, Object>> queryCompanyList() {
        return tdTjBhkMapper.queryCompanyList();
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tdTjBhk 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TdTjBhk> LikeAllField(TdTjBhk tdTjBhk, SearchVo searchVo) {
        QueryWrapper<TdTjBhk> queryWrapper = new QueryWrapper<>();
        if (tdTjBhk.getId() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getId, tdTjBhk.getId()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getRid())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getRid, tdTjBhk.getRid()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getBhkorganCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBhkorganCode, tdTjBhk.getBhkorganCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getBhkCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBhkCode, tdTjBhk.getBhkCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getInstitutionCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getInstitutionCode, tdTjBhk.getInstitutionCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getCrptName())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getCrptName, tdTjBhk.getCrptName()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getCrptAddr())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getCrptAddr, tdTjBhk.getCrptAddr()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getPersonName())) {
            queryWrapper.lambda().and(i -> i.like(TdTjBhk::getPersonName, tdTjBhk.getPersonName()));
        }
        if (tdTjBhk.getSex() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getSex, tdTjBhk.getSex()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getIdc())) {
            queryWrapper.lambda().and(i -> i.like(TdTjBhk::getIdc, tdTjBhk.getIdc()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getBrth())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBrth, tdTjBhk.getBrth()));
        }
        if (tdTjBhk.getAge() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getAge, tdTjBhk.getAge()));
        }
        if (tdTjBhk.getIsxmrd() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getIsxmrd, tdTjBhk.getIsxmrd()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getLnktel())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getLnktel, tdTjBhk.getLnktel()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getDpt())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getDpt, tdTjBhk.getDpt()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getWrknum())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getWrknum, tdTjBhk.getWrknum()));
        }
        if (tdTjBhk.getWrklnt() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getWrklnt, tdTjBhk.getWrklnt()));
        }
        if (tdTjBhk.getWrklntmonth() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getWrklntmonth, tdTjBhk.getWrklntmonth()));
        }
        if (tdTjBhk.getTchbadrsntim() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getTchbadrsntim, tdTjBhk.getTchbadrsntim()));
        }
        if (tdTjBhk.getTchbadrsnmonth() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getTchbadrsnmonth, tdTjBhk.getTchbadrsnmonth()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getWorkName())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getWorkName, tdTjBhk.getWorkName()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getOnguardState())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getOnguardState, tdTjBhk.getOnguardState()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getBhkDate())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBhkDate, tdTjBhk.getBhkDate()));
        }
        if (tdTjBhk.getBhkrst() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBhkrst, tdTjBhk.getBhkrst()));
        }
        if (tdTjBhk.getMhkadv() != null && StringUtils.isNotBlank(tdTjBhk.getMhkadv())) {
            queryWrapper.lambda().and(i -> i.like(TdTjBhk::getMhkadv, tdTjBhk.getMhkadv()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getVerdict())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getVerdict, tdTjBhk.getVerdict()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getMhkdct())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getMhkdct, tdTjBhk.getMhkdct()));
        }
        if (tdTjBhk.getBhkType() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBhkType, tdTjBhk.getBhkType()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getJdgdat())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getJdgdat, tdTjBhk.getJdgdat()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getBadrsn())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getBadrsn, tdTjBhk.getBadrsn()));
        }
        if (tdTjBhk.getIfRhk() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getIfRhk, tdTjBhk.getIfRhk()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getLastBhkCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getLastBhkCode, tdTjBhk.getLastBhkCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getIdCardTypeCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getIdCardTypeCode, tdTjBhk.getIdCardTypeCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getWorkTypeCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getWorkTypeCode, tdTjBhk.getWorkTypeCode()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getHarmStartDate())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getHarmStartDate, tdTjBhk.getHarmStartDate()));
        }
        if (tdTjBhk.getJcType() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getJcType, tdTjBhk.getJcType()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getRptPrintDate())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getRptPrintDate, tdTjBhk.getRptPrintDate()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getCreditCodeEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getCreditCodeEmp, tdTjBhk.getCreditCodeEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getCrptNameEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getCrptNameEmp, tdTjBhk.getCrptNameEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getIndusTypeCodeEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getIndusTypeCodeEmp, tdTjBhk.getIndusTypeCodeEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getEconomyCodeEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getEconomyCodeEmp, tdTjBhk.getEconomyCodeEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getCrptSizeCodeEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getCrptSizeCodeEmp, tdTjBhk.getCrptSizeCodeEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getZoneCodeEmp())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getZoneCodeEmp, tdTjBhk.getZoneCodeEmp()));
        }
        if (StringUtils.isNotBlank(tdTjBhk.getOrderCode())) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getOrderCode, tdTjBhk.getOrderCode()));
        }
        if (tdTjBhk.getFlag() != null) {
            queryWrapper.lambda().and(i -> i.eq(TdTjBhk::getFlag, tdTjBhk.getFlag()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TdTjBhk::getBhkDate, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}