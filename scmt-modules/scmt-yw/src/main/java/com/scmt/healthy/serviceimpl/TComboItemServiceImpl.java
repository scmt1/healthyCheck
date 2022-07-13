package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TComboItem;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TComboItemMapper;
import com.scmt.core.utis.FileUtil;
import javax.servlet.http.HttpServletResponse;

import com.scmt.healthy.service.ITComboItemService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
/**
 *@author
 **/
@Service
public class TComboItemServiceImpl extends ServiceImpl<TComboItemMapper, TComboItem> implements ITComboItemService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TComboItemMapper tComboItemMapper;

    @Override
    public IPage<TComboItem> queryTComboItemListByPage(TComboItem  tComboItem, SearchVo searchVo, PageVo pageVo){
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
        Page<TComboItem> pageData = new Page<>(page, limit);
        QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
        if (tComboItem !=null) {
            queryWrapper = LikeAllFeild(tComboItem,searchVo);
        }
        IPage<TComboItem> result = tComboItemMapper.selectPage(pageData, queryWrapper);
        return  result;
    }
    @Override
    public void download(TComboItem tComboItem, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
        if (tComboItem !=null) {
            queryWrapper = LikeAllFeild(tComboItem,null);
        }
        List<TComboItem> list = tComboItemMapper.selectList(queryWrapper);
        for (TComboItem re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("名称", re.getName());
            map.put("简称", re.getShortName());
            map.put("排序", re.getOrderNum());
            map.put("销售价（元）", re.getSalePrice());
            map.put("折扣(元)", re.getDiscount());
            map.put("折扣价(元)", re.getDiscountPrice());
            map.put("适合人群", re.getSuitableRange());
            map.put("项目介绍", re.getIntroduce());
            map.put("检查地址", re.getAddress());
            map.put("备注", re.getRemark());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TComboItem> listByComboIds(QueryWrapper<TComboItem> queryWrapper) {
        return tComboItemMapper.listByComboIds(queryWrapper);
    }

    /**
     * 功能描述：构建模糊查询
     * @param tComboItem 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TComboItem>  LikeAllFeild(TComboItem  tComboItem, SearchVo searchVo) {
        QueryWrapper<TComboItem> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(tComboItem.getId())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getId, tComboItem.getId()));
        }
        if(StringUtils.isNotBlank(tComboItem.getName())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getName, tComboItem.getName()));
        }
        if(StringUtils.isNotBlank(tComboItem.getShortName())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getShortName, tComboItem.getShortName()));
        }
        if(tComboItem.getOrderNum() != null){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getOrderNum, tComboItem.getOrderNum()));
        }
        if(tComboItem.getSalePrice() != null){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getSalePrice, tComboItem.getSalePrice()));
        }
        if(tComboItem.getDiscount() != null){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getDiscount, tComboItem.getDiscount()));
        }
        if(tComboItem.getDiscountPrice() != null){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getDiscountPrice, tComboItem.getDiscountPrice()));
        }
        if(StringUtils.isNotBlank(tComboItem.getSuitableRange())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getSuitableRange, tComboItem.getSuitableRange()));
        }
        if(StringUtils.isNotBlank(tComboItem.getIntroduce())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getIntroduce, tComboItem.getIntroduce()));
        }
        if(StringUtils.isNotBlank(tComboItem.getAddress())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getAddress, tComboItem.getAddress()));
        }
        if(StringUtils.isNotBlank(tComboItem.getRemark())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getRemark, tComboItem.getRemark()));
        }
        if(StringUtils.isNotBlank(tComboItem.getCreateId())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getCreateId, tComboItem.getCreateId()));
        }
        if(tComboItem.getCreateTime() != null){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getCreateTime, tComboItem.getCreateTime()));
        }

        if(StringUtils.isNotBlank(tComboItem.getDepartmentId())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getDepartmentId, tComboItem.getDepartmentId()));
        }
        if(StringUtils.isNotBlank(tComboItem.getTemplate())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getTemplate, tComboItem.getTemplate()));
        }
        if(StringUtils.isNotBlank(tComboItem.getServiceType())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getServiceType, tComboItem.getServiceType()));
        }
        if(StringUtils.isNotBlank(tComboItem.getSpecimen())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getSpecimen, tComboItem.getSpecimen()));
        }
        if(StringUtils.isNotBlank(tComboItem.getDiagnostic())){
            queryWrapper.lambda().and(i -> i.like(TComboItem::getDiagnostic, tComboItem.getDiagnostic()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.lambda().and(i -> i.between(TComboItem::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
