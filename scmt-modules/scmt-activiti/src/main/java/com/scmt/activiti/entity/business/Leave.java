package com.scmt.activiti.entity.business;

import com.scmt.core.base.ScmtBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Exrick
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_leave")
@TableName("t_leave")
@ApiModel(value = "请假申请")
public class Leave extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "请假类型")
    private String type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "说明")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "截止日期")
    private Date endDate;

    @ApiModelProperty(value = "请假时长（小时）")
    private Integer duration;
}
