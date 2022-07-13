package com.scmt.core.utis;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;

import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scmt.core.common.exception.ScmtException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil extends cn.hutool.core.io.FileUtil {
    private static final int GB = 1073741824;
    private static final int MB = 1048576;
    private static final int KB = 1024;
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public FileUtil() {
    }

    public static File toFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String prefix = "." + getExtensionName(fileName);
        File file = null;

        try {
            file = File.createTempFile(IdUtil.simpleUUID(), prefix);
            multipartFile.transferTo(file);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return file;
    }

    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }

        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length()) {
                return filename.substring(0, dot);
            }
        }

        return filename;
    }

    public static String getSize(long size) {
        String resultSize;
        if (size / 1073741824L >= 1L) {
            resultSize = DF.format((double)((float)size / 1.07374182E9F)) + "GB   ";
        } else if (size / 1048576L >= 1L) {
            resultSize = DF.format((double)((float)size / 1048576.0F)) + "MB   ";
        } else if (size / 1024L >= 1L) {
            resultSize = DF.format((double)((float)size / 1024.0F)) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }

        return resultSize;
    }

    static File inputStreamToFile(InputStream ins, String name) throws Exception {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        } else {
            OutputStream os = new FileOutputStream(file);
            int len = 8192;
            byte[] buffer = new byte[len];

            int bytesRead;
            while((bytesRead = ins.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.close();
            ins.close();
            return file;
        }
    }

    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        String name = getFileNameNoEx(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);

        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            File dest = (new File(path)).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            file.transferTo(dest);
            return dest;
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    public static String fileToBase64(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        String base64 = Base64.encode(buffer);
        return base64.replaceAll("[\\s*\t\n\r]", "");
    }

    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = System.getProperty("java.io.tmpdir") + IdUtil.fastSimpleUUID() + ".xlsx";
        createExcel(list, tempPath, response);
    }

    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return "图片";
        } else if (documents.contains(type)) {
            return "文档";
        } else if (music.contains(type)) {
            return "音乐";
        } else {
            return video.contains(type) ? "视频" : "其他";
        }
    }

    public static String getFileTypeByMimeType(String type) {
        String mimeType = (new MimetypesFileTypeMap()).getContentType("." + type);
        return mimeType.split("/")[0];
    }

    public static void checkSize(long maxSize, long size) {
        int len = 1048576;
        if (size > maxSize * (long)len) {
            throw new ScmtException("文件超出规定大小");
        }
    }

    public static boolean check(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        return img1Md5.equals(img2Md5);
    }

    public static boolean check(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    private static byte[] getByte(File file) {
        byte[] b = new byte[(int)file.length()];

        try {
            FileInputStream in = new FileInputStream(file);

            try {
                in.read(b);
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return b;
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    private static String getMd5(byte[] bytes) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            byte[] var7 = md;
            int var8 = md.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                byte byte0 = var7[var9];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var11) {
            var11.printStackTrace();
            return null;
        }
    }

    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }

    public static void createExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        sheet.setDefaultColumnWidth(20);
        XSSFRow row = sheet.createRow(0);
        if (list.size() > 0) {
            Map<String, Object> strArray = (Map)list.get(0);
            int h = 0;
            XSSFCell cell = null;

            for(Iterator var9 = strArray.keySet().iterator(); var9.hasNext(); ++h) {
                String str = (String)var9.next();
                cell = row.createCell(h);
                cell.setCellValue(str);
                cell.setCellStyle(setHeadCellStyle(wb));
            }

            for(int i = 0; i < list.size(); ++i) {
                row = sheet.createRow(i + 1);
                Map<String, Object> map = (Map)list.get(i);
                int j = 0;

                for(Iterator var12 = strArray.keySet().iterator(); var12.hasNext(); ++j) {
                    String str = (String)var12.next();
                    String string = "";
                    if (map.get(str) != null) {
                        string = map.get(str).toString();
                    }

                    cell = row.createCell(j);
                    cell.setCellValue(string);
                }
            }
        }

        try {
            response.setHeader("content-Type", "application/ms-excel");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=utf-8''" + fileName);
            OutputStream out = response.getOutputStream();
            wb.write(out);
            IoUtil.close(out);
        } catch (Exception var15) {
            var15.printStackTrace();
        }

    }

    public static XSSFCellStyle setCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle columnTopStyle = getStyle(workbook);
        return columnTopStyle;
    }

    public static XSSFCellStyle setHeadCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);
        columnTopStyle.setBorderBottom(BorderStyle.THIN);
        columnTopStyle.setBorderLeft(BorderStyle.THIN);
        columnTopStyle.setBorderTop(BorderStyle.THIN);
        columnTopStyle.setBorderRight(BorderStyle.THIN);
        columnTopStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        columnTopStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeight(700);
        font.setFontHeightInPoints((short)12);
        columnTopStyle.setFont(font);
        return columnTopStyle;
    }

    public static XSSFCellStyle getColumnTopStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)11);
        font.setFontHeight((short)700);
        font.setFontName("Courier New");
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor((short)8);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor((short)8);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor((short)8);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor((short)8);
        style.setFont(font);
        style.setWrapText(false);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    public static XSSFCellStyle getStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor((short)8);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor((short)8);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor((short)8);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor((short)8);
        style.setFont(font);
        style.setWrapText(false);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
