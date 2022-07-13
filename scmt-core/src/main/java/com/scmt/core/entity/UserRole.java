package com.scmt.core.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Exrickx
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user_role")
@TableName("t_user_role")
@ApiModel(value = "用户角色")
public class UserRole extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户唯一id")
    private String userId;

    @ApiModelProperty(value = "角色唯一id")
    private String roleId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "角色名")
    private String roleName;
}
