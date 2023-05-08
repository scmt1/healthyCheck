package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author dengjie
 * @since 2023-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TCheckOrg对象", description="体检机构")
public class TCheckOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "机构名称")
    private String name;

    @ApiModelProperty(value = "机构简介")
    private String introduction;

    @ApiModelProperty(value = "机构地址")
    private String address;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "营业时间")
    private String businessHours;

    @ApiModelProperty(value = "体检机构头像")
    private String avatar;

    @ApiModelProperty(value = "体检机构描述图片")
    private String images;

    @ApiModelProperty(value = "体检机构等级")
    private String level;

    @ApiModelProperty(value = "体检机构标签")
    private String tags;

    @ApiModelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "到院须知")
    private String notice;

    private String createBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String updateBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除状态")
    @TableLogic(value = "0",delval = "1")
    private Integer delFlag;

    @ApiModelProperty(value = "是否等级优先")
    @TableField(exist = false,value = "isLevel")
    private String isLevel;

    @ApiModelProperty(value = "是否距离优先")
    @TableField(exist = false,value = "isDistance")
    private String isDistance;

    private String type;

    private String code;

    private String status;

    @ApiModelProperty(value = "检查类型")
    private String checkType;

    @ApiModelProperty(value = "套餐实体属性")
    @TableField(exist = false,value = "tCombos")
    private List<TCombo> tCombos;

    @ApiModelProperty(value = "套餐类型")
    @TableField(exist = false,value = "combosType")
    private String combosType;

    @ApiModelProperty(value = "套餐名字")
    @TableField(exist = false,value = "combosName")
    private String combosName;

    @ApiModelProperty(value = "职业阶段")
    @TableField(exist = false,value = "careerStage")
    private String careerStage;

    @ApiModelProperty(value = "是否是小程序端")
    @TableField(exist = false,value = "isMiniApps")
    private Boolean isMiniApps;


}
