package com.scmt.core.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.scmt.core.common.constant.MemberConstant;
import com.scmt.core.common.utils.NameUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Exrick
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "app_member")
@TableName("app_member")
@ApiModel(value = "会员（注册用户）")
public class Member extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    @Column(unique = true, nullable = false)
    private String username;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    @Size(max = 20, message = "昵称长度不能超过20")
    private String nickname;

    @ApiModelProperty(value = "手机")
    @Pattern(regexp = NameUtil.regMobile, message = "11位手机号格式不正确")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "性别")
    private String sex;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
    private Date birth;

    @ApiModelProperty(value = "积分 默认0")
    private Integer grade = 0;

    @ApiModelProperty(value = "定位")
    private String position;

    @ApiModelProperty(value = "地区")
    private String address;

    @ApiModelProperty(value = "简介")
    private String description;

    @ApiModelProperty(value = "邀请人")
    private String inviteBy;

    @ApiModelProperty(value = "会员头像")
    private String avatar = MemberConstant.MEMBER_DEFAULT_AVATAR;

    @ApiModelProperty(value = "会员类型 默认0普通用户 1会员")
    private Integer type = MemberConstant.MEMBER_TYPE_NORMAL;

    @ApiModelProperty(value = "状态 默认0正常 -1拉黑禁用")
    private Integer status = MemberConstant.MEMBER_STATUS_NORMAL;

    @ApiModelProperty(value = "注册平台来源 -1未知 0PC/H5 1安卓 2苹果 3微信 4支付宝 5QQ 6字节 7百度")
    private Integer platform;

    @ApiModelProperty(value = "拥有权限信息 多个逗号分隔 默认MEMBER")
    private String permissions = MemberConstant.MEMBER_PERMISSION;

    @ApiModelProperty(value = "VIP状态 默认0未开通 1已开通 2已过期")
    private Integer vipStatus = MemberConstant.MEMBER_VIP_NONE;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "会员开通时间")
    private Date vipStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "会员到期时间")
    private Date vipEndTime;
}
