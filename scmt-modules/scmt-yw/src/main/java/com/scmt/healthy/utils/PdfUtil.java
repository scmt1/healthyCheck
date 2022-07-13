package com.scmt.healthy.utils;

import com.aspose.words.SaveFormat;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.*;

/**
 * @author dengjie
 */
public class PdfUtil {
	/**
	 * 2003- 版本的word
	 */
	private static final String word2003L = ".doc";
	/**
	 * 2007+ 版本的word
	 */
	private final static String word2007U = ".docx";
	/**
	 * 文件夹路径分格符
	 */
	private static String separator = File.separator;

//	/**
//	 * word转pdf
//	 * @param inPath
//	 * @param outPath
//	 * @return
//	 */
//	public static boolean doc2pdf(String inPath, String outPath) throws Exception {
//		// 是否需清除中间转换的docx文档
//		Boolean isDelete = false;
//		String fileType = inPath.substring(inPath.lastIndexOf(".")).toLowerCase();
//		if (word2003L.equals(fileType)){
//			docToDOcx(inPath,inPath+"x");
//			inPath = inPath+"x";
//			isDelete = true;
//		}else if(word2007U.equals(fileType)){
//
//		}else {
//			return false;
//		}
//
////		WordPdfUtil.doc2Pdf(inPath,outPath);
//
//		FileInputStream fileInputStream = null;
//		FileOutputStream fileOutputStream=null;
//		try {
//			fileInputStream = new FileInputStream(inPath);
//			XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
//			PdfOptions pdfOptions = PdfOptions.create();
//			fileOutputStream = new FileOutputStream(outPath);
//			PdfConverter.getInstance().convert(xwpfDocument,fileOutputStream,pdfOptions);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally {
//
//			fileInputStream.close();
//			fileOutputStream.close();
//			if (isDelete){
//				deleteDocx(inPath);
//			}
//		}
//		if (isDelete){
//			deleteDocx(inPath);
//		}
//		return true;
//	}

	/**
	 * doc转docx
	 * @param path  需要转换文件的路径
	 * @param outPath 转换后文件的路径
	 * @return
	 */
	public static void docToDOcx(String path,String outPath) throws Exception {
//		Document doc = new Document(path);
//		doc.save(outPath);
		OutputStream outputStream = new FileOutputStream(outPath);

		com.aspose.words.Document doc = new com.aspose.words.Document(path);
		//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
		doc.save(outputStream, SaveFormat.DOCX);
		outputStream.close();
	}


	/**
	 * 清除临时转换docx
	 * @param path
	 */
	public static void  deleteDocx(String path){
		File file = new File(path);
		file.delete();
	}

	/**
	 * 合并PDF
	 * @param inFiles  需要合并的PDf文档路径集合
	 * @param outPath  合并pdf生成的文件名
	 * @throws IOException
	 */
	public static void MergePdf(String[] inFiles, String outPath) throws Exception {
		if(inFiles==null||inFiles.length==0){
			throw new RuntimeException("传入的Pdf路径有误！");
		}
		//pdf合并工具类
		PDFMergerUtility mergePdf = new PDFMergerUtility();
		//先创建输出文件路径
		File dest = (new File(outPath)).getCanonicalFile();
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		//再创建输出文件
		File file3 = new File(outPath);
		//存在则删除
		if(file3.exists()){
			file3.delete();
		}
		file3.createNewFile();
		for (int i = 0; i < inFiles.length; i++) {
			if(StringUtils.isNotBlank(inFiles[i])){
				String[] split = inFiles[i].split("/tempFileUrl");
				if(split!=null && split.length>1){
					String file = DocUtil.getClassPath().split(":")[0] + ":" + DocUtil.basePath + split[1];
					String inPathNew = file.replaceAll("\\\\", "\\\\\\\\").replace("//tempfile/wordTemplate", "");
					mergePdf.addSource(inPathNew);

				}
				else if(split!=null && split.length == 1){
					mergePdf.addSource(inFiles[i]);
				}
				else {
					throw new RuntimeException("传入的Pdf路径有误！");
				}

			}
		}
		//设置合并生成pdf文件名称
		mergePdf.setDestinationFileName(outPath);
		//合并pdf
		mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

		System.out.println("pdf文件合并成功");
	}

}
