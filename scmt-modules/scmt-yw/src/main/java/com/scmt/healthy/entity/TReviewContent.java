package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author mike
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tReviewContent对象", description="")
public class TReviewContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "评审级别 1一级审核 2二级审核 3三级审核 4完成审核")
    private Integer auditLevel;

    @ApiModelProperty(value = "用人单位基本情况信息表是否存在")
    private Integer isInfoTable;

    @ApiModelProperty(value = "近期职业病危害因素检测报告或评价报告是否存在")
    private Integer isAssessmentReport;

    @ApiModelProperty(value = "营业制造（三证合一）是否存在")
    private Integer isBusinessLicense;

    @ApiModelProperty(value = "合同（委托书）是否存在")
    private Integer isEntrustReport;

    @ApiModelProperty(value = "预体检人员名单信息是否存在")
    private Integer isPersonInfo;

    @ApiModelProperty(value = "检查类别和项目是否与备案一致")
    private Integer isKeepRecord;

    @ApiModelProperty(value = "仪器设备是否满足合同（协议）所订职业健康检查需求")
    private Integer isEquipmentTrue;

    @ApiModelProperty(value = "委托单位要求是否符合国家有关法律、政策和标准规范的要求")
    private Integer isStandard;

    @ApiModelProperty(value = "是否与委托单位进行了真实而有效的沟通，了解委托单位的真实需求")
    private Integer isUnderstandNeeds;

    @ApiModelProperty(value = "是否涉及项目分包")
    private Integer isSubcontract;

    @ApiModelProperty(value = "委托方对体检时间、期限有无特别要求")
    private Integer isSpecialRequirements;

    @ApiModelProperty(value = "是否告知委托方体检方案及体检注意事项")
    private Integer isInform;

    @ApiModelProperty(value = "是否委托方自取")
    private Integer isTakeFromOneself;

    @ApiModelProperty(value = "是否可以为委托单位提供职业健康检查服务")
    private Integer isCanService;

    @ApiModelProperty(value = "不能为委托单位提供职业健康检查服务，原因")
    private Integer reason;

    @ApiModelProperty(value = "质控科负责人或体检中心负责人")
    private String conclusionPersonInCharge;

    @ApiModelProperty(value = "审核结论日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date conclusionDate;

    @ApiModelProperty(value = "领导或技术负责人")
    private String approvalPersonInCharge;

    @ApiModelProperty(value = "是否同意批准")
    private Integer isAgree;

    @ApiModelProperty(value = "审核批准日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date approvalDate;
}
