package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TLisData;
import com.scmt.healthy.entity.TTestRecord;
import com.scmt.healthy.mapper.TTestRecordMapper;
import com.scmt.healthy.service.TTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 **/
@Service
public class TTestRecordServiceImpl extends ServiceImpl<TTestRecordMapper, TTestRecord> implements TTestRecordService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TTestRecordMapper tTestRecordMapper;

    @Override
    public IPage<TTestRecord> queryTTestRecordListByPage(TTestRecord  tTestRecord, SearchVo searchVo, PageVo pageVo){
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
        Page<TTestRecord> pageData = new Page<>(page, limit);
        QueryWrapper<TTestRecord> queryWrapper = new QueryWrapper<>();
        if (tTestRecord !=null) {
            queryWrapper = LikeAllField(tTestRecord,searchVo);
        }
        //默认创建时间倒序
        if (pageVo.getSort() != null) {
            if (pageVo.getSort().equals("asc")) {
                queryWrapper.orderByAsc("t_test_record." + pageVo.getSort());
            } else {
                queryWrapper.orderByDesc("t_test_record." + pageVo.getSort());
            }
        } else {
            queryWrapper.orderByDesc("t_test_record.create_time");
        }
        IPage<TTestRecord> result = tTestRecordMapper.selectPage(pageData, queryWrapper);
        return  result;
    }

    @Override
    public List<TTestRecord> queryTTestRecordList(TTestRecord  tTestRecord, SearchVo searchVo){
        QueryWrapper<TTestRecord> queryWrapper = new QueryWrapper<>();
        if (tTestRecord !=null) {
            queryWrapper = LikeAllField(tTestRecord,searchVo);
        }
        queryWrapper.orderByDesc("t_test_record.create_time");
        List<TTestRecord> result = tTestRecordMapper.selectList(queryWrapper);
        return  result;
    }

    /**
     * 功能描述：构建模糊查询
     * @param tTestRecord 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TTestRecord>  LikeAllField(TTestRecord  tTestRecord, SearchVo searchVo) {
        QueryWrapper<TTestRecord> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(tTestRecord.getId())){
            queryWrapper.and(i -> i.like("t_test_record.id", tTestRecord.getId()));
        }
        if(StringUtils.isNotBlank(tTestRecord.getUnitId())){
            queryWrapper.and(i -> i.like("t_test_record.unit_id", tTestRecord.getUnitId()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.lambda().and(i -> i.between(TTestRecord::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TTestRecord::getDelFlag, 0));
        return queryWrapper;

    }
}
