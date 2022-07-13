package com.scmt.activiti.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Exrick
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_act_node")
@TableName("t_act_node")
@ApiModel(value = "节点")
public class ActNode extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点id")
    private String nodeId;

    @ApiModelProperty(value = "节点关联类型 0角色 1用户 2部门")
    private Integer type;

    @ApiModelProperty(value = "关联其他表id")
    private String relateId;

}
