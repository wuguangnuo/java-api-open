package com.xyj.api.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Thumbnails 图片处理
 *
 * @author WuGuangNuo
 */
@Component
public class ThumbnailsUtil {

    /**
     * 图片压缩(自动转jpg)
     *
     * @param imgUrl 图片URL
     * @param scale  压缩率
     * @return 图片base64(无URI)
     */
    public String imageCompress(String imgUrl, double scale) {
        URL url;
        try {
            url = new URL(imgUrl);
        } catch (MalformedURLException e) {
            return "输入有误: " + e.toString();
        }
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(url.openStream());
        } catch (IOException e) {
            return e.toString();
        }
        BufferedImage thumbnailBI = null;
        try {
            thumbnailBI = Thumbnails.of(inputStream).scale(scale).outputFormat("jpg").asBufferedImage(); // .size()
        } catch (IOException e) {
            return e.toString();
        }
        return imageThumbnailsToBase64(thumbnailBI);
    }

    /**
     * 压缩图片转base64
     *
     * @param bI BufferedImage(jpg)
     * @return 图片base64(无URI)
     */
    private static String imageThumbnailsToBase64(BufferedImage bI) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageOutputStream imageOut;

            imageOut = ImageIO.createImageOutputStream(outputStream);
            ImageIO.write(bI, "jpg", imageOut);

            // 图片转换为base64并返回
            BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            byte[] bytes = outputStream.toByteArray();
            return encoder.encodeBuffer(bytes).trim().replaceAll("\r\n", "");
        } catch (Exception e) {
            return e.toString();
        }
    }
}
