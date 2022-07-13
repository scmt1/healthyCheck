package com.scmt.healthy.utils;

import com.scmt.core.utis.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yzy
 */
public class UploadFileUtils {
    public static String basePath = "/usr/local/zsyz/uploadfile/tempfile/";

    public static String deletePath = "/usr/local/zsyz/uploadfile";

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
        return "/tempfile/" + DataStr + "/" + name;
    }

    /**
     * 将文件存储在
     *
     * @param multipartFile
     * @return
     */
    public static String batchUpload(MultipartFile[] multipartFile) {

        String res = "";
        for (MultipartFile file : multipartFile) {
            String path = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String DataStr = format.format(new Date());
            path = basePath + "" + DataStr + "/";
            File upload = FileUtil.upload(file, path);
            String name = upload.getName();
            res += "/tempfile/" + DataStr + "/" + name + ",";
        }

        return res;

    }

    /**
     * 删除nginx代理路劲下的文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        String resPath = deletePath + path;
        File delete = new File(resPath);
        if (delete.exists()) {
            delete.delete();
        }
    }


    /**
     * 存文件到本地
     *
     * @param url
     * @param method
     * @return
     */
    public static String saveUrlAs(String url, String method, String fileType) throws IOException {
        String fileName = url.substring(url.lastIndexOf("/"));
        fileName = fileName.split("/")[1];
        String filePath = "/usr/local/bdc_zzk/uploadfile/local";
        //创建不同的文件夹目录
        File file = new File(filePath);
        //判断文件夹是否存在
        if (!file.exists()) {
            //如果文件夹不存在，则创建新的的文件夹
            file.mkdirs();
        }
        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        // 建立链接
        URL httpUrl = new URL(url);
        conn = (HttpURLConnection) httpUrl.openConnection();
        //以Post方式提交表单，默认get方式
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // post方式不能使用缓存
        conn.setUseCaches(false);
        //连接指定的资源
        conn.connect();
        //获取网络输入流
        inputStream = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        //判断文件的保存路径后面是否以/结尾
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        //写入到文件（注意文件保存路径的后面一定要加上文件的名称）
        fileOut = new FileOutputStream(filePath + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fileOut);
        byte[] buf = new byte[4096];
        int length = bis.read(buf);
        //保存文件
        while (length != -1) {
            bos.write(buf, 0, length);
            length = bis.read(buf);
        }
        bos.close();
        bis.close();
        conn.disconnect();
        if ("png".equals(fileType)) {
            File file1 = new File(filePath + fileName);
            // 读取图片
            BufferedImage bufImage = ImageIO.read(file1);
            // 获取图片的宽高
            int width = bufImage.getWidth();
            int height = bufImage.getHeight();
            int v = (int) (height / 1.75);
            BufferedImage subimage = bufImage.getSubimage(0, 0, width, v);
            width = (int) (subimage.getWidth() * 0.4f);
            height = (int) (subimage.getHeight() * 0.4f);

            BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            finalImage.getGraphics().drawImage(subimage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            try {
                file1.delete();
            } catch (Exception e) {
            }
            // 把修改过的 bufImage 保存到本地
            ImageIO.write(finalImage, "png", new File(filePath + fileName));
        }
        return "/local/" + fileName;
    }

//    /**
//     *
//     *
//     * @param src  源文件
//     * @param dest 目标文件
//     * @throws IOException
//     * @throws DocumentException
//     */
//    public static void manipulatePdf(String src, String dest) throws IOException, DocumentException {
//        float FACTOR = 0.1f;
//        PdfName key = new PdfName("ITXT_SpecialId");
//        PdfName value = new PdfName("123456789");
//        // 读取pdf文件
//        PdfReader reader = new PdfReader(src);
//        int n = reader.getXrefSize();
//        PdfObject object;
//        PRStream stream;
//        for (int i = 0; i < n; i++) {
//            object = reader.getPdfObject(i);
//            if (object == null || !object.isStream())
//                continue;
//            stream = (PRStream) object;
//            PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
//            if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
//                PdfImageObject image = new PdfImageObject(stream);
//                BufferedImage bi = image.getBufferedImage();
//                if (bi == null) continue;
//                int width = (int) (bi.getWidth() * FACTOR);
//                int height = (int) (bi.getHeight() * FACTOR);
//                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//                AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
//                Graphics2D g = img.createGraphics();
//                g.drawRenderedImage(bi, at);
//                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
//                //标记此处，后面会修改
//                //判断文件流的大小，超过500k的才进行压缩，否则不进行压缩
//                if(img.getData().getDataBuffer().getSize()>512000){
//                    ImageIO.write(img, "JPG", imgBytes);
//                    stream.clear();
//                    stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
//                    stream.put(PdfName.TYPE, PdfName.XOBJECT);
//                    stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
//                    stream.put(key, value);
//                    stream.put(PdfName.FILTER, PdfName.DCTDECODE);
//                    stream.put(PdfName.WIDTH, new PdfNumber(width));
//                    stream.put(PdfName.HEIGHT, new PdfNumber(height));
//                    stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
//                    stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
//                }else {
//                    ImageIO.write(img, "JPG", imgBytes);
//                }
//            }
//        }
//        // Save altered PDF
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//        stamper.close();
//        reader.close();
//    }
}
