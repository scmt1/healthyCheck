package com.scmt.healthy.mapper;

import com.scmt.healthy.entity.TBarcode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户条形码 Mapper 接口
 * </p>
 *
 * @author dengjie
 * @since 2021-10-29
 */
public interface TBarcodeMapper extends BaseMapper<TBarcode> {

    TBarcode getOneByWhere();

    TBarcode getOneByTestNum();

    TBarcode getTBarcodeByPersonId(@Param("personId") String personId, @Param("testNum") String testNum);

    TBarcode getTBarcodeByPersonIdAndItemId(@Param("personId") String personId,@Param("groupItemId") String groupItemId, @Param("testNum") String testNum);

    int checkBarcodeExists(String barcode);
}
