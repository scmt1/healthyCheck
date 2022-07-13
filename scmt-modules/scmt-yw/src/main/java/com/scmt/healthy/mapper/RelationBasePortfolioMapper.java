package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.RelationBasePortfolio;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-08
 */
public interface RelationBasePortfolioMapper extends BaseMapper<RelationBasePortfolio> {

    ArrayList<String> queryBaseProjectIdList(String portfolioId);
}
