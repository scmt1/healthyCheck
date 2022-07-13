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

	public Boolean getLisCode() {
		return lisCode;
	}

	public void setLisCode(Boolean lisCode) {
		this.lisCode = lisCode;
	}

	public Boolean getIsAutograph() {
		return isAutograph;
	}

	public void setIsAutograph(Boolean isAutograph) {
		this.isAutograph = isAutograph;
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

}
