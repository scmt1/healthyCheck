package com.scmt.healthy.service;

import com.scmt.healthy.entity.TEmploymentToken;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-04-07
 */
public interface ITTokenService extends IService<TEmploymentToken> {

    /**
     * 获取从业体检网报上传token
     */
    Boolean getToken();

}
