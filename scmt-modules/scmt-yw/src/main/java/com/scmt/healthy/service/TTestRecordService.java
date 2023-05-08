package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TTestRecord;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface TTestRecordService extends IService<TTestRecord> {


    /**
     * 功能描述：实现分页查询
     * @param tTestRecord 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TTestRecord> queryTTestRecordListByPage(TTestRecord tTestRecord, SearchVo searchVo, PageVo pageVo);


    /**
     * 功能描述：查询
     * @param tTestRecord 需要模糊查询的信息
     * @param searchVo 排序参数
     * @return 返回获取结果
     */
    public List<TTestRecord> queryTTestRecordList(TTestRecord tTestRecord, SearchVo searchVo);
}
