package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TBarcode;
import com.scmt.healthy.service.ITBarcodeService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TBarcodeMapper;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

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
public class TBarcodeServiceImpl extends ServiceImpl<TBarcodeMapper, TBarcode> implements ITBarcodeService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TBarcodeMapper tBarcodeMapper;

    @Override
    public IPage<TBarcode> queryTBarcodeListByPage(TBarcode tBarcode, SearchVo searchVo, PageVo pageVo) {
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
        Page<TBarcode> pageData = new Page<>(page, limit);
        QueryWrapper<TBarcode> queryWrapper = new QueryWrapper<>();
        if (tBarcode != null) {
            queryWrapper = LikeAllField(tBarcode, searchVo);
        }
        IPage<TBarcode> result = tBarcodeMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TBarcode tBarcode, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TBarcode> queryWrapper = new QueryWrapper<>();
        if (tBarcode != null) {
            queryWrapper = LikeAllField(tBarcode, null);
        }
        List<TBarcode> list = tBarcodeMapper.selectList(queryWrapper);
        for (TBarcode re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public TBarcode getOneByWhere() {
        return tBarcodeMapper.getOneByWhere();
    }

    @Override
    public TBarcode getOneByTestNum() {
        return tBarcodeMapper.getOneByTestNum();
    }

    @Override
    public TBarcode getTBarcodeByPersonId(String personId,String testNum) {
        return tBarcodeMapper.getTBarcodeByPersonId(personId,testNum);
    }

    @Override
    public TBarcode getTBarcodeByPersonIdAndItemId(String personId,String groupItemId,String testNum) {
        return tBarcodeMapper.getTBarcodeByPersonIdAndItemId(personId,groupItemId,testNum);
    }

    @Override
    public int checkBarcodeExists(String barcode) {
        return tBarcodeMapper.checkBarcodeExists(barcode);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tBarcode 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TBarcode> LikeAllField(TBarcode tBarcode, SearchVo searchVo) {
        QueryWrapper<TBarcode> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tBarcode.getId())) {
            queryWrapper.lambda().and(i -> i.like(TBarcode::getId, tBarcode.getId()));
        }
        if (StringUtils.isNotBlank(tBarcode.getPersonId())) {
            queryWrapper.lambda().and(i -> i.like(TBarcode::getPersonId, tBarcode.getPersonId()));
        }
        if (StringUtils.isNotBlank(tBarcode.getGroupItemId())) {
            queryWrapper.lambda().and(i -> i.like(TBarcode::getGroupItemId, tBarcode.getGroupItemId()));
        }
        if (StringUtils.isNotBlank(tBarcode.getBarcode())) {
            queryWrapper.lambda().and(i -> i.like(TBarcode::getBarcode, tBarcode.getBarcode()));
        }
        if (tBarcode.getCreateTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TBarcode::getCreateTime, tBarcode.getCreateTime()));
        }
        if (searchVo != null) {
            if (searchVo.getStartDate() != null && searchVo.getEndDate() != null) {
                queryWrapper.lambda().and(i -> i.between(TBarcode::getCreateTime, searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;
    }
}
