package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.core.vo.RoleDTO;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.TGroupOrder;
import com.scmt.healthy.mapper.TGroupOrderMapper;
import com.scmt.healthy.service.ITGroupOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author
 **/
@Service
public class TGroupOrderServiceImpl extends ServiceImpl<TGroupOrderMapper, TGroupOrder> implements ITGroupOrderService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TGroupOrderMapper tGroupOrderMapper;

    @Autowired
    private SecurityUtil securityUtil;
    /**
     * socket配置
     */
    @Autowired
    public SocketConfig socketConfig;

    @Override
    public IPage<TGroupOrder> queryTGroupOrderListByPage(TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TGroupOrder> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (tGroupOrder != null) {
            queryWrapper = LikeAllFeild1(tGroupOrder, searchVo);
        }
        queryWrapper.orderByDesc("t_group_order.create_time");
        IPage<TGroupOrder> result = tGroupOrderMapper.queryGroupOrderListByPage(queryWrapper,pageData );
        return result;
    }

    @Override
    public IPage<TGroupOrder> queryTGroupOrderAppList(TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo, List<String> unitIds) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TGroupOrder> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (tGroupOrder != null) {
            String orderCode = "";
            if(tGroupOrder.getOrderCode()!=null && tGroupOrder.getOrderCode().trim().length()>0){
                orderCode = tGroupOrder.getOrderCode();
                tGroupOrder.setOrderCode(null);
            }
            queryWrapper = LikeAllFeild1(tGroupOrder, searchVo);
            if(orderCode.trim().length()>0){
                queryWrapper.like("order_code",orderCode);
            }
            if(unitIds!=null && unitIds.size()>0){
                queryWrapper.in("t_group_order.group_unit_id",unitIds);
            }
            queryWrapper.groupBy("t_group_order.id");
            queryWrapper.orderByDesc("t_group_order.create_time");
        }
        IPage<TGroupOrder> result = tGroupOrderMapper.queryGroupOrderAppListByPage(queryWrapper,pageData);
        return result;
    }


    /**
     * 功能描述：构建模糊查询
     *
     * @param tGroupOrder 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TGroupOrder> LikeAllFeild1(TGroupOrder tGroupOrder, SearchVo searchVo) {
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tGroupOrder.getId())) {
            queryWrapper.and(i -> i.eq("t_group_order.id", tGroupOrder.getId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getOrderCode())) {
            queryWrapper.and(i -> i.eq("t_group_order.order_code", tGroupOrder.getOrderCode()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getDepartmentId())) {
            queryWrapper.and(i -> i.eq("t_group_order.department_id", tGroupOrder.getDepartmentId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getGroupUnitId())) {
            queryWrapper.and(i -> i.like("t_group_order.group_unit_id", tGroupOrder.getGroupUnitId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getCreateId())) {
            queryWrapper.and(i -> i.eq("t_group_order.create_id", tGroupOrder.getCreateId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getGroupUnitName())) {
            queryWrapper.and(i -> i.like("t_group_order.group_unit_name", tGroupOrder.getGroupUnitName()));
        }

        if (StringUtils.isNotBlank(tGroupOrder.getPhysicalType())) {
            queryWrapper.and(i -> i.eq("t_group_order.physical_type", tGroupOrder.getPhysicalType()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getSalesDirector())) {
            queryWrapper.and(i -> i.eq("t_group_order.sales_director", tGroupOrder.getSalesDirector()));
        }
        if (tGroupOrder.getSigningTime() != null) {
            queryWrapper.and(i -> i.like("t_group_order.signing_time", tGroupOrder.getSigningTime()));
        }
        if (tGroupOrder.getDeliveryTime() != null) {
            queryWrapper.and(i -> i.like("t_group_order.delivery_time" ,tGroupOrder.getDeliveryTime()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getRemark())) {
            queryWrapper.and(i -> i.like("t_group_order.remark", tGroupOrder.getRemark()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getSalesParticipant())) {
            queryWrapper.and(i -> i.like("t_group_order.sales_participant", tGroupOrder.getSalesParticipant()));
        }
        if (tGroupOrder.getAuditState() != null) {
            if (tGroupOrder.getAuditState() == 99) {
                queryWrapper.and(i -> i.eq("t_group_order.audit_state", 2)
                        .or().eq("t_group_order.audit_state", 3));
            } else if(tGroupOrder.getAuditState() == 88){
                queryWrapper.and(i -> i.eq("t_group_order.audit_state", 0)
                        .or().eq("t_group_order.audit_state", 2));
            }
            else {
                queryWrapper.and(i -> i.eq("t_group_order.audit_state", tGroupOrder.getAuditState()));
            }
        }
        if (tGroupOrder.getPayStatus() != null) {
            queryWrapper.and(i -> i.eq("t_group_order.pay_status", tGroupOrder.getPayStatus()));
        }

        if (StringUtils.isNotBlank(tGroupOrder.getSearchKey())) {
            queryWrapper.and(i -> i.like("t_group_order.group_unit_name", tGroupOrder.getSearchKey()));
        }

        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("t_group_order.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            } else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {//当天
                String date = format.format(new Date());
                queryWrapper.apply(StringUtils.isNotBlank(searchVo.getStartDate()), "Date(t_group_order.create_time)=STR_TO_Date('" + date + "','%Y-%m-%d')");
            } else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {//当月
                queryWrapper.inSql("MONTH(t_group_order.create_time)", "MONTH(CURDATE())");
            }
        }
        queryWrapper.and(i -> i.eq("t_group_order.del_flag", 0));
        return queryWrapper;
    }

    @Override
    public IPage<TGroupOrder> queryApproveTGroupOrderList(String auditUserId, TGroupOrder tGroupOrder, SearchVo searchVo, PageVo pageVo) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TGroupOrder> pageData = new Page<>(page, limit);
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();

        if (tGroupOrder != null) {
            queryWrapper = LikeAllFeild(tGroupOrder, searchVo,auditUserId);
        }
        //queryWrapper.inSql("id", "select group_order_id from t_order_flow where audit_user_id = '" + auditUserId + "' or (audit_state =3  and create_user_id = '" + auditUserId + "')");
        queryWrapper.groupBy("t_group_order.create_time");
        IPage<TGroupOrder> result = tGroupOrderMapper.queryApproveTGroupOrderList(queryWrapper, pageData);
        return result;
    }

    @Override
    public Map<String,Object> getComNameByGroupId(String groupId) {
        return tGroupOrderMapper.getComNameByGroupId(groupId);
    }


    @Override
    public void download(TGroupOrder tGroupOrder, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (tGroupOrder != null) {
            queryWrapper = LikeAllFeild(tGroupOrder, null,null);
        }
        List<TGroupOrder> list = tGroupOrderMapper.selectList(queryWrapper);
        for (TGroupOrder re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TGroupOrder> queryAllTGroupOrderList(TGroupOrder tGroupOrder) {
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (tGroupOrder != null) {
            queryWrapper = LikeAllFeild(tGroupOrder, null,null);
        }
        queryWrapper.orderByDesc("t_group_order.create_time");
        return tGroupOrderMapper.queryAllTGroupOrderList(queryWrapper);
    }

    @Override
    public TGroupOrder getOneByWhere(String departmentId) {
        return tGroupOrderMapper.getOneByWhere(departmentId);
    }

    /**
     * 获取当天最新的订单信息
     * @return
     */
    @Override
    public TGroupOrder getLastGroupOrderInfo() {
        TGroupOrder groupOrder = tGroupOrderMapper.getLastGroupOrderByOrderDateAndCheckOrgId();
        return groupOrder;
    }

    @Override
    public TGroupOrder getTGroupOrderNumByCreateId(String auditUserId,String physicalType) {
        if(socketConfig!=null) {
            List<RoleDTO> roles = securityUtil.getCurrUser().getRoles();
            if(roles == null || roles.size()==0){
                return null;
            }
            //主治医师
            if( StringUtils.isNotBlank(socketConfig.getAttendingPhysician()) &&  StringUtils.isNotBlank(socketConfig.getPhysicalDirector())){
                Boolean isAttendingPhysician = false;//是否有 主治医师角色权限
                Boolean isPhysicalDirector = false;//是否有 体检中心主任角色权限
                Boolean isTechnicalDirector = false;//是否有 技术负责人角色权限
                for(RoleDTO role :roles){
                    if(role!=null && StringUtils.isNotBlank(role.getId())&& role.getId().equals(socketConfig.getAttendingPhysician())){
                        isAttendingPhysician = true;
//                        return tGroupOrderMapper.getTGroupOrderNumByCreateId(auditUserId,physicalType);
                    }
                    if(role!=null && StringUtils.isNotBlank(role.getId())&& role.getId().equals(socketConfig.getPhysicalDirector())){
                        isPhysicalDirector = true;
//                        return tGroupOrderMapper.getTGroupOrderNum(auditUserId,physicalType);
                    }
                    if(role!=null && StringUtils.isNotBlank(role.getId())&& role.getId().equals(socketConfig.getTechnicalDirector())){
                        isTechnicalDirector = true;
//                        return tGroupOrderMapper.getTGroupOrderNumFinish(auditUserId,physicalType);
                    }
                }
                if(isAttendingPhysician && !isPhysicalDirector && !isTechnicalDirector){
                    //只有 主治医师角色权限
                    return tGroupOrderMapper.getTGroupOrderNumByCreateId(auditUserId,physicalType);
                }else if(!isAttendingPhysician && isPhysicalDirector && !isTechnicalDirector){
                    //只有 体检中心主任角色权限
                    return tGroupOrderMapper.getTGroupOrderNum(auditUserId,physicalType);
                }else if(!isAttendingPhysician && !isPhysicalDirector && isTechnicalDirector){
                    //只有 技术负责人角色权限
                    return tGroupOrderMapper.getTGroupOrderNumFinish(auditUserId,physicalType);
                }else if(isAttendingPhysician && isPhysicalDirector && !isTechnicalDirector){
                    //既有 主治医师角色权限 又有 体检中心主任角色权限
                    return tGroupOrderMapper.getTGroupOrderNumAndByCreateId(auditUserId,physicalType);
                }else if(isAttendingPhysician && isPhysicalDirector && isTechnicalDirector){
                    //三个审核权限都有
                    return tGroupOrderMapper.getTGroupOrderNumAll(auditUserId,physicalType);
                }else if(isAttendingPhysician && !isPhysicalDirector && isTechnicalDirector){
                    //既有 主治医师角色权限 又有 技术负责人角色权限
                    return tGroupOrderMapper.getTGroupOrderNumFinishAndByCreateId(auditUserId,physicalType);
                }else if(!isAttendingPhysician && isPhysicalDirector && isTechnicalDirector){
                    //既有 体检中心主任角色权限 又有 技术负责人角色权限
                    return tGroupOrderMapper.getTGroupOrderNumAndFinish(auditUserId,physicalType);
                }
            }
            //其他人不要查询
            return null;

        }
        //其他人不要查询
        else {
            return null;
        }

    }

    @Override
    public Map<String, Object> getTGroupOrderByIdWithLink(String id) {
        return tGroupOrderMapper.getTGroupOrderByIdWithLink(id);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tGroupOrder 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TGroupOrder> LikeAllFeild(TGroupOrder tGroupOrder, SearchVo searchVo,String auditUserId) {
        QueryWrapper<TGroupOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tGroupOrder.getId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupOrder::getId, tGroupOrder.getId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getOrderCode())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupOrder::getOrderCode, tGroupOrder.getOrderCode()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getDepartmentId())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupOrder::getDepartmentId, tGroupOrder.getDepartmentId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getGroupUnitId())) {
            queryWrapper.lambda().and(i -> i.like(TGroupOrder::getGroupUnitId, tGroupOrder.getGroupUnitId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getCreateId())) {
            queryWrapper.and(i -> i.eq("t_group_order.create_id", tGroupOrder.getCreateId()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getGroupUnitName())) {
            queryWrapper.and(i -> i.like("t_group_order.group_unit_name", tGroupOrder.getGroupUnitName()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getCompanyName())) {
            queryWrapper.and(i -> i.like("t_group_unit.name", tGroupOrder.getCompanyName()));
        }

        if (StringUtils.isNotBlank(tGroupOrder.getPhysicalType())) {
            queryWrapper.and(i -> i.like("t_group_order.physical_type", tGroupOrder.getPhysicalType()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getSalesDirector())) {
            queryWrapper.lambda().and(i -> i.eq(TGroupOrder::getSalesDirector, tGroupOrder.getSalesDirector()));
        }
        if (tGroupOrder.getSigningTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupOrder::getSigningTime, tGroupOrder.getSigningTime()));
        }
        if (tGroupOrder.getDeliveryTime() != null) {
            queryWrapper.lambda().and(i -> i.like(TGroupOrder::getDeliveryTime, tGroupOrder.getDeliveryTime()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getRemark())) {
            queryWrapper.lambda().and(i -> i.like(TGroupOrder::getRemark, tGroupOrder.getRemark()));
        }
        if (StringUtils.isNotBlank(tGroupOrder.getSalesParticipant())) {
            queryWrapper.lambda().and(i -> i.like(TGroupOrder::getSalesParticipant, tGroupOrder.getSalesParticipant()));
        }
        if (tGroupOrder.getAuditState() != null)  {
            if(socketConfig!=null && StringUtils.isNotBlank(auditUserId)) {
                List<RoleDTO> roles = securityUtil.getCurrUser().getRoles();
                if(roles != null &&  roles.size()>0 && StringUtils.isNotBlank(socketConfig.getAttendingPhysician()) && StringUtils.isNotBlank(socketConfig.getPhysicalDirector())){
                    Boolean isAttendingPhysician = false;
                    Boolean isPhysicalDirector = false;
                    Boolean isTechnicalDirector = false;
                    //待检的
                    if(tGroupOrder.getAuditState()==1) {

                        //主治医师
                        List<String> auditStates = new ArrayList<>();//审核状态
                        for (RoleDTO role : roles) {
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getAttendingPhysician())) {
//                                queryWrapper.and(i -> i.eq("t_group_order.audit_state", 1));
                                auditStates.add("1");
                                isAttendingPhysician = true;
//                                break;
                            }
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getPhysicalDirector())) {
//                                queryWrapper.and(i -> i.in("t_group_order.audit_state", 2));
                                auditStates.add("2");
                                isPhysicalDirector = true;
//                                break;
                            }
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getTechnicalDirector())) {
//                                queryWrapper.and(i -> i.in("t_group_order.audit_state", 3));
                                auditStates.add("3");
                                isTechnicalDirector = true;
//                                break;
                            }
                        }
                        /*//中心主任
                        if (!isAttendingPhysician) {
                            for (RoleDTO role : roles) {
                                if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getPhysicalDirector())) {
                                    queryWrapper.and(i -> i.in("t_group_order.audit_state", 2));
                                    isPhysicalDirector = true;
                                    break;
                                }
                            }
                            //技术负责人
                            if (!isPhysicalDirector) {
                                for (RoleDTO role : roles) {
                                    if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getTechnicalDirector())) {
                                        queryWrapper.and(i -> i.in("t_group_order.audit_state", 3));
                                        isTechnicalDirector = true;
                                        break;
                                    }
                                }

                            }
                        }*/
                        //都不是
                        if (!isAttendingPhysician && !isPhysicalDirector && !isTechnicalDirector) {
                            queryWrapper.and(i -> i.eq("t_group_order.audit_state", 999));
                        }else{
                            queryWrapper.and(i -> i.in("t_group_order.audit_state", auditStates));
                        }
                    }
                    //已检的
                    else if(tGroupOrder.getAuditState()==99){
                        //主治医师
                        List<String> auditStates = new ArrayList<>();//审核状态
                        for (RoleDTO role : roles) {
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getAttendingPhysician())) {
//                                queryWrapper.and(i -> i.in("t_group_order.audit_state", 2,3,4))
                                isAttendingPhysician = true;
//                                break;
                            }
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getPhysicalDirector())) {
//                                queryWrapper.and(i -> i.in("t_group_order.audit_state", 3,4));
                                isPhysicalDirector = true;
//                                break;
                            }
                            if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getTechnicalDirector())) {
//                                queryWrapper.and(i -> i.in("t_group_order.audit_state", 4));
                                isTechnicalDirector = true;
//                                break;
                            }
                        }
                        /*//中心主任
                        if (!isAttendingPhysician) {
                            for (RoleDTO role : roles) {
                                if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getPhysicalDirector())) {
                                    queryWrapper.and(i -> i.in("t_group_order.audit_state", 3,4));
                                    isPhysicalDirector = true;
                                    break;
                                }
                            }
                            //技术负责人
                            if (!isPhysicalDirector) {
                                for (RoleDTO role : roles) {
                                    if (role != null && StringUtils.isNotBlank(role.getId()) && role.getId().equals(socketConfig.getTechnicalDirector())) {
                                        queryWrapper.and(i -> i.in("t_group_order.audit_state", 4));
                                        isTechnicalDirector = true;
                                        break;
                                    }
                                }

                            }
                        }*/
                        //都不是
                        if (!isAttendingPhysician && !isPhysicalDirector && !isTechnicalDirector) {
                            queryWrapper.and(i -> i.eq("t_group_order.audit_state", 999));
                        }else{
                            if(isAttendingPhysician && !isPhysicalDirector && !isTechnicalDirector){
                                //只有 主治医师角色权限
                                auditStates.add("2");
                                auditStates.add("3");
                                auditStates.add("4");
                            }else if(!isAttendingPhysician && isPhysicalDirector && !isTechnicalDirector){
                                //只有 体检中心主任角色权限
                                auditStates.add("3");
                                auditStates.add("4");
                            }else if(!isAttendingPhysician && !isPhysicalDirector && isTechnicalDirector){
                                //只有 技术负责人角色权限
                                auditStates.add("4");
                            }else if(isAttendingPhysician && isPhysicalDirector && !isTechnicalDirector){
                                //既有 主治医师角色权限 又有 体检中心主任角色权限
                                auditStates.add("3");
                                auditStates.add("4");
                            }else if(isAttendingPhysician && isPhysicalDirector && isTechnicalDirector){
                                //三个审核权限都有
                                auditStates.add("4");
                            }else if(isAttendingPhysician && !isPhysicalDirector && isTechnicalDirector){
                                //既有 主治医师角色权限 又有 技术负责人角色权限
                                auditStates.add("2");
                                auditStates.add("4");
                            }else if(!isAttendingPhysician && isPhysicalDirector && isTechnicalDirector){
                                //既有 体检中心主任角色权限 又有 技术负责人角色权限
                                auditStates.add("4");
                            }
                            queryWrapper.and(i -> i.in("t_group_order.audit_state", auditStates));
                        }

                    }
                    else {
                        queryWrapper.and(i -> i.eq("t_group_order.audit_state", 999));
                    }
                }
                //其他人不要查询
                else {
                    queryWrapper.and(i -> i.eq("t_group_order.audit_state", 999));
                }
            }
            //其他人不要查询
            else {
                queryWrapper.and(i -> i.eq("t_group_order.audit_state", 999));
            }
        }
        if (tGroupOrder.getPayStatus() != null) {
            queryWrapper.lambda().and(i -> i.eq(TGroupOrder::getPayStatus, tGroupOrder.getPayStatus()));
        }

        if (StringUtils.isNotBlank(tGroupOrder.getSearchKey())) {
            queryWrapper.and(i -> i.like("t_group_order.group_unit_name", tGroupOrder.getSearchKey()));
        }

        if (searchVo != null) {
            SimpleDateFormat format = new SimpleDateFormat();
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("t_group_order.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            } else if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isBlank(searchVo.getEndDate())) {//当天
                String date = format.format(new Date());
                queryWrapper.apply(StringUtils.isNotBlank(searchVo.getStartDate()), "Date(create_time)=STR_TO_Date('" + date + "','%Y-%m-%d')");
            } else if (StringUtils.isBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {//当月
                queryWrapper.inSql("MONTH(create_time)", "MONTH(CURDATE())");
            }
        }
        queryWrapper.and(i -> i.eq("t_group_order.del_flag", 0));
        return queryWrapper;
    }
}
