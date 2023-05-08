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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author ycy
 * @since 2021-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TGroupOrder对象", description="")
public class TGroupOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @ApiModelProperty(value = "团检单位id")
    private String groupUnitId;

    @ApiModelProperty(value = "团检单位名称")
    private String groupUnitName;

    @ApiModelProperty(value = "团检类型")
    private String physicalType;

    @ApiModelProperty(value = "销售负责人")
    private String salesDirector;

    @ApiModelProperty(value = "销售负责人姓名")
    private String salesDirectorName;

    @ApiModelProperty(value = "销售参与人")
    private String salesParticipant;

    @ApiModelProperty(value = "销售参与人姓名")
    private String salesParticipantName;

    @ApiModelProperty(value = "签订日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date signingTime;

    @ApiModelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deliveryTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @ApiModelProperty(value = "订单状态  1暂存 2提交")
    private Integer state;
    @ApiModelProperty(value = "审核状态 1处理中 2已通过 -1未通过")
    private Integer auditState;

    @ApiModelProperty(value = "待审批订单数")
    @TableField(exist = false)
    private Integer waitApprove;

    @ApiModelProperty(value = "已审批订单数")
    @TableField(exist = false)
    private Integer approved;

    @ApiModelProperty(value = "订单分组信息")
    @TableField(exist = false)
    private List<TOrderGroup> groupData;

    @ApiModelProperty(value = "订单支付状态")
    private Integer payStatus;

    @ApiModelProperty(value = "套餐原价")
    private BigDecimal packagePrice;
    @ApiModelProperty(value = "套餐合计")
    private BigDecimal packageTotal;
    @ApiModelProperty(value = "套餐折扣")
    private BigDecimal packageDiscount;
    @ApiModelProperty(value = "团单原价")
    private BigDecimal orderPrice;
    @ApiModelProperty(value = "团单合计")
    private BigDecimal orderTotal;
    @ApiModelProperty(value = "团单折扣")
    private BigDecimal orderDiscount;
    @ApiModelProperty(value = "订单总人数")
    private Integer personCount;
    @ApiModelProperty(value = "是否零星体检")
    private Integer sporadicPhysical;

    @ApiModelProperty(value = "流程id")
    @TableField(exist = false)
    private String orderFlowId;

    @ApiModelProperty(value = "订单分组信息")
    @TableField(exist = false)
    private TGroupPerson groupPerson;
    @TableField(exist = false)
    private String searchKey;

    @TableField(exist = false)
    private String auditContent;

    @ApiModelProperty(value = "订单合同地址")
    private String orderPath;

    @ApiModelProperty(value = "订单执照地址")
    private String orderLicensePath;

    @ApiModelProperty(value = "订单评价地址")
    private String orderEvaluatePath;

    @ApiModelProperty(value = "基本信息表地址")
    private String orderInfoPath;

    @ApiModelProperty(value = "人员名单表地址")
    private String orderPersonDataPath;

    @ApiModelProperty(value = "分组数据")
    @TableField(exist = false)
    private List<TOrderGroup> tOrderGroups;

    @ApiModelProperty(value = "订单联系人手机号")
    @TableField(exist = false)
    private String mobile;

    @ApiModelProperty(value = "订单下套餐id集合")
    @TableField(exist = false)
    private String comboIds;

    @ApiModelProperty(value = "订单下套餐名集合")
    @TableField(exist = false)
    private String comboNames;

    @ApiModelProperty(value = "订单公司名称")
    @TableField(exist = false)
    private String unitName;

    @ApiModelProperty(value = "企业代码")
    @TableField(exist = false)
    private String usccCode;

    @ApiModelProperty(value = "所选套餐")
    @TableField(exist = false)
    private List<TCombo> setMealItems;

    @ApiModelProperty(value = "分组人数可以为零")
    @TableField(exist = false)
    private Boolean tolerable;

    @ApiModelProperty("公司名称")
    @TableField(exist = false)
    private String companyName;

    @ApiModelProperty("人员数组")
    @TableField(exist = false)
    private List<TGroupPerson> tGroupPersonData;

    @ApiModelProperty("套餐id")
    @TableField(exist = false)
    private String ComboId;

}
