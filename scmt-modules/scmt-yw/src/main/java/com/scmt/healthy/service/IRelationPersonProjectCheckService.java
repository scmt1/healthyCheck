package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.RelationPersonProjectCheck;
import com.scmt.healthy.entity.TOrderGroupItem;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author
 **/
public interface IRelationPersonProjectCheckService extends IService<RelationPersonProjectCheck> {

    /**
     * 功能描述：实现分页查询
     *
     * @param relationPersonProjectCheck 需要模糊查询的信息
     * @param searchVo                   排序参数
     * @param pageVo                     分页参数
     * @return 返回获取结果
     */
    public IPage<RelationPersonProjectCheck> queryRelationPersonProjectCheckListByPage(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现查询全部
     *
     * @param relationPersonProjectCheck 需要模糊查询的信息
     * @param searchVo                   排序参数
     * @return 返回获取结果
     */
    public List<RelationPersonProjectCheck> queryRelationPersonProjectCheckListAll(RelationPersonProjectCheck relationPersonProjectCheck, SearchVo searchVo);

    /**
     * 功能描述： 导出
     *
     * @param relationPersonProjectCheck 查询参数
     * @param response                   response参数
     */
    public void download(RelationPersonProjectCheck relationPersonProjectCheck, HttpServletResponse response);

    List<TOrderGroupItem> getNoRegistProjectData(String personId, List<String> deparmentIds);

    List<TOrderGroupItem> getNoRegistProjectDataReview(String personId, List<String> deparmentIds);

}
