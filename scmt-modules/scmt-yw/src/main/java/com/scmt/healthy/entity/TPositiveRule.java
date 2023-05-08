package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TPositiveRule对象", description="")
public class TPositiveRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "规则值")
    private String regularValue;

    @ApiModelProperty(value = "阳性规则id")
    private String positiveResultsId;


}
