package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单分组
 * </p>
 *
 * @author ycy
 * @since 2021-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOrderGroup对象", description="订单分组")
public class TOrderGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单id")
    private String groupOrderId;

    @ApiModelProperty(value = "分组名称")
    private String name;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;

    @ApiModelProperty(value = "团单折扣")
    private Integer discount;

    @ApiModelProperty(value = "增项缴费方式")
    private String additionalPayment;

    @ApiModelProperty(value = "增项折扣")
    private Integer addDiscount;

    @ApiModelProperty(value = "组人数")
    private Integer personCount;

    @ApiModelProperty(value = "套餐id")
    private String comboId;

    @ApiModelProperty(value = "分组项目")
    @TableField(exist = false)
    private List<TOrderGroupItem> projectData;

    @TableField(exist = false)
    private Boolean show = true;

    @ApiModelProperty(value = "体检类型")
    @TableField(exist = false)
    private String physicalType;

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

    @ApiModelProperty(value = "环评因素")
    private String eiaFactors;

    @ApiModelProperty(value = "检查项目")
    @TableField(exist = false)
    private String projects;

    @ApiModelProperty(value = "价格")
    @TableField(exist = false)
    private Integer prices;
}
