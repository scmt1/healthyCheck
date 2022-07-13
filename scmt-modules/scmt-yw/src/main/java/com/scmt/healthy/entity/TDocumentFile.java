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
 * @since 2021-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TDocumentFile对象", description="")
public class TDocumentFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "其他表的主键")
    private String foreignKey;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "文件地址")
    private String url;


}
