package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TPositivePerson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2023-02-16
 */
public interface TPositivePersonMapper extends BaseMapper<TPositivePerson> {

    List<TPositivePerson> getByPersonId(String personId);
}
