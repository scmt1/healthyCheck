package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TProType;
import com.scmt.healthy.mapper.TProTypeMapper;
import com.scmt.healthy.service.ITProTypeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author
 **/
@Service
public class TProTypeServiceImpl extends ServiceImpl<TProTypeMapper, TProType> implements ITProTypeService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TProTypeMapper tProTypeMapper;

    @Override
    public List<TProType> queryTProTypeListByPage(TProType tProType, SearchVo searchVo, PageVo pageVo) {
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
        Page<TProType> pageData = new Page<>(page, limit);
        QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
        if (tProType != null) {
            queryWrapper = LikeAllFeild(tProType, searchVo);
        }

        return tProTypeMapper.selectList(queryWrapper);
        //IPage<TProType> result = tProTypeMapper.selectPage(pageData, queryWrapper);
        //return result;
    }

    @Override
    public void download(TProType tProType, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
        if (tProType != null) {
            queryWrapper = LikeAllFeild(tProType, null);
        }
        List<TProType> list = tProTypeMapper.selectList(queryWrapper);
        for (TProType re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("类型名称", re.getTypeName());
            map.put("类型编码", re.getTypeCode());
            map.put("排序", re.getOrderNum());
            map.put("备注", re.getRemark());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tProType 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TProType> LikeAllFeild(TProType tProType, SearchVo searchVo) {
        QueryWrapper<TProType> queryWrapper = new QueryWrapper<>();
        if (tProType.getId() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getId, tProType.getId()));
        }
        if (StringUtils.isNotBlank(tProType.getTypeName())) {
            queryWrapper.lambda().and(i -> i.like(TProType::getTypeName, tProType.getTypeName()));
        }
        if (StringUtils.isNotBlank(tProType.getTypeCode())) {
            queryWrapper.lambda().and(i -> i.like(TProType::getTypeCode, tProType.getTypeCode()));
        }
        if (tProType.getOrderNum() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getOrderNum, tProType.getOrderNum()));
        }
        if (StringUtils.isNotBlank(tProType.getRemark())) {
            queryWrapper.lambda().and(i -> i.like(TProType::getRemark, tProType.getRemark()));
        }
        if (tProType.getParentId() != null && StringUtils.isNotBlank(tProType.getParentId())) {
            queryWrapper.lambda().and(i -> i.eq(TProType::getParentId, tProType.getParentId()));
        }
        if (tProType.getCreateId() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getCreateId, tProType.getCreateId()));
        }
        if (tProType.getCreateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getCreateTime, tProType.getCreateTime()));
        }
        if (tProType.getUpdateId() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getUpdateId, tProType.getUpdateId()));
        }
        if (tProType.getUpdateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getUpdateTime, tProType.getUpdateTime()));
        }
        if (tProType.getDeleteId() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getDeleteId, tProType.getDeleteId()));
        }
        if (tProType.getDeleteTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TProType::getDeleteTime, tProType.getDeleteTime()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TProType::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TProType::getDelFlag, 0));
        return queryWrapper;
    }
}
