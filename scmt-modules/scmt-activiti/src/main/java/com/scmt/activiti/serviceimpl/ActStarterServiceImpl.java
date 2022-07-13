package com.scmt.activiti.serviceimpl;

import com.scmt.activiti.dao.ActStarterDao;
import com.scmt.activiti.entity.ActStarter;
import com.scmt.activiti.service.ActStarterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 流程可发起人接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class ActStarterServiceImpl implements ActStarterService {

    @Autowired
    private ActStarterDao actStarterDao;

    @Override
    public ActStarterDao getRepository() {
        return actStarterDao;
    }

    @Override
    public Set<String> findByUserId(String userId) {

        HashSet<String> starters = new HashSet<>();
        List<ActStarter> list = actStarterDao.findByUserId(userId);
        for (ActStarter actStarter : list) {
            starters.add(actStarter.getProcessDefId());
        }
        return starters;
    }

    @Override
    public Boolean hasRecord(String processDefId, String userId) {

        List<ActStarter> listNode = actStarterDao.findByProcessDefIdAndUserId(processDefId, userId);
        if (listNode != null && listNode.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteByProcessDefId(String processDefId) {

        actStarterDao.deleteByProcessDefId(processDefId);
    }
}
