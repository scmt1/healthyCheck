package com.scmt.activiti.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.scmt.core.common.constant.CommonConstant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * @author Exrick
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_act_category")
@TableName("t_act_category")
@ApiModel(value = "流程分类")
public class ActCategory extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类名称")
    private String title;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "是否为父节点(含子节点) 默认false")
    private Boolean isParent = false;

    @ApiModelProperty(value = "排序值")
    @Column(precision = 10, scale = 2)
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "类型 0分组 1分类")
    private Integer type;

    @ApiModelProperty(value = "是否启用 0启用 -1禁用")
    private Integer status = CommonConstant.STATUS_NORMAL;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "父节点名称")
    private String parentTitle;
}
