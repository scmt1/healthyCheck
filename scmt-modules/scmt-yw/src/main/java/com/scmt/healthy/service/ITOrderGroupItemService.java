package com.scmt.healthy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public interface ITOrderGroupItemService extends IService<TOrderGroupItem> {

    /**
     * 功能描述：实现分页查询
     *
     * @param tOrderGroupItem 需要模糊查询的信息
     * @param searchVo        排序参数
     * @param pageVo          分页参数
     * @return 返回获取结果
     */
    public IPage<TOrderGroupItem> queryTOrderGroupItemListByPage(TOrderGroupItem tOrderGroupItem, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述： 查询订单体检项目数据
     *
     * @param groupOrderId 订单id
     */
    public List<TOrderGroupItem> queryOrderGroupItemList(String groupOrderId, String groupId);

    /**
     * 功能描述： 导出
     *
     * @param tOrderGroupItem 查询参数
     * @param response        response参数
     */
    public void download(TOrderGroupItem tOrderGroupItem, HttpServletResponse response);

    List<TOrderGroupItem> queryDataListByGroupId(QueryWrapper<TOrderGroupItem> queryWrapper, String personId);

    //获取分组下边所有的分组项
    Integer getAllCheckCount(String personId, String groupId);

    //获取检查结果中已经检查的项目
    Integer getDepartResultCount(String personId, String groupId);

    List<TOrderGroupItem> listByQueryWrapper(TOrderGroupItem orderGroupItem);
}
