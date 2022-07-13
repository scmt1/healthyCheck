package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TDepartItemResult;
import com.scmt.healthy.entity.TDepartResult;
import com.scmt.healthy.entity.TReviewProject;
import com.scmt.healthy.entity.TTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 模板 Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2021-10-21
 */
public interface TTemplateMapper extends BaseMapper<TTemplate> {
	/**
	 *
	 * 使用MP提供的Wrapper条件构造器，
	 *
	 * @param userWrapper
	 * @return
	 */
	List<TTemplate> selectByMyWrapper(@Param(Constants.WRAPPER) Wrapper<TTemplate> userWrapper);

	/**
	 *
	 * 使用MP提供的Wrapper条件构造器（分页查询），
	 *
	 * @param userWrapper
	 * @return
	 */
	IPage<TTemplate> selectByMyWrapper(@Param(Constants.WRAPPER) Wrapper<TTemplate> userWrapper, @Param(value = "page") Page page);

	/**
	 * 根据用户id查询组合项目结果
	 * @param personId
	 * @return
	 */
	List<TDepartResult> getDepartResultList(@Param(value = "personId")String personId,@Param(value = "groupId")String groupId);

	/**
	 * 根据用户id查询组合项目结果
	 * @param personId
	 * @return
	 */
	List<TDepartItemResult> getDepartItemResultList(@Param(value = "personId")String personId,@Param(value = "groupId")String groupId);

	/**
	 * 根据用户id 集合查询组合项目结果
	 * @param personIds
	 * @return
	 */
	List<TDepartResult> getDepartResultListByPersonIds(@Param(value = "personIds") List<String> personIds,@Param(value = "groupIds") List<String> groupIds);

	/**
	 * 根据用户id集合查询组合项目结果
	 * @param personIds
	 * @return
	 */
	List<TDepartItemResult> getDepartItemResultListByPersonIds(@Param(value = "personIds")List<String> personIds,@Param(value = "groupIds")List<String> groupIds);
}
