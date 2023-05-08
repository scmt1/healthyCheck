package com.scmt.healthy.service;

import com.scmt.healthy.entity.TPositivePerson;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-16
 */
public interface ITPositivePersonService extends IService<TPositivePerson> {

    List<TPositivePerson> getByPersonId(String personId);

}
