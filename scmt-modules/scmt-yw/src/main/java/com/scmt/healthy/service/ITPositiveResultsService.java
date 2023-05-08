package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TPositiveResults;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-14
 */
public interface ITPositiveResultsService extends IService<TPositiveResults> {

    /**
     * 功能描述：实现分页查询
     * @param tPositiveResults 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    IPage<TPositiveResults> querytPositiveResultList(TPositiveResults  tPositiveResults, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现查询
     * @param tPositiveResults 需要模糊查询的信息
     * @param searchVo 排序参数
     * @return 返回获取结果
     */
    List<TPositiveResults> getPositiveResultList(TPositiveResults  tPositiveResults, SearchVo searchVo);

    TPositiveResults getByName(String name);
}
