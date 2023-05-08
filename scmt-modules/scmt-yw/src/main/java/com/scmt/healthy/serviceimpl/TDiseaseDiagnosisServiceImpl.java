package com.scmt.healthy.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TDiseaseDiagnosis;
import com.scmt.healthy.entity.TPastMedicalHistory;
import com.scmt.healthy.mapper.TDiseaseDiagnosisMapper;
import com.scmt.healthy.service.ITDiseaseDiagnosisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-03-09
 */
@Service
public class TDiseaseDiagnosisServiceImpl extends ServiceImpl<TDiseaseDiagnosisMapper, TDiseaseDiagnosis> implements ITDiseaseDiagnosisService {
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private TDiseaseDiagnosisMapper tDiseaseDiagnosisMapper;

    @Override
    public IPage<TDiseaseDiagnosis> queryTDiseaseDiagnosisListByPage(TDiseaseDiagnosis  tDiseaseDiagnosis, SearchVo searchVo, PageVo pageVo){
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
        Page<TDiseaseDiagnosis> pageData = new Page<>(page, limit);
        QueryWrapper<TDiseaseDiagnosis> queryWrapper = new QueryWrapper<>();
        if (tDiseaseDiagnosis !=null) {
            queryWrapper = LikeAllField(tDiseaseDiagnosis,searchVo);
        }
        IPage<TDiseaseDiagnosis> result = tDiseaseDiagnosisMapper.selectPage(pageData, queryWrapper);
        return  result;
    }
    @Override
    public void download(TDiseaseDiagnosis tDiseaseDiagnosis, HttpServletResponse response) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<TDiseaseDiagnosis> queryWrapper = new QueryWrapper<>();
        if (tDiseaseDiagnosis !=null) {
            queryWrapper = LikeAllField(tDiseaseDiagnosis,null);
        }
        List<TDiseaseDiagnosis> list = tDiseaseDiagnosisMapper.selectList(queryWrapper);
        for (TDiseaseDiagnosis re : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("主键", re.getId());
            map.put("人员id", re.getPersonId());
            map.put("细菌性痢疾", re.getIsDiseaseOne());
            map.put("伤寒和副伤寒", re.getIsDiseaseTwo());
            map.put("病毒性肝炎（甲型、戊型）", re.getIsDiseaseThree());
            map.put("活动性肺结核", re.getIsDiseaseFour());
            map.put("化脓性或渗出性皮肤病", re.getIsDiseaseFive());
            map.put("手癣、指甲癣", re.getIsDiseaseSix());
            map.put("手部湿疹", re.getIsDiseaseSeven());
            map.put("手部的银屑病或者鳞屑", re.getIsDiseaseEight());
            map.put("删除标识（0-未删除，1-删除）", re.getDelFlag());
            map.put("创建人", re.getCreateId());
            map.put("创建时间", re.getCreateTime());
            map.put("修改人", re.getUpdateId());
            map.put("修改时间", re.getUpdateTime());
            map.put("删除人", re.getDeleteId());
            map.put("删除时间", re.getDeleteTime());
            mapList.add(map);
        }
        FileUtil.createExcel(mapList, "exel.xlsx", response);
    }




    /**
     * 功能描述：构建模糊查询
     * @param tDiseaseDiagnosis 需要模糊查询的信息
     * @return 返回查询
     */
    public QueryWrapper<TDiseaseDiagnosis> LikeAllField(TDiseaseDiagnosis  tDiseaseDiagnosis, SearchVo searchVo) {
        QueryWrapper<TDiseaseDiagnosis> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(tDiseaseDiagnosis.getId())){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.id", tDiseaseDiagnosis.getId()));
        }
        if(StringUtils.isNotBlank(tDiseaseDiagnosis.getPersonId())){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.person_id", tDiseaseDiagnosis.getPersonId()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseOne() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_one", tDiseaseDiagnosis.getIsDiseaseOne()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseTwo() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_two", tDiseaseDiagnosis.getIsDiseaseTwo()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseThree() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_three", tDiseaseDiagnosis.getIsDiseaseThree()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseFour() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_four", tDiseaseDiagnosis.getIsDiseaseFour()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseFive() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_five", tDiseaseDiagnosis.getIsDiseaseFive()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseSix() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_six", tDiseaseDiagnosis.getIsDiseaseSix()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseSeven() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_seven", tDiseaseDiagnosis.getIsDiseaseSeven()));
        }
        if(tDiseaseDiagnosis.getIsDiseaseEight() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.is_disease_eight", tDiseaseDiagnosis.getIsDiseaseEight()));
        }
        if(tDiseaseDiagnosis.getDelFlag() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.del_flag", tDiseaseDiagnosis.getDelFlag()));
        }
        if(StringUtils.isNotBlank(tDiseaseDiagnosis.getCreateId())){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.create_id", tDiseaseDiagnosis.getCreateId()));
        }
        if(tDiseaseDiagnosis.getCreateTime() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.create_time", tDiseaseDiagnosis.getCreateTime()));
        }
        if(StringUtils.isNotBlank(tDiseaseDiagnosis.getUpdateId())){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.update_id", tDiseaseDiagnosis.getUpdateId()));
        }
        if(tDiseaseDiagnosis.getUpdateTime() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.update_time", tDiseaseDiagnosis.getUpdateTime()));
        }
        if(StringUtils.isNotBlank(tDiseaseDiagnosis.getDeleteId())){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.delete_id", tDiseaseDiagnosis.getDeleteId()));
        }
        if(tDiseaseDiagnosis.getDeleteTime() != null){
            queryWrapper.and(i -> i.like("t_disease_diagnosis.delete_time", tDiseaseDiagnosis.getDeleteTime()));
        }
        if(searchVo!=null){
            if(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){
                queryWrapper.lambda().and(i -> i.between(TDiseaseDiagnosis::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));
            }
        }
        queryWrapper.lambda().and(i -> i.eq(TDiseaseDiagnosis::getDelFlag, 0));
        return queryWrapper;

    }
}
