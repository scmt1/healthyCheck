package com.scmt.healthy.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.poi.word.WordUtil;
import com.alibaba.fastjson.JSON;
import com.aspose.words.Document;
import com.aspose.words.ImportFormatMode;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scmt.core.utis.FileUtil;
import com.scmt.healthy.entity.TOrderGroup;
import com.scmt.healthy.entity.TTemplate;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 模板工具类
 * @author dengjie
 */
public class TemplateUtil {

	private static final Logger logger = LoggerFactory.getLogger(TemplateUtil.class);

	/**
	 * 匹配模板数据并转为pdf
	 * @param personId 体检人员Id
	 * @param Ip 请求Ip
	 * @param list 模板集合
	 * @return pdf 文件路径
	 * @throws Exception 抛出的异常
	 */
	public static  String templateHandData(String personId,String Ip, ArrayList wordData, List<TTemplate> list) throws  Exception{
		String res  = "";
		List<Map<String, Object>> arr = new ArrayList<>();
		final int length = wordData.size();
		//合并后的Word 地址
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String DataStr = format.format(new Date());
		String templateWordPath = "";
		//
		String classPath = DocUtil.getClassPath();
		String pdfPath = classPath.split(":")[0]+":"  + DocUtil.basePath +"/pdf/word/"+personId+"/"+Ip+"/"+DataStr+".pdf";
		File fileOutput = new File(pdfPath);
		//解析过程中间文件删除
		FileUtil.del(fileOutput.getParentFile());

		for (int i = 0; i <wordData.size() ; i++) {
			//关键是这一句代码，将 i 转化为  j，这样j 还是final类型的参与线程
			final int j=i;
					LinkedHashMap o = (LinkedHashMap) wordData.get(j);
					if(o!=null && o.containsKey("id") && o.containsKey("baseProjectId") && o.containsKey("templateData")){
						String id = o.get("id")!=null?o.get("id").toString():"";
						String baseProjectId = o.get("baseProjectId")!=null?o.get("baseProjectId").toString():"";
						String inPath = "" ;
						List<TTemplate> filter =new  ArrayList();
						if(StringUtils.isNotBlank(id)){
							filter= list.stream()
									.filter(user -> user!=null && id.equals(user.getId()) )
									.collect(Collectors.toList());
						}
						else if(StringUtils.isNotBlank(baseProjectId)){
							filter= list.stream()
									.filter(user -> user!=null &&  baseProjectId.equals(user.getBaseProjectId() ) )
									.collect(Collectors.toList());
						}
						if(filter!=null && filter.size()>0){
							inPath = filter.get(0).getContent();
							String templateData = o.get("templateData")!=null?o.get("templateData").toString():"";
							Map<String, Object> variableMap = new HashMap<>();
							//设置日期格式
							SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
							variableMap.put("date", df.format(new Date()));
							if (StringUtils.isNotBlank(templateData)) {
								Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
								variableMap.putAll(jsonToMap);
							}
							String WordPath = "";
							if(inPath.indexOf(".docx")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCXWithData(inPath, variableMap, personId, Ip);
							}
							else if(inPath.indexOf(".doc")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCWithData(inPath, variableMap, personId, Ip);
							}
							if(StringUtils.isNotBlank(WordPath)){
								Map<String,Object> map  = new HashMap<>();
								map.put("key",j);
								map.put("data",WordPath);
								arr.add(map);
							}
						}

					}

		}


			if(arr.size()>0 && arr.size() == length){
				//排序
				Collections.sort(arr , new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Integer o1Value = Integer.valueOf(o1.get("key").toString());
						Integer o2Value = Integer.valueOf(o2.get("key").toString());
						return o1Value.compareTo(o2Value);
					}
				});
				//取值
				List<String> arrList = new ArrayList<>();
				for (Map<String, Object> x : arr) {
					if(x.containsKey("data") && x.get("data")!=null){
						arrList.add(x.get("data").toString());
					}
				}
				templateWordPath = arrList.get(0).substring(0, arrList.get(0).lastIndexOf("/word"))+"/word/"+personId+"/"+Ip+"/"+personId+"-"+DataStr+".Pdf" ;
				//合并Word 并转为Pdf
				DocUtil.mergeDoc(arrList,templateWordPath);
				String PdfPath = templateWordPath.replaceAll(".docx",".pdf");
				res = PdfPath.substring(PdfPath.indexOf("/tempfile"),PdfPath.length());
				//break;
			}
			return  res;
	}

	/**
	 * 批量匹配模板数据并转为pdf
	 * @param dataList  批量模板数据
	 * @param Ip 请求Ip
	 * @param list 模板集合
	 * @return pdf 文件路径
	 * @throws Exception 抛出的异常
	 */
	public static  String templateBatchHandData(List <Map<String,Object>> dataList,String Ip,String userId, List<TTemplate> list) throws  Exception{
		String res  = "";
		List<String> urls  = new ArrayList<>();
		List<String> resListPath = new ArrayList<>();
		//获取程序
		String classPath = DocUtil.getClassPath();
		//时间戳
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String DataStr = format.format(new Date());
		//过程中间文件地址
		String pdfPath = classPath.split(":")[0]+":"  + DocUtil.basePath +"/pdf/word/"+userId+"/"+Ip+"/"+DataStr+".pdf";
		File fileOutput = new File(pdfPath);
		//解析过程中间文件删除
		FileUtil.del(fileOutput.getParentFile());
		for (Map<String,Object> map:dataList) {
			if(map.containsKey("personId")||map.containsKey("wordData")){
				String personId = map.get("personId").toString();
				String personName = "";
				if(map.containsKey("personName")){
					personName = map.get("personName").toString();
				}
				ArrayList wordData =  (ArrayList)   map.get("wordData");

				List<Map<String, Object>> arrMap = new ArrayList<>();

				//通过线程池创建线程
				for (int i = 0; i <wordData.size() ; i++) {
					final  int j = i;
					LinkedHashMap o = (LinkedHashMap) wordData.get(j);
					if(o!=null && o.containsKey("id") && o.containsKey("baseProjectId") && o.containsKey("templateData")){
						String id = o.get("id")!=null?o.get("id").toString():"";
						String baseProjectId = o.get("baseProjectId")!=null?o.get("baseProjectId").toString():"";
						String inPath = "" ;
						List<TTemplate> filter =new  ArrayList();
						if(StringUtils.isNotBlank(id)){
							filter= list.stream()
									.filter(user -> user!=null && id.equals(user.getId()) )
									.collect(Collectors.toList());
						}
						else if(StringUtils.isNotBlank(baseProjectId)){
							filter= list.stream()
									.filter(user -> user!=null &&  baseProjectId.equals(user.getBaseProjectId() ) )
									.collect(Collectors.toList());
						}
						if(filter!=null && filter.size()>0){
							inPath = filter.get(0).getContent();
							String templateData = o.get("templateData")!=null?o.get("templateData").toString():"";
							Map<String, Object> variableMap = new HashMap<>();
							//设置日期格式
							SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
							variableMap.put("date", df.format(new Date()));
							if (StringUtils.isNotBlank(templateData)) {
								Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
								variableMap.putAll(jsonToMap);
							}
							String WordPath = "";
							if(inPath.indexOf(".docx")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCXWithData(inPath, variableMap, userId, Ip);
							}
							else if(inPath.indexOf(".doc")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCWithData(inPath, variableMap, userId, Ip);
							}
							if(StringUtils.isNotBlank(WordPath)){
								Map<String,Object> mapItem  = new HashMap<>();
								mapItem.put("key",j);
								mapItem.put("data",WordPath);
								arrMap.add(mapItem);
							}
						}
					}
				}
				if(arrMap.size()>0 && arrMap.size() == wordData.size()){
					//排序
					Collections.sort(arrMap , (o1, o2) -> {
						Integer o1Value = Integer.valueOf(o1.get("key").toString());
						Integer o2Value = Integer.valueOf(o2.get("key").toString());
						return o1Value.compareTo(o2Value);
					});
					com.aspose.words.Document doc3 = new com.aspose.words.Document();
					doc3.removeAllChildren();
					//取值
					List<String> arrList = new ArrayList<>();
					for (Map<String, Object> x : arrMap) {
						if(x.containsKey("data") && x.get("data")!=null){
							String pathWordNow = x.get("data").toString();//当前word路径
							arrList.add(pathWordNow);
						}
					}

					for (int i = 0; i < arrList.size(); i++) {
						String path = arrList.get(i);
						Document doc = new Document(path);
						doc3.appendDocument(doc, ImportFormatMode.USE_DESTINATION_STYLES);
					}

					//统计页数
					int pages = doc3.getPageCount();
					//个检报告 页数为奇数时 加入空白页
					if(pages%2 != 0 && !(Ip.indexOf("封面模板") > -1)){//奇数页
						String newPathHead = arrList.get(0).split("pdf/word/")[0];//头部路径
						newPathHead += "2022-01-21/空白页.docx";
						arrList.add(newPathHead);
					}

					resListPath.addAll(arrList);
				}
			}
		}
		//合并后的Word 地址
		String templateWordPath = resListPath.get(0).substring(0, resListPath.get(0).lastIndexOf("/word"))+"/word/"+userId+"/"+Ip+"/"+userId+"-"+DataStr+".Pdf" ;
		//合并Word 并转为Pdf
		DocUtil.mergeDoc(resListPath,templateWordPath);
		String PdfPath = templateWordPath.replaceAll(".docx",".pdf");
//		resListPath.add(PdfPath);
//		String PdfPath = resListPath.get(0).substring(0, resListPath.get(0).lastIndexOf("/word"))+"/word/"+userId+"/"+Ip+"/batch/"+userId+"-"+DataStr+".Pdf" ;
//		//合并Word 并转为Pdf
//		PdfUtil.MergePdf( resListPath.toArray(new String[resListPath.size()]),PdfPath);
		res =  PdfPath.substring(PdfPath.indexOf("/tempfile"),PdfPath.length());
		return  res;
	}

	/**
	 * 将文件缩成zip(导出pdf)
	 * @param srcFiles
	 * @param out
	 */
	public static void toPDFZip(List<Map<String, Object>> srcFiles, OutputStream out) {
		try (ZipOutputStream zos = new ZipOutputStream(out);) {
			// 最终压缩文件
			for (Map<String, Object> mapFile : srcFiles) {
				String fileName = mapFile.get("fileName").toString();
				byte[] buffer = (byte[]) mapFile.get("pdf");
				ZipEntry entry = new ZipEntry(fileName);
				zos.putNextEntry(entry);
				zos.write(buffer);
			}
			zos.flush();
			zos.close();
		} catch (Exception e) {
			throw new RuntimeException("zip error", e);
		}
	}

	/**
	 * 批量匹配模板数据并转为pdf
	 * @param dataList  批量模板数据
	 * @param Ip 请求Ip
	 * @param list 模板集合
	 * @return pdf 文件路径
	 * @throws Exception 抛出的异常
	 */
	public static  List<String> templateBatchHandDataPdf(List <Map<String,Object>> dataList,String Ip,String userId, List<TTemplate> list) throws  Exception{
		String res  = "";
		List<String> urls  = new ArrayList<>();
		List<String> resListPath = new ArrayList<>();
		//获取程序
		String classPath = DocUtil.getClassPath();
		//时间戳
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String DataStr = format.format(new Date());
		//过程中间文件地址
		String pdfPath = classPath.split(":")[0]+":"  + DocUtil.basePath +"/pdf/word/"+userId+"/"+Ip+"/"+DataStr+".pdf";
		File fileOutput = new File(pdfPath);
		//解析过程中间文件删除
		FileUtil.del(fileOutput.getParentFile());
		for (Map<String,Object> map:dataList) {
			if(map.containsKey("personId")||map.containsKey("wordData")){
				String personId = map.get("personId").toString();
				String personName = "";
				if(map.containsKey("personName")){
					personName = map.get("personName").toString();
				}
				ArrayList wordData =  (ArrayList)   map.get("wordData");

				List<Map<String, Object>> arrMap = new ArrayList<>();

				//通过线程池创建线程
				for (int i = 0; i <wordData.size() ; i++) {
					final  int j = i;
					LinkedHashMap o = (LinkedHashMap) wordData.get(j);
					if(o!=null && o.containsKey("id") && o.containsKey("baseProjectId") && o.containsKey("templateData")){
						String id = o.get("id")!=null?o.get("id").toString():"";
						String baseProjectId = o.get("baseProjectId")!=null?o.get("baseProjectId").toString():"";
						String inPath = "" ;
						List<TTemplate> filter =new  ArrayList();
						if(StringUtils.isNotBlank(id)){
							filter= list.stream()
									.filter(user -> user!=null && id.equals(user.getId()) )
									.collect(Collectors.toList());
						}
						else if(StringUtils.isNotBlank(baseProjectId)){
							filter= list.stream()
									.filter(user -> user!=null &&  baseProjectId.equals(user.getBaseProjectId() ) )
									.collect(Collectors.toList());
						}
						if(filter!=null && filter.size()>0){
							inPath = filter.get(0).getContent();
							String templateData = o.get("templateData")!=null?o.get("templateData").toString():"";
							Map<String, Object> variableMap = new HashMap<>();
							//设置日期格式
							SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
							variableMap.put("date", df.format(new Date()));
							if (StringUtils.isNotBlank(templateData)) {
								Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
								variableMap.putAll(jsonToMap);
							}
							String WordPath = "";
							if(inPath.indexOf(".docx")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCXWithData(inPath, variableMap, userId, Ip);
							}
							else if(inPath.indexOf(".doc")>-1){
								//替换模板数据
								WordPath = DocUtil.WordDOCWithData(inPath, variableMap, userId, Ip);
							}
							if(StringUtils.isNotBlank(WordPath)){
								Map<String,Object> mapItem  = new HashMap<>();
								mapItem.put("key",j);
								mapItem.put("data",WordPath);
								arrMap.add(mapItem);
							}
						}
					}
				}
				if(arrMap.size()>0 && arrMap.size() == wordData.size()){
					//排序
					Collections.sort(arrMap , (o1, o2) -> {
						Integer o1Value = Integer.valueOf(o1.get("key").toString());
						Integer o2Value = Integer.valueOf(o2.get("key").toString());
						return o1Value.compareTo(o2Value);
					});
					com.aspose.words.Document doc3 = new com.aspose.words.Document();
					doc3.removeAllChildren();
					//取值
					List<String> arrList = new ArrayList<>();
					for (Map<String, Object> x : arrMap) {
						if(x.containsKey("data") && x.get("data")!=null){
							String pathWordNow = x.get("data").toString();//当前word路径
							arrList.add(pathWordNow);
						}
					}

					for (int i = 0; i < arrList.size(); i++) {
						String path = arrList.get(i);
						Document doc = new Document(path);
						doc3.appendDocument(doc, ImportFormatMode.USE_DESTINATION_STYLES);
					}

					//统计页数
					int pages = doc3.getPageCount();
					//个检报告 页数为奇数时 加入空白页
					if(pages%2 != 0 && !(Ip.indexOf("封面") > -1)){//奇数页
						String newPathHead = arrList.get(0).split("pdf/word/")[0];//头部路径
						newPathHead += "2022-01-21/空白页.docx";
						arrList.add(newPathHead);
					}

					//生成体检报告文件(单个生成pdf和word)
					String templatePdfPath = arrList.get(0).substring(0, arrList.get(0).lastIndexOf("/word"))+"/word/"+userId+"/"+Ip+"/"+personName+"-"+personId+".Pdf" ;
					String templateWordPath = arrList.get(0).substring(0, arrList.get(0).lastIndexOf("/word"))+"/word/"+userId+"/"+Ip+"/"+personName+"-"+personId+".docx" ;
					DocUtil.mergeDoc(arrList,templatePdfPath);//检查项目合并生成文件(单个)
					resListPath.add(templateWordPath);//存入批量合成的路径集合
					templatePdfPath = templatePdfPath.substring(templatePdfPath.indexOf("/tempfile"),templatePdfPath.length());

					urls.add(templatePdfPath);//要返回的路径集合
				}
			}
		}
		return  urls;
	}

	/**
	 * 匹配模板数据并转为pdf(健康证)
	 * @param wordName
	 * @param Ip
	 * @param wordData
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static  String templateHandDataHealthy(String wordName,String Ip, ArrayList wordData, List<TTemplate> list) throws  Exception{
		String res  = "";
		List<Map<String, Object>> arr = new ArrayList<>();
		final int length = wordData.size();
		//合并后的Word 地址
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String DataStr = format.format(new Date());
		String templateWordPath = "";
		//
		String classPath = DocUtil.getClassPath();
		String pdfPath = classPath.split(":")[0]+":"  + DocUtil.basePath +"/pdf/word/"+wordName+"/"+Ip+"/"+DataStr+".pdf";
		File fileOutput = new File(pdfPath);
		//解析过程中间文件删除
		FileUtil.del(fileOutput.getParentFile());

		//匹配模板
		for (int i = 0; i <wordData.size() ; i++) {
			//关键是这一句代码，将 i 转化为  j，这样j 还是final类型的参与线程
			final int j=i;
			LinkedHashMap o = (LinkedHashMap) wordData.get(j);
			if(o!=null && o.containsKey("id") && o.containsKey("baseProjectId") && o.containsKey("templateData")){
				String id = o.get("id")!=null?o.get("id").toString():"";
				String baseProjectId = o.get("baseProjectId")!=null?o.get("baseProjectId").toString():"";
				String inPath = "" ;
				List<TTemplate> filter =new  ArrayList();
				if(StringUtils.isNotBlank(id)){
					filter= list.stream()
							.filter(user -> user!=null && id.equals(user.getId()) )
							.collect(Collectors.toList());
				}
				else if(StringUtils.isNotBlank(baseProjectId)){
					filter= list.stream()
							.filter(user -> user!=null &&  baseProjectId.equals(user.getBaseProjectId() ) )
							.collect(Collectors.toList());
				}
				if(filter!=null && filter.size()>0){
					inPath = filter.get(0).getContent();
					String templateData = o.get("baseProjectId")!=null?o.get("templateData").toString():"";
					Map<String, Object> variableMap = new HashMap<>();
					//设置日期格式
					SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
					variableMap.put("date", df.format(new Date()));
					if (StringUtils.isNotBlank(templateData)) {
						Map<String, Object> jsonToMap = JSONUtil.parseJSON2Map(templateData);
						variableMap.putAll(jsonToMap);
					}
					String WordPath = "";
					if(inPath.indexOf(".docx")>-1){
						//替换模板数据
						WordPath = DocUtil.WordDOCXWithData(inPath, variableMap, wordName, Ip);
					}
					else if(inPath.indexOf(".doc")>-1){
						//替换模板数据
						WordPath = DocUtil.WordDOCWithData(inPath, variableMap, wordName, Ip);
					}
					if(StringUtils.isNotBlank(WordPath)){
						Map<String,Object> map  = new HashMap<>();
						map.put("key",j);
						map.put("data",WordPath);
						arr.add(map);
					}
				}

			}
		}

		if(arr.size()>0 && arr.size() == length){
			//排序
			Collections.sort(arr , new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Integer o1Value = Integer.valueOf(o1.get("key").toString());
					Integer o2Value = Integer.valueOf(o2.get("key").toString());
					return o1Value.compareTo(o2Value);
				}
			});
			//取值
			List<String> arrList = new ArrayList<>();
			for (Map<String, Object> x : arr) {
				if(x.containsKey("data") && x.get("data")!=null){
					arrList.add(x.get("data").toString());
				}
			}
			templateWordPath = arrList.get(0).substring(0, arrList.get(0).lastIndexOf("/word"))+"/word/"+wordName+"/"+Ip+"/"+wordName+"-"+DataStr+".Pdf" ;
			//合并Word 并转为Pdf
			DocUtil.mergeDoc(arrList,templateWordPath);
			String PdfPath = templateWordPath.replaceAll(".docx",".pdf");
			res = PdfPath.substring(PdfPath.indexOf("/tempfile"),PdfPath.length());
			//break;
		}

		return  res;
	}


}
