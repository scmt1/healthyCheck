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
 * 危害因素体检结论表
 * </p>
 *
 * @author dengjie
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TdTjBadrsns对象", description="危害因素体检结论表")
public class TdTjBadrsns implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "有害因素编码")
    @TableField("BADRSN_CODE")
    private String badrsnCode;

    @ApiModelProperty(value = "有害因素名称")
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "体检结论代码")
    @TableField("EXAM_CONCLUSION_CODE")
    private String examConclusionCode;

    @ApiModelProperty(value = "疑似职业病代码")
    @TableField("YSZYB_CODE")
    private String yszybCode;

    @ApiModelProperty(value = "职业禁忌证代码")
    @TableField("ZYJJZ_CODE")
    private String zyjjzCode;

    @ApiModelProperty(value = "其他疾病或异常描述")
    @TableField("QTJB_NAME")
    private String qtjbName;

    @ApiModelProperty(value = "其他危害因素名称")
    @TableField("OTHER_BADRSN")
    private String otherBadrsn;

    @ApiModelProperty(value = "体检主表外键")
    @TableField("FK_BHK_ID")
    private String fkBhkId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty("删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deleteTime;

}
