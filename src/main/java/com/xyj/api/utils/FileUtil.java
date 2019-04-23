package com.xyj.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;

@Slf4j
@Component
public class FileUtil {

    /**
     * base64 转 MultipartFile
     *
     * @param base64
     * @return
     */
    public MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStr = base64.split(",");

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            b = decoder.decodeBuffer(baseStr[1]);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new Base64DecodeMultipartFile(b, baseStr[0]);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}