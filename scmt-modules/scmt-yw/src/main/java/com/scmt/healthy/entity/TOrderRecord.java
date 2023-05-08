package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author dengjie
 * @since 2023-04-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOrderRecord对象", description="TOrderRecord实体")
public class TOrderRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "机构id")
    private String checkOrgId;

    @ApiModelProperty(value = "体检人员id")
    private String personId;

    @ApiModelProperty(value = "套餐id")
    private String comboId;

    @ApiModelProperty(value = "预约状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "体检状态")
    private Integer checkStatus;

    @ApiModelProperty(value = "套餐名称")
    private String comboName;

    @ApiModelProperty(value = "订单id")
    private String groupOrderId;

    @ApiModelProperty(value = "预约时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除状态")
    @TableLogic(value = "0",delval = "1")
    private Integer delFlag;

    private String status;

    private String type;

    private String code;


    @ApiModelProperty(value = "体检人员姓名")
    @TableField(exist = false,value = "personName")
    private String personName;

    @ApiModelProperty(value = "体检类别")
    @TableField(exist = false,value = "physicalType")
    private String physicalType;


    @ApiModelProperty(value = "体检日期")
    @TableField(exist = false,value = "checkDate")
    private String checkDate;

    @ApiModelProperty(value = "体检人员状态")
    @TableField(exist = false,value = "isPass")
    private String isPass;

    @ApiModelProperty(value = "体检机构实体属性")
    @TableField(exist = false,value = "tCheckOrg")
    private TCheckOrg tCheckOrg;

    @ApiModelProperty(value = "体检人员实体属性")
    @TableField(exist = false,value = "tGroupPerson")
    private TGroupPerson tGroupPerson;








}
