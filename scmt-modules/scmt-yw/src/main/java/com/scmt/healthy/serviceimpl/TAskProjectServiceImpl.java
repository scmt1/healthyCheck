package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.healthy.entity.TAskProject;
import com.scmt.healthy.mapper.TAskProjectMapper;
import com.scmt.healthy.service.ITAskProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author
 **/
@Service
public class TAskProjectServiceImpl extends ServiceImpl<TAskProjectMapper, TAskProject> implements ITAskProjectService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TAskProjectMapper tAskProjectMapper;


}
