package com.scmt.healthy.controller;

import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.utils.DocUtil;
import com.scmt.healthy.utils.ExecuteSqlUtil;
import com.scmt.healthy.utils.ImportInsertUtil;
import com.scmt.healthy.utils.UploadFileUtils;
import io.swagger.annotations.ApiOperation;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mike
 * @since 2021-10-14
 */
@RestController
@RequestMapping("/scmt/dataBase")
public class DataBaseController {
	@Autowired
	ExecuteSqlUtil executeSqlUtil;

	/**
	 * 功能描述：导入数据
	 * <p>
	 * 根据不同的体检类型，导入模板不一样
	 *
	 * @return
	 */
	@ApiOperation("导入数据")
	@PostMapping("/import")
	@Transactional(rollbackOn = Exception.class)
	public Result<Object> importExcel(@RequestParam(value = "file") MultipartFile multipartFile) throws Exception {
		try {
			String classPath = DocUtil.getClassPath().split(":")[0];
			File file1 = new File(classPath+":/HealthyDatabase.zip");
			//存在则删除
			if(file1.isFile() && file1.exists()){
				file1.delete();
				file1 = new File(classPath+":/HealthyDatabase.zip");
			}
			FileUtils.writeByteArrayToFile(file1,multipartFile.getBytes());
			String path=classPath+":" + UploadFileUtils.basePath +"dcmd";
			//String path="E:/";
			ZipFile zipFile = new ZipFile(file1);
			//给的vr是gbk，如果这里，你上传的zip包为utf-8，那么这里改为utf-8
			zipFile.setFileNameCharset("gbk");
			//解压到指定目录
			zipFile.extractAll(path);
			//运行sql 文件
			String sqlPath  = path +"/sql/Healthy.sql";
			executeSqlUtil.executeSql(sqlPath);
			return ResultUtil.success("导入成功");
		}
		catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(e.getMessage());
		}
	}

	/**
	 * 功能描述：上传图片
	 * <p>
	 * 根据不同的体检类型，导入模板不一样
	 *
	 * @return
	 */
	@ApiOperation("上传图片")
	@PostMapping("/imageUpload")
	@Transactional(rollbackOn = Exception.class)
	public Result<Object> imageUpload(@RequestParam(value = "file") MultipartFile multipartFile) throws Exception {
		try {
			String classPath = DocUtil.getClassPath().split(":")[0];
			//时间戳
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String DataStr = format.format(new Date());
			String name = multipartFile.getOriginalFilename();
			File file1 = new File(classPath+":" + UploadFileUtils.basePath +"dcm/image/" + DataStr + "/" + name);
			//存在则删除
			if(file1.isFile() && file1.exists()){
				file1.delete();
				file1 = new File(classPath+":" + UploadFileUtils.basePath +"dcm/image/" + DataStr + "/" + name);
			}
			FileUtils.writeByteArrayToFile(file1,multipartFile.getBytes());
			String url = "/tempFileUrl/tempfile/dcm/image/" + DataStr + "/" + name;
			return ResultUtil.data(url);
		}
		catch (Exception e){
			e.printStackTrace();
			return ResultUtil.error(e.getMessage());
		}
	}
	@ApiOperation("导出订单以及相关信息")
	@RequestMapping(value = "downloadSql")
	public void downloadSql(String orderId) throws Exception {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			assert requestAttributes != null;
			HttpServletResponse response = requestAttributes.getResponse();
			String filePath = ImportInsertUtil.importSql(orderId);

			File file = new File(filePath);
			// 取得文件名。
			String fileName = file.getName();
			InputStream fis = null;
			fis = new FileInputStream(file);
			response.setCharacterEncoding("UTF-8");
			// 配置文件下载
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setHeader("Content-Length", String.valueOf(file.length()));
			byte[] b = new byte[1024];
			int len;
			while ((len = fis.read(b)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			response.flushBuffer();
			fis.close();

		}catch (Exception e ){
			throw new RuntimeException(e);
		}

	}

}
