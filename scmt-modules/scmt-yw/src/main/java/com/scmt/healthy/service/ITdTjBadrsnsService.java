package com.scmt.healthy.service;

import com.scmt.healthy.entity.TdTjBadrsns;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 危害因素体检结论表 服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-02-17
 */
public interface ITdTjBadrsnsService extends IService<TdTjBadrsns> {
    /**
     * 功能描述：根据ids查询
     * @param ids
     * @return 返回获取结果
     */
    public List<TdTjBadrsns> selectListByIds(List<String> ids);
}
