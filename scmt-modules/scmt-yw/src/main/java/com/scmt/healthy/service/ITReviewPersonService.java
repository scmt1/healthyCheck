package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TReviewPerson;

/**
 *@author
 **/
public interface ITReviewPersonService extends IService<TReviewPerson> {

    /**
     * 功能描述：实现分页查询
     * @param tReviewPerson 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TReviewPerson> queryTReviewPersonListByPage(TReviewPerson  tReviewPerson, SearchVo searchVo, PageVo pageVo);


    /**
     * 功能描述：分页查询复查人员信息及检查项目
     *
     * @param searchVo 需要模糊查询的信息
     * @param pageVo   分页参数
     * @return 返回获取结果
     */
    IPage<TReviewPerson> getReviewProjectPerson(TReviewPerson tReviewPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：根据id查询复查人员信息
     *
     * @param id 需要模糊查询的信息
     * @return 返回获取结果
     */
    TReviewPerson getReviewPersonById(String id);

}
