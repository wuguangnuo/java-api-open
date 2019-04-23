package com.xyj.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * PDF 工具
 *
 * @author WuGuangNuo
 */
@Component
@Slf4j
public class PDFUtil {
    /**
     * PDF文件转PNG图片，第N页
     *
     * @param file      pdf文件 MultipartFile
     * @param pageIndex 第几页
     * @param dpi       dpi越大转换后越清晰，相对转换速度越慢 默认300
     * @return
     */
    public String pdf2Image(MultipartFile file, int pageIndex, int dpi) {
        if (dpi <= 0 || dpi > 300) dpi = 300;
        PDDocument pdDocument;
        String base64Str = null;
        try {
            pdDocument = PDDocument.load(file.getInputStream());
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi);
            ImageIO.write(image, "png", baos);
            base64Str = Base64Util.encode(baos.toByteArray());

            log.info("PDF文档转PNG图片成功！大小: " + base64Str.length() / 1024 + "KB");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Str;
    }

    /**
     * @param file pdf文件 MultipartFile
     *             默认第一页; dpi: 300
     * @return base64 String
     */
    public String pdf2Image(MultipartFile file) {
        return this.pdf2Image(file, 0, 300);
    }
}
