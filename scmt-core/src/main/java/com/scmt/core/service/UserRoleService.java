package com.scmt.core.service;


import com.scmt.core.base.ScmtBaseService;
import com.scmt.core.entity.User;
import com.scmt.core.entity.UserRole;

import java.util.List;

/**
 * 用户角色接口
 * @author Exrickx
 */
public interface UserRoleService extends ScmtBaseService<UserRole, String> {

    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 通过roleId查找用户
     * @param roleId
     * @return
     */
    List<User> findUserByRoleId(String roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    void deleteByUserId(String userId);
}
