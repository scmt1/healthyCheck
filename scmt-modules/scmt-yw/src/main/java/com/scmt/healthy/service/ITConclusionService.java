package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TConclusion;
import com.scmt.healthy.entity.TLisData;

import java.util.List;

/**
 * @author
 **/
public interface ITConclusionService extends IService<TConclusion> {

    IPage<TConclusion> queryTConclusionDataListByPage(TConclusion tConclusion, SearchVo searchVo, PageVo pageVo);
}
