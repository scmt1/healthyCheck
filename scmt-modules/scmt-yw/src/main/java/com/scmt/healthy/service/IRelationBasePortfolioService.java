package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.RelationBasePortfolio;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 **/
public interface IRelationBasePortfolioService extends IService<RelationBasePortfolio> {

    /**
     * 功能描述：实现分页查询
     *
     * @param relationBasePortfolio 需要模糊查询的信息
     * @param searchVo              排序参数
     * @param pageVo                分页参数
     * @return 返回获取结果
     */
    public IPage<RelationBasePortfolio> queryRelationBasePortfolioListByPage(RelationBasePortfolio relationBasePortfolio, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述： 导出
     *
     * @param relationBasePortfolio 查询参数
     * @param response              response参数
     */
    public void download(RelationBasePortfolio relationBasePortfolio, HttpServletResponse response);

    ArrayList<String> queryBaseProjectIdList(String portfolioId);
}
