package com.scmt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scmt.healthy.common.SocketConfig;
import com.scmt.healthy.entity.RelationPersonProjectCheck;
import com.scmt.healthy.entity.TGroupPerson;
import com.scmt.healthy.service.IRelationPersonProjectCheckService;
import com.scmt.healthy.service.ITGroupPersonService;
import com.scmt.healthy.service.ITOrderGroupItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	}
}
