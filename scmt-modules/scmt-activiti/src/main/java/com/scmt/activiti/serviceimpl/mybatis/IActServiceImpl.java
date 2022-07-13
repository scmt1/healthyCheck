package com.scmt.activiti.serviceimpl.mybatis;

import com.scmt.activiti.dao.mapper.ActMapper;
import com.scmt.activiti.service.mybatis.IActService;
import com.scmt.core.common.exception.ScmtException;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Exrickx
 */
@Service
public class IActServiceImpl implements IActService {

    @Autowired
    private ActMapper actMapper;

    @Override
    public Integer deleteBusiness(String table, String id) {

        if (StrUtil.isBlank(table) || StrUtil.isBlank(id)) {
            throw new ScmtException("关联业务表名或id为空");
        }
        return actMapper.deleteBusiness(table, id);
    }
}
