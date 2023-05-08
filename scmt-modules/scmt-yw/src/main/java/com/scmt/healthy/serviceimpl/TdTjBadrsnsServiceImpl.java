package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TdTjBadrsns;
import com.scmt.healthy.entity.TdTjBhk;
import com.scmt.healthy.mapper.TdTjBadrsnsMapper;
import com.scmt.healthy.mapper.TdTjBhkMapper;
import com.scmt.healthy.service.ITdTjBadrsnsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 危害因素体检结论表 服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-17
 */
@Service
public class TdTjBadrsnsServiceImpl extends ServiceImpl<TdTjBadrsnsMapper, TdTjBadrsns> implements ITdTjBadrsnsService {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TdTjBadrsnsMapper tdTjBadrsnsMapper;

    @Override
    public List<TdTjBadrsns> selectListByIds(List<String> ids) {
        List<TdTjBadrsns> result = tdTjBadrsnsMapper.selectListByIds(ids);
        return result;
    }
}
