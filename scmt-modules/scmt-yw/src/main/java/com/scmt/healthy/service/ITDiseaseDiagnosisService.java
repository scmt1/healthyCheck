package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TDiseaseDiagnosis;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dengjie
 * @since 2023-03-09
 */
public interface ITDiseaseDiagnosisService extends IService<TDiseaseDiagnosis> {
    /**
     * 功能描述：实现分页查询
     * @param tDiseaseDiagnosis 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TDiseaseDiagnosis> queryTDiseaseDiagnosisListByPage(TDiseaseDiagnosis  tDiseaseDiagnosis, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述： 导出
     * @param tDiseaseDiagnosis 查询参数
     * @param response response参数
     */
    public void download(TDiseaseDiagnosis  tDiseaseDiagnosis, HttpServletResponse response) ;



}
