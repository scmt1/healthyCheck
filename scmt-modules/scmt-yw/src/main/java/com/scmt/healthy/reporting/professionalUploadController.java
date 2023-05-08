package com.scmt.healthy.reporting;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.healthy.entity.*;
import com.scmt.healthy.service.ITCertificateManageService;
import com.scmt.healthy.service.ITPastMedicalHistoryService;
import com.scmt.healthy.service.ITTokenService;
import com.scmt.healthy.serviceimpl.TDepartItemResultServiceImpl;
import com.scmt.healthy.serviceimpl.TDiseaseDiagnosisServiceImpl;
import com.scmt.healthy.serviceimpl.TGroupPersonServiceImpl;
import com.scmt.healthy.serviceimpl.TInspectionRecordServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.scmt.healthy.utils.HttpClient.sendPostRequestMh;

@RestController
@RequestMapping("/scmt/professionalUpload")
public class professionalUploadController {
    @Autowired
    private ITCertificateManageService tCertificateManageService;

    @Autowired
    private ITPastMedicalHistoryService tPastMedicalHistoryService;

    @Autowired
    private TDepartItemResultServiceImpl tDepartItemResultService;

    @Autowired
    private TGroupPersonServiceImpl tGroupPersonService;

    @Autowired
    private TDiseaseDiagnosisServiceImpl tDiseaseDiagnosisService;

    @Autowired
    private TInspectionRecordServiceImpl tInspectionRecordService;

    @Autowired
    private EmploymentUpload employmentUpload;

    @Autowired
    private ITTokenService tokenService;

