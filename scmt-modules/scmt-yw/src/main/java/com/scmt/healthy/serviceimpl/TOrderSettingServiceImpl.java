package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TOrderSetting;
import com.scmt.healthy.mapper.TOrderSettingMapper;
import com.scmt.healthy.service.ITOrderSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 */
@Service
@Transactional
public class TOrderSettingServiceImpl extends ServiceImpl<TOrderSettingMapper, TOrderSetting> implements ITOrderSettingService {

    @Resource
    private TOrderSettingMapper tOrderSettingMapper;

    @Resource
    private SecurityUtil securityUtil;


    @Override
    public IPage<TOrderSetting> queryTOrderSettingListByPage(TOrderSetting  tOrderSetting, SearchVo searchVo, PageVo pageVo){
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
        Page<TOrderSetting> pageData = new Page<>(page, limit);
        QueryWrapper<TOrderSetting> queryWrapper = new QueryWrapper<>();
        if (tOrderSetting !=null) {
            queryWrapper = LikeAllField(tOrderSetting,searchVo);
        }
        IPage<TOrderSetting> result = tOrderSettingMapper.selectPage(pageData, queryWrapper);
        return  result;
    }

    /**
     * 更新体检预约信息
     * @param tOrderSetting
     * @return
     */
    @Override
    public Boolean updateOrderSettingInfo(TOrderSetting tOrderSetting) {
        tOrderSetting.setUpdateBy(securityUtil.getCurrUser().getId());
        tOrderSetting.setUpdateTime(new Date());
        int row = tOrderSettingMapper.updateById(tOrderSetting);
        return row > 0;
    }

