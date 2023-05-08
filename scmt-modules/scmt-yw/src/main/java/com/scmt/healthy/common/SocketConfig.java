package com.scmt.healthy.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Socket配置类型
 *  @author dengjie
 */
@Component
public class SocketConfig {


	/**
	 *作为server 端的Ip（127.0.0.1 代表localhost,实际用时直接改成本机Ip）
	 */
	@Value("${skconfig.lisServerIp}")
	private String lisServerIp;
	/**
	 *作为server 端的port
	 */
	@Value("${skconfig.lisServerPort}")
	private Integer lisServerPort;

	/**
	 * 主治医生
	 */
	@Value("${skconfig.attendingPhysician}")
	private String attendingPhysician;

	/**
	 * 体检中心主任
	 */
	@Value("${skconfig.physicalDirector}")
	private String physicalDirector ;

	/**
	 * 技术负责人
	 */
	@Value("${skconfig.technicalDirector}")
	private String technicalDirector ;

	/**
	 * lis码是否统一
	 */
	@Value("${skconfig.lisCode}")
	private Boolean lisCode ;

	/**
	 * 是否定时更新问诊签名
	 */
	@Value("${skconfig.isAutograph}")
	private Boolean isAutograph ;

	/**
	 * 同步数据是否开启名字模糊查询
	 */
	@Value("${skconfig.isPersonName}")
	private Boolean isPersonName ;

	/**
	 * 同步数据是否开启code模糊查询
	 */
	@Value("${skconfig.isCodeLike}")
	private Boolean isCodeLike ;

	/**
	 * 是否打印心电图条码
	 */
	@Value("${skconfig.isShowXDT}")
	private Boolean isShowXDT ;

	/**
	 * 是否去掉条码下的项目名称(去掉之后以“姓名-年龄 条码号”展示)
	 */
	@Value("${skconfig.isDeleteCodeName}")
	private Boolean isDeleteCodeName ;

	/**
	 * 是否以“姓名-年龄-项目名 条码号”展示
	 */
	@Value("${skconfig.isShowByNameSexProject}")
	private Boolean isShowByNameSexProject ;

	/**
	 * 是否额外增加两个条码
	 */
	@Value("${skconfig.isAddCode}")
	private Boolean isAddCode ;

	/**
	 * 是否匹配血常规图片
	 */
	@Value("${skconfig.isUpdateBloodImg}")
	private Boolean isUpdateBloodImg ;

	/**
	 * 是否生成13位复查条码
	 */
	@Value("${skconfig.isThirteenCode}")
	private Boolean isThirteenCode ;

	/**
	 * 是否使用wps打印
	 */
	@Value("${skconfig.isWpsPrint}")
	private Boolean isWpsPrint ;

	/**
	 * 是否生化合并
	 */
	@Value("${skconfig.isbiochemistryMerge}")
	private Boolean isbiochemistryMerge ;

	/**
	 * 是否匹配分组套餐
	 */
	@Value("${skconfig.isMatchingGroupCombo}")
	private Boolean isMatchingGroupCombo ;

	/**
	 * 是否修改创建方法(订单、体检、条码编号)
	 */
	@Value("${skconfig.isUpdateCreateMethd}")
	private Boolean isUpdateCreateMethd ;

	/**
	 * 是否展示第一次检查结果
	 */
	@Value("${skconfig.isInitialMerger}")
	private Boolean isInitialMerger ;



	/**
	 * 体检诊台是否合并问诊页面
	 */
	@Value("${skconfig.isCombinedConsultation}")
	private Boolean isCombinedConsultation ;

	/**
	 * 弃检后的小结
	 */
	@Value("${skconfig.giveUp}")
	private String giveUp;

	public String getGiveUp() {
		return giveUp;
	}

	public void setGiveUp(String giveUp) {
		this.giveUp = giveUp;
	}

	public Boolean getUpdateCreateMethd() {
		return isUpdateCreateMethd;
	}

	public void setUpdateCreateMethd(Boolean updateCreateMethd) {
		isUpdateCreateMethd = updateCreateMethd;
	}

