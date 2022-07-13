package com.scmt.activiti.serviceimpl.mybatis;

import com.scmt.activiti.dao.mapper.RunIdentityMapper;
import com.scmt.activiti.service.mybatis.IRunIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Exrickx
 */
@Service
public class IRunIdentityServiceImpl implements IRunIdentityService {

    @Autowired
    private RunIdentityMapper runIdentityMapper;

    @Override
    public List<String> selectByConditions(String taskId, String type) {
        return runIdentityMapper.selectByConditions(taskId, type);
    }
}
