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
 * 单位报告表
 * </p>
 *
 * @author lbc
 * @since 2021-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TUnitReport对象", description="单位报告表")
public class TUnitReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "报告编号")
    private String code;

    @ApiModelProperty(value = "体检单位")
    private String physicalUnit;

    @ApiModelProperty(value = "委托单位")
    private String entrustUnit;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactors;

    @ApiModelProperty(value = "体检类型")
    private String physicalType;

    @ApiModelProperty(value = "体检日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date physicalDate;

    @ApiModelProperty(value = "体检人数")
    private Integer physicalNumber;

    @ApiModelProperty(value = "体检项目")
    private String physicalProject;

    @ApiModelProperty(value = "评论依据")
    private String evaluationBasis;

    @ApiModelProperty(value = "结论报告")
    private String concludingObservations;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty(value = "是否显示(0-不显示，1-显示)")
    private Integer isShow;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;


    // 统计用
    @ApiModelProperty(value = "危害因素")
    @TableField(exist = false)
    private String hazardFactorsText;
    @ApiModelProperty(value = "在岗状态")
    @TableField(exist = false)
    private String workStateText;
    @ApiModelProperty(value = "参检人数")
    @TableField(exist = false)
    private Integer total;
    @ApiModelProperty(value = "未见异常人数")
    @TableField(exist = false)
    private Integer noAbnormalNum;
    @ApiModelProperty(value = "其他异常人数")
    @TableField(exist = false)
    private Integer otherAbnormalNum;
    @ApiModelProperty(value = "职业禁忌症异常人数")
    @TableField(exist = false)
    private Integer tabooNum;
    @ApiModelProperty(value = "职业病人数")
    @TableField(exist = false)
    private Integer diseaseNum;
    @ApiModelProperty(value = "复查人数")
    @TableField(exist = false)
    private Integer recheckNum;
    @ApiModelProperty(value = "与职业危害因素相关的指标异常及复查人数")
    @TableField(exist = false)
    private Integer recheckNums;
    @ApiModelProperty(value = "正常人数")
    @TableField(exist = false)
    private Integer normalNum;
    @ApiModelProperty(value = "异常人数")
    @TableField(exist = false)
    private Integer reviewNum;
    @ApiModelProperty(value = "参检人数")
    @TableField(exist = false)
    private Integer allNum;


}
