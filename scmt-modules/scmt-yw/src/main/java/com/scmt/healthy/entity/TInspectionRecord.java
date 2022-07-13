package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

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
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TInspectionRecord对象", description="")
public class TInspectionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "体检人员Id")
    private String personId;

    @ApiModelProperty(value = "总检医生")
    private String inspectionDoctor;

    @ApiModelProperty(value = "总检医生签名")
    private Object inspectionAutograph;

    @ApiModelProperty(value = "总检日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inspectionDate;

    @ApiModelProperty(value = "操作员")
    private String operator;

    @ApiModelProperty(value = "结论")
    private String conclusion;

    @ApiModelProperty(value = "健康证条件")
    private String healthCertificateConditions;

    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新人id")
    private String updateId;

    @ApiModelProperty(value = "更新")
    private Date updateTime;

    @ApiModelProperty(value = "删除人id")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;

    @ApiModelProperty(value = "医学建议")
    private String medicalAdvice;

    @ApiModelProperty(value = "其他检查异常结果")
    private String otherCheckAbnormalResults;

    @ApiModelProperty(value = "处理意见")
    private String handleOpinion;

    @ApiModelProperty(value = "职业检查异常结果")
    private String careerCheckAbnormalResults;

    @ApiModelProperty(value = "职业禁忌或疑似职业病名称")
    private String careerIllnessName;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "总检类型（1-人工总检，2-自动总检）")
    private String inspectionType;

    @ApiModelProperty(value = "结论")
    private String conclusionCode;

    @ApiModelProperty(value = "当前所选职业禁忌证")
    private String occupationalTabooNow;

    @ApiModelProperty(value = "当前所选职业禁忌证code")
    private String occupationalTabooNowCode;

    @ApiModelProperty(value = "当前所选职业病")
    private String occupationalDiseasesNow;

    @ApiModelProperty(value = "当前所选职业病code")
    private String occupationalDiseasesNowCode;

    @ApiModelProperty(value = "复查结论")
    private String reviewResult;

    @ApiModelProperty(value = "是否复查")
    private Integer isRecheck;
}
