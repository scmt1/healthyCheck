package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.healthy.entity.TCombo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scmt.healthy.entity.TGroupPerson;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mike
 * @since 2021-10-12
 */
public interface TComboMapper extends BaseMapper<TCombo> {

    public TCombo getTComboByPersonId(@Param("personId") String personId,@Param("hazardFactors") String hazardFactors,@Param("content") String content);

    public List<TCombo> gethazardFactorsByGroupId(@Param("groupId") String groupId);

    IPage<TCombo> queryTComboAndItemList(@Param(Constants.WRAPPER) QueryWrapper<TCombo> queryWrapper, @Param("page") Page page);

    List<TCombo>  tComboMapper(String id);

    TCombo getTComboById(String id);

    List<TCombo> getTComboItem(String id);

    Integer findItemPrice(String itemId);
}
