package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.annotation.SystemLog;
import com.scmt.core.common.enums.LogType;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TConclusion;
import com.scmt.healthy.entity.TLisData;
import com.scmt.healthy.service.ITConclusionService;
import com.scmt.healthy.service.ITLisDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 *@author
 **/
@RestController
@Api(tags =" tConclusion数据接口")
@RequestMapping("/scmt/tConclusion")
public class TConclusionController {
	@Autowired
	private ITConclusionService tConclusionService;
	@Autowired
	private SecurityUtil securityUtil;

	/**
	* 功能描述：实现分页查询
	* @param searchVo 需要模糊查询的信息
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	@SystemLog(description = "分页查询tConclusion数据", type = LogType.OPERATION)
	@ApiOperation("分页查询tConclusion数据")
	@GetMapping("queryTConclusionList")
	public Result<Object> queryTConclusionList(TConclusion tConclusion, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TConclusion> result =tConclusionService.queryTConclusionDataListByPage(tConclusion, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error("查询异常:" + e.getMessage());
		}
	}

}
