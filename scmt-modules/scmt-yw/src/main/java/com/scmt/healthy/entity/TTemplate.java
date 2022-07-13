package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.sql.Blob;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 模板
 * </p>
 *
 * @author dengjie
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TTemplate对象", description="模板")
public class TTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id	")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "组合项目id")
    private String baseProjectId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "报告类型")
    private String reportType;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "模板内容")
    private String content;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    @TableLogic
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

    @ApiModelProperty(value = "模板内容文件名")
    private String contentName;

    @ApiModelProperty(value = "模板内容数据")
    @TableField(exist = false)
    private String templateData;
    @ApiModelProperty(value = "组合项目")
    @TableField(exist = false)
    private TPortfolioProject tPortfolioProject;
}
