package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mike
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RelationComboItem对象", description="")
public class RelationComboItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "套餐id")
    private String comboId;

    @ApiModelProperty(value = "套餐内项目id")
    private String comItemId;


}
