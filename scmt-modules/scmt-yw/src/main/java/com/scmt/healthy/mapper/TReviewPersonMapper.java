package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TGroupOrder;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.entity.TReviewPerson;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-12
 */
public interface TReviewPersonMapper extends BaseMapper<TReviewPerson> {

	/**
	 * 查询复查人员信息及检查项目
	 * @param queryWrapper 查询条件
	 * @param pageData 分页条件
	 * @return  返回查询到的 复查人员信息及检查项目
	 */
	IPage<TReviewPerson> getReviewProjectPerson(@Param(Constants.WRAPPER)QueryWrapper<TReviewPerson> queryWrapper, @Param("page")Page pageData);

	TReviewPerson getReviewPersonById(@Param(Constants.WRAPPER)QueryWrapper<TReviewPerson> queryWrapper);
}
