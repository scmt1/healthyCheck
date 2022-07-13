package com.scmt.activiti.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.scmt.core.common.constant.ActivitiConstant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Exrickx
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_act_business")
@TableName("t_act_business")
@ApiModel(value = "业务申请")
public class ActBusiness extends ScmtBaseEntity {

    @ApiModelProperty(value = "申请标题")
    private String title;

    @ApiModelProperty(value = "创建用户id")
    private String userId;

    @ApiModelProperty(value = "关联表id")
    private String tableId;

    @ApiModelProperty(value = "流程定义id")
    private String procDefId;

    @ApiModelProperty(value = "流程实例id")
    private String procInstId;

    @ApiModelProperty(value = "状态 0草稿默认 1处理中 2结束")
    private Integer status = ActivitiConstant.STATUS_TO_APPLY;

    @ApiModelProperty(value = "结果状态 0未提交默认 1处理中 2通过 3驳回")
    private Integer result = ActivitiConstant.RESULT_TO_SUBMIT;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交申请时间")
    private Date applyTime;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "流程版本")
    private Integer version;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "分配用户id")
    private String[] assignees;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "所属流程名")
    private String processName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "前端路由名")
    private String routeName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "任务优先级 默认0")
    private Integer priority = 0;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "当前任务")
    private String currTaskName;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "第一个节点是否为网关")
    private Boolean firstGateway = false;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "是否发送站内消息")
    private Boolean sendMessage;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "是否发送短信通知")
    private Boolean sendSms;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "是否发送邮件通知")
    private Boolean sendEmail;

    @Transient
    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty(value = "流程设置参数")
    private Map<String, Object> params = new HashMap<>(16);
}
