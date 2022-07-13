package com.scmt.core.service;

import com.scmt.core.base.ScmtBaseService;
import com.scmt.core.entity.RolePermission;

import java.util.List;

/**
 * 角色权限接口
 * @author Exrick
 */
public interface RolePermissionService extends ScmtBaseService<RolePermission, String> {

    /**
     * 通过permissionId获取
     * @param permissionId
     * @return
     */
    List<RolePermission> findByPermissionId(String permissionId);

    /**
     * 通过roleId获取
     * @param roleId
     * @return
     */
    List<RolePermission> findByRoleId(String roleId);

    /**
     * 通过roleId删除
     * @param roleId
     */
    void deleteByRoleId(String roleId);
}