    /**
     * 功能描述：从业体检上传
     *
     * @param
     * @return 返回新增结果
     */
    @ApiOperation("从业体检上传")
    @PostMapping("uploadEmployeeInfo")
    public Result<Object> uploadEmployeeInfo(@RequestParam String[] ids) {
        if (ids == null || ids.length == 0) {
            return ResultUtil.error("参数为空，请联系管理员！！");
        }
        //通过传入的id查询人员信息
        try {
            List<TEmploymentToken> list = tokenService.list();
            if (list.size() <= 0 || list == null){
                tokenService.getToken();
                list = tokenService.list();
            }
            Reporting reporting = JSONObject.parseObject(list.get(0).getToken(), Reporting.class);
            Token token1 = JSONObject.parseObject(reporting.getData(), Token.class);
            if (reporting.getCode().equals("200")) {
                //拼接上传接口参数
                List<TGroupPerson> personList = tGroupPersonService.getByPersonIdList(ids);
                QueryWrapper<TDepartItemResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.in("person_id", ids);
                QueryWrapper<TCertificateManage> queryWrapper2 = new QueryWrapper();
                queryWrapper2.in("person_id", ids);
                List<TDepartItemResult> tDepartItemResults = tDepartItemResultService.list(queryWrapper);
                List<TPastMedicalHistory> listPastMedicalHistory = tPastMedicalHistoryService.getByTPastMedicalHistoryControllerList(ids);
                QueryWrapper<TDiseaseDiagnosis> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.in("person_id", ids);
                List<TDiseaseDiagnosis> TDiseaseDiagnosisList = tDiseaseDiagnosisService.list(queryWrapper1);
                List<TInspectionRecord> TInspectionRecordList = tInspectionRecordService.getInspectionRecordList(ids);
                List<TCertificateManage> TCertificateManageList = tCertificateManageService.getByTCertificateManageList(ids);
                if (TCertificateManageList == null || TCertificateManageList.size() == 0) {
                    return ResultUtil.error("查询健康证信息失败！！");
                }
                if (personList == null || personList.size() == 0) {
                    return ResultUtil.error("查询人员信息信息失败！！");
                }
                if (tDepartItemResults == null || tDepartItemResults.size() == 0) {
                    return ResultUtil.error("查询健康证信息失败！！");
                }
                for (int i = 0; i < TCertificateManageList.size(); i++) {
                    TCertificateManage tCertificateManage = TCertificateManageList.get(i);
                    List<TGroupPerson> collectPersonList = personList.stream().filter(ii -> tCertificateManage.getPersonId().equals(ii.getId())).collect(Collectors.toList());
                    if (collectPersonList == null) {
                        continue;
                    }
                    Reporting res = personFor(collectPersonList.get(0), token1.getToken());

                    if (res.getCode().equals("200")||tCertificateManage.getBasicPersonId()!=null) {
                        QueryWrapper<TCertificateManage>  TCertificateManageQueryWrapper = new QueryWrapper<>();
                        TCertificateManageQueryWrapper.eq("person_id",tCertificateManage.getPersonId());
                        tCertificateManage.setBasicPersonId(res.getData());
                        boolean b = tCertificateManageService.update(tCertificateManage,TCertificateManageQueryWrapper);
                        if (!b) {
                            tCertificateManage.setExceptionMessage("更新健康证网报上传Id失败，请联系管理员");
                            tCertificateManage.setIsUpload(2);
                            continue;
                        }
                        //筛选职业史
                        List<TPastMedicalHistory> collectListPastMedicalHistory = listPastMedicalHistory.stream().filter(ii -> tCertificateManage.getPersonId().equals(ii.getPersonId())).collect(Collectors.toList());
                        //筛选检查结果
                        List<TDepartItemResult> collectTDepartItemResults = tDepartItemResults.stream().filter(ii -> tCertificateManage.getPersonId().equals(ii.getPersonId())).collect(Collectors.toList());
                        //筛选问诊
                        List<TDiseaseDiagnosis> collectTDiseaseDiagnosisList = TDiseaseDiagnosisList.stream().filter(ii -> tCertificateManage.getPersonId().equals(ii.getPersonId())).collect(Collectors.toList());
                        //筛选总检
                        List<TInspectionRecord> collectTInspectionRecordList = TInspectionRecordList.stream().filter(ii -> tCertificateManage.getPersonId().equals(ii.getPersonId())).collect(Collectors.toList());

                        Reporting information = information(collectListPastMedicalHistory, collectTDepartItemResults, collectTDiseaseDiagnosisList, collectTInspectionRecordList, token1.getToken());
                        if ("200".equals(information.getCode()) ||tCertificateManage.getPhysicalExaminationId()!=null) {
                            tCertificateManage.setPhysicalExaminationId(information.getData());
                            boolean update = tCertificateManageService.update(tCertificateManage, TCertificateManageQueryWrapper);
                            if (!update){
                                tCertificateManage.setExceptionMessage("更新健康证网报上传Id失败，请联系管理员");
                                tCertificateManage.setIsUpload(2);
                                continue;
                            }
                            Reporting reporting1 = healthCertificate(TCertificateManageList.get(i), token1.getToken());
                            if ("200".equals(reporting1.getCode()) || tCertificateManage.getMedicalCertificateId()!=null) {
                                tCertificateManage.setMedicalCertificateId(reporting1.getData());
                                tCertificateManage.setIsUpload(1);
                                tCertificateManage.setExceptionMessage("");
                                boolean update1 = tCertificateManageService.update(tCertificateManage,TCertificateManageQueryWrapper);
                                if (!update1){
                                    tCertificateManage.setExceptionMessage("更新健康证网报上传Id失败，请联系管理员");
                                    tCertificateManage.setIsUpload(2);
                                    continue;
                                }
                            } else {
                                getMessage(reporting1.getMessage(), tCertificateManage);
                                tCertificateManage.setIsUpload(2);
                                continue;
                            }
                        } else {
                            tCertificateManage.setIsUpload(2);
                            getMessage(information.getMessage(), tCertificateManage);
                            continue;
                        }
                    } else {
                        tCertificateManage.setIsUpload(2);
                        getMessage(res.getMessage(), tCertificateManage);
                        continue;
                    }
                }
                return ResultUtil.success("调用接口成功");
            } else {
                return ResultUtil.error("上传失败:登录获取token失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("上传异常:" + e.getMessage());
        }
    }

    /**
     * 拼接人员参数
     *
     * @param tGroupPerson
     * @param token
     * @return
     */
    public Reporting personFor(TGroupPerson tGroupPerson, String token) {
        if (tGroupPerson == null || StringUtils.isBlank(token)) {
            return null;
        }
        //通过传入的id查询人员信息
        long time = System.currentTimeMillis();
        String personId = "2c1728c7bd9332537337f5adc9f9266d";
        String personurl = employmentUpload.getReportingIp() + "/adapter/http/ehrcjob/v1.0/api/cyrytj/uploadEmployeeInfo?";
        String personMd5 = new Digester(DigestAlgorithm.MD5).digestHex(personId + employmentUpload.getHieAppKey() + time + employmentUpload.getHieAdapter());
        String tGroupPersonurl = personurl + "hie_event_code=" + personId + "&hie_app_key=" + employmentUpload.getHieAppKey() + "&hie_time_stamp=" + time + "&hie_secret=" + personMd5;
        Map<String, Object> mapPerson = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //1.筛人员信息
        //2.拼接人员上传接口参数
        //出生日期
        if (tGroupPerson.getBirth()!=null){
            mapPerson.put("csrq", simpleDateFormat.format(tGroupPerson.getBirth()));
        }else {
            mapPerson.put("csrq", null);
        }
        //从业类型
        if (tGroupPerson.getCertificateType()!=null){
            mapPerson.put("cylx", tGroupPerson.getCertificateType());
        }else {
            mapPerson.put("cylx", null);
        }
        //登记号码
        if (tGroupPerson.getRegistrationNumber()!=null){
            mapPerson.put("djhm", tGroupPerson.getRegistrationNumber());
        }else {
            mapPerson.put("djhm", null);
        }
        //登记日期
        if (tGroupPerson.getRegistDate()!=null){
            mapPerson.put("djrq", simpleDateFormat.format(tGroupPerson.getRegistDate()));
        }else {
            mapPerson.put("djrq", null);
        }

        //id
        if ( tGroupPerson.getBasicPersonId()!=null){
            mapPerson.put("id", tGroupPerson.getBasicPersonId());
        }else {
            mapPerson.put("id", null);
        }
        //联系电话
        if (tGroupPerson.getMobile()!=null){
            mapPerson.put("lxdh", tGroupPerson.getMobile());
        }else {
            mapPerson.put("lxdh", null);
        }

        //性别
        if (tGroupPerson.getSex().equals("男")) {
            mapPerson.put("xb", "1");
        } else {
            mapPerson.put("xb", "2");
        }
        //姓名
        if (tGroupPerson.getPersonName()!=null){
            mapPerson.put("xm", tGroupPerson.getPersonName());
        }else {
            mapPerson.put("xm", null);
        }

        //用人单位地址
        if (tGroupPerson.getAddress()!=null){
            mapPerson.put("yrdwdz", tGroupPerson.getAddress());
        }else {
            mapPerson.put("yrdwdz", null);
        }

        //用人信用代码
        if (tGroupPerson.getDept()!=null){
            mapPerson.put("yrdwmc", tGroupPerson.getDept());
        }else {
            mapPerson.put("yrdwmc", null);
        }


        //用人单位信用代码
        if (tGroupPerson.getUscc()!=null){
            mapPerson.put("yrdwxydm", tGroupPerson.getUscc());
        }else {
            mapPerson.put("yrdwxydm", null);
        }

        //证件号码
        if (tGroupPerson.getIdCard()!=null){
            mapPerson.put("zjhm", tGroupPerson.getIdCard());
        }else {
            mapPerson.put("zjhm", null);
        }

        //证件类型
        mapPerson.put("zjlx", "10");
        //民族
        if (tGroupPerson.getNation()!=null){
            mapPerson.put("mz", tGroupPerson.getNation());
        }else {
            mapPerson.put("mz", null);
        }
        HttpHeaders header = header(token);
        String res = sendPostRequestMh(tGroupPersonurl, mapPerson, header);
        Reporting account = JSONObject.parseObject(res, Reporting.class);
        return account;
    }


    //拼接体检信息上传接口参数
    public Reporting information(List<TPastMedicalHistory> listPastMedicalHistory, List<TDepartItemResult> tDepartIte, List<TDiseaseDiagnosis> TDiseaseDiagnosisList, List<TInspectionRecord> TInspectionRecordList, String token) {
        if (listPastMedicalHistory == null || tDepartIte==null || TDiseaseDiagnosisList==null || TInspectionRecordList==null || StringUtils.isBlank(token)) {
            return null;
        }
        //通过传入的id查询体检信息
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = System.currentTimeMillis();
        String tPastMedicalHistorId = "ffbbb5fe0c5cd6f4395f149ab40aac55";
        String tPastMedicalHistory1 = employmentUpload.getReportingIp() + "/adapter/http/ehrcjob/v1.0/api/cyrytj/uploadPvHlthExamRst?";
        String tPastMedicalHistoryMd5 = new Digester(DigestAlgorithm.MD5).digestHex(tPastMedicalHistorId + employmentUpload.getHieAppKey() + time + employmentUpload.getHieAdapter());
        String tPastMedicalHistoryurl = tPastMedicalHistory1 + "hie_event_code=" + tPastMedicalHistorId + "&hie_app_key=" + employmentUpload.getHieAppKey() + "&hie_time_stamp=" + time + "&hie_secret=" + tPastMedicalHistoryMd5;
        Map<String, Object> maptDepartIte = new HashMap<>();
        TDiseaseDiagnosis tDiseaseDiagnosis = TDiseaseDiagnosisList.get(0);
        //疾病_病毒性肝炎
        if (tDiseaseDiagnosis.getIsDiseaseThree() != null) {
            maptDepartIte.put("jbBdxgy", tDiseaseDiagnosis.getIsDiseaseThree());
        }else {
            maptDepartIte.put("jbBdxgy", 0);
        }
        //疾病_活动性肺结核
        if (tDiseaseDiagnosis.getIsDiseaseFour() != null) {
            maptDepartIte.put("jbHdxfjh", tDiseaseDiagnosis.getIsDiseaseFour());
        }else {
            maptDepartIte.put("jbHdxfjh", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseFive() != null) {
            maptDepartIte.put("jbPfb", tDiseaseDiagnosis.getIsDiseaseFive());
        }else {
            maptDepartIte.put("jbPfb", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseSeven() != null) {
            maptDepartIte.put("jbSbsz", tDiseaseDiagnosis.getIsDiseaseSeven());
        }else {
            maptDepartIte.put("jbSbsz", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseTwo() != null) {
            maptDepartIte.put("jbSh", tDiseaseDiagnosis.getIsDiseaseTwo());
        }else {
            maptDepartIte.put("jbSh", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseSix() != null) {
            maptDepartIte.put("jbSx", tDiseaseDiagnosis.getIsDiseaseSix());
        }else {
            maptDepartIte.put("jbSx", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseOne() != null) {
            maptDepartIte.put("jbXjxlj", tDiseaseDiagnosis.getIsDiseaseOne());
        }else {
            maptDepartIte.put("jbXjxlj", 0);
        }
        if (tDiseaseDiagnosis.getIsDiseaseEight() != null) {
            maptDepartIte.put("jbYxb", tDiseaseDiagnosis.getIsDiseaseEight());
        }else {
            maptDepartIte.put("jbYxb", 0);
        }

        TInspectionRecord tInspectionRecord = TInspectionRecordList.get(0);
        maptDepartIte.put("zjys", tInspectionRecord.getInspectionDoctor());
        maptDepartIte.put("id", tInspectionRecord.getPhysicalExaminationId());
        if (tInspectionRecord.getHealthCertificateConditions().equals("合格")) {
            maptDepartIte.put("jcjg", "1");
        } else if (tInspectionRecord.getHealthCertificateConditions().equals("不合格")) {
            maptDepartIte.put("jcjg", "0");
        }
        if (tInspectionRecord.getHealthCertificateConditions() != null) {
            maptDepartIte.put("jcjl", tInspectionRecord.getHealthCertificateConditions());
        }else {
            maptDepartIte.put("jcjl", null);
        }
        if (tInspectionRecord.getInspectionDate() != null) {
            maptDepartIte.put("jcrq", simpleDateFormat.format(tInspectionRecord.getInspectionDate()));
        }else {
            maptDepartIte.put("jcrq", null);
        }
        if (tInspectionRecord.getInspectionDoctor() != null) {
            maptDepartIte.put("zjys", tInspectionRecord.getInspectionDoctor());
        }else {
            maptDepartIte.put("zjys", null);
        }
        if (tInspectionRecord.getRegistrationNumber() != null) {
            maptDepartIte.put("djhm", tInspectionRecord.getRegistrationNumber());
        }else {
            maptDepartIte.put("djhm", null);
        }
        if (tInspectionRecord.getIdCard() != null) {
            maptDepartIte.put("zjhm", tInspectionRecord.getIdCard());
        }else {
            maptDepartIte.put("zjhm", null);
        }

        maptDepartIte.put("jcQt", "无");
        maptDepartIte.put("tzQtMs", "无");

        for (int i = 0; i < tDepartIte.size(); i++) {
            TDepartItemResult tDepartItemResult = tDepartIte.get(i);
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("谷丙转氨酶[ALT]") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("jcGgnGbJg", tDepartItemResult.getResult());
                maptDepartIte.put("jcGgnGbJys", tDepartItemResult.getCheckDoc());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("沙门氏菌") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("jcGszSmjJg", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("志贺氏菌") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("jcGszZhjJg", tDepartItemResult.getResult());
                maptDepartIte.put("jcGszZhjJys", tDepartItemResult.getCheckDoc());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("甲肝Igm") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("jcJgktJg", tDepartItemResult.getResult());
                maptDepartIte.put("jcJgktJys", tDepartItemResult.getCheckDoc());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("戊肝Igm") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("jcWgktJg", tDepartItemResult.getResult());
                maptDepartIte.put("jcWgktJys", tDepartItemResult.getCheckDoc());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("肺") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("tzF", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("肝") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("tzG", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf( "脾") >-1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("tzP", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("全身皮肤") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("tzPf", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("心") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("tzX", tDepartItemResult.getResult());
            }
            if (tDepartItemResult.getOrderGroupItemProjectName() != null && tDepartItemResult.getOrderGroupItemProjectName().indexOf("胸部正位片") > -1&&tDepartItemResult.getResult()!=null) {
                maptDepartIte.put("xt", tDepartItemResult.getResult());
                maptDepartIte.put("xtYs", tDepartItemResult.getCheckDoc());
            }
        }

        maptDepartIte.put("TzYs", "无");
        maptDepartIte.put("jwbsLj", "无");
        maptDepartIte.put("jwbsQt", "无");

        for (int i = 0; i < listPastMedicalHistory.size(); i++) {
            TPastMedicalHistory tPastMedicalHistory = listPastMedicalHistory.get(i);
            //既往病史_肺结核
            if (tPastMedicalHistory.getDiseaseName().indexOf("肺结核") > -1 && tPastMedicalHistory.getYesOrNoSick() != null) {
                maptDepartIte.put("jwbsFjh", tPastMedicalHistory.getYesOrNoSick());
            }
            //既往病史_肝炎
            if (tPastMedicalHistory.getDiseaseName().indexOf("肝炎") > -1 && tPastMedicalHistory.getYesOrNoSick() != null) {
                maptDepartIte.put("jwbsGy", tPastMedicalHistory.getYesOrNoSick());
            }
            //既往病史_痢疾
            if (tPastMedicalHistory.getDiseaseName().indexOf("痢疾") > -1 && tPastMedicalHistory.getYesOrNoSick() != null) {
                maptDepartIte.put("jwbsLj", tPastMedicalHistory.getYesOrNoSick());
            }
            //既往病史_皮肤病
            if (tPastMedicalHistory.getDiseaseName().indexOf("皮肤病") > -1 && tPastMedicalHistory.getYesOrNoSick() != null) {
                maptDepartIte.put("jwbsPfb", tPastMedicalHistory.getYesOrNoSick());
            }
            //既往病史_伤寒
            if (tPastMedicalHistory.getDiseaseName().indexOf("伤寒") > -1 && tPastMedicalHistory.getYesOrNoSick() != null) {
                maptDepartIte.put("jwbsSh", tPastMedicalHistory.getYesOrNoSick());
            }
            //既往病史_其他
            if (tPastMedicalHistory.getDiseaseName() != null && tPastMedicalHistory.getDiseaseName().equals("其他")) {
                maptDepartIte.put("jwbsQt", tPastMedicalHistory.getYesOrNoSick());
            }
        }

        HttpHeaders header = header(token);
        //发送请求
        String res = sendPostRequestMh(tPastMedicalHistoryurl, maptDepartIte, header);
        //转回实体
        Reporting account = JSONObject.parseObject(res, Reporting.class);
        return account;
    }

    /**
     * 健康证参数
     *
     * @param tCertificateManage
     * @param token
     * @return
     */
    public Reporting healthCertificate(TCertificateManage tCertificateManage, String token) {
        if (tCertificateManage == null || StringUtils.isBlank(token)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = System.currentTimeMillis();
        String tCertificateManageId = "bd6118d133eb0f359d62051071472685";
        String tCertificateManages = employmentUpload.getReportingIp() + "/adapter/http/ehrcjob/v1.0/api/cyrytj/uploadPvHlthExamCert?";
        String tCertificateManageMd5 = new Digester(DigestAlgorithm.MD5).digestHex(tCertificateManageId + employmentUpload.getHieAppKey() + time + employmentUpload.getHieAdapter());
        String tCertificateManageurl = tCertificateManages + "hie_event_code=" + tCertificateManageId + "&hie_app_key=" + employmentUpload.getHieAppKey() + "&hie_time_stamp=" + time + "&hie_secret=" + tCertificateManageMd5;
        //健康证信息
        Map<String, Object> mapTCertificateManage = new HashMap<>();
        //通过传入的id查询健康证信息
        if (tCertificateManage.getBirth()!=null){
            mapTCertificateManage.put("csrq", simpleDateFormat.format(tCertificateManage.getBirth()));
        }else {
            mapTCertificateManage.put("csrq", null);
        }
        if (tCertificateManage.getCertificateType()!=null){
            mapTCertificateManage.put("cylx", tCertificateManage.getCertificateType());
        }else {
            mapTCertificateManage.put("cylx", null);
        }
       if (tCertificateManage.getRegistrationNumber()!=null){
           mapTCertificateManage.put("djhm", tCertificateManage.getRegistrationNumber());
       }else {
           mapTCertificateManage.put("djhm", null);
       }
        if (tCertificateManage.getRegistDate()!=null){
            mapTCertificateManage.put("djrq", simpleDateFormat.format(tCertificateManage.getRegistDate()));
        }else {
            mapTCertificateManage.put("djrq", null);
        }
        if (tCertificateManage.getDateOfIssue()!=null){
            mapTCertificateManage.put("fzrq", tCertificateManage.getDateOfIssue());
        }else {
            mapTCertificateManage.put("fzrq", null);
        }
        if (tCertificateManage.getHealthCcertificate()!=null){
            mapTCertificateManage.put("hgzbh", tCertificateManage.getHealthCcertificate());
        }else {
            mapTCertificateManage.put("hgzbh", null);
        }
        if (tCertificateManage.getMedicalCertificateId()!=null){
            mapTCertificateManage.put("id", tCertificateManage.getMedicalCertificateId());
        }else {
            mapTCertificateManage.put("id", null);
        }
        if (tCertificateManage.getNation()!=null){
            mapTCertificateManage.put("mz", tCertificateManage.getNation());
        }else {
            mapTCertificateManage.put("mz", "01");
        }
        if (tCertificateManage.getHeadImg()!=null){
            mapTCertificateManage.put("tx", tCertificateManage.getHeadImg());
        }else {
            mapTCertificateManage.put("tx", null);
        }
        if (StringUtils.isNotBlank(tCertificateManage.getSex())){
            if (tCertificateManage.getSex().equals("男")) {
                mapTCertificateManage.put("xb", "1");
            } else {
                mapTCertificateManage.put("xb", "2");
            }
        }else {
            mapTCertificateManage.put("xb", null);
        }
        if (tCertificateManage.getName()!=null){
            mapTCertificateManage.put("xm", tCertificateManage.getName());

        }else {
            mapTCertificateManage.put("xm", null);
        }
        if ( tCertificateManage.getIdCard()!=null){
            mapTCertificateManage.put("zjhm", tCertificateManage.getIdCard());
        }else {
            mapTCertificateManage.put("zjhm", null);
        }
        if (tCertificateManage.getMobile()!=null){
            mapTCertificateManage.put("lxdh", tCertificateManage.getMobile());
        }else {
            mapTCertificateManage.put("lxdh", null);
        }
        mapTCertificateManage.put("zjlx", "10");

        HttpHeaders header = header(token);
        String res = sendPostRequestMh(tCertificateManageurl, mapTCertificateManage, header);
        Reporting account = JSONObject.parseObject(res, Reporting.class);
        return account;
    }

    /**
     * 拼接 请求头（设置Headers 的 token（accesstoken） 值）
     *
     * @param token
     * @return
     */
    public HttpHeaders header(String token) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("accesstoken", token);
        header.add("ehrkey", "ehr#health@check");
        return header;
    }

    /**
     * 更新错误信息
     *
     * @param message 错误信息
     * @param one
     */
    public void getMessage(String message, TCertificateManage one) {
        QueryWrapper<TCertificateManage>  TCertificateManageQueryWrapper = new QueryWrapper<>();
        TCertificateManageQueryWrapper.eq("person_id",one.getPersonId());
        one.setExceptionMessage(message);
        one.setIsUpload(2);
        tCertificateManageService.update(one,TCertificateManageQueryWrapper);
    }

}

