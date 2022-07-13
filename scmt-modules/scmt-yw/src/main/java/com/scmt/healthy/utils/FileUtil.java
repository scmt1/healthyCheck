package com.scmt.healthy.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 *
 * @author dengjie
 */
public class FileUtil {
    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     *
     * @param filePath
     */
    public static List<String> readTxtFile(String filePath) {
        List<String> res = new ArrayList<>();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                //考虑到编码格式
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    res.add(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
            return res;
        }
        return res;
    }

    /**
     * 将list 写入文件
     * @param list
     * @param fileName
     */
    public static void writeListToFile(List<String> list, String fileName) {
        if (list == null || list.size() == 0 || StringUtils.isBlank(fileName)) {
            return;
        }
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }


            BufferedWriter bw = null;

            bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < list.size(); i++) {
                bw.write(list.get(i));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
