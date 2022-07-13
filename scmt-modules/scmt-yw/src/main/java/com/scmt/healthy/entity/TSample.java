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
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author Mike
 * @since 2021-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TSample对象", description = "")
public class TSample implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "条码份数")
    private Integer barcodeNum;

    @ApiModelProperty(value = "规格")
    private String spec;

    @ApiModelProperty(value = "是否需要采血，0：否，1：是")
    private Integer needTakeBlood;

    @ApiModelProperty(value = "标本代码")
    private String code;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "是否打印")
    private Integer isPrint;

    @ApiModelProperty(value = "关联码")
    private String liscode;

    @ApiModelProperty(value = "容量")
    private String capacity;

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
}
