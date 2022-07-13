package com.scmt.core.serviceimpl;

import com.scmt.core.common.constant.CommonConstant;
import com.scmt.core.dao.UserDao;
import com.scmt.core.dao.UserRoleDao;
import com.scmt.core.entity.User;
import com.scmt.core.entity.UserRole;
import com.scmt.core.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色接口实现
 * @author Exrickx
 */
@Slf4j
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserDao userDao;

    @Override
    public UserRoleDao getRepository() {
        return userRoleDao;
    }

    @Override
    public List<UserRole> findByRoleId(String roleId) {
        return userRoleDao.findByRoleId(roleId);
    }

    @Override
    public List<User> findUserByRoleId(String roleId) {

        List<UserRole> userRoleList = userRoleDao.findByRoleId(roleId);
        List<User> list = new ArrayList<>();
        for (UserRole ur : userRoleList) {
            User u = userDao.findById(ur.getUserId()).orElse(null);
            if (u != null && CommonConstant.USER_STATUS_NORMAL.equals(u.getStatus())) {
                list.add(u);
            }
        }
        return list;
    }

    @Override
    public void deleteByUserId(String userId) {
        userRoleDao.deleteByUserId(userId);
    }
}
