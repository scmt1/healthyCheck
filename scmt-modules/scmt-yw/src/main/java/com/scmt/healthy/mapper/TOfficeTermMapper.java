package com.scmt.healthy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TOfficeTerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 科室术语表 Mapper 接口
 * </p>
 *
 * @author ycy
 * @since 2021-10-21
 */
public interface TOfficeTermMapper extends BaseMapper<TOfficeTerm> {

    /**
     * 分页查询科室术语
     *
     * @param tOfficeTerm
     * @param searchVo
     * @param page
     * @return
     */
    IPage<TOfficeTerm> selectTOfficeTermPageList(@Param("tOfficeTerm")TOfficeTerm tOfficeTerm, @Param("searchVo")SearchVo searchVo, @Param("page")Page page);
}
