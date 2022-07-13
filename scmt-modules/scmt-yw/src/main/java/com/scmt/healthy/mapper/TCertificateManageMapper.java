package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCertificateManage;
import com.scmt.healthy.entity.TGroupOrder;
import com.scmt.healthy.entity.TUnitReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 健康证管理表 Mapper 接口
 * </p>
 *
 * @author lbc
 * @since 2021-10-30
 */
public interface TCertificateManageMapper extends BaseMapper<TCertificateManage> {

    /**
     * 不分页查询全部
     */
    List<TCertificateManage> queryTCertificateManageListByNotPage(@Param("TCertificateManage") TCertificateManage tCertificateManage, @Param("searchVo") SearchVo searchVo);

    /**
     * 分页查询信息
     *
     */
    IPage<TCertificateManage> queryTCertificateManageListByPage(@Param(Constants.WRAPPER) QueryWrapper<TCertificateManage> queryWrapper, @Param("page") Page page);

}
