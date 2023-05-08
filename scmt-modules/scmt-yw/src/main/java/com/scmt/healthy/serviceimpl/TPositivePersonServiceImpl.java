package com.scmt.healthy.serviceimpl;

import com.scmt.healthy.entity.TPositivePerson;
import com.scmt.healthy.mapper.TPositivePersonMapper;
import com.scmt.healthy.service.ITPositivePersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-16
 */
@Service
public class TPositivePersonServiceImpl extends ServiceImpl<TPositivePersonMapper, TPositivePerson> implements ITPositivePersonService {

    @Autowired
    private TPositivePersonMapper tPositivePersonMapper;

    @Override
    public List<TPositivePerson> getByPersonId(String personId) {
        return  tPositivePersonMapper.getByPersonId(personId) ;
    }
}
