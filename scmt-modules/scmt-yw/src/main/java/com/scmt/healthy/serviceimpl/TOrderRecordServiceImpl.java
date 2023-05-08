package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.mapper.*;
import com.scmt.healthy.service.ITOrderRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *@author
 **/
@Service
public class TOrderRecordServiceImpl extends ServiceImpl<TOrderRecordMapper, TOrderRecord> implements ITOrderRecordService {

	@SuppressWarnings("SpringJavaAutowiringInspection")

	@Autowired
	private TOrderRecordMapper tOrderRecordMapper;

	@Resource
	private TComboMapper tComboMapper;

	@Resource
	private TCheckOrgMapper tCheckOrgMapper;

	@Resource
	private TGroupPersonMapper tGroupPersonMapper;

	@Resource
	private TGroupOrderMapper tGroupOrderMapper;

	@Override
	public IPage<TOrderRecord> queryTOrderRecordListByPage(TOrderRecord  tOrderRecord, SearchVo searchVo, PageVo pageVo){
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
		Page<TOrderRecord> pageData = new Page<>(page, limit);
		QueryWrapper<TOrderRecord> queryWrapper = new QueryWrapper<>();
		if (tOrderRecord !=null) {
			queryWrapper = LikeAllField(tOrderRecord,searchVo);
			queryWrapper.orderByDesc("t_order_record.create_time");
		}
		IPage<TOrderRecord> result = tOrderRecordMapper.selectRecordInfoPage(queryWrapper,pageData,tOrderRecord.getPersonName(),tOrderRecord.getPhysicalType(),tOrderRecord.getCheckDate(),tOrderRecord.getIsPass());
		return  result;
	}
	@Override
	public void download(TOrderRecord tOrderRecord, HttpServletResponse response,SearchVo searchVo, PageVo pageVo) {
		List<Map<String, Object>> mapList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TOrderRecord> list = queryTOrderRecordListByPage(tOrderRecord, searchVo, pageVo).getRecords();
		for (TOrderRecord re : list) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("体检人员姓名", re.getTGroupPerson().getPersonName());
			map.put("性别", re.getTGroupPerson().getSex());
			map.put("年龄", re.getTGroupPerson().getAge());
			map.put("手机号码", re.getTGroupPerson().getMobile());
			map.put("身份证号码",re.getTGroupPerson().getIdCard());
			map.put("体检机构名字", re.getTCheckOrg().getName());
			map.put("预约状态", re.getOrderStatus() == 0 ? "已到场" : "未到场");
			map.put("体检状态", re.getCheckStatus() == 0 ? "体检完成" : "体检未完成");
			map.put("预约时间", simpleDateFormat.format(re.getOrderDate()));
			map.put("体检类型", re.getType());
			mapList.add(map);
		}
		System.out.println(mapList);
		FileUtil.createExcel(mapList, "exel.xlsx", response);
	}

	/**
	 * 根据订单id查询对应的预约记录信息
	 * @param orderId
	 * @return
	 */
	@Override
	public Map<String, Object> getOrderRecordInfoByOrderId(String orderId) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> map  = new HashMap<>();
		//获取订单信息
		Map<String,Object> groupOrderInfo = new HashMap<>();
		TGroupOrder groupOrder = tGroupOrderMapper.selectById(orderId);
		if(groupOrder != null){
			groupOrderInfo.put("groupOrderId",groupOrder.getId());
			groupOrderInfo.put("orderCode",groupOrder.getOrderCode());
			groupOrderInfo.put("signingTime",simpleDateFormat.format(groupOrder.getSigningTime()));
			groupOrderInfo.put("createTime",simpleDateFormat1.format(groupOrder.getCreateTime()));
			map.put("groupOrderInfo",groupOrderInfo);
		}
		//根据订单id获取对应的预约记录
		List<TOrderRecord> orderRecords = tOrderRecordMapper.selectOrderRecordByOrderId(orderId);
		//获取机构信息
		Map<String,Object>  checkOrgInfo = new HashMap<>();
		TCheckOrg checkOrg = tCheckOrgMapper.selectById(orderRecords.get(0).getCheckOrgId());
		if (checkOrg != null){
			checkOrgInfo.put("checkOrgId",checkOrg.getId());
			checkOrgInfo.put("checkOrgName",checkOrg.getName());
			checkOrgInfo.put("checkOrgAddress",checkOrg.getAddress());
			map.put("checkOrgInfo",checkOrgInfo);
		}
		//获取套餐信息
		if(orderRecords.get(0).getComboName() != null){
			String comboNames = orderRecords.get(0).getComboName();
			//套餐名称不含指定子串
			if(comboNames.indexOf(";") == -1){
				List<Map<String,Object>> comboInfoList = new ArrayList<>();
				Map<String,Object> comboInfo = new HashMap();
				TCombo combo = tOrderRecordMapper.selectComboByName(comboNames);
				if(combo != null){
					List<String> comboItems = tOrderRecordMapper.selectTComboItemName(combo.getId());
					String comboItemNames = String.join(";",comboItems);

					Integer price = tComboMapper.selectTComboPriceById(combo.getId());
					comboInfo.put("comboId",combo.getId());
					comboInfo.put("comboName",comboNames);
					comboInfo.put("price",price);
					comboInfo.put("comboItemNames",comboItemNames);

					comboInfoList.add(comboInfo);
				}
				map.put("comboInfoList",comboInfoList);
			}else {
				List<Map<String,Object>> comboInfoList = new ArrayList<>();
				String[] comboNameList = comboNames.split(";");
				for (String name:comboNameList) {
					TCombo combo = tOrderRecordMapper.selectComboByName(name);
					if(combo != null){
						Map<String,Object> comboInfo = new HashMap();
						List<String> comboItems = tOrderRecordMapper.selectTComboItemName(combo.getId());
						String comboItemNames = String.join(";",comboItems);
						Integer price = tComboMapper.selectTComboPriceById(combo.getId());
						comboInfo.put("comboId",combo.getId());
						comboInfo.put("comboName",name);
						comboInfo.put("price",price);
						comboInfo.put("comboItemNames",comboItemNames);
						comboInfoList.add(comboInfo);
					}
				}
				map.put("comboInfoList",comboInfoList);
			}
		}
		//获取体检人员信息
		List<Map<String,Object>> personList = new ArrayList<>();
		for (TOrderRecord orderRecord:orderRecords) {
			if(orderRecord != null){
				TGroupPerson groupPerson = tGroupPersonMapper.selectById(orderRecord.getPersonId());
				if(groupPerson != null){
					Map<String,Object> personInfo = new HashMap<>();
					personInfo.put("personId",groupPerson.getId());
					personInfo.put("personName",groupPerson.getPersonName());
					personInfo.put("sex",groupPerson.getSex());
					personInfo.put("idCard",groupPerson.getIdCard());
					personInfo.put("isPass",groupPerson.getIsPass());
					personInfo.put("age",groupPerson.getAge());
					personInfo.put("mobile",groupPerson.getMobile());
					personInfo.put("dept",groupPerson.getDept());
					personInfo.put("testNum",groupPerson.getTestNum());
					personInfo.put("checkDate",simpleDateFormat.format(groupPerson.getCheckDate()));
					personInfo.put("physicalType",groupPerson.getPhysicalType());
					personList.add(personInfo);
				}
			}
		}
		map.put("personList",personList);
		return map;
	}

	/**
	 * 根据手机号和检查状态获取对应的预约记录列表
	 * @param mobile
	 * @param isPass
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getOrderRecordInfoListByMobile(String mobile, String isPass) {
		//通过手机号和检查状态获取对应的预约记录
		List<TOrderRecord> orderRecords = tOrderRecordMapper.selectOrderRecordInfoByMobileAndIsPass(mobile, isPass);
		if(orderRecords == null){
			return null;
		}
		List<Map<String,Object>> orderRecordList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (TOrderRecord orderRecord:orderRecords) {
			Map<String,Object> map = new HashMap<>();
			//获取机构信息
			TCheckOrg checkOrg = tCheckOrgMapper.selectById(orderRecord.getCheckOrgId());
			if(checkOrg != null){
				Map<String,Object> checkOrgInfo = new HashMap<>();
				checkOrgInfo.put("checkOrgId",checkOrg.getId());
				checkOrgInfo.put("checkOrgName",checkOrg.getName());
				checkOrgInfo.put("checkOrgAddress",checkOrg.getAddress());
				map.put("checkOrgInfo",checkOrgInfo);
			}
			//获取订单信息
			TGroupOrder groupOrder = tGroupOrderMapper.selectById(orderRecord.getGroupOrderId());
			if(groupOrder != null){
				Map<String,Object> groupOrderInfo = new HashMap<>();
				groupOrderInfo.put("groupOrderId",groupOrder.getId());
				groupOrderInfo.put("orderCode",groupOrder.getOrderCode());
				groupOrderInfo.put("signingTime",simpleDateFormat.format(groupOrder.getSigningTime()));
				map.put("groupOrderInfo",groupOrderInfo);
			}
			//获取体检人员信息
			TGroupPerson groupPerson = tGroupPersonMapper.selectById(orderRecord.getPersonId());
			if(groupPerson != null){
				Map<String,Object> personInfo = new HashMap<>();
				personInfo.put("personId",groupPerson.getId());
				personInfo.put("personName",groupPerson.getPersonName());
				personInfo.put("sex",groupPerson.getSex());
				personInfo.put("idCard",groupPerson.getIdCard());
				personInfo.put("age",groupPerson.getAge());
				personInfo.put("isPass",groupPerson.getIsPass());
				personInfo.put("mobile",groupPerson.getMobile());
				personInfo.put("dept",groupPerson.getDept());
				personInfo.put("testNum",groupPerson.getTestNum());
				personInfo.put("checkDate",simpleDateFormat.format(groupPerson.getCheckDate()));
				personInfo.put("physicalType",groupPerson.getPhysicalType());
				map.put("personInfo",personInfo);
			}
			//获取套餐信息
			String comboNames = orderRecord.getComboName();
			//判断套餐名称中是否包含字符`;`
			if(comboNames.indexOf(";") == -1){
				TCombo combo = tOrderRecordMapper.selectComboByName(comboNames);
				List<Map<String,Object>> comboInfoList = new ArrayList<>();
				Map<String,Object> comboInfo = new HashMap<>();
				if(combo != null){
					//获取项目名称列表
					List<String> comboItems = tOrderRecordMapper.selectTComboItemName(combo.getId());
					String comboItemsNames = String.join(",",comboItems);
					//获取套餐价格
					Integer price = tComboMapper.selectTComboPriceById(combo.getId());
					comboInfo.put("comboId",combo.getId());
					comboInfo.put("comboName",comboNames);
					comboInfo.put("price",price);
					comboInfo.put("comboItemsNames",comboItemsNames);
				}
				comboInfoList.add(comboInfo);
				map.put("comboInfoList",comboInfoList);
			}else {
				//拆分套餐名称
				String[] comboNameList = comboNames.split(";");
				List<Map<String,Object>> comboInfoList = new ArrayList<>();
				for (String name:comboNameList) {
					TCombo combo = tOrderRecordMapper.selectComboByName(name);
					if(combo != null){
						Map<String,Object> comboInfo = new HashMap<>();
						List<String> comboItems = tOrderRecordMapper.selectTComboItemName(combo.getId());
						String comboItemsNames = String.join(",",comboItems);
						Integer price = tComboMapper.selectTComboPriceById(combo.getId());
						comboInfo.put("comboId",combo.getId());
						comboInfo.put("comboName",name);
						comboInfo.put("price",price);
						comboInfo.put("comboItemsNames",comboItemsNames);
						comboInfoList.add(comboInfo);
					}
				}
				map.put("comboInfoList",comboInfoList);
			}
			orderRecordList.add(map);
		}
		return orderRecordList;
	}

	/**
	 * 校验体检人员在同一天是否在同一个机构多次提交
	 * @param idCard
	 * @param checkDate
	 * @param checkOrgId
	 * @return
	 */
	@Override
	public Boolean getGroupPersonRepeatCommit(String idCard, String checkDate, String checkOrgId) {
		Integer count = tOrderRecordMapper.selectRecordCountByIdCardAndCheckDateAndCheckOrgId(idCard, checkDate, checkOrgId);
		return count > 0;
	}

	/**
	* 功能描述：构建模糊查询
	* @param tOrderRecord 需要模糊查询的信息
	* @return 返回查询
	*/
	public QueryWrapper<TOrderRecord>  LikeAllField(TOrderRecord  tOrderRecord, SearchVo searchVo) {
		QueryWrapper<TOrderRecord> queryWrapper = new QueryWrapper<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(StringUtils.isNotBlank(tOrderRecord.getId())){
			queryWrapper.and(i -> i.like("t_order_record.id", tOrderRecord.getId()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getCheckOrgId())){
			queryWrapper.and(i -> i.like("t_order_record.check_org_id", tOrderRecord.getCheckOrgId()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getPersonId())){
			queryWrapper.and(i -> i.like("t_order_record.person_id", tOrderRecord.getPersonId()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getComboId())){
			queryWrapper.and(i -> i.like("t_order_record.combo_id", tOrderRecord.getComboId()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getGroupOrderId())){
			queryWrapper.and(i -> i.like("t_order_record.group_order_id", tOrderRecord.getGroupOrderId()));
		}
		if(tOrderRecord.getOrderStatus() != null){
			queryWrapper.and(i -> i.like("t_order_record.order_status", tOrderRecord.getOrderStatus()));
		}
		if(tOrderRecord.getCheckStatus() != null){
			queryWrapper.and(i -> i.like("t_order_record.check_status", tOrderRecord.getCheckStatus()));
		}
		if(tOrderRecord.getComboName() != null){
			queryWrapper.and(i -> i.like("t_order_record.combo_name", tOrderRecord.getComboName()));
		}
		if(tOrderRecord.getOrderDate() != null){
			queryWrapper.and(i -> i.like("t_order_record.order_date", simpleDateFormat.format(tOrderRecord.getOrderDate())));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getCreateBy())){
			queryWrapper.and(i -> i.like("t_order_record.create_by", tOrderRecord.getCreateBy()));
		}
		if(tOrderRecord.getCreateTime() != null){
			queryWrapper.and(i -> i.like("t_order_record.create_time", tOrderRecord.getCreateTime()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getUpdateBy())){
			queryWrapper.and(i -> i.like("t_order_record.update_by", tOrderRecord.getUpdateBy()));
		}
		if(tOrderRecord.getUpdateTime() != null){
			queryWrapper.and(i -> i.like("t_order_record.update_time", tOrderRecord.getUpdateTime()));
		}
		if(tOrderRecord.getDelFlag() != null){
			queryWrapper.and(i -> i.like("t_order_record.del_flag", tOrderRecord.getDelFlag()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getStatus())){
			queryWrapper.and(i -> i.like("t_order_record.status", tOrderRecord.getStatus()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getType())){
			queryWrapper.and(i -> i.like("t_order_record.type", tOrderRecord.getType()));
		}
		if(StringUtils.isNotBlank(tOrderRecord.getCode())){
			queryWrapper.and(i -> i.like("t_order_record.code", tOrderRecord.getCode()));
		}
		if(searchVo!=null){
			if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
				queryWrapper.lambda().and(i -> i.between(TOrderRecord::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
			}
		}
		queryWrapper.lambda().and(i -> i.eq(TOrderRecord::getDelFlag, 0));
		return queryWrapper;

}
}
