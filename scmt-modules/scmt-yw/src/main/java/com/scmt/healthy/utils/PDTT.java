package com.scmt.healthy.utils;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.artofsolving.jodconverter.DocumentConverter;
//import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
//import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
//import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
//import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

public class PDTT {

    // 将word格式的文件转换为pdf格式
    public static void WordToPDF(String startFile, String overFile) throws IOException {
        // 源文件目录
        File inputFile = new File(startFile);
        if (!inputFile.exists()) {
            System.out.println("源文件不存在！");
            return;
        }

        // 输出文件目录
        File outputFile = new File(overFile);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().exists();
        }

        // 连接openoffice服务,xx.xx.xxx.xxx为启动的openOffice在linux服务器的ip
        /*OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
        connection.connect();*/

        // 转换
        /*DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);*/


        /*converter.convert(inputFile, outputFile);*/

        // 关闭连接
        /*connection.disconnect();*/

        // 关闭进程
//        p.destroy();
    }
    public static void main(String[] args) {
        String start = "D:\\demo.docx";
        String over = "D:\\成了.pdf";
        try {
            WordToPDF(start, over);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}