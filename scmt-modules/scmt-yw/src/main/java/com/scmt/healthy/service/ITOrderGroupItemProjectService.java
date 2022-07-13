package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOrderGroupItem;
import com.scmt.healthy.entity.TOrderGroupItemProject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author
 **/
public interface ITOrderGroupItemProjectService extends IService<TOrderGroupItemProject> {

    /**
     * 功能描述：实现分页查询
     *
     * @param tOrderGroupItemProject 需要模糊查询的信息
     * @param searchVo               排序参数
     * @param pageVo                 分页参数
     * @return 返回获取结果
     */
    public IPage<TOrderGroupItemProject> queryTOrderGroupItemProjectListByPage(TOrderGroupItemProject tOrderGroupItemProject, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述： 导出
     *
     * @param tOrderGroupItemProject 查询参数
     * @param response               response参数
     */
    public void download(TOrderGroupItemProject tOrderGroupItemProject, HttpServletResponse response);

    /**
     * 功能描述： 查询未体检项目数据
     *
     * @param personId     人员id
     */
    public List<TOrderGroupItem> queryNoCheckTOrderGroupItemProjectList(String personId, String groupId);

    /**
     * 功能描述： 根据复检表的基础项目id和分组id查询基础项目
     *
     * @param portfolioId 订单id
     * @param groupId     人员id
     */
    List<TOrderGroupItemProject> getOrderGroupITemProjectByReview(String portfolioId, String groupId, List<String> officeId);

    /**
     * 功能描述： 查询弃检项目数据
     *
     * @param personId     人员id
     */
    public List<TOrderGroupItem> queryAbandonTOrderGroupItemProjectList(String personId, String groupId);
}
