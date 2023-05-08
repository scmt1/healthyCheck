package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.healthy.entity.TInterrogation;
import com.scmt.healthy.mapper.TInterrogationMapper;
import com.scmt.healthy.service.TInterrogationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *@author
 **/
@Service
public class TInterrogationServiceImpl extends ServiceImpl<TInterrogationMapper, TInterrogation> implements TInterrogationService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TInterrogationMapper tInterrogationMapper;


}
