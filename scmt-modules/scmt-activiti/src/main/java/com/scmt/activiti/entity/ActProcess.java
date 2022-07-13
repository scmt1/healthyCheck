package com.scmt.activiti.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.scmt.core.common.constant.ActivitiConstant;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Exrick
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_act_process")
@TableName("t_act_process")
@ApiModel(value = "流程定义")
public class ActProcess extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "流程标识名称")
    private String processKey;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "部署id")
    private String deploymentId;

    @ApiModelProperty(value = "所属分类")
    private String categoryId;

    @ApiModelProperty(value = "xml文件名")
    private String xmlName;

    @ApiModelProperty(value = "流程图片名")
    private String diagramName;

    @ApiModelProperty(value = "描述/备注")
    private String description;

    @ApiModelProperty(value = "最新版本")
    private Boolean latest;

    @ApiModelProperty(value = "流程状态 部署后默认1激活")
    private Integer status = ActivitiConstant.PROCESS_STATUS_ACTIVE;

    @ApiModelProperty(value = "关联前端表单路由名")
    private String routeName;

    @ApiModelProperty(value = "关联业务表名")
    private String businessTable;

    @ApiModelProperty(value = "是否所有人可见")
    private Boolean allUser;

    @ApiModelProperty(value = "所属分类名称")
    private String categoryTitle;
}
