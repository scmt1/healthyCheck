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
 * 用户条形码
 * </p>
 *
 * @author dengjie
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TBarcode对象", description="用户条形码")
public class TBarcode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "分组项目id")
    private String groupItemId;

    @ApiModelProperty(value = "条形码内容")
    private String barcode;

    @ApiModelProperty(value = "生成日期")
    private Date createTime;

    @ApiModelProperty(value = "条码类型 1检验条码 2登记条码")
    private Integer type;

    @ApiModelProperty(value = "体检编号")
    private String testNum;

}
