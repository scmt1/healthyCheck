package com.scmt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时判断是否检查完
 */
@Component
@EnableScheduling
public class HealthyTask {
	private static ThreadLocal<SimpleDateFormat> dateFormat =
			ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	@Autowired
	private ITGroupPersonService tGroupPersonService;

	@Autowired
	private ITOrderGroupItemService itOrderGroupItemService;

	@Autowired
	private IRelationPersonProjectCheckService iRelationPersonProjectCheckService;

	@Autowired
	public SocketConfig socketConfig;

	@Autowired
	private ITDepartResultService tDepartResultService;

	@Autowired
	private ITLisDataService itLisDataService;

	@Autowired
	private ITBarcodeService tBarcodeService;

	@Autowired
	private ITOrderGroupService itOrderGroupService;

	@Autowired
	private ITComboService itComboService;
	/**
	 * 在方法上使用@Scheduled注解来创建具体的定时任务
	 */
	//@Scheduled(cron = "0 30 2 * * ?")
	@Scheduled(cron = "0 0/60 * * * ?")
	//@Scheduled(cron = "*/30 * * * * ?")
	private void task1() {
		QueryWrapper<TGroupPerson> personQueryWrapper = new QueryWrapper<>();
		personQueryWrapper.eq("del_flag", 0);
		personQueryWrapper.eq("is_pass", 2);
		//personQueryWrapper.eq("is_wz_check", 1);
		personQueryWrapper.select("id","group_id","is_pass","is_wz_check");
		List<TGroupPerson> list = tGroupPersonService.list(personQueryWrapper);
		if(list!=null && list.size()>0){
			for (TGroupPerson byId: list) {
				if (byId!=null && byId.getIsWzCheck() ==1 &&  byId.getIsPass() < 3) {
					//判断其他科室是否都检查完了
					Integer count = itOrderGroupItemService.getAllCheckCount(byId.getId(), byId.getGroupId());
					Integer count1 = itOrderGroupItemService.getDepartResultCount(byId.getId(), byId.getGroupId());

					//弃检项目
					QueryWrapper<RelationPersonProjectCheck> checkQueryWrapper = new QueryWrapper<>();
					checkQueryWrapper.eq("state", 2);
					checkQueryWrapper.eq("person_id", byId.getId());
					int count2 = iRelationPersonProjectCheckService.count(checkQueryWrapper);
					if (count1.intValue() >= (count.intValue() - count2)) {
						byId.setIsPass(3);
						tGroupPersonService.updateById(byId);
					}
				}
			}
		}

		/*更新问诊签名*/
		if(socketConfig.getIsAutograph()){
			Integer returnData = tGroupPersonService.updatewAutograph();
			System.out.println("更新问诊签名："+returnData);
		}

		/*匹配血常规图片*/
		if(socketConfig.getIsUpdateBloodImg()){
			QueryWrapper<TDepartResult> tDepartResultQueryWrapper = new QueryWrapper<>();
			tDepartResultQueryWrapper.isNull("url");
			tDepartResultQueryWrapper.like("group_item_name","血常规");
			List<TDepartResult> tDepartResults = tDepartResultService.list(tDepartResultQueryWrapper);
			//根据barcode 去读取结果
			QueryWrapper<TLisData> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("type", "ZN-HA");
			List<TLisData> tLisDatas = itLisDataService.list(queryWrapper);
			for (TDepartResult tDepartResult : tDepartResults){
				QueryWrapper<TBarcode> wrapper = new QueryWrapper<>();
				wrapper.eq("person_id", tDepartResult.getPersonId());
				wrapper.eq("group_item_id", tDepartResult.getGroupItemId());
				TBarcode one = tBarcodeService.getOne(wrapper);
				if(one != null && StringUtils.isNotBlank(one.getBarcode())){
					List<TLisData> tLisDataNew = tLisDatas.stream().filter(ii -> ii.getCode().equals(one.getBarcode())).collect(Collectors.toList());

					if(tLisDataNew != null && tLisDataNew.size() > 0){

						JSONArray jsonData = JSON.parseArray(tLisDataNew.get(0).getData());
						String urls = "";
						for(int i=0;i<jsonData.size();i++) {
							if(jsonData.getJSONObject(i) != null && jsonData.getJSONObject(i).containsKey("type") && jsonData.getJSONObject(i).get("type").equals("image")){
								if(urls == ""){
									urls += jsonData.getJSONObject(i).get("base64");
								}else{
									urls += "," + jsonData.getJSONObject(i).get("base64");
								}
							}
						}

						QueryWrapper<TDepartResult> tDepartResultUpdateWrapper = new QueryWrapper<>();
						tDepartResultUpdateWrapper.eq("id", tDepartResult.getId());
						tDepartResultUpdateWrapper.eq("person_id", tDepartResult.getPersonId());
						tDepartResultUpdateWrapper.eq("group_item_name", "血常规");
						TDepartResult tDepartResultNew = new TDepartResult();
						tDepartResultNew.setUrl(urls);
						tDepartResultService.update(tDepartResultNew,tDepartResultUpdateWrapper);
					}
				}
			}
		}

		/*分组危害因素对应套餐匹配(自动补齐套餐id为空的分组)*/
		if(socketConfig.getMatchingGroupCombo()){
			QueryWrapper<TOrderGroup> orderGroupQueryWrapper = new QueryWrapper<>();
			orderGroupQueryWrapper.eq("del_flag",0);
			orderGroupQueryWrapper.isNull("combo_id");
			orderGroupQueryWrapper.like("name","岗");
			orderGroupQueryWrapper.like("name","[");
			orderGroupQueryWrapper.notLike("name","危害因素");
			List<TOrderGroup> tOrderGroups = itOrderGroupService.list(orderGroupQueryWrapper);
			if(tOrderGroups!=null && tOrderGroups.size()>0){
				for(TOrderGroup tOrderGroup : tOrderGroups){
					if(tOrderGroup!=null && StringUtils.isNotBlank(tOrderGroup.getName())){
						String groupName = tOrderGroup.getName();
						String[] groupNames = groupName.split("\\[");
						//在岗状态
						String workStateText = groupNames[1].replaceAll("\\]","");
						//危害因素 字符串集合
						String[] hazardFactorsTexts = groupNames[0].split("\\|");
						if(StringUtils.isNotBlank(workStateText) && hazardFactorsTexts!=null && hazardFactorsTexts.length>0){
							QueryWrapper<TCombo> comboQueryWrapper = new QueryWrapper<>();
							comboQueryWrapper.eq("del_flag",0);
							comboQueryWrapper.in("hazard_factors_text",hazardFactorsTexts);
							comboQueryWrapper.eq("career_stage",workStateText);
							List<TCombo> tComboList = itComboService.list(comboQueryWrapper);
							if(tComboList!=null && tComboList.size()>0){
								String tComboIds = "";
								for(TCombo tCombo : tComboList){
									if(tCombo!=null && StringUtils.isNotBlank(tCombo.getId())){
										if(StringUtils.isBlank(tComboIds)){
											tComboIds += tCombo.getId();
										}else{
											tComboIds += "," + tCombo.getId();
										}
									}
								}
								if(StringUtils.isNotBlank(tComboIds)){
									tOrderGroup.setComboId(tComboIds);
									itOrderGroupService.updateById(tOrderGroup);
								}
							}
						}
					}
				}
			}
		}
	}
}
