package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @author mike
 * @since 2021-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TProType对象", description = "")
public class TProType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "删除状态(0-未删除，1-已删除)")
    private Integer delFlag;

    @ApiModelProperty(value = "父节点id")
    private String parentId;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "子集")
    @TableField(exist = false)
    private List<TProType> children = new ArrayList<>();

    @ApiModelProperty(value = "异步加载")
    @TableField(exist = false)
    private Boolean _loading = false;

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
}
