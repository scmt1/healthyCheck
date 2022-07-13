package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scmt.core.common.utils.SnowFlakeUtil;
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
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TSectionOffice对象", description="")
public class TSectionOffice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id")
    private String id = SnowFlakeUtil.nextId().toString();;

    @ApiModelProperty(value = "编码")
    private String sectionCode;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @ApiModelProperty(value = "科室名称")
    private String sectionName;

    @ApiModelProperty(value = "科室简拼")
    private String sectionAlphbet;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系人电话")
    private String contactMobile;

    @ApiModelProperty(value = "检查类型")
    private String checkType;

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
}
