package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.healthy.entity.TDocumentFile;
import com.scmt.healthy.service.ITDocumentFileService;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.mapper.TDocumentFileMapper;
import com.scmt.core.utis.FileUtil;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author
 **/
@Service
public class TDocumentFileServiceImpl extends ServiceImpl<TDocumentFileMapper, TDocumentFile> implements ITDocumentFileService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TDocumentFileMapper tDocumentFileMapper;

    @Override
    public IPage<TDocumentFile> queryTDocumentFileListByPage(TDocumentFile tDocumentFile, SearchVo searchVo, PageVo pageVo) {
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
        Page<TDocumentFile> pageData = new Page<>(page, limit);
        QueryWrapper<TDocumentFile> queryWrapper = new QueryWrapper<>();
        if (tDocumentFile != null) {
            queryWrapper = LikeAllFeild(tDocumentFile, searchVo);
        }
        IPage<TDocumentFile> result = tDocumentFileMapper.selectPage(pageData, queryWrapper);
        return result;
    }

    @Override
    public void download(TDocumentFile tDocumentFile, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TDocumentFile> queryWrapper = new QueryWrapper<>();
        if (tDocumentFile != null) {
            queryWrapper = LikeAllFeild(tDocumentFile, null);
        }
        List<TDocumentFile> list = tDocumentFileMapper.selectList(queryWrapper);
        for (TDocumentFile re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }

    /**
     * 功能描述：构建模糊查询
     *
     * @param tDocumentFile 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDocumentFile> LikeAllFeild(TDocumentFile tDocumentFile, SearchVo searchVo) {
        QueryWrapper<TDocumentFile> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(tDocumentFile.getId())) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getId, tDocumentFile.getId()));
        }
        if (StringUtils.isNotBlank(tDocumentFile.getForeignKey())) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getForeignKey, tDocumentFile.getForeignKey()));
        }
        if (StringUtils.isNotBlank(tDocumentFile.getName())) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getName, tDocumentFile.getName()));
        }
        if (tDocumentFile.getSize() != null) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getSize, tDocumentFile.getSize()));
        }
        if (StringUtils.isNotBlank(tDocumentFile.getType())) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getType, tDocumentFile.getType()));
        }
        if (StringUtils.isNotBlank(tDocumentFile.getUrl())) {
            queryWrapper.lambda().and(i -> i.like(TDocumentFile::getUrl, tDocumentFile.getUrl()));
        }
        return queryWrapper;
    }
}
