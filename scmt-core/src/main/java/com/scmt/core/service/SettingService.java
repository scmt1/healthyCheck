package com.scmt.core.service;

import com.scmt.core.entity.Setting;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;


/**
 * 配置接口
 * @author Exrick
 */
@CacheConfig(cacheNames = "setting")
public interface SettingService {

    /**
     * 通过id获取
     * @param id
     * @return
     */
    @Cacheable(key = "#id")
    Setting get(String id);

    /**
     * 修改
     * @param setting
     * @return
     */
    @CacheEvict(key = "#setting.id")
    Setting saveOrUpdate(Setting setting);
}
