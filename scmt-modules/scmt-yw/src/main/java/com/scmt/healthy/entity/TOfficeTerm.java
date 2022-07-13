package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 科室术语表
 * </p>
 *
 * @author ycy
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOfficeTerm对象", description="科室术语表")
public class TOfficeTerm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "科室Id")
    private String officeId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "体检类型")
    private String inspectType;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactors;

    @ApiModelProperty(value = "危害因素text")
    private String hazardFactorsText;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "术语内容")
    private String content;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "在岗状态编码")
    private String workStateCode;

    @ApiModelProperty(value = "在岗状态名称")
    private String workStateText;

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

    @ApiModelProperty("科室名称")
    @TableField(exist = false)
    private String officeName;

    @ApiModelProperty("类型名称")
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "科室数据")
    @TableField(exist = false)
    private TSectionOffice officeData;


}