	public Boolean getMatchingGroupCombo() {
		return isMatchingGroupCombo;
	}

	public void setMatchingGroupCombo(Boolean matchingGroupCombo) {
		isMatchingGroupCombo = matchingGroupCombo;
	}

	public Boolean getIsbiochemistryMerge() {
		return isbiochemistryMerge;
	}

	public void setIsbiochemistryMerge(Boolean isbiochemistryMerge) {
		this.isbiochemistryMerge = isbiochemistryMerge;
	}

	public Boolean getIsWpsPrint() {
		return isWpsPrint;
	}

	public void setIsWpsPrint(Boolean isWpsPrint) {
		this.isWpsPrint = isWpsPrint;
	}

	public Boolean getIsUpdateBloodImg() {
		return isUpdateBloodImg;
	}

	public void setIsUpdateBloodImg(Boolean isUpdateBloodImg) {
		this.isUpdateBloodImg = isUpdateBloodImg;
	}

	public Boolean getIsThirteenCode() {
		return isThirteenCode;
	}

	public void setIsThirteenCode(Boolean isThirteenCode) {
		this.isThirteenCode = isThirteenCode;
	}

	public Boolean getLisCode() {
		return lisCode;
	}

	public void setLisCode(Boolean lisCode) {
		this.lisCode = lisCode;
	}

	public Boolean getIsDeleteCodeName() {
		return isDeleteCodeName;
	}

	public void setIsDeleteCodeName(Boolean isDeleteCodeName) {
		this.isDeleteCodeName = isDeleteCodeName;
	}

	public Boolean getShowByNameSexProject() {
		return isShowByNameSexProject;
	}

	public void setShowByNameSexProject(Boolean showByNameSexProject) {
		isShowByNameSexProject = showByNameSexProject;
	}

	public Boolean getIsAddCode() {
		return isAddCode;
	}

	public void setIsAddCode(Boolean isAddCode) {
		this.isAddCode = isAddCode;
	}

	public Boolean getIsShowXDT() {
		return isShowXDT;
	}

	public void setIsShowXDT(Boolean isShowXDT) {
		this.isShowXDT = isShowXDT;
	}

	public Boolean getIsAutograph() {
		return isAutograph;
	}

	public void setIsAutograph(Boolean isAutograph) {
		this.isAutograph = isAutograph;
	}

	public Boolean getIsPersonName() {
		return isPersonName;
	}

	public void setIsPersonName(Boolean isPersonName) {
		this.isPersonName = isPersonName;
	}
	public Boolean getIsCodeLike() {
		return isCodeLike;
	}

	public void setIsCodeLike(Boolean isCodeLike) {
		this.isCodeLike = isCodeLike;
	}

	public String getTechnicalDirector() {
		return technicalDirector;
	}

	public void setTechnicalDirector(String technicalDirector) {
		this.technicalDirector = technicalDirector;
	}

	public void setPhysicalDirector(String physicalDirector) {
		this.physicalDirector = physicalDirector;
	}

	public String getLisServerIp() {
		return lisServerIp;
	}

	public void setLisServerIp(String lisServerIp) {
		this.lisServerIp = lisServerIp;
	}

	public Integer getLisServerPort() {
		return lisServerPort;
	}

	public void setLisServerPort(Integer lisServerPort) {
		this.lisServerPort = lisServerPort;
	}

	public String getAttendingPhysician() {
		return attendingPhysician;
	}

	public void setAttendingPhysician(String attendingPhysician) {
		this.attendingPhysician = attendingPhysician;
	}

	public String getPhysicalDirector() {
		return physicalDirector;
	}

	public Boolean getInitialMerger() { return isInitialMerger; }

	public void setInitialMerger(Boolean initialMerger) { isInitialMerger = initialMerger; }
	public Boolean getCombinedConsultation() { return isCombinedConsultation; }

	public void setCombinedConsultation(Boolean combinedConsultation) { isCombinedConsultation = combinedConsultation; }
}
