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
 * @author ycy
 * @since 2021-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RelationBasePortfolio对象", description="")
public class RelationBasePortfolio implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "基础项目id")
    private String baseProjectId;

    @ApiModelProperty(value = "组合项目id")
    private String portfolioProjectId;


}
