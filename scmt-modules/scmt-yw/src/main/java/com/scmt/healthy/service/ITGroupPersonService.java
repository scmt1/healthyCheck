package com.scmt.healthy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TGroupPerson;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author
 **/
public interface ITGroupPersonService extends IService<TGroupPerson> {

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryTGroupPersonListByPage(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryTGroupPersonAndResultList(TGroupPerson  tGroupPerson, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryTGroupPersonAndResultAppList(TGroupPerson  tGroupPerson, PageVo pageVo);

    /**
     * 功能描述： 导出
     * @param tGroupPerson 查询参数
     * @param response response参数
     */
    public void download(TGroupPerson  tGroupPerson, HttpServletResponse response) ;

    /**
     * 医生科室对应 体检人员 分页
     * @param officeId
     * @return
     */
    IPage<TGroupPerson> getPersonByOfficeId(List<String> officeId, TGroupPerson groupPerson,PageVo pageVo,SearchVo searchVo);

    /**
     * 根据id获取团检人员关联信息
     * @param id
     * @return
     */
    Map<String, Object> getGroupPersonByIdWithLink(String id);

    /**
     * 根据订单id获取团检人员关联信息
     * @param orderId
     * @return
     */
    List<TGroupPerson> getTGroupPersonByOrderId(String orderId);

    /**
     * 根据id和状态获取团检人员关联信息
     * @param id
     * @return
     */
    Map<String, Object> getGroupPersonInfo(String id, String type);

    /**
     * 功能描述：实现分页查询
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> getTGroupPersonInspection(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> getInspectionTGroupPersonList(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> getInspectionTGroupPersonReviewList(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryNoCheckProjectPersonList(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @return 返回获取结果
     */
    public TGroupPerson getPersonListNum(List<String> orderIdList,String physicalType);

    /**
     * 功能描述：通过分组id查询团检人员数量
     * @return 返回获取结果
     */
    public TGroupPerson getPersonNumByGroupId(String groupId);

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryStatisticPersonList(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    /**
     * 功能描述：实现分页查询
     * @param tGroupPerson 需要模糊查询的信息
     * @param searchVo 排序参数
     * @param pageVo 分页参数
     * @return 返回获取结果
     */
    public IPage<TGroupPerson> queryExamineFinishPersonList(TGroupPerson  tGroupPerson, SearchVo searchVo, PageVo pageVo);

    List<TGroupPerson> queryPersonDataListByOrderId(String orderId);


    /**
     * 根据ids获取团检人员关联信息
     * @param ids
     * @return
     */
    List<Map<String, Object>> getGroupPersonInfoByIds(List<String> ids);

    /**
     * 查询对应科室的复查人员
     * @param officeId
     * @param tGroupPerson
     * @param pageVo
     * @param searchVo
     * @return
     */
	IPage<TGroupPerson> getPersonReviewerByOfficeId(List<String> officeId, TGroupPerson tGroupPerson, PageVo pageVo, SearchVo searchVo);

    /**
     * 更新问诊签名
     */
    Integer updatewAutograph();

    TGroupPerson queryTGroupPersonAndResultApp(TGroupPerson tGroupPerson);
}
