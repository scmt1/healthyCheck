package com.scmt.healthy.utils;

import cn.hutool.core.io.file.FileMode;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.deepoove.poi.XWPFTemplate;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.*;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.scmt.core.common.utils.IpInfoUtil;
import com.scmt.core.utis.FileUtil;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author dengjie
 */
public class DocUtil {
    public static final String 封面模板 = "封面模板";
    private static Logger log = LoggerFactory.getLogger(DocUtil.class);

    public static String basePath = UploadFileUtils.basePath + "wordTemplate/";


    /**
     * 将文件存储在 nginx代理路劲下
     *
     * @param multipartFile 文件
     * @return
     */
    public static String uploadFile(MultipartFile multipartFile) {
        String path = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DataStr = format.format(new Date());
        path = basePath + "" + DataStr + "/";
        File upload = FileUtil.upload(multipartFile, path);
        String name = upload.getName();
        return "/tempfile/wordTemplate/" + DataStr + "/" + name;
    }

    /**
     * 删除nginx代理路劲下的文件
     *
     * @param tmpFile
     */
    public static void deleteFile(String tmpFile) throws Exception {
        String classPath = getClassPath();
        String file = classPath.split(":")[0] + ":" + basePath + tmpFile;
        String fileName = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        File delete = new File(fileName);
        if (delete.exists()) {
            delete.delete();
        }
    }

    /**
     * 删除一个目录下面的所有文件
     *
     * @param file
     */
    public static void deleteAll(File file) {
        if (file == null || file.list() == null) {
            return;
        }
        if (file.isFile() || Objects.requireNonNull(file.list()).length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteAll(files[i]);
                files[i].delete();
            }
            //如果文件本身就是目录 ，就要删除目录
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 模板文件替换
     *
     * @param tmpFile    文件模板路径
     * @param contentMap 替换的内容
     * @param response
     */
    public static void getBuild(String tmpFile, Map<String, Object> contentMap, HttpServletResponse response) throws IOException, Exception {

        String classPath = getClassPath();
        String file = classPath.split(":")[0] + ":" + basePath + tmpFile;
        String fileName = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        File dest = (new File(fileName)).getCanonicalFile();

        if (!dest.getParentFile().exists()) {
            throw new Exception("文件路径不能为空");
        }
        String fileNameNew = "";
        if (fileName.indexOf(".docx") < 0 && fileName.indexOf(".doc") > 0) {
            fileNameNew = fileName.replaceAll(".doc", ".docx");
            //先转为docx
            PdfUtil.docToDOcx(fileName, fileNameNew);
        } else {
            fileNameNew = fileName;
        }

        //解析word模板
        XWPFTemplate template = XWPFTemplate.compile(fileNameNew).render(contentMap);

        //导出到文件
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode("模板" + ".doc", StandardCharsets.UTF_8.name()));
        // 定义输出类型
        response.setContentType("application/msword");
        template.write(response.getOutputStream());
        template.close();
        if (StringUtils.isNotBlank(fileNameNew)) {
            PdfUtil.deleteDocx(fileNameNew);
        }
    }

