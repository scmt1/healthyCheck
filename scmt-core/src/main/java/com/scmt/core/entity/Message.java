package com.scmt.core.entity;

import com.scmt.core.base.ScmtBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Exrick
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_message")
@TableName("t_message")
@ApiModel(value = "消息")
public class Message extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "消息类型")
    private String type;

    @ApiModelProperty(value = "新创建账号也推送")
    private Boolean createSend;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "发送范围")
    private Integer range;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "发送指定用户id")
    private String[] userIds;
}
