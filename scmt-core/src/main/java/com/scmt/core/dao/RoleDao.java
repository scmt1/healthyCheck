package com.scmt.core.dao;

import com.scmt.core.base.ScmtBaseDao;
import com.scmt.core.entity.Role;

import java.util.List;

/**
 * 角色数据处理层
 * @author Exrickx
 */
public interface RoleDao extends ScmtBaseDao<Role, String> {

    /**
     * 获取默认角色
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);
}
