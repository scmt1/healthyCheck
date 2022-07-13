package com.scmt.core.service.mybatis;

import com.scmt.core.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Exrickx
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    List<Permission> findByUserId(String userId);
}
