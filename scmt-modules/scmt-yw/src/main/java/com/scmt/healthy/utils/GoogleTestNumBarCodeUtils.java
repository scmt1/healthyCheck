package com.scmt.healthy.utils;

import cn.hutool.core.codec.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GoogleTestNumBarCodeUtils {
    /**
     * 条形码宽度
     */
    private static final int WIDTH = 180;

    /**
     * 条形码高度
     */
    private static final int HEIGHT = 50;

    /**
     * 加文字 条形码
     */
    private static final int WORDHEIGHT = 90;
    /**
     * 设置 条形码参数
     */
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;

        {
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, "utf-8");
        }
    };

    /**
     * 生成 图片缓冲
     *
     * @param vaNumber VA 码
     * @return 返回BufferedImage
     * @author fxbin
     */
    public static BufferedImage getBarCode(String vaNumber) {
        try {
            Code128Writer writer = new Code128Writer();
            // 编码内容, 编码类型, 宽度, 高度, 设置参数
            BitMatrix bitMatrix = writer.encode(vaNumber, BarcodeFormat.CODE_128, 180, 50, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把带logo的二维码下面加上文字
     *
     * @param image 条形码图片
     * @param words 文字
     * @return 返回BufferedImage
     * @author fxbin
     */
    public static BufferedImage insertWords(BufferedImage image, String words) {
        // 新的图片，把带logo的二维码下面加上文字
        if (StringUtils.isNotEmpty(words)) {
            BufferedImage outImage = new BufferedImage(WIDTH, WORDHEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outImage.createGraphics();
            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d);
            // 画条形码到新的面板
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            // 画文字到新的面板
            Color color = new Color(0, 0, 0);
            g2d.setColor(color);
            // 字体、字型、字号
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            //文字长度
            int strWidth = g2d.getFontMetrics().stringWidth(words);
            //总长度减去文字长度的一半  （居中显示）
            int wordStartX = (WIDTH - strWidth) / 2;
            //height + (outImage.getHeight() - height) / 2 + 12
            int wordStartY = HEIGHT + 20;
            // 画文字
            g2d.drawString(words, wordStartX, wordStartY);

            //drawStringWithFontStyleLineFeed(g2d, words, WIDTH - 60, wordStartX, wordStartY);
            g2d.dispose();
            outImage.flush();
            return outImage;
        }else{
            return image;
        }
    }


    /**
     * 根据指定宽度自动换行
     *
     * @param g
     * @param maxWidth
     * @param str
     * @param loc_X
     * @param loc_Y
     */
    private static void drawStringWithFontStyleLineFeed(Graphics g, String str, int maxWidth, int loc_X, int loc_Y) {
        // 得到当前的font metrics
        FontMetrics metrics = g.getFontMetrics();
        int StrPixelWidth = metrics.stringWidth(str); // 字符串长度（像素） str要打印的字符串
        int lineSize = (int) Math.ceil(StrPixelWidth * 1.0 / maxWidth);// 算出行数
        System.out.println(StrPixelWidth + "---:");
        int height = getStringHeight(g);
        if (maxWidth < StrPixelWidth) {// 页面宽度（width）小于 字符串长度
            StringBuilder sb = new StringBuilder();// 存储每一行的字符串
            int j = 0;
            int tempStart = 0;
            String tempStrs[] = new String[lineSize];// 存储换行之后每一行的字符串
            for (int i1 = 0; i1 < str.length(); i1++) {
                char ch = str.charAt(i1);
                sb.append(ch);
                Rectangle2D bounds2 = metrics.getStringBounds(sb.toString(), null);
                int tempStrPi1exlWi1dth = (int) bounds2.getWidth();
                if (tempStrPi1exlWi1dth > maxWidth) {
                    tempStrs[j++] = str.substring(tempStart, i1);
                    tempStart = i1;
                    sb.delete(0, sb.length());
                    sb.append(ch);
                }
                if (i1 == str.length() - 1) {// 最后一行
                    tempStrs[j] = str.substring(tempStart);
                }
            }
            loc_X = 35;
            for (int i = 0; i < tempStrs.length; i++) {
                if(i == 0){
                    g.drawString(tempStrs[i], loc_X, loc_Y);
                }else {
                    g.drawString(tempStrs[i], loc_X, loc_Y + height);
                }
            }
        } else {
            g.drawString(str, loc_X, loc_Y);
        }
    }

    private static int getStringHeight(Graphics g) {
        int height = g.getFontMetrics().getHeight();
        return height;
    }

    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    /**
     * 设置背景为白色
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setColorWhite(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, 600, 600);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }


    /**
     * @param data 生成条形码的数据
     * @param info 条形码下方的描述
     * @return
     */
    public static String generatorBase64Barcode(String data, String info) throws IOException {
        BufferedImage image = insertWords(getBarCode(data), info);
        //输出流
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        String encode = Base64.encode(stream.toByteArray());
        return "data:image/png;base64," + encode;
    }


    public static void main(String[] args) throws IOException, PrintException {
        //BufferedImage image = insertWords(getBarCode("A80/90R8A8A"), "张三-血常规-2021-10-10");
        String aa = generatorBase64Barcode("123456789", "张三-血常规-20211010");
        //构建打印请求属性集
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        //设置打印格式，因为未确定文件类型，这里选择AUTOSENSE
        DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
        //查找所有的可用打印服务
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        //定位默认的打印服务
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        //显示打印对话框
        MultipartFile imgFile = BASE64DecodedMultipartFile.base64ToMultipart(aa);
        DocPrintJob job = defaultService.createPrintJob(); //创建打印作业
//        FileInputStream fis = new FileInputStream(imgFile); //构造待打印的文件流
        InputStream fis = imgFile.getInputStream();
        DocAttributeSet das = new HashDocAttributeSet();
        Doc doc = new SimpleDoc(fis, flavor, das); //建立打印文件格式
        job.print(doc, pras); //进行文件的打印
//        ImageIO.write(image, "jpg", new File("G:/Images/barcode.png"));
//        String aa = generatorBase64Barcode("123456789", "张三李四AAAAA");
//        System.out.println(aa);
    }
}