    /**
     * 得到类的路径
     *
     * @return
     * @throws java.lang.Exception
     */
    public static String getClassPath() throws Exception {
        try {
            String strClassName = DocUtil.class.getName();
            String strPackageName = "";
            if (DocUtil.class.getPackage() != null) {
                strPackageName = DocUtil.class.getPackage().getName();
            }
            String strClassFileName = "";
            if (!"".equals(strPackageName)) {
                strClassFileName = strClassName.substring(strPackageName.length() + 1,
                        strClassName.length());
            } else {
                strClassFileName = strClassName;
            }
            URL url = null;
            url = DocUtil.class.getResource(strClassFileName + ".class");
            String strURL = url.toString();
            strURL = strURL.substring(strURL.indexOf('/') + 1, strURL.lastIndexOf('/'));
            //返回当前类的路径，并且处理路径中的空格，因为在路径中出现的空格如果不处理的话，
            //在访问时就会从空格处断开，那么也就取不到完整的信息了，这个问题在web开发中尤其要注意
            return strURL.replaceAll("%20", " ");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Word 转 Pdf
     *
     * @param inPath
     * @throws IOException
     */
    public static String WordToPDF(String inPath) throws IOException {
        String outPath = inPath.replaceAll(".docx", ".pdf").replaceAll(".doc", ".pdf");
        try {
            String classPath = getClassPath();
            String file = classPath.split(":")[0] + ":" + basePath + inPath;
            String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
            String outPathNew = inPathNew.replaceAll(".docx", ".pdf").replaceAll(".doc", ".pdf");

            // 读取docx文件

            File fileOutput = new File(outPathNew);
            File inputWord = new File(inPathNew);
            if (!fileOutput.exists()) {
                fileOutput.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(fileOutput);
            com.aspose.words.Document doc = new com.aspose.words.Document(inPathNew);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(outputStream, SaveFormat.DOCX);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        } finally {
            return outPath;
        }
    }

    /**
     * Word2007 转 Pdf（并替换模板数据）
     *
     * @param inPath
     * @throws IOException
     */
    public static String WordDOCXToPDFWithData(String inPath, Map map, String userId, String ip) throws Exception {
        if (StringUtils.isBlank(inPath)) {
            throw new RuntimeException("原始文件名错误");
        }
        String classPath = getClassPath();
        String fName = inPath.replaceAll(".docx", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String DataStr = format.format(new Date());
        //获取文件名（不带后缀）
        String name = fName.substring(fName.lastIndexOf("\\") + 1).substring(fName.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("获取文件名错误");
        }
        //生成的pdf文件路径
        String path = classPath.split(":")[0] + ":" + basePath + "pdf/" + userId + "/" + name + "/" + ip;
        String outPath = "/tempfile/wordTemplate/pdf/" + userId + "/" + name + "/" + ip + "/" + name + "-" + DataStr + ".pdf";
        String file = classPath.split(":")[0] + ":" + basePath + inPath;
        String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        String outPathNew = path + "/" + name + "-" + DataStr + ".pdf";
        String inPathNewTemplate =path + "/" + name + "-" + DataStr + ".docx";
        //先删除之前的pdf;
        File directory = new File(path);
        deleteAll(directory);

        //再解析word 然后转pdf
        WordToPDF(inPathNew, inPathNewTemplate, outPathNew, map);
        return outPath;

    }

    /**
     * Word2007 转 Pdf（并替换模板数据） 健康证
     *
     * @param inPath
     * @throws IOException
     */
    public static String WordDOCXToPDFWithHealthyData(String inPath, Map map, String userId, String ip) throws Exception {
        if (StringUtils.isBlank(inPath)) {
            throw new RuntimeException("原始文件名错误");
        }
        String classPath = getClassPath();
        String fName = inPath.replaceAll(".docx", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String DataStr = format.format(new Date());
        //获取文件名（不带后缀）
        String name = fName.substring(fName.lastIndexOf("\\") + 1).substring(fName.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("获取文件名错误");
        }
        //生成的pdf文件路径
        String path = classPath.split(":")[0] + ":" + basePath + "pdf/" + userId + "/" + name + "/" + ip;
        String outPath = "/tempfile/wordTemplate/pdf/" + userId + "/" + name + "/" + ip + "/" + name + "-" + DataStr + ".pdf";
        String file = classPath.split(":")[0] + ":" + basePath + inPath;
        String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        String outPathNew = path + "/" + name + "-" + DataStr + ".pdf";
        String inPathNewTemplate = inPathNew.split(".docx")[0].split(".doc")[0] + DataStr + ".docx";
        //先删除之前的pdf;
		/*File directory  = new File(path);
		deleteAll(directory);*/

        //再解析word 然后转pdf
        WordToPDF(inPathNew, inPathNewTemplate, outPathNew, map);
        return outPath;

    }

    /**
     * Word2003 转 Pdf（并替换模板数据）
     *
     * @param inPath 源文件路径
     * @param map    模板数据
     * @param userId 用户Id
     * @return
     * @throws Exception
     */
    public static String WordDOCToPDFWithData(String inPath, Map map, String userId) throws Exception {
        if (StringUtils.isBlank(inPath)) {
            throw new RuntimeException("原始文件名错误");
        }
        String classPath = getClassPath();
        String fName = inPath.replaceAll(".docx", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String DataStr = format.format(new Date());
        //获取文件名（不带后缀）
        String name = fName.substring(fName.lastIndexOf("\\") + 1).substring(fName.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("获取文件名错误");
        }
        //生成的pdf文件路径
        String path = classPath.split(":")[0] + ":" + basePath + "pdf/" + userId + "/" + name;
        String outPath = "/tempfile/wordTemplate/pdf/" + userId + "/" + name + "/" + name + "-" + DataStr + ".pdf";
        String file = classPath.split(":")[0] + ":" + basePath + inPath;
        String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        //pdf地址
        String outPathNew = path + "/" + name + "-" + DataStr + ".pdf";
        //替换数据后的地址
        String inPathNewTemplate =  path + "/" + name + "-" + DataStr + ".docx";
        //替换数据前的文件地址（由doc转为docx）
        String inDocPathNewTemplate = inPathNew.split(".doc")[0] + "2-" + DataStr + ".docx";
        //先转为docx
        PdfUtil.docToDOcx(inPathNew, inDocPathNewTemplate);
        //先解析word 在转pdf
        WordToPDF(inDocPathNewTemplate, inPathNewTemplate, outPathNew, map);
        //解析完删除转为docx中间过程产生的文件
        PdfUtil.deleteDocx(inDocPathNewTemplate);
        return outPath;

    }

    /**
     * word to PDF（并替换模板数据）
     *
     * @param inPath            模板路径
     * @param outPathNew        PDF 文件路径
     * @param inPathNewTemplate 替换数据后的模板路径
     * @param map               模板数据
     * @throws IOException
     */
    public static void WordToPDF(String inPath, String inPathNewTemplate, String outPathNew, Map<String, Object> map) throws Exception {

        //先创建输出文件路径
        File dest = (new File(outPathNew)).getCanonicalFile();
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        //再创建替换数据后的模板路径
        File destIn = (new File(inPathNewTemplate)).getCanonicalFile();
        if (!destIn.getParentFile().exists()) {
            destIn.getParentFile().mkdirs();
        }
        //先解析word 在转pdf
        //获取解析word后的地址
        File fileOutputTemplate = new File(inPathNewTemplate);
        if (!fileOutputTemplate.exists()) {
            fileOutputTemplate.createNewFile();
        }
        //先处理模板数据
        parseMapData(map);

        //构建 表格列(循环)
        LoopRowTableRenderPolicy goods = new LoopRowTableRenderPolicy();
        LoopRowTableRenderPolicy dataLeft = new LoopRowTableRenderPolicy();
        LoopRowTableRenderPolicy dataRight = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder().bind("goods", goods).bind("dataLeft", dataLeft).bind("dataRight", dataRight).build();
        config.customPolicy("dataRight", dataRight);
        if(map.containsKey("tableMon") && map.get("tableMon") != null){
            LoopRowTableRenderPolicy tableMon = new LoopRowTableRenderPolicy();
            config.customPolicy("tableMon", tableMon);
        }
        if(map.containsKey("vitalCapacity1") && map.get("vitalCapacity1") != null){
            LoopRowTableRenderPolicy vitalCapacity1 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity1", vitalCapacity1);
        }
        if(map.containsKey("vitalCapacity2") && map.get("vitalCapacity2") != null){
            LoopRowTableRenderPolicy vitalCapacity2 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity2", vitalCapacity2);
        }
        if(map.containsKey("vitalCapacity3") && map.get("vitalCapacity3") != null){
            LoopRowTableRenderPolicy vitalCapacity3 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity3", vitalCapacity3);
        }
        if(map.containsKey("goodsState") && map.get("goodsState") != null){
            LoopRowTableRenderPolicy goodsState = new LoopRowTableRenderPolicy();
            config.customPolicy("goodsState", goodsState);
        }
        if(map.containsKey("tableMonitoring") && map.get("tableMonitoring") != null){
            LoopRowTableRenderPolicy tableMonitoring = new LoopRowTableRenderPolicy();
            config.customPolicy("tableMonitoring", tableMonitoring);
        }
        if(map.containsKey("resultDatas") && map.get("resultDatas") != null){
            LoopRowTableRenderPolicy resultDatas = new LoopRowTableRenderPolicy();
            config.customPolicy("resultDatas", resultDatas);
        }
        if(map.containsKey("consciousSymptoms") && map.get("consciousSymptoms") != null){
            LoopRowTableRenderPolicy consciousSymptoms = new LoopRowTableRenderPolicy();
            config.customPolicy("consciousSymptoms", consciousSymptoms);
        }
        if(map.containsKey("personData") && map.get("personData") != null){
            LoopRowTableRenderPolicy personData = new LoopRowTableRenderPolicy();
            config.customPolicy("personData", personData);
        }
        if(map.containsKey("recheckData") && map.get("recheckData") != null){
            LoopRowTableRenderPolicy recheckData = new LoopRowTableRenderPolicy();
            config.customPolicy("recheckData", recheckData);
        }
        if(map.containsKey("tabooData") && map.get("tabooData") != null){
            LoopRowTableRenderPolicy tabooData = new LoopRowTableRenderPolicy();
            config.customPolicy("tabooData", tabooData);
        }
        if(map.containsKey("diseaseData") && map.get("diseaseData") != null){
            LoopRowTableRenderPolicy diseaseData = new LoopRowTableRenderPolicy();
            config.customPolicy("diseaseData", diseaseData);
        }
        if(map.containsKey("otherAbnormalData") && map.get("otherAbnormalData") != null){
            LoopRowTableRenderPolicy otherAbnormalData = new LoopRowTableRenderPolicy();
            config.customPolicy("otherAbnormalData", otherAbnormalData);
        }
        if(map.containsKey("groupDatas") && map.get("groupDatas") != null){
            LoopRowTableRenderPolicy groupDatas = new LoopRowTableRenderPolicy();
            config.customPolicy("groupDatas", groupDatas);
        }
        if(map.containsKey("careerHis") && map.get("careerHis") != null){
            LoopRowTableRenderPolicy careerHis = new LoopRowTableRenderPolicy();
            config.customPolicy("careerHis", careerHis);
        }
        if(map.containsKey("pastMedical") && map.get("pastMedical") != null){
            LoopRowTableRenderPolicy pastMedical = new LoopRowTableRenderPolicy();
            config.customPolicy("pastMedical", pastMedical);
        }

        //解析word模板（新的地址）
        XWPFTemplate template = XWPFTemplate.compile(inPath, config).render(map);
        OutputStream outputStreamTemplate = new FileOutputStream(fileOutputTemplate);
        template.write(outputStreamTemplate);
        template.close();
        outputStreamTemplate.close();

        // 读取pdf文件
        File fileOutput = new File(outPathNew);
        if (!fileOutput.exists()) {
            fileOutput.createNewFile();
        }
        //转换pdf文件

//        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
//            return;
//        }

        //PdfUtil.doc2pdf(inPathNewTemplate,outPathNew);
        OutputStream outputStream = new FileOutputStream(fileOutput);
        //InputStream docxInputStream = new FileInputStream(fileOutputTemplate);
        //IConverter converter = LocalConverter.builder().build();
        //converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
        //outputStream.close();
        //docxInputStream.close();
        //sourcerFile是将要被转化的word文档
        com.aspose.words.Document doc = new com.aspose.words.Document(inPathNewTemplate);
        //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
        doc.save(outputStream, com.aspose.words.SaveFormat.PDF);

        outputStream.close();
        // 有下载功能则不删除
        if(outPathNew.indexOf("单位报告") == -1 && outPathNew.indexOf("复查报告") == -1&& outPathNew.indexOf("合同模板") == -1){
            //解析过程中间文件删除
            fileOutputTemplate.delete();
        }

    }
    /**
     * word to PDF
     *
     * @param inPath            word路径
     * @param outPathNew        PDF 文件路径
     * @throws IOException
     */
    public static void WordToPDF(String inPath, String outPathNew) throws Exception {
        // 读取pdf文件
        File fileIn = new File(inPath);
        if (!fileIn.exists()) {
            throw new RuntimeException("word文件不存在");
        }
        //先创建输出文件路径
        File dest = (new File(outPathNew)).getCanonicalFile();
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        // 读取pdf文件
        File fileOutput = new File(outPathNew);
        if (!fileOutput.exists()) {
            fileOutput.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(fileOutput);
        //sourcerFile是将要被转化的word文档
        com.aspose.words.Document doc = new com.aspose.words.Document(inPath);
        //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
        doc.save(outputStream, com.aspose.words.SaveFormat.PDF);
        outputStream.close();
        //File destIn = (new File(inPath)).getCanonicalFile();
        //解析过程中间文件删除
        //FileUtil.del(destIn.getParentFile());
    }
    /**
     * 判断是否有授权文件 如果没有则会认为是试用版，转换的文件会有水印
     *
     * @return
     */
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = DocUtil.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            //aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 替换Word2003模板数据
     *
     * @param inPath
     * @throws IOException
     */
    public static String WordDOCWithData(String inPath, Map map, String userId, String ip) throws Exception {
        if (StringUtils.isBlank(inPath)) {
            throw new RuntimeException("原始文件名错误");
        }
        String classPath = getClassPath();
        String fName = inPath.replaceAll(".docx", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String DataStr = format.format(new Date());
        //获取文件名（不带后缀）
        String name = fName.substring(fName.lastIndexOf("\\") + 1).substring(fName.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("获取文件名错误");
        }
        //生成的pdf文件路径
        String path = classPath.split(":")[0] + ":" + basePath + "pdf/word/" + userId + "/" + ip + "/" + name ;
        String file = classPath.split(":")[0] + ":" + basePath + inPath;
        String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        String outPathNew = path + "/-1-" + DataStr + ".docx";

        //先转为docx
        PdfUtil.docToDOcx(inPathNew, outPathNew);
        //再解析word
        WordDOCXWithData(outPathNew, map, userId, ip);
        return outPathNew;

    }

    /**
     * 替换Word2007模板数据
     * @param inPath 模板路径
     * @param map 数据
     * @param userId 用户Id
     * @param ip 请求 Ip
     * @throws Exception
     */
    public static String WordDOCXWithData(String inPath, Map map, String userId, String ip) throws Exception {
        if (StringUtils.isBlank(inPath)) {
            throw new RuntimeException("原始文件名错误");
        }
        String classPath = getClassPath();
        String fName = inPath.replaceAll(".docx", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String DataStr = format.format(new Date());
        //获取文件名（不带后缀）
        String name = fName.substring(fName.lastIndexOf("\\") + 1).substring(fName.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("获取文件名错误");
        }else if(map.get("id") != null){
            name = name + "-" + map.get("id");
        }
        if(map.get("projectNamePath") != null){
            name = name + "-" + map.get("projectNamePath");
        }
        String file = classPath.split(":")[0] + ":" + basePath + inPath;
        String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
        //生成的word文件路径
        String path = classPath.split(":")[0] + ":" + basePath + "pdf/word/" + userId+ "/" + ip + "/" + name;
        String inPathNewTemplate = path+"/" + DataStr + ".docx";
        //替换word数据
        //先创建输出文件路径
        File destIn = (new File(inPathNewTemplate)).getCanonicalFile();
        if (!destIn.getParentFile().exists()) {
            destIn.getParentFile().mkdirs();
        }
        //再创建解析word后的地址
        File fileOutputTemplate = new File(inPathNewTemplate);
        if (!fileOutputTemplate.exists()) {
            fileOutputTemplate.createNewFile();
        }
        //先处理模板数据
        parseMapData(map);

        //构建 表格列(循环)
        LoopRowTableRenderPolicy goods = new LoopRowTableRenderPolicy();
        LoopRowTableRenderPolicy dataLeft = new LoopRowTableRenderPolicy();
        LoopRowTableRenderPolicy dataRight = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder().bind("goods", goods).bind("dataLeft", dataLeft).bind("dataRight", dataRight).build();
        config.customPolicy("dataRight", dataRight);
        if(map.containsKey("tableMon") && map.get("tableMon") != null){
            LoopRowTableRenderPolicy tableMon = new LoopRowTableRenderPolicy();
            config.customPolicy("tableMon", tableMon);
        }
        if(map.containsKey("vitalCapacity1") && map.get("vitalCapacity1") != null){
            LoopRowTableRenderPolicy vitalCapacity1 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity1", vitalCapacity1);
        }
        if(map.containsKey("vitalCapacity2") && map.get("vitalCapacity2") != null){
            LoopRowTableRenderPolicy vitalCapacity2 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity2", vitalCapacity2);
        }
        if(map.containsKey("vitalCapacity3") && map.get("vitalCapacity3") != null){
            LoopRowTableRenderPolicy vitalCapacity3 = new LoopRowTableRenderPolicy();
            config.customPolicy("vitalCapacity3", vitalCapacity3);
        }
        if(map.containsKey("goodsState") && map.get("goodsState") != null){
            LoopRowTableRenderPolicy goodsState = new LoopRowTableRenderPolicy();
            config.customPolicy("goodsState", goodsState);
        }
        if(map.containsKey("tableMonitoring") && map.get("tableMonitoring") != null){
            LoopRowTableRenderPolicy tableMonitoring = new LoopRowTableRenderPolicy();
            config.customPolicy("tableMonitoring", tableMonitoring);
        }
        if(map.containsKey("resultDatas") && map.get("resultDatas") != null){
            LoopRowTableRenderPolicy resultDatas = new LoopRowTableRenderPolicy();
            config.customPolicy("resultDatas", resultDatas);
        }
        if(map.containsKey("consciousSymptoms") && map.get("consciousSymptoms") != null){
            LoopRowTableRenderPolicy consciousSymptoms = new LoopRowTableRenderPolicy();
            config.customPolicy("consciousSymptoms", consciousSymptoms);
        }
        if(map.containsKey("personData") && map.get("personData") != null){
            LoopRowTableRenderPolicy personData = new LoopRowTableRenderPolicy();
            config.customPolicy("personData", personData);
        }
        if(map.containsKey("recheckData") && map.get("recheckData") != null){
            LoopRowTableRenderPolicy recheckData = new LoopRowTableRenderPolicy();
            config.customPolicy("recheckData", recheckData);
        }
        if(map.containsKey("tabooData") && map.get("tabooData") != null){
            LoopRowTableRenderPolicy tabooData = new LoopRowTableRenderPolicy();
            config.customPolicy("tabooData", tabooData);
        }
        if(map.containsKey("diseaseData") && map.get("diseaseData") != null){
            LoopRowTableRenderPolicy diseaseData = new LoopRowTableRenderPolicy();
            config.customPolicy("diseaseData", diseaseData);
        }
        if(map.containsKey("otherAbnormalData") && map.get("otherAbnormalData") != null){
            LoopRowTableRenderPolicy otherAbnormalData = new LoopRowTableRenderPolicy();
            config.customPolicy("otherAbnormalData", otherAbnormalData);
        }
        if(map.containsKey("groupDatas") && map.get("groupDatas") != null){
            LoopRowTableRenderPolicy groupDatas = new LoopRowTableRenderPolicy();
            config.customPolicy("groupDatas", groupDatas);
        }
        if(map.containsKey("careerHis") && map.get("careerHis") != null){
            LoopRowTableRenderPolicy careerHis = new LoopRowTableRenderPolicy();
            config.customPolicy("careerHis", careerHis);
        }
        if(map.containsKey("pastMedical") && map.get("pastMedical") != null){
            LoopRowTableRenderPolicy pastMedical = new LoopRowTableRenderPolicy();
            config.customPolicy("pastMedical", pastMedical);
        }

        //解析word模板（新的地址）
        XWPFTemplate template = XWPFTemplate.compile(inPathNew, config).render(map);
        OutputStream outputStreamTemplate = new FileOutputStream(fileOutputTemplate);
        template.write(outputStreamTemplate);
        template.close();
        outputStreamTemplate.close();
        return inPathNewTemplate;
    }

    /**
     * 解析模板map，构造图片，解析提示等
     *
     * @param map
     */
    public static void parseMapData(Map<String, Object> map) throws Exception {
        if (map != null) {
            Map<String, Object> mapCopy = new HashMap<>();
            mapCopy.putAll(map);
            for (String key : mapCopy.keySet()) {
                if (StringUtils.isNotBlank(key) && map.get(key) != null) {
                    //条形码
                    if (map.containsKey("barCodeImg") && map.get("barCodeImg") != null) {
                        String barCodeImg = map.get("barCodeImg").toString();
                        if (StringUtils.isNotBlank(barCodeImg)) {
                            //转换为
                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(barCodeImg);
                            if (bufferedImage != null) {
                                // java图片
                                map.put("barCodeImg", Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                        .size(180, 50).create());
                            }

                        }
                    }
                    //头像图片
                    if (map.containsKey("headImg") && map.get("headImg") != null) {
                        String headImg = map.get("headImg").toString();
                        if (StringUtils.isNotBlank(headImg)) {
                            //转换为
                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(headImg);
                            if (bufferedImage != null) {
                                // java图片
                                map.put("headImg", Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                        .size(120, 140).create());
                            }

                        }
                    }
                    //电测听统计图
                    //左耳
                    if ("leftImgChart".equals(key)) {
                        JSONObject leftImg = (JSONObject) map.get("leftImgChart");
                        if (leftImg != null && leftImg.containsKey("x") && leftImg.containsKey("y") && leftImg.get("x") != null && leftImg.get("y") != null) {
                            JSONArray xArray =  leftImg.getJSONArray("x");
                            List<String> xList = JSONObject.parseArray(xArray.toJSONString(), String.class);
                            JSONArray yArray =  leftImg.getJSONArray("y");
                            List<Number> yList = JSONObject.parseArray(yArray.toJSONString(), Number.class);
                            if(leftImg.containsKey("gy")){
                                JSONArray gyArray =  leftImg.getJSONArray("gy");
                                List<Number> gyList = JSONObject.parseArray(gyArray.toJSONString(), Number.class);
                                ChartMultiSeriesRenderData comb = Charts
                                        .ofComboSeries("左耳", xList.toArray(new String[0]))
                                        .addLineSeries("气体传值耳机", yList.toArray(new Number[0])).addLineSeries("骨传导耳机", gyList.toArray(new Number[0])).create();
                                map.put("leftImgChart", comb);
                            }else{
                                ChartMultiSeriesRenderData comb = Charts
                                        .ofComboSeries("左耳", xList.toArray(new String[0]))
                                        .addLineSeries("气体传值耳机", yList.toArray(new Number[0])).create();
                                map.put("leftImgChart", comb);
                            }
                        }
                    }
                    //右耳
                    if ("rightImgChart".equals(key)) {
						JSONObject rightImg = (JSONObject) map.get("rightImgChart");
                        if (rightImg != null && rightImg.containsKey("x") && rightImg.containsKey("y") && rightImg.get("x") != null && rightImg.get("y") != null) {
							JSONArray xArray =  rightImg.getJSONArray("x");
							List<String> xList = JSONObject.parseArray(xArray.toJSONString(), String.class);
							JSONArray yArray =  rightImg.getJSONArray("y");
							List<Number> yList = JSONObject.parseArray(yArray.toJSONString(), Number.class);
                            if(rightImg.containsKey("gy")){
                                JSONArray gyArray =  rightImg.getJSONArray("gy");
                                List<Number> gyList = JSONObject.parseArray(gyArray.toJSONString(), Number.class);
                                ChartMultiSeriesRenderData comb = Charts
                                        .ofComboSeries("右耳", xList.toArray(new String[0]))
                                        .addLineSeries("气体传值耳机", yList.toArray(new Number[0])).addLineSeries("骨传导耳机", gyList.toArray(new Number[0])).create();
                                map.put("rightImgChart", comb);
                            }else{
                                ChartMultiSeriesRenderData comb = Charts
                                        .ofComboSeries("右耳", xList.toArray(new String[0]))
                                        .addLineSeries("气体传值耳机", yList.toArray(new Number[0])).create();
                                map.put("rightImgChart", comb);
                            }
                        }
                    }
                    //幽门螺旋杆菌
                    if ("histogramImage".equals(key)) {
						JSONObject histogramImage = (JSONObject) map.get("histogramImage");
                        JSONArray xArray =  histogramImage.getJSONArray("x");
                        JSONArray yArray =  histogramImage.getJSONArray("y");
                        ChartMultiSeriesRenderData chart = Charts
                                .ofMultiSeries("ChartTitle", new String[] { xArray.getString(0), xArray.getString(1) })
                                .addSeries("countries", new Double[] { yArray.getDouble(0),yArray.getDouble(1) })
                                .create();
                        map.put("histogramImage", chart);
                    }
                    //总检医生签名图片
                    if ("autograph".equals(key)) {
                        String autograph = map.get("autograph").toString();
                        if (StringUtils.isNotBlank(autograph)) {
                            //转换为
                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(autograph);
                            if (bufferedImage != null) {
                                // java图片
                                map.put("autograph", Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                        .size(70, 30).create());
                            }

                        }
                    }
                    //心电图图片
                    if ("zoncareImg".equals(key)) {
                        String zoncareImg = map.get("zoncareImg").toString();
                        if (StringUtils.isNotBlank(zoncareImg)) {
                            String classPath = getClassPath();
                            String imgPath = classPath.split(":")[0] + ":" + UploadFileUtils.deletePath + zoncareImg;
                            /*//修改图片大小
                            //得到文件
                            java.io.File file = new java.io.File(imgPath);
                            BufferedImage bi = null;
                            //读取图片
                            int[] a = new int[2];
                            bi = javax.imageio.ImageIO.read(file);
                            a[0] = bi.getWidth(); //获得 宽度
                            a[1] = bi.getHeight(); //获得 高度
                            int targetWidth = 297 * 4 - 10;
                            int targetHeight = 160 * 4;
                            //int targetWidth = 3508;
                            //int targetHeight = 2479;


                            BufferedImage dimg = new BufferedImage(targetWidth, targetHeight, bi.getType());

                            Graphics2D g = dimg.createGraphics();

                            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,

                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                            g.drawImage(bi, 0, 0, targetWidth, targetHeight, a[0], a[1], 0, 0, null);

                            g.dispose();*/

                        /*    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imgPath));

                            ImageIO.write(outputImage, "png",out);


                            out.close();*/

                            // 图片流
                            map.put("zoncareImg", Pictures.ofStream(new FileInputStream(imgPath), PictureType.JPEG)
                                    .size(297 * 4 - 10, 160 * 4).create());
                            /*map.put("zoncareImg", Pictures.ofStream(new FileInputStream(imgPath), PictureType.PNG)
                                    .size(targetWidth, targetHeight).create());*/
                            /*map.put("zoncareImg", Pictures.ofBufferedImage(dimg, PictureType.PNG)
                                    .size(297 * 4 - 10, 160 * 4).create());*/

                        }
                    }
                    //肝脾B超報告图片
                    if ("gpImg".equals(key)) {
                        String gpImg = map.get("gpImg").toString();
                        if (StringUtils.isNotBlank(gpImg)) {
                            String classPath = getClassPath();
                            String imgPath = classPath.split(":")[0] + ":" + UploadFileUtils.deletePath + gpImg;
                            // 图片流
                            map.put("gpImg", Pictures.ofStream(new FileInputStream(imgPath), PictureType.JPEG)
                                    .size(210 * 4, 297 * 4).create());

                        }
                    }
                    if (key.indexOf("tableMonitoring") > -1) {
                        ArrayList table = (ArrayList) map.get(key);
                        if (table != null && table.size() > 0) {
                            for (int i = 0; i < table.size(); i++) {
                                Map<String, Object> o = (Map<String, Object>) table.get(i);
                                //检测项的提示
                                if (o.containsKey("resultTips") && o.get("resultTips") != null) {
                                    String resultTips = o.get("resultTips").toString();
                                    if (StringUtils.isNotBlank(resultTips)) {
                                        if (resultTips.indexOf("高于")>-1) {
                                            o.put("resultTips", new TextRenderData("FF0000", "↑"));
                                        } else if (resultTips.indexOf("低于")>-1) {
                                            o.put("resultTips", new TextRenderData("e9d22d", "↓"));
                                        } else {
                                            o.put("resultTips", new TextRenderData("000000", ""));
                                        }
                                    }
                                }
                                for (String oKey : o.keySet()) {
                                    if(oKey.indexOf("autograph") > -1){
                                        String autograph = o.get(oKey).toString();
                                        if (StringUtils.isNotBlank(autograph)) {
                                            //转换为
                                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(autograph);
                                            if (bufferedImage != null) {
                                                // java图片
                                                o.put(oKey, Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                                        .size(70, 30).create());
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    //分项汇总
                    if (key.indexOf("DatasTZ") > -1) {
                        ArrayList table = (ArrayList) map.get(key);
                        if (table != null && table.size() > 0) {
                            for (int i = 0; i < table.size(); i++) {
                                Map<String, Object> o = (Map<String, Object>) table.get(i);
                                for (String oKey : o.keySet()) {
                                    if(oKey.indexOf("Autograph") > -1){
                                        String autograph = o.get(oKey).toString();
                                        if (StringUtils.isNotBlank(autograph)) {
                                            //转换为
                                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(autograph);
                                            if (bufferedImage != null) {
                                                // java图片
                                                o.put(oKey, Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                                        .size(70, 30).create());
                                            }

                                        }
                                    }
                                    if(oKey.indexOf("Left") > -1){
                                        ArrayList leftTable = (ArrayList) o.get(oKey);
                                        if(leftTable != null && leftTable.size() > 0){
                                            for (int j = 0; j < leftTable.size(); j++) {
                                                Map<String, Object> oLeft = (Map<String, Object>) leftTable.get(j);
                                            }
                                        }
                                    }
                                    if(oKey.indexOf("Right") > -1){
                                        ArrayList rightTable = (ArrayList) o.get(oKey);
                                        if(rightTable != null && rightTable.size() > 0){
                                            for (int j = 0; j < rightTable.size(); j++) {
                                                Map<String, Object> oRight = (Map<String, Object>) rightTable.get(j);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //图片列表
                    if (key.indexOf("imgs") > -1) {
                        ArrayList table = (ArrayList) map.get(key);
                        if (table != null && table.size() > 0) {
                            for (int i = 0; i < table.size(); i++) {
                                Map<String, Object> o = (Map<String, Object>) table.get(i);
                                if (o != null && o.containsKey("img") && o.get("img") != null) {
                                    String barCodeImg = (String) o.get("img");
                                    //图片
                                    if (StringUtils.isNotBlank(barCodeImg)) {
                                        //转换为img对象
                                        BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(barCodeImg);
                                        if (bufferedImage != null) {
                                            // java图片
                                            o.put("img", Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                                    .size(200, 20).create());
                                        }
                                    }
                                }

                            }
                        }
                    }
                    //图片列表
                    if (key.indexOf("imgList") > -1) {
                        ArrayList table = (ArrayList) map.get(key);
                        if (table != null && table.size() > 0) {
                            for (int i = 0; i < table.size(); i++) {
                                Map<String, Object> o = (Map<String, Object>) table.get(i);
                                if (o != null && o.containsKey("img") && o.get("img") != null) {
                                    String barCodeImg = (String) o.get("img");
                                    //图片
                                    if (StringUtils.isNotBlank(barCodeImg)) {
                                        //转换为img对象
                                        BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(barCodeImg);
                                        if (bufferedImage != null) {
                                            // java图片
                                            o.put("img", Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                                    .size(140, 120).create());
                                        }
                                    }
                                }

                            }
                        }
                    }
                    if (key.indexOf("Autograph") > -1) {
                        String autograph = map.get(key).toString();
                        if (StringUtils.isNotBlank(autograph)) {
                            //转换为
                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(autograph);
                            if (bufferedImage != null) {
                                // java图片
                                map.put(key, Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                        .size(70, 30).create());
                            }

                        }
                    }
                    if (key.indexOf("jthctImage") > -1) {
                        String autograph = map.get(key).toString();
                        if (StringUtils.isNotBlank(autograph)) {
                            //转换为
                            BufferedImage bufferedImage = BASE64DecodedMultipartFile.base64ToBufferedImage(autograph);
                            if (bufferedImage != null) {
                                // java图片
                                map.put(key, Pictures.ofBufferedImage(bufferedImage, PictureType.PNG)
                                        .size(210, 100).create());
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * 合并Word 并转为Pdf
     * @param inPaths 需要合并的word 文件路径集合
     * @param outPath 输出的pdf 路径
     */
    public static void mergeDoc(List<String> inPaths,String outPath) throws Exception {

        if(inPaths==null ||inPaths.size()==0|| StringUtils.isBlank(outPath)){
            return;
        }
        //先创建输出文件路径
        File dest = (new File(outPath)).getCanonicalFile();
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        //再创建输出文件
        File fileOutput = new File(outPath);
        if (!fileOutput.exists()) {
            fileOutput.createNewFile();
        }
        String outPathWord = outPath.replaceAll(".Pdf",".docx");
        //再创建输出文件
        File fileOutputWord = new File(outPathWord);
        if (!fileOutputWord.exists()) {
            fileOutputWord.createNewFile();
        }
        Document doc3 = new Document();
        OutputStream outputStreamWord = new FileOutputStream(fileOutputWord);
        OutputStream outputStream = new FileOutputStream(fileOutput);
        doc3.removeAllChildren();
        Boolean flag = false;
        for (int i = 0; i < inPaths.size(); i++) {
            String path = inPaths.get(i);
            if(path.indexOf("封面模板") > -1){
                flag = true;
            }
            Document doc = new Document(path);
            doc3.appendDocument(doc, ImportFormatMode.USE_DESTINATION_STYLES);
        }
        //统计页数
        int pages = doc3.getPageCount();
        int number = pages%2;
        if(number != 0 && !flag){//奇数页
            String newPathHead = inPaths.get(0).split("pdf/word/")[0];//头部路径
            newPathHead += "2022-01-21/空白页.docx";
            Document doc0 = new Document(newPathHead);
            doc3.appendDocument(doc0, ImportFormatMode.KEEP_SOURCE_FORMATTING);
        }
        doc3.save(outputStreamWord,SaveFormat.DOCX);
        outputStreamWord.close();
        Document docPdf = new Document(outPathWord);
        docPdf.save(outputStream,SaveFormat.PDF);
        outputStream.close();
    }
}

