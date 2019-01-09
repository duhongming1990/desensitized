package com.danny.log.waterprint;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawTranslucentPngUtilTest {
    @Test
    public void test(){
        BufferedImage bufferedImageTemp = DrawTranslucentPngUtil.drawTranslucentStringPic(
                255, 217,
                30,
                "00203219\n0601\n158143024\n20180917102134");
        BufferedImage bufferedImage = DrawTranslucentPngUtil.rotateImage(bufferedImageTemp,315);
        File imgFile = new File("D://tuomin.png");
        try {
            ImageIO.write(bufferedImage, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}