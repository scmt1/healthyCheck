package com.scmt.healthy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.common.constant.MessageConstant;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.utils.SecurityUtil;
import com.scmt.core.common.vo.PageVo;
import com.scmt.core.common.vo.Result;
import com.scmt.core.common.vo.SearchVo;
import com.scmt.healthy.entity.TCheckOrg;
import com.scmt.healthy.entity.TCombo;
import com.scmt.healthy.service.ITCheckOrgService;
import com.scmt.healthy.service.ITComboService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *@author
 **/
@RestController
@Api(tags =" 体检机构数据接口")
@RequestMapping("/scmt/tCheckOrg")
public class TCheckOrgController {
	@Autowired
	private ITCheckOrgService tCheckOrgService;
	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private ITComboService tComboService;

	/**
	* 功能描述：新增体检机构数据
	* @param tCheckOrg 实体
	* @return 返回新增结果
	*/
	@ApiOperation("新增体检机构数据")
	@PostMapping("addTCheckOrg")
	public Result<Object> addTCheckOrg(@RequestBody TCheckOrg tCheckOrg){
		if(tCheckOrg != null){
			try {
				//校验体检机构是否存在
				Boolean exist = tCheckOrgService.checkOrgName(tCheckOrg);
				if(exist){
					return ResultUtil.error("体检机构名称重复!");
				}
				Boolean avatar = tCheckOrgService.handleCheckOrgImg(tCheckOrg, tCheckOrg.getAvatar());
				if(!avatar){
					return ResultUtil.error("体检机构头像未上传!");
				}
				Boolean images = tCheckOrgService.handleCheckOrgImg(tCheckOrg, tCheckOrg.getImages());
				if(!images){
					return ResultUtil.error("体检机构背景图未上传!");
				}
				tCheckOrg.setDelFlag(0);
				tCheckOrg.setCreateBy(securityUtil.getCurrUser().getId());
				tCheckOrg.setCreateTime(new Date());
				Boolean res = tCheckOrgService.save(tCheckOrg);
				if (res) {
					return ResultUtil.data(res, MessageConstant.SAVE_SUCCESS);
				} else {
					return ResultUtil.error(MessageConstant.SAVE_FAIL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ResultUtil.error(MessageConstant.SAVE_EXCEPTION + e.getMessage());
			}
		}
		return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);

	}

	/**
	* 功能描述：更新数据
	* @param tCheckOrg 实体
	* @return 返回更新结果
	*/
	@ApiOperation("更新体检机构数据")
	@PostMapping("updateTCheckOrg")
	public Result<Object> updateTCheckOrg(@RequestBody TCheckOrg tCheckOrg){
		//如果实体id不为空
		if (!StringUtils.isBlank(tCheckOrg.getId())) {
			try {
				//校验体检机构是否存在
				Boolean exist = tCheckOrgService.checkOrgName(tCheckOrg);
				if(exist){
					return ResultUtil.error("体检机构名称重复！");
				}
				Boolean avatar = tCheckOrgService.handleCheckOrgImg(tCheckOrg, tCheckOrg.getAvatar());
				if(!avatar){
					return ResultUtil.error("体检机构头像未上传!");
				}
				Boolean images = tCheckOrgService.handleCheckOrgImg(tCheckOrg, tCheckOrg.getImages());
				if(!images){
					return ResultUtil.error("体检机构背景图未上传!");
				}
				tCheckOrg.setUpdateBy(securityUtil.getCurrUser().getId());
				tCheckOrg.setUpdateTime(new Date());
				Boolean res = tCheckOrgService.updateById(tCheckOrg);
				if (res) {
					return ResultUtil.data(res, MessageConstant.UPDATE_DATA_SUCCESS);
				} else {
					return ResultUtil.error(MessageConstant.UPDATE_DATA_FAIL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ResultUtil.error(MessageConstant.SAVE_EXCEPTION + e.getMessage());
			}
		}
		return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);

	}

	/**
	* 功能描述：根据主键来删除数据
	* @param ids 主键集合
	* @return 返回删除结果
	*/
	@ApiOperation("根据主键来删除体检机构数据")
	@PostMapping("deleteTCheckOrg")
	public Result<Object> deleteTCheckOrg(@RequestParam String[] ids){
		if (ids == null || ids.length == 0) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			boolean res = tCheckOrgService.removeByIds(Arrays.asList(ids));
			if (res) {
				return ResultUtil.data(res, MessageConstant.DELETE_DATA_SUCCESS);
			} else {
				return ResultUtil.error(MessageConstant.DELETE_DATA_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.DELETE_DATA_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：根据主键来获取数据
	* @param id 主键
	* @return 返回获取结果
	*/
	@ApiOperation("根据主键来获取体检机构数据")
	@GetMapping("getTCheckOrg")
	public Result<Object> getTCheckOrg(@RequestParam(name = "id")String id){
		if (StringUtils.isBlank(id)) {
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			TCheckOrg res = tCheckOrgService.getById(id);
			if (res != null) {
				return ResultUtil.data(res, MessageConstant.QUERY_DATA_SUCCESS);
			} else {
				return ResultUtil.data(res,MessageConstant.QUERY_DATA_FAIL);
			}
		} catch (Exception e) {
			 e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}

	/**
	* 功能描述：实现分页查询
	* @param searchVo 需要模糊查询的信息
	* @param pageVo 分页参数
	* @return 返回获取结果
	*/
	@ApiOperation("分页查询体检机构数据")
	@GetMapping("queryTCheckOrgList")
	public Result<Object> queryTCheckOrgList(TCheckOrg  tCheckOrg, SearchVo searchVo, PageVo pageVo){
		try {
			 IPage<TCheckOrg> result =tCheckOrgService.queryTCheckOrgListByPage(tCheckOrg, searchVo, pageVo);
			 return ResultUtil.data(result);
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResultUtil.error(MessageConstant.QUERY_EXCEPTION + e.getMessage());
		}
	}

	/**
	 * 动态模糊查询所有机构
	 * @param tCheckOrg
	 * @param searchVo
	 * @return
	 */
	@ApiOperation("查询所有的体检机构数据")
	@GetMapping("getAllCheckOrg")
	public Result<Object> getCheckOrg(TCheckOrg tCheckOrg,SearchVo searchVo){
		try {
			List<TCheckOrg> checkOrg = tCheckOrgService.getAllCheckOrg(tCheckOrg,searchVo);
			if(checkOrg != null && checkOrg.size() > 0){
				return ResultUtil.data(checkOrg, MessageConstant.QUERY_DATA_SUCCESS);
			}
			return ResultUtil.data(checkOrg,MessageConstant.QUERY_DATA_FAIL);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
		}
	}

	/**
	 * 检验添加体检机构名称的唯一性
	 * @param tCheckOrg
	 * @return
	 */
	@ApiOperation("根据name查找对应的体检机构")
	@GetMapping("findCheckOrgByName")
	public Result<Object> findCheckOrgByOrgName(TCheckOrg tCheckOrg){
		try {
			List<TCheckOrg> checkOrg = tCheckOrgService.getAllCheckOrg(tCheckOrg,null);
			if(checkOrg==null || checkOrg.size() == 0){
				return ResultUtil.data(checkOrg,MessageConstant.QUERY_DATA_NOT_EXIST);
			}
			return ResultUtil.data(checkOrg.get(0),MessageConstant.QUERY_DATA_ALREADY_EXIST);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
		}
	}

	@ApiOperation("分页查询体检机构及对应套餐信息")
	@GetMapping("findOrgAndCombo")
	public Result<Object> getOrgAndCombo(TCheckOrg tCheckOrg,SearchVo searchVo, PageVo pageVo){
		try {
			IPage<TCheckOrg> list = tCheckOrgService.getOrgAndComboInfoByPage(tCheckOrg,searchVo,pageVo);
			//判断是否为小程序端，是的话去除图片地址的/tempFileUrl前缀
			if(tCheckOrg.getIsMiniApps() != null && tCheckOrg.getIsMiniApps()){
				List<TCheckOrg> records = list.getRecords();
				for (TCheckOrg checkOrg:records) {
					String avatar = checkOrg.getAvatar().replaceAll("/tempFileUrl", "");
					String images = checkOrg.getImages().replaceAll("/tempFileUrl", "");
					checkOrg.setAvatar(avatar);
					checkOrg.setImages(images);
					List<TCombo> combos = checkOrg.getTCombos();
					for (TCombo combo:combos) {
						String url = combo.getUrl().replaceAll("/tempFileUrl", "");
						combo.setUrl(url);
					}
				}
			}
			return ResultUtil.data(list);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
		}
	}

	@ApiOperation("根据id查询体检机构及对应套餐信息")
	@GetMapping("findOrgAndComboById")
	public Result<Object> getOrgAndCombo(TCheckOrg tCheckOrg){
		if(tCheckOrg.getId() == null){
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			TCheckOrg list = tCheckOrgService.getOrgAndComboInfo(tCheckOrg);
			return ResultUtil.data(list);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
		}
	}
	/**
	* 功能描述：导出数据
	* @param response 请求参数
	* @param tCheckOrg 查询参数
	* @return
	*/
	@ApiOperation("导出体检机构数据")
	@PostMapping("/download")
	public void download(HttpServletResponse response,TCheckOrg  tCheckOrg){
		try {
			tCheckOrgService.download( tCheckOrg,response);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	@ApiOperation("根据机构id查询体检机构及对应套餐信息")
	@GetMapping("getOrgAndComboData")
	public Result<Object> getOrgAndComboData(TCheckOrg tCheckOrg){
		if(tCheckOrg.getId() == null){
			return ResultUtil.error(MessageConstant.PARAMETER_IS_NULL);
		}
		try {
			List<TCombo> orgAndComboData = tComboService.getOrgAndComboData(tCheckOrg);
			//判断是否为小程序端，是的话去除图片地址的/tempFileUrl前缀
			if(tCheckOrg.getIsMiniApps() != null && tCheckOrg.getIsMiniApps()){
				for (TCombo tCombo:orgAndComboData) {
					String url = tCombo.getUrl().replaceAll("/tempFileUrl", "");
					tCombo.setUrl(url);
				}
			}
			return ResultUtil.data(orgAndComboData);
		}catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(MessageConstant.QUERY_EXCEPTION+e.getMessage());
		}
	}


}
