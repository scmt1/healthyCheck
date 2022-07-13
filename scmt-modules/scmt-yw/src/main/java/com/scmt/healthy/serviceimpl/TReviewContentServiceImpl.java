package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.healthy.entity.TReviewContent;
import com.scmt.healthy.mapper.TReviewContentMapper;
import com.scmt.healthy.service.TReviewContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *@author
 **/
@Service
public class TReviewContentServiceImpl extends ServiceImpl<TReviewContentMapper, TReviewContent> implements TReviewContentService {
	@Autowired
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TReviewContentMapper tReviewContentMapper;


}
