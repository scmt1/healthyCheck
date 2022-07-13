package com.scmt.healthy.utils;

import java.awt.geom.Rectangle2D;
import java.io.*;

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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoogleBarCodeUtils {
    /**
     * 条形码宽度
     */
    private static int WIDTH = 180;

    /**
     * 条形码高度
     */
    private static final int HEIGHT = 40;

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
            if (vaNumber.length() == 6) {
                WIDTH = 155;
            } else {
                WIDTH = 180;
            }
            BitMatrix bitMatrix = writer.encode(vaNumber, BarcodeFormat.CODE_128, WIDTH, HEIGHT, hints);
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
    public static BufferedImage insertWords(BufferedImage image, String data, String words) {
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
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            //文字长度
            int strWidth = g2d.getFontMetrics().stringWidth(words);
            //总长度减去文字长度的一半  （居中显示）
            int wordStartX = (WIDTH - strWidth) / 2;
            //height + (outImage.getHeight() - height) / 2 + 12
            int wordStartY = HEIGHT + 20;
            // 画文字
            //g2d.drawString(words, wordStartX, wordStartY);

            drawStringWithFontStyleLineFeed(g2d, data, words, WIDTH - 70, wordStartX, wordStartY);
            g2d.dispose();
            outImage.flush();
            return outImage;
        }
        return null;
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
    private static void drawStringWithFontStyleLineFeed(Graphics g, String data, String str, int maxWidth, int loc_X, int loc_Y) {
        // 得到当前的font metrics
        FontMetrics metrics = g.getFontMetrics();
        int StrPixelWidth = metrics.stringWidth(str); // 字符串长度（像素） str要打印的字符串
        int lineSize = (int) Math.ceil(StrPixelWidth * 1.0 / maxWidth);// 算出行数
        int height = getStringHeight(g);
        if (maxWidth < StrPixelWidth) {// 页面宽度（width）小于 字符串长度
            StringBuilder sb = new StringBuilder();// 存储每一行的字符串
            int j = 0;
            String tempStrs[] = new String[lineSize];// 存储换行之后每一行的字符串
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                sb.append(c);
                int stringWidth = metrics.stringWidth(sb.toString());
                if (stringWidth > maxWidth) {
                    tempStrs[j] = sb.toString();
                    sb.delete(0, sb.length());
                    j++;
                }
                // 最后一行
                if (i == str.length() - 1) {
                    tempStrs[j] = sb.toString();
                }
            }
            loc_X = 35;
            int num = 1;
            for (int i = 0; i < tempStrs.length; i++) {
                if (i == 0) {
                    g.drawString(tempStrs[i], loc_X, loc_Y);
                } else {
                    if (i > 0 && tempStrs[i].length() > 8) {
                        g.drawString(tempStrs[i].substring(0, 8) + "...", loc_X, loc_Y + height);
                    } else {
                        g.drawString(tempStrs[i], loc_X, loc_Y + height);
                    }
                    if (StringUtils.isNotBlank(tempStrs[i])) {
                        num += 1;
                    }
                }
            }
            num = num > 2 ? 2 : num;
            g.drawString(data, loc_X, loc_Y + height * num);
        } else {
            g.drawString(str, loc_X, loc_Y);
            g.drawString(data, loc_X, loc_Y + height);
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
        BufferedImage image = insertWords(getBarCode(data), data, info);
        //输出流
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        String encode = Base64.encode(stream.toByteArray());
        return "data:image/png;base64," + encode;
    }
}
