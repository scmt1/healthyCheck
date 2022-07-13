package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 个人症状询问
 * </p>
 *
 * @author dengjie
 * @since 2021-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TSymptom对象", description="个人症状询问")
public class TSymptom implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类别")
    private String type;

    @ApiModelProperty(value = "项目名称")
    private String projectName;
    @ApiModelProperty(value = "项目编码")
    private String code;

    @ApiModelProperty(value = "程度")
    private String degree;

    @ApiModelProperty(value = "病程时间")
    private String courseTime;

    @ApiModelProperty(value = "人员主键")
    private String personId;

    @ApiModelProperty(value = "创建人-检查医生")
    private String createId;
    @ApiModelProperty(value = "创建人-检查医生")
    private String createName;

    @ApiModelProperty(value = "创建时间-检查时间")
    private Date createTime;


}
