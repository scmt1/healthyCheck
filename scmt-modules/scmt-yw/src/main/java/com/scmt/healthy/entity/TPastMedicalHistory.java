package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 既往病史
 * </p>
 *
 * @author dengjie
 * @since 2021-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TPastMedicalHistory对象", description="既往病史")
public class TPastMedicalHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "患病名称")
    private String diseaseName;

    @ApiModelProperty(value = "诊断日期")
    private Date diseaseDate;

    @ApiModelProperty(value = "诊断单位")
    private String diagnosticUnit;

    @ApiModelProperty(value = "治疗经过")
    private String afterTreatment;

    @ApiModelProperty(value = "转归")
    private String fate;

    @ApiModelProperty(value = "人员主键")
    private String personId;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否患病")
    private String yesOrNoSick;

    @ApiModelProperty(value = "证件号码")
    @TableField(exist = false)
    private String idCard;

    @ApiModelProperty("登记号码")
    @TableField(exist = false)
    private String registrationNumber;


}
