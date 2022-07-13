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
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author Mike
 * @since 2021-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TBaseProject对象", description="")
public class TBaseProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "项目代码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "科室id")
    private String officeName;

    @ApiModelProperty(value = "项目单位")
    private String unitCode;
    @ApiModelProperty(value = "项目单位")
    private String unitName;

    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @ApiModelProperty(value = "结果类型")
    private String resultType;

    @ApiModelProperty(value = "是否进入小结")
    private String inConclusion;

    @ApiModelProperty(value = "是否进入报告")
    private String inReport;

    @ApiModelProperty(value = "LIS关联码")
    private String relationCode;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @TableField(exist = false)
    private String keyword;

    @TableField(exist = false)
    private List<RelationProjectReference> projectReferenceList;

}