    /**
     * 导出excel
     * @param tOrderSetting 查询参数
     * @param response response参数
     */
    @Override
    public void download(TOrderSetting tOrderSetting, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TOrderSetting> queryWrapper = new QueryWrapper<>();
        if (tOrderSetting !=null) {
            queryWrapper = LikeAllField(tOrderSetting,null);
        }
        List<TOrderSetting> list = tOrderSettingMapper.selectList(queryWrapper);
        for (TOrderSetting re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("", re.getId());
            map.put("约预日期", re.getOrderDate());
            map.put("可预约人数", re.getNumber());
            map.put("已预约人数", re.getReservations());
            map.put("创建人", re.getCreateBy());
            map.put("创建时间", re.getCreateTime());
            map.put("修改人", re.getUpdateBy());
            map.put("修改时间", re.getUpdateTime());
            map.put("删除状态", re.getDelFlag());
            map.put("类型", re.getType());
            map.put("", re.getCode());
            map.put("", re.getStatus());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     * @param tOrderSetting 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TOrderSetting>  LikeAllField(TOrderSetting  tOrderSetting, SearchVo searchVo) {
        QueryWrapper<TOrderSetting> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(tOrderSetting.getId())){
            queryWrapper.and(i -> i.like("t_order_setting.id", tOrderSetting.getId()));
        }
        if(tOrderSetting.getOrderDate() != null){
            queryWrapper.and(i -> i.like("t_order_setting.order_date", tOrderSetting.getOrderDate()));
        }
        if(tOrderSetting.getNumber() != null){
            queryWrapper.and(i -> i.like("t_order_setting.number", tOrderSetting.getNumber()));
        }
        if(tOrderSetting.getReservations() != null){
            queryWrapper.and(i -> i.like("t_order_setting.reservations", tOrderSetting.getReservations()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getCreateBy())){
            queryWrapper.and(i -> i.like("t_order_setting.create_by", tOrderSetting.getCreateBy()));
        }
        if(tOrderSetting.getCreateTime() != null){
            queryWrapper.and(i -> i.like("t_order_setting.create_time", tOrderSetting.getCreateTime()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getUpdateBy())){
            queryWrapper.and(i -> i.like("t_order_setting.update_by", tOrderSetting.getUpdateBy()));
        }
        if(tOrderSetting.getUpdateTime() != null){
            queryWrapper.and(i -> i.like("t_order_setting.update_time", tOrderSetting.getUpdateTime()));
        }
        if(tOrderSetting.getDelFlag() != null){
            queryWrapper.and(i -> i.like("t_order_setting.del_flag", tOrderSetting.getDelFlag()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getType())){
            queryWrapper.and(i -> i.like("t_order_setting.type", tOrderSetting.getType()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getCode())){
            queryWrapper.and(i -> i.like("t_order_setting.code", tOrderSetting.getCode()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getStatus())){
            queryWrapper.and(i -> i.like("t_order_setting.status", tOrderSetting.getStatus()));
        }
        if(StringUtils.isNotBlank(tOrderSetting.getCheckOrgId())){
            queryWrapper.and(i -> i.like("t_order_setting.t_check_org_id", tOrderSetting.getCheckOrgId()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.lambda().and(i -> i.between(TOrderSetting::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TOrderSetting::getDelFlag, 0));
        return queryWrapper;

    }



    /**
     * 批量添加或修改预约设置信息
     * @param tOrderSettings
     * @return
     */
    @Override
    public boolean saveOrUpdateBatchInfo(List<TOrderSetting> tOrderSettings) {

        if(tOrderSettings != null || tOrderSettings.size() != 0){
            for (TOrderSetting orderSetting:tOrderSettings) {
                //如果orderSetting中没有CheckOrgId和orderDate的值,则不能添加
                if(orderSetting.getCheckOrgId() == null || orderSetting.getOrderDate() == null){
                    continue;
                }
                //当前日期已经存在预约设置，执行更新操作
                TOrderSetting tOrderSetting = selectOrderSettingIsExist(orderSetting);
                if(tOrderSetting != null){
                    //更新tOrderSetting的属性值
                    tOrderSetting.setNumber(orderSetting.getNumber());
                    tOrderSetting.setUpdateBy(securityUtil.getCurrUser().getId());
                    tOrderSetting.setUpdateTime(new Date());
                    //执行更新
                    tOrderSettingMapper.updateById(tOrderSetting);
                }
                //否则执行新增操作
                else if(tOrderSetting == null){
                    orderSetting.setCreateBy(securityUtil.getCurrUser().getId());
                    orderSetting.setCreateTime(new Date());
                    tOrderSettingMapper.insert(orderSetting);
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 根据体检机构和时间格式来查看对应的预约设置信息
     * @param tOrderSetting
     * @return
     */
    @Override
    public List<Map<String,Object>> findOrderSettingInfoByOrg(TOrderSetting tOrderSetting,String dateTime) {
        if(tOrderSetting.getCheckOrgId() != null || dateTime != null){
            //判断dateTime的格式(yyyy或yyyy-MM)
            if(dateTime.indexOf("-") != -1){
                List<Map<String, Object>> list = tOrderSettingMapper.getOrderInfoByOrgIdAndMonth(tOrderSetting.getCheckOrgId(), dateTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (Map<String,Object> map:list) {
                    Date date = (Date)map.get("orderDate");
                    map.put("orderDate",simpleDateFormat.format(date));
                }
                return list;
            }else if(dateTime.indexOf("-") == -1){
                List<Map<String, Object>> list = tOrderSettingMapper.getCountInfoByOrgIdAndYear(tOrderSetting.getCheckOrgId(), dateTime);
                return list;
            }else {
                return null;
            }
        }
       return null;
    }

    /**
     * 根据体检机构id和当前日期来获取对应时间区间的预约设置信息
     * @param tOrderSetting
     * @return
     */
    @Override
    public List<Map<String,Object>> findOrderSettingInfoByOrgId(TOrderSetting tOrderSetting) {
        if(tOrderSetting.getCheckOrgId() != null){
            if(tOrderSetting.getBeginDate() != null && tOrderSetting.getEndDate() != null){
                //判断日期大小
                if(tOrderSetting.getBeginDate().compareTo(tOrderSetting.getEndDate()) > 0){
                    String temp = tOrderSetting.getBeginDate();
                    tOrderSetting.setBeginDate(tOrderSetting.getEndDate());
                    tOrderSetting.setEndDate(temp);
                }
                List<Map<String, Object>> list = tOrderSettingMapper.getAvailableTimeByOrgIdAndOrderDate(tOrderSetting);
                return list;
            }
        }
        return null;
    }

    /**
     *判断同一天,是否存在同一个机构拥有多个体检预约设置
     * @param tOrderSetting
     * @return
     */
    public TOrderSetting selectOrderSettingIsExist(TOrderSetting tOrderSetting){
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
         QueryWrapper<TOrderSetting> queryWrapper = new QueryWrapper<>();
         queryWrapper.and(i -> i.eq("t_order_setting.check_org_id", tOrderSetting.getCheckOrgId()));
         String orderDate = simpleDateFormat.format(tOrderSetting.getOrderDate());
         queryWrapper.and(i -> i.like("t_order_setting.order_date", orderDate));
         return tOrderSettingMapper.selectOne(queryWrapper);
    }

    /**
     * 根据体检机构id和体检时间获取对应的预约设置信息
     * @param checkOrgId
     * @param checkDate
     * @return
     */
    @Override
    public TOrderSetting findOrderSettingByCheckOrgAndCheckDate(String checkOrgId, String checkDate) {
        if(checkOrgId == null && checkDate == null){
            return null;
        }
        QueryWrapper<TOrderSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(i -> i.eq("t_order_setting.check_org_id", checkOrgId));
        queryWrapper.and(i -> i.like("t_order_setting.order_date", checkDate));
        return tOrderSettingMapper.selectOne(queryWrapper);
    }
}
