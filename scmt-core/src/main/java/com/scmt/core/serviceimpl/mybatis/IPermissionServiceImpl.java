package com.scmt.core.serviceimpl.mybatis;

import com.scmt.core.dao.mapper.PermissionMapper;
import com.scmt.core.entity.Permission;
import com.scmt.core.service.mybatis.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Exrickx
 */
@Service
public class IPermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> findByUserId(String userId) {

        return permissionMapper.findByUserId(userId);
    }
}
