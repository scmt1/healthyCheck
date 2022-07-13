package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author ycy
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOrderFlow对象", description="")
public class TOrderFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单id")
    private String groupOrderId;

    @ApiModelProperty(value = "审核人员")
    private String auditUserId;

    @ApiModelProperty(value = "审核人员")
    private String auditUserName;

    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "提交人")
    private String createUserId;

    @ApiModelProperty(value = "提交人")
    private String createUserName;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    @ApiModelProperty(value = "审核意见")
    private String auditContent;

    @ApiModelProperty(value = "抄送人员")
    private String showUserId;

    @ApiModelProperty(value = "抄送人员")
    private String showUserName;

    @ApiModelProperty(value = "审核状态 2通过 -1 未通过  0默认")
    private Integer auditState;

    @TableField(exist = false)
    private List<String> documentList;

}
