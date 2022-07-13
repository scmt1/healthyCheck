package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TSectionOffice;
import com.scmt.healthy.mapper.TSectionOfficeMapper;
import com.scmt.healthy.service.ITSectionOfficeService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author
 **/
@Service
public class TSectionOfficeServiceImpl extends ServiceImpl<TSectionOfficeMapper, TSectionOffice> implements ITSectionOfficeService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TSectionOfficeMapper tSectionOfficeMapper;

    @Override
    public Result<Object> queryTSectionOfficeListByPage(TSectionOffice tSectionOffice, SearchVo searchVo, PageVo pageVo) {
        int page = 1;
        int limit = 10;
        if (pageVo != null) {
            if (pageVo.getPageNumber() != 0) {
                page = pageVo.getPageNumber();
            }
            if (pageVo.getPageSize() != 0) {
                limit = pageVo.getPageSize();
            }
        }
        Page<TSectionOffice> pageData = new Page<>(page, limit);
        QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
        if (tSectionOffice != null) {
            queryWrapper = LikeAllFeild(tSectionOffice, searchVo);
        }
        if (pageVo.getSort() != null) {
            if (pageVo.getOrder().equals("asc")) {
                queryWrapper.orderByAsc("t_section_office." + pageVo.getSort());
            } else {
                queryWrapper.orderByDesc("t_section_office." + pageVo.getSort());
            }
        } else {
            queryWrapper.orderByDesc("t_section_office.create_time");
        }
        IPage<TSectionOffice> result = tSectionOfficeMapper.selectPage(pageData, queryWrapper);
        return ResultUtil.data(result);
    }

    @Override
    public void download(TSectionOffice tSectionOffice, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
        if (tSectionOffice != null) {
            queryWrapper = LikeAllFeild(tSectionOffice, null);
        }
        List<TSectionOffice> list = tSectionOfficeMapper.selectList(queryWrapper);
        for (TSectionOffice re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("编码", re.getSectionCode());
            map.put("科室名称", re.getSectionName());
            map.put("科室简拼", re.getSectionAlphbet());
            map.put("排序", re.getOrderNum());
            map.put("联系人", re.getContact());
            map.put("联系人电话", re.getContactMobile());
            map.put("检查类型", re.getCheckType());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    @Override
    public List<TSectionOffice> queryAllSectionOfficeData(TSectionOffice tSectionOffice) {
        QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();
        if (tSectionOffice != null) {
            queryWrapper = LikeAllFeild(tSectionOffice, null);
        }
        queryWrapper.orderByAsc("order_num");
        return tSectionOfficeMapper.selectList(queryWrapper);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tSectionOffice 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TSectionOffice> LikeAllFeild(TSectionOffice tSectionOffice, SearchVo searchVo) {
        QueryWrapper<TSectionOffice> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(tSectionOffice.getSectionCode())) {
            queryWrapper.and(i -> i.like("t_section_office.section_code", tSectionOffice.getSectionCode()));
        }
        if (StringUtils.isNotBlank(tSectionOffice.getSectionName())) {
            queryWrapper.and(i -> i.like("t_section_office.section_name", tSectionOffice.getSectionName()));
        }
        if (StringUtils.isNotBlank(tSectionOffice.getSectionAlphbet())) {
            queryWrapper.and(i -> i.like("t_section_office.section_alphbet", tSectionOffice.getSectionAlphbet()));
        }
        if (StringUtils.isNotBlank(tSectionOffice.getContact())) {
            queryWrapper.and(i -> i.like("t_section_office.contact", tSectionOffice.getContact()));
        }
        if (StringUtils.isNotBlank(tSectionOffice.getContactMobile())) {
            queryWrapper.and(i -> i.like("t_section_office.contact_mobile", tSectionOffice.getContactMobile()));
        }
        if (StringUtils.isNotBlank(tSectionOffice.getCheckType())) {
            String[] split = tSectionOffice.getCheckType().split(",");
            Consumer<QueryWrapper<TSectionOffice>> queryWrapperConsumer = null;
            queryWrapperConsumer = i -> {
                for (String s : split) {
                    i.or().like("t_section_office.check_type", s);
                }
            };
            queryWrapper.and(queryWrapperConsumer);
        }
        if (StringUtils.isNotBlank(tSectionOffice.getDepartmentId())) {
            queryWrapper.and(i -> i.eq("t_section_office.department_id", tSectionOffice.getDepartmentId()));
        }
        if (tSectionOffice.getDelFlag() != null) {
            queryWrapper.and(i -> i.like("t_section_office.del_flag", tSectionOffice.getDelFlag()));
        }
        if (tSectionOffice.getCreateId() != null) {
            queryWrapper.and(i -> i.like("t_section_office.create_id", tSectionOffice.getCreateId()));
        }
        if (tSectionOffice.getCreateTime() != null) {
            queryWrapper.and(i -> i.like("t_section_office.create_time", tSectionOffice.getCreateTime()));
        }
        if (tSectionOffice.getUpdateId() != null) {
            queryWrapper.and(i -> i.like("t_section_office.update_id", tSectionOffice.getUpdateId()));
        }
        if (tSectionOffice.getUpdateTime() != null) {
            queryWrapper.and(i -> i.like("t_section_office.update_time", tSectionOffice.getUpdateTime()));
        }
        if (tSectionOffice.getDeleteId() != null) {
            queryWrapper.and(i -> i.like("t_section_office.delete_id", tSectionOffice.getDeleteId()));
        }
        if (tSectionOffice.getDeleteTime() != null) {
            queryWrapper.and(i -> i.like("t_section_office.delete_time", tSectionOffice.getDeleteTime()));
        }
        if (searchVo != null) {
            if (StringUtils.isNotBlank(searchVo.getStartDate()) && StringUtils.isNotBlank(searchVo.getEndDate())) {
                queryWrapper.and(i -> i.between("t_section_office.create_time", searchVo.getStartDate(), searchVo.getEndDate()));
            }
        }
        return queryWrapper;

    }
}
