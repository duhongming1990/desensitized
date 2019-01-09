package com.danny.log.waterprint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/17 10:12
 */
public class DrawTranslucentPngUtil {
    public static BufferedImage drawTranslucentStringPic(int width, int height, Integer fontHeight, String drawStr) {
        try {

            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gd = buffImg.createGraphics();

            //设置背景透明  start
            buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            gd = buffImg.createGraphics();
            //设置背景透明  end

            gd.setFont(new Font("宋体", Font.PLAIN, fontHeight)); //设置字体
            gd.setColor(new Color(228, 228, 228)); //设置颜色

            //设置图片字体换行、居中
            String[] splitStr = drawStr.split("\\n");
            for (int i = 0; i < splitStr.length; i++) {
                //字体居中
                FontRenderContext frc =
                        new FontRenderContext(null, true, true);
                Font font= new Font("宋体", Font.PLAIN, fontHeight);
                Rectangle2D r2D = font.getStringBounds(splitStr[i], frc);
                int rWidth = (int) Math.round(r2D.getWidth());
                int rX = (int) Math.round(r2D.getX());
                int a = (width / 2) - (rWidth / 2) - rX;
                //字体换行
                gd.drawString(splitStr[i], a, (i+1)*fontHeight);// 画出一行字符串,注意y轴坐标需要变动
            }
            return buffImg;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 旋转图片为指定角度
     *
     * @param bufferedimage
     * 目标图像
     * @param degree
     * 旋转角度
     * @return
     */
    public static BufferedImage rotateImage(final BufferedImage bufferedimage,
                                            final int degree){

        int w= bufferedimage.getWidth();// 得到图片宽度。
        int h= bufferedimage.getHeight();// 得到图片高度。

        int type= bufferedimage.getColorModel().getTransparency();// 得到图片透明度。

        BufferedImage img;// 空的图片。
        Graphics2D graphics2d;// 空的画笔。

        (graphics2d= (img= new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);// 旋转，degree是整型，度数，比如垂直90度。
        graphics2d.drawImage(bufferedimage, 0, 0, null);// 从bufferedimagecopy图片至img，0,0是img的坐标。
        graphics2d.dispose();
        return img;// 返回复制好的图片，原图片依然没有变，没有旋转，下次还可以使用。
    }
}