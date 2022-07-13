package com.scmt.healthy.utils;

/**
 * @author dengjie
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class HtmlToWord {

	/**
	 * 外部接口
	 *
	 * @param htmlPath html文件的路径
	 * @param cssPath  css文件的路径
	 * @param wordPath word文件的路径(保存本地的路径)
	 * @param code     编码方式(一般都为utf-8)
	 * @throws Exception
	 */
	public void htmlToWord2(String htmlPath, String cssPath, String wordPath, String code) throws Exception {
		/*InputStream bodyIs = new FileInputStream("H:\\MyTest\\Java\\test_show\\test.html");
		InputStream cssIs = new FileInputStream("H:\\MyTest\\Java\\test_show\\test.css");*/
		InputStream bodyIs = new FileInputStream(htmlPath);
		InputStream cssIs = new FileInputStream(cssPath);
		String body = this.getContent(bodyIs);
		String css = this.getContent(cssIs);
// 拼一个标准的HTML格式文档
		String content = "<html><head><style>" + css + "</style></head><body>" + body + "</body></html>";
		InputStream is = new ByteArrayInputStream(content.getBytes(code));
		OutputStream os = new FileOutputStream(wordPath);
		this.inputStreamToWord(is, os);
	}

	/**
	 * 把is写入到对应的word输出流os中 不考虑异常的捕获，直接抛出
	 *
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	private void inputStreamToWord(InputStream is, OutputStream os) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem();
		// 对应于org.apache.poi.hdf.extractor.WordDocument
		fs.createDocument(is, "WordDocument");
		fs.writeFilesystem(os);
		os.close();
		is.close();

	}

	/**
	 * 把输入流里面的内容以UTF-8编码当文本取出。 不考虑异常，直接抛出
	 *
	 * @param ises
	 * @return
	 * @throws IOException
	 */
	private String getContent(InputStream... ises) throws IOException {
		if (ises != null) {
			StringBuilder result = new StringBuilder();
			BufferedReader br;
			String line;
			for (InputStream is : ises) {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = br.readLine()) != null) {
					result.append(line);
				}
			}
			return result.toString();
		}
		return null;
	}
}